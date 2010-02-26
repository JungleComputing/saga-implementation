package org.ogf.saga.adaptors.fuse.properties;

import org.ogf.saga.url.URL;

public interface FuseToken {

    public String parse(URL url) throws PropertyParseException;
    
    @Override
    public String toString();
    
}
