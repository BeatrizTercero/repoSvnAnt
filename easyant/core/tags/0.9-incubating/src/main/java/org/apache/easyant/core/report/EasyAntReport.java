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
package org.apache.easyant.core.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.easyant.core.descriptor.PropertyDescriptor;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.report.ResolveReport;

/**
 * This class represent a report of an easyant module It contains all informations about properties / parameters /
 * plugins / etc..
 */
public class EasyAntReport {

    private List<TargetReport> targetReports = new ArrayList<TargetReport>();
    private List<ExtensionPointReport> extensionPointReports = new ArrayList<ExtensionPointReport>();
    private List<ParameterReport> parameterReports = new ArrayList<ParameterReport>();
    private List<ImportedModuleReport> importedModuleReports = new ArrayList<ImportedModuleReport>();
    private Map<String, PropertyDescriptor> propertyReports = new HashMap<String, PropertyDescriptor>();
    private ResolveReport resolveReport;
    private ModuleDescriptor moduleDescriptor;
    private boolean extensionPointsConfigured;

    /**
     * Get a list of targets available in this module
     * 
     * @return
     */
    public List<TargetReport> getTargetReports() {
        return Collections.unmodifiableList(targetReports);
    }

    public TargetReport getTargetReport(String name) {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("target name cannot be null");
        }
        for (TargetReport target : targetReports) {
            if (target.getName().equals(name)) {
                return target;
            }
        }
        return null;
    }

    /**
     * Add a given targetReport to the list of know target
     * 
     * @param targetReport
     *            a given targeReport
     */
    public void addTargetReport(TargetReport targetReport) {
        if (targetReport == null) {
            throw new IllegalArgumentException("targetReport cannot be null");
        }
        targetReports.add(targetReport);
    }

    /**
     * Get an extension point by its name Return null if no extensionPointReport where found with this name
     * 
     * @param name
     *            represent the extension point name
     * @return an extension point report
     */
    public ExtensionPointReport getExtensionPointReport(String name) {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("extension point name cannot be null");
        }
        maybeConfigureExtensionPoints();
        for (ExtensionPointReport extensionPointReport : extensionPointReports) {
            if (extensionPointReport.getName().equals(name)) {
                return extensionPointReport;
            }
        }
        return null;
    }

    /**
     * Get a list of extension points available in this module
     * 
     * @return a list of extension points
     */
    public List<ExtensionPointReport> getExtensionPointReports() {
        maybeConfigureExtensionPoints();
        return Collections.unmodifiableList(extensionPointReports);
    }

    private void maybeConfigureExtensionPoints() {
        if (!extensionPointsConfigured) {
            for (TargetReport targetReport : targetReports) {
                if (targetReport.getExtensionPoint() != null) {
                    for (ExtensionPointReport extensionPointReport : extensionPointReports) {
                        if (extensionPointReport.getName().equals(targetReport.getExtensionPoint())) {
                            extensionPointReport.addTargetReport(targetReport);
                        }
                    }
                }
            }
            extensionPointsConfigured = true;
        }
    }

    /**
     * Add a given extensionPointReport
     * 
     * @param extensionPointReport
     *            a given extensionPointReport
     */
    public void addExtensionPointReport(ExtensionPointReport extensionPointReport) {
        if (extensionPointReport == null) {
            throw new IllegalArgumentException("extensionPointReport cannot be null");
        }
        extensionPointReports.add(extensionPointReport);
    }

    /**
     * Get a list of all parameters defined in this module
     * 
     * @return a list of parameters
     */
    public List<ParameterReport> getParameterReports() {
        return Collections.unmodifiableList(parameterReports);
    }

    /**
     * Get a parameter reports by its name Return null if not found
     * 
     * @param name
     *            represent parameter name
     * @return a parameter report
     */
    public ParameterReport getParameterReport(String parameterName) {
        if (parameterName == null || parameterName.equals("")) {
            throw new IllegalArgumentException("parameterName cannot be null");
        }
        for (ParameterReport parameterReport : parameterReports) {
            if (parameterReport.getName().equals(parameterName)) {
                return parameterReport;
            }
        }
        return null;
    }

    /**
     * Add a parameterReport
     * 
     * @param parameterReport
     *            a parameterReport
     */
    public void addParameterReport(ParameterReport parameterReport) {
        if (parameterReport == null) {
            throw new IllegalArgumentException("parameterReport cannot be null");
        }
        parameterReports.add(parameterReport);
    }

    /**
     * Get a list of all imported modules
     * 
     * @return a list of imported modules
     */
    public List<ImportedModuleReport> getImportedModuleReports() {
        return Collections.unmodifiableList(importedModuleReports);
    }

    /**
     * Returns the imported module indicated by the passed parameter.
     * 
     * The method attempts to match either the complete module id, module name or the module alias as specified in the
     * build module.
     * 
     * @param module
     *            name of the module - either module id or the module alias.
     * 
     * @return instance of the exact module report queried for, if such a module exists. it returns null otherwise.
     */
    public ImportedModuleReport getImportedModuleReport(String module) {
        if (module.indexOf(';') > 0) {
            module = module.substring(0, module.indexOf(';'));
        }
        ImportedModuleReport retVal = null;
        for (ImportedModuleReport moduleRep : importedModuleReports) {
            if (moduleRep.moduleMrid != null && moduleRep.moduleMrid.startsWith(module)) {
                retVal = moduleRep;
                break;
            } else if (module.equals(moduleRep.getModuleRevisionId().getName())) {
                retVal = moduleRep;
                break;
            } else if (moduleRep.as != null && moduleRep.as.equals(module)) {
                retVal = moduleRep;
                break;
            }
            if (moduleRep.getEasyantReport() != null) {
                retVal = moduleRep.getEasyantReport().getImportedModuleReport(module);
                if (retVal != null) {
                    break;
                }
            }
        }
        return retVal;
    }

    /**
     * Add an imported module
     * 
     * @param importedModuleReport
     *            a report that represent the importedModule
     */
    public void addImportedModuleReport(ImportedModuleReport importedModuleReport) {
        if (importedModuleReport == null) {
            throw new IllegalArgumentException("importedModuleReport cannot be null");
        }
        importedModuleReports.add(importedModuleReport);
    }

    /**
     * Add a property
     * 
     * @param propertyName
     *            the property name
     * @param propertyDescriptor
     *            a property descriptor that contains several info on the propery (description / required or not etc...)
     */
    public void addPropertyDescriptor(String propertyName, PropertyDescriptor propertyDescriptor) {
        if (propertyName == null || propertyDescriptor == null) {
            throw new IllegalArgumentException("propertyName and propertyDescriptor cannot be null");
        }
        addProperty(propertyDescriptor, propertyReports);
    }

    /**
     * Add all properties contained in an other propertyDescriptor
     * 
     * @param properties
     *            a map of propertyDescriptor to inject
     */
    public void addAllPropertyDescriptor(Map<String, PropertyDescriptor> properties) {
        if (properties == null) {
            throw new IllegalArgumentException("properties cannot be null");
        }
        this.propertyReports.putAll(properties);
    }

    /**
     * Return a map of the properties
     * 
     * @return a map of the properties
     */
    public Map<String, PropertyDescriptor> getPropertyDescriptors() {
        return Collections.unmodifiableMap(propertyReports);
    }

    /**
     * Get a list of all the properties available in this module or in all imported modules
     * 
     * @return a list of all the properties available in this module or in all imported modules
     */
    public Map<String, PropertyDescriptor> getAvailableProperties() {
        Map<String, PropertyDescriptor> availableProperties = new HashMap<String, PropertyDescriptor>();

        if (propertyReports != null) {
            availableProperties.putAll(propertyReports);
        }
        if (importedModuleReports != null) {
            for (ImportedModuleReport importedModuleReport : importedModuleReports) {
                if (importedModuleReport.getEasyantReport() != null) {
                    Map<String, PropertyDescriptor> subproperties = importedModuleReport.getEasyantReport()
                            .getAvailableProperties();
                    for (PropertyDescriptor propertyDescriptor : subproperties.values()) {
                        addProperty(propertyDescriptor, availableProperties);
                    }
                }
            }
        }
        return availableProperties;
    }

    private void addProperty(PropertyDescriptor propertyDescriptor, Map<String, PropertyDescriptor> availableProperties) {
        if (availableProperties.containsKey(propertyDescriptor.getName())) {
            PropertyDescriptor existingProperty = availableProperties.get(propertyDescriptor.getName());
            if (existingProperty.getDescription() == null) {
                existingProperty.setDescription(propertyDescriptor.getDescription());
            }
            if (existingProperty.getBuildConfigurations() == null) {
                existingProperty.setBuildConfigurations(propertyDescriptor.getBuildConfigurations());
            }
            if (existingProperty.getDefaultValue() == null) {
                existingProperty.setDefaultValue(propertyDescriptor.getDefaultValue());
            }
            if (existingProperty.getValue() == null) {
                existingProperty.setValue(propertyDescriptor.getValue());
            }
            availableProperties.put(propertyDescriptor.getName(), existingProperty);
        } else {
            availableProperties.put(propertyDescriptor.getName(), propertyDescriptor);
        }
    }

    /**
     * Return a list of target that are not bound to any extension-points
     */
    public List<TargetReport> getUnboundTargets() {
        List<TargetReport> targets = new ArrayList<TargetReport>();
        for (TargetReport targetReport : targetReports) {
            if (targetReport.getExtensionPoint() == null) {
                targets.add(targetReport);
            }
        }
        return targets;
    }

    public ResolveReport getResolveReport() {
        return resolveReport;
    }

    public void setResolveReport(ResolveReport resolveReport) {
        this.resolveReport = resolveReport;
    }

    /**
     * Get attached module descriptor
     * 
     * @return attached module descriptor
     */
    public ModuleDescriptor getModuleDescriptor() {
        return moduleDescriptor;
    }

    /**
     * Set attached module descriptor
     * 
     * @param moduleDescriptor
     *            attached module descriptor
     */
    public void setModuleDescriptor(ModuleDescriptor moduleDescriptor) {
        this.moduleDescriptor = moduleDescriptor;

    }

}
