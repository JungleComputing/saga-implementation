package org.ogf.saga.proxies.buffer;

import org.ogf.saga.ObjectType;
import org.ogf.saga.buffer.Buffer;
import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.IncorrectState;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.session.Session;

final class BufferWrapper implements Buffer {
    final Buffer proxy;
    
    BufferWrapper(Buffer proxy) {
        this.proxy = proxy;
    }

    public Object clone() throws CloneNotSupportedException {
        return new BufferWrapper((Buffer) proxy.clone());
    }

    public void close() throws NotImplemented {
        proxy.close();
    }

    public void close(float timeoutInSeconds) throws NotImplemented {
        proxy.close(timeoutInSeconds);
    }

    public byte[] getData() throws NotImplemented, DoesNotExist, IncorrectState {
        return proxy.getData();
    }

    public String getId() {
        return proxy.getId();
    }

    public Session getSession() throws DoesNotExist {
        return proxy.getSession();
    }

    public int getSize() throws NotImplemented, IncorrectState {
        return proxy.getSize();
    }

    public ObjectType getType() {
        return ObjectType.BUFFER;
    }

    public void setData(byte[] data) throws NotImplemented, BadParameter,
            IncorrectState, NoSuccess {
        proxy.setData(data);
    }

    public void setSize() throws NotImplemented, BadParameter, IncorrectState,
            NoSuccess {
        proxy.setSize();
    }

    public void setSize(int size) throws NotImplemented, BadParameter,
            IncorrectState, NoSuccess {
        proxy.setSize(size);
    }
}
