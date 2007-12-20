package org.ogf.saga.impl.monitoring;

import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.IncorrectState;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.impl.attributes.AttributeType;
import org.ogf.saga.impl.attributes.Attributes;

class MetricAttributes extends Attributes {
    MetricAttributes() {
        addAttribute(Metric.NAME, AttributeType.STRING, false, true, false, false);
        addAttribute(Metric.DESCRIPTION, AttributeType.STRING, false, true, false, false);
        addAttribute(Metric.MODE, AttributeType.STRING, false, true, false, false);
        addAttribute(Metric.UNIT, AttributeType.STRING, false, true, false, false);
        addAttribute(Metric.TYPE, AttributeType.STRING, false, true, false, false);
        addAttribute(Metric.VALUE, AttributeType.STRING, false, false, false, false);
    }
    
    MetricAttributes(MetricAttributes orig) {
        super(orig);
    }
    
    public Object clone() {
        return new MetricAttributes(this);
    }
    
    protected void setValue(String key, String value)
            throws NotImplemented, BadParameter, DoesNotExist, IncorrectState {
        super.setValue(key, value);
    }
    
    protected void checkValueType(String key, AttributeType type, String value)
            throws BadParameter {
        if (Metric.TYPE.equals(key)) {
            if (! STRING.equals(value)
                && ! INT.equals(value)
                && ! ENUM.equals(value)
                && ! FLOAT.equals(value)
                && ! BOOL.equals(value)
                && ! TIME.equals(value)
                && ! TRIGGER.equals(value)) {
                throw new BadParameter("Illegal metric type: " + value);
            }
        } else if (Metric.MODE.equals(key)) {
            if (! "ReadOnly".equals(value)
                && ! "ReadWrite".equals(value)
                && ! "Final".equals(value)) {
                throw new BadParameter("Illegal metric mode: " + value);
            }
        }        
    }
    protected void setVectorValue(String key, String[] values)
            throws DoesNotExist, NotImplemented, IncorrectState, BadParameter {
        super.setVectorValue(key, values);
    }
    
    protected String getValue(String key) {
        return super.getValue(key);
    }
}
