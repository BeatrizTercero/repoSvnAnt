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

import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.BuildException;

// TODO
// we may want to make this registry more integrated with ant class loading, like typedef
public class FunctionRegistry {

    private Map<String, AntFunction> registry = new HashMap<String, AntFunction>();

    public FunctionRegistry() {
        // for now, let's hard code the available functions
        register(new EvalAntFunction());
        register(new SubStringAntFunction());
        register(new IndexOfAntFunction());
        register(new ParseIntAntFunction());
        register(new ParseLongAntFunction());
        register(new ParseFloatAntFunction());
        register(new ParseDoubleAntFunction());
    }

    private void register(AntFunction func) {
        registry.put(func.getName(), func);
    }

    public void register(String name, Class< ? extends AntFunction> c) {
        AntFunction func;
        try {
            func = c.newInstance();
        } catch (Exception e) {
            throw new BuildException("Unable to register the function '" + name + "' with '" + c.getName() + "': " + e.getMessage() + "("
                    + e.getClass().getName() + ")", e);
        }
        registry.put(name, func);
    }

    public AntFunction getFunction(String name) {
        return registry.get(name);
    }
}
