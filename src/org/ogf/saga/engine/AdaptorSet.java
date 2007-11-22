package org.ogf.saga.engine;

import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * Container for adaptor lists, organized by the SPI that they implement.
 */
class AdaptorSet extends ArrayList<AdaptorList> {
    private static final long serialVersionUID = 1L;
    protected static Logger logger = Logger.getLogger(AdaptorSet.class);
	
    AdaptorList getAdaptorList(String typeName) {
        for (AdaptorList l : this) {
            String type = l.getSpiName();
            if (type.equals(typeName)) {
                return l;
            }
        }
        return null;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("------------LOADED ADAPTORS------------\n");
        for (AdaptorList l : this) {
            buf.append("Adaptor type: ");
            buf.append(l.getSpiName());
            buf.append(":\n");
            for (Adaptor a : l) {
                buf.append("    ");
                buf.append(a);
                buf.append("\n");
            }
            buf.append("\n");
        }
        buf.append("---------------------------------------");
        return buf.toString();
    }
}
