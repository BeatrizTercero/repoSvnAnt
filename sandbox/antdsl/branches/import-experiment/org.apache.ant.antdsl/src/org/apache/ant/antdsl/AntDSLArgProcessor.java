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

import java.io.PrintStream;
import java.util.List;

import org.apache.tools.ant.ArgumentProcessor;
import org.apache.tools.ant.Project;

public class AntDSLArgProcessor implements ArgumentProcessor {

    public int readArguments(String[] args, int pos) {
        if (args[pos].equals("-update-build")) {
            return pos + 1;
        }
        return -1;
    }

    public boolean handleArg(List<String> extraArgs) {
        return false;
    }

    public void prepareConfigure(Project project, List<String> extraArgs) {
        project.addReference(AntPathManager.REFID_UPDATE_BUILD, true);
    }

    public boolean handleArg(Project project, List<String> arg) {
        return true;
    }

    public void printUsage(PrintStream writer) {
        writer.println("  -update-build          launch a resolve of the ant path");
    }

}
