package org.ogf.saga.engine;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.SagaException;

/**
 * This class make the various SAGA adaptors available to SAGA. Some of this
 * code is stolen from the JavaGAT engine implementation. This class also
 * supports cloning of SAGA adaptors, which is required when a SAGA object is
 * cloned.
 * 
 * The adaptor jar files are searched in directories that are specified in the
 * system property <code>saga.adaptor.path</code>. The directories specified
 * in this list are supposed to have sub-directories with names that end with
 * "...Adaptor". Each of these subdirectories is supposed to contain that
 * adaptor jar, and all supporting jar files.
 * 
 * A separate class loader is instantiated for each adaptor, to prevent problems
 * when different adaptors need different versions of some third-party software.
 */
public class SAGAEngine {

    /** This member variable holds reference to the single SAGAEngine. */
    private static SAGAEngine sagaEngine = null;
    
    private static final ClassLoader systemLoader = ClassLoader.getSystemClassLoader();

    private boolean ended = false;

    /** Classloader to be used as parent classloader for the URL classloaders. */
    private final ClassLoader parentLoader;
    
    /** Keys are SPI names, elements are AdaptorLists. */
    private HashMap<String, AdaptorList> adaptors;

    private static final Properties properties = org.ogf.saga.bootstrap.SagaProperties
            .getDefaultProperties();

    private static Logger logger = LoggerFactory.getLogger(SAGAEngine.class);

    private static String sagaLocation = getProperty("saga.location");

    /**
     * A helper class to compare URLs, so that they can be sorted,
     * and the order becomes predictable and reproducable.
     */
    private static class URLComparator implements Comparator<URL>,
            java.io.Serializable {
        private static final long serialVersionUID = 1L;

        // Serializable, because findbugs wants that.
        public int compare(URL u1, URL u2) {
            return u1.toString().compareTo(u2.toString());
        }
    }
    
    /**
     * A helper class to get the call context. It subclasses SecurityManager
     * to make getClassContext() accessible. Don't install this as an actual
     * security manager!
     */
    private static final class CallerResolver extends SecurityManager {
        protected Class<?>[] getClassContext() {
            return super.getClassContext ();
        }
    }


    /** Constructs a default SAGAEngine instance. */
    private SAGAEngine() {

        adaptors = new HashMap<String, AdaptorList>();
        
        parentLoader = getParentClassLoader();

        readJarFiles();

        if (adaptors.size() == 0) {
            if (sagaLocation == null) {
                throw new Error("SAGA: No adaptors could be loaded. Apparently, saga.location is not set");
            }
            throw new Error("SAGA: No adaptors could be loaded");
        }

        if (logger.isInfoEnabled()) {
            StringBuffer buf = new StringBuffer();
            buf.append("------------LOADED ADAPTORS------------\n");
            for (AdaptorList l : adaptors.values()) {
                buf.append("Adaptor type: ");
                buf.append(l.getSpiName());
                buf.append(":\n");
                for (Adaptor a : l) {
                    buf.append("    ");
                    buf.append(a);
                    buf.append("\n");
                }
                buf.append("\n");
            }
            buf.append("---------------------------------------");
            logger.info("\n" + buf.toString());
        }
    }

    /**
     * Singleton method to construct a SAGAEngine.
     * 
     * @return A SAGAEngine instance
     */
    public static synchronized SAGAEngine getSAGAEngine() {
        if (sagaEngine == null) {
            sagaEngine = new SAGAEngine();
        }

        return sagaEngine;
    }
    
    /**
     * This method tries to determine a suitable classloader to be used
     * as parent classloader for the URLClassloaders of the adaptors.
     * Sometimes, the classloader that loaded the SAGAEngine class is not
     * a good candidate because this probably is just the system classloader.
     * A better candidate might be the classloader of the class that prompted
     * the loading of SAGA in the first place, or the context classloader.
     * 
     * @return the classloader to be used.
     */
    private ClassLoader getParentClassLoader() {
        // Find the Class instance of the class that prompted the loading
        // of JavaGAT.
        Class<?>[] callers = (new CallerResolver()).getClassContext();
        Class<?> callerClass = null;
        for (Class<?> c : callers) {
            String name = c.getCanonicalName();
            if (name != null && name.startsWith("org.ogf.saga")) {
                continue;
            }
            callerClass = c;
            break;
        }
        // If we cannot find it, use the SAGAEngine class instance, for lack of
        // a better choice.
        if (callerClass == null) {
            callerClass = SAGAEngine.class;
        }
        // Now, there are basically two choices: the classloader that loaded the
        // caller class, or the context classloader. If there is a parent-relation,
        // choose the child.
        ClassLoader callerLoader = callerClass.getClassLoader();
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader result;
        
        if (isChild(contextLoader, callerLoader)) {
            result = callerLoader;
        } else if (isChild(callerLoader, contextLoader)) {
            result = contextLoader;
        } else {
            // Apparently there is no relation. The following may not be right,
            // but then, there is no "right".
            result = contextLoader;
        }
        
        // If the system classloader is a child of the result found so far,
        // use the system classloader instead.
        if (isChild (result, systemLoader)) {
            result = systemLoader;
        }
        
        return result;
    }
    
    /**
     * Determines if loader l2 is a child of loader l1.
     * @param l1
     * @param l2
     * @return true if l2 is a child of l1.
     */
    private static boolean isChild(ClassLoader l1, ClassLoader l2) {
        if (l1 == null) {
            // Primordial loader is parent of all classloaders.
            return true;
        }
        while (l2 != null) {
            if (l1 == l2) {
                return true;
            }
            l2 = l2.getParent();
        }
        return false;
    }


    /**
     * Returns a list of adaptors for the specified service provider interface.
     * 
     * @param spi
     *            the spi class for which to look
     * @return the list of adaptors
     * @throws NoSuccessException
     *             when no adaptors are loaded for this SPI.
     */
    private AdaptorList getAdaptorList(Class<?> spi) throws NoSuccessException {
        String name = spi.getSimpleName().replace("SPI", "");

        AdaptorList list = adaptors.get(name);
        if (list == null) {
            // no adaptors for this type loaded.
            logger.error("getAdaptorList: No adaptors loaded for type " + name);

            throw new NoSuccessException(
                    "getAdaptorList: No adaptors loaded for type " + name);
        }
        return list;
    }

    /**
     * Obtains the value of the specified property. If the property name ends
     * with ".path", any occurrence of the string <code>SAGA_LOCATION</code>
     * is replaced by the actual value of the environment variable with the same
     * name, occurrences of a '/' are replaced by a file separator, and
     * occurrences of a ':' are replaced by a ';', unless they are followed by a
     * '\' (in this case they are assumed to separate a drive indicator from the
     * rest of the path).
     * 
     * @param s
     *            the property name.
     * @return the value of the property.
     */
    public static String getProperty(String s) {
        String result = properties.getProperty(s);
        if (s.endsWith(".path")) {
            if (result != null && sagaLocation != null) {
                result = result.replace("SAGA_LOCATION", sagaLocation);
            }
            if (result != null) {
                result = result.replace("/", File.separator);
                result = result.replace(":\\",
                        "___SOME___SPECIAL___SEQUENCE___");
                result = result.replace(":", File.pathSeparator);
                result = result.replace("___SOME___SPECIAL___SEQUENCE___",
                        ":\\");
            }
        }
        return result;
    }

    public static void setProperty(String key, String value) {
        value = value.replace("SAGA_LOCATION", sagaLocation);
        System.setProperty(key, value);
    }

    /**
     * This method populates the Map returned from a call to the method
     * getSpiClasses().
     */
    private void readJarFiles() {
        HashMap<String, ClassLoader> adaptorClassLoaders = new HashMap<String, ClassLoader>();

        String adaptorPath = getProperty("saga.adaptor.path");

        logger.debug("Adaptor path = " + adaptorPath);

        if (adaptorPath != null) {
            StringTokenizer st = new StringTokenizer(adaptorPath,
                    File.pathSeparator);
            while (st.hasMoreTokens()) {
                String dir = st.nextToken();
                logger.debug("readJarFiles: dir = " + dir);

                File adaptorRoot = new File(dir);
                if (!adaptorRoot.isDirectory()) {
                    logger.debug("Specified path " + dir
                            + " is not a directory");
                    continue;
                }
                // Now get the adaptor directories from the adaptor path.
                // Adaptor directories are directories whose name ends
                // with "Adaptor".
                File[] adaptorDirs = adaptorRoot.listFiles(new FileFilter() {
                    public boolean accept(File file) {
                        return file.isDirectory()
                                && file.getName().endsWith("Adaptor");
                    }
                });

                // Create a separate classloader for each adaptor directory.
                if (adaptorDirs != null) {
                    for (File adaptorDir : adaptorDirs) {

                        try {
                            adaptorClassLoaders.put(adaptorDir.getName(),
                                    loadDirectory(adaptorDir));
                        } catch (Exception e) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Unable to load adaptor '"
                                        + adaptorDir.getName() + "': " + e);
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates a classloader for a specific adaptor (directory). The name of the
     * adaptor jar-file must be the same as that of the directory, i.e., a
     * directory SocketAdaptor must have a SocketAdaptor.jar.
     * 
     * @param adaptorDir
     *            the name of the adaptor directory
     * @return the class loader.
     * @throws Exception
     *             is thrown in case of trouble.
     */
    private ClassLoader loadDirectory(File adaptorDir) throws Exception {

        // Construct a file object for the adaptor jar file.
        File adaptorJarFile = new File(adaptorDir.getPath() + File.separator
                + adaptorDir.getName() + ".jar");
        logger.debug("adaptorJarFile: " + adaptorJarFile.getPath());
        if (!adaptorJarFile.exists()) {
            // TODO: deal with exceptions better.
            throw new Exception("found adaptor dir '" + adaptorDir.getPath()
                    + "' that doesn't contain an adaptor named '"
                    + adaptorJarFile.getPath() + "'");
        }

        // Obtain list of jar files in the specified directory.
        String[] externalJars = adaptorDir.list(new java.io.FilenameFilter() {
            public boolean accept(File file, String name) {
                return name.endsWith(".jar");
            }
        });

        // Since we create an URLClassLoader, we want URLS.
        ArrayList<URL> adaptorPathURLs = new ArrayList<URL>();
        adaptorPathURLs.add(adaptorJarFile.toURI().toURL());
        if (externalJars != null) {
            for (String externalJar : externalJars) {
                if (!externalJar.equals(adaptorJarFile.getName())) {
                    adaptorPathURLs.add(new URL(adaptorJarFile.getParentFile()
                            .toURI().toURL().toString()
                            + externalJar));
                }
            }
        }

        URL[] urls = adaptorPathURLs.toArray(new URL[adaptorPathURLs.size()]);

        // Sort, so that the results are reproducable.
        Arrays.sort(urls, new URLComparator());

        URLClassLoader adaptorLoader = new URLClassLoader(urls, parentLoader);

        // Now we have a class loader.
        // Next, we find out which adaptors are inside the adaptor jar.
        JarFile adaptorJar = new JarFile(adaptorJarFile, true);
        Attributes attributes = adaptorJar.getManifest().getMainAttributes();
        for (Map.Entry<Object, Object> entry : attributes.entrySet()) {
            Attributes.Name key = (Attributes.Name) entry.getKey();
            if (key.toString().endsWith("Spi-class")) {
                // This is an adaptor!
                // Now get the service provider interface name
                // (for an attribute named 'FileSpi-class' the SPI name is
                // 'File').
                String spiName = key.toString().replace("Spi-class", "");

                // A single jar may contain more than one adaptor. In that
                // case, the list is comma-separated.
                String value = (String) entry.getValue();
                String[] adaptorClasses = value.split(",");
                for (String adaptorClass : adaptorClasses) {
                    ClassLoader context = Thread.currentThread()
                            .getContextClassLoader();
                    try {
                        // Set the context class loader of this thread,
                        // as some middleware may use the context classloader.
                        Thread.currentThread().setContextClassLoader(
                                adaptorLoader);

                        Class<?> clazz = adaptorLoader.loadClass(adaptorClass);

                        Adaptor a = new Adaptor(clazz);
                        AdaptorList s = adaptors.get(spiName);

                        if (s == null) {
                            s = new AdaptorList(spiName);
                            adaptors.put(spiName, s);
                        }

                        s.add(a);
                        // Ok, now we're done loading this class and updating
                        // our administration.
                    } catch (Exception e) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Could not load Adaptor for " + key
                                    + ": " + e);
                        }
                    } finally {
                        Thread.currentThread().setContextClassLoader(context);
                    }
                }
            }
        }

        return adaptorLoader;
    }

    /**
     * This method should not be called by the user. In fact, it is not used
     * currently. It may be useful when some adaptor uses middleware that needs
     * to be terminated explicitly. In that case, it may declare a parameterless
     * static method <code>end</code>.
     */
    public static void end() {
        SAGAEngine engine = getSAGAEngine();

        synchronized (engine) {
            if (engine.ended) {
                return;
            }

            engine.ended = true;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("shutting down SAGA");
        }

        for (AdaptorList l : engine.adaptors.values()) {
            for (Adaptor a : l) {
                Class<?> c = a.getAdaptorClass();

                // Invoke the "end" static method of the class.
                try {
                    Method m = c.getMethod("end", (Class[]) null);
                    m.invoke((Object) null, (Object[]) null);
                } catch (Throwable t) {
                    // ignore
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("shutting down SAGA DONE");
        }
    }

    /**
     * Obtains the full name of the specified adaptor from the specified adaptor
     * list. For instance, if the specified adaptor is "javagat", and the
     * adaptorlist contains all JobService adaptors, the result could be, for
     * instance, "org.ogf.saga.adaptors.javaGAT.job.JobServiceAdaptor".
     * 
     * @param shortName
     *            the name indicating the specific adaptor.
     * @param adaptors
     *            the adaptor list.
     * @return the full name for the specified adaptor, or <code>null</code>
     *         if not found.
     */

    private static String getFullAdaptorName(String shortName,
            AdaptorList adaptors) {
        // The idea of adaptor class names is the following:
        // - the name ends with the name of the SPI it is providing, so a
        // JobService adaptor should end with "JobServiceAdaptor".
        // - the specific adaptor, f.i. javaGAT, should be in the
        // package name, or in the class name as well.
        for (Adaptor adaptor : adaptors) {
            String adaptorType = adaptors.getSpiName() + "Adaptor";
            String adaptorName = adaptor.getShortAdaptorClassName();
            if (adaptorName.endsWith(adaptorType)) {
                if (adaptor.getAdaptorName().toLowerCase().contains(
                        shortName.toLowerCase())) {
                    logger.debug("getFullAdaptorName returns "
                            + adaptor.getAdaptorName());
                    return adaptor.getAdaptorName();
                }
            }
        }
        logger.debug("getFullAdaptorName returns null");
        return null;
    }

    /**
     * Reorders the adaptor list as requested by the user through a system
     * property. For the JobService adaptors for instance, the user may set the
     * system property <code>JobService.adaptor.name</code>. For example:
     * <br>
     * <code>
     * JobService.adaptor.name=javagat,gridsam
     * </code> </br> means that
     * the engine must first try the <code>javagat</code> adaptor, and then
     * the <code>gridsam</code> adaptor. <br>
     * <code>
     * JobService.adaptor.name=!javagat
     * </code> means that the engine
     * must try all available adaptors, except for <code>javagat</code>.
     * 
     * @param adaptors
     *            the adaptor list.
     * @return the resulting adaptor list.
     */
    private static AdaptorList reorderAdaptorList(AdaptorList adaptors) {
        // parse the orderingString
        // all adaptor names are separated by a ',' and adaptors that should
        // not be used are prefixed with a '!'
        // the case of the adaptor's name doesn't matter
        int insertPosition = 0;

        // Create a copy of the adaptor list.
        AdaptorList result = new AdaptorList(adaptors);

        // retrieve the adaptor type from the cpiClass
        String adaptorType = adaptors.getSpiName();

        String nameString = getProperty(adaptorType + ".adaptor.name");

        if (logger.isDebugEnabled()) {
            logger.debug("Property " + adaptorType + ".adaptor.name = "
                    + (nameString == null ? "(null)" : nameString));
        }
        if (nameString == null) {
            return result;
        }

        // split the nameString into individual names
        String[] names = nameString.split(",");
        for (String name : names) {
            name = name.trim(); // remove the whitespace
            // names of adaptors that should not be used start with a '!'
            if (name.startsWith("!")) {
                name = name.substring(1); // remove the '!'
                int pos = result.getPos(getFullAdaptorName(name, adaptors));
                // if the adaptor is found, remove it from the list
                if (pos >= 0) {
                    result.remove(pos);
                    // the insert position changes when an adaptor before
                    // this position is removed, so adjust the insertPosition
                    // administration.
                    if (pos < insertPosition)
                        insertPosition--;
                } else {
                    if (logger.isInfoEnabled()) {
                        logger.info("Found non existing adaptor in "
                                + adaptorType + ".adaptor.name property: "
                                + name);
                    }
                }
            } else if (name.equals("")) {
                // which means there is an empty name string. All the remaining
                // adaptors can be added in random order, so just return the
                // result
                return result;
            } else {
                // when the current position is before the insert position, it
                // means that the adaptor is already inserted, so don't insert
                // it again
                String fullAdaptorName = getFullAdaptorName(name, adaptors);
                if (result.getPos(fullAdaptorName) >= insertPosition) {
                    // try to place the adaptor on the proper position
                    if (result.placeAdaptor(insertPosition, fullAdaptorName) >= 0) {
                        // adjust the insert position only when the replacing
                        // succeeded
                        insertPosition++;
                    } else {
                        if (logger.isInfoEnabled()) {
                            logger.info("Found non existing adaptor in "
                                    + adaptorType + ".adaptor.name property: "
                                    + name);
                        }
                    }

                }
            }
        }
        // when at least one adaptor has been replaced properly (without being
        // removed) the other adaptors are removed from the list unless, the
        // namestring ends with a ','
        if (insertPosition > 0 && !nameString.trim().endsWith(",")) {
            int endPosition = result.size();
            for (int i = insertPosition; i < endPosition; i++) {
                result.remove(insertPosition);
            }
        } else if (insertPosition == 0) {
            throw new Error("no adaptors available for property: \""
                    + adaptorType + ".adaptor.name\", \"" + nameString + "\"");
        }
        return result;
    }

    /**
     * Creates a proxy for the adaptor spi interface, instantiating adaptors on
     * the fly.
     * 
     * @param interfaceClass
     *            The adaptor spi.
     * @param types
     *            the types of the constructor parameters.
     * @param tmpParams
     *            the actual constructor parameters.
     * @return the proxy object.
     * @throws SagaException
     *             when no adaptor could be created, the most specific exception
     *             is thrown.
     */
    public static Object createAdaptorProxy(Class<?> interfaceClass,
            Class<?>[] types, Object[] tmpParams) throws SagaException {

        SAGAEngine sagaEngine = SAGAEngine.getSAGAEngine();

        AdaptorList adaptors = sagaEngine.getAdaptorList(interfaceClass);

        adaptors = reorderAdaptorList(adaptors);

        AdaptorInvocationHandler handler = new AdaptorInvocationHandler(
                adaptors, types, tmpParams);
        Object proxy = Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class[] { interfaceClass }, handler);
        return proxy;
    }

    /**
     * Creates a new proxy, which is a copy (clone) of the specified proxy, with
     * cloned adaptors.
     * 
     * @param interfaceClass
     *            the adaptor spi.
     * @param proxy
     *            the proxy to clone.
     * @param wrapper
     *            the clone of the wrapper object initiating the clone.
     * @return the proxy clone.
     */
    public static Object createAdaptorCopy(Class<?> interfaceClass,
            Object proxy, Object wrapper) {
        AdaptorInvocationHandler copy = new AdaptorInvocationHandler(
                (AdaptorInvocationHandler) Proxy.getInvocationHandler(proxy),
                wrapper);
        return Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class[] { interfaceClass }, copy);
    }
}
