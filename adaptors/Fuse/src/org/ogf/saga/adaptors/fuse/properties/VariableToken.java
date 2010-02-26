package org.ogf.saga.adaptors.fuse.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.ogf.saga.context.Context;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.url.URL;

public class VariableToken implements FuseToken {

    private static Map<String, String> VAR_PROP_MAP = new HashMap<String, String>();
    static {
        VAR_PROP_MAP.put(FusePropertyParser.VAR_JAVA_FILESEP, "file.separator");
        VAR_PROP_MAP.put(FusePropertyParser.VAR_JAVA_TMPDIR, "java.io.tmpdir");
        VAR_PROP_MAP.put(FusePropertyParser.VAR_JAVA_USERDIR, "user.dir");
        VAR_PROP_MAP.put(FusePropertyParser.VAR_JAVA_USERHOME, "user.home");
        VAR_PROP_MAP.put(FusePropertyParser.VAR_JAVA_USERNAME, "user.name");
    }

    private static Map<String, String> VAR_CONTEXT_ATTR_MAP = new HashMap<String, String>();
    static {
        VAR_CONTEXT_ATTR_MAP.put(FusePropertyParser.VAR_CONTEXT_USERCERT, Context.USERCERT);
        VAR_CONTEXT_ATTR_MAP.put(FusePropertyParser.VAR_CONTEXT_USERID, Context.USERID);
        VAR_CONTEXT_ATTR_MAP.put(FusePropertyParser.VAR_CONTEXT_USERKEY, Context.USERKEY);
        VAR_CONTEXT_ATTR_MAP.put(FusePropertyParser.VAR_CONTEXT_USERPASS, Context.USERPASS);
    }

    private static final Random random = new Random();
    
    private String var;
    private boolean allowUnset;
    private boolean blank;
    private String value = null;
    
    VariableToken(String var, boolean allowUnset, boolean blank, String fs, 
    		String mountpoint, Context context) throws PropertyParseException {
        this.var = var;
        this.allowUnset = allowUnset;
        this.blank = blank;
        
        if (VAR_PROP_MAP.containsKey(var)) {
            String javaProp = VAR_PROP_MAP.get(var);
            value = nullIfEmpty(System.getProperty(javaProp));
        } else if (VAR_CONTEXT_ATTR_MAP.containsKey(var)) {
            if (context == null) {
                if (allowUnset) {
                    value = null;
                } else {
                    throw new PropertyParseException(
                            "Missing context for variable: " + var);
                }
            } else {
                String contextAttr = VAR_CONTEXT_ATTR_MAP.get(var);
                try {
                    value = nullIfEmpty(context.getAttribute(contextAttr));
                } catch (SagaException e) {
                    if (allowUnset) {
                        value = null;
                    } else {
                        throw new PropertyParseException(
                                "Unset context attribute used: " + contextAttr, e);
                    }
                }
            }
        } else if (FusePropertyParser.VAR_FS.equals(var)) {
            fs = nullIfEmpty(fs);
        	if (fs == null && !allowUnset) {
                throw new PropertyParseException("Unset variable used: " + 
                        FusePropertyParser.VAR_FS);
            }
            value = fs;
        } else if (FusePropertyParser.VAR_MOUNTPOINT.equals(var)) {
            mountpoint = nullIfEmpty(mountpoint);
        	if (mountpoint == null && !allowUnset) {
                throw new PropertyParseException("Unset variable used: " + 
                        FusePropertyParser.VAR_MOUNTPOINT);
            }
            value = mountpoint;
        } else {
            // variable can only be discovered in parse() (e.g. a URL part)
        }
        
        if (blank && value != null) {
        	// static variable that should be blanked
        	value = "";
        }
    }
    
    @Override
    public String parse(URL url) throws PropertyParseException {
    	if (value != null) {
            // static variable
    		return value;
        }
    	        
        // variable
    	String result = null;
    	
        if (url != null) {
            if (FusePropertyParser.VAR_URL_SCHEME.equals(var)) {
                result = nullIfEmpty(url.getScheme());
            } else if (FusePropertyParser.VAR_URL_USERINFO.equals(var)) {
                result = nullIfEmpty(url.getUserInfo());
            } else if (FusePropertyParser.VAR_URL_HOST.equals(var)) {
                result = nullIfEmpty(url.getHost());
            } else if (FusePropertyParser.VAR_URL_PORT.equals(var)) {
                result = nullIfNegative(url.getPort());
            } else if (FusePropertyParser.VAR_URL_PATH.equals(var)) {
                result = nullIfEmpty(url.getPath());
            } else if (FusePropertyParser.VAR_URL_FRAGMENT.equals(var)) {
                result = nullIfEmpty(url.getFragment());
            }
        }
        if (result == null && FusePropertyParser.VAR_JAVA_RANDOM.equals(var)) {
            long l = random.nextLong();
            result = Long.toString(l);
        }
        if (result == null) {
        	if (allowUnset) {
        		return null;
        	} else {
				throw new PropertyParseException(
						"Cannot resolve required variable: " + var);
        	}
    	}
        if (blank) {
        	return "";
        }
        return result;
    }
    
    private static String nullIfEmpty(String s) {
        if (s == null || s.isEmpty()) {
        	return null;
        } else {
        	return s;
        }
    }

    private static String nullIfNegative(int i) 
    throws PropertyParseException {
        return i < 0 ? null : Integer.toString(i);
    }
    
    @Override
    public String toString() {
        return var;
    }
    
}
