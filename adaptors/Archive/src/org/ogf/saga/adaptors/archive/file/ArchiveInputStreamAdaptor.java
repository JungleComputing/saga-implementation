package org.ogf.saga.adaptors.archive.file;

import org.ogf.saga.adaptors.archive.ArchiveAdaptorTool;
import org.ogf.saga.adaptors.local.file.LocalInputStreamAdaptor;
import org.ogf.saga.error.AlreadyExistsException;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.session.SessionImpl;
import org.ogf.saga.proxies.file.FileInputStreamWrapper;
import org.ogf.saga.url.URL;

public class ArchiveInputStreamAdaptor extends LocalInputStreamAdaptor {

    public ArchiveInputStreamAdaptor(FileInputStreamWrapper wrapper,
            SessionImpl session, URL source) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, DoesNotExistException,
            AlreadyExistsException, TimeoutException, NoSuccessException {
        super(wrapper, session, source, ArchiveAdaptorTool.getInstance());
    }

}
