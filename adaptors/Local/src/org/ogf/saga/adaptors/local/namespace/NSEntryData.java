package org.ogf.saga.adaptors.local.namespace;

import java.io.File;

import org.ogf.saga.url.URL;

class NSEntryData {

    private URL url;
    private File file;

    NSEntryData(URL url, File file) {
        this.url = url;
        this.file = file;
    }

    URL getURL() {
        return url;
    }
    
    File getFile() {
        return file;
    }
    
}
    
