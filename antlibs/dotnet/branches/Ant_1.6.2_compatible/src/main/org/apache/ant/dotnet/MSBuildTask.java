/*
 * Copyright 2003-2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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

package org.apache.ant.dotnet;

import java.io.File;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Runs a MSBuild build process.
 */
public class MSBuildTask extends AbstractBuildTask {

    private static final String TARGET = "generated-by-ant";

    public MSBuildTask() {
        super();
    }

    protected String getExecutable() {
        return "MSBuild.exe";
    }

    protected String[] getBuildfileArguments(File buildFile) {
        if (buildFile != null) {
            return new String[] {
                buildFile.getAbsolutePath()
            };
        } else {
            return new String[0];
        }
    }

    protected String[] getTargetArguments(List targets) {
        if (targets.size() > 0) {
            StringBuffer sb = new StringBuffer("/target:");
            Iterator iter = targets.iterator();
            boolean first = true;
            while (iter.hasNext()) {
                AbstractBuildTask.Target t = 
                    (AbstractBuildTask.Target) iter.next();
                if (!first) {
                    sb.append(";");
                } else {
                    first = false;
                }
                sb.append(t.getName());
            }
            return new String[]{sb.toString()};
        } else {
            return new String[0];
        }
    }

    protected String[] getPropertyArguments(List properties) {
        if (properties.size() > 0) {
            StringBuffer sb = new StringBuffer("/property:");
            Iterator iter = properties.iterator();
            boolean first = true;
            while (iter.hasNext()) {
                AbstractBuildTask.Property p = 
                    (AbstractBuildTask.Property) iter.next();
                if (!first) {
                    sb.append(";");
                } else {
                    first = false;
                }
                sb.append(p.getName()).append("=").append(p.getValue());
            }
            return new String[]{sb.toString()};
        } else {
            return new String[0];
        }
    }

    /**
     * Turn the DocumentFragment into a DOM tree suitable as a build
     * file when serialized.
     *
     * <p>If we have exactly one <Project> child, return that.
     * Otherwise if we have only <Task> children, wrap them into a
     * <Target> which in turn gets wrapped into a <Project>.
     * Otherwise, fail.</p>
     */
    protected Element makeTree(DocumentFragment f) {
        throw new BuildException("MSBuild requires build files to have"
                                 + " a certain default namespace, which"
                                 + " cannot be achieved with Ant prior to"
                                 + " Ant 1.7.");
    }
}
