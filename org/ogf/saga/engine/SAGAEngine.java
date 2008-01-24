package org.ogf.saga.engine;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Comparator;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.log4j.Logger;
import org.ogf.saga.spi.file.DirectorySpiInterface;
import org.ogf.saga.spi.file.FileInputStreamSpiInterface;
import org.ogf.saga.spi.file.FileOutputStreamSpiInterface;
import org.ogf.saga.spi.file.FileSpiInterface;
import org.ogf.saga.spi.job.JobServiceSpiInterface;
import org.ogf.saga.spi.logicalfile.LogicalDirectorySpiInterface;
import org.ogf.saga.spi.logicalfile.LogicalFileSpiInterface;
import org.ogf.saga.spi.namespace.NSDirectorySpiInterface;
import org.ogf.saga.spi.namespace.NSEntrySpiInterface;
import org.ogf.saga.spi.rpc.RPCSpiInterface;
import org.ogf.saga.spi.stream.StreamServiceSpiInterface;
import org.ogf.saga.spi.stream.StreamSpiInterface;

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

    private URLClassLoader sagaClassLoader = null;

    private static Logger logger = Logger.getLogger(SAGAEngine.class);

    private static class FileComparator implements Comparator<File> {
        public int compare(File f1, File f2) {
            return f1.getName().compareTo(f2.getName());
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
        AdaptorList list = adaptors.getAdaptorList(spiClass.getName());
        if (list == null) {
            // no adaptors for this type loaded.
            if (logger.isInfoEnabled()) {
                logger.info("getAdaptorList: No adaptors loaded for type "
                        + spiClass.getName());
            }

            throw new Error(
                    "getAdaptorList: No adaptors loaded for type "
                    + spiClass.getName());
        }
        return list;
    }

    /**
     * This method populates the Map returned from a call to the
     * method getSpiClasses().
     */
    private void readJarFiles() {
        List<JarFile> adaptorPathList = new ArrayList<JarFile>();

        String adaptorPath = System.getProperty("saga.adaptor.path");

        if (adaptorPath != null) {
            StringTokenizer st = new StringTokenizer(adaptorPath,
                    File.pathSeparator);

            while (st.hasMoreTokens()) {
                String dir = st.nextToken();
                List<JarFile> l = getJarFiles(dir);
                adaptorPathList.addAll(l);
            }
        }

        ArrayList<URL> adaptorPathURLs = new ArrayList<URL>();

        // Sort jar files: put adaptors first.
        // Adaptors might override classes in the external jars.
        
        for (JarFile jarFile : adaptorPathList) {

            try {
                File f = new File(jarFile.getName());

                if (jarFile.getName().endsWith("Adaptor.jar")) {
                    adaptorPathURLs.add(0, f.toURI().toURL()); // add to
                    // beginning
                } else {
                    adaptorPathURLs.add(f.toURI().toURL()); // add to end
                }

            } catch (Exception e) {
                throw new Error(e);
            }
        }

        URL[] urls = adaptorPathURLs.toArray(new URL[adaptorPathURLs.size()]);

        if (logger.isDebugEnabled()) {
            logger.debug("List of SAGA jar files is: " + getJarsAsString(urls));
        }
        sagaClassLoader = new URLClassLoader(urls, 
                this.getClass().getClassLoader());

        // Populate spiClasses
        loadJarFiles(adaptorPathList);
    }

    private String getJarsAsString(URL[] urls) {
        StringBuffer buf = new StringBuffer();
        for (URL url : urls) {
            buf.append("    ");
            buf.append(url.getFile());
        }
        return buf.toString();
    }

    /**
     * Obtains Files in the specified directory.
     * 
     * @param f
     *            a directory to list
     * @return a list of files in the passed directory
     */
    private File[] getFiles(File f) {
        File[] files = f.listFiles();

        if (files == null) {
            return new File[0];
        }
        // Sort the list of files so that the classloader always sees the jar
        // files in the same order. Then, at least, it is deterministic which
        // version of a class is loaded. It could still be the wrong one for
        // a specific adaptor, though, especially when different adaptors
        // depend on different versions of a jar.
        // I don't know what to do about that. Different classloader for
        // each adaptor? --Ceriel
        Arrays.sort(files, new FileComparator());

        return files;
    }

    /**
     * Obtains JarFiles in the specified directory.
     * 
     * @param dir
     *            the directory to get the jar files from
     * @return a list of JarFile objects
     */
    private List<JarFile> getJarFiles(String dir) {
        // Obtain files in the optional directory.
        File[] files = getFiles(new File(dir));
        ArrayList<JarFile> jarFiles = new ArrayList<JarFile>();

        for (File file : files) {
            if (file.isFile()) {
                try {
                    JarFile jarFile = new JarFile(file, true);
                    Manifest manifest = jarFile.getManifest();

                    if (null != manifest) {
                        manifest.getMainAttributes();
                        jarFiles.add(jarFile);
                    }
                } catch (IOException ioException) {
                    // Ignore IOException
                }
            }
        }

        return jarFiles;
    }

    private void loadSpiClass(JarFile jarFile, Manifest manifest,
            Attributes attributes, String className, Class<?> spiClazz) {
        if (logger.isDebugEnabled()) {
            logger.debug("Trying to load adaptor for " + className);
        }

        // Get info for the adaptor
        String attributeName = className + "Spi-class";
        String clazzString = attributes.getValue(attributeName);
        if (clazzString == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Adaptor for " + className
                        + " not found in Manifest");
            }
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Adaptor for " + className
                    + " found in Manifest, loading");
        }

        Class<?> clazz = null;

        // Use a URL classloader to load the adaptors. This way, they don't have
        // to be in the classpath.

        try {
            // Note: this will try the parent classloader first, which may not
            // be what you want.
            clazz = sagaClassLoader.loadClass(clazzString);
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Could not load Adaptor for " + className + ": "
                        + e, e);
            }
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Adaptor for " + className + " loaded");
        }

        Adaptor a = new Adaptor(spiClazz, clazz);
        AdaptorList s = adaptors.getAdaptorList(spiClazz.getName());

        if (s == null) {
            s = new AdaptorList(spiClazz.getName());
            adaptors.add(s);
        }

        s.add(a);
    }

    protected void loadSPIClassesFromJar(JarFile jarFile) {
        Manifest manifest = null;

        // Get info for all adaptors.
        try {
            manifest = jarFile.getManifest();
        } catch (IOException e) {
            return;
        }

        Attributes attributes = manifest.getMainAttributes();
        
        loadSpiClass(jarFile, manifest, attributes, "NSEntry",
                NSEntrySpiInterface.class);
        loadSpiClass(jarFile, manifest, attributes, "NSDirectory",
                NSDirectorySpiInterface.class);
        loadSpiClass(jarFile, manifest, attributes, "File",
                FileSpiInterface.class);
        loadSpiClass(jarFile, manifest, attributes, "FileInputStream",
                FileInputStreamSpiInterface.class);
        loadSpiClass(jarFile, manifest, attributes, "FileOutputStream",
                FileOutputStreamSpiInterface.class);
        loadSpiClass(jarFile, manifest, attributes, "Directory",
                DirectorySpiInterface.class);
        loadSpiClass(jarFile, manifest, attributes, "LogicalFile",
                LogicalFileSpiInterface.class);
        loadSpiClass(jarFile, manifest, attributes, "LogicalDirectory",
                LogicalDirectorySpiInterface.class);
        loadSpiClass(jarFile, manifest, attributes, "Stream",
                StreamSpiInterface.class);
        loadSpiClass(jarFile, manifest, attributes, "StreamService",
                StreamServiceSpiInterface.class);
        loadSpiClass(jarFile, manifest, attributes, "JobService",
                JobServiceSpiInterface.class);
        loadSpiClass(jarFile, manifest, attributes, "RPC",
                RPCSpiInterface.class);
    }

    /**
     * Load jar files in the list, looking for SPI classes.
     * 
     * @param jarFiles
     *            the list of JarFile objects to load
     */
    private void loadJarFiles(List<JarFile> jarFiles) {

        for (JarFile jarFile : jarFiles) {
            if (logger.isDebugEnabled()) {
                logger.debug("loading adaptors from " + jarFile.getName());
            }
            loadSPIClassesFromJar(jarFile);
        }
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
            String adaptorType = adaptor.getShortSpiName();
            String adaptorName = adaptor.getShortAdaptorClassName();
            if (adaptorName.endsWith(adaptorType)) {
                if (adaptor.getAdaptorName().toLowerCase().contains(shortName.toLowerCase())) {
                    return adaptor.getAdaptorName();
                }
            }
        }
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
        String adaptorType = adaptors.getSpiName().replace("SpiInterface", "");

        String nameString = System.getProperty(adaptorType + ".adaptor.name");
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
     * @throws org.ogf.saga.error.Exception when no adaptor could be
     * created, the most specific exception is thrown.
     */
    public static Object createAdaptorProxy(
            Class<?> interfaceClass, Class[] types, Object[] tmpParams)
                throws org.ogf.saga.error.Exception {

        SAGAEngine sagaEngine = SAGAEngine.getSAGAEngine();

        AdaptorList adaptors = sagaEngine.getAdaptorList(interfaceClass);

        reorderAdaptorList(adaptors);
    
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