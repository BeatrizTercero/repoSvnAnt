/*
 * Copyright 2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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

package org.apache.tools.ant.helper;


import org.apache.tools.ant.Project;
import org.apache.tools.ant.Executor;
import org.apache.tools.ant.BuildException;


/**
 * "Keep-going" Target executor implementation.
 * Differs from {@link DefaultExecutor} in that a failure in one target does
 * not halt execution; all targets are attempted, in order. The last failure,
 * if any, is reported to the caller.
 * @since Ant 1.6.3
 */
public class KeepGoingExecutor implements Executor {

    //inherit doc
    public void executeTargets(Project project, String[] targetNames)
        throws BuildException {
        BuildException thrownException = null;
        for (int i = 0; i < targetNames.length; i++) {
            try {
                project.executeTarget(targetNames[i]);
            } catch (BuildException ex) {
                thrownException = ex;
            }
        }
        if (thrownException != null) {
            throw thrownException;
        }
    }

}
