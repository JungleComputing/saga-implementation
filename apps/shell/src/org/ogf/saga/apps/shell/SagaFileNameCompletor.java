package org.ogf.saga.apps.shell;

import java.util.Collections;
import java.util.List;

import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.namespace.NSDirectory;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jline.Completor;

/**
 * Customized version of the FileNameCompletor in jline 0.9.94.
 * This version:
 * - uses SAGA's namespace package instead of java.io.File
 * - allows changing the base directory used for resolving
 */
@SuppressWarnings("unchecked")
public class SagaFileNameCompletor implements Completor {

    private Logger logger = LoggerFactory.getLogger(SagaFileNameCompletor.class);
    private NSDirectory base;

    public SagaFileNameCompletor() {
        base = null;
    }

    public void setBase(NSDirectory base) {
        this.base = base;
    }

    private URL getBaseURL() throws NotImplementedException,
            IncorrectStateException, TimeoutException, NoSuccessException,
            BadParameterException {

        URL baseUrl = base.getURL();
        String basePath = baseUrl.getPath();
        
        if (!basePath.endsWith("/")) {
            basePath += "/";
            baseUrl.setPath(basePath);
        }
        
        return baseUrl;
    }
    
    public int complete(final String buf, final int cursor,
            final List candidates) {
        if (base == null) {
            return -1;
        }

        String buffer = (buf == null) ? "" : buf;

        try {
            if (buffer.endsWith("..")) {
                // add a '/' to complete the only candidate (a directory)
                return completeDotDot(buffer, candidates);
            }
        
            URL baseUrl = getBaseURL();
            URL bufferUrl = URLFactory.createURL(buffer);
            URL resolveUrl = baseUrl.resolve(bufferUrl);
            
            String pattern = "*";
            String resolvePath = resolveUrl.getPath();
            
            if (resolvePath.endsWith("..")) {
                resolvePath += "/";
            }
            
            if (!resolvePath.endsWith("/")) {
                // the name part will act as a pattern
                java.io.File resolveFile = new java.io.File(resolvePath);
                resolvePath = resolveFile.getParent();
                if (resolvePath == null) {
                    resolvePath = "/";
                }
                resolveUrl.setPath(resolvePath);
                pattern = resolveFile.getName() + "*";
            }
            NSDirectory resolveDir = base.openDir(resolveUrl);

            logger.trace("Resolving in " + resolveUrl + " with pattern: " + pattern);
            
            List<URL> matches = resolveDir.find(pattern, Flags.NONE.getValue());
            
            if (matches.size() == 1) {
                // add a trailing '/' is the sole match is a directory
                URL match = matches.get(0);
                if (resolveDir.isDir(match)) {
                    candidates.add(match.toString() + "/");
                } else {
                    candidates.add(match.toString());
                }
            } else {
                // add all matches as-is
                for (URL match: matches) {
                    candidates.add(match.toString());
                }
            }
            
            Collections.sort(candidates);

            return buffer.lastIndexOf("/") + 1; 
            
        } catch (DoesNotExistException e) {
            return -1;
        } catch (SagaException e) {
            logger.debug("Error during file name completion", e);
            return -1;
        }
    }

    private int completeDotDot(String buffer, final List candidates)
            throws SagaException {
        
        if (onlyContainsDotDot(buffer)) {
            // URL always refers to a directory
            candidates.add("/");
            return buffer.length();
        }
        
        // check if URL refers to a directory
        URL bufferUrl = URLFactory.createURL(buffer);
        if (base.isDir(bufferUrl)) {
            candidates.add("/");
            return buffer.length();
        } else {
            // unknown directory; do not complete
            return -1;
        }
    }
    
    private boolean onlyContainsDotDot(String s) {
        String noDotDotSlash = s.replaceAll("\\.\\./", "");
        return noDotDotSlash.isEmpty() || noDotDotSlash.equals("..");
    }

}
