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

public class AddAntExpression extends CommutativeAntExpression {

    public AddAntExpression() {
        super("add");
    }

    // Note : + is not commutative for String
    // but since we don't support mixing Strings with numbers, it still works

    @Override
    protected Object eval(String v1, String v2) {
        return v1 + v2;
    }

    @Override
    protected Object eval(String v1, int v2) {
        throw buildIncompatibleTypeException("String", "int");
    }

    @Override
    protected Object eval(String v1, long v2) {
        throw buildIncompatibleTypeException("String", "long");
    }

    @Override
    protected Object eval(String v1, float v2) {
        throw buildIncompatibleTypeException("String", "float");
    }

    @Override
    protected Object eval(String v1, double v2) {
        throw buildIncompatibleTypeException("String", "double");
    }

    @Override
    protected Object eval(int v1, int v2) {
        return v1 + v2;
    }

    @Override
    protected Object eval(int v1, long v2) {
        return v1 + v2;
    }

    @Override
    protected Object eval(int v1, float v2) {
        return v1 + v2;
    }

    @Override
    protected Object eval(int v1, double v2) {
        return v1 + v2;
    }

    @Override
    protected Object eval(long v1, long v2) {
        return v1 + v2;
    }

    @Override
    protected Object eval(long v1, float v2) {
        return v1 + v2;
    }

    @Override
    protected Object eval(long v1, double v2) {
        return v1 + v2;
    }

    @Override
    protected Object eval(float v1, float v2) {
        return v1 + v2;
    }

    @Override
    protected Object eval(float v1, double v2) {
        return v1 + v2;
    }

    @Override
    protected Object eval(double v1, double v2) {
        return v1 + v2;
    }

}
