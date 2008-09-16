package org.ogf.saga.engine;

import java.util.ArrayList;

/**
 * Adaptor container for adaptors that are loaded for a single specific
 * SAGA object type (e.g. File).
 */
class AdaptorList extends ArrayList<Adaptor> {

    private static final long serialVersionUID = 1L;

    /** The short name for the service provider interface. */
    private final String spiName;

    /**
     * Constructs an empty adaptor list for the specified service provider
     * interface.
     * @param spi
     *            The service provider interface.
     */
    AdaptorList(String spi) {
        this.spiName = spi;
    }

    /**
     * Copying constructor: constructs a new adaptor list by copying the
     * parameter.
     * @param l the adaptor list to copy.
     */
    AdaptorList(AdaptorList l) {
        super(l);
        this.spiName = l.spiName;
    }
   
    /**
     * Obtains the name of the service provider interface of this adaptor
     * list.
     * @return the name of the service provider interface.
     */
    String getSpiName() {
        return spiName;
    }

    /**
     * Returns the current position of the specified adaptor name in this
     * adaptor list.
     * @param adaptorName the name of the adaptor.
     * @return the index, or -1 if not found.
     */
    int getPos(String adaptorName) {
        for (int i = 0; i < size(); i++) {
            Adaptor a = get(i);

            if (a.getAdaptorName().equals(adaptorName)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Moves the specified adaptor to the specified index, unless it is
     * not present.
     * @param destPos the specified index for the adaptor.
     * @param name the name of the adaptor.
     * @return the next position.
     */
    int placeAdaptor(int destPos, String name) {
        int pos = getPos(name);

        if (pos != -1) {
            add(destPos, remove(pos));
            destPos++;
        }

        return destPos;
    }
    
    /**
     * Returns the adaptor list, as a string suitable for printing.
     * @return the adaptor list as a string.
     */
    public String toString() {
        String res = "Adaptor list for " + spiName;
        
        res += ", adaptors = " + super.toString();
        return res;
    }
}
