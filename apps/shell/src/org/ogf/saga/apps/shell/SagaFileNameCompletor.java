package org.ogf.saga.apps.shell;

import java.util.*;

import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.namespace.NSDirectory;
import org.ogf.saga.namespace.NSFactory;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

import jline.Completor;

/**
 * Customized version of the FileNameCompletor in jline 0.9.94.
 * This version:
 * - uses SAGA's namespace package instead of java.io.File
 * - allows changing the base directory used for resolving
 */
@SuppressWarnings("unchecked")
public class SagaFileNameCompletor implements Completor {

    private URL base;

    public SagaFileNameCompletor() {
        base = null;
    }

    public void setBase(URL base) {
        this.base = base;
    }

    public int complete(final String buf, final int cursor,
            final List candidates) {
        if (base == null) {
            return -1;
        }

        String buffer = (buf == null) ? "" : buf;

        String translated = buffer;

        if (!(translated.startsWith("/"))) {
            String basePath = base.getPath();
            if (basePath.endsWith("/")) {
                translated = base + translated;
            } else {
                translated = base + "/" + translated;
            }
        }

        try {
            URL translatedUrl = URLFactory.createURL(translated);

            URL resolveUrl = URLFactory.createURL(translatedUrl.toString());
            String resolvePath = resolveUrl.getPath();
            if (!resolvePath.endsWith("/")) {
                java.io.File p = new java.io.File(resolvePath);
                String parent = p.getParent();
                if (parent == null || parent.equals("/")) {
                    parent = "/";
                }
                resolveUrl.setPath(parent);
            }

            NSDirectory dir = NSFactory.createNSDirectory(resolveUrl);

            return matchFiles(buffer, translatedUrl, dir, candidates);
        } catch (SagaException e) {
            e.printStackTrace();
            return -1;
        } finally {
            // we want to output a sorted list of files
            sortFileNames(candidates);
        }
    }

    protected void sortFileNames(final List fileNames) {
        Collections.sort(fileNames);
    }

    /**
     *  Match the specified <i>buffer</i> to the array of <i>entries</i>
     *  and enter the matches into the list of <i>candidates</i>. This method
     *  can be overridden in a subclass that wants to do more
     *  sophisticated file name completion.
     *
     *  @param        buffer                the untranslated buffer
     *  @param        translated        the buffer with common characters replaced
     *  @param        entries                the list of files to match
     *  @param        candidates        the list of candidates to populate
     *
     *  @return  the offset of the match
     */
    public int matchFiles(String buffer, URL translatedUrl, NSDirectory dir,
            List candidates) throws SagaException {

        List<URL> entries = (dir == null) ? Collections.EMPTY_LIST : dir.list();

        if (entries == null) {
            return -1;
        }

        String lastName = null;
        URL lastMatch = null;

        for (URL entry : entries) {
            try {
                URL resolved = translatedUrl.resolve(entry);

                if (resolved.toString().startsWith(translatedUrl.toString())) {
                    java.io.File f = new java.io.File(resolved.getPath());
                    lastName = f.getName();
                    candidates.add(lastName);
                    lastMatch = resolved;
                }
            } catch (NoSuccessException e) {
                e.printStackTrace();
            }
        }

        if (candidates.size() == 1) {
            try {
                if (dir.isDir(lastMatch)) {
                    lastName += "/";
                } else {
                    lastName += " ";
                }
                candidates.set(0, lastName);
            } catch (SagaException ignored) {
                ignored.printStackTrace();
            }
        }

        int index = buffer.lastIndexOf("/");

        return index + 1;
    }
}
