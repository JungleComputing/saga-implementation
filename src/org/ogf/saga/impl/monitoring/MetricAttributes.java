package org.ogf.saga.impl.monitoring;

import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.impl.attributes.AttributeType;
import org.ogf.saga.impl.attributes.AttributesImpl;

class MetricAttributes extends AttributesImpl {
    MetricAttributes() {
        addAttribute(MetricImpl.NAME, AttributeType.STRING, false, true, false,
                false);
        addAttribute(MetricImpl.DESCRIPTION, AttributeType.STRING, false, true,
                false, false);
        addAttribute(MetricImpl.MODE, AttributeType.STRING, false, true, false,
                false);
        addAttribute(MetricImpl.UNIT, AttributeType.STRING, false, true, false,
                false);
        addAttribute(MetricImpl.TYPE, AttributeType.STRING, false, true, false,
                false);
        addAttribute(MetricImpl.VALUE, AttributeType.STRING, false, false,
                false, false);
    }

    MetricAttributes(MetricAttributes orig) {
        super(orig);
    }

    public Object clone() {
        return new MetricAttributes(this);
    }

    protected synchronized void setValue(String key, String value)
            throws NotImplementedException, BadParameterException,
            DoesNotExistException, IncorrectStateException {
        super.setValue(key, value);
    }

    protected void checkValueType(String key, AttributeType type, String value)
            throws BadParameterException {
        if (MetricImpl.TYPE.equals(key)) {
            if (!STRING.equals(value) && !INT.equals(value)
                    && !ENUM.equals(value) && !FLOAT.equals(value)
                    && !BOOL.equals(value) && !TIME.equals(value)
                    && !TRIGGER.equals(value)) {
                throw new BadParameterException("Illegal metric type: " + value);
            }
        } else if (MetricImpl.MODE.equals(key)) {
            if (!"ReadOnly".equals(value) && !"ReadWrite".equals(value)
                    && !"Final".equals(value)) {
                throw new BadParameterException("Illegal metric mode: " + value);
            }
        }
    }

    protected synchronized void setVectorValue(String key, String[] values)
            throws DoesNotExistException, NotImplementedException,
            IncorrectStateException, BadParameterException {
        super.setVectorValue(key, values);
    }

    protected synchronized String getValue(String key) {
        return super.getValue(key);
    }
}
