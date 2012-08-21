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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.ant.antdsl.expr.AntExpression;
import org.apache.tools.ant.AntTypeDefinition;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.taskdefs.AntlibDefinition;

public class FunctionDef extends AntlibDefinition {

    private NestedSequential nestedSequential;

    private String name;

    private boolean backTrace = true;

    private List<LocalProperty> localProperties = new ArrayList<LocalProperty>();

    private Map<String, TemplateElement> elements = new HashMap<String, TemplateElement>();

    private boolean hasImplicitElement = false;

    /**
     * Name of the definition
     * 
     * @param name the name of the definition
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the backTrace attribute.
     * 
     * @param backTrace if true and the macro instance generates an error, a backtrace of the location within the macro and call to the macro will be
     *            output. if false, only the location of the call to the macro will be shown. Default is true.
     * @since ant 1.7
     */
    public void setBackTrace(boolean backTrace) {
        this.backTrace = backTrace;
    }

    /**
     * @return the backTrace attribute.
     * @since ant 1.7
     */
    public boolean getBackTrace() {
        return backTrace;
    }

    /**
     * This is the sequential nested element of the macrodef.
     * 
     * @return a sequential element to be configured.
     */
    public NestedSequential createSequential() {
        if (this.nestedSequential != null) {
            throw new BuildException("Only one sequential allowed");
        }
        this.nestedSequential = new NestedSequential();
        return this.nestedSequential;
    }

    /**
     * The class corresponding to the sequential nested element. This is a simple task container.
     */
    public static class NestedSequential implements TaskContainer {

        private List<Task> nested = new ArrayList<Task>();

        public void addTask(Task task) {
            nested.add(task);
        }

        public List<Task> getNested() {
            return nested;
        }

        /**
         * A compare function to compare this with another NestedSequential. It calls similar on the nested unknown elements.
         * 
         * @param other the nested sequential to compare with.
         * @return true if they are similar, false otherwise
         */
        public boolean similar(NestedSequential other) {
            final int size = nested.size();
            if (size != other.nested.size()) {
                return false;
            }
            for (int i = 0; i < size; ++i) {
                UnknownElement me = (UnknownElement) nested.get(i);
                UnknownElement o = (UnknownElement) other.nested.get(i);
                if (!me.similar(o)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Convert the nested sequential to an unknown element
     * 
     * @return the nested sequential as an unknown element.
     */
    public UnknownElement getNestedTask() {
        UnknownElement ret = new UnknownElement("sequential");
        ret.setTaskName("sequential");
        ret.setNamespace("");
        ret.setQName("sequential");
        new RuntimeConfigurable(ret, "sequential");
        final int size = nestedSequential.getNested().size();
        for (int i = 0; i < size; ++i) {
            UnknownElement e = (UnknownElement) nestedSequential.getNested().get(i);
            ret.addChild(e);
            ret.getWrapper().addChild(e.getWrapper());
        }
        return ret;
    }

    public List<LocalProperty> getLocalProperties() {
        return localProperties;
    }

    /**
     * Gets this macro's elements.
     * 
     * @return the map nested elements, keyed by element name, with {@link TemplateElement} values.
     */
    public Map<String, TemplateElement> getElements() {
        return elements;
    }

    /**
     * Check if a character is a valid character for an element or attribute name.
     * 
     * @param c the character to check
     * @return true if the character is a letter or digit or '.' or '-' attribute name
     */
    public static boolean isValidNameCharacter(char c) {
        // ? is there an xml api for this ?
        return Character.isLetterOrDigit(c) || c == '.' || c == '-';
    }

    /**
     * Check if a string is a valid name for an element or attribute.
     * 
     * @param name the string to check
     * @return true if the name consists of valid name characters
     */
    private static boolean isValidName(String name) {
        if (name.length() == 0) {
            return false;
        }
        for (int i = 0; i < name.length(); ++i) {
            if (!isValidNameCharacter(name.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Add a local property.
     * 
     * @param localProperty a local property
     */
    public void addConfiguredLocalProperty(LocalProperty localProperty) {
        if (localProperty.getName() == null) {
            throw new BuildException("the local property nested element needed a name");
        }
        final int size = localProperties.size();
        for (int i = 0; i < size; ++i) {
            LocalProperty att = (LocalProperty) localProperties.get(i);
            if (att.getName().equals(localProperty.getName())) {
                throw new BuildException("the name \"" + localProperty.getName() + "\" has already been used in another local property element");
            }
        }
        localProperties.add(localProperty);
    }

    /**
     * Add an element element.
     * 
     * @param element an element nested element.
     */
    public void addConfiguredElement(TemplateElement element) {
        if (element.getName() == null) {
            throw new BuildException("the element nested needed a name");
        }
        if (elements.get(element.getName()) != null) {
            throw new BuildException("the element " + element.getName() + " has already been specified");
        }
        if (hasImplicitElement || (element.isImplicit() && elements.size() != 0)) {
            throw new BuildException("Only one element allowed when using implicit elements");
        }
        hasImplicitElement = element.isImplicit();
        elements.put(element.getName(), element);
    }

    /**
     * Create a new ant type based on the embedded tasks and types.
     */
    public void execute() {
        if (nestedSequential == null) {
            throw new BuildException("Missing sequential element");
        }
        if (name == null) {
            throw new BuildException("Name not specified");
        }

        name = ProjectHelper.genComponentName(getURI(), name);

        FunctionTypeDefinition def = new FunctionTypeDefinition(this);
        def.setName(name);
        def.setClass(FunctionCall.class);

        ComponentHelper helper = ComponentHelper.getComponentHelper(getProject());

        helper.addDataTypeDefinition(def);
        log("creating macro  " + name, Project.MSG_VERBOSE);
    }

    public static class LocalProperty {

        private String name;

        private AntExpression defaultValue;

        private String description;

        public void setName(String name) {
            if (!isValidName(name)) {
                throw new BuildException("Illegal name [" + name + "] for argument");
            }
            this.name = name.toLowerCase(Locale.ENGLISH);
        }

        public String getName() {
            return name;
        }

        public void setDefault(AntExpression defaultValue) {
            this.defaultValue = defaultValue;
        }

        public AntExpression getDefault() {
            return defaultValue;
        }

        public void setDescription(String desc) {
            description = desc;
        }

        public String getDescription() {
            return description;
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }
            LocalProperty other = (LocalProperty) obj;
            if (name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!name.equals(other.name)) {
                return false;
            }
            if (defaultValue == null) {
                if (other.defaultValue != null) {
                    return false;
                }
            } else if (!defaultValue.equals(other.defaultValue)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return objectHashCode(defaultValue) + objectHashCode(name);
        }
    }

    /**
     * A nested element for the MacroDef task.
     */
    public static class TemplateElement {

        private String name;
        private String description;
        private boolean optional = false;
        private boolean implicit = false;

        /**
         * Sets the name of this element.
         * 
         * @param name the name of the element
         */
        public void setName(String name) {
            if (!isValidName(name)) {
                throw new BuildException("Illegal name [" + name + "] for function element");
            }
            this.name = name.toLowerCase(Locale.ENGLISH);
        }

        /**
         * Gets the name of this element.
         * 
         * @return the name of the element.
         */
        public String getName() {
            return name;
        }

        /**
         * Sets a textual description of this element, for build documentation purposes only.
         * 
         * @param desc Description of the element.
         * @since ant 1.6.1
         */
        public void setDescription(String desc) {
            description = desc;
        }

        /**
         * Gets the description of this element.
         * 
         * @return the description of the element, or <code>null</code> if no description is available.
         * @since ant 1.6.1
         */
        public String getDescription() {
            return description;
        }

        /**
         * Sets whether this element is optional.
         * 
         * @param optional if true this element may be left out, default is false.
         */
        public void setOptional(boolean optional) {
            this.optional = optional;
        }

        /**
         * Gets whether this element is optional.
         * 
         * @return the optional attribute
         */
        public boolean isOptional() {
            return optional;
        }

        /**
         * Sets whether this element is implicit.
         * 
         * @param implicit if true this element may be left out, default is false.
         */
        public void setImplicit(boolean implicit) {
            this.implicit = implicit;
        }

        /**
         * Gets whether this element is implicit.
         * 
         * @return the implicit attribute
         */
        public boolean isImplicit() {
            return implicit;
        }

        /**
         * equality method.
         * 
         * @param obj an <code>Object</code> value
         * @return a <code>boolean</code> value
         */
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || !obj.getClass().equals(getClass())) {
                return false;
            }
            TemplateElement t = (TemplateElement) obj;
            return (name == null ? t.name == null : name.equals(t.name)) && optional == t.optional && implicit == t.implicit;
        }

        /**
         * @return a hash code value for this object.
         */
        public int hashCode() {
            return objectHashCode(name) + (optional ? 1 : 0) + (implicit ? 1 : 0);
        }

    } // END static class TemplateElement

    /**
     * same or similar equality method for macrodef, ignores project and runtime info.
     * 
     * @param obj an <code>Object</code> value
     * @param same if true test for sameness, otherwise just similiar
     * @return a <code>boolean</code> value
     */
    private boolean sameOrSimilar(Object obj, boolean same) {
        if (obj == this) {
            return true;
        }

        if (obj == null) {
            return false;
        }
        if (!obj.getClass().equals(getClass())) {
            return false;
        }
        FunctionDef other = (FunctionDef) obj;
        if (name == null) {
            return other.name == null;
        }
        if (!name.equals(other.name)) {
            return false;
        }
        // Allow two macro definitions with the same location
        // to be treated as similar - bugzilla 31215
        if (other.getLocation() != null && other.getLocation().equals(getLocation()) && !same) {
            return true;
        }
        if (getURI() == null || getURI().equals("") || getURI().equals(ProjectHelper.ANT_CORE_URI)) {
            if (!(other.getURI() == null || other.getURI().equals("") || other.getURI().equals(ProjectHelper.ANT_CORE_URI))) {
                return false;
            }
        } else {
            if (!getURI().equals(other.getURI())) {
                return false;
            }
        }

        if (!nestedSequential.similar(other.nestedSequential)) {
            return false;
        }
        if (!localProperties.equals(other.localProperties)) {
            return false;
        }
        if (!elements.equals(other.elements)) {
            return false;
        }
        return true;
    }

    /**
     * Similar method for this definition
     * 
     * @param obj another definition
     * @return true if the definitions are similar
     */
    public boolean similar(Object obj) {
        return sameOrSimilar(obj, false);
    }

    /**
     * Equality method for this definition
     * 
     * @param obj another definition
     * @return true if the definitions are the same
     */
    public boolean sameDefinition(Object obj) {
        return sameOrSimilar(obj, true);
    }

    /**
     * extends AntTypeDefinition, on create of the object, the template macro definition is given.
     */
    private static class FunctionTypeDefinition extends AntTypeDefinition {
        private FunctionDef functionDef;

        public FunctionTypeDefinition(FunctionDef functionDef) {
            this.functionDef = functionDef;
        }

        /**
         * Create an instance of the definition. The instance may be wrapped in a proxy class.
         * 
         * @param project the current project
         * @return the created object
         */
        public Object create(Project project) {
            Object o = super.create(project);
            if (o == null) {
                return null;
            }
            ((FunctionCall) o).setFunctionDef(functionDef);
            return o;
        }

        /**
         * Equality method for this definition
         * 
         * @param other another definition
         * @param project the current project
         * @return true if the definitions are the same
         */
        public boolean sameDefinition(AntTypeDefinition other, Project project) {
            if (!super.sameDefinition(other, project)) {
                return false;
            }
            FunctionTypeDefinition otherDef = (FunctionTypeDefinition) other;
            return functionDef.sameDefinition(otherDef.functionDef);
        }

        /**
         * Similar method for this definition
         * 
         * @param other another definition
         * @param project the current project
         * @return true if the definitions are the same
         */
        public boolean similarDefinition(AntTypeDefinition other, Project project) {
            if (!super.similarDefinition(other, project)) {
                return false;
            }
            FunctionTypeDefinition otherDef = (FunctionTypeDefinition) other;
            return functionDef.similar(otherDef.functionDef);
        }
    }

    private static int objectHashCode(Object o) {
        if (o == null) {
            return 0;
        } else {
            return o.hashCode();
        }
    }

}
