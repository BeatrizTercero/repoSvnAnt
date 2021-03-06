/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.myrmidon.components.configurer;

import java.util.ArrayList;

/**
 * Simple class to test typed adder.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision$ $Date$
 */
public class ConfigTestTypedProp
{
    private ArrayList m_roles = new ArrayList();

    public void add( final MyRole1 role1 )
    {
        m_roles.add( role1 );
    }

    public boolean equals( final Object object )
    {
        final ConfigTestTypedProp other = (ConfigTestTypedProp)object;
        return m_roles.equals( other.m_roles );
    }
}
