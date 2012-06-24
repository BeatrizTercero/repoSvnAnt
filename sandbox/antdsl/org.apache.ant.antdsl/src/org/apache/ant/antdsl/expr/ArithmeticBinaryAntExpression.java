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

public abstract class ArithmeticBinaryAntExpression extends PrimitiveBinaryAntExpression {

    public ArithmeticBinaryAntExpression(String name) {
        super(name);
    }

    // not final, for the exception of operation +
    @Override
    protected Object eval(String v1, String v2) {
        throw buildIncompatibleTypeException("String", "String");
    }

    @Override
    final protected Object eval(String v1, boolean v2) {
        throw buildIncompatibleTypeException("String", "boolean");
    }

    @Override
    final protected Object eval(String v1, byte v2) {
        throw buildIncompatibleTypeException("String", "byte");
    }

    @Override
    final protected Object eval(String v1, short v2) {
        throw buildIncompatibleTypeException("String", "short");
    }

    @Override
    final protected Object eval(String v1, int v2) {
        throw buildIncompatibleTypeException("String", "int");
    }

    @Override
    final protected Object eval(String v1, long v2) {
        throw buildIncompatibleTypeException("String", "long");
    }

    @Override
    final protected Object eval(String v1, float v2) {
        throw buildIncompatibleTypeException("String", "float");
    }

    @Override
    final protected Object eval(String v1, double v2) {
        throw buildIncompatibleTypeException("String", "double");
    }

    @Override
    final protected Object eval(boolean v1, String v2) {
        throw buildIncompatibleTypeException("boolean", "String");
    }

    @Override
    final protected Object eval(boolean v1, boolean v2) {
        throw buildIncompatibleTypeException("boolean", "boolean");
    }

    @Override
    final protected Object eval(boolean v1, byte v2) {
        throw buildIncompatibleTypeException("boolean", "byte");
    }

    @Override
    final protected Object eval(boolean v1, short v2) {
        throw buildIncompatibleTypeException("boolean", "short");
    }

    @Override
    final protected Object eval(boolean v1, int v2) {
        throw buildIncompatibleTypeException("boolean", "int");
    }

    @Override
    final protected Object eval(boolean v1, long v2) {
        throw buildIncompatibleTypeException("boolean", "long");
    }

    @Override
    final protected Object eval(boolean v1, float v2) {
        throw buildIncompatibleTypeException("boolean", "float");
    }

    @Override
    final protected Object eval(boolean v1, double v2) {
        throw buildIncompatibleTypeException("boolean", "double");
    }

    abstract protected Object eval(byte v1, byte v2, String t1, String t2);

    @Override
    final protected Object eval(byte v1, String v2) {
        throw buildIncompatibleTypeException("byte", "String");
    }

    @Override
    final protected Object eval(byte v1, boolean v2) {
        throw buildIncompatibleTypeException("byte", "boolean");
    }

    @Override
    final protected Object eval(byte v1, byte v2) {
        return eval(v1, v2, "byte", "byte");
    }

    @Override
    final protected Object eval(byte v1, short v2) {
        return eval((short) v1, v2, "byte", "short");
    }

    @Override
    final protected Object eval(byte v1, int v2) {
        return eval((int) v1, v2, "byte", "int");
    }

    @Override
    final protected Object eval(byte v1, long v2) {
        return eval((long) v1, v2, "byte", "long");
    }

    @Override
    final protected Object eval(byte v1, float v2) {
        return eval((float) v1, v2, "byte", "float");
    }

    @Override
    final protected Object eval(byte v1, double v2) {
        return eval((double) v1, v2, "byte", "double");
    }

    abstract protected Object eval(short v1, short v2, String t1, String t2);

    @Override
    final protected Object eval(short v1, String v2) {
        throw buildIncompatibleTypeException("short", "String");
    }

    @Override
    final protected Object eval(short v1, boolean v2) {
        throw buildIncompatibleTypeException("short", "boolean");
    }

    @Override
    final protected Object eval(short v1, byte v2) {
        return eval(v1, (short) v2, "short", "byte");
    }

    @Override
    final protected Object eval(short v1, short v2) {
        return eval(v1, v2, "short", "short");
    }

    @Override
    final protected Object eval(short v1, int v2) {
        return eval((int) v1, v2, "short", "int");
    }

    @Override
    final protected Object eval(short v1, long v2) {
        return eval((long) v1, v2, "short", "long");
    }

    @Override
    final protected Object eval(short v1, float v2) {
        return eval((float) v1, v2, "short", "float");
    }

    @Override
    final protected Object eval(short v1, double v2) {
        return eval((double) v1, v2, "short", "double");
    }

    abstract protected Object eval(int v1, int v2, String t1, String t2);

    @Override
    final protected Object eval(int v1, String v2) {
        throw buildIncompatibleTypeException("int", "String");
    }

    @Override
    final protected Object eval(int v1, boolean v2) {
        throw buildIncompatibleTypeException("int", "boolean");
    }

    @Override
    final protected Object eval(int v1, byte v2) {
        return eval(v1, (int) v2, "int", "byte");
    }

    @Override
    final protected Object eval(int v1, short v2) {
        return eval(v1, (int) v2, "int", "short");
    }

    @Override
    final protected Object eval(int v1, int v2) {
        return eval(v1, v2, "int", "int");
    }

    @Override
    final protected Object eval(int v1, long v2) {
        return eval((long) v1, v2, "int", "long");
    }

    @Override
    final protected Object eval(int v1, float v2) {
        return eval((float) v1, v2, "int", "float");
    }

    @Override
    final protected Object eval(int v1, double v2) {
        return eval((double) v1, v2, "int", "double");
    }

    abstract protected Object eval(long v1, long v2, String t1, String t2);

    @Override
    final protected Object eval(long v1, String v2) {
        throw buildIncompatibleTypeException("long", "String");
    }

    @Override
    final protected Object eval(long v1, boolean v2) {
        throw buildIncompatibleTypeException("long", "boolean");
    }

    @Override
    final protected Object eval(long v1, byte v2) {
        return eval(v1, (long) v2, "long", "byte");
    }

    @Override
    final protected Object eval(long v1, short v2) {
        return eval(v1, (long) v2, "long", "short");
    }

    @Override
    final protected Object eval(long v1, int v2) {
        return eval(v1, (long) v2, "long", "int");
    }

    @Override
    final protected Object eval(long v1, long v2) {
        return eval(v1, v2, "long", "long");
    }

    @Override
    final protected Object eval(long v1, float v2) {
        return eval((float) v1, v2, "long", "float");
    }

    @Override
    final protected Object eval(long v1, double v2) {
        return eval((double) v1, v2, "long", "double");
    }

    abstract protected Object eval(float v1, float v2, String t1, String t2);

    @Override
    final protected Object eval(float v1, String v2) {
        throw buildIncompatibleTypeException("float", "String");
    }

    @Override
    final protected Object eval(float v1, boolean v2) {
        throw buildIncompatibleTypeException("float", "boolean");
    }

    @Override
    final protected Object eval(float v1, byte v2) {
        return eval(v1, (float) v2, "float", "byte");
    }

    @Override
    final protected Object eval(float v1, short v2) {
        return eval(v1, (float) v2, "float", "short");
    }

    @Override
    final protected Object eval(float v1, int v2) {
        return eval(v1, (float) v2, "float", "int");
    }

    @Override
    final protected Object eval(float v1, long v2) {
        return eval(v1, (float) v2, "float", "long");
    }

    @Override
    final protected Object eval(float v1, float v2) {
        return eval(v1, v2, "float", "float");
    }

    @Override
    final protected Object eval(float v1, double v2) {
        return eval((double) v1, v2, "float", "double");
    }

    abstract protected Object eval(double v1, double v2, String t1, String t2);

    @Override
    final protected Object eval(double v1, String v2) {
        throw buildIncompatibleTypeException("double", "String");
    }

    @Override
    final protected Object eval(double v1, boolean v2) {
        throw buildIncompatibleTypeException("double", "boolean");
    }

    @Override
    final protected Object eval(double v1, byte v2) {
        return eval(v1, (double) v2, "double", "byte");
    }

    @Override
    final protected Object eval(double v1, short v2) {
        return eval(v1, (double) v2, "double", "short");
    }

    @Override
    final protected Object eval(double v1, int v2) {
        return eval(v1, (double) v2, "double", "int");
    }

    @Override
    final protected Object eval(double v1, long v2) {
        return eval(v1, (double) v2, "double", "long");
    }

    @Override
    final protected Object eval(double v1, float v2) {
        return eval(v1, (double) v2, "double", "float");
    }

    @Override
    final protected Object eval(double v1, double v2) {
        return eval(v1, v2, "double", "double");
    }

}
