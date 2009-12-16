package org.ogf.saga.impl.url;

import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.NoSuccessException;

public class URLFactoryImpl extends org.ogf.saga.url.URLFactory {

    @Override
    protected org.ogf.saga.url.URL doCreateURL(String url)
            throws BadParameterException, NoSuccessException {
        return new URLImpl(url);
    }

}
