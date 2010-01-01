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
package org.apache.ant.groovyfront;

import groovy.lang.DelegatingMetaClass;
import groovy.lang.MetaClass;
import groovy.lang.MissingMethodException;
import groovy.lang.Tuple;

import org.codehaus.groovy.runtime.MetaClassHelper;

public class GroovyFrontMetaClass extends DelegatingMetaClass {

    protected final GroovyFrontBuilder groovyFrontBuilder;

    public GroovyFrontMetaClass(final MetaClass metaClass, final GroovyFrontBuilder groovyFrontBuilder) {
        super(metaClass);
        this.groovyFrontBuilder = groovyFrontBuilder;
    }

    public Object invokeMethod(final Object object, final String methodName, final Object[] arguments) {
        Object returnObject = null;
        try {
            returnObject = super.invokeMethod(object, methodName, arguments);
        } catch (final MissingMethodException mme) {
            if (groovyFrontBuilder.isTaskDefined(methodName)) {
                returnObject = groovyFrontBuilder.invokeMethod(methodName, arguments);
            } else {
                throw mme;
            }
        }
        return returnObject;
    }

    public Object invokeMethod(final Object object, final String methodName, final Object arguments) {
        if (arguments == null) {
            return invokeMethod(object, methodName, MetaClassHelper.EMPTY_ARRAY);
        } else if (arguments instanceof Tuple) {
            return invokeMethod(object, methodName, ((Tuple) arguments).toArray());
        } else if (arguments instanceof Object[]) {
            return invokeMethod(object, methodName, (Object[]) arguments);
        } else {
            return invokeMethod(object, methodName, new Object[] { arguments });
        }
    }

    public Object invokeMethod(final String name, final Object args) {
        return invokeMethod(this, name, args);
    }

    public Object invokeMethod(final Class sender, final Object receiver, final String methodName,
            final Object[] arguments, final boolean isCallToSuper, final boolean fromInsideClass) {
        return invokeMethod(receiver, methodName, arguments);
    }

}
