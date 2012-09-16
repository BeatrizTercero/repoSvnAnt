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
package org.apache.ant.antdsl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.helper.AntXMLContext;
import org.xml.sax.Locator;

public class AntDslContext extends AntXMLContext {

    private Stack<Map<String, String>> fqnPrefixesMapping = new Stack<Map<String, String>>();

    private Set<String> fqnPrefixes = new HashSet<String>();

    private Location location;

    public AntDslContext(Project project) {
        super(project);
        push();
    }

    public void push() {
        fqnPrefixesMapping.push(new HashMap<String, String>());
    }

    public void pop() {
        fqnPrefixesMapping.pop();
    }

    public String addFQNPrefix(String name, String resource) {
        Map<String, String> mapping = fqnPrefixesMapping.peek();
        if (mapping.containsKey(name)) {
            throw new BuildException("Alias " + name + " already defined in the file", getLocation());
        }
        String fqnPrefix = asFQN(resource);
        if (fqnPrefixes.contains(fqnPrefix)) {
            int i = 1;
            while(fqnPrefixes.contains(fqnPrefix + (++i)));
            fqnPrefix = fqnPrefix + i;
        }
        boolean added = fqnPrefixes.add(fqnPrefix);
        if (!added) {
            throw new RuntimeException("Fully qualified name prefix already used " + fqnPrefix);
        }
        mapping.put(name, fqnPrefix);
        return fqnPrefix;
    }

    public String getFQNPrefix(String name) {
        return fqnPrefixesMapping.peek().get(name);
    }

    private String asFQN(String resource) {
        if (resource == null) {
            resource = "";
        }
        String pkgName;
        if (resource.startsWith("/")) {
            pkgName = resource.substring(1);
        } else {
            pkgName = resource;
        }
        int end = pkgName.lastIndexOf('/');
        if (end > 0) {
            pkgName = pkgName.substring(0, end);
        }
        pkgName = pkgName.replace('/', '.');
        return pkgName;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        if (location != null) {
            return location;
        }
        Locator l = super.getLocator();
        if (l != null) {
            return new Location(l.getSystemId(), l.getLineNumber(), l.getColumnNumber());
        }
        return null;
    }
}
