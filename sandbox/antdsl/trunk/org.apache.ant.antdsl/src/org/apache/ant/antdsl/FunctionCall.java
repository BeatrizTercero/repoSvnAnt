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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.ant.antdsl.FunctionDef.LocalProperty;
import org.apache.ant.antdsl.FunctionDef.TemplateElement;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DynamicObjectAttribute;
import org.apache.tools.ant.Evaluable;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.property.LocalProperties;

/**
 * The class to be placed in the ant type definition. It is given a pointer to the template definition, and makes a copy of the unknown element,
 * substituting the parameter values in attributes and text.
 * 
 */
public class FunctionCall extends Task implements DynamicObjectAttribute, TaskContainer {

    private FunctionDef functionDef;

    private Map<String, Object> map = new HashMap<String, Object>();

    private Map<String, TemplateElement> nsElements = null;

    private Map<String, UnknownElement> presentElements;

    private String implicitTag = null;

    private List<UnknownElement> unknownElements = new ArrayList<UnknownElement>();

    public void setFunctionDef(FunctionDef functionDef) {
        this.functionDef = functionDef;
    }

    public FunctionDef getFunctionDef() {
        return functionDef;
    }

    /**
     * A parameter name value pair as a xml attribute.
     * 
     * @param name the name of the attribute
     * @param value the value of the attribute
     */
    public void setDynamicAttribute(String name, Object value) {
        map.put(name, value);
    }

    private Map<String, TemplateElement> getNsElements() {
        if (nsElements == null) {
            nsElements = new HashMap<String, TemplateElement>();
            for (Entry<String, TemplateElement> entry : functionDef.getElements().entrySet()) {
                FunctionDef.TemplateElement te = entry.getValue();
                nsElements.put(entry.getKey(), te);
                if (te.isImplicit()) {
                    implicitTag = te.getName();
                }
            }
        }
        return nsElements;
    }

    /**
     * Add a unknownElement for the macro instances nested elements.
     * 
     * @param nestedTask a nested element.
     */
    public void addTask(Task nestedTask) {
        unknownElements.add((UnknownElement) nestedTask);
    }

    private void processTasks() {
        if (implicitTag != null) {
            return;
        }
        for (UnknownElement ue : unknownElements) {
            String name = ProjectHelper.extractNameFromComponentName(ue.getTag()).toLowerCase(Locale.ENGLISH);
            if (getNsElements().get(name) == null) {
                throw new BuildException("unsupported element " + name);
            }
            if (presentElements.get(name) != null) {
                throw new BuildException("Element " + name + " already present");
            }
            presentElements.put(name, ue);
        }
    }

    /**
     * Embedded element in macro instance
     */
    public static class Element implements TaskContainer {
        private List<UnknownElement> unknownElements = new ArrayList<UnknownElement>();

        /**
         * Add an unknown element (to be snipped into the macroDef instance)
         * 
         * @param nestedTask an unknown element
         */
        public void addTask(Task nestedTask) {
            unknownElements.add((UnknownElement) nestedTask);
        }

        /**
         * @return the list of unknown elements
         */
        public List<UnknownElement> getUnknownElements() {
            return unknownElements;
        }
    }

    private UnknownElement copy(UnknownElement ue, boolean nested) {
        UnknownElement ret = new UnknownElement(ue.getTag());
        ret.setNamespace(ue.getNamespace());
        ret.setProject(getProject());
        ret.setQName(ue.getQName());
        ret.setTaskType(ue.getTaskType());
        ret.setTaskName(ue.getTaskName());
        ret.setLocation(functionDef.getBackTrace() ? ue.getLocation() : getLocation());
        if (getOwningTarget() == null) {
            Target t = new Target();
            t.setProject(getProject());
            ret.setOwningTarget(t);
        } else {
            ret.setOwningTarget(getOwningTarget());
        }
        RuntimeConfigurable rc = new RuntimeConfigurable(ret, ue.getTaskName());
        rc.setPolyType(ue.getWrapper().getPolyType());
        Map<String, Object> m = ((RuntimeConfigurable) ue.getWrapper()).getAttributeMap();
        for (Entry<String, Object> entry : m.entrySet()) {
            rc.setAttribute(entry.getKey(), entry.getValue());
        }

        Enumeration<RuntimeConfigurable> e = ue.getWrapper().getChildren();
        while (e.hasMoreElements()) {
            RuntimeConfigurable r = e.nextElement();
            UnknownElement unknownElement = (UnknownElement) r.getProxy();
            String tag = unknownElement.getTaskType();
            if (tag != null) {
                tag = tag.toLowerCase(Locale.ENGLISH);
            }
            FunctionDef.TemplateElement templateElement = (FunctionDef.TemplateElement) getNsElements().get(tag);
            if (templateElement == null || nested) {
                UnknownElement child = copy(unknownElement, nested);
                rc.addChild(child.getWrapper());
                ret.addChild(child);
            } else if (templateElement.isImplicit()) {
                if (unknownElements.size() == 0 && !templateElement.isOptional()) {
                    throw new BuildException("Missing nested elements for implicit element " + templateElement.getName());
                }
                for (UnknownElement elem : unknownElements) {
                    UnknownElement child = copy(elem, true);
                    rc.addChild(child.getWrapper());
                    ret.addChild(child);
                }
            } else {
                UnknownElement presentElement = presentElements.get(tag);
                if (presentElement == null) {
                    if (!templateElement.isOptional()) {
                        throw new BuildException("Required nested element " + templateElement.getName() + " missing");
                    }
                    continue;
                }
                List<UnknownElement> list = presentElement.getChildren();
                if (list != null) {
                    for (UnknownElement elem : list) {
                        UnknownElement child = copy(elem, true);
                        rc.addChild(child.getWrapper());
                        ret.addChild(child);
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Execute the templates instance. Copies the unknown element, substitutes the attributes, and calls perform on the unknown element.
     * 
     */
    public void execute() {
        presentElements = new HashMap<String, UnknownElement>();
        getNsElements();
        processTasks();

        LocalProperties localProperties = LocalProperties.get(getProject());
        localProperties.enterScope();
        PropertyHelper propertyHelper = PropertyHelper.getPropertyHelper(getProject());

        try {
            Set<String> copyKeys = new HashSet<String>(map.keySet());
            for (LocalProperty localProperty : functionDef.getLocalProperties()) {
                Object value = map.get(localProperty.getName());
                if (value == null) {
                    value = localProperty.getDefault();
                }
                if (value == null) {
                    throw new BuildException("required argument " + localProperty.getName() + " not set");
                }
                if (value instanceof Evaluable) {
                    value = ((Evaluable) value).eval();
                }
                localProperties.addLocal(localProperty.getName());
                localProperties.setNew(localProperty.getName(), value, propertyHelper);
                copyKeys.remove(localProperty.getName());
            }
            if (copyKeys.contains("id")) {
                copyKeys.remove("id");
            }
            if (copyKeys.size() != 0) {
                throw new BuildException("Unknown argument" + (copyKeys.size() > 1 ? "s " : " ") + copyKeys);
            }
    
            // need to set the project on unknown element
            UnknownElement c = copy(functionDef.getNestedTask(), false);
            c.init();
            c.perform();
        } catch (BuildException ex) {
            if (functionDef.getBackTrace()) {
                throw ProjectHelper.addLocationToBuildException(ex, getLocation());
            } else {
                ex.setLocation(getLocation());
                throw ex;
            }
        } finally {
            presentElements = null;
            localProperties.exitScope();
        }
    }

}
