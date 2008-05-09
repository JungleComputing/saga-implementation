package org.ogf.saga.adaptors.generic.logicalfile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.ogf.saga.URL;
import org.ogf.saga.error.AlreadyExistsException;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.file.FileFactory;
import org.ogf.saga.impl.session.Session;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.namespace.NSEntry;
import org.ogf.saga.namespace.NSFactory;
import org.ogf.saga.proxies.logicalfile.LogicalFileWrapper;
import org.ogf.saga.spi.logicalfile.LogicalFileAdaptorBase;

/**
 * This LogicalFile adaptor actually stores its contents
 * (the list of replicas) in a Saga NSEntry object.
 * As such, it uses other SAGA packages (namespace and file)
 * for its implementation.
 * It recognizes the schemes "file" and "any".
 */
public class LogicalFileAdaptor extends LogicalFileAdaptorBase {

    private NSEntry entry;

    private HashSet<URL> urls = new HashSet<URL>();
    
    private final URL fileURL;
    
   public LogicalFileAdaptor(LogicalFileWrapper wrapper, Session session,
            URL url, int flags) throws NotImplementedException,
            IncorrectURLException, BadParameterException,
            DoesNotExistException, PermissionDeniedException,
            AuthorizationFailedException, AuthenticationFailedException,
            TimeoutException, NoSuccessException, AlreadyExistsException {
               
        super(wrapper, session, url, flags);
        
        // Check scheme.
        checkScheme(url);
        
        // Set scheme for underlying file.
        fileURL = new URL(url.toString());
        fileURL.setScheme("file");
        
        entry = NSFactory.createNSEntry(session, fileURL,
                flags & Flags.ALLNAMESPACEFLAGS.getValue());
    
        if (Flags.READ.isSet(flags)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    FileFactory.createFileInputStream(session, fileURL)));
            try {
                for (;;) {
                    String s = in.readLine();
                    if (s == null) {
                        break;
                    }
                    try {
                        URL u = new URL(s);
                        urls.add(u);
                    } catch(Throwable e) {
                        try {
                            in.close();
                        } catch(Throwable ex) {
                            // ignored
                        }
                        throw new NoSuccessException("" + fileURL + " contains " + s
                                + ", which does not seem to be an URL");
                    }
                }
                in.close();
            } catch (IOException e) {
                throw new NoSuccessException("Exception while reading " + fileURL,
                        e);
            }
        }
    }
   
    static void checkScheme(URL url) throws NotImplementedException {
        String scheme = url.getScheme();
        if (! "file".equals(scheme) && ! "any".equals(scheme)) {
            throw new NotImplementedException("Unknown scheme");
        }
    }

    public Object clone() throws CloneNotSupportedException {
        LogicalFileAdaptor clone = (LogicalFileAdaptor) super.clone();
        clone.entry = (NSEntry) entry.clone();
        clone.urls = new HashSet<URL>(urls);
        return clone;
    }

    public void close(float timeoutInSeconds) throws NotImplementedException,
            IncorrectStateException, NoSuccessException {
        entry.close(timeoutInSeconds);
        super.close(timeoutInSeconds);
    }

    public synchronized void addLocation(URL name)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        
        if (closed) {
            throw new IncorrectStateException(
                    "addLocation() called on closed LogicalFile");
        }
        if (!Flags.WRITE.isSet(logicalFileFlags)) {
            throw new PermissionDeniedException(
                    "addLocation() called on LogicalFile not opened for writing");
        }

        if (doAdd(name)) {
            write();
        }
    }

    private boolean doAdd(URL name) {

        name = name.normalize();
        if (urls.contains(name)) {
            // Should not throw AlreadyExists. Ignore, silently.
            return false;
        }
        urls.add(name);
        return true;
    }
    
    public synchronized List<URL> listLocations()
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        if (closed) {
            throw new IncorrectStateException(
                    "listLocations() called on closed LogicalFile");
        }
        if (!Flags.READ.isSet(logicalFileFlags)) {
            throw new PermissionDeniedException(
                    "listLocations() called on LogicalFile not opened for reading");
        }
        return new ArrayList<URL>(urls);
    }

    public synchronized void removeLocation(URL name)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        if (closed) {
            throw new IncorrectStateException(
                    "removeLocation() called on closed LogicalFile");
        }
        if (!Flags.WRITE.isSet(logicalFileFlags)) {
            throw new PermissionDeniedException(
                    "removeLocation() called on LogicalFile not opened for writing");
        }

        if (doRemove(name)) {
            write();
        } else {
            throw new DoesNotExistException("url " + name + " not found");
        }
    }

    private boolean doRemove(URL name) throws DoesNotExistException {       
        name = name.normalize();
        if (!urls.contains(name)) {
            return false;
        }
        urls.remove(name);
        return true;
    }
    
    public synchronized void replicate(URL name, int flags)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        if (closed) {
            throw new IncorrectStateException(
                    "replicate() called on closed LogicalFile");
        }
        if (!Flags.WRITE.isSet(logicalFileFlags)
                || !Flags.READ.isSet(logicalFileFlags)) {
            throw new PermissionDeniedException(
                    "replicate() called on LogicalFile not opened for reading/writing");
        }
        if (Flags.RECURSIVE.isSet(flags)) {
            throw new BadParameterException(
                    "replicate() call with RECURSIVE flag");
        }
        if (! name.isAbsolute()) {
            throw new BadParameterException(
                    "replicate() call with relative URL " + name);
        }

        name = name.normalize();

        URL[] array = urls.toArray(new URL[urls.size()]);
        if (array.length == 0) {
            throw new IncorrectStateException(
                    "replicate() called on empty LogicalFile");
        }

        // Create entry so that we can copy.
        try {
            NSEntry e = NSFactory.createNSEntry(session, array[0],
                    Flags.NONE.getValue());

            // Pick the first location. Exceptions passed on to user.
            // If this fails, the location is not added.
            e.copy(name, flags);
            e.close();
        } catch (Throwable e) {
            throw new NoSuccessException("Copy failed", e);
        }

        if (doAdd(name)) {
            write();
        }
    }

    public synchronized void updateLocation(URL nameOld, URL nameNew)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        
        if (closed) {
            throw new IncorrectStateException(
                    "updateLocation() called on closed LogicalFile");
        }
        
        if (!Flags.WRITE.isSet(logicalFileFlags)
                || !Flags.READ.isSet(logicalFileFlags)) {
            throw new PermissionDeniedException(
                    "updateLocation() called on LogicalFile not opened for reading/writing");
        }
        
        if (! nameNew.isAbsolute()) {
            throw new BadParameterException(
                    "updateLocation() call with relative URL " + nameNew);
        }
        
        nameNew = nameNew.normalize();

        if (urls.contains(nameNew)) {
            throw new AlreadyExistsException("URL " + nameNew
                    + " already exists in LogicalFile");
        }
        if (! doRemove(nameOld)) {
            throw new DoesNotExistException("url " + nameOld + " not found");
        }

        doAdd(nameNew);
        write();
    }
    
    private void write() throws NoSuccessException {
        
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                    FileFactory.createFileOutputStream(session, fileURL)));
            for (URL u : urls) {
                out.write(u.toString());
                out.newLine();
            }
            out.close();
        } catch (Throwable e) {
            throw new NoSuccessException("Exception while writing "
                    + nameUrl, e);
        }
    }

    public void copy(URL target, int flags) throws IncorrectStateException,
            NoSuccessException, BadParameterException, AlreadyExistsException,
            IncorrectURLException, NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, DoesNotExistException {
        checkScheme(target);
        entry.copy(target, flags);
    }

    public void move(URL target, int flags) throws IncorrectStateException,
            NoSuccessException, BadParameterException, AlreadyExistsException,
            NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            TimeoutException, IncorrectURLException, DoesNotExistException {
        checkScheme(target);
        entry.move(target, flags);
    }

    public boolean isDir() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        return entry.isDir();
    }

    public boolean isEntry() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        return entry.isEntry();
    }

    public boolean isLink() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        return entry.isLink();
    }

    public void link(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException, TimeoutException,
            NoSuccessException, IncorrectURLException {
        checkScheme(target);
        entry.link(target, flags);
    }

    public void permissionsAllow(String id, int permissions, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException, TimeoutException,
            NoSuccessException {
        entry.permissionsAllow(id, permissions, flags);
    }

    public void permissionsDeny(String id, int permissions, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, IncorrectStateException,
            PermissionDeniedException, BadParameterException, TimeoutException,
            NoSuccessException {
        entry.permissionsDeny(id, permissions, flags);
    }

    public URL readLink() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        return entry.readLink();
    }

    public void remove(int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        entry.remove(flags);
    }

    public String getGroup() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return entry.getGroup();
    }

    public String getOwner() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return entry.getOwner();
    }

    public boolean permissionsCheck(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        return entry.permissionsCheck(id, permissions);
    }
}
