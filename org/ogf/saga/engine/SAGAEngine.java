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
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;

/**
 * This class make the various SAGA adaptors available to SAGA.
 * Some of this code is stolen from the JavaGAT engine implementation.
 * This class also supports cloning of SAGA adaptors, which is
 * required when a SAGA object is cloned.
 */
public class SAGAEngine {

    /**
     * This member variable holds reference to the single SAGAEngine.
     */
    private static SAGAEngine sagaEngine = null;

    private boolean ended = false;

    /** Keys are SPI names, elements are AdaptorLists. */
    private AdaptorSet adaptors;

    private static Logger logger = Logger.getLogger(SAGAEngine.class);

    private static class URLComparator implements Comparator<URL> {
        public int compare(URL u1, URL u2) {
            return u1.toString().compareTo(u2.toString());
        }
    }

    /** Constructs a default SAGAEngine instance. */
    private SAGAEngine() {

        adaptors = new AdaptorSet();

        readJarFiles();

        if (adaptors.size() == 0) {
            throw new Error("SAGA: No adaptors could be loaded");
        }

        if (logger.isInfoEnabled()) {
            logger.info("\n" + adaptors.toString());
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
     * Returns a list of adaptors for the specified spiClass.
     * 
     * @param spiClass
     *            the spi class for which to look
     * @return the list of adaptors
     */
    private AdaptorList getAdaptorList(Class<?> spiClass) {
        String name = spiClass.getSimpleName().replace("SpiInterface", ""); 

        AdaptorList list = adaptors.getAdaptorList(name);
        if (list == null) {
            // no adaptors for this type loaded.
            if (logger.isInfoEnabled()) {
                logger.info("getAdaptorList: No adaptors loaded for type "
                        + name);
            }

            throw new Error(
                    "getAdaptorList: No adaptors loaded for type "
                    + name);
        }
        return list;
    }

    /**
     * This method populates the Map returned from a call to the
     * method getSpiClasses().
     */
    private void readJarFiles() {
        HashMap<String, ClassLoader> adaptorClassLoaders
        = new HashMap<String, ClassLoader>();

        String adaptorPath = System.getProperty("saga.adaptor.path");

        if (adaptorPath != null) {
            StringTokenizer st = new StringTokenizer(adaptorPath,
                    File.pathSeparator);
            while (st.hasMoreTokens()) {
                String dir = st.nextToken();

                File adaptorRoot = new File(dir);
                // Now get the adaptor dirs from the adaptor path, adaptor dirs are
                // of course directories and further will end with "Adaptor".
                File[] adaptorDirs = adaptorRoot.listFiles(new FileFilter() {
                    public boolean accept(File file) {
                        return file.isDirectory()
                        && file.getName().endsWith("Adaptor");
                    }
                });

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

    private ClassLoader loadDirectory(File adaptorDir) throws Exception {
        File adaptorJarFile = new File(adaptorDir.getPath() + File.separator
                + adaptorDir.getName() + ".jar");
        logger.debug("adaptorJarFile: " + adaptorJarFile.getPath());
        if (!adaptorJarFile.exists()) {
            throw new Exception("found adaptor dir '" + adaptorDir.getPath()
                    + "' that doesn't contain an adaptor named '"
                    + adaptorJarFile.getPath() + "'");
        }
        JarFile adaptorJar = new JarFile(adaptorJarFile, true);
        Attributes attributes = adaptorJar.getManifest().getMainAttributes();
        String[] externalJars = adaptorDir.list(new java.io.FilenameFilter() {
            public boolean accept(File file, String name) {
                return name.endsWith(".jar");
            }
        });
        ArrayList<URL> adaptorPathURLs = new ArrayList<URL>();
        adaptorPathURLs.add(adaptorJarFile.toURI().toURL());
        if (externalJars != null) {
            for (String externalJar : externalJars) {
                adaptorPathURLs.add(new URL(adaptorJarFile.getParentFile()
                        .toURI().toURL().toString()
                        + externalJar));
            }
        }
        
        URL[] urls = adaptorPathURLs.toArray(new URL[adaptorPathURLs.size()]);
        
        Arrays.sort(urls, new URLComparator());

        for (URL url : urls) {
            System.out.println("URL: " + url);
        }
        URLClassLoader adaptorLoader = new URLClassLoader(urls, this.getClass()
                .getClassLoader());
        
        // We've a class loader, now have a look at which adaptors are inside
        // this jar.
        for (Object key : attributes.keySet()) {
            if (((Attributes.Name) key).toString().endsWith("Spi-class")) {
                // this is an adaptor!

                // now get the spi name (for 'FileSpi' the spi name is 'File')
                String spiName = ((Attributes.Name) key).toString().replace(
                        "Spi-class", "");
                String[] adaptorClasses = attributes.getValue(
                        (Attributes.Name) key).split(",");
                for (String adaptorClass : adaptorClasses) {
                    try {
                        // Thread.currentThread().setContextClassLoader(
                        //        adaptorLoader);
                        Class<?> clazz = adaptorLoader.loadClass(adaptorClass);
                        
                        Adaptor a = new Adaptor(spiName, clazz);
                        AdaptorList s = adaptors.getAdaptorList(spiName);

                        if (s == null) {
                            s = new AdaptorList(spiName);
                            adaptors.add(s);
                        }

                        s.add(a);
                        // Ok, now we're done loading this class and updating
                        // our administration.
                    } catch (Exception e) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Could not load Adaptor for " + key
                                    + ": " + e);
                        }
                    }
                }
            }
        }

        return adaptorLoader;
    }

    /**
     * This method should not be called by the user.
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

        for (AdaptorList l : engine.adaptors) {
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

    // The idea of adaptor class names is the following:
    // - the name ends with the name of the SPI it is providing, so a
    //   JobService adaptor should end with "JobService".
    // - the specific adaptor, f.i. javaGAT, should be in the
    //   package name, or in the class name as well.
    private static String getFullAdaptorName(String shortName,
            AdaptorList adaptors) {
        for (Adaptor adaptor : adaptors) {
            String adaptorType = adaptor.getSpiName();
            String adaptorName = adaptor.getShortAdaptorClassName();
            if (adaptorName.endsWith(adaptorType)) {
                if (adaptor.getAdaptorName().toLowerCase().contains(shortName.toLowerCase())) {
                    logger.debug("getFullAdaptorName returns " + adaptor.getAdaptorName());
                    return adaptor.getAdaptorName();
                }
            }
        }
        logger.debug("getFullAdaptorName returns null");
        return null;
    }

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

        String nameString = System.getProperty(adaptorType + ".adaptor.name");

        if (logger.isDebugEnabled()) {
            logger.debug("adaptorType = " + adaptorType + ", nameString = " + (nameString == null ? "(null)" : nameString));
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
                                    + adaptorType
                                    + ".adaptor.name property: " + name);
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
	    throw new Error("no adaptors available for property: \"" + adaptorType + ".adaptor.name\", \"" + nameString + "\"");
	}
        return result;
    }

    /**
     * Creates a proxy for the adaptor spi interface, instantiating adaptors
     * on the fly.
     * @param interfaceClass The adaptor spi.
     * @param types the types of the constructor parameters.
     * @param tmpParams the actual constructor parameters.
     * @return the proxy object.
     * @throws org.ogf.saga.error.SagaException when no adaptor could be
     * created, the most specific exception is thrown.
     */
    public static Object createAdaptorProxy(
            Class<?> interfaceClass, Class[] types, Object[] tmpParams)
                throws org.ogf.saga.error.SagaException {

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
     * Creates a new proxy, which is a copy (clone) of the specified proxy,
     * with cloned adaptors.
     * @param interfaceClass the adaptor spi.
     * @param proxy the proxy to clone. 
     * @param wrapper the clone of the wrapper object initiating the clone.
     * @return the proxy clone.
     */
    public static Object createAdaptorCopy(Class<?> interfaceClass, Object proxy, Object wrapper) {
        AdaptorInvocationHandler copy = new AdaptorInvocationHandler(
                (AdaptorInvocationHandler) Proxy.getInvocationHandler(proxy), wrapper);
        return Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class[] { interfaceClass }, copy);
    }
}
