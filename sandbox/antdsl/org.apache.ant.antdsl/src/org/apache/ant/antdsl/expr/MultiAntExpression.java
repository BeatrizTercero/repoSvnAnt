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

import org.apache.tools.ant.BuildException;

public abstract class MultiAntExpression extends AntExpression {

    private List<AntExpression> children = new ArrayList<AntExpression>();

    private final String name;

    public MultiAntExpression(String name) {
        this.name = name;
    }

    public void add(AntExpression expr) {
        children.add(expr);
    }

    public List<AntExpression> getChildren() {
        return children;
    }

    @Override
    public Object eval() {
        Object value = null;
        for (AntExpression child : getChildren()) {
            Object childValue = child.eval();
            if (value == null) {
                value = childValue;
            } else if (value instanceof Integer) {
                value = eval((Integer) value, childValue);
            } else if (value instanceof Long) {
                value = eval((Long) value, childValue);
            } else if (value instanceof Float) {
                value = eval((Float) value, childValue);
            } else if (value instanceof Double) {
                value = eval((Double) value, childValue);
            } else {
                value = eval(value.toString(), childValue);
            }
        }
        return value;
    }

    protected BuildException buildIncompatibleTypeException(String t1, String t2) {
        return new BuildException("incomptable type for operation " + name + ": '" + t1 + "' and '" + t2 + "'");
    }

    private Object eval(String v1, Object v2) {
        if (v2 instanceof String) {
            return eval(v1, ((String) v2));
        }
        if (v2 instanceof Integer) {
            return eval(v1, ((Integer) v2).intValue());
        }
        if (v2 instanceof Long) {
            return eval(v1, ((Long) v2).longValue());
        }
        if (v2 instanceof Float) {
            return eval(v1, ((Float) v2).floatValue());
        }
        if (v2 instanceof Double) {
            return eval(v1, ((Double) v2).doubleValue());
        }
        return eval(v1, v2.toString());
    }

    abstract protected Object eval(String v1, String v2);

    abstract protected Object eval(String v1, int v2);

    abstract protected Object eval(String v1, long v2);

    abstract protected Object eval(String v1, float v2);

    abstract protected Object eval(String v1, double v2);

    private Object eval(Integer v1, Object v2) {
        if (v2 instanceof String) {
            return eval(v1.intValue(), ((String) v2));
        }
        if (v2 instanceof Integer) {
            return eval(v1.intValue(), ((Integer) v2).intValue());
        }
        if (v2 instanceof Long) {
            return eval(v1.intValue(), ((Long) v2).longValue());
        }
        if (v2 instanceof Float) {
            return eval(v1.intValue(), ((Float) v2).floatValue());
        }
        if (v2 instanceof Double) {
            return eval(v1.intValue(), ((Double) v2).doubleValue());
        }
        return eval(v1.intValue(), v2.toString());
    }

    abstract protected Object eval(int v1, String v2);

    abstract protected Object eval(int v1, int v2);

    abstract protected Object eval(int v1, long v2);

    abstract protected Object eval(int v1, float v2);

    abstract protected Object eval(int v1, double v2);

    private Object eval(Long v1, Object v2) {
        if (v2 instanceof String) {
            return eval(v1.longValue(), ((String) v2));
        }
        if (v2 instanceof Integer) {
            return eval(v1.longValue(), ((Integer) v2).intValue());
        }
        if (v2 instanceof Long) {
            return eval(v1.longValue(), ((Long) v2).longValue());
        }
        if (v2 instanceof Float) {
            return eval(v1.longValue(), ((Float) v2).floatValue());
        }
        if (v2 instanceof Double) {
            return eval(v1.longValue(), ((Double) v2).doubleValue());
        }
        return eval(v1.longValue(), v2.toString());
    }

    abstract protected Object eval(long v1, String v2);

    abstract protected Object eval(long v1, int v2);

    abstract protected Object eval(long v1, long v2);

    abstract protected Object eval(long v1, float v2);

    abstract protected Object eval(long v1, double v2);

    private Object eval(Float v1, Object v2) {
        if (v2 instanceof String) {
            return eval(v1.floatValue(), ((String) v2));
        }
        if (v2 instanceof Integer) {
            return eval(v1.floatValue(), ((Integer) v2).intValue());
        }
        if (v2 instanceof Long) {
            return eval(v1.floatValue(), ((Long) v2).longValue());
        }
        if (v2 instanceof Float) {
            return eval(v1.floatValue(), ((Float) v2).floatValue());
        }
        if (v2 instanceof Double) {
            return eval(v1.floatValue(), ((Double) v2).doubleValue());
        }
        return eval(v1.floatValue(), v2.toString());
    }

    abstract protected Object eval(float v1, String v2);

    abstract protected Object eval(float v1, int v2);

    abstract protected Object eval(float v1, long v2);

    abstract protected Object eval(float v1, float v2);

    abstract protected Object eval(float v1, double v2);

    private Object eval(Double v1, Object v2) {
        if (v2 instanceof String) {
            return eval(v1.doubleValue(), ((String) v2));
        }
        if (v2 instanceof Integer) {
            return eval(v1.doubleValue(), ((Integer) v2).intValue());
        }
        if (v2 instanceof Long) {
            return eval(v1.doubleValue(), ((Long) v2).longValue());
        }
        if (v2 instanceof Float) {
            return eval(v1.doubleValue(), ((Float) v2).floatValue());
        }
        if (v2 instanceof Double) {
            return eval(v1.doubleValue(), ((Double) v2).doubleValue());
        }
        return eval(v1.doubleValue(), v2.toString());
    }

    abstract protected Object eval(double v1, String v2);

    abstract protected Object eval(double v1, int v2);

    abstract protected Object eval(double v1, long v2);

    abstract protected Object eval(double v1, float v2);

    abstract protected Object eval(double v1, double v2);

}
