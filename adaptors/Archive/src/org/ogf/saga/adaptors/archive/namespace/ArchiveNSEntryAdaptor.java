package org.ogf.saga.adaptors.archive.namespace;

import org.ogf.saga.adaptors.archive.ArchiveAdaptorTool;
import org.ogf.saga.adaptors.local.AdaptorTool;
import org.ogf.saga.adaptors.local.namespace.LocalNSEntryAdaptor;
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
import org.ogf.saga.proxies.namespace.NSEntryWrapper;
import org.ogf.saga.url.URL;

public class ArchiveNSEntryAdaptor extends LocalNSEntryAdaptor {
    
    public static String[] getSupportedSchemes() {
        return ArchiveAdaptorTool.getSupportedSchemes();
    }

    public ArchiveNSEntryAdaptor(NSEntryWrapper wrapper,
            SessionImpl sessionImpl, URL name, int flags)
            throws NotImplementedException, IncorrectURLException,
            BadParameterException, DoesNotExistException,
            PermissionDeniedException, AuthorizationFailedException,
            AuthenticationFailedException, TimeoutException,
            NoSuccessException, AlreadyExistsException {
        this(wrapper, sessionImpl, name, flags, false, 
                ArchiveAdaptorTool.getInstance());
    }

    public ArchiveNSEntryAdaptor(NSEntryWrapper wrapper, SessionImpl sessionImpl,
            URL name, int flags, boolean isDir, AdaptorTool tool)
            throws NotImplementedException, IncorrectURLException,
            BadParameterException, DoesNotExistException,
            PermissionDeniedException, AuthorizationFailedException,
            AuthenticationFailedException, TimeoutException,
            NoSuccessException, AlreadyExistsException {
        super(wrapper, sessionImpl, name, flags, isDir, tool);
    }
    
}