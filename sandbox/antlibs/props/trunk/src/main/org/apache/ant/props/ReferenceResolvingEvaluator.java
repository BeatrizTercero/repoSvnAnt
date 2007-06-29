/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.apache.ant.props;

import org.apache.tools.ant.PropertyHelper;

/**
 * PropertyEvaluator that resolves a reference against the current project.
 */
public class ReferenceResolvingEvaluator extends StaticPrefixedEvaluator {
    /** Default prefix */
    public static final String DEFAULT_PREFIX = "ref";

    /**
     * Create a new ReferenceResolvingEvaluator.
     */
    public ReferenceResolvingEvaluator() {
        super(DEFAULT_PREFIX);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.ant.props.PrefixedEvaluator#evaluatePrefixed(java.lang.String,
     *      java.lang.String, org.apache.tools.ant.PropertyHelper)
     */
    protected Object evaluate(String property, String prefix, PropertyHelper propertyHelper) {
        return propertyHelper.getProject().getReference(property);
    }
}
