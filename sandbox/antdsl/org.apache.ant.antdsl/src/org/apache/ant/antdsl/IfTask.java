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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Sequential;
import org.apache.tools.ant.taskdefs.condition.Condition;

public class IfTask extends Task {

    public static final class ConditionnalSequential extends Sequential {

        private Condition condition;

        public void setCondition(Condition condition) {
            this.condition = condition;
        }

        public boolean eval() throws BuildException {
            return condition.eval();
        }

    }

    private List<ConditionnalSequential> elseIfs = new ArrayList<ConditionnalSequential>();

    private Sequential elses;

    private ConditionnalSequential main;

    public void setMain(ConditionnalSequential main) {
        this.main = main;
    }

    public void addElseIf(ConditionnalSequential elseif) {
        elseIfs.add(elseif);
    }

    public void setElse(Sequential elses) {
        this.elses = elses;
    }

    public void execute() throws BuildException {
        if (main.eval()) {
            main.execute();
        } else {
            boolean done = false;
            Iterator<ConditionnalSequential> itElseIf = elseIfs.iterator();
            while (!done && itElseIf.hasNext()) {
                ConditionnalSequential elseIf = itElseIf.next();
                if (elseIf.eval()) {
                    done = true;
                    elseIf.execute();
                }
            }

            if (!done && elses != null) {
                elses.execute();
            }
        }
    }

}
