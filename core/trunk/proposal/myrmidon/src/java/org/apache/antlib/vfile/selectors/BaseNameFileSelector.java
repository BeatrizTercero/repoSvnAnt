/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.antlib.vfile.selectors;

import org.apache.aut.vfs.FileObject;

/**
 * A file selector that selects files based on their base-name.
 *
 * @author <a href="mailto:adammurdoch@apache.org">Adam Murdoch</a>
 * @version $Revision$ $Date$
 *
 * @ant:data-type name="basename-selector"
 * @ant:type type="v-file-selector" name="basename"
 */
public class BaseNameFileSelector
    extends AbstractNameFileSelector
{
    /**
     * Returns the name to match against.
     */
    protected String getNameForMatch( final String path,
                                      final FileObject file )
    {
        return file.getName().getBaseName();
    }
}
