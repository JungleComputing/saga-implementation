package org.ogf.saga.spi.file;

import java.io.IOException;

import org.ogf.saga.impl.AdaptorBase;
import org.ogf.saga.impl.session.Session;

public abstract class FileOutputStreamSpi extends AdaptorBase implements
        FileOutputStreamSpiInterface {

    public FileOutputStreamSpi(Session session, Object wrapper) {
        super(session, wrapper);
    }

    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
        
    }
}
