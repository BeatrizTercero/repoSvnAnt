/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.myrmidon.components.configurer.test.data;

import java.util.List;
import java.util.ArrayList;
import org.apache.myrmidon.components.configurer.test.DefaultConfigurerTestCase;

/**
 * Simple class to test setter.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision$ $Date$
 */
public class ConfigTestSetAttribute
{
    private String m_someProp;

    public boolean equals( final Object obj )
    {
        final ConfigTestSetAttribute test = (ConfigTestSetAttribute)obj;
        if( !DefaultConfigurerTestCase.equals( m_someProp, test.m_someProp ) )
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public void setSomeProp( final String value )
    {
        m_someProp = value;
    }
}
