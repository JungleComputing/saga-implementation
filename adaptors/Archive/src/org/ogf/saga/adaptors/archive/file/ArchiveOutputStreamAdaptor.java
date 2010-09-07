package org.ogf.saga.adaptors.archive.file;

import org.ogf.saga.adaptors.archive.ArchiveAdaptorTool;
import org.ogf.saga.adaptors.local.file.LocalOutputStreamAdaptor;
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
import org.ogf.saga.proxies.file.FileOutputStreamWrapper;
import org.ogf.saga.url.URL;

public class ArchiveOutputStreamAdaptor extends LocalOutputStreamAdaptor {
    
    public static String[] getSupportedSchemes() {
        return ArchiveAdaptorTool.getSupportedSchemes();
    }

    public ArchiveOutputStreamAdaptor(FileOutputStreamWrapper wrapper,
            SessionImpl session, URL source) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, DoesNotExistException,
            AlreadyExistsException, TimeoutException, NoSuccessException {
        super(wrapper, session, source, ArchiveAdaptorTool.getInstance());
    }

}
