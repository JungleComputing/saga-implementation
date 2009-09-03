package org.ogf.saga.proxies.isn;

import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.isn.EntityDataSet;
import org.ogf.saga.isn.ISNFactory;
import org.ogf.saga.session.Session;
import org.ogf.saga.url.URL;

/*
 * (non-Javadoc)
 * 
 * @see org.ogf.saga.isn.ISNFactory
 */
public class ISNWrapperFactory extends ISNFactory {
    public ISNWrapperFactory() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.isn.ISNFactory#doCreateEntityDataSet(org.ogf.saga.session.Session,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    protected EntityDataSet doCreateEntityDataSet(Session session, String model, String entityName, String filter)
            throws BadParameterException, NoSuccessException {
        return new ISNWrapper(session, null, model, entityName, filter);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.isn.ISNFactory#doCreateEntityDataSet(org.ogf.saga.session.Session,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      org.ogf.saga.url.URL)
     */
    @Override
    protected EntityDataSet doCreateEntityDataSet(Session session, String model, String entityName, String filter,
            URL infoSystemUrl) throws BadParameterException, NoSuccessException {
        return new ISNWrapper(session, infoSystemUrl, model, entityName, filter);
    }

}
