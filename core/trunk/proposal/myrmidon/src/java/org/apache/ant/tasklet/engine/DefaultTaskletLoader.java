/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.ant.tasklet.engine;

import java.net.URL;
import org.apache.ant.tasklet.Tasklet;
import org.apache.ant.convert.DefaultConverterLoader;

/**
 * Class used to load tasks et al from a source.
 * 
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultTaskletLoader
    extends DefaultConverterLoader
    implements TaskletLoader
{
    public DefaultTaskletLoader()
    {
    }

    public DefaultTaskletLoader( final URL location )
    {
        super( location );
    }

    public Tasklet loadTasklet( final String tasklet )
        throws Exception
    {
        return (Tasklet)load( tasklet );
    }
}
