/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
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
package org.apache.ant.start;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import org.apache.ant.init.InitConfig;

/**
 * This is the main startup class for the command line interface of Ant. It
 * establishes the classloaders used by the other components of Ant.
 *
 * @author Conor MacNeill
 * @created 9 January 2002
 */
public class Main {
    /** The actual class that implements the command line front end. */
    public static final String COMMANDLINE_CLASS
         = "org.apache.ant.cli.Commandline";

         
    /** The default front end name */
    public static final String DEFAULT_FRONTEND = "cli";
         
    /**
     * Entry point for starting command line Ant
     *
     * @param args commandline arguments
     */
    public static void main(String[] args) {
        Main main = new Main();
        main.start(DEFAULT_FRONTEND, args);
    }

    /**
     * Internal start method used to initialise front end
     *
     * @param frontend the frontend jar to launch
     * @param args commandline arguments
     */
    public void start(String frontend, String[] args) {
        try {
            InitConfig config = new InitConfig();

            URL frontendJar = new URL(config.getLibraryURL(), 
                "frontend/" + frontend + ".jar");
            URL[] frontendJars = new URL[]{frontendJar};
            ClassLoader frontEndLoader 
                = new URLClassLoader(frontendJars, config.getCoreLoader());

            //System.out.println("Front End Loader config");
            //LoaderUtils.dumpLoader(System.out, frontEndLoader);

            // Now start the front end by reflection.
            Class commandLineClass = Class.forName(COMMANDLINE_CLASS, true,
                frontEndLoader);

            final Class[] param = {Class.forName("[Ljava.lang.String;"),
                InitConfig.class};
            final Method startMethod
                 = commandLineClass.getMethod("start", param);
            final Object[] argument = {args, config};
            startMethod.invoke(null, argument);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

