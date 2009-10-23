package org.ogf.saga.apps.shell;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FlagsParser {

    public static final String FLAG_PREFIX = "-";
    
    private String booleanFlags;
    private Set<String> stringFlags;
    
    private Set<Character> parsedBooleans;
    private Map<String, String> parsedStrings;
    
    public FlagsParser(String[] stringFlags) {
        this(stringFlags, null);
    }
    
    public FlagsParser(String booleanFlags) {
        this(null, booleanFlags);
    }

    /**
     * Creates a flags parser that recognizes that given flags.
     * 
     * @param booleanFlags
     *            all allowed single-character flags that indicate a boolean
     *            value.
     * @param stringFlags
     *            all allowed string flags that should be followed by a string
     */
    public FlagsParser(String[] stringFlags, String booleanFlags) {
        this.booleanFlags = booleanFlags != null ? booleanFlags : "";
        
        this.stringFlags = new HashSet<String>();
        if (stringFlags != null) {
            this.stringFlags.addAll(Arrays.asList(stringFlags));
        }
                
        parsedStrings = new HashMap<String, String>();
        parsedBooleans = new HashSet<Character>();
    }
    
    /**
     * Parses the flag in the given shell arguments. The parsed results can be 
     * obtained via the getter methods. All flags must start with FLAG_PREFIX. 
     * Multiple single-character boolean flags can be combined after a single prefix.
     * String flags should be followed by a string value. Each flag (except the 
     * last one) is first interpreted as a String flag whose value is the next 
     * argument. If the String flag is not known or it is the last flag, it is 
     * parsed as a list of single-character boolean flags. A present boolean 
     * flag means 'true', non-present means 'false'. Parsing stops if an 
     * argument does not match any known flags, or matches a flag that has 
     * already been parsed before. This method returns the index of the last 
     * argument that has not been parsed.   
     * 
     * @param args
     *            the shell arguments to parse
     * @param offset
     *            the offset in the argument array to start parsing           
     */
    public int parse(String[] args, int offset) {
        parsedBooleans.clear();
        parsedStrings.clear();
        
        if (offset < args.length) {
            for (int i = offset; i < args.length; i++) {
                if (!args[i].startsWith(FLAG_PREFIX)) {
                    return i;
                }
            
                String arg = args[i].substring(1);

                if (arg.isEmpty()) {
                    return i;
                } else if (i < args.length - 1 && stringFlags.contains(arg)) {
                    // parse a string argument
                    if (parsedStrings.containsKey(arg)) {
                        return i;
                    } else {
                        parsedStrings.put(arg, args[++i]);
                    }
                } else {
                    // parse boolean argument(s)
                    for (int j = 0; j < arg.length(); j++) {
                        char flag = arg.charAt(j);
                        if (booleanFlags.indexOf(flag) >= 0) {
                            if (!parsedBooleans.add(flag)) {
                                return i;
                            }
                        }
                    }
                }
            }
        }
        
        return args.length;
    }
    
    public boolean getBooleanValue(char flag) {
        return parsedBooleans.contains(flag);
    }
    
    public String getStringValue(String flag) {
        return parsedStrings.get(flag);
    }
    
}
