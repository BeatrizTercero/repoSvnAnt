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

package org.apache.bsf;

public class BSFManager {
    public void declareBean(String s, Object o, Class c) {}
    public Object eval(String s0, String s1, int i1, int i2, String s2) {
        return null;
    }
    public Object exec(String s1, String s2, int i1, int i2, String s3) {
        return null;
    }
    public BSFEngine loadScriptingEngine(String language) {
        return null;
    }
    public static void registerScriptingEngine(String string, String string2, String[] strings) {}
    public void setClassLoader(ClassLoader scriptClassLoader) {}
    public void undeclareBean(String key) {}
}
