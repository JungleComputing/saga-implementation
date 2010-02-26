package org.ogf.saga.adaptors.fuse.properties;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ogf.saga.context.Context;
import org.ogf.saga.context.ContextFactory;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

public class FusePropertyParserTest {

    private static final String MOUNTPOINT = "/tmp";
    private static final String FS = "testfs";
    private static final String URL_USERINFO = "mathijs";
    private static final String URL_HOST = "host.com";
    private static final String URL_PORT = "1234";
    private static final String URL_PATH = "/etc/passwd";
    private static final String URL_FRAGMENT = "frag";
    private static final String CONTEXT_TYPE = "ssh";
    private static final String CONTEXT_USERID = "john";
    private static final String CONTEXT_USERPASS = "secret";
    private static final String CONTEXT_USERKEY = "/home/john/.ssh/id_rsa";
    private static final String CONTEXT_USERCERT = "/home/john/.globus/user.crt";
        
    private URL url;
    private Context context; 
    
    @BeforeClass
    public static void init() {
        // use the main log4j.properties file in the SAGA source tree  
        System.setProperty("log4j.configuration", "file:log4j.properties");
    }
    
    @Before
    public void setUp() throws SagaException {
        url = URLFactory.createURL(FS + "://" + URL_USERINFO + "@" + URL_HOST + 
                ":" + URL_PORT + URL_PATH + "#" + URL_FRAGMENT);
        context = ContextFactory.createContext(CONTEXT_TYPE);
        context.setAttribute(Context.USERID, CONTEXT_USERID);
        context.setAttribute(Context.USERPASS, CONTEXT_USERPASS);
        context.setAttribute(Context.USERKEY, CONTEXT_USERKEY);
        context.setAttribute(Context.USERCERT, CONTEXT_USERCERT);
    }
    
    @Test
    public void testFusePropertyParser() throws Exception {
        new FusePropertyParser("echo", FS, MOUNTPOINT, null);
    }

    @Test(expected=PropertyParseException.class)
    public void testFusePropertyParserNoFS() throws Exception {
        new FusePropertyParser("echo " + FusePropertyParser.VAR_FS, null, null, 
                null);
    }

    @Test(expected=PropertyParseException.class)
    public void testFusePropertyParserNoMountpoint() 
    throws PropertyParseException 
    {
        new FusePropertyParser("echo " + FusePropertyParser.VAR_MOUNTPOINT, FS, null,
                null);
    }

    @Test
    public void testParsePlain() throws PropertyParseException {
        FusePropertyParser p = new FusePropertyParser("echo", FS, MOUNTPOINT, null);
        assertEquals("echo", p.parse(url));
    }

    @Test
    public void testParseVariables() throws PropertyParseException {
        Map<String, String> parsed = new HashMap<String, String>();
        parsed.put(FusePropertyParser.VAR_CONTEXT_USERCERT, CONTEXT_USERCERT);
        parsed.put(FusePropertyParser.VAR_CONTEXT_USERID, CONTEXT_USERID);
        parsed.put(FusePropertyParser.VAR_CONTEXT_USERKEY, CONTEXT_USERKEY);
        parsed.put(FusePropertyParser.VAR_CONTEXT_USERPASS, CONTEXT_USERPASS);
        parsed.put(FusePropertyParser.VAR_FS, FS);
        parsed.put(FusePropertyParser.VAR_JAVA_FILESEP, System.getProperty("file.separator"));
        parsed.put(FusePropertyParser.VAR_JAVA_TMPDIR, System.getProperty("java.io.tmpdir"));
        parsed.put(FusePropertyParser.VAR_JAVA_USERDIR, System.getProperty("user.dir"));
        parsed.put(FusePropertyParser.VAR_JAVA_USERHOME, System.getProperty("user.home"));
        parsed.put(FusePropertyParser.VAR_JAVA_USERNAME, System.getProperty("user.name"));
        parsed.put(FusePropertyParser.VAR_MOUNTPOINT, MOUNTPOINT);
        parsed.put(FusePropertyParser.VAR_URL_FRAGMENT, URL_FRAGMENT);
        parsed.put(FusePropertyParser.VAR_URL_HOST, URL_HOST);
        parsed.put(FusePropertyParser.VAR_URL_PATH, URL_PATH);
        parsed.put(FusePropertyParser.VAR_URL_PORT, URL_PORT);
        parsed.put(FusePropertyParser.VAR_URL_SCHEME, FS);
        parsed.put(FusePropertyParser.VAR_URL_USERINFO, URL_USERINFO);
        
        Set<String> variables = new HashSet<String>(FusePropertyParser.VARIABLES);
        variables.remove(FusePropertyParser.VAR_JAVA_RANDOM);
        for (String var: variables) {
            FusePropertyParser p = new FusePropertyParser(var, FS, MOUNTPOINT, context);
            String result = parsed.get(var); 
            assertEquals(result, p.parse(url));
        }
        
        FusePropertyParser p = new FusePropertyParser(
                FusePropertyParser.VAR_JAVA_RANDOM, FS, MOUNTPOINT, context);
        String l1 = p.parse(url);
        String l2 = p.parse(url);
        assertNotSame(l1, l2);
    }
    
    @Test
    public void testParseBlankVariable() throws PropertyParseException {
    	FusePropertyParser p = new FusePropertyParser("A%%url_hostB", FS, null,
                null);
    	assertEquals("AB", p.parse(url));
    }
    
    @Test(expected=PropertyParseException.class)
    public void testMissingContext() throws PropertyParseException {
        new FusePropertyParser("%context_userid", FS, MOUNTPOINT, null);
    }
    
    @Test
    public void testParseChoiceCurly() throws PropertyParseException, SagaException {
        FusePropertyParser p = new FusePropertyParser("{%url_port|22}", FS, MOUNTPOINT,
                    context);
        
        // choose the left part
        assertEquals(URL_PORT, p.parse(url));
        
        // choose the right part
        URL withoutPort = URLFactory.createURL("ssh://localhost");
        assertEquals("22", p.parse(withoutPort));
    }

    @Test(expected=PropertyParseException.class)
    public void testParseChoiceCurlyEmpty() throws PropertyParseException {
        new FusePropertyParser("{}", FS, MOUNTPOINT, context);
    }
    
    @Test(expected=PropertyParseException.class)
    public void testParseChoiceCurlyNotEnough() throws PropertyParseException {
        new FusePropertyParser("{aap}", FS, MOUNTPOINT, context);
    }

    @Test(expected=PropertyParseException.class)
    public void testParseChoiceCurlyTooMany() throws PropertyParseException {
        new FusePropertyParser("{aap|noot|mies}", FS, MOUNTPOINT, context);
    }

    @Test
    public void testParseChoiceUnsetVariable() 
    throws PropertyParseException, SagaException 
    {
        URL u = URLFactory.createURL("/etc/passwd");
        FusePropertyParser p = new FusePropertyParser("{%url_host|%url_scheme}", FS, MOUNTPOINT, 
                null);
        try {
            p.parse(u);
            fail("Expected " + PropertyParseException.class);
        } catch (PropertyParseException expected) {
            // ignore
        }
    }

    @Test
    public void testParseChoiceBrackets() throws PropertyParseException, SagaException {
        FusePropertyParser p = new FusePropertyParser("[-host %url_host]", FS, MOUNTPOINT,
                    context);
        
        // choose the left part
        assertEquals("-host " + URL_HOST, p.parse(url));
        
        // choose the right part
        URL withoutHost = URLFactory.createURL("/etc/passwd");
        assertEquals("", p.parse(withoutHost));
    }

    @Test
    public void testParseChoiceBracketsOptionalContext() 
    throws PropertyParseException, SagaException 
    {
        FusePropertyParser p = new FusePropertyParser("test[%context_userid]", FS, MOUNTPOINT,
                    null);
        assertEquals("test", p.parse(url));
    }

    @Test
    public void testParseChoiceBracketsOptionalMountpoint() 
    throws PropertyParseException, SagaException 
    {
        FusePropertyParser p = new FusePropertyParser("test[%mount_point]", FS, null,
                    null);
        assertEquals("test", p.parse(url));
    }

    @Test
    public void testComplexMountpoint() throws PropertyParseException, SagaException {
        FusePropertyParser p = new FusePropertyParser("%java_userhome%java_filesep.javasaga%java_filesepfuseadaptor" +
        "%java_filesep%fs_[%url_userinfo@]%url_host[:%url_port]", FS, null, 
                context);
                
        String userHome = System.getProperty("user.home");
        String fileSep = System.getProperty("file.separator");
        String expected =  userHome + fileSep + ".javasaga" + fileSep + 
                "fuseadaptor" + fileSep + FS + "_" + URL_USERINFO + "@" + URL_HOST + 
                ":" + URL_PORT;
        assertEquals(expected, p.parse(url));
    }

    @Test
    public void testChoiceCurlyBlankVariables() throws PropertyParseException, SagaException {
    	FusePropertyParser p = new FusePropertyParser("{%%url_portA|B}", FS, MOUNTPOINT, context);
    
    	// choose the left part
    	assertEquals("A", p.parse(url));
    
    	// choose the right part
    	URL withoutPort = URLFactory.createURL("file://localhost/");
    	assertEquals("B", p.parse(withoutPort));
    }
    
    @Test
    public void testChoiceCurlyBlankVariablesContext() throws PropertyParseException, SagaException {
    	FusePropertyParser p1 = new FusePropertyParser("{%%context_userpassA|B}", FS, MOUNTPOINT, context);
    	FusePropertyParser p2 = new FusePropertyParser("{%%context_userpassA|B}", FS, MOUNTPOINT, null);
    
    	// choose the left part
    	assertEquals("A", p1.parse(url));
    
    	// choose the right part
    	URL withoutPort = URLFactory.createURL("file://localhost/");
    	assertEquals("B", p2.parse(withoutPort));
    }

    @Test
    public void testXtreemFSMountCommand() throws PropertyParseException, SagaException {
        String mountCmd = "xtfs_mount %url_host:{%url_port|32638}/%url_userinfo " +
            "%mount_point --timeout-ms=10000[ --cert %context_usercert]" +
            "[ --pkey %context_userkey][ --pass %context_userpass]";

        // check full command with all options enabled
        FusePropertyParser p = new FusePropertyParser(mountCmd, FS, MOUNTPOINT, 
                context); 
        String expected = "xtfs_mount " + URL_HOST + ":" + URL_PORT + "/" + 
                URL_USERINFO + " " + MOUNTPOINT + " --timeout-ms=10000 --cert " +
                CONTEXT_USERCERT + " --pkey " + CONTEXT_USERKEY + " --pass " + 
                CONTEXT_USERPASS;
        assertEquals(expected, p.parse(url));
        
        // check default mount command with all default values
        p = new FusePropertyParser(mountCmd, FS, MOUNTPOINT, null);
        URL basic = URLFactory.createURL("xtreemfs://vol42@localhost/");
        expected = "xtfs_mount localhost:32638/vol42 " + MOUNTPOINT + 
        		" --timeout-ms=10000";
        assertEquals(expected, p.parse(basic));
    }

    @Test
    public void testSshfsMountCommand() throws PropertyParseException, SagaException {
        String mountCmd = "sshfs [%context_userid@]%url_host:/ %mount_point[ -p %url_port] -o transform_symlinks {%%context_userpass-o password_stdin|-o NumberOfPasswordPrompts=0}[ -o IdentityFile=%context_userkey]";

        // check full command with all options enabled
        FusePropertyParser p = new FusePropertyParser(mountCmd, FS, MOUNTPOINT, 
                context); 
        String expected = "sshfs " + CONTEXT_USERID + "@" + URL_HOST + ":/ " +
                MOUNTPOINT + " -p " + URL_PORT + " -o transform_symlinks -o password_stdin -o IdentityFile=/home/john/.ssh/id_rsa";
        assertEquals(expected, p.parse(url));
        
        // check default mount command with all default values
        p = new FusePropertyParser(mountCmd, FS, MOUNTPOINT, null);
        URL basic = URLFactory.createURL("ssh://localhost/");
        expected = "sshfs localhost:/ " + MOUNTPOINT + " -o transform_symlinks -o NumberOfPasswordPrompts=0";
        assertEquals(expected, p.parse(basic));
    }

}
