/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.ant.groovyfront.cache;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Resource;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;

public class GroovyScriptLoader {

    public Script loadScript(Resource r, Binding binding, ClassLoader parent) {
        return parseScript(r, binding, parent, null);
    }

    /**
     * Parse and compile the build file. If the provided scriptCache is not
     * <code>null</code>, then the resulting classes will be put in that folder
     * for later reloading.
     * 
     * @param r
     * @param binding
     * @param classLoader
     * @param scriptCache
     * @return
     */
    protected Script parseScript(Resource r, Binding binding,
            ClassLoader classLoader, File scriptCache) {
        CompilerConfiguration config = new CompilerConfiguration(
                CompilerConfiguration.DEFAULT);
        config.setTargetDirectory(scriptCache);
        GroovyShell groovyShell = new GroovyShell(classLoader, binding, config);
        InputStream in;
        try {
            in = r.getInputStream();
        } catch (IOException e) {
            throw new BuildException("Error reading groovy file " + r + ": "
                    + e.getMessage(), e);
        }
        try {
            return groovyShell.parse(new InputStreamReader(in),
                    asGroovyClass(r.getName()));
        } catch (CompilationFailedException e) {
            throw new BuildException("Error reading groovy file " + r + ": "
                    + e.getMessage(), e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                // don't care
            }
        }
    }

    /**
     * Turn the build file name into a compatible Groovy file name
     * 
     * @param filename
     * @return
     */
    protected String asGroovyClass(String filename) {
        return filename.replaceAll("-", "_");
    }

}
