package org.ogf.saga.impl.monitoring;

import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.Timeout;

public class MonitoringFactory extends
        org.ogf.saga.monitoring.MonitoringFactory {

    @Override
    protected org.ogf.saga.monitoring.Metric doCreateMetric(String name,
            String desc, String mode, String unit, String type, String value)
    throws NotImplemented, BadParameter, Timeout, NoSuccess {
        return new Metric(name, desc, mode, unit, type, value);
    }
}
