package org.ogf.saga.spi.file;

import java.io.IOException;

import org.ogf.saga.impl.AdaptorBase;

public abstract class FileInputStreamSpi extends AdaptorBase implements
        FileInputStreamSpiInterface {

    public FileInputStreamSpi(Object wrapper) {
        super(wrapper);
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }
}
