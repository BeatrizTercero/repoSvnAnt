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

import java.util.ArrayList;
import java.util.List;

import org.apache.ant.antdsl.AbstractAntDslProjectHelper;
import org.apache.ant.antdsl.expr.func.AntFunction;
import org.apache.ant.antdsl.expr.func.FunctionRegistry;
import org.apache.tools.ant.BuildException;

public class FuncAntExpression extends AntExpression {

    private String name;

    private List<AntExpression> arguments = new ArrayList<AntExpression>();

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addArgument(AntExpression expr) {
        arguments.add(expr);
    }

    public List<AntExpression> getArguments() {
        return arguments;
    }

    @Override
    public Object eval() {
        FunctionRegistry functionRegistry = (FunctionRegistry) getProject().getReference(AbstractAntDslProjectHelper.REFID_FUNCTION_REGISTRY);
        AntFunction func = functionRegistry.getFunction(name);
        if (func == null) {
            throw new BuildException("Unknown function '" + name + "'");
        }
        List<Object> argValues = new ArrayList<Object>(arguments.size());
        for (AntExpression arg : arguments) {
            argValues.add(arg.eval());
        }
        return func.eval(getProject(), argValues);
    }
}
