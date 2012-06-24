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
package org.apache.ant.antdsl.expr;

import org.apache.tools.ant.BuildException;

public class IncompatibleTypeException extends BuildException {

    private final String name;

    private final String t1;

    private final String t2;

    public IncompatibleTypeException(String name, String t1, String t2) {
        super("incomptable type for operation " + name + ": '" + t1 + "' and '" + t2 + "'");
        this.name = name;
        this.t1 = t1;
        this.t2 = t2;
    }

    public IncompatibleTypeException switchTypes() {
        return new IncompatibleTypeException(name, t2, t1);
    }
}
