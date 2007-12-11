package org.ogf.saga.spi.file;

import java.io.IOException;

import org.ogf.saga.impl.AdaptorBase;

public abstract class FileOutputStreamSpi extends AdaptorBase implements
        FileOutputStreamSpiInterface {

    public FileOutputStreamSpi(Object wrapper) {
        super(wrapper);
    }

    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
        
    }
}
