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

import java.text.ParsePosition;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;

/**
 * Oft-requested nested Ant property expansion.
 */
public class NestedPropertyExpander implements PropertyHelper.PropertyExpander {

    /**
     * {@inheritDoc}
     * @see org.apache.tools.ant.PropertyHelper.PropertyExpander#parsePropertyName(java.lang.String, java.text.ParsePosition, org.apache.tools.ant.PropertyHelper)
     */
    public String parsePropertyName(String value, ParsePosition pos, PropertyHelper propertyHelper) {
        int start = pos.getIndex();
        Project p = propertyHelper.getProject();
        if (value.indexOf("${", start) == start) {
            p.log("Attempting nested property processing", Project.MSG_DEBUG);
            pos.setIndex(start + 2);
            StringBuffer sb = new StringBuffer();
            for (int c = pos.getIndex(); c < value.length(); c = pos.getIndex()) {
                if (value.charAt(c) == '}') {
                    pos.setIndex(c + 1);
                    return sb.toString();
                }
                Object o = propertyHelper.parseNextProperty(value, pos);
                if (o != null) {
                    sb.append(o);
                } else {
                    sb.append(value.charAt(c));
                    pos.setIndex(c + 1);
                }
            }
        }
        pos.setIndex(start);
        return null;
    }

}
