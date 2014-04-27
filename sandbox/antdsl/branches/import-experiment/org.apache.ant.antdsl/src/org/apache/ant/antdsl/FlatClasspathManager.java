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
package org.apache.ant.antdsl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.tools.ant.BuildException;

class FlatClasspathManager extends ClasspathManager {

    private URLClassLoader classloader;

    @Override
    public void start(boolean update, List<File> antPath) {
        URL[] urls = new URL[antPath.size()];
        int i = 0;
        for(File file : antPath) {
            try {
                urls[i++] = file.getAbsoluteFile().toURI().toURL();
            } catch (MalformedURLException e) {
                throw new BuildException("Unexpected malformed url to file resolved by Ivy: " + file, e);
            }
        }
        classloader = new URLClassLoader(urls);
    }

    @Override
    public ClassLoader getMainClassLoader() {
        return classloader;
    }

    @Override
    public ClassLoader getClassLoader(String buildModule, URL buildUrl) {
        return classloader;
    }

}
