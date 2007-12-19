package org.ogf.saga.impl.attributes;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.ogf.saga.error.AuthenticationFailed;
import org.ogf.saga.error.AuthorizationFailed;
import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.IncorrectState;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.PermissionDenied;
import org.ogf.saga.error.Timeout;

// Questions:
// - what exactly should listAttributes list? All attributes that have a value?
//   Or just all supported attributes? Or all implemented attributes?

/**
 * This is the base class of all attributes in this SAGA implementation.
 */
public class Attributes implements org.ogf.saga.attributes.Attributes, Cloneable {
    
    // This format is not quite correct: the date should be space-padded,
    // not zero-padded. Cannot express this, though, so this is fixed, in the
    // dateFormat() method.
    private final SimpleDateFormat dateFormatter
        = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
    
    // Information about attributes: name, type, value,
    // read/write, removable or not, implemented. 
    private static class AttributeInfo implements Cloneable {
        final String name;
        final AttributeType type;
        String value;
        String[] vectorValue;
        final boolean vector;
        final boolean readOnly;
        final boolean removable;
        final boolean notImplemented;
        
        public AttributeInfo(String name, AttributeType type, boolean vector,
                boolean readOnly, boolean notImplemented, boolean removable) {
            this.name = name;
            this.type = type;
            this.vector = vector;
            this.readOnly = readOnly;
            this.notImplemented = notImplemented;
            this.removable = removable;
            this.value = "";
            this.vectorValue = new String[0];
        }
        
        public int hashCode() {
            return name.hashCode();
        }
        
        public boolean equals(Object o) {
            if (o == null || ! (o instanceof AttributeInfo)) {
                return false;
            }
            AttributeInfo info = (AttributeInfo) o;
            if (! info.name.equals(name) || info.type != type) {
                return false;
            }
            if (info.notImplemented != notImplemented
                    || info.removable != removable
                    || info.vector != vector
                    || info.readOnly != readOnly) {
                return false;
            }

            if (! vector) {
                return info.value.equals(value);
            }
            if (info.vectorValue.length != vectorValue.length) {
                return false;
            }
            for (int i = 0; i < vectorValue.length; i++) {
                if (! info.vectorValue[i].equals(vectorValue[i])) {
                    return false;
                }
            }
            return true;
        }
        
        protected Object clone() throws CloneNotSupportedException {
            AttributeInfo clone = (AttributeInfo) super.clone();
            clone.vectorValue = vectorValue.clone();
            return clone;
        }
    }
    
    private HashMap<String, AttributeInfo> attributes;
    
    private boolean autoAdd;

    public Attributes(boolean autoAdd) {
        attributes = new HashMap<String, AttributeInfo>();
        dateFormatter.setLenient(false);
        this.autoAdd = autoAdd;
    }
    
    public Attributes() {
        this(false);
    }
    
    public Object clone() throws CloneNotSupportedException {
        Attributes clone = (Attributes) super.clone();
        clone.attributes = new HashMap<String, AttributeInfo>();
        for (String s : attributes.keySet()) {
            clone.attributes.put(s, (AttributeInfo) attributes.get(s).clone());
        }
        return clone;
    }
    
    public int hashCode() {
        return attributes.hashCode();
    }
    
    public boolean equals(Object o) {
        if (o == null || ! (o instanceof Attributes)) {
            return false;
        }
        Attributes a = (Attributes) o;
        if (a.autoAdd != autoAdd) {
            return false;
        }
        return attributes.equals(a.attributes);
    }
    
    // Stores information about a specific attribute.
    protected synchronized void addAttribute(String name, AttributeType type, boolean vector,
            boolean readOnly, boolean notImplemented, boolean removeable) {
        attributes.put(name,
            new AttributeInfo(name, type, vector, readOnly, notImplemented, removeable));
    }
    
    // split pattern up into key-part and value-part.
    private String[] splitPattern(String pattern) {
        int index = pattern.indexOf('=');
        if (index == -1) {
            return new String[] { pattern };
        }
        if (index == 0) {
            return new String[] {"", pattern.substring(1)};
        }
        
        while (pattern.charAt(index-1) == '\\') {
            // It may be escaped. Find out.
            boolean escaped = false;
            for (int i = 0; i < index; i++) {
                if (pattern.charAt(i) == '\\') {
                    // next character is escaped, unless the backslash 
                    // itself was escaped.
                    escaped = ! escaped;
                } else {
                    // next character is certainly not escaped.
                    escaped = false;
                }
            }
            if (! escaped) {
                break;
            }
            
            // Now, we know that it was escaped. Remove the backslash.
            pattern = pattern.substring(0, index-1) + pattern.substring(index);
            index = pattern.indexOf('=', index);
            if (index == -1) {
                return new String[] { pattern };
            }
        }
        if (index == pattern.length()-1) {
            return new String[] { pattern.substring(0, index) };
        }
        return new String[] { pattern.substring(0, index), pattern.substring(index + 1) };
    }

    public String[] findAttributes(String... patterns) throws NotImplemented,
            BadParameter, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, Timeout, NoSuccess {
        PatternConverter[][] matchers = new PatternConverter[patterns.length][];
        
        for (int i = 0; i < patterns.length; i++) {
            String[] split = splitPattern(patterns[i]);
            matchers[i] = new PatternConverter[split.length];
            for (int j = 0; j < split.length; j++) {
                matchers[i][j] = new PatternConverter(split[j]);
            }
        }
        
        ArrayList<String> result = new ArrayList<String>();
        
        for (String key : attributes.keySet()) {
            for (int i = 0; i < matchers.length; i++) {
                boolean matching = false;
                if (matchers[i][0].matches(key)) {
                    if (matchers[i].length != 1) {
                        AttributeInfo info = attributes.get(key);
                        if (! info.vector && matchers[i][1].matches(info.value)) {
                            matching = true;
                        }
                    } else {
                        matching = true;
                    }
                }
                if (matching) {
                    result.add(key);
                    break;
                }
            }
        }
        
        return result.toArray(new String[result.size()]);
    }

    private AttributeInfo getInfo(String key) throws DoesNotExist, NotImplemented {
        AttributeInfo info = attributes.get(key);
        
        if (info == null) {
            if (autoAdd) {
                addAttribute(key, AttributeType.STRING, false, false, false, true);
                info = attributes.get(key);
            } else {
                throw new DoesNotExist("Attribute " + key + " does not exist");
            }
        }
        
        if (info.type == AttributeType.TRIGGER) {
            throw new DoesNotExist("Cannot get/set value of a Trigger");
        }
        
        if (info.notImplemented) {
            throw new NotImplemented("Attribute " + key
                    + " not available in this implementation");
        }
        return info;
    }
    
    private AttributeInfo getInfoCheckVector(String key, boolean vector)
            throws DoesNotExist, NotImplemented, IncorrectState {
        AttributeInfo info = getInfo(key);
        if (vector != info.vector) {
            throw new IncorrectState("Attribute " + key
                    + " is " + (vector ? "not " : "") + "a vector attribute");
        }
        return info;
    }
    
    public synchronized String getAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            IncorrectState, DoesNotExist, Timeout, NoSuccess {
        
        return getInfoCheckVector(key, false).value;
    }

    public synchronized String[] getVectorAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            IncorrectState, DoesNotExist, Timeout, NoSuccess {
        
        return getInfoCheckVector(key, true).vectorValue.clone();
    }

    public synchronized boolean isReadOnlyAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        
        return getInfo(key).readOnly;
    }

    public synchronized boolean isRemovableAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        return getInfo(key).removable;
    }

    public synchronized boolean isVectorAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        return getInfo(key).vector;
    }

    public synchronized boolean isWritableAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        return ! getInfo(key).readOnly;
    }

    public synchronized String[] listAttributes() throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            Timeout, NoSuccess {
        HashSet<String> keys = new HashSet<String>();
        for (String key : attributes.keySet()) {
            try {
                getInfo(key); // will throw exception if not implemented.
                keys.add(key);
            } catch(Throwable e) {
                // ignored
                e.printStackTrace();
            }
        }
        
        return keys.toArray(new String[keys.size()]);
    }

    public synchronized void removeAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        AttributeInfo info = getInfo(key);
        if (! info.removable) {
            throw new PermissionDenied("Attribute " + key + " is not removable");
        }
        attributes.remove(key);
    }
    
    // Set method without checks for readOnly. We must be able to set the
    // attribute, somehow.
    protected synchronized void setValue(String key, String value)
            throws DoesNotExist, NotImplemented, IncorrectState, BadParameter { 
        AttributeInfo info = getInfoCheckVector(key, false);
        if (info.type == AttributeType.TIME) {
            try {
                long v = Long.parseLong(value);
                value = dateFormat(new Date(v));
            } catch(NumberFormatException e) {
                // ignored. checkValueType will check the format.
            }
        }
        checkValueType(key, info.type, value);
        info.value = value;
    }
 
    // Set method without checks for readOnly. We must be able to set the
    // attribute, somehow.   
    protected synchronized void setVectorValue(String key, String[] values)
            throws DoesNotExist, NotImplemented, IncorrectState, BadParameter {
        AttributeInfo info = getInfoCheckVector(key, true);
        
        values = values.clone();

        for (int i = 0; i < values.length; i++) {
            if (info.type == AttributeType.TIME) {
                try {
                    long v = Long.parseLong(values[i]);
                    values[i] = dateFormat(new Date(v));
                } catch(NumberFormatException e) {
                    // ignored. checkValueType will check the format.
                }
            }
            checkValueType(key, info.type, values[i]);
        }
        info.vectorValue = values;
    }
    
    protected synchronized String getValue(String key) {
        AttributeInfo info = attributes.get(key);
        if (info != null) {
            return info.value;
        }
        return null;
    }
    
    protected synchronized String[] getVectorValue(String key) {
        AttributeInfo info = attributes.get(key);
        if (info != null && info.vectorValue != null) {
            return info.vectorValue.clone();
        }
        return null;
    }
    
    protected void checkValueType(String key, AttributeType type, String value) throws BadParameter {
        if (value == null) {
            throw new BadParameter("Attribute value set to null");
        }
        switch(type) {
        case STRING:
        case ENUM:
            // Leave possible checks up to subclasses.
            break;
        case INT:
            try {
                Long.parseLong(value);  
            } catch(NumberFormatException e) {
                throw new BadParameter("Int-typed attribute set to non-integer " + value);
            }
            break;
        case FLOAT:
            try {
                Double.parseDouble(value);
            } catch(NumberFormatException e) {
                throw new BadParameter("Float-typed attribute set to non-float " + value);
            }
            break;
        case BOOL:
            if (! value.equals(TRUE) && ! value.equals(FALSE)) {
                throw new BadParameter("Bool-typed attribute set to non-bool " + value);
            }
            break;
        case TIME:
            ParsePosition p = new ParsePosition(0);
            dateFormatter.parse(value, p);
            if (p.getErrorIndex() >= 0 || p.getIndex() < value.length()) {
                throw new BadParameter("Time-values attribute set to non-time " + value);
            }
            break;
        }
    }

    public synchronized void setAttribute(String key, String value) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            IncorrectState, BadParameter, DoesNotExist, Timeout, NoSuccess {
        AttributeInfo info = getInfoCheckVector(key, false);

        if (info.readOnly) {
            throw new PermissionDenied("Attribute " + key + " is readOnly");
        }

        if (info.type == AttributeType.TIME) {
            try {
                long v = Long.parseLong(value);
                value = dateFormat(new Date(v));
            } catch(NumberFormatException e) {
                // ignored. Try and parse value here?
            }
        }
        checkValueType(key, info.type, value);
        info.value = value;
    }

    public synchronized void setVectorAttribute(String key, String[] values)
            throws NotImplemented, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, IncorrectState, BadParameter, DoesNotExist,
            Timeout, NoSuccess {
        AttributeInfo info = getInfoCheckVector(key, true);
        
        if (info.readOnly) {
            throw new PermissionDenied("Attribute " + key + " is readOnly");
        }
        
        values = values.clone();
        for (int i = 0; i < values.length; i++) {
            if (info.type == AttributeType.TIME) {
                try {
                    long v = Long.parseLong(values[i]);
                    values[i] = dateFormat(new Date(v));
                } catch(NumberFormatException e) {
                    // ignored. checkValueType will check the format.
                }
            }
            checkValueType(key, info.type, values[i]);
        }
        info.vectorValue = values;
    }
    
    // Formats the date, fixing the zero-padding.
    private String dateFormat(Date d) {
        String s = dateFormatter.format(d);
        StringBuffer b = new StringBuffer(s);
        if (b.charAt(8) == '0') {
            b.setCharAt(8, ' ');
            return b.toString();
        }
        return s;
    }
    
    public static void main(String[] args) throws Exception {
        Attributes attribs = new Attributes(false);
        attribs.addAttribute("time", AttributeType.TIME, false, false, false, false);
        attribs.setAttribute("time", "" + System.currentTimeMillis());
        System.out.println("time = " + attribs.getAttribute("time"));
        String[] keys = attribs.findAttributes(new String[] { "t{i,k}?*=[A-Z]*"});
        System.out.println("Found " + keys.length + " keys, should be 1");
        keys = attribs.findAttributes(new String[] { "t{i,k}?"});
        System.out.println("Found " + keys.length + " keys, should be 0");
    }
}
