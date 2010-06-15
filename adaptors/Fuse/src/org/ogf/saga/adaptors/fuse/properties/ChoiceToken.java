package org.ogf.saga.adaptors.fuse.properties;

import java.util.List;

import org.ogf.saga.url.URL;

public class ChoiceToken implements FuseToken {
    
    private List<FuseToken> left, right;
    
    ChoiceToken(List<FuseToken> left, List<FuseToken> right) {
        this.left = left;
        this.right = right;
    }

    public String parse(URL url) throws PropertyParseException {
        StringBuilder result = parseTokens(url, left);
        
        if (result == null) {
            result = parseTokens(url, right);
        }
        if (result == null) {
            String desc = createDescription(right);
            throw new PropertyParseException("Cannot parse: " + desc); 
        }
        return result.toString();
    }
    
    private static String createDescription(List<FuseToken> tokens) {
        StringBuilder b = new StringBuilder();
        for (FuseToken t: tokens) {
            b.append(t);
        }
        return b.toString();
    }
    
    private static StringBuilder parseTokens(URL url, List<FuseToken> tokens) 
    throws PropertyParseException
    {
        StringBuilder result = new StringBuilder();
                
        for (FuseToken token: tokens) {
            String s = token.parse(url);
            if (s == null) {
                return null;
            }
            result.append(s);
        }

        return result;
    }
    
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder('{');
        
        String concat = "";  
        for (FuseToken t: left) {
            b.append(concat);
            b.append(t.toString());
            concat = ",";
        }
        
        b.append("|");
        
        concat = "";
        for (FuseToken t: right) {
            b.append(concat);
            b.append(t.toString());
            concat = ",";
        }
        
        b.append('}');
        
        return b.toString();
    }
    
}
