/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.ant.tasklet.engine;

import org.apache.avalon.camelot.AbstractRegistry;

public class DefaultTaskletRegistry
    extends AbstractRegistry
    implements TaskletRegistry
{
    protected Class getInfoClass()
    {
        return TaskletInfo.class;
    }
}
