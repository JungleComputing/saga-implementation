package org.ogf.saga.adaptors.archive.namespace;

import org.ogf.saga.adaptors.archive.ArchiveAdaptorTool;
import org.ogf.saga.adaptors.local.AdaptorTool;
import org.ogf.saga.adaptors.local.namespace.LocalNSDirectoryAdaptor;
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
import org.ogf.saga.proxies.namespace.NSDirectoryWrapper;
import org.ogf.saga.url.URL;

public class ArchiveNSDirectoryAdaptor extends LocalNSDirectoryAdaptor {
    
    public static String[] getSupportedSchemes() {
        return ArchiveAdaptorTool.getSupportedSchemes();
    }

    public ArchiveNSDirectoryAdaptor(NSDirectoryWrapper wrapper, SessionImpl session,
            URL name, int flags) throws NotImplementedException,
            IncorrectURLException, BadParameterException,
            DoesNotExistException, PermissionDeniedException,
            AuthorizationFailedException, AuthenticationFailedException,
            TimeoutException, NoSuccessException, AlreadyExistsException {
        super(wrapper, session, name, flags, ArchiveAdaptorTool.getInstance());
    }

    @Override
    protected LocalNSEntryAdaptor createNSEntryAdaptor(SessionImpl session,
            URL name, int flags, boolean isDir, AdaptorTool tool)
            throws NotImplementedException, IncorrectURLException,
            BadParameterException, DoesNotExistException,
            PermissionDeniedException, AuthorizationFailedException,
            AuthenticationFailedException, TimeoutException,
            NoSuccessException, AlreadyExistsException {
        return new ArchiveNSEntryAdaptor(null, session, name, flags, isDir,
                tool);
    }
    
}