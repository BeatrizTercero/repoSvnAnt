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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
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
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;

class AntPathManager {

    private static final String IVY_XML_LOCATION = "ant/ivy.xml";

	private static final String IVY_FIXED_XML_LOCATION = "ant/ivy-fixed.xml";

	private static final String ANT_PATH_LOCATION = "ant/ant.path";

    static final String OSGI_STORAGE_LOCATION = "ant/osgi-storage";

    public static final String REFID_UPDATE_BUILD = "antdsl.update-build";

    private static final Charset UTF8 = Charset.forName("UTF-8");

    void readAntPath(Project project, Path antPath) {
        File ivyFile = new File(project.getBaseDir(), IVY_XML_LOCATION);
        if (!ivyFile.exists()) {
        	// no ivy file, just an empty path then
        	return;
        }
        File ivyFixFile = new File(project.getBaseDir(), IVY_FIXED_XML_LOCATION);
        Boolean updateBuild = (Boolean) project.getReference(REFID_UPDATE_BUILD);
        if (Boolean.TRUE.equals(updateBuild) || !ivyFixFile.exists() || ivyFixFile.lastModified() < ivyFile.lastModified()) {
            updateBuild(project, antPath);
            return;
        }
        File antPathFile = new File(project.getBaseDir(), ANT_PATH_LOCATION);
        if (!antPathFile.exists() || antPathFile.lastModified() < ivyFixFile.lastModified()) {
            Ivy ivy = configureBuildIvy(project);
            ResolveReport report = resolveBuild(project, ivyFixFile, ivy);
            Path ivyPath = getIvyBuildPath(project, report);
            writePath(project, ivyPath);
            antPath.add(ivyPath);
            return;
        }

        String path;
        try {
            path = FileUtils.readFully(new FileReader(antPathFile));
        } catch (FileNotFoundException e) {
            throw new BuildException("The cached ant path " + antPathFile + " has been deleted juste before reading it", e);
        } catch (IOException e) {
            throw new BuildException("The cached ant path " + antPathFile + " cannot be read", e);
        }
        antPath.createPathElement().setPath(path);
    }

    private Path getIvyBuildPath(Project project, ResolveReport report) {
        // TODO make it configurable
        boolean uncompress = true;
        boolean osgi = true;
        Filter artifactfilter = FilterHelper.getArtifactTypeFilter(new String[] {"bundle", "jar"});

        Path ivyPath = new Path(project);
        for (ArtifactDownloadReport adr : report.getAllArtifactsReports()) {
            if (artifactfilter.accept(adr.getArtifact())) {
                File f = adr.getLocalFile();
                if (uncompress && adr.getUncompressedLocalDir() != null) {
                    f = adr.getUncompressedLocalDir();
                }
                addToPath(ivyPath, f, osgi);
            }
        }
        return ivyPath;
    }

    private void writePath(Project project, Path ivyPath) {
        File antPathFile = new File(project.getBaseDir(), ANT_PATH_LOCATION);
        FileOutputStream out;
        try {
            out = new FileOutputStream(antPathFile);
        } catch (FileNotFoundException e) {
            throw new BuildException("The cached ant path " + antPathFile + "could not be written", e);
        }
        try {
            out.write(ivyPath.toString().getBytes(UTF8.name()));
        } catch (IOException e) {
            throw new BuildException("The cached ant path " + antPathFile + "could not be written", e);
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    private void addToPath(Path path, File f, boolean osgi) {
        if (!osgi || !f.isDirectory()) {
            path.createPathElement().setLocation(f);
            return;
        }
        File manifest = new File(f, "META-INF/MANIFEST.MF");
        if (!manifest.exists()) {
            path.createPathElement().setLocation(f);
            return;
        }
        BundleInfo bundleInfo;
        try {
            bundleInfo = ManifestParser.parseManifest(manifest);
        } catch (IOException e) {
            throw new BuildException("The manifest " + manifest + " could not be read", e);
        } catch (ParseException e) {
            throw new BuildException("The manifest " + manifest + " could not be parsed", e);
        }
        @SuppressWarnings("unchecked")
        List<String> cp = bundleInfo.getClasspath();
        if (cp == null) {
            path.createPathElement().setLocation(f);
            return;
        }
        for (int i = 0; i < cp.size(); i++) {
            String p = (String) cp.get(i);
            if (p.equals(".")) {
                path.createPathElement().setLocation(f);
            } else {
                path.createPathElement().setLocation(new File(f, p));
            }
        }
    }

    private void updateBuild(Project project, Path antPath) {
        File ivyFile = new File(project.getBaseDir(), IVY_XML_LOCATION);
        if (!ivyFile.exists()) {
            return;
        }

        Ivy ivy = configureBuildIvy(project);

        ResolveReport report = resolveBuild(project, ivyFile, ivy);
        writeIvyFixed(project, ivy, report);
        Path ivyPath = getIvyBuildPath(project, report);
        writePath(project, ivyPath);
        antPath.add(ivyPath);
    }

    private void writeIvyFixed(Project project, Ivy ivy, ResolveReport report) {
        File ivyFixFile = new File(project.getBaseDir(), IVY_FIXED_XML_LOCATION);
        ModuleDescriptor md = report.toFixedModuleDescriptor(ivy.getSettings(), null);
        try {
            XmlModuleDescriptorWriter.write(md, ivyFixFile);
        } catch (IOException e) {
            throw new BuildException("Failed to write into the file " + ivyFixFile + " (" + e.getMessage() + ")", e);
        }
    }

    private ResolveReport resolveBuild(Project project, File ivyFile, Ivy ivy) {
        ResolveReport report;
        try {
            ResolveOptions options = new ResolveOptions();
            options.setUncompress(true);
            report = ivy.resolve(ivyFile, options);
        } catch (ParseException e) {
            throw new BuildException("The ivy file " + ivyFile + " could not be parsed", e);
        } catch (IOException e) {
            throw new BuildException("The ivy file " + ivyFile + " could not be read", e);
        }
        if (report.hasError()) {
            @SuppressWarnings("unchecked")
            List<String> errors = (List<String>) report.getAllProblemMessages();
            for (String error : errors) {
                project.log(error, Project.MSG_ERR);
            }
            throw new BuildException("Resolve of the build path failed");
        }
        return report;
    }

    private Ivy configureBuildIvy(Project project) {
        Ivy ivy = Ivy.newInstance();

        File ivysettingsFile = new File(project.getBaseDir(), "ant/ivysettings.xml");
        if (ivysettingsFile.exists()) {
            try {
                ivy.configure(ivysettingsFile);
            } catch (ParseException e) {
                throw new BuildException("The ivysettings file " + ivysettingsFile + " could not be parsed", e);
            } catch (IOException e) {
                throw new BuildException("The ivysettings file " + ivysettingsFile + " could not be read", e);
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

}
