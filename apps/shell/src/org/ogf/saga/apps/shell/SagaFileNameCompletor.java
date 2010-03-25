package org.ogf.saga.apps.shell;

import java.util.Collections;
import java.util.List;

import org.ogf.saga.error.SagaException;
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

    public int complete(final String buf, final int cursor,
            final List candidates) {
        if (base == null) {
            return -1;
        }

        if (buf != null && buf.endsWith("..")) {
            candidates.add("/");
            return buf.length();
        }
        
        String buffer = (buf == null) ? "" : buf;
        
        try {
            URL baseUrl = base.getURL();
            String basePath = baseUrl.getPath();
            if (!basePath.endsWith("/")) {
                basePath += "/";
                baseUrl.setPath(basePath);
            }
            
            URL bufferUrl = URLFactory.createURL(buffer);
            URL resolveUrl = baseUrl.resolve(bufferUrl);
            
            String pattern = "*";
            String resolvePath = resolveUrl.getPath();
            
            if (resolvePath.endsWith("..")) {
                buffer += "/";
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
            
        } catch (SagaException e) {
            logger.debug("Error during file name completion", e);
            return -1;
        }
    }

}
