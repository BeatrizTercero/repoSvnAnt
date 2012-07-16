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

public class InstanceofAntExpression extends BinaryAntExpression {

    @Override
    protected Boolean eval(Object v1, Object v2) {
        // FIXME ugly implementation
        try {
            return Class.forName(v2.toString()).isAssignableFrom(v1.getClass());
        } catch (ClassNotFoundException e) {
            throw new BuildException(e);
        }
    }

}
