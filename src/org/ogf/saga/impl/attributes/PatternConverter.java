package org.ogf.saga.impl.attributes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.log4j.Logger;
import org.ogf.saga.error.BadParameter;

public class PatternConverter {
    
    private String wildcard;
    private int    wildcardLen;
    protected Pattern pattern;
    protected boolean hasWildcard = false;
    private static Logger logger = Logger.getLogger(PatternConverter.class);
    
    // Translate wildcards to Java regex.
    private String wildcardToRegex() {
        StringBuffer s = new StringBuffer(wildcardLen);
        int index = 0;

        while (index < wildcardLen) {
            char c = wildcard.charAt(index);
            switch(c) {
            case '*':
                hasWildcard = true;
                s.append(".*");
                break;
            case '?':
                hasWildcard = true;
                s.append(".");
                break;
            // escape special regexp-characters
            case '(': case ')': case '$':
            case '^': case '.': case '|':
            case '\\':
                s.append("\\");
                s.append(c);
                break;
            case '[':
                hasWildcard = true;
                index = handleSquare(s, index+1);
                break;
            case '{':
                // Should we do this? SAGA sais yes, POSIX sais no.
                hasWildcard = true;
                index = handleCurly(s,  index+1);
                break;
            default:
                s.append(c);
                break;
            }
            index++;
        }

        return s.toString();
    }

    private int handleSquare(StringBuffer s, int index) {
        
        StringBuffer s1 = new StringBuffer();

        int i = index;

        if (i < wildcardLen) {
            char c1 = wildcard.charAt(i);
            if (c1 == '!' || c1 == '^') {
                s1.append("[^");
                i++;
            } else {
                s1.append("[");
            }
        }

        while (i < wildcardLen) {
            char c = wildcard.charAt(i);
            switch(c) {
            case '[':
            case '&':
            case '\\':
                s1.append("\\");
                s1.append(c);
                break;
            case '/':
                // POSIX exception: it was not a sq construction after all.
                s.append("\\");
                s.append("[");
                return index;
            case ']':
                s1.append(c);
                s.append(s1);
                return i;
            default:
                s1.append(c);
                break;
            }
            i++;
        }
        throw new Error("Unmatched '['");
    }


    private int handleCurly(StringBuffer s, int index) {
        
        int i = index;

        s.append("(");

        while (i < wildcardLen) {
            char c = wildcard.charAt(i);
            switch(c) {
            case '[':
                i = handleSquare(s, i);
                break;
            case '{':
                i = handleCurly(s, i);
                break;
            case '*':
                s.append(".*");
                break;
            case '?':
                s.append(".");
                break;
            // escape special regexp-characters
            case '(': case ')': case '$':
            case '^': case '.': case '|':
            case '\\': case ']':
                s.append("\\");
                s.append(c);
                break;
            case '}':
                s.append(")");
                return i;
            case ',':
                s.append("|");
                break;
            default:
                s.append(c);
                break;
            }
            i++;
        }
        throw new Error("Unmatched {");
    }

    protected PatternConverter(String wildcard) throws BadParameter {
        this.wildcard = wildcard;
        this.wildcardLen = wildcard.length();
        try {
            String regexPattern = this.wildcardToRegex();
            if (logger.isDebugEnabled()) {
                logger.debug("wildcard \"" + wildcard + "\" converted to regex \"" + regexPattern + "\"");
            }
            pattern = Pattern.compile(regexPattern);
        } catch(PatternSyntaxException e) {
            throw new BadParameter("Conversion to regex error", e);
        } catch(Throwable e) {
            throw new BadParameter("Illegal wildcard expression", e);
        }
    }
 
    public boolean matches(String s) {
        Matcher matcher = pattern.matcher(s);
        return matcher.matches();
    }
}
