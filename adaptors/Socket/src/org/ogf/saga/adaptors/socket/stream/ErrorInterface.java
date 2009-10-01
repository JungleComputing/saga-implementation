package org.ogf.saga.adaptors.socket.stream;

public interface ErrorInterface {

    public void signalReaderException(StreamExceptionalSituation e);

    // public void signalReaderConnectionDropped();

}
