package org.ogf.saga.adaptors.javaGAT.stream;

import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.impl.monitoring.Metric;
import org.ogf.saga.stream.StreamState;

public class StreamStateUtils {

    static boolean equalsStreamState(Metric streamState, StreamState state) throws NoSuccessException {
        String val;
        try {
            val = streamState.getAttribute(Metric.VALUE);
        } catch (Throwable  e) {
            throw new NoSuccessException("Internal error", e);
        }
        return state.toString().equals(val);
    }

    static void checkStreamState(Metric streamState, StreamState state)
            throws NoSuccessException, IncorrectStateException {
        String val;
        try {
            val = streamState.getAttribute(Metric.VALUE);
        } catch (Throwable  e) {
            throw new NoSuccessException("Internal error", e);
        }
        if (!state.toString().equals(val)) {
            throw new IncorrectStateException("Should have been in " + state
                    + " state, not in " + val);
        }
    }

    static void setStreamState(Metric streamState, StreamState state)
            throws NoSuccessException {
        try {
            streamState.setValue(state.toString());
        } catch (Throwable e) {
            throw new NoSuccessException("Internal error", e);
        }
    }
    
    static boolean isFinalState(Metric streamState)
            throws NoSuccessException {
        try {
            String val = streamState.getAttribute(Metric.VALUE);
            return (val.equals(StreamState.DROPPED.toString())
                    || val.equals(StreamState.CLOSED.toString())
                    || val.equals(StreamState.ERROR.toString()));
        } catch(Throwable e) {
            throw new NoSuccessException("Internal error", e);
        }
    }
}
