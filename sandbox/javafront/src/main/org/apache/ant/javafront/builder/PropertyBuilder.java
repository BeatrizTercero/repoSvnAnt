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
package org.apache.ant.javafront.builder;

import org.apache.tools.ant.Project;

public class PropertyBuilder extends Tag<PropertyBuilder> {
    PropertyBuilder(Project p) {
        super(p, "property");
    }

    public PropertyWithNameBuilder withName(String name) {
        withAttribute("name", name);
        return new PropertyWithNameBuilder();
    }

    public class PropertyWithNameBuilder {
        public PropertyBuilder andValue(String value) {
            withAttribute("value", value);
            return PropertyBuilder.this;
        }
        public PropertyBuilder andLocation(String value) {
            withAttribute("location", value);
            return PropertyBuilder.this;
        }
    }
}