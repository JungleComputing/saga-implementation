package org.ogf.saga.engine;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.log4j.Logger;

/**
 * This class make the various SAGA adaptors available to SAGA.
 * The code is mostly stolen from the JavaGAT engine implementation.
 */
public class SAGAEngine {

    /**
     * This member variable holds reference to the single SAGAEngine.
     */
    private static SAGAEngine sagaEngine = null;

    private boolean ended = false;

    /** Keys are spiClass names, elements are AdaptorLists. */
    private AdaptorSet adaptors;

    private URLClassLoader sagaClassLoader = null;

    private static Logger logger = Logger.getLogger(SAGAEngine.class);

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
     * This method periodically populates the Map returned from a call to the
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
     * Obtains Files in the optional directory.
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

        return files;
    }

    /**
     * Obtains JarFiles in the optional directory that are SAGA jars.
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
        
        loadSpiClass(jarFile, manifest, attributes, "Blabla", null);

        /*
        loadSpiClass(jarFile, manifest, attributes, "Endpoint",
                EndpointSpi.class);
        loadSpiClass(jarFile, manifest, attributes, "AdvertService",
                AdvertServiceSpi.class);
        loadSpiClass(jarFile, manifest, attributes, "Monitorable",
                MonitorableSpi.class);
        loadSpiClass(jarFile, manifest, attributes, "SteeringManager",
                SteeringManagerSpi.class);
        loadSpiClass(jarFile, manifest, attributes, "File", FileSpi.class);
        loadSpiClass(jarFile, manifest, attributes, "LogicalFile",
                LogicalFileSpi.class);
        loadSpiClass(jarFile, manifest, attributes, "RandomAccessFile",
                RandomAccessFileSpi.class);
        loadSpiClass(jarFile, manifest, attributes, "FileInputStream",
                FileInputStreamSpi.class);
        loadSpiClass(jarFile, manifest, attributes, "FileOutputStream",
                FileOutputStreamSpi.class);
        loadSpiClass(jarFile, manifest, attributes, "ResourceBroker",
                ResourceBrokerSpi.class);
        */
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

    public static Object createAdaptorProxy(String spiClassName,
            Class<?> interfaceClass, Object[] tmpParams) {

        Class<?> spiClass;
        try {
            spiClass = Class.forName(spiClassName);
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }

        SAGAEngine sagaEngine = SAGAEngine.getSAGAEngine();

        AdaptorList adaptors = sagaEngine.getAdaptorList(spiClass);
        if (adaptors == null) {
            throw new Error("could not find any adaptors");
        }

        AdaptorInvocationHandler handler = new AdaptorInvocationHandler(
                adaptors, tmpParams);
        Object proxy = Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class[] { interfaceClass }, handler);
        return proxy;
    }
}