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
package org.apache.ant.javafront.helper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.tools.ant.BuildException;

class Target extends org.apache.tools.ant.Target {
    private final Runnable runnable;

    Target(final Method noArgMethod, final Object buildFile) {
        runnable = new Runnable() {
                public void run() {
                    try {
                        noArgMethod.invoke(buildFile);
                    } catch (IllegalAccessException e) {
                        throw new BuildException("failed to invoke noArgMethod"
                                                 + " in " + buildFile, e);
                    } catch (InvocationTargetException e) {
                        if (e.getCause() instanceof BuildException) {
                            throw (BuildException) e.getCause();
                        }
                        throw new BuildException("failed to invoke noArgMethod"
                                                 + " in " + buildFile, e);
                    }
                }
            };
    }

    @Override
    public void execute() {
        runnable.run();
    }
}