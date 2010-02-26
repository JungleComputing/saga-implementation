package org.ogf.saga.adaptors.fuse.properties;

import org.ogf.saga.url.URL;

public class PlainToken implements FuseToken {

    private String s;
    
    PlainToken(String s) {
        this.s = s;
    }
    
    @Override
    public String parse(URL url) {
        return s;
    }

    @Override
    public String toString() {
        return s;
    }
    
}
