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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.ivy.Ivy;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.report.ArtifactDownloadReport;
import org.apache.ivy.core.report.ResolveReport;
import org.apache.ivy.core.resolve.ResolveOptions;
import org.apache.ivy.osgi.core.BundleInfo;
import org.apache.ivy.osgi.core.ManifestParser;
import org.apache.ivy.plugins.parser.xml.XmlModuleDescriptorWriter;
import org.apache.ivy.util.filter.Filter;
import org.apache.ivy.util.filter.FilterHelper;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.util.FileUtils;

class AntPathManager {

    private static final String IVY_XML_LOCATION = "ant/ivy.xml";

    private static final String IVY_FIXED_XML_LOCATION = "ant/ivy-fixed.xml";

    private static final String BUILD_PROPERTIES_LOCATION = "ant/build.properties";

    private static final String LOCAL_BUILD_PROPERTIES_LOCATION = "ant/local.build.properties";

    static final String CACHE_LOCATION = "ant/.cache";

    private static final String ANT_PATH_LOCATION = CACHE_LOCATION + "/ant.path";

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private AntPathManager() {
        // Utility class
    }

    static void loadProperties(Project project) {
        File localPropFile = new File(project.getBaseDir(), LOCAL_BUILD_PROPERTIES_LOCATION);
        if (localPropFile.exists()) {
            Property property = new Property();
            property.setProject(project);
            property.setFile(localPropFile);
            property.execute();
        }
        File propFile = new File(project.getBaseDir(), BUILD_PROPERTIES_LOCATION);
        if (propFile.exists()) {
            Property property = new Property();
            property.setProject(project);
            property.setFile(propFile);
            property.execute();
        }
    }

    static boolean readAntPath(Project project, List<File> antPath) {
        File ivyFile = new File(project.getBaseDir(), IVY_XML_LOCATION);
        if (!ivyFile.exists()) {
            log(project, null, "No ivy build found at " + ivyFile.getAbsolutePath(), Project.MSG_VERBOSE);
            // no ivy file, just an empty path then
            return true;
        }
        File ivyFixFile = new File(project.getBaseDir(), IVY_FIXED_XML_LOCATION);
        if (!ivyFixFile.exists()) {
            log(project, null, "No ivy fixed build file found at '" + ivyFixFile.getAbsolutePath() + "'",
                    Project.MSG_ERR);
            throw new BuildException("");
        }
        File antPathFile = new File(project.getBaseDir(), ANT_PATH_LOCATION);
        boolean update = false;
        if (!antPathFile.exists()) {
            log(project, null, "The serialized ant path '" + antPathFile + "' was not found", Project.MSG_INFO);
            update = true;
        } else if (antPathFile.lastModified() < ivyFixFile.lastModified()) {
            log(project, null, "The serialized ant path '" + antPathFile.getAbsolutePath() + "' is not up to date",
                    Project.MSG_INFO);
            update = true;
        } else {
            log(project, null, "The serialized ant path '" + antPathFile.getAbsolutePath() + "' is up to date",
                    Project.MSG_VERBOSE);
        }
        if (update) {
            updateBuild(project, null, antPath, ivyFixFile);
            return true;
        }

        String path;
        try {
            path = FileUtils.readFully(new InputStreamReader(new FileInputStream(antPathFile), UTF8));
        } catch (FileNotFoundException e) {
            throw new BuildException("The cached ant path '" + antPathFile
                    + "' has been deleted juste before reading it", e);
        } catch (IOException e) {
            throw new BuildException("The cached ant path '" + antPathFile + "' cannot be read", e);
        }
        if (path != null) {
            for (String file : path.split("\n")) {
                antPath.add(new File(file));
            }
        }
        return false;
    }

    static void updateBuild(Project project, Task task) {
        updateBuild(project, task, new ArrayList<File>(), new File(project.getBaseDir(), IVY_FIXED_XML_LOCATION));
    }

    private static void updateBuild(Project project, Task task, List<File> antPath, File ivyFixFile) {
        log(project, task, "Launching an update of the ant path", Project.MSG_INFO);
        Ivy ivy = configureBuildIvy(project);
        log(project, task, "Launching a resolve of the ivy fixed build file '" + ivyFixFile.getAbsolutePath() + "'",
                Project.MSG_INFO);
        ResolveReport report = resolve(project, ivyFixFile, ivy);
        populateAntPath(project, report, antPath);
        writePath(project, task, antPath);
    }

    private static List<File> populateAntPath(Project project, ResolveReport report, List<File> antPath) {
        // TODO make it configurable
        boolean uncompress = true;
        boolean osgi = true;
        Filter artifactfilter = FilterHelper.getArtifactTypeFilter(new String[] { "bundle", "jar" });

        for (ArtifactDownloadReport adr : report.getAllArtifactsReports()) {
            if (artifactfilter.accept(adr.getArtifact())) {
                File f = adr.getLocalFile();
                if (uncompress && adr.getUnpackedLocalFile() != null) {
                    f = adr.getUnpackedLocalFile();
                }
                addToPath(antPath, f, osgi);
            }
        }
        return antPath;
    }

    private static void writePath(Project project, Task task, List<File> antPath) {
        File antPathFile = new File(project.getBaseDir(), ANT_PATH_LOCATION);
        log(project, task, "Serializing the ant path to '" + antPathFile.getAbsolutePath() + "'", Project.MSG_INFO);
        OutputStreamWriter out;
        try {
            out = new OutputStreamWriter(new FileOutputStream(antPathFile), UTF8);
        } catch (FileNotFoundException e) {
            throw new BuildException("The cached ant path '" + antPathFile + "' could not be written", e);
        }
        try {
            for (File file : antPath) {
                out.write(file.getAbsolutePath());
                out.write('\n');
            }
        } catch (IOException e) {
            throw new BuildException("The cached ant path '" + antPathFile + "' could not be written", e);
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    private static void addToPath(List<File> antPath, File f, boolean osgi) {
        if (!osgi || !f.isDirectory()) {
            antPath.add(f);
            return;
        }
        File manifest = new File(f, "META-INF/MANIFEST.MF");
        if (!manifest.exists()) {
            antPath.add(f);
            return;
        }
        BundleInfo bundleInfo;
        try {
            bundleInfo = ManifestParser.parseManifest(manifest);
        } catch (IOException e) {
            throw new BuildException("The manifest '" + manifest + "' could not be read", e);
        } catch (ParseException e) {
            throw new BuildException("The manifest '" + manifest + "' could not be parsed", e);
        }
        @SuppressWarnings("unchecked")
        List<String> cp = bundleInfo.getClasspath();
        if (cp == null) {
            antPath.add(f);
            return;
        }
        for (int i = 0; i < cp.size(); i++) {
            String p = (String) cp.get(i);
            if (p.equals(".")) {
                antPath.add(f);
            } else {
                antPath.add(new File(f, p));
            }
        }
    }

    static void resolveBuild(Project project, Task task) {
        File ivyFile = new File(project.getBaseDir(), IVY_XML_LOCATION);
        Ivy ivy = configureBuildIvy(project);

        log(project, task, "Launching a resolve of the ivy build file '" + ivyFile.getAbsolutePath() + "'",
                Project.MSG_INFO);
        ResolveReport report = resolve(project, ivyFile, ivy);
        writeIvyFixed(project, task, ivy, report);
        List<File> antPath = new ArrayList<File>();
        populateAntPath(project, report, antPath);
        writePath(project, task, antPath);
    }

    private static void writeIvyFixed(Project project, Task task, Ivy ivy, ResolveReport report) {
        File ivyFixFile = new File(project.getBaseDir(), IVY_FIXED_XML_LOCATION);
        log(project, task, "Writing the ivy fixed file to '" + ivyFixFile.getAbsolutePath() + "'", Project.MSG_INFO);
        ModuleDescriptor md = report.toFixedModuleDescriptor(ivy.getSettings(), null);
        try {
            XmlModuleDescriptorWriter.write(md, ivyFixFile);
        } catch (IOException e) {
            throw new BuildException("Failed to write into the file '" + ivyFixFile + "' (" + e.getMessage() + ")", e);
        }
    }

    private static ResolveReport resolve(Project project, File ivyFile, Ivy ivy) {
        ResolveReport report;
        try {
            ResolveOptions options = new ResolveOptions();
            report = ivy.resolve(ivyFile, options);
        } catch (ParseException e) {
            throw new BuildException("The ivy file '" + ivyFile + "' could not be parsed", e);
        } catch (IOException e) {
            throw new BuildException("The ivy file '" + ivyFile + "' could not be read", e);
        }
        if (report.hasError()) {
            @SuppressWarnings("unchecked")
            List<String> errors = (List<String>) report.getAllProblemMessages();
            for (String error : errors) {
                project.log(error, Project.MSG_ERR);
            }
            throw new BuildException("Resolve failed");
        }
        return report;
    }

    private static Ivy configureBuildIvy(Project project) {
        Ivy ivy = Ivy.newInstance();

        File ivysettingsFile = new File(project.getBaseDir(), "ant/ivysettings.xml");
        if (ivysettingsFile.exists()) {
            try {
                ivy.configure(ivysettingsFile);
            } catch (ParseException e) {
                throw new BuildException("The ivysettings file '" + ivysettingsFile + "' could not be parsed", e);
            } catch (IOException e) {
                throw new BuildException("The ivysettings file '" + ivysettingsFile + "' could not be read", e);
            }
        } else {
            try {
                ivy.configureDefault();
            } catch (ParseException e) {
                throw new BuildException("The default ivysettings file could not be parsed", e);
            } catch (IOException e) {
                throw new BuildException("The default ivysettings file could not be read", e);
            }
        }
        return ivy;
    }

    private static void log(Project project, Task task, String message, int level) {
        if (task == null) {
            project.log("== Ant Path Manager == " + message, level);
        } else {
            project.log(task, message, level);
        }
    }
}
