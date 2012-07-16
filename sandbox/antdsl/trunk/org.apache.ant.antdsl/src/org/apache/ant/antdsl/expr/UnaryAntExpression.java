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

public abstract class UnaryAntExpression extends AntExpression {

    private final String name;

    private AntExpression expr;

    public UnaryAntExpression(String name) {
        this.name = name;
    }

    public void setExpr(AntExpression expr) {
        this.expr = expr;
    }

    protected BuildException buildUnexpectedTypeException(String type) {
        return new BuildException("unexpected type for operation " + name + ": '" + type + "'");
    }

    @Override
    final public Object eval() {
        return eval(expr.eval());
    }

    private Object eval(Object v) {
        if (v instanceof String) {
            return eval((String) v);
        }
        if (v instanceof Boolean) {
            return eval(((Boolean) v).booleanValue());
        }
        if (v instanceof Byte) {
            return eval(((Byte) v).byteValue());
        }
        if (v instanceof Short) {
            return eval(((Short) v).shortValue());
        }
        if (v instanceof Integer) {
            return eval(((Integer) v).intValue());
        }
        if (v instanceof Long) {
            return eval(((Long) v).longValue());
        }
        if (v instanceof Float) {
            return eval(((Float) v).floatValue());
        }
        if (v instanceof Double) {
            return eval(((Double) v).doubleValue());
        }
        return eval(v.toString());
    }

    abstract protected Object eval(String v);

    abstract protected Object eval(boolean v);

    abstract protected Object eval(byte v);

    abstract protected Object eval(short v);

    abstract protected Object eval(int v);

    abstract protected Object eval(long v);

    abstract protected Object eval(float v);

    abstract protected Object eval(double v);

}
