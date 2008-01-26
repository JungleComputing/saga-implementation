package org.ogf.saga.proxies.file;

import org.ogf.saga.error.BadParameter;

/**
 * This class provides conversion from String to a FALLS pattern.
 * FALLS stands for "FAmiLy of Line Segments". A FALLS pattern
 * consists of a 5-tuple "(from,to,stride,rep,(pat))" where the
 * ",(pat)" part is optional and stands for a nested FALLS pattern.
 */
public class Falls {
    
    private int from;
    private int to;
    private int stride;
    private int rep;
    private Falls nested = null;
    
    private Tokenizer tokenizer;
    
    private static class Tokenizer {
        public static final int COMMA = 0;
        public static final int LEFTPAR = 1;
        public static final int RIGHTPAR = 2;
        public static final int INTEGER = 3;
        public static final int EOS = 4;
        public static final int UNKNOWN = 5;
        
        int index = 0;
        int tokval;
        char[] chars;
        
        public Tokenizer(String s) {
            chars = s.toCharArray();
        }
        
        public int nextToken() {
            if (index >= chars.length) {
                return EOS;
            }
            if (Character.isDigit(chars[index])) {
                int val = 0;
                while (index < chars.length && Character.isDigit(chars[index])) {
                    val = val * 10 + Character.digit(chars[index], 10);
                    index++;
                }
                tokval = val;
                return INTEGER;  
            }
            switch(chars[index++]) {
            case '(':
                return LEFTPAR;
            case ')':
                return RIGHTPAR;
            case ',':
                return COMMA;
            }
            return UNKNOWN;
        }
    }
   
    /**
     * Creates the FALLS pattern information from the specified string.
     * @param s the FALLS pattern as a string.
     * @exception BadParameter when the string is not recognized as a FALLS
     *  pattern.
     */
    public Falls(String s) throws BadParameter {
        tokenizer = new Tokenizer(s);
        read();
        int tok = tokenizer.nextToken();
        if (tok != Tokenizer.EOS) {
            throw new BadParameter("Garbage at end of FALLS pattern");
        }
        check();
        tokenizer = null;
    }
    
    private Falls(Tokenizer t) throws BadParameter {
        tokenizer = t;
        read();
        check();
        tokenizer = null;
    }
    
    private void check() throws BadParameter {
        if (to < from) {
            throw new BadParameter("to < from in FALLS pattern");
        }
        if (stride < (to - from + 1)) {
            throw new BadParameter("stride too small for specified"
                    + " to and from in FALLS pattern");
        }
        if (rep == 0) {
            throw new BadParameter("rep = 0 in FALLS pattern");
        }
    }
    
    private void getComma() throws BadParameter {
        if (tokenizer.nextToken() != Tokenizer.COMMA) {
            throw new BadParameter("Comma expected in FALLS pattern");
        } 
    }

    private int getInt() throws BadParameter {
        if (tokenizer.nextToken() != Tokenizer.INTEGER) {
            throw new BadParameter("Integer expected in FALLS pattern");
        }
        return tokenizer.tokval;
    }
    
    private void read() throws BadParameter {
        int tok = tokenizer.nextToken();
        if (tok != Tokenizer.LEFTPAR) {
            throw new BadParameter("FALLS pattern should start with '('");
        }
        from = getInt();
        getComma();
        to = getInt();
        getComma();
        stride = getInt();
        getComma();
        rep = getInt();
        tok = tokenizer.nextToken();
        if (tok == Tokenizer.COMMA) {
            nested = new Falls(tokenizer);
            tok = tokenizer.nextToken();
        }
        if (tok != Tokenizer.RIGHTPAR) {
            throw new BadParameter("')' expected in FALLS pattern");
        }
    }
    
    /**
     * Returns the start offset of the first repetition
     * of this FALLS pattern.
     * @return the start offset.
     */
    public int getFrom() {
        return from;
    }
    
    /**
     * Returns the finishing offset of the first repetition
     * of this FALLS pattern.
     * @return the finishing offset.
     */
    public int getTo() {
        return to;
    }
    
    /**
     * Returns the stride of this FALLS pattern.
     * @return the stride.
     */
    public int getStride() {
        return stride;
    }
    
    /**
     * Returns the number of repetitions of this FALLS pattern.
     * @return the number of repetitions.
     */
    public int getRep() {
        return rep;
    }
    
    /**
     * Returns the nested FALLS pattern, or <code>null</code>.
     * @return the nested FALLS pattern, or <code>null</code>.
     */
    public Falls getNested() {
        return nested;
    }
    
    /**
     * Returns the size of the buffer required for this FALLS pattern.
     * The only things that matter here is the number of repetitions
     * and the size of the nested pattern.
     * @return the required size.
     */
    public int getSize() {
        return rep * (nested != null ? nested.getSize() : 1);
    }
}
