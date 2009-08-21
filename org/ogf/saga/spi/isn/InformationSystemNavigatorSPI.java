package org.ogf.saga.spi.isn;

import java.util.List;
import java.util.Set;

import org.ogf.saga.isn.EntityData;
import org.ogf.saga.url.URL;

/**
 * Provides the interface for Information System Navigator adapters. Adapters
 * should not implement this interface but instead they should extend
 * {@link InformationSystemNavigatorAdaptorBase}
 * 
 */
public interface InformationSystemNavigatorSPI {

    /**
     * Returns a set of <code>EntityData</code> objects.
     * 
     * @return a set of <code>EntityData</code> objects associated with this
     *         entity
     */
    public Set<EntityData> getData();

    /**
     * Returns a set of names of those entities that may be navigated to, from
     * this EntityDataSet. N.B There is a special case where there is a self
     * relationship between entities, i.e. "AdminDomain" in GLUE 2, in such
     * cases the keywords <code>up</code> and <code>down</code> will also be
     * returned as appropriate.
     * 
     * @return a list of names of related entities
     */
    public List<String> listRelatedEntityNames();

    /**
     * Returns the URL of the information system that was used when populating
     * the entity data.
     * 
     * @return the URL of the information system
     */
    public URL getInfoSystemUrl();

}
