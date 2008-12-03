package org.ogf.saga.impl.monitoring;

import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.TimeoutException;

public class MonitoringFactoryImpl extends
        org.ogf.saga.monitoring.MonitoringFactory {

    @Override
    protected org.ogf.saga.monitoring.Metric doCreateMetric(String name,
            String desc, String mode, String unit, String type, String value)
            throws NotImplementedException, BadParameterException,
            TimeoutException, NoSuccessException {
        return new MetricImpl(name, desc, mode, unit, type, value);
    }
}
