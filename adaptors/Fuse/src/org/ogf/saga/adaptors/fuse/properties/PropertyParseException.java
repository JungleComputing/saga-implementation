package org.ogf.saga.adaptors.fuse.properties;

@SuppressWarnings("serial")
public class PropertyParseException extends Exception {

    public PropertyParseException(String msg) {
        super(msg);
    }

    public PropertyParseException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
