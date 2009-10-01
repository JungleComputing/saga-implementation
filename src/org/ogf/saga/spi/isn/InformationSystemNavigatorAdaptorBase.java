package org.ogf.saga.spi.isn;

import org.ogf.saga.impl.AdaptorBase;
import org.ogf.saga.impl.session.SessionImpl;
import org.ogf.saga.proxies.isn.ISNWrapper;

/**
 * Base class for Information System Navigator adapters. Adapters extending this
 * class should provide two constructors.
 * <dl>
 * <dt>One constructor should take:</dt>
 * <dd><code>ISNWrapper wrapper</code>, needed by the super constructor</dd>
 * <dd><code>SessionImpl sessionImpl</code>, needed by the super constructor</dd>
 * <dd><code>URL infoSystemUrl</code>, the URL to guide the implementation,
 * may be <code>null</code></dd>
 * <dd><code>String model</code>, a string containing the name of the
 * information model</dd>
 * <dd><code>String entityName</code>, a string containing the name of the
 * entity to navigate</dd>
 * <dd><code>String filter</code>, a string containing the filter for
 * filtering entities, may be <code>null</code></dd>
 * </dt>
 * </dl>
 * <dl>
 * <dt>The other constructor should take:</dt>
 * <dd><code>ISNWrapper wrapper</code>, needed by the super constructor</dd>
 * <dd><code>SessionImpl sessionImpl</code>, needed by the super constructor</dd>
 * <dd><code>URL infoSystemUrl</code>, the URL to guide the implementation,
 * may be <code>null</code></dd>
 * <dd><code>String model</code>, a string containing the name of the
 * information model</dd>
 * <dd><code>String entityName</code>, a string containing the name of the
 * current entity</dd>
 * <dd><code>String filter</code>, a string containing the filter for
 * filtering entities, may be <code>null</code></dd>
 * <dd><code>Set<EntityData> entityData</code>, the entity data set of the
 * current entity</dd>
 * <dd><code>String nextEntity</code>, a string containing the name of the
 * entity to navigate to</dd>
 * </dt>
 * </dl>
 * 
 */
public abstract class InformationSystemNavigatorAdaptorBase extends AdaptorBase<ISNWrapper> implements
        InformationSystemNavigatorSPI {

    /**
     * Constructs a EntityDataSetAdaptorBase object.
     * 
     * @param wrapper
     *            the wrapper object
     * @param sessionImpl
     *            the session of the adapter
     */
    public InformationSystemNavigatorAdaptorBase(ISNWrapper wrapper, SessionImpl sessionImpl) {
        super(sessionImpl, wrapper);
    }

}
