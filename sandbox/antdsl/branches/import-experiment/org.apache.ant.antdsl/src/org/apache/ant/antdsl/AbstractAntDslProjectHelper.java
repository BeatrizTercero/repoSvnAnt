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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.UUID;
import java.util.Vector;

import org.apache.ant.antdsl.expr.AntExpression;
import org.apache.ant.antdsl.expr.AntExpressionCondition;
import org.apache.ant.antdsl.expr.ConditionAntExpression;
import org.apache.ant.antdsl.expr.func.FunctionRegistry;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ExtensionPoint;
import org.apache.tools.ant.MagicNames;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.ProjectHelperRepository;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.helper.AntXMLContext;
import org.apache.tools.ant.taskdefs.Taskdef;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.URLProvider;
import org.apache.tools.ant.types.resources.URLResource;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.zip.ZipFile;
import org.osgi.framework.BundleException;

public abstract class AbstractAntDslProjectHelper extends ProjectHelper {

    private static final String REFID_CONTEXT = "antdsl.parsingcontext";

    public static final String REFID_FUNCTION_REGISTRY = "antdsl.function.registry";

    public static final String REFID_OSGI_FRAMEWORK_MANAGER = "antdsl.osgi.framework.manager";

    public static final String REFID_CLASSLOADER_STACK = "antdsl.classloader.stack";

    public String getDefaultBuildFile() {
        return "build.ant";
    }

    public boolean canParseBuildFile(Resource buildFile) {
        return buildFile.getName().toLowerCase().endsWith(".ant");
    }

    public void parse(Project project, Object source) throws BuildException {
        Vector<Object> stack = getImportStack();
        stack.addElement(source);

        AntDslContext context = null;
        context = project.getReference(REFID_CONTEXT);
        if (context == null) {
            context = new AntDslContext(project);
            project.addReference(REFID_CONTEXT, context);
        }

        FunctionRegistry functionRegistry = project.getReference(REFID_FUNCTION_REGISTRY);
        if (functionRegistry == null) {
            functionRegistry = new FunctionRegistry();
            project.addReference(REFID_FUNCTION_REGISTRY, functionRegistry);
        }

        OSGiFrameworkManager osgiFrameworkManager = project.getReference(REFID_OSGI_FRAMEWORK_MANAGER);
        if (osgiFrameworkManager == null) {
            try {
                osgiFrameworkManager = new OSGiFrameworkManager();
            } catch (BundleException e) {
                throw new BuildException("Unable to boot the OSGi framwork (" + e.getMessage() + ")", e);
            }
            project.addReference(REFID_OSGI_FRAMEWORK_MANAGER, osgiFrameworkManager);
        }

        Stack<ClassLoader> classloaderStack = project.getReference(REFID_CLASSLOADER_STACK);
        if (classloaderStack == null) {
            classloaderStack = new Stack<ClassLoader>();
            project.addReference(REFID_CLASSLOADER_STACK, classloaderStack);
        }

        if (getImportStack().size() > 1) {
            context.setIgnoreProjectTag(true);
            Target currentTarget = context.getCurrentTarget();
            Target currentImplicit = context.getImplicitTarget();
            Map<String, Target> currentTargets = context.getCurrentTargets();
            try {
                Target newCurrent = new Target();
                newCurrent.setProject(project);
                newCurrent.setName("");
                context.setCurrentTarget(newCurrent);
                context.setCurrentTargets(new HashMap<String, Target>());
                context.setImplicitTarget(newCurrent);
                parse(project, source, context);
                newCurrent.execute();
            } finally {
                context.setCurrentTarget(currentTarget);
                context.setImplicitTarget(currentImplicit);
                context.setCurrentTargets(currentTargets);
            }
        } else {
            // top level file
            classloaderStack.push(getOSGiFrameworkManager(project).getGodClassLoader());

            context.setCurrentTargets(new HashMap<String, Target>());
            parse(project, source, context);

            // Execute the top-level target
            context.getImplicitTarget().execute();

            // resolve extensionOf attributes
            for (String[] extensionInfo : getExtensionStack()) {
                String tgName = extensionInfo[0];
                String name = extensionInfo[1];
                OnMissingExtensionPoint missingBehaviour = OnMissingExtensionPoint.valueOf(extensionInfo[2]);
                Hashtable<String, Target> projectTargets = project.getTargets();
                if (!projectTargets.containsKey(tgName)) {
                    String message = "can't add target " + name + " to extension-point " + tgName + " because the extension-point is unknown.";
                    if (missingBehaviour == OnMissingExtensionPoint.FAIL) {
                        throw new BuildException(message);
                    } else if (missingBehaviour == OnMissingExtensionPoint.WARN) {
                        Target target = (Target) projectTargets.get(name);
                        context.getProject().log(target, "Warning: " + message, Project.MSG_WARN);
                    }
                } else {
                    Target t = (Target) projectTargets.get(tgName);
                    if (!(t instanceof ExtensionPoint)) {
                        throw new BuildException("referenced target " + tgName + " is not an extension-point");
                    }
                    t.addDependency(name);
                }
            }
        }
    }

    private OSGiFrameworkManager getOSGiFrameworkManager(Project p) {
        return p.getReference(REFID_OSGI_FRAMEWORK_MANAGER);
    }

    private Stack<ClassLoader> getClassloaderStack(Project p) {
        return p.getReference(REFID_CLASSLOADER_STACK);
    }

    private void parse(Project project, Object source, AntDslContext context) {
        File buildFile = null;
        URL url = null;
        String buildFileName = null;

        if (source instanceof File) {
            buildFile = (File) source;
        } else if (source instanceof URL) {
            url = (URL) source;
        } else if (source instanceof Resource) {
            FileProvider fp = (FileProvider) ((Resource) source).as(FileProvider.class);
            if (fp != null) {
                buildFile = fp.getFile();
            } else {
                URLProvider up = (URLProvider) ((Resource) source).as(URLProvider.class);
                if (up != null) {
                    url = up.getURL();
                }
            }
        }
        if (buildFile != null) {
            buildFile = FileUtils.getFileUtils().normalize(buildFile.getAbsolutePath());
            context.setBuildFile(buildFile);
            buildFileName = buildFile.toString();
        } else if (url != null) {
            try {
                context.setBuildFile((File) null);
                context.setBuildFile(url);
            } catch (java.net.MalformedURLException ex) {
                throw new BuildException(ex);
            }
            buildFileName = url.toString();
        } else {
            throw new BuildException("Source " + source.getClass().getName() + " not supported by this plugin");
        }

        try {
            InputStream in = null;

            if (buildFile != null) {
                in = new FileInputStream(buildFile);
            } else {
                String uri = url.toString();
                int pling = -1;
                if (uri.startsWith("jar:file") && (pling = uri.indexOf("!/")) > -1) {
                    ZipFile zf = new ZipFile(org.apache.tools.ant.launch.Locator.fromJarURI(uri), "UTF-8");
                    in = zf.getInputStream(zf.getEntry(uri.substring(pling + 1)));
                } else {
                    in = url.openStream();
                }
            }

            doParse(in, buildFileName, project, context);
        } catch (IOException e) {
            throw new BuildException("Error reading antdsl file " + buildFileName + ": " + e.getMessage(), e);
        }

    }

    abstract protected void doParse(InputStream in, String buildFileName, Project project, AntDslContext context) throws IOException;

    public void setupProject(Project project, AntDslContext context, String name, String basedir, String def) {
        boolean nameAttributeSet = false;

        if (name != null) {
            context.setCurrentProjectName(name);
            nameAttributeSet = true;
        }

        if (!context.isIgnoringProjectTag()) {
            if (name != null) {
                project.setName(name);
                project.addReference(name, project);
            }
            if (basedir != null) {
                project.setBasedir(basedir);
            }
            if (def != null) {
                project.setDefault(def);
            }
        } else {
            if (isInIncludeMode() && !"".equals(name) && (getCurrentTargetPrefix() == null || getCurrentTargetPrefix().length() == 0)) {
                // help nested include tasks
                setCurrentTargetPrefix(name);
            }
        }

        String antFileProp = MagicNames.ANT_FILE + "." + context.getCurrentProjectName();
        String dup = project.getProperty(antFileProp);
        String typeProp = MagicNames.ANT_FILE_TYPE + "." + context.getCurrentProjectName();
        String dupType = project.getProperty(typeProp);
        if (dup != null && nameAttributeSet) {
            Object dupFile = null;
            Object contextFile = null;
            if (MagicNames.ANT_FILE_TYPE_URL.equals(dupType)) {
                try {
                    dupFile = new URL(dup);
                } catch (java.net.MalformedURLException mue) {
                    throw new BuildException("failed to parse " + dup + " as URL while looking" + " at a duplicate project" + " name.", mue);
                }
                contextFile = context.getBuildFileURL();
            } else {
                dupFile = new File(dup);
                contextFile = context.getBuildFile();
            }

            if (context.isIgnoringProjectTag() && !dupFile.equals(contextFile)) {
                project.log("Duplicated project name in import. Project " + context.getCurrentProjectName() + " defined first in " + dup
                        + " and again in " + contextFile, Project.MSG_WARN);
            }
        }
        if (nameAttributeSet) {
            if (context.getBuildFile() != null) {
                project.setUserProperty(antFileProp, context.getBuildFile().toString());
                project.setUserProperty(typeProp, MagicNames.ANT_FILE_TYPE_FILE);
            } else if (context.getBuildFileURL() != null) {
                project.setUserProperty(antFileProp, context.getBuildFileURL().toString());
                project.setUserProperty(typeProp, MagicNames.ANT_FILE_TYPE_URL);
            }
        }
        if (context.isIgnoringProjectTag()) {
            // no further processing
            return;
        }
        // set explicitly before starting ?
        if (project.getProperty("basedir") != null) {
            project.setBasedir(project.getProperty("basedir"));
        } else {
            // Default for baseDir is the location of the build file.
            if (basedir == null) {
                project.setBasedir(context.getBuildFileParent().getAbsolutePath());
            } else {
                // check whether the user has specified an absolute path
                if ((new File(basedir)).isAbsolute()) {
                    project.setBasedir(basedir);
                } else {
                    project.setBaseDir(FileUtils.getFileUtils().resolveFile(context.getBuildFileParent(), basedir));
                }
            }
        }
        project.addTarget("", context.getImplicitTarget());
        context.setCurrentTarget(context.getImplicitTarget());
    }

    protected String getTargetPrefix(AntXMLContext context) {
        String configuredValue = getCurrentTargetPrefix();
        if (configuredValue != null && configuredValue.length() == 0) {
            configuredValue = null;
        }
        if (configuredValue != null) {
            return configuredValue;
        }

        String projectName = context.getCurrentProjectName();
        if ("".equals(projectName)) {
            projectName = null;
        }

        return projectName;
    }

    protected void setupAntpath(Project project, AntDslContext context, List<InnerElement> antpathElements) {
        UnknownElement element = new UnknownElement("path");
        element.setProject(project);
        RuntimeConfigurable wrapper = new RuntimeConfigurable(element, element.getTaskName());
        context.pushWrapper(wrapper);
        for (InnerElement antpathElement : antpathElements) {
            UnknownElement child = mapUnknown(project, context, antpathElement, false);
            element.addChild(child);
        }
        context.popWrapper();

        Path path = new Path(project);
        element.configure(path);

        OSGiFrameworkManager osgiFrameworkManager = getOSGiFrameworkManager(project);
        Iterator<Resource> itResources = path.iterator();
        while (itResources.hasNext()) {
            Resource resource = itResources.next();
            String url;
            if (resource instanceof URLProvider) {
                url = ((URLProvider) resource).getURL().toExternalForm();
            } else if (resource instanceof FileProvider) {
                try {
                    url = ((FileProvider) resource).getFile().toURI().toURL().toExternalForm();
                } catch (MalformedURLException e) {
                    throw new BuildException("Unable to get url of " + resource, e);
                }
            } else {
                // very speculative, but let's try
                url = resource.getName();
            }
            try {
                osgiFrameworkManager.install(url);
            } catch (BundleException e) {
                throw new BuildException("Unable to install the bundle " + resource.getName() + " (" + e.getMessage() + ")", e);
            }
        }
    }

    protected void importAntlib(Project project, AntDslContext context, String name, String resource) {
        // FIXME seems ugly
        String uri = UUID.randomUUID().toString();

        ClassLoader cl = getClassloaderStack(project).peek();

        Taskdef taskdef = new Taskdef();
        taskdef.setProject(project);
        taskdef.setResource(resource);
        taskdef.setURI(uri);
        taskdef.setAntlibClassLoader(cl);
        taskdef.execute();
        context.addNamespace(name, uri);
    }

    protected void importBuildModule(Project project, AntDslContext context, String buildModule) {
        ClassLoader cl = getClassloaderStack(project).peek();

        URL buildUrl = cl.getResource(buildModule);
        if (buildUrl == null) {
            throw new BuildException("Unable to find the build module " + buildModule);
        }
        URLResource urlResource = new URLResource(buildUrl);

        Vector<Object> importStack = getImportStack();

        if (importStack.contains(urlResource)) {
            project.log("Skipped already imported file:\n" + urlResource + "\n", Project.MSG_VERBOSE);
            return;
        }

        ProjectHelper subHelper = ProjectHelperRepository.getInstance().getProjectHelperForBuildFile(urlResource);

        // TODO
        // add to the classloader stack the classloader of the imported build
        
        // push current stacks into the sub helper
        subHelper.getImportStack().addAll(this.getImportStack());
        subHelper.getExtensionStack().addAll(this.getExtensionStack());
        project.addReference(ProjectHelper.PROJECTHELPER_REFERENCE, subHelper);

        subHelper.parse(project, urlResource);

        // push back the stack from the sub helper to the main one
        project.addReference(ProjectHelper.PROJECTHELPER_REFERENCE, this);
        getImportStack().clear();
        getImportStack().addAll(subHelper.getImportStack());
        getExtensionStack().clear();
        getExtensionStack().addAll(subHelper.getExtensionStack());
    }

    public void mapCommonTarget(
            Target target, Project project, AntDslContext context, String name, String description, List<String> depends, List<String> extensionsOf,
            String onMiss) {
        OnMissingExtensionPoint extensionPointMissing = null;
        if (onMiss != null) {
            extensionPointMissing = OnMissingExtensionPoint.valueOf(onMiss.toUpperCase(Locale.ENGLISH));
        }

        context.addTarget(target);
        target.setProject(project);
        if ("".equals(name)) {
            throw new BuildException("name attribute must " + "not be empty");
        }
        target.setName(name);
        target.setDescription(description);

        String prefix = null;
        boolean isInIncludeMode = context.isIgnoringProjectTag() && isInIncludeMode();
        String sep = getCurrentPrefixSeparator();

        if (isInIncludeMode) {
            prefix = getTargetPrefix(context);
            if (prefix == null) {
                throw new BuildException("can't include build file " + context.getBuildFileURL() + ", no as attribute has been given"
                        + " and the project tag doesn't" + " specify a name attribute");
            }
            name = prefix + sep + name;
        }

        // Check if this target is in the current build file
        if (context.getCurrentTargets().get(name) != null) {
            throw new BuildException("Duplicate target '" + name + "'", target.getLocation());
        }
        Hashtable<String, Target> projectTargets = project.getTargets();
        boolean usedTarget = false;
        // If the name has not already been defined define it
        if (projectTargets.containsKey(name)) {
            project.log("Already defined in main or a previous import, ignore " + name, Project.MSG_VERBOSE);
        } else {
            target.setName(name);
            context.getCurrentTargets().put(name, target);
            project.addOrReplaceTarget(name, target);
            usedTarget = true;
        }

        if (depends != null) {
            for (String dep : depends) {
                if (!isInIncludeMode) {
                    target.addDependency(dep);
                } else {
                    target.addDependency(prefix + sep + dep);
                }
            }
        }

        if (!isInIncludeMode && context.isIgnoringProjectTag() && (prefix = getTargetPrefix(context)) != null) {
            // In an imported file (and not completely
            // ignoring the project tag or having a preconfigured prefix)
            String newName = prefix + sep + name;
            Target newTarget = usedTarget ? new Target(target) : target;
            newTarget.setName(newName);
            context.getCurrentTargets().put(newName, newTarget);
            project.addOrReplaceTarget(newName, newTarget);
        }
        if (extensionPointMissing != null && extensionsOf == null) {
            throw new BuildException("onMissingExtensionPoint attribute cannot be specified unless extensionOf is specified", target.getLocation());
        }
        if (extensionsOf != null) {
            ProjectHelper helper = context.getProject().getReference(ProjectHelper.PROJECTHELPER_REFERENCE);
            for (String extensionOf : extensionsOf) {
                if (isInIncludeMode()) {
                    extensionOf = prefix + sep + extensionOf;
                }
                if (extensionPointMissing == null) {
                    extensionPointMissing = OnMissingExtensionPoint.FAIL;
                }
                // defer extensionpoint resolution until the full
                // import stack has been processed
                helper.getExtensionStack().add(new String[] {extensionOf, name, extensionPointMissing.name()});
            }
        }
    }

    public void mapCommonTask(Project project, AntDslContext context, Task task) {
        task.setProject(project);
        task.setOwningTarget(context.getCurrentTarget());
    }

    public UnknownElement mapUnknown(Project project, AntDslContext context, InnerElement innerElement, boolean innerUnknown) {
        RuntimeConfigurable parentWrapper = (RuntimeConfigurable) context.currentWrapper();
        Object parent = null;

        if (parentWrapper != null) {
            parent = parentWrapper.getProxy();
        }

        String tag = innerElement.name;
        String qname;
        String uri = null;
        String ns = innerElement.ns;
        if (ns == null) {
            qname = tag;
        } else {
            qname = ns + ":" + tag;
            uri = context.getURI(ns);
            if (uri == null) {
                uri = ns;
            }
        }
        if (uri == null) {
            uri = "";
        }
        UnknownElement element = new UnknownElement(tag);
        element.setNamespace(uri);
        element.setQName(qname);
        element.setTaskType(ProjectHelper.genComponentName(element.getNamespace(), tag));
        element.setTaskName(qname);
        mapCommonTask(project, context, element);

        if (innerUnknown && parent != null) {
            // Nested element
            ((UnknownElement) parent).addChild(element);
        }

        RuntimeConfigurable wrapper = new RuntimeConfigurable(element, element.getTaskName());

        if (innerElement.attributes != null) {
            for (Entry<String, AntExpression> att : innerElement.attributes.entrySet()) {
                wrapper.setAttribute(att.getKey(), att.getValue());
            }
        }

        if (parentWrapper != null) {
            parentWrapper.addChild(wrapper);
        }
        context.pushWrapper(wrapper);

        if (innerElement.children != null) {
            for (InnerElement child : innerElement.children) {
                mapUnknown(project, context, child, true);
            }
        }

        context.popWrapper();
        return element;
    }

    public AntExpression mapCallAntExpression(Project project, AntDslContext context, InnerElement eInnerElement) {
        ProjectComponent component = configureInnerElement(project, context, eInnerElement);
        if (component == null) {
            return null;
        }
        if (component instanceof Condition) {
            return condition2Expression((Condition) component);
        }
        throw new BuildException("Unsupported type of ant expression call " + component.getClass().getName());
    }

    private ProjectComponent configureInnerElement(Project project, AntDslContext context, InnerElement eInnerElement) {
        if (eInnerElement == null) {
            return null;
        }

        UnknownElement element = new UnknownElement("projectComponentContainer");
        element.setProject(project);
        RuntimeConfigurable wrapper = new RuntimeConfigurable(element, element.getTaskName());
        context.pushWrapper(wrapper);
        UnknownElement child = mapUnknown(project, context, eInnerElement, false);
        element.addChild(child);
        context.popWrapper();

        ProjectComponentContainer container = new ProjectComponentContainer();
        element.configure(container);
        return container.component;
    }

    public static class ProjectComponentContainer {
        ProjectComponent component;

        public void add(ProjectComponent component) {
            this.component = component;
        }
    }

    public static class InnerElement {

        public InnerElement() {
        }

        public String ns;

        public String name;

        public LinkedHashMap<String, AntExpression> attributes;

        public List<InnerElement> children;
    }

    public String readDoc(String s) {
        if (s == null) {
            return null;
        }
        String[] split = s.split("\r?\n");
        StringBuilder builder = new StringBuilder();
        for (String line : split) {
            builder.append(line.substring(1)); // remove the leading %
            builder.append(' '); // replace the line end by a space
        }
        return builder.toString();
    }

    public String readVariable(String s) {
        if (s == null) {
            return null;
        }
        if (s.charAt(1) == '{' || s.charAt(1) == '`') {
            // remove the lead $ and the enclosing characters
            s = s.substring(2, s.length() - 1);
        } else {
            // remove the leading $
            s = s.substring(1);
        }
        return s;
    }

    public String readIdentifier(String s) {
        if (s == null) {
            return null;
        }
        if (s.charAt(0) == '`') {
            // remove the lead ` and the ending `
            s = s.substring(1, s.length() - 1);
        }
        return s;
    }

    public String readString(String s) {
        if (s == null) {
            return null;
        }
        // first remove the quotes
        s = s.substring(1, s.length() - 1);
        // code copied from org.eclipse.xtext.util.Strings.convertFromJavaString(String, boolean)
        char[] in = s.toCharArray();
        int off = 0;
        int len = s.length();
        char[] convtBuf = new char[len];
        char aChar;
        char[] out = convtBuf;
        int outLen = 0;
        int end = off + len;

        while (off < end) {
            aChar = in[off++];
            if (aChar == '\\') {
                aChar = in[off++];
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    if (off + 4 > end)
                        throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
                    for (int i = 0; i < 4; i++) {
                        aChar = in[off++];
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
                        }
                    }
                    out[outLen++] = (char) value;
                } else {
                    aChar = getEscapedChar(aChar);
                    out[outLen++] = aChar;
                }
            } else {
                out[outLen++] = aChar;
            }
        }
        return new String(out, 0, outLen);
    }

    private char getEscapedChar(char escaped) {
        if (escaped == 't') {
            escaped = '\t';
        } else if (escaped == 'r') {
            escaped = '\r';
        } else if (escaped == 'n') {
            escaped = '\n';
        } else if (escaped == 'f') {
            escaped = '\f';
        } else if (escaped == 'b') {
            escaped = '\b';
        } else if (escaped == '"') {
            escaped = '\"';
        } else if (escaped == '\'') {
            escaped = '\'';
        } else if (escaped == '\\') {
            escaped = '\\';
        } else {
            throw new IllegalArgumentException("Illegal escape character \\" + escaped);
        }
        return escaped;
    }

    public Character readChar(String s) {
        if (s == null) {
            return null;
        }
        char c = s.charAt(1);
        if (c == '\\') {
            return getEscapedChar(s.charAt(2));
        }
        return c;
    }

    public Number readDecimal(String s) {
        return readNumber(s, 10);
    }

    public Number readOctal(String s) {
        return readNumber(s.substring(1), 8);
    }

    public Number readHex(String s) {
        return readNumber(s.substring(2), 16);
    }

    public Number readNumber(String s, int radix) {
        if (s == null) {
            return null;
        }
        char suffix = s.charAt(s.length() - 1);
        if (suffix == 'l' || suffix == 'L') {
            return Long.parseLong(s.substring(0, s.length() - 1), radix);
        } else {
            return Integer.parseInt(s, radix);
        }
    }

    public Number readFloat(String s) {
        char suffix = s.charAt(s.length() - 1);
        if (suffix == 'f' || suffix == 'F') {
            return Float.parseFloat(s.substring(0, s.length() - 1));
        } else if (suffix == 'd' || suffix == 'D') {
            return Double.parseDouble(s.substring(0, s.length() - 1));
        }
        return Double.parseDouble(s);
    }

    public AntExpression condition2Expression(Condition condition) {
        if (condition == null) {
            return null;
        }
        if (condition instanceof AntExpressionCondition) {
            return ((AntExpressionCondition) condition).getExpr();
        }
        ConditionAntExpression bae = new ConditionAntExpression();
        bae.setCondition(condition);
        return bae;
    }

    public Condition expression2Condition(AntExpression expr) {
        if (expr == null) {
            return null;
        }
        if (expr instanceof ConditionAntExpression) {
            return ((ConditionAntExpression) expr).getCondition();
        }
        AntExpressionCondition condition = new AntExpressionCondition();
        condition.setExpr(expr);
        return condition;
    }
}
