/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.tools.ant.taskdefs.rmic;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;


/**
 * Creates the necessary rmic adapter, given basic criteria.
 *
 * @author <a href="mailto:tokamoto@rd.nttdata.co.jp">Takashi Okamoto</a>
 * @author <a href="mailto:jayglanville@home.com">J D Glanville</a>
 */
public class RmicAdapterFactory
{

    /**
     * This is a singlton -- can't create instances!!
     */
    private RmicAdapterFactory() { }

    /**
     * Based on the parameter passed in, this method creates the necessary
     * factory desired. The current mapping for rmic names are as follows:
     * <ul>
     *   <li> sun = SUN's rmic
     *   <li> kaffe = Kaffe's rmic
     *   <li> <i>a fully quallified classname</i> = the name of a rmic adapter
     *
     * </ul>
     *
     *
     * @param rmicType either the name of the desired rmic, or the full
     *      classname of the rmic's adapter.
     * @param task a task to log through.
     * @return The Rmic value
     * @throws BuildException if the rmic type could not be resolved into a rmic
     *      adapter.
     */
    public static RmicAdapter getRmic( String rmicType, Task task )
        throws BuildException
    {
        if( rmicType == null )
        {
            /*
             * When not specified rmicType, search SUN's rmic and
             * Kaffe's rmic.
             */
            try
            {
                Class.forName( "sun.rmi.rmic.Main" );
                rmicType = "sun";
            }
            catch( ClassNotFoundException cnfe )
            {
                try
                {
                    Class.forName( "kaffe.rmi.rmic.RMIC" );
                    Class.forName( "kaffe.tools.compiler.Compiler" );
                    rmicType = "kaffe";
                }
                catch( ClassNotFoundException cnfk )
                {
                    throw new BuildException( "Couldn\'t guess rmic implementation" );
                }
            }
        }

        if( rmicType.equalsIgnoreCase( "sun" ) )
        {
            return new SunRmic();
        }
        else if( rmicType.equalsIgnoreCase( "kaffe" ) )
        {
            return new KaffeRmic();
        }
        else if( rmicType.equalsIgnoreCase( "weblogic" ) )
        {
            return new WLRmic();
        }
        return resolveClassName( rmicType );
    }

    /**
     * Tries to resolve the given classname into a rmic adapter. Throws a fit if
     * it can't.
     *
     * @param className The fully qualified classname to be created.
     * @return Description of the Returned Value
     * @throws BuildException This is the fit that is thrown if className isn't
     *      an instance of RmicAdapter.
     */
    private static RmicAdapter resolveClassName( String className )
        throws BuildException
    {
        try
        {
            Class c = Class.forName( className );
            Object o = c.newInstance();
            return ( RmicAdapter )o;
        }
        catch( ClassNotFoundException cnfe )
        {
            throw new BuildException( className + " can\'t be found.", cnfe );
        }
        catch( ClassCastException cce )
        {
            throw new BuildException( className + " isn\'t the classname of "
                 + "a rmic adapter.", cce );
        }
        catch( Throwable t )
        {
            // for all other possibilities
            throw new BuildException( className + " caused an interesting "
                 + "exception.", t );
        }
    }
}
