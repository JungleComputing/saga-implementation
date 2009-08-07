package org.ogf.saga.proxies.isn;

import java.util.List;
import java.util.Set;

import org.ogf.saga.engine.SAGAEngine;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.impl.SagaObjectBase;
import org.ogf.saga.isn.EntityData;
import org.ogf.saga.isn.EntityDataSet;
import org.ogf.saga.session.Session;
import org.ogf.saga.spi.isn.InformationSystemNavigatorSPI;
import org.ogf.saga.url.URL;

/*
 * (non-Javadoc)
 * 
 * @see org.ogf.saga.isn.EntityDataSet
 */
public class ISNWrapper extends SagaObjectBase implements EntityDataSet {

    /** The Information System Navigator adapter. */
    private InformationSystemNavigatorSPI m_proxy;

    /**
     * The name of the entity from the information system, as selected by the
     * user.
     */
    private String m_entityName;

    /**
     * Create a EntityDataSet wrapper.
     * 
     * @param session
     * @param infoSystemUrl
     * @param entityName
     * @param filter
     * @throws BadParameterException
     * @throws NoSuccessException
     */
    protected ISNWrapper(Session session, URL infoSystemUrl, String entityName, String filter)
            throws BadParameterException, NoSuccessException {
        super(session);
        m_entityName = entityName;
        Object[] parameters = { this, session, infoSystemUrl, entityName, filter };
        createProxy1(parameters);
    }

    /**
     * Create a EntityDataSet wrapper.
     * 
     * @param session
     * @param infoSystemUrl
     * @param entityName
     * @param filter
     * @param entityData
     * @param nextEntity
     * @throws BadParameterException
     * @throws NoSuccessException
     */
    private ISNWrapper(Session session, URL infoSystemUrl, String entityName, String filter,
            Set<EntityData> entityData, String nextEntity) throws BadParameterException, NoSuccessException {
        super(session);
        m_entityName = entityName;
        Object[] parameters = { this, session, infoSystemUrl, entityName, filter, entityData, nextEntity };
        createProxy2(parameters);
    }

    /**
     * Clone the ISNWrapper and attached adapter.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        ISNWrapper clone = (ISNWrapper) super.clone();
        clone.m_proxy = (InformationSystemNavigatorSPI) SAGAEngine.createAdaptorCopy(
                InformationSystemNavigatorSPI.class, m_proxy, clone);
        return clone;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.isn.EntityDataSet#getData()
     */
    @Override
    public Set<EntityData> getData() {
        return m_proxy.getData();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.isn.EntityDataSet#getRelatedEntities(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public EntityDataSet getRelatedEntities(String relatedName, String filter) throws BadParameterException,
            NoSuccessException {
        return new ISNWrapper(sessionImpl, m_proxy.getInfoSystemUrl(), m_entityName, filter, getData(), relatedName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.isn.EntityDataSet#getRelatedEntities(java.lang.String)
     */
    @Override
    public EntityDataSet getRelatedEntities(String relatedName) throws BadParameterException, NoSuccessException {
        return new ISNWrapper(sessionImpl, m_proxy.getInfoSystemUrl(), m_entityName, null, getData(), relatedName);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.isn.EntityDataSet#listRelatedEntityNames()
     */
    @Override
    public List<String> listRelatedEntityNames() {
        return m_proxy.listRelatedEntityNames();
    }

    /**
     * Create the proxy that points to the adapter, where
     * <code>parameters</code> contains
     * <code>this, session, infoSystemUrl, entityName, filter
     * </code>
     * 
     * @param parameters
     * @throws BadParameterException
     * @throws NoSuccessException
     */
    private void createProxy1(Object[] parameters) throws BadParameterException, NoSuccessException {
        try {
            m_proxy = (InformationSystemNavigatorSPI) SAGAEngine.createAdaptorProxy(
                    InformationSystemNavigatorSPI.class, new Class[] { ISNWrapper.class,
                            org.ogf.saga.impl.session.SessionImpl.class, URL.class, String.class, String.class },
                    parameters);
        } catch (BadParameterException e) {
            throw e;
        } catch (NoSuccessException e) {
            throw e;
        } catch (SagaException e) {
            throw new NoSuccessException("Constructor failed", e);
        }
    }

    /**
     * Create the proxy that points to the adapter, where
     * <code>parameters</code> contains
     * <code>this, session, infoSystemUrl, entityName, filter, entityData, nextEntity
     * </code>
     * 
     * @param parameters
     * @throws BadParameterException
     * @throws NoSuccessException
     */
    private void createProxy2(Object[] parameters) throws BadParameterException, NoSuccessException {
        try {
            m_proxy = (InformationSystemNavigatorSPI) SAGAEngine.createAdaptorProxy(
                    InformationSystemNavigatorSPI.class, new Class[] { ISNWrapper.class,
                            org.ogf.saga.impl.session.SessionImpl.class, URL.class, String.class, String.class,
                            Set.class, String.class }, parameters);
        } catch (BadParameterException e) {
            throw e;
        } catch (NoSuccessException e) {
            throw e;
        } catch (SagaException e) {
            throw new NoSuccessException("Constructor failed", e);
        }
    }

}
