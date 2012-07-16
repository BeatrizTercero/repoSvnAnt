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
package org.apache.ant.antdsl.expr.func;

import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

public abstract class AntFunction {

    private final String name;

    public AntFunction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    protected void checkArgumentSize(List<Object> arguments, Integer lower, Integer upper) {
        if (upper == null) {
            if (arguments.size() != lower.intValue()) {
                throw new BuildException("The function '" + getName() + "' is expecting at least " + lower.intValue() + " argument"
                        + (lower.intValue() > 1 ? "s" : "") + " but " + arguments.size() + " were found");
            }
            return;
        }
        if (lower == null || lower.intValue() == 0) {
            if (arguments.size() > upper.intValue()) {
                throw new BuildException("The function '" + getName() + "' is expecting up to " + upper.intValue() + " argument"
                        + (upper.intValue() > 1 ? "s" : "") + " but " + arguments.size() + " were found");
            }
            return;
        }
        if (lower.intValue() == upper.intValue()) {
            if (arguments.size() != lower.intValue()) {
                throw new BuildException("The function '" + getName() + "' is expecting " + lower.intValue() + " argument"
                        + (lower.intValue() > 1 ? "s" : "") + " but " + arguments.size() + " were found");
            }
            return;
        }
        if (arguments.size() < lower.intValue() || arguments.size() > upper.intValue()) {
            throw new BuildException("The function '" + getName() + "' is expecting from " + lower.intValue() + " to " + upper.intValue()
                    + " arguments but " + arguments.size() + " were found");
        }
    }

    abstract public Object eval(Project project, List<Object> arguments);

    protected int asInt(Object arg, int n) {
        if (arg instanceof Integer) {
            return ((Integer) arg).intValue();
        }
        if (arg instanceof Long) {
            return ((Long) arg).intValue();
        }
        if (arg instanceof Float) {
            return ((Float) arg).intValue();
        }
        if (arg instanceof Double) {
            return ((Double) arg).intValue();
        }
        throw buildWrongTypeException("int", arg, n);
    }

    protected BuildException buildWrongTypeException(String type, Object arg, int n) {
        return new BuildException("The function '" + name + "' is expecting a " + type + " as argument #" + n + " but " + arg.getClass().getName()
                + " was found");
    }

}
