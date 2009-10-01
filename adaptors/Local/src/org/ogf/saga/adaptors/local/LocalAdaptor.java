package org.ogf.saga.adaptors.local;

import java.util.Collection;
import java.util.HashSet;

import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.url.URL;

public class LocalAdaptor {

    private static final Collection<String> ACCEPTED_SCHEMES = new HashSet<String>(
            3);
    static {
        ACCEPTED_SCHEMES.add("file");
        ACCEPTED_SCHEMES.add("local");
        ACCEPTED_SCHEMES.add("any");
    }
    
    /**
     * Check if the given URL is accepted by the local adaptor. We only
     * accept absolute URLs with a scheme 'local', 'file', or 'any', or relative
     * URLs (who's path is then considered to be local).
     * 
     * @param u
     *            the URL to check
     * @throws IncorrectURLException
     *             if the URL is not accepted as a base URL.
     */
    public static void checkURL(URL u) throws IncorrectURLException {
        if (u.isAbsolute()) {
            String scheme = u.getScheme();
            if (!ACCEPTED_SCHEMES.contains(scheme)) {
                throw new IncorrectURLException("Unknown scheme: '" + scheme
                        + "', local adaptor only accepts "
                        + ACCEPTED_SCHEMES);
            }
    
            String path = u.getPath();
            if (path == null || path.isEmpty()) {
                throw new IncorrectURLException("Path is empty");
            }
        }
    }
    
}
