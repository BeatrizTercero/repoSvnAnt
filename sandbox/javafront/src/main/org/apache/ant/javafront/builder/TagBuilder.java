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

public final class TagBuilder {

    private final Project project;

    private TagBuilder(Project project) {
        this.project = project;
    }

    /**
     * A TagBuilder for a given project.
     */
    public static TagBuilder forProject(Project p) {
        return new TagBuilder(p);
    }

    /**
     * Collects information for a given task/type.
     */
    public Tag tag(String name) {
        return tagWithNs(name, "");
    }

    /**
     * Collects information for a given task/type.
     */
    public Tag tagWithNs(String name, String namespaceUri) {
        return new Tag(project, name, namespaceUri);
    }

    /**
     * Specialized for the property task.
     */
    public PropertyBuilder property() {
        return new PropertyBuilder(project);
    }

    /**
     * Specialized for the copy task.
     */
    public CopyBuilder copy() {
        return new CopyBuilder(project);
    }

    /**
     * Specialized for the echo task.
     */
    public DeleteBuilder delete() {
        return new DeleteBuilder(project);
    }

    /**
     * Specialized for the mkdir task.
     */
    public MkdirBuilder mkdir() {
        return new MkdirBuilder(project);
    }

    /**
     * Specialized for the mkdir task.
     */
    public EchoBuilder echo() {
        return new EchoBuilder(project);
    }
}