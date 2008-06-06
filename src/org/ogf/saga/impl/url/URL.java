package org.ogf.saga.impl.url;

import java.net.URI;
import java.net.URISyntaxException;

import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;

/**
 * URL class as specified by SAGA. The java.net.URL class is not usable because
 * of all kinds of side-effects.
 */
public class URL implements org.ogf.saga.url.URL {
    private URI u;

    /**
     * Constructs an URL from the specified string.
     * 
     * @param url
     *            the string.
     * @exception BadParameterException
     *                is thrown when there is a syntax error in the parameter.
     */
    public URL(String url) throws NotImplementedException,
            BadParameterException, NoSuccessException {
        try {
            u = new URI(url);
        } catch (URISyntaxException e) {
            throw new BadParameterException("syntax error in url", e);
        }
    }

    private URL(URI u) {
        this.u = u;
    }

    /* (non-Javadoc)
     * @see org.ogf.saga.url.URL#setURL(java.lang.String)
     */
    public void setURL(String url) throws NotImplementedException,
            BadParameterException {
        try {
            u = new URI(url);
        } catch (URISyntaxException e) {
            throw new BadParameterException("syntax error in url", e);
        }
    }

    /* (non-Javadoc)
     * @see org.ogf.saga.url.URL#getURL()
     */
    public String getURL() throws NotImplementedException {
        return toString();
    }

    /* (non-Javadoc)
     * @see org.ogf.saga.url.URL#getFragment()
     */
    public String getFragment() throws NotImplementedException {
        return u.getFragment();
    }

    /* (non-Javadoc)
     * @see org.ogf.saga.url.URL#setFragment(java.lang.String)
     */
    public void setFragment(String fragment) throws NotImplementedException,
            BadParameterException {
        try {
            u = new URI(u.getScheme(), u.getUserInfo(), u.getHost(), u
                    .getPort(), u.getPath(), u.getQuery(), fragment);
        } catch (URISyntaxException e) {
            throw new BadParameterException("syntax error in fragment", e);
        }
    }

    /* (non-Javadoc)
     * @see org.ogf.saga.url.URL#getHost()
     */
    public String getHost() throws NotImplementedException {
        return u.getHost();
    }

    /* (non-Javadoc)
     * @see org.ogf.saga.url.URL#setHost(java.lang.String)
     */
    public void setHost(String host) throws NotImplementedException,
            BadParameterException {
        try {
            u = new URI(u.getScheme(), u.getUserInfo(), host, u.getPort(), u
                    .getPath(), u.getQuery(), u.getFragment());
        } catch (URISyntaxException e) {
            throw new BadParameterException("syntax error in host", e);
        }
    }

    /* (non-Javadoc)
     * @see org.ogf.saga.url.URL#getPath()
     */
    public String getPath() throws NotImplementedException {
        return u.getPath();
    }

    /* (non-Javadoc)
     * @see org.ogf.saga.url.URL#setPath(java.lang.String)
     */
    public void setPath(String path) throws NotImplementedException,
            BadParameterException {
        try {
            u = new URI(u.getScheme(), u.getUserInfo(), u.getHost(), u
                    .getPort(), path, u.getQuery(), u.getFragment());
        } catch (URISyntaxException e) {
            throw new BadParameterException("syntax error in host", e);
        }
    }

    /* (non-Javadoc)
     * @see org.ogf.saga.url.URL#getPort()
     */
    public int getPort() throws NotImplementedException {
        return u.getPort();
    }

    /* (non-Javadoc)
     * @see org.ogf.saga.url.URL#setPort(int)
     */
    public void setPort(int port) throws NotImplementedException,
            BadParameterException {
        try {
            u = new URI(u.getScheme(), u.getUserInfo(), u.getHost(), port, u
                    .getPath(), u.getQuery(), u.getFragment());
        } catch (URISyntaxException e) {
            throw new BadParameterException("syntax error in port", e); // ???
        }
    }

    /* (non-Javadoc)
     * @see org.ogf.saga.url.URL#getQuery()
     */
    public String getQuery() throws NotImplementedException {
        return u.getQuery();
    }

    /* (non-Javadoc)
     * @see org.ogf.saga.url.URL#setQuery(java.lang.String)
     */
    public void setQuery(String query) throws NotImplementedException,
            BadParameterException {
        try {
            u = new URI(u.getScheme(), u.getUserInfo(), u.getHost(), u
                    .getPort(), u.getPath(), query, u.getFragment());
        } catch (URISyntaxException e) {
            throw new BadParameterException("syntax error in query", e);
        }
    }

    /* (non-Javadoc)
     * @see org.ogf.saga.url.URL#getScheme()
     */
    public String getScheme() throws NotImplementedException {
        return u.getScheme();
    }

    /* (non-Javadoc)
     * @see org.ogf.saga.url.URL#setScheme(java.lang.String)
     */
    public void setScheme(String scheme) throws NotImplementedException,
            BadParameterException {
        try {
            u = new URI(scheme, u.getUserInfo(), u.getHost(), u.getPort(), u
                    .getPath(), u.getQuery(), u.getFragment());
        } catch (URISyntaxException e) {
            throw new BadParameterException("syntax error in scheme", e);
        }
    }

    /* (non-Javadoc)
     * @see org.ogf.saga.url.URL#getUserInfo()
     */
    public String getUserInfo() throws NotImplementedException {
        return u.getUserInfo();
    }

    /* (non-Javadoc)
     * @see org.ogf.saga.url.URL#setUserInfo(java.lang.String)
     */
    public void setUserInfo(String userInfo) throws NotImplementedException,
            BadParameterException {
        try {
            u = new URI(u.getScheme(), userInfo, u.getHost(), u.getPort(), u
                    .getPath(), u.getQuery(), u.getFragment());
        } catch (URISyntaxException e) {
            throw new BadParameterException("syntax error in query", e);
        }
    }

    /* (non-Javadoc)
     * @see org.ogf.saga.url.URL#translate(java.lang.String)
     */
    public org.ogf.saga.url.URL translate(String scheme) throws NotImplementedException,
            BadParameterException, NoSuccessException {
        try {
            URI url = new URI(scheme, u.getUserInfo(), u.getHost(),
                    u.getPort(), u.getPath(), u.getQuery(), u.getFragment());
            // Not quite correct: the SAGA specs say that NoSuccess should be
            // thrown when the scheme is not supported. How to check this
            // here ???
            return new URL(url);
        } catch (URISyntaxException e) {
            throw new BadParameterException("syntax error in scheme", e);
        }
    }

    public int hashCode() {
        return u.hashCode();
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof URL)) {
            return false;
        }
        URL other = (URL) o;
        return u.equals(other.u);
    }

    public String toString() {
        return u.toString();
    }

    /* (non-Javadoc)
     * @see org.ogf.saga.url.URL#resolve(org.ogf.saga.URL)
     */
    public org.ogf.saga.url.URL resolve(org.ogf.saga.url.URL url) throws NoSuccessException {
        String s = url.toString();
        URI uri;
        try {
            uri = new URI(s);
        } catch (URISyntaxException e) {
            throw new NoSuccessException("URL " + s + " not recognized as a URL", e);
        }
        uri = u.resolve(uri);
        if (uri.toString().equals(s)) {
            return url;
        }

        return new URL(uri);
    }

    /* (non-Javadoc)
     * @see org.ogf.saga.url.URL#isAbsolute()
     */
    public boolean isAbsolute() {
        return u.isAbsolute();
    }

    /* (non-Javadoc)
     * @see org.ogf.saga.url.URL#normalize()
     */
    public URL normalize() {
        URI uri = u.normalize();
        if (uri == u) {
            return this;
        }
        return new URL(uri);
    }
}
