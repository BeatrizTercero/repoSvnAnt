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

public abstract class PrimitiveBinaryAntExpression extends BinaryAntExpression {

    private final String name;

    public PrimitiveBinaryAntExpression(String name) {
        this.name = name;
    }

    protected BuildException buildIncompatibleTypeException(String t1, String t2) {
        return new IncompatibleTypeException(name, t1, t2);
    }

    @Override
    final protected Object eval(Object v1, Object v2) {
        if (v1 instanceof String) {
            return eval((String) v1, v2);
        } else if (v1 instanceof Boolean) {
            return eval((Boolean) v1, v2);
        } else if (v1 instanceof Byte) {
            return eval((Byte) v1, v2);
        } else if (v1 instanceof Short) {
            return eval((Short) v1, v2);
        } else if (v1 instanceof Integer) {
            return eval((Integer) v1, v2);
        } else if (v1 instanceof Long) {
            return eval((Long) v1, v2);
        } else if (v1 instanceof Float) {
            return eval((Float) v1, v2);
        } else if (v1 instanceof Double) {
            return eval((Double) v1, v2);
        } else {
            return eval(v1.toString(), v2);
        }
    }

    private Object eval(String v1, Object v2) {
        if (v2 instanceof String) {
            return eval(v1, (String) v2);
        }
        if (v2 instanceof Boolean) {
            return eval(v1, ((Boolean) v2).booleanValue());
        }
        if (v2 instanceof Byte) {
            return eval(v1, ((Byte) v2).byteValue());
        }
        if (v2 instanceof Short) {
            return eval(v1, ((Short) v2).shortValue());
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

    abstract protected Object eval(String v1, boolean v2);

    abstract protected Object eval(String v1, byte v2);

    abstract protected Object eval(String v1, short v2);

    abstract protected Object eval(String v1, int v2);

    abstract protected Object eval(String v1, long v2);

    abstract protected Object eval(String v1, float v2);

    abstract protected Object eval(String v1, double v2);

    private Object eval(Boolean v1, Object v2) {
        if (v2 instanceof String) {
            return eval(v1.booleanValue(), (String) v2);
        }
        if (v2 instanceof Boolean) {
            return eval(v1.booleanValue(), ((Boolean) v2).booleanValue());
        }
        if (v2 instanceof Byte) {
            return eval(v1.booleanValue(), ((Byte) v2).byteValue());
        }
        if (v2 instanceof Short) {
            return eval(v1.booleanValue(), ((Short) v2).shortValue());
        }
        if (v2 instanceof Integer) {
            return eval(v1.booleanValue(), ((Integer) v2).intValue());
        }
        if (v2 instanceof Long) {
            return eval(v1.booleanValue(), ((Long) v2).longValue());
        }
        if (v2 instanceof Float) {
            return eval(v1.booleanValue(), ((Float) v2).floatValue());
        }
        if (v2 instanceof Double) {
            return eval(v1.booleanValue(), ((Double) v2).doubleValue());
        }
        return eval(v1.booleanValue(), v2.toString());
    }

    abstract protected Object eval(boolean v1, String v2);

    abstract protected Object eval(boolean v1, boolean v2);

    abstract protected Object eval(boolean v1, byte v2);

    abstract protected Object eval(boolean v1, short v2);

    abstract protected Object eval(boolean v1, int v2);

    abstract protected Object eval(boolean v1, long v2);

    abstract protected Object eval(boolean v1, float v2);

    abstract protected Object eval(boolean v1, double v2);

    private Object eval(Byte v1, Object v2) {
        if (v2 instanceof String) {
            return eval(v1.byteValue(), (String) v2);
        }
        if (v2 instanceof Boolean) {
            return eval(v1.byteValue(), ((Boolean) v2).booleanValue());
        }
        if (v2 instanceof Byte) {
            return eval(v1.byteValue(), ((Byte) v2).byteValue());
        }
        if (v2 instanceof Short) {
            return eval(v1.byteValue(), ((Short) v2).shortValue());
        }
        if (v2 instanceof Integer) {
            return eval(v1.byteValue(), ((Integer) v2).intValue());
        }
        if (v2 instanceof Long) {
            return eval(v1.byteValue(), ((Long) v2).longValue());
        }
        if (v2 instanceof Float) {
            return eval(v1.byteValue(), ((Float) v2).floatValue());
        }
        if (v2 instanceof Double) {
            return eval(v1.byteValue(), ((Double) v2).doubleValue());
        }
        return eval(v1.byteValue(), v2.toString());
    }

    abstract protected Object eval(byte v1, String v2);

    abstract protected Object eval(byte v1, boolean v2);

    abstract protected Object eval(byte v1, byte v2);

    abstract protected Object eval(byte v1, short v2);

    abstract protected Object eval(byte v1, int v2);

    abstract protected Object eval(byte v1, long v2);

    abstract protected Object eval(byte v1, float v2);

    abstract protected Object eval(byte v1, double v2);

    private Object eval(Short v1, Object v2) {
        if (v2 instanceof String) {
            return eval(v1.shortValue(), (String) v2);
        }
        if (v2 instanceof Boolean) {
            return eval(v1.shortValue(), ((Boolean) v2).booleanValue());
        }
        if (v2 instanceof Byte) {
            return eval(v1.shortValue(), ((Byte) v2).byteValue());
        }
        if (v2 instanceof Short) {
            return eval(v1.shortValue(), ((Short) v2).shortValue());
        }
        if (v2 instanceof Integer) {
            return eval(v1.shortValue(), ((Integer) v2).intValue());
        }
        if (v2 instanceof Long) {
            return eval(v1.shortValue(), ((Long) v2).longValue());
        }
        if (v2 instanceof Float) {
            return eval(v1.shortValue(), ((Float) v2).floatValue());
        }
        if (v2 instanceof Double) {
            return eval(v1.shortValue(), ((Double) v2).doubleValue());
        }
        return eval(v1.shortValue(), v2.toString());
    }

    abstract protected Object eval(short v1, String v2);

    abstract protected Object eval(short v1, boolean v2);

    abstract protected Object eval(short v1, byte v2);

    abstract protected Object eval(short v1, short v2);

    abstract protected Object eval(short v1, int v2);

    abstract protected Object eval(short v1, long v2);

    abstract protected Object eval(short v1, float v2);

    abstract protected Object eval(short v1, double v2);

    private Object eval(Integer v1, Object v2) {
        if (v2 instanceof String) {
            return eval(v1.intValue(), (String) v2);
        }
        if (v2 instanceof Boolean) {
            return eval(v1.intValue(), ((Boolean) v2).booleanValue());
        }
        if (v2 instanceof Byte) {
            return eval(v1.intValue(), ((Byte) v2).byteValue());
        }
        if (v2 instanceof Short) {
            return eval(v1.intValue(), ((Short) v2).shortValue());
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

    abstract protected Object eval(int v1, boolean v2);

    abstract protected Object eval(int v1, byte v2);

    abstract protected Object eval(int v1, short v2);

    abstract protected Object eval(int v1, int v2);

    abstract protected Object eval(int v1, long v2);

    abstract protected Object eval(int v1, float v2);

    abstract protected Object eval(int v1, double v2);

    private Object eval(Long v1, Object v2) {
        if (v2 instanceof String) {
            return eval(v1.longValue(), (String) v2);
        }
        if (v2 instanceof Boolean) {
            return eval(v1.longValue(), ((Boolean) v2).booleanValue());
        }
        if (v2 instanceof Byte) {
            return eval(v1.longValue(), ((Byte) v2).byteValue());
        }
        if (v2 instanceof Short) {
            return eval(v1.longValue(), ((Short) v2).shortValue());
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

    abstract protected Object eval(long v1, boolean v2);

    abstract protected Object eval(long v1, byte v2);

    abstract protected Object eval(long v1, short v2);

    abstract protected Object eval(long v1, int v2);

    abstract protected Object eval(long v1, long v2);

    abstract protected Object eval(long v1, float v2);

    abstract protected Object eval(long v1, double v2);

    private Object eval(Float v1, Object v2) {
        if (v2 instanceof String) {
            return eval(v1.floatValue(), (String) v2);
        }
        if (v2 instanceof Boolean) {
            return eval(v1.floatValue(), ((Boolean) v2).booleanValue());
        }
        if (v2 instanceof Byte) {
            return eval(v1.floatValue(), ((Byte) v2).byteValue());
        }
        if (v2 instanceof Short) {
            return eval(v1.floatValue(), ((Short) v2).shortValue());
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

    abstract protected Object eval(float v1, boolean v2);

    abstract protected Object eval(float v1, byte v2);

    abstract protected Object eval(float v1, short v2);

    abstract protected Object eval(float v1, int v2);

    abstract protected Object eval(float v1, long v2);

    abstract protected Object eval(float v1, float v2);

    abstract protected Object eval(float v1, double v2);

    private Object eval(Double v1, Object v2) {
        if (v2 instanceof String) {
            return eval(v1.doubleValue(), (String) v2);
        }
        if (v2 instanceof Boolean) {
            return eval(v1.doubleValue(), ((Boolean) v2).booleanValue());
        }
        if (v2 instanceof Byte) {
            return eval(v1.doubleValue(), ((Byte) v2).byteValue());
        }
        if (v2 instanceof Short) {
            return eval(v1.doubleValue(), ((Short) v2).shortValue());
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

    abstract protected Object eval(double v1, boolean v2);

    abstract protected Object eval(double v1, byte v2);

    abstract protected Object eval(double v1, short v2);

    abstract protected Object eval(double v1, int v2);

    abstract protected Object eval(double v1, long v2);

    abstract protected Object eval(double v1, float v2);

    abstract protected Object eval(double v1, double v2);

}
