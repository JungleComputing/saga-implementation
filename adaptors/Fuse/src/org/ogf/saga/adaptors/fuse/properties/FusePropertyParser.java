package org.ogf.saga.adaptors.fuse.properties;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.ogf.saga.context.Context;
import org.ogf.saga.url.URL;

public class FusePropertyParser {

    static final char CHOICE_PREFIX = '{';
    static final char CHOICE_SIMPLE_PREFIX = '[';
    static final char CHOICE_SUFFIX = '}';
    static final char CHOICE_SIMPLE_SUFFIX = ']';
    
    static final HashSet<String> VARIABLES = new HashSet<String>(); 
    static final char VAR_PREFIX = '%';
    
    static final String VAR_JAVA_FILESEP = newVar("java_filesep");
    static final String VAR_JAVA_RANDOM = newVar("java_random");
    public static final String VAR_JAVA_TMPDIR = newVar("java_tmpdir");
    static final String VAR_JAVA_USERDIR = newVar("java_userdir");
    static final String VAR_JAVA_USERHOME = newVar("java_userhome");
    static final String VAR_JAVA_USERNAME = newVar("java_username");
    static final String VAR_FS = newVar("fs");
    static final String VAR_MOUNTPOINT = newVar("mount_point");
    static final String VAR_URL_USERINFO = newVar("url_userinfo");
    static final String VAR_URL_HOST = newVar("url_host");
    static final String VAR_URL_PORT = newVar("url_port");
    static final String VAR_URL_PATH = newVar("url_path");
    static final String VAR_URL_SCHEME = newVar("url_scheme");
    static final String VAR_URL_FRAGMENT = newVar("url_fragment");
    static final String VAR_CONTEXT_USERID = newVar("context_userid");
    static final String VAR_CONTEXT_USERPASS = newVar("context_userpass");
    static final String VAR_CONTEXT_USERKEY = newVar("context_userkey");
    static final String VAR_CONTEXT_USERCERT = newVar("context_usercert");
    
	static final String NOT_PLAIN = "" + VAR_PREFIX + CHOICE_PREFIX
			+ CHOICE_SUFFIX;

    private static final String newVar(String name) {
        String s = VAR_PREFIX + name;
        VARIABLES.add(s);
        return s;
    }
        
    private List<FuseToken> tokens;
    
    public FusePropertyParser(String prop, String fs, 
            String mountpoint, Context c)
    throws PropertyParseException 
    {
        tokens = tokenize(fs, mountpoint, c, prop, false);
    }
    
    private static List<FuseToken> tokenize(String fs, String mountpoint, 
            Context c, String s, boolean allowUnsetVariables) 
            throws PropertyParseException {
        List<FuseToken> result = new LinkedList<FuseToken>();

        s = s.replaceAll("\\" + CHOICE_SIMPLE_PREFIX, "" + CHOICE_PREFIX);
        s = s.replaceAll("\\" + CHOICE_SIMPLE_SUFFIX, "|" + CHOICE_SUFFIX);

        int pos = 0;
            
        while (pos < s.length()) {
        	char pivot = s.charAt(pos);
            if (pivot == VAR_PREFIX) {
            	boolean blank = false;
            	if (pos < (s.length() - 1) && s.charAt(pos + 1) == VAR_PREFIX) {
            		blank = true;
            		pos++;
            	}
                String var = readVariable(s, pos);
                VariableToken varToken = new VariableToken(var, 
                        allowUnsetVariables, blank, fs, mountpoint, c);
                result.add(varToken);
                pos += var.length();
            } else if (pivot == CHOICE_PREFIX) {
            	String choice = readChoice(s, pos, CHOICE_SUFFIX);
                String leftRight = choice.substring(1, choice.length() - 1);
                String[] parts = leftRight.split("\\|", -1);
                if (parts.length != 2) {
					throw new PropertyParseException(choice + " contains "
							+ parts.length + " parts");
                }
				List<FuseToken> l = tokenize(fs, mountpoint, c, parts[0], true);
				List<FuseToken> r = tokenize(fs, mountpoint, c, parts[1], true);
                ChoiceToken choiceToken = new ChoiceToken(l, r);
                result.add(choiceToken);
                pos += choice.length();
            } else {
                String plain = readPlain(s, pos);
                PlainToken plainToken = new PlainToken(plain);
                result.add(plainToken);
                pos += plain.length();
            }
        }
        
        return result;
    }

    private static String readVariable(String s, int pos) 
    throws PropertyParseException {
        String var = "";
        
        for (int i = pos; i < s.length(); i++) {
            var += s.charAt(i);
            if (VARIABLES.contains(var)) {
                return var;
            }
        }
            
        throw new PropertyParseException("Unknown variable: " + var);
    }
    
    private static String readChoice(String s, int pos, char suffix)
    throws PropertyParseException {
        int end = s.indexOf(suffix, pos + 1);
        if (end < 0) {
            throw new PropertyParseException("Missing '" + suffix + "'");
        }
        return s.substring(pos, end + 1);
    }
    
    private static String readPlain(String s, int pos) {
        int i = pos;
        while(i < s.length() && NOT_PLAIN.indexOf(s.charAt(i)) < 0) {
            i++;
        }
        
        return s.substring(pos, i);
    }
    
    public String parse(URL url) throws PropertyParseException {
        StringBuilder b = new StringBuilder();
        
        for (FuseToken t: tokens) {
            String s = t.parse(url);
            if (s == null) {
                throw new PropertyParseException("Error while parsing: " + t);
            }
            b.append(s);
        }
                
        return b.toString();
    }
        
}
