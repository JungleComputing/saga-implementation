package org.ogf.saga.engine;

import java.util.ArrayList;

/**
 * Adaptor container for adaptors that are loaded for a single specific
 * SAGA object type (e.g. File).
 */
class AdaptorList extends ArrayList<Adaptor> {

    private static final long serialVersionUID = 1L;

    /** The short name for the api class. */
    private final String spiName;

    /**
     * Constructs an empty adaptor list for the specified spi class.
     * @param spiClass
     *            The api class all adaptors in this set implement.
     */
    AdaptorList(String spiClass) {
        this.spiName = spiClass;
    }

    AdaptorList(AdaptorList l) {
        super(l);
        this.spiName = l.spiName;
    }
   
    String getSpiName() {
        return spiName;
    }

    int getPos(String adaptorName) {
        for (int i = 0; i < size(); i++) {
            Adaptor a = get(i);

            if (a.getAdaptorName().equals(adaptorName)) {
                return i;
            }
        }

        return -1;
    }

    int placeAdaptor(int destPos, String name) {
        int pos = getPos(name);

        if (pos != -1) {
            add(destPos, remove(pos));
            destPos++;
        }

        return destPos;
    }
    
    public String toString() {
        String res = "Adaptor list for " + spiName;
        
        res += ", adaptors = " + super.toString();
        return res;
    }
}
