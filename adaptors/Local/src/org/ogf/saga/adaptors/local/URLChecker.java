package org.ogf.saga.adaptors.local;

import java.io.IOException;
import java.net.InetAddress;

import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.url.URL;

public class URLChecker {
  
    private static String getLocalHostName() {
        try {
            InetAddress a = InetAddress.getLocalHost();
            if (a != null) {
                return a.getHostName();
            }
        } catch (IOException e) {
            // ignore
        }
        return "localhost";
    }
    
    private static String getLocalCanonicalHostName() {
        try {
            InetAddress a = InetAddress.getLocalHost();
            if (a != null) {
                return a.getCanonicalHostName();
            }
        } catch (IOException e) {
            // ignore
        }
        return "localhost";
    }

    private static String[] getLocalHostIPs() {
        try {
            InetAddress[] all = InetAddress.getAllByName("localhost");
            String[] res = new String[all.length];
            for (int i = 0; i < all.length; i++) {
                res[i] = all[i].getHostAddress();
            }
            return res;
        } catch (IOException e) {
            // ignore
        }
        return null;
    }

    private static String[] getLocalHostIPsFromHostName() {
        try {
            InetAddress a = InetAddress.getLocalHost();
            if (a != null) {
                InetAddress[] all = InetAddress.getAllByName(a.getHostName());
                String[] res = new String[all.length];
                for (int i = 0; i < all.length; i++) {
                    res[i] = all[i].getHostAddress();
                }
                return res;
            }
        } catch (IOException e) {
            // ignore
        }
        return null;
    }
    
    /**
     * Extensive check whether the URL refers to the local machine. This method
     * checks for the hostname "localhost", the short hostname, the full
     * hostname and the ip address. When the URL specifies a port number, it may
     * in fact be a tunnel and this call will return <tt>false</tt>.
     * 
     * @return true if the URI refers to the localhost, false otherwise.
     */
    protected static boolean refersToLocalHost(URL u) {
        String host = u.getHost();
        if (host == null) {
            return true;
        }

        if (u.getPort() != -1) {
            return false;
        }

        if (host.equals("localhost")) {
            return true;
        }

        if (getLocalHostName().equals(host)) {
            return true;
        }

        if (getLocalCanonicalHostName().equals(host)) {
            return true;
        }

        String[] localhostIPs = getLocalHostIPs();
        if (localhostIPs != null) {
            for (String localhostIP : localhostIPs) {
                if (localhostIP.equals(host)) {
                    return true;
                }
            }
        }

        String[] localhostIPsFromHostName = getLocalHostIPsFromHostName();
        if (localhostIPsFromHostName != null) {
            for (String localhostIP : localhostIPsFromHostName) {
                if (localhostIP.equals(host)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Check if the given URL is acceptable. We only accept absolute URLs with
     * one of the valid schemes, or relative URLs (who's path is then considered
     * to be local).
     * 
     * @param u
     *            the URL to check
     * @throws IncorrectURLException
     *             if the URL is not acceptable
     */
    public static void check(URL u) throws IncorrectURLException {
        if (u.isAbsolute()) {
            if (!refersToLocalHost(u)) {
                throw new IncorrectURLException(
                        "URL does not refer to local host");
            }

            String path = u.getPath();
            if (path == null || path.length() == 0) {
                throw new IncorrectURLException("Path is empty");
            }
        }
    }
    
}
