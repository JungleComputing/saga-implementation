package org.ogf.saga.adaptors.archive.file;

import org.ogf.saga.adaptors.archive.namespace.ArchiveNSDirectoryAdaptor;
import org.ogf.saga.adaptors.local.file.LocalDirectoryAdaptor;
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
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.proxies.file.DirectoryWrapper;
import org.ogf.saga.url.URL;

public class ArchiveDirectoryAdaptor extends LocalDirectoryAdaptor {

    public ArchiveDirectoryAdaptor(DirectoryWrapper wrapper,
            SessionImpl session, URL name, int flags)
            throws NotImplementedException, IncorrectURLException,
            BadParameterException, DoesNotExistException,
            PermissionDeniedException, AuthorizationFailedException,
            AuthenticationFailedException, TimeoutException,
            NoSuccessException, AlreadyExistsException {
        super(wrapper, session, name, flags,
                new ArchiveNSDirectoryAdaptor(null, session, name, flags
                        & Flags.ALLNAMESPACEFLAGS.getValue()));
    }
    
}
