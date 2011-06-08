package org.ogf.saga.impl.url;

import java.net.URI;
import java.net.URISyntaxException;

import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.session.Session;
import org.ogf.saga.url.URL;
import org.ogf.saga.impl.SagaObjectBase;

/**
 * URL class as specified by SAGA. The java.net.URL class is not usable because
 * of all kinds of side-effects.
 */
public class URLImpl extends SagaObjectBase implements URL {
    private URI u;

    /**
     * Constructs an URL from the specified string.
     * 
     * @param url
     *            the string.
     * @exception BadParameterException
     *                is thrown when there is a syntax error in the parameter.
     */
    public URLImpl(String url) throws BadParameterException, NoSuccessException {
        try {
            u = new URI(url);
        } catch (URISyntaxException e) {
            throw new BadParameterException("syntax error in url \"" + url + "\"", e);
        }
    }

    private URLImpl(URI u) {
        this.u = u;
    }

    public Object clone() throws CloneNotSupportedException {
        URLImpl o = (URLImpl) super.clone();
        o.u = URI.create(u.toString());
        return o;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.url.URL#setString(java.lang.String)
     */
    public void setString(String url) throws BadParameterException {
        try {
            u = new URI(url);
        } catch (URISyntaxException e) {
            throw new BadParameterException("syntax error in url \"" + url + "\"", e);
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.url.URL#setString()
     */
    public void setString() throws BadParameterException {
        setString("");

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.url.URL#getEscaped()
     */
    public String getEscaped() {
        return toString();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.url.URL#getString()
     */
    public String getString() {
        return toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.url.URL#getFragment()
     */
    public String getFragment() {
        String fragment = u.getFragment();
        if (fragment == null) {
            return "";
        }
        return fragment;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.url.URL#setFragment(java.lang.String)
     */
    public void setFragment(String fragment) throws BadParameterException {
        try {
            u = new URI(u.getScheme(), u.getUserInfo(), u.getHost(), u
                    .getPort(), u.getPath(), u.getQuery(), fragment);
        } catch (URISyntaxException e) {
            throw new BadParameterException("syntax error in fragment \"" + fragment + "\"", e);
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.url.URL#setFragment()
     */
    public void setFragment() throws BadParameterException {
        setFragment("");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.url.URL#getHost()
     */
    public String getHost() {
        String host = u.getHost();
        if (host == null) {
            return "";
        }
        return host;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.url.URL#setHost(java.lang.String)
     */
    public void setHost(String host) throws BadParameterException {
        try {
            u = new URI(u.getScheme(), u.getUserInfo(), host, u.getPort(), u
                    .getPath(), u.getQuery(), u.getFragment());
        } catch (URISyntaxException e) {
            throw new BadParameterException("syntax error in host \"" + host + "\"", e);
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.url.URL#setHost()
     */
    public void setHost() throws BadParameterException {
        setHost("");
    } 

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.url.URL#getPath()
     */
    public String getPath() {
        String path = u.getPath();
        if (path == null) {
            return "";
        }
        return path;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.url.URL#setPath(java.lang.String)
     */
    public void setPath(String path) throws BadParameterException {
        try {
            u = new URI(u.getScheme(), u.getUserInfo(), u.getHost(), u
                    .getPort(), path, u.getQuery(), u.getFragment());
        } catch (URISyntaxException e) {
            throw new BadParameterException("syntax error in path \"" + path + "\"", e);
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.url.URL#setPath()
     */
    public void setPath() throws BadParameterException {
        setPath("");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.url.URL#getPort()
     */
    public int getPort() {
        return u.getPort();     // Yes: u.getPort() returns -1 if undefined.
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.url.URL#setPort(int)
     */
    public void setPort(int port) throws BadParameterException {
        try {
            u = new URI(u.getScheme(), u.getUserInfo(), u.getHost(), port, u
                    .getPath(), u.getQuery(), u.getFragment());
        } catch (URISyntaxException e) {
            throw new BadParameterException("syntax error in port", e); // ???
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.url.URL#setPort()
     */
    public void setPort() throws BadParameterException {
        setPort(-1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.url.URL#getQuery()
     */
    public String getQuery() {
        String query = u.getQuery();
        if (query == null) {
            return "";
        }
        return query;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.url.URL#setQuery(java.lang.String)
     */
    public void setQuery(String query) throws BadParameterException {
        try {
            u = new URI(u.getScheme(), u.getUserInfo(), u.getHost(), u
                    .getPort(), u.getPath(), query, u.getFragment());
        } catch (URISyntaxException e) {
            throw new BadParameterException("syntax error in query \"" + query + "\"", e);
        }
    }
    

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.url.URL#setQuery()
     */
    public void setQuery() throws BadParameterException {
        setQuery("");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.url.URL#getScheme()
     */
    public String getScheme() {
        String scheme = u.getScheme();
        if (scheme == null) {
            return "";
        }
        return scheme;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.url.URL#setScheme(java.lang.String)
     */
    public void setScheme(String scheme) throws BadParameterException {
        try {
            u = new URI(scheme, u.getUserInfo(), u.getHost(), u.getPort(), u
                    .getPath(), u.getQuery(), u.getFragment());
        } catch (URISyntaxException e) {
            throw new BadParameterException("syntax error in scheme \"" + scheme + "\"", e);
        }
    }
    

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.url.URL#setScheme()
     */
    public void setScheme() throws BadParameterException {
        setScheme("");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.url.URL#getUserInfo()
     */
    public String getUserInfo() {
        String userInfo = u.getUserInfo();
        if (userInfo ==  null) {
            return "";
        }
        return userInfo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.url.URL#setUserInfo(java.lang.String)
     */
    public void setUserInfo(String userInfo) throws BadParameterException {
        try {
            u = new URI(u.getScheme(), userInfo, u.getHost(), u.getPort(), u
                    .getPath(), u.getQuery(), u.getFragment());
        } catch (URISyntaxException e) {
            throw new BadParameterException("syntax error in userInfo \"" + userInfo + "\"", e);
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.url.URL#setUserInfo()
     */
    public void setUserInfo() throws BadParameterException {
        setUserInfo("");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.url.URL#translate(java.lang.String)
     */
    public URL translate(String scheme)
            throws BadParameterException, NoSuccessException {
        try {
            URI url = new URI(scheme, u.getUserInfo(), u.getHost(),
                    u.getPort(), u.getPath(), u.getQuery(), u.getFragment());
            // Not quite correct: the SAGA specs say that NoSuccess should be
            // thrown when the scheme is not supported. How to check this
            // here ???
            return new URLImpl(url);
        } catch (URISyntaxException e) {
            throw new BadParameterException("syntax error in scheme \"" + scheme + "\"", e);
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.url.URL#translate(Session, java.lang.String)
     */
    public URL translate(Session session, String scheme)
            throws BadParameterException, NoSuccessException {
	
	checkSessionType(session);
	
        return translate(scheme);
    }

    public int hashCode() {
        return u.hashCode();
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof URLImpl)) {
            return false;
        }
        URLImpl other = (URLImpl) o;
        return u.equals(other.u);
    }

    public String toString() {
        return u.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.url.URL#resolve(org.ogf.saga.url.URL)
     */
    public org.ogf.saga.url.URL resolve(URL url)
            throws NoSuccessException {
	
	checkURLType(url);
	
        String s = url.toString();
        URI uri;
        try {
            uri = new URI(s);
        } catch (URISyntaxException e) {
            throw new NoSuccessException("URL \"" + s
                    + "\" not recognized as a URL", e);
        }
        uri = u.resolve(uri);
        if (uri.toString().equals(s)) {
            return url;
        }

        return new URLImpl(uri);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.url.URL#isAbsolute()
     */
    public boolean isAbsolute() {
        return u.isAbsolute();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.url.URL#normalize()
     */
    public URLImpl normalize() {
        URI uri = u.normalize();
        if (uri == u) {
            return this;
        }
        return new URLImpl(uri);
    }

}
