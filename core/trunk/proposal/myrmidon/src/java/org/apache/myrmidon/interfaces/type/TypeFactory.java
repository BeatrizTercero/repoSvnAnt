/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.myrmidon.interfaces.type;

/**
 * Create an instance on name.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version CVS $Revision$ $Date$
 */
public interface TypeFactory
{
    /**
     * Create a type instance based on name.
     *
     * @param name the name
     * @return the type instance
     * @exception TypeException if an error occurs
     */
    Object create( String name )
        throws TypeException;
}
