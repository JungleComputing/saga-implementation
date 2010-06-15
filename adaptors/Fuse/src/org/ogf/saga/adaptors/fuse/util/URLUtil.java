package org.ogf.saga.adaptors.fuse.util;

import java.io.File;
import java.net.URI;

import org.ogf.saga.adaptors.fuse.AutoMounter;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

public class URLUtil {

    /**
     * @throws NotImplementedException
     *             if the 'external' URL points to a local entry.
     */
    public static void checkNotLocal(URL base) throws NotImplementedException {
        if (isLocal(base)) {
            throw new NotImplementedException(
                    "FUSE adaptor rejects local operation");
        }
    }

    /**
     * @throws NotImplementedException if the combination of the 'external' 
     * base URL and the given one (possible relative to the base URL) points 
     * to a local entry. 
     */
    public static void checkNotLocal(URL base, URL name)
            throws NotImplementedException {
        if (isLocal(base, name)) {
            throw new NotImplementedException(
                    "FUSE adaptor rejects local operation");
        }
    }

    /**
     * @throws NotImplementedException if the combination of the 'external' 
     * base URL and the given parameter (possible relative to the base URL) 
     * results in a local source entry (i.e. the base URL) and a local target
     * entry.
     */
    public static void checkNotLocalToLocal(URL base, URL target)
            throws NotImplementedException {
        if (isLocal(base, target)) {
            throw new NotImplementedException(
                    "FUSE adaptor rejects local-to-local operation");
        }
    }

    /**
     * @throws NotImplementedException if the combination of the 'external' 
     * base URL and the given parameters (possible relative to the base URL) 
     * results in a local source entry and a local target entry.
     */
    public static void checkNotLocalToLocal(URL base, URL source, URL target)
            throws NotImplementedException {
        if (isLocal(base, source) && isLocal(base, target)) {
            throw new NotImplementedException(
                    "FUSE adaptor rejects local-to-local operation");
        }
    }
    
    public static boolean isLocal(URL base, URL rhs) {
        if (isRelative(rhs)) {
            return isLocal(base);
        }
        return isLocal(rhs);
    }
    
    public static boolean isLocal(URL u) {
        String scheme = u.getScheme();
    
        // any URL without a scheme is considered to be local
        if (!URLUtil.isSet(scheme)) {
            return true;
        }
    
        // local URLs with a scheme should have scheme 'file' 
        // and host 'localhost' or the current host name
        return AutoMounter.FILE_SCHEME.equals(scheme) &&
            org.ogf.saga.impl.url.URLUtil.refersToLocalHost(u);
    }

    public static boolean isRelative(URL u) {
        // a URL is relative if it does not have a scheme nor a host
        String scheme = u.getScheme();
        String host = u.getHost();
        return !URLUtil.isSet(scheme) && !URLUtil.isSet(host);
    }

    public static boolean isSet(String s) {
        if (s == null) {
            return false;
        }
        return s.trim().length() > 0;
    }

    public static String getPathSafe(URL u) {
        String result = u.getPath();
    
        if (result == null) {
            return "";
        }
        return result;
    }

    public static String normalizePath(String path) {
        // remove all leading '/../' entries
        while (path.startsWith("/../")) {
            path = path.substring(4);
        }
    
        // change a last '/..' to '/'
        if (path.equals("/..")) {
            path = "/";
        }
    
        return path;
    }

    public static URL createRelativeURL(URL base, URL rhs) 
            throws BadParameterException, NoSuccessException {
        
    	String basePath = base.getPath();
    	if (!isSet(basePath) || "/".equals(basePath)) {
        	// if the base URL has no path, we assume it's '/'
    		// in this case, 
    	}

    	// combine the base and rhs path
        File f = new File(basePath, rhs.getPath());
        URI u = f.toURI();
        
        // normalize the combined path (i.e. remove all '..' entries)
        String path = normalizePath(u.getPath());

        
        if (path.startsWith(basePath)) {
        	path = path.substring(basePath.length());
        }
        
        URL result = URLFactory.createURL(rhs.toString());
        result.setPath(path);
        
        return result;
    }

}
