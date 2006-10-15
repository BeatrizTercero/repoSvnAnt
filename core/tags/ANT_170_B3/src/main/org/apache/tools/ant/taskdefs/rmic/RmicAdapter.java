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

package org.apache.tools.ant.taskdefs.rmic;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Rmic;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileNameMapper;

/**
 * The interface that all rmic adapters must adhere to.
 *
 * <p>A rmic adapter is an adapter that interprets the rmic's
 * parameters in preperation to be passed off to the compiler this
 * adapter represents.  As all the necessary values are stored in the
 * Rmic task itself, the only thing all adapters need is the rmic
 * task, the execute command and a parameterless constructor (for
 * reflection).</p>
 *
 * @since Ant 1.4
 */

public interface RmicAdapter {

    /**
     * Sets the rmic attributes, which are stored in the Rmic task.
     * @param attributes the rmic attributes to use
     */
    void setRmic(Rmic attributes);

    /**
     * Executes the task.
     *
     * @return has the compilation been successful
     * @throws BuildException on error
     */
    boolean execute() throws BuildException;

    /**
     * Maps source class files to the files generated by this rmic
     * implementation.
     * @return the filename mapper used by this implementation
     */
    FileNameMapper getMapper();

    /**
     * The CLASSPATH this rmic process will use.
     * @return the classpaht this rmic process will use
     */
    Path getClasspath();
}
