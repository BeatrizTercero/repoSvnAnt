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

import javax.xml.namespace.QName;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.UnknownElement;

public class Tag<T extends Tag<T>> {

    // is there a cleaner way to do it?
    @SuppressWarnings("unchecked")
    private final T self = (T) this;

    private final UnknownElement ue;
    private final RuntimeConfigurable rc;

    protected Tag(Project p, String name) {
        this(p, name, "");
    }

    protected Tag(Project p, String name, String namespaceUri) {
        ue = new UnknownElement(name);
        ue.setNamespace(namespaceUri);
        if (namespaceUri == null || namespaceUri.length() == 0) {
            ue.setQName(new QName(name).toString());
        } else {
            ue.setQName(new QName(namespaceUri, name).toString());
        }
        ue.setProject(p);
        ue.setTaskType(name);
        ue.setTaskName(name);
        rc = new RuntimeConfigurable(ue, name);
    }

    public T withAttribute(String name, String value) {
        rc.setAttribute(name, value);
        return self;
    }

    public T withChild(Tag<?> child) {
        ue.addChild(child.ue);
        rc.addChild(child.rc);
        return self;
    }

    public T withNestedText(String text) {
        rc.addText(text);
        return self;
    }

    public Object build() {
        ue.maybeConfigure();
        return ue.getRealThing();
    }

    public void execute() {
        Object o = build();
        if (o instanceof Task) {
            ((Task) o).perform();
        }
    }
    
    // More for debugging ...
    public String toString() {
        return ue.getTag();
    }
    public UnknownElement getUE() {
        return ue;
    }
}