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
package org.apache.ant.debian;

import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Holds the data for a debian control file
 */
public class ControlFile extends Resource {

    private static final String PACKAGE_HEADER = "Package: ";
    private static final String VERSION_HEADER = "Version: ";
    private static final String SECTION_HEADER = "Section: ";
    private static final String PRIORITY_HEADER = "Priority: ";
    private static final String ARCHITECTURE_HEADER = "Architecture: ";
    private static final String ESSENTIAL_HEADER = "Essential: ";
    private static final String MAINTAINER_HEADER = "Maintainer: ";
    private static final String PROVIDES_HEADER = "Provides: ";
    private static final String DESCRIPTION_HEADER = "Description: ";
    private static final String DEPENDENCIES_HEADER = "Dependencies: ";


    private Vector dependencies = new Vector();

    private String debPackage;

    private String debVersion;

    private String debSection;

    private String debPriority;

    private String debArchitecture;

    private String debEssential;

    private String debMaintainer;

    private String debProvides;

    private Description description;

    /* default */
    public ControlFile() {}

    public void addDependency(Dependency d) {
        dependencies.add(d);
    }

    public String getDebPackage() {
        return debPackage;
    }

    public void setDebPackage(String debPackage) {
        this.debPackage = debPackage;
    }

    public String getDebSection() {
        return debSection;
    }

    public void setDebSection(String debSection) {
        this.debSection = debSection;
    }

    public String getDebVersion() {
        return debVersion;
    }

    public void setDebVersion(String debVersion) {
        this.debVersion = debVersion;
    }

    public String getDebPriority() {
        return debPriority;
    }

    public void setDebPriority(String debPriority) {
        this.debPriority = debPriority;
    }

    public String getDebArchitecture() {
        return debArchitecture;
    }

    public void setDebArchitecture(String debArchitecture) {
        this.debArchitecture = debArchitecture;
    }

    public String getDebEssential() {
        return debEssential;
    }

    public void setDebEssential(String debEssential) {
        this.debEssential = debEssential;
    }

    public String getDebMaintainer() {
        return debMaintainer;
    }

    public void setDebMaintainer(String debMaintainer) {
        this.debMaintainer = debMaintainer;
    }

    public String getDebProvides() {
        return debProvides;
    }

    public void setDebProvides(String debProvides) {
        this.debProvides = debProvides;
    }

    public void addDescription(Description d) {
        this.description = d;
    }

    public void write(PrintWriter writer) throws IOException {
        writer.println(PACKAGE_HEADER+debPackage);
        writer.println(VERSION_HEADER+debVersion);
        writer.println(SECTION_HEADER+debSection);
        writer.println(PRIORITY_HEADER+debPriority);
        writer.println(ARCHITECTURE_HEADER+debArchitecture);
        writer.println(ESSENTIAL_HEADER+debEssential);
        writer.println(MAINTAINER_HEADER+debMaintainer);
        writer.println(PROVIDES_HEADER+debProvides);
        if(dependencies.size() > 0) {
            writer.print(DEPENDENCIES_HEADER);
            for(Iterator i = dependencies.iterator();i.hasNext();) {
                writer.print(((Dependency)i.next()).getName());
                if(i.hasNext()) {
                    writer.print(",");
                } else {
                    writer.print("\n");
                }
            }
        }
        if(null != description) {
            writer.println(DESCRIPTION_HEADER+description.getDesc());
        }
    }

    public static class Dependency {
        private String name;

        public Dependency() {}

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void addText(String text) {
            this.name = text;
        }
    }

    public static class Description {
        private String desc;

        public Description() {}

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public void addText(String text) {
            this.desc = text;
        }
    }
}