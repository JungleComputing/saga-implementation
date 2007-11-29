package org.ogf.saga.spi.stream;

import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.IncorrectState;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.impl.monitoring.Metric;
import org.ogf.saga.monitoring.Monitorable;
import org.ogf.saga.session.Session;

public class StreamMetric extends Metric {

    public StreamMetric(Monitorable monitorable, Session session, String name,
            String desc, String mode, String unit, String type, String value)
            throws NotImplemented, BadParameter {
        super(monitorable, session, name, desc, mode, unit, type, value);
    }
    
    // Make setMode available for classes in this package.
    protected void setMode(String value) throws NotImplemented, BadParameter,
            DoesNotExist, IncorrectState {
        super.setMode(value);
    }
    
    // Make setValue available for classes in this package.
    protected void setValue(String value) throws NotImplemented, BadParameter,
            IncorrectState, DoesNotExist {
        super.setValue(value);
    }
    
    // make internalFire available for classes in this package.
    protected void internalFire() {
        super.internalFire();
    }

}
