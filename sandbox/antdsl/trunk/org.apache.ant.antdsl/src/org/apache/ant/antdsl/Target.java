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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.condition.Condition;

public class Target extends org.apache.tools.ant.Target {

    private Condition if_;

    private Condition unless;

    public void setIf(Condition if_) {
        this.if_ = if_;
    }

    public void setUnless(Condition unless) {
        this.unless = unless;
    }

    @Override
    public void execute() throws BuildException {
        if (if_ != null) {
            if (!if_.eval()) {
                getProject().log(this, "Skipped because if condition evaluate to false.", Project.MSG_VERBOSE);
                return;
            }
        }
        if (unless != null) {
            if (unless.eval()) {
                getProject().log(this, "Skipped because unless condition evaluate to true.", Project.MSG_VERBOSE);
                return;
            }
        }
        super.execute();
    }
}
