/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Ant", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.tools.ant.taskdefs.rmic;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;


/**
 * Creates the necessary rmic adapter, given basic criteria.
 *
 * @author Takashi Okamoto <tokamoto@rd.nttdata.co.jp>
 * @author <a href="mailto:jayglanville@home.com">J D Glanville</a>
 */
public class RmicAdapterFactory {

    /** This is a singlton -- can't create instances!! */
    private RmicAdapterFactory() {
    }

    /**
     * Based on the parameter passed in, this method creates the necessary
     * factory desired.
     *
     * The current mapping for rmic names are as follows:
     * <ul><li>sun = SUN's rmic
     * <li>kaffe = Kaffe's rmic
     * <li><i>a fully quallified classname</i> = the name of a rmic
     * adapter
     * </ul>
     *
     * @param rmicType either the name of the desired rmic, or the
     * full classname of the rmic's adapter.
     * @param task a task to log through.
     * @throws BuildException if the rmic type could not be resolved into
     * a rmic adapter.
     */
    public static RmicAdapter getRmic( String rmicType, Task task ) 
        throws BuildException {
        if( rmicType == null){
            /* 
             * When not specified rmicType, search SUN's rmic and
             * Kaffe's rmic.
             */
            try {
                Class.forName("sun.rmi.rmic.Main");
                rmicType = "sun";
            } catch (ClassNotFoundException cnfe) {
                try {
                    Class.forName("kaffe.rmi.rmic.RMIC");
                    Class.forName("kaffe.tools.compiler.Compiler");
                    rmicType = "kaffe";
                } catch (ClassNotFoundException cnfk) {
                    throw new BuildException("Couldn\'t guess rmic implementation");
                }
            }
        }

        if ( rmicType.equalsIgnoreCase("sun") ) {
            return new SunRmic();
        } else if ( rmicType.equalsIgnoreCase("kaffe") ) {
            return new KaffeRmic();
        } else if ( rmicType.equalsIgnoreCase("weblogic") ) {
            return new WLRmic();
        }
        return resolveClassName( rmicType );
    }

    /**
     * Tries to resolve the given classname into a rmic adapter.
     * Throws a fit if it can't.
     *
     * @param className The fully qualified classname to be created.
     * @throws BuildException This is the fit that is thrown if className
     * isn't an instance of RmicAdapter.
     */
    private static RmicAdapter resolveClassName( String className )
        throws BuildException {
        try {
            Class c = Class.forName( className );
            Object o = c.newInstance();
            return (RmicAdapter) o;
        } catch ( ClassNotFoundException cnfe ) {
            throw new BuildException( className + " can\'t be found.", cnfe );
        } catch ( ClassCastException cce ) {
            throw new BuildException(className + " isn\'t the classname of "
                                     + "a rmic adapter.", cce);
        } catch ( Throwable t ) {
            // for all other possibilities
            throw new BuildException(className + " caused an interesting "
                                     + "exception.", t);
        }
    }
}
