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

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.easyant.core.descriptor.PropertyDescriptor;
import org.apache.ivy.Ivy;
import org.apache.ivy.core.cache.ArtifactOrigin;
import org.apache.ivy.core.module.descriptor.Configuration;
import org.apache.ivy.core.module.descriptor.License;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.module.id.ModuleId;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.report.ArtifactDownloadReport;
import org.apache.ivy.core.report.ConfigurationResolveReport;
import org.apache.ivy.core.report.MetadataArtifactDownloadReport;
import org.apache.ivy.core.resolve.IvyNode;
import org.apache.ivy.core.resolve.IvyNodeCallers.Caller;
import org.apache.ivy.core.resolve.IvyNodeEviction.EvictionData;
import org.apache.ivy.util.Message;
import org.apache.ivy.util.XMLHelper;

/**
 * XmlReportWriter allows to write ResolveReport in an xml format.
 */
public class XMLEasyAntReportWriter {

    static final String REPORT_ENCODING = "UTF-8";
    private boolean displaySubProperties = false;

    public void output(EasyAntReport easyAntReport, OutputStream stream) {
        for (String conf : easyAntReport.getResolveReport().getConfigurations()) {
            output(easyAntReport, easyAntReport.getResolveReport().getConfigurationReport(conf), stream);
        }
    }

    public void output(EasyAntReport easyAntReport, ConfigurationResolveReport report, OutputStream stream) {
        OutputStreamWriter encodedOutStream;
        try {
            encodedOutStream = new OutputStreamWriter(stream, REPORT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(REPORT_ENCODING + " is not known on your jvm", e);
        }
        PrintWriter out = new PrintWriter(new BufferedWriter(encodedOutStream));
        ModuleRevisionId mrid = report.getModuleDescriptor().getModuleRevisionId();
        // out.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        out.println("<?xml version=\"1.0\" encoding=\"" + REPORT_ENCODING + "\"?>");
        out.println("<?xml-stylesheet type=\"text/xsl\" href=\"easyant-report.xsl\"?>");
        out.println("<easyant-report version=\"1.0\">");
        out.println("\t<info");
        out.println("\t\torganisation=\"" + XMLHelper.escape(mrid.getOrganisation()) + "\"");
        out.println("\t\tmodule=\"" + XMLHelper.escape(mrid.getName()) + "\"");
        out.println("\t\trevision=\"" + XMLHelper.escape(mrid.getRevision()) + "\"");
        if (mrid.getBranch() != null) {
            out.println("\t\tbranch=\"" + XMLHelper.escape(mrid.getBranch()) + "\"");
        }
        Map<?, ?> extraAttributes = mrid.getExtraAttributes();
        for (Map.Entry<?, ?> entry : extraAttributes.entrySet()) {
            out.println("\t\textra-" + entry.getKey() + "=\"" + XMLHelper.escape(entry.getValue().toString()) + "\"");
        }
        out.println("\t\tconf=\"" + XMLHelper.escape(report.getConfiguration()) + "\"");
        out.println("\t\tdate=\"" + Ivy.DATE_FORMAT.format(report.getDate()) + "\"/>");
        out.println("\t<description>");
        out.println(report.getModuleDescriptor().getDescription());
        out.println("\t</description>");
        out.println("\t<configurations>");
        StringBuffer sb = new StringBuffer();
        for (Configuration configuration : easyAntReport.getModuleDescriptor().getConfigurations()) {
            sb.append("\t\t<configuration name=\"");
            sb.append(XMLHelper.escape(configuration.getName()));
            sb.append("\" description=\"");
            sb.append(XMLHelper.escape(configuration.getDescription()));
            sb.append("\" extends=\"");
            sb.append(XMLHelper.escape(Arrays.toString(configuration.getExtends())));
            sb.append("\" deprecated=\"");
            sb.append(XMLHelper.escape(configuration.getDeprecated()));
            sb.append("\" visibility=\"");
            sb.append(XMLHelper.escape(configuration.getVisibility().toString()));
            sb.append("\"/>");
            out.println(sb.toString());
        }
        out.println("\t</configurations>");

        out.println("\t<dependencies>");

        // create a list of ModuleRevisionIds indicating the position for each dependency
        List<?> dependencies = new ArrayList(report.getModuleRevisionIds());

        for (Iterator<?> iter = report.getModuleIds().iterator(); iter.hasNext();) {
            ModuleId mid = (ModuleId) iter.next();
            out.println("\t\t<module organisation=\"" + XMLHelper.escape(mid.getOrganisation()) + "\"" + " name=\""
                    + XMLHelper.escape(mid.getName()) + "\">");
            for (Iterator<?> it2 = report.getNodes(mid).iterator(); it2.hasNext();) {
                IvyNode dep = (IvyNode) it2.next();
                ouputRevision(report, out, dependencies, dep, easyAntReport);
            }
            out.println("\t\t</module>");
        }
        out.println("\t</dependencies>");
        outputEasyAntModuleInfos(easyAntReport, out);

        out.println("</easyant-report>");
        out.flush();
    }

    private void ouputRevision(ConfigurationResolveReport report, PrintWriter out, List dependencies, IvyNode dep,
            EasyAntReport easyAntReport) {
        Map<?, ?> extraAttributes;
        ModuleDescriptor md = null;
        if (dep.getModuleRevision() != null) {
            md = dep.getModuleRevision().getDescriptor();
        }
        StringBuffer details = new StringBuffer();
        if (dep.isLoaded()) {
            details.append(" status=\"");
            details.append(XMLHelper.escape(dep.getDescriptor().getStatus()));
            details.append("\" pubdate=\"");
            details.append(Ivy.DATE_FORMAT.format(new Date(dep.getPublication())));
            details.append("\" resolver=\"");
            details.append(XMLHelper.escape(dep.getModuleRevision().getResolver().getName()));
            details.append("\" artresolver=\"");
            details.append(XMLHelper.escape(dep.getModuleRevision().getArtifactResolver().getName()));
            details.append("\"");
        }
        if (dep.isEvicted(report.getConfiguration())) {
            EvictionData ed = dep.getEvictedData(report.getConfiguration());
            if (ed.getConflictManager() != null) {
                details.append(" evicted=\"").append(XMLHelper.escape(ed.getConflictManager().toString())).append("\"");
            } else {
                details.append(" evicted=\"transitive\"");
            }
            details.append(" evicted-reason=\"").append(XMLHelper.escape(ed.getDetail() == null ? "" : ed.getDetail()))
                    .append("\"");
        }
        if (dep.hasProblem()) {
            details.append(" error=\"").append(XMLHelper.escape(dep.getProblem().getMessage())).append("\"");
        }
        if (md != null && md.getHomePage() != null) {
            details.append(" homepage=\"").append(XMLHelper.escape(md.getHomePage())).append("\"");
        }
        extraAttributes = md != null ? md.getExtraAttributes() : dep.getResolvedId().getExtraAttributes();
        for (Iterator<?> iterator = extraAttributes.keySet().iterator(); iterator.hasNext();) {
            String attName = (String) iterator.next();
            details.append(" extra-").append(attName).append("=\"")
                    .append(XMLHelper.escape(extraAttributes.get(attName).toString())).append("\"");
        }
        String defaultValue = dep.getDescriptor() != null ? " default=\"" + dep.getDescriptor().isDefault() + "\"" : "";
        int position = dependencies.indexOf(dep.getResolvedId());
        out.println("\t\t\t<revision name=\""
                + XMLHelper.escape(dep.getResolvedId().getRevision())
                + "\""
                + (dep.getResolvedId().getBranch() == null ? "" : " branch=\""
                        + XMLHelper.escape(dep.getResolvedId().getBranch()) + "\"") + details + " downloaded=\""
                + dep.isDownloaded() + "\"" + " searched=\"" + dep.isSearched() + "\"" + defaultValue + " conf=\""
                + toString(dep.getConfigurations(report.getConfiguration())) + "\"" + " position=\"" + position + "\">");
        if (md != null) {
            License[] licenses = md.getLicenses();
            for (int i = 0; i < licenses.length; i++) {
                String lurl;
                if (licenses[i].getUrl() != null) {
                    lurl = " url=\"" + XMLHelper.escape(licenses[i].getUrl()) + "\"";
                } else {
                    lurl = "";
                }
                out.println("\t\t\t\t<license name=\"" + XMLHelper.escape(licenses[i].getName()) + "\"" + lurl + "/>");
            }
        }
        outputMetadataArtifact(out, dep);
        outputEvictionInformation(report, out, dep);
        outputCallers(report, out, dep);
        outputArtifacts(report, out, dep);
        out.println("\t\t\t</revision>");
    }

    private void outputEvictionInformation(ConfigurationResolveReport report, PrintWriter out, IvyNode dep) {
        if (dep.isEvicted(report.getConfiguration())) {
            EvictionData ed = dep.getEvictedData(report.getConfiguration());
            Collection<?> selected = ed.getSelected();
            if (selected != null) {
                for (Iterator<?> it3 = selected.iterator(); it3.hasNext();) {
                    IvyNode sel = (IvyNode) it3.next();
                    out.println("\t\t\t\t<evicted-by rev=\"" + XMLHelper.escape(sel.getResolvedId().getRevision())
                            + "\"/>");
                }
            }
        }
    }

    private void outputMetadataArtifact(PrintWriter out, IvyNode dep) {
        if (dep.getModuleRevision() != null) {
            MetadataArtifactDownloadReport madr = dep.getModuleRevision().getReport();
            out.print("\t\t\t\t<metadata-artifact");
            out.print(" status=\"" + XMLHelper.escape(madr.getDownloadStatus().toString()) + "\"");
            out.print(" details=\"" + XMLHelper.escape(madr.getDownloadDetails()) + "\"");
            out.print(" size=\"" + madr.getSize() + "\"");
            out.print(" time=\"" + madr.getDownloadTimeMillis() + "\"");
            if (madr.getLocalFile() != null) {
                out.print(" location=\"" + XMLHelper.escape(madr.getLocalFile().getAbsolutePath()) + "\"");
            }

            out.print(" searched=\"" + madr.isSearched() + "\"");
            if (madr.getOriginalLocalFile() != null) {
                out.print(" original-local-location=\""
                        + XMLHelper.escape(madr.getOriginalLocalFile().getAbsolutePath()) + "\"");
            }

            ArtifactOrigin origin = madr.getArtifactOrigin();
            if (origin != null) {
                out.print(" origin-is-local=\"" + String.valueOf(origin.isLocal()) + "\"");
                out.print(" origin-location=\"" + XMLHelper.escape(origin.getLocation()) + "\"");
            }
            out.println("/>");

        }
    }

    private void outputCallers(ConfigurationResolveReport report, PrintWriter out, IvyNode dep) {
        Caller[] callers = dep.getCallers(report.getConfiguration());
        for (int i = 0; i < callers.length; i++) {
            StringBuffer callerDetails = new StringBuffer();
            Map<?, ?> callerExtraAttributes = callers[i].getDependencyDescriptor().getExtraAttributes();
            for (Iterator<?> iterator = callerExtraAttributes.keySet().iterator(); iterator.hasNext();) {
                String attName = (String) iterator.next();
                callerDetails.append(" extra-").append(attName).append("=\"")
                        .append(XMLHelper.escape(callerExtraAttributes.get(attName).toString())).append("\"");
            }

            out.println("\t\t\t\t<caller organisation=\""
                    + XMLHelper.escape(callers[i].getModuleRevisionId().getOrganisation())
                    + "\""
                    + " name=\""
                    + XMLHelper.escape(callers[i].getModuleRevisionId().getName())
                    + "\""
                    + " conf=\""
                    + XMLHelper.escape(toString(callers[i].getCallerConfigurations()))
                    + "\""
                    + " rev=\""
                    + XMLHelper.escape(callers[i].getAskedDependencyId(dep.getData()).getRevision())
                    + "\""
                    + " rev-constraint-default=\""
                    + XMLHelper.escape(callers[i].getDependencyDescriptor().getDependencyRevisionId().getRevision())
                    + "\""
                    + " rev-constraint-dynamic=\""
                    + XMLHelper.escape(callers[i].getDependencyDescriptor().getDynamicConstraintDependencyRevisionId()
                            .getRevision()) + "\"" + " callerrev=\""
                    + XMLHelper.escape(callers[i].getModuleRevisionId().getRevision()) + "\"" + callerDetails + "/>");
        }
    }

    private void outputArtifacts(ConfigurationResolveReport report, PrintWriter out, IvyNode dep) {
        Map<?, ?> extraAttributes;
        ArtifactDownloadReport[] adr = report.getDownloadReports(dep.getResolvedId());
        out.println("\t\t\t\t<artifacts>");
        for (int i = 0; i < adr.length; i++) {
            out.print("\t\t\t\t\t<artifact name=\"" + XMLHelper.escape(adr[i].getName()) + "\" type=\""
                    + XMLHelper.escape(adr[i].getType()) + "\" ext=\"" + XMLHelper.escape(adr[i].getExt()) + "\"");
            extraAttributes = adr[i].getArtifact().getExtraAttributes();
            for (Iterator<?> iterator = extraAttributes.keySet().iterator(); iterator.hasNext();) {
                String attName = (String) iterator.next();
                out.print(" extra-" + attName + "=\"" + XMLHelper.escape(extraAttributes.get(attName).toString())
                        + "\"");
            }
            out.print(" status=\"" + XMLHelper.escape(adr[i].getDownloadStatus().toString()) + "\"");
            out.print(" details=\"" + XMLHelper.escape(adr[i].getDownloadDetails()) + "\"");
            out.print(" size=\"" + adr[i].getSize() + "\"");
            out.print(" time=\"" + adr[i].getDownloadTimeMillis() + "\"");
            if (adr[i].getLocalFile() != null) {
                out.print(" location=\"" + XMLHelper.escape(adr[i].getLocalFile().getAbsolutePath()) + "\"");
            }

            ArtifactOrigin origin = adr[i].getArtifactOrigin();
            if (origin != null) {
                out.println(">");
                out.println("\t\t\t\t\t\t<origin-location is-local=\"" + String.valueOf(origin.isLocal()) + "\""
                        + " location=\"" + XMLHelper.escape(origin.getLocation()) + "\"/>");
                out.println("\t\t\t\t\t</artifact>");
            } else {
                out.println("/>");
            }
        }
        out.println("\t\t\t\t</artifacts>");
    }

    private String toString(String[] strs) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < strs.length; i++) {
            buf.append(strs[i]);
            if (i + 1 < strs.length) {
                buf.append(", ");
            }
        }
        return XMLHelper.escape(buf.toString());
    }

    public void setDisplaySubProperties(boolean displaySubProperties) {
        this.displaySubProperties = displaySubProperties;

    }

    private void outputEasyAntModuleInfos(EasyAntReport easyAntReport, PrintWriter out) {
        out.println("\t<easyant>");
        // targets
        outputTargets(easyAntReport, out);
        outputExtensionPoints(easyAntReport, out);
        outputImportedModules(easyAntReport, out);
        outputParameters(easyAntReport, out);
        outputProperties(easyAntReport, out);
        out.println("\t</easyant>");

    }

    private void outputProperties(EasyAntReport easyAntReport, PrintWriter out) {
        out.println("\t\t<properties>");
        Map<String, PropertyDescriptor> properties;
        if (displaySubProperties) {
            properties = easyAntReport.getAvailableProperties();
        } else {
            properties = easyAntReport.getPropertyDescriptors();
        }

        for (Entry<String, PropertyDescriptor> entry : properties.entrySet()) {
            PropertyDescriptor propertyDescriptor = entry.getValue();

            StringBuffer param = new StringBuffer();
            param.append("\t\t\t<property name=\"");
            param.append(propertyDescriptor.getName());
            param.append("\"");
            if (propertyDescriptor.getDescription() != null) {
                param.append(" description=\"");
                param.append(propertyDescriptor.getDescription());
                param.append("\"");
            }
            param.append(" required=\"");
            param.append(propertyDescriptor.isRequired());
            param.append("\"");
            if (propertyDescriptor.getDefaultValue() != null) {
                param.append(" default=\"");
                param.append(propertyDescriptor.getDefaultValue());
                param.append("\"");
            }
            if (propertyDescriptor.getValue() != null) {
                param.append(" value=\"");
                param.append(propertyDescriptor.getValue());
                param.append("\"");
            }
            param.append("/>");
            out.println(param.toString());

        }
        out.println("\t\t</properties>");
    }

    private void outputParameters(EasyAntReport easyAntReport, PrintWriter out) {
        out.println("\t\t<parameters>");
        for (ParameterReport paramReport : easyAntReport.getParameterReports()) {
            StringBuffer param = new StringBuffer();

            if (!ParameterType.PROPERTY.equals(paramReport.getType())) {
                if (ParameterType.PATH.equals(paramReport.getType())) {
                    param.append("\t\t\t<path name=\"");
                }
                if (ParameterType.FILESET.equals(paramReport.getType())) {
                    param.append("\t\t\t<fileset name=\"");
                }
                param.append(paramReport.getName());
                param.append("\"");
                if (paramReport.getDescription() != null) {
                    param.append(" description=\"");
                    param.append(paramReport.getDescription());
                    param.append("\"");
                }
                if (paramReport.isRequired()) {
                    param.append(" required=\"");
                    param.append(paramReport.isRequired());
                    param.append("\"");
                }
                param.append("/>");
            }
            out.println(param);
        }

        out.println("\t\t</parameters>");
    }

    private void outputImportedModules(EasyAntReport easyAntReport, PrintWriter out) {
        out.println("\t\t<imports>");
        for (ImportedModuleReport importedModuleReport : easyAntReport.getImportedModuleReports()) {
            StringBuffer importedModule = new StringBuffer();
            try {
                ModuleRevisionId mrid = ModuleRevisionId.parse(importedModuleReport.getModuleMrid());
                importedModule.append("\t\t\t<import organisation=\"").append(mrid.getOrganisation())
                        .append("\" name=\"").append(mrid.getName()).append("\" revision=\"")
                        .append(mrid.getRevision()).append("\" type=\"").append(importedModuleReport.getMode())
                        .append("\"");

            } catch (IllegalArgumentException e) {
                Message.debug("Unable to parse " + importedModuleReport.getModuleMrid());
                importedModule.append("                        <import organisation=\"")
                        .append(importedModuleReport.getModuleMrid()).append("\" name=\"").append("null")
                        .append("\" revision=\"").append("null").append("\" type=\"")
                        .append(importedModuleReport.getMode()).append("\"");

            }
            importedModule.append(" mandatory=\"");
            importedModule.append(importedModuleReport.isMandatory());
            importedModule.append("\"");
            if (importedModuleReport.getAs() != null) {
                importedModule.append(" as=\"");
                importedModule.append(importedModuleReport.getAs());
                importedModule.append("\"");
            }
            importedModule.append(">");
            out.println(importedModule.toString());
            if (importedModuleReport.getEasyantReport() != null) {
                outputEasyAntModuleInfos(importedModuleReport.getEasyantReport(), out);
            }
            out.println("\t\t\t</import>");

        }
        out.println("\t\t</imports>");

    }

    private void outputExtensionPoints(EasyAntReport easyAntReport, PrintWriter out) {
        out.println("\t\t<extension-points>");
        for (ExtensionPointReport extensionPointReport : easyAntReport.getExtensionPointReports()) {
            StringBuffer extensionPoint = new StringBuffer();
            extensionPoint.append("\t\t\t<extension-point name=\"").append(extensionPointReport.getName()).append("\"");
            if (extensionPointReport.getDescription() != null) {
                extensionPoint.append(" description=\"");
                extensionPoint.append(extensionPointReport.getDescription());
                extensionPoint.append("\"");
            }
            if (extensionPointReport.getDepends() != null) {
                extensionPoint.append(" depends=\"");
                extensionPoint.append(extensionPointReport.getDepends());
                extensionPoint.append("\"");
            }
            extensionPoint.append("/>");
            out.println(extensionPoint.toString());
        }
        out.println("\t\t</extension-points>");
    }

    private void outputTargets(EasyAntReport easyAntReport, PrintWriter out) {
        out.println("\t\t<targets>");
        for (TargetReport targetReport : easyAntReport.getTargetReports()) {
            StringBuffer target = new StringBuffer();
            target.append("\t\t\t<target name=\"").append(targetReport.getName()).append("\"");
            if (targetReport.getDescription() != null) {
                target.append(" description=\"");
                target.append(targetReport.getDescription());
                target.append("\"");
            }
            if (targetReport.getDepends() != null) {
                target.append(" depends=\"");
                target.append(targetReport.getDepends());
                target.append("\"");
            }
            if (targetReport.getIfCase() != null) {
                target.append(" if=\"");
                target.append(targetReport.getIfCase());
                target.append("\"");
            }
            if (targetReport.getExtensionPoint() != null) {
                target.append(" extensionOf=\"");
                target.append(targetReport.getExtensionPoint());
                target.append("\"");
            }
            if (targetReport.getUnlessCase() != null) {
                target.append(" unless=\"");
                target.append(targetReport.getUnlessCase());
                target.append("\"");
            }
            target.append("/>");
            out.println(target.toString());
        }
        out.println("\t\t</targets>");
    }
}
