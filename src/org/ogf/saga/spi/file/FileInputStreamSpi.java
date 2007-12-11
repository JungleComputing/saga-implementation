package org.ogf.saga.spi.file;

import java.io.IOException;

import org.ogf.saga.impl.AdaptorBase;
import org.ogf.saga.impl.session.Session;

public abstract class FileInputStreamSpi extends AdaptorBase implements
        FileInputStreamSpiInterface {

    public FileInputStreamSpi(Session session, Object wrapper) {
        super(session, wrapper);
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }
}
