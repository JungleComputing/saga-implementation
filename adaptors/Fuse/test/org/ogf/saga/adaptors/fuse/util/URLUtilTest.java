package org.ogf.saga.adaptors.fuse.util;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

public class URLUtilTest {

    @Test
    public void testIsLocalURL() throws SagaException, UnknownHostException {
        assertTrue(URLUtil.isLocal(URLFactory.createURL("foo.txt")));
        assertTrue(URLUtil.isLocal(URLFactory.createURL("/etc/passwd")));
        assertTrue(URLUtil.isLocal(URLFactory.createURL("file://localhost/")));
        assertTrue(URLUtil.isLocal(URLFactory.createURL("file://localhost/foo.txt")));
        assertTrue(URLUtil.isLocal(URLFactory.createURL("file:///foo.txt")));
        
        InetAddress localhost = InetAddress.getLocalHost();
        assertTrue(URLUtil.isLocal(URLFactory.createURL("file://" + localhost.getCanonicalHostName()))); 
        assertTrue(URLUtil.isLocal(URLFactory.createURL("file://" + localhost.getHostAddress()))); 
        assertTrue(URLUtil.isLocal(URLFactory.createURL("file://" + localhost.getHostName()))); 

        assertFalse(URLUtil.isLocal(URLFactory.createURL("file://example.com"))); 
        assertFalse(URLUtil.isLocal(URLFactory.createURL("ssh://localhost"))); 
    }

    @Test
    public void testIsRelative() throws SagaException {
        assertTrue(URLUtil.isRelative(URLFactory.createURL("foo.txt")));
        assertTrue(URLUtil.isRelative(URLFactory.createURL("dir/foo.txt")));
        assertTrue(URLUtil.isRelative(URLFactory.createURL("./test")));
        assertTrue(URLUtil.isRelative(URLFactory.createURL("../test")));
        
        assertFalse(URLUtil.isRelative(URLFactory.createURL("file://localhost/")));
        assertFalse(URLUtil.isRelative(URLFactory.createURL("file://localhost")));
        assertFalse(URLUtil.isRelative(URLFactory.createURL("file:///")));
    }

    @Test
    public void testIsSet() {
        assertTrue(URLUtil.isSet("test"));
        assertFalse(URLUtil.isSet("  "));
        assertFalse(URLUtil.isSet(""));
        assertFalse(URLUtil.isSet(null));
    }

    @Test
    public void testGetPathSafe() throws SagaException {
        {
            URL u = URLFactory.createURL("file://localhost");
            assertEquals("", u.getPath());
        }
        {
            URL u = URLFactory.createURL("file://localhost/etc/passwd");
            assertEquals("/etc/passwd", u.getPath());
        }
    }

    @Test
    public void testCreateURL() throws SagaException {
		URL[] bases = { URLFactory.createURL("dummy://localhost/"),
				URLFactory.createURL("dummy://localhost/tmp/") };
		URL[] rel = { URLFactory.createURL("foo"),
				URLFactory.createURL("dir/bar") };

		URL u1 = URLFactory.createURL("dummy://localhost");
		URL u2 = URLFactory.createURL("foo");
		URL result = URLUtil.createRelativeURL(u1, u2);
		System.err.println("result=" + result);
		
		for (URL base: bases) {
			for (URL rhs: rel) {
				URL relative = URLUtil.createRelativeURL(base, rhs);
				assertEquals(rhs.toString(), relative.toString());
			}
		}
    }

}
