/*
 * Copyright  2001-2002,2004,2006 The Apache Software Foundation
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
package org.apache.tools.ant.taskdefs.optional.net;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.email.EmailTask;

/**
 * A task to send SMTP email; Use <tt>mail</tt> instead
 *
 * @deprecated since 1.6.x. 
 *             Use {@link EmailTask} instead.
 *
 * @since Ant1.4
 */
public class MimeMail extends EmailTask {
    /**
     * Executes this build task.
     *
     * @exception BuildException On error.
     */
    public void execute()
        throws BuildException {
        log("DEPRECATED - The " + getTaskName() + " task is deprecated. "
            + "Use the mail task instead.");
        super.execute();
    }
}
