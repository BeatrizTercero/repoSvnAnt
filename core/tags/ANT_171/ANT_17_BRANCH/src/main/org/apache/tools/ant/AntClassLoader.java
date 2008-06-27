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

package org.apache.tools.ant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Locale;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.CollectionUtils;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.apache.tools.ant.util.LoaderUtils;
import org.apache.tools.ant.launch.Locator;

/**
 * Used to load classes within ant with a different classpath from
 * that used to start ant. Note that it is possible to force a class
 * into this loader even when that class is on the system classpath by
 * using the forceLoadClass method. Any subsequent classes loaded by that
 * class will then use this loader rather than the system class loader.
 *
 * <p>
 * Note that this classloader has a feature to allow loading
 * in reverse order and for "isolation".
 * Due to the fact that a number of
 * methods in java.lang.ClassLoader are final (at least
 * in java 1.4 getResources) this means that the
 * class has to fake the given parent.
 * </p>
 *
 */
public class AntClassLoader extends ClassLoader implements SubBuildListener {

    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();

    /**
     * An enumeration of all resources of a given name found within the
     * classpath of this class loader. This enumeration is used by the
     * ClassLoader.findResources method, which is in
     * turn used by the ClassLoader.getResources method.
     *
     * @see AntClassLoader#findResources(String)
     * @see java.lang.ClassLoader#getResources(String)
     */
    private class ResourceEnumeration implements Enumeration {
        /**
         * The name of the resource being searched for.
         */
        private String resourceName;

        /**
         * The index of the next classpath element to search.
         */
        private int pathElementsIndex;

        /**
         * The URL of the next resource to return in the enumeration. If this
         * field is <code>null</code> then the enumeration has been completed,
         * i.e., there are no more elements to return.
         */
        private URL nextResource;

        /**
         * Constructs a new enumeration of resources of the given name found
         * within this class loader's classpath.
         *
         * @param name the name of the resource to search for.
         */
        ResourceEnumeration(String name) {
            this.resourceName = name;
            this.pathElementsIndex = 0;
            findNextResource();
        }

        /**
         * Indicates whether there are more elements in the enumeration to
         * return.
         *
         * @return <code>true</code> if there are more elements in the
         *         enumeration; <code>false</code> otherwise.
         */
        public boolean hasMoreElements() {
            return (this.nextResource != null);
        }

        /**
         * Returns the next resource in the enumeration.
         *
         * @return the next resource in the enumeration
         */
        public Object nextElement() {
            URL ret = this.nextResource;
            findNextResource();
            return ret;
        }

        /**
         * Locates the next resource of the correct name in the classpath and
         * sets <code>nextResource</code> to the URL of that resource. If no
         * more resources can be found, <code>nextResource</code> is set to
         * <code>null</code>.
         */
        private void findNextResource() {
            URL url = null;
            while ((pathElementsIndex < pathComponents.size())
                   && (url == null)) {
                try {
                    File pathComponent
                        = (File) pathComponents.elementAt(pathElementsIndex);
                    url = getResourceURL(pathComponent, this.resourceName);
                    pathElementsIndex++;
                } catch (BuildException e) {
                    // ignore path elements which are not valid relative to the
                    // project
                }
            }
            this.nextResource = url;
        }
    }

    /**
     * The size of buffers to be used in this classloader.
     */
    private static final int BUFFER_SIZE = 8192;
    /**
     * Number of array elements in a test array of strings
     */
    private static final int NUMBER_OF_STRINGS = 256;

    /**
     * The components of the classpath that the classloader searches
     * for classes.
     */
    private Vector pathComponents  = new Vector();

    /**
     * The project to which this class loader belongs.
     */
    private Project project;

    /**
     * Indicates whether the parent class loader should be
     * consulted before trying to load with this class loader.
     */
    private boolean parentFirst = true;

    /**
     * These are the package roots that are to be loaded by the parent class
     * loader regardless of whether the parent class loader is being searched
     * first or not.
     */
    private Vector systemPackages = new Vector();

    /**
     * These are the package roots that are to be loaded by this class loader
     * regardless of whether the parent class loader is being searched first
     * or not.
     */
    private Vector loaderPackages = new Vector();

    /**
     * Whether or not this classloader will ignore the base
     * classloader if it can't find a class.
     *
     * @see #setIsolated(boolean)
     */
    private boolean ignoreBase = false;

    /**
     * The parent class loader, if one is given or can be determined.
     */
    private ClassLoader parent = null;

    /**
     * A hashtable of zip files opened by the classloader (File to ZipFile).
     */
    private Hashtable zipFiles = new Hashtable();

    /** Static map of jar file/time to manifest class-path entries */
    private static Map/*<String,String>*/ pathMap = Collections.synchronizedMap(new HashMap());

    /**
     * The context loader saved when setting the thread's current
     * context loader.
     */
    private ClassLoader savedContextLoader = null;
    /**
     * Whether or not the context loader is currently saved.
     */
    private boolean isContextLoaderSaved = false;

    /**
     * Create an Ant ClassLoader for a given project, with
     * a parent classloader and an initial classpath.
     * @since Ant 1.7.
     * @param parent the parent for this classloader.
     * @param project The project to which this classloader is to
     *                belong.
     * @param classpath The classpath to use to load classes.
     */
    public AntClassLoader(
        ClassLoader parent, Project project, Path classpath) {
        setParent(parent);
        setClassPath(classpath);
        setProject(project);
    }

    /**
     * Create an Ant Class Loader
     */
    public AntClassLoader() {
        setParent(null);
    }

    /**
     * Creates a classloader for the given project using the classpath given.
     *
     * @param project The project to which this classloader is to belong.
     *                Must not be <code>null</code>.
     * @param classpath The classpath to use to load the classes.  This
     *                is combined with the system classpath in a manner
     *                determined by the value of ${build.sysclasspath}.
     *                May be <code>null</code>, in which case no path
     *                elements are set up to start with.
     */
    public AntClassLoader(Project project, Path classpath) {
        setParent(null);
        setProject(project);
        setClassPath(classpath);
    }

    /**
     * Creates a classloader for the given project using the classpath given.
     *
     * @param parent The parent classloader to which unsatisfied loading
     *               attempts are delegated. May be <code>null</code>,
     *               in which case the classloader which loaded this
     *               class is used as the parent.
     * @param project The project to which this classloader is to belong.
     *                Must not be <code>null</code>.
     * @param classpath the classpath to use to load the classes.
     *                  May be <code>null</code>, in which case no path
     *                  elements are set up to start with.
     * @param parentFirst If <code>true</code>, indicates that the parent
     *                    classloader should be consulted  before trying to
     *                    load the a class through this loader.
     */
    public AntClassLoader(ClassLoader parent, Project project, Path classpath,
                          boolean parentFirst) {
        this(project, classpath);
        if (parent != null) {
            setParent(parent);
        }
        setParentFirst(parentFirst);
        addJavaLibraries();
    }


    /**
     * Creates a classloader for the given project using the classpath given.
     *
     * @param project The project to which this classloader is to belong.
     *                Must not be <code>null</code>.
     * @param classpath The classpath to use to load the classes. May be
     *                  <code>null</code>, in which case no path
     *                  elements are set up to start with.
     * @param parentFirst If <code>true</code>, indicates that the parent
     *                    classloader should be consulted before trying to
     *                    load the a class through this loader.
     */
    public AntClassLoader(Project project, Path classpath,
                          boolean parentFirst) {
        this(null, project, classpath, parentFirst);
    }

    /**
     * Creates an empty class loader. The classloader should be configured
     * with path elements to specify where the loader is to look for
     * classes.
     *
     * @param parent The parent classloader to which unsatisfied loading
     *               attempts are delegated. May be <code>null</code>,
     *               in which case the classloader which loaded this
     *               class is used as the parent.
     * @param parentFirst If <code>true</code>, indicates that the parent
     *                    classloader should be consulted before trying to
     *                    load the a class through this loader.
     */
    public AntClassLoader(ClassLoader parent, boolean parentFirst) {
        setParent(parent);
        project = null;
        this.parentFirst = parentFirst;
    }

    /**
     * Set the project associated with this class loader
     *
     * @param project the project instance
     */
    public void setProject(Project project) {
        this.project = project;
        if (project != null) {
            project.addBuildListener(this);
        }
    }

    /**
     * Set the classpath to search for classes to load. This should not be
     * changed once the classloader starts to server classes
     *
     * @param classpath the search classpath consisting of directories and
     *        jar/zip files.
     */
    public void setClassPath(Path classpath) {
        pathComponents.removeAllElements();
        if (classpath != null) {
            Path actualClasspath = classpath.concatSystemClasspath("ignore");
            String[] pathElements = actualClasspath.list();
            for (int i = 0; i < pathElements.length; ++i) {
                try {
                    addPathElement(pathElements[i]);
                } catch (BuildException e) {
                    // ignore path elements which are invalid
                    // relative to the project
                }
            }
        }
    }

    /**
     * Set the parent for this class loader. This is the class loader to which
     * this class loader will delegate to load classes
     *
     * @param parent the parent class loader.
     */
    public void setParent(ClassLoader parent) {
        if (parent == null) {
            this.parent = AntClassLoader.class.getClassLoader();
        } else {
            this.parent = parent;
        }
    }

    /**
     * Control whether class lookup is delegated to the parent loader first
     * or after this loader. Use with extreme caution. Setting this to
     * false violates the class loader hierarchy and can lead to Linkage errors
     *
     * @param parentFirst if true, delegate initial class search to the parent
     *                    classloader.
     */
    public void setParentFirst(boolean parentFirst) {
        this.parentFirst = parentFirst;
    }


    /**
     * Logs a message through the project object if one has been provided.
     *
     * @param message The message to log.
     *                Should not be <code>null</code>.
     *
     * @param priority The logging priority of the message.
     */
    protected void log(String message, int priority) {
        if (project != null) {
            project.log(message, priority);
        }
        //         else {
        //             System.out.println(message);
        //         }
    }

    /**
     * Sets the current thread's context loader to this classloader, storing
     * the current loader value for later resetting.
     */
    public void setThreadContextLoader() {
        if (isContextLoaderSaved) {
            throw new BuildException("Context loader has not been reset");
        }
        if (LoaderUtils.isContextLoaderAvailable()) {
            savedContextLoader = LoaderUtils.getContextClassLoader();
            ClassLoader loader = this;
            if (project != null
                && "only".equals(project.getProperty("build.sysclasspath"))) {
                loader = this.getClass().getClassLoader();
            }
            LoaderUtils.setContextClassLoader(loader);
            isContextLoaderSaved = true;
        }
    }

    /**
     * Resets the current thread's context loader to its original value.
     */
    public void resetThreadContextLoader() {
        if (LoaderUtils.isContextLoaderAvailable()
            && isContextLoaderSaved) {
            LoaderUtils.setContextClassLoader(savedContextLoader);
            savedContextLoader = null;
            isContextLoaderSaved = false;
        }
    }


    /**
     * Adds an element to the classpath to be searched.
     *
     * @param pathElement The path element to add. Must not be
     *                    <code>null</code>.
     *
     * @exception BuildException if the given path element cannot be resolved
     *                           against the project.
     */
    public void addPathElement(String pathElement) throws BuildException {
        File pathComponent
            = project != null ? project.resolveFile(pathElement)
            : new File(pathElement);
        try {
            addPathFile(pathComponent);
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }

    /**
     * Add a path component.
     * This simply adds the file, unlike addPathElement
     * it does not open jar files and load files from
     * their CLASSPATH entry in the manifest file.
     * @param file the jar file or directory to add.
     */
    public void addPathComponent(File file) {
        if (pathComponents.contains(file)) {
            return;
        }
        pathComponents.addElement(file);
    }

    /**
     * Add a file to the path.
     * Reads the manifest, if available, and adds any additional class path jars
     * specified in the manifest.
     *
     * @param pathComponent the file which is to be added to the path for
     *                      this class loader
     *
     * @throws IOException if data needed from the file cannot be read.
     */
    protected void addPathFile(File pathComponent) throws IOException {
        pathComponents.addElement(pathComponent);
        if (pathComponent.isDirectory()) {
            return;
        }

        String absPathPlusTimeAndLength =
            pathComponent.getAbsolutePath() + pathComponent.lastModified() + "-"
            + pathComponent.length();
        String classpath = (String) pathMap.get(absPathPlusTimeAndLength);
        if (classpath == null) {
            ZipFile jarFile = null;
            InputStream manifestStream = null;
            try {
                jarFile = new ZipFile(pathComponent);
                manifestStream
                    = jarFile.getInputStream(new ZipEntry("META-INF/MANIFEST.MF"));

                if (manifestStream == null) {
                    return;
                }
                Reader manifestReader
                    = new InputStreamReader(manifestStream, "UTF-8");
                org.apache.tools.ant.taskdefs.Manifest manifest
                    = new org.apache.tools.ant.taskdefs.Manifest(manifestReader);
                classpath
                    = manifest.getMainSection().getAttributeValue("Class-Path");

            } catch (org.apache.tools.ant.taskdefs.ManifestException e) {
                // ignore
            } finally {
                FileUtils.close(manifestStream);
                if (jarFile != null) {
                    jarFile.close();
                }
            }
            if (classpath == null) {
                classpath = "";
            }
            pathMap.put(absPathPlusTimeAndLength, classpath);
        }

        if (!"".equals(classpath)) {
            URL baseURL = FILE_UTILS.getFileURL(pathComponent);
            StringTokenizer st = new StringTokenizer(classpath);
            while (st.hasMoreTokens()) {
                String classpathElement = st.nextToken();
                URL libraryURL = new URL(baseURL, classpathElement);
                if (!libraryURL.getProtocol().equals("file")) {
                    log("Skipping jar library " + classpathElement
                        + " since only relative URLs are supported by this"
                        + " loader", Project.MSG_VERBOSE);
                    continue;
                }
                String decodedPath = Locator.decodeUri(libraryURL.getFile());
                File libraryFile = new File(decodedPath);
                if (libraryFile.exists() && !isInPath(libraryFile)) {
                    addPathFile(libraryFile);
                }
            }
        }
    }

    /**
     * Returns the classpath this classloader will consult.
     *
     * @return the classpath used for this classloader, with elements
     *         separated by the path separator for the system.
     */
    public String getClasspath() {
        StringBuffer sb = new StringBuffer();
        boolean firstPass = true;
        Enumeration componentEnum = pathComponents.elements();
        while (componentEnum.hasMoreElements()) {
            if (!firstPass) {
                sb.append(System.getProperty("path.separator"));
            } else {
                firstPass = false;
            }
            sb.append(((File) componentEnum.nextElement()).getAbsolutePath());
        }
        return sb.toString();
    }

    /**
     * Sets whether this classloader should run in isolated mode. In
     * isolated mode, classes not found on the given classpath will
     * not be referred to the parent class loader but will cause a
     * ClassNotFoundException.
     *
     * @param isolated Whether or not this classloader should run in
     *                 isolated mode.
     */
    public synchronized void setIsolated(boolean isolated) {
        ignoreBase = isolated;
    }

    /**
     * Forces initialization of a class in a JDK 1.1 compatible, albeit hacky
     * way.
     *
     * @param theClass The class to initialize.
     *                 Must not be <code>null</code>.
     *
     * @deprecated since 1.6.x.
     *             Use Class.forName with initialize=true instead.
     */
    public static void initializeClass(Class theClass) {
        // ***HACK*** We ask the VM to create an instance
        // by voluntarily providing illegal arguments to force
        // the VM to run the class' static initializer, while
        // at the same time not running a valid constructor.

        final Constructor[] cons = theClass.getDeclaredConstructors();
        //At least one constructor is guaranteed to be there, but check anyway.
        if (cons != null) {
            if (cons.length > 0 && cons[0] != null) {
                final String[] strs = new String[NUMBER_OF_STRINGS];
                try {
                    cons[0].newInstance((Object[]) strs);
                    // Expecting an exception to be thrown by this call:
                    // IllegalArgumentException: wrong number of Arguments
                } catch (Exception e) {
                    // Ignore - we are interested only in the side
                    // effect - that of getting the static initializers
                    // invoked.  As we do not want to call a valid
                    // constructor to get this side effect, an
                    // attempt is made to call a hopefully
                    // invalid constructor - come on, nobody
                    // would have a constructor that takes in
                    // 256 String arguments ;-)
                    // (In fact, they can't - according to JVM spec
                    // section 4.10, the number of method parameters is limited
                    // to 255 by the definition of a method descriptor.
                    // Constructors count as methods here.)
                }
            }
        }
    }

    /**
     * Adds a package root to the list of packages which must be loaded on the
     * parent loader.
     *
     * All subpackages are also included.
     *
     * @param packageRoot The root of all packages to be included.
     *                    Should not be <code>null</code>.
     */
    public void addSystemPackageRoot(String packageRoot) {
        systemPackages.addElement(packageRoot
                                  + (packageRoot.endsWith(".") ? "" : "."));
    }

    /**
     * Adds a package root to the list of packages which must be loaded using
     * this loader.
     *
     * All subpackages are also included.
     *
     * @param packageRoot The root of all packages to be included.
     *                    Should not be <code>null</code>.
     */
    public void addLoaderPackageRoot(String packageRoot) {
        loaderPackages.addElement(packageRoot
                                  + (packageRoot.endsWith(".") ? "" : "."));
    }

    /**
     * Loads a class through this class loader even if that class is available
     * on the parent classpath.
     *
     * This ensures that any classes which are loaded by the returned class
     * will use this classloader.
     *
     * @param classname The name of the class to be loaded.
     *                  Must not be <code>null</code>.
     *
     * @return the required Class object
     *
     * @exception ClassNotFoundException if the requested class does not exist
     *                                   on this loader's classpath.
     */
    public Class forceLoadClass(String classname)
        throws ClassNotFoundException {
        log("force loading " + classname, Project.MSG_DEBUG);

        Class theClass = findLoadedClass(classname);

        if (theClass == null) {
            theClass = findClass(classname);
        }

        return theClass;
    }

    /**
     * Loads a class through this class loader but defer to the parent class
     * loader.
     *
     * This ensures that instances of the returned class will be compatible
     * with instances which have already been loaded on the parent
     * loader.
     *
     * @param classname The name of the class to be loaded.
     *                  Must not be <code>null</code>.
     *
     * @return the required Class object
     *
     * @exception ClassNotFoundException if the requested class does not exist
     * on this loader's classpath.
     */
    public Class forceLoadSystemClass(String classname)
        throws ClassNotFoundException {
        log("force system loading " + classname, Project.MSG_DEBUG);

        Class theClass = findLoadedClass(classname);

        if (theClass == null) {
            theClass = findBaseClass(classname);
        }

        return theClass;
    }

    /**
     * Returns a stream to read the requested resource name.
     *
     * @param name The name of the resource for which a stream is required.
     *             Must not be <code>null</code>.
     *
     * @return a stream to the required resource or <code>null</code> if the
     *         resource cannot be found on the loader's classpath.
     */
    public InputStream getResourceAsStream(String name) {

        InputStream resourceStream = null;
        if (isParentFirst(name)) {
            resourceStream = loadBaseResource(name);
            if (resourceStream != null) {
                log("ResourceStream for " + name
                    + " loaded from parent loader", Project.MSG_DEBUG);

            } else {
                resourceStream = loadResource(name);
                if (resourceStream != null) {
                    log("ResourceStream for " + name
                        + " loaded from ant loader", Project.MSG_DEBUG);
                }
            }
        } else {
            resourceStream = loadResource(name);
            if (resourceStream != null) {
                log("ResourceStream for " + name
                    + " loaded from ant loader", Project.MSG_DEBUG);

            } else {
                resourceStream = loadBaseResource(name);
                if (resourceStream != null) {
                    log("ResourceStream for " + name
                        + " loaded from parent loader", Project.MSG_DEBUG);
                }
            }
        }

        if (resourceStream == null) {
            log("Couldn't load ResourceStream for " + name,
                Project.MSG_DEBUG);
        }

        return resourceStream;
    }

    /**
     * Returns a stream to read the requested resource name from this loader.
     *
     * @param name The name of the resource for which a stream is required.
     *             Must not be <code>null</code>.
     *
     * @return a stream to the required resource or <code>null</code> if
     *         the resource cannot be found on the loader's classpath.
     */
    private InputStream loadResource(String name) {
        // we need to search the components of the path to see if we can
        // find the class we want.
        InputStream stream = null;

        Enumeration e = pathComponents.elements();
        while (e.hasMoreElements() && stream == null) {
            File pathComponent = (File) e.nextElement();
            stream = getResourceStream(pathComponent, name);
        }
        return stream;
    }

    /**
     * Finds a system resource (which should be loaded from the parent
     * classloader).
     *
     * @param name The name of the system resource to load.
     *             Must not be <code>null</code>.
     *
     * @return a stream to the named resource, or <code>null</code> if
     *         the resource cannot be found.
     */
    private InputStream loadBaseResource(String name) {
        if (parent == null) {
            return getSystemResourceAsStream(name);
        } else {
            return parent.getResourceAsStream(name);
        }
    }

    /**
     * Returns an inputstream to a given resource in the given file which may
     * either be a directory or a zip file.
     *
     * @param file the file (directory or jar) in which to search for the
     *             resource. Must not be <code>null</code>.
     * @param resourceName The name of the resource for which a stream is
     *                     required. Must not be <code>null</code>.
     *
     * @return a stream to the required resource or <code>null</code> if
     *         the resource cannot be found in the given file.
     */
    private InputStream getResourceStream(File file, String resourceName) {
        try {
            ZipFile zipFile = (ZipFile) zipFiles.get(file);
            if (zipFile == null && file.isDirectory()) {
                File resource = new File(file, resourceName);

                if (resource.exists()) {
                    return new FileInputStream(resource);
                }
            } else {
                if (zipFile == null) {
                    if (file.exists()) {

                        zipFile = new ZipFile(file);
                        zipFiles.put(file, zipFile);
                    } else {
                        return null;
                    }
                    //to eliminate a race condition, retrieve the entry
                    //that is in the hash table under that filename
                    zipFile = (ZipFile) zipFiles.get(file);
                }
                ZipEntry entry = zipFile.getEntry(resourceName);
                if (entry != null) {
                    return zipFile.getInputStream(entry);
                }
            }
        } catch (Exception e) {
            log("Ignoring Exception " + e.getClass().getName() + ": "
                    + e.getMessage() + " reading resource " + resourceName
                    + " from " + file, Project.MSG_VERBOSE);
        }

        return null;
    }

    /**
     * Tests whether or not the parent classloader should be checked for a
     * resource before this one. If the resource matches both the "use parent
     * classloader first" and the "use this classloader first" lists, the latter
     * takes priority.
     *
     * @param resourceName
     *            The name of the resource to check. Must not be
     *            <code>null</code>.
     *
     * @return whether or not the parent classloader should be checked for a
     *         resource before this one is.
     */
    private boolean isParentFirst(String resourceName) {
        // default to the global setting and then see
        // if this class belongs to a package which has been
        // designated to use a specific loader first
        // (this one or the parent one)

        // XXX - shouldn't this always return false in isolated mode?

        boolean useParentFirst = parentFirst;

        for (Enumeration e = systemPackages.elements(); e.hasMoreElements();) {
            String packageName = (String) e.nextElement();
            if (resourceName.startsWith(packageName)) {
                useParentFirst = true;
                break;
            }
        }

        for (Enumeration e = loaderPackages.elements(); e.hasMoreElements();) {
            String packageName = (String) e.nextElement();
            if (resourceName.startsWith(packageName)) {
                useParentFirst = false;
                break;
            }
        }

        return useParentFirst;
    }

    /**
     * Used for isolated resource seaching.
     * @return the root classloader of AntClassLoader.
     */
    private ClassLoader getRootLoader() {
        ClassLoader ret = getClass().getClassLoader();
        while (ret != null && ret.getParent() != null) {
            ret = ret.getParent();
        }
        return ret;
    }

    /**
     * Finds the resource with the given name. A resource is
     * some data (images, audio, text, etc) that can be accessed by class
     * code in a way that is independent of the location of the code.
     *
     * @param name The name of the resource for which a stream is required.
     *             Must not be <code>null</code>.
     *
     * @return a URL for reading the resource, or <code>null</code> if the
     *         resource could not be found or the caller doesn't have
     *         adequate privileges to get the resource.
     */
    public URL getResource(String name) {
        // we need to search the components of the path to see if
        // we can find the class we want.
        URL url = null;
        if (isParentFirst(name)) {
            url = (parent == null) ? super.getResource(name)
                : parent.getResource(name);
        }

        if (url != null) {
            log("Resource " + name + " loaded from parent loader",
                Project.MSG_DEBUG);

        } else {
            // try and load from this loader if the parent either didn't find
            // it or wasn't consulted.
            Enumeration e = pathComponents.elements();
            while (e.hasMoreElements() && url == null) {
                File pathComponent = (File) e.nextElement();
                url = getResourceURL(pathComponent, name);
                if (url != null) {
                    log("Resource " + name
                        + " loaded from ant loader",
                        Project.MSG_DEBUG);
                }
            }
        }

        if (url == null && !isParentFirst(name)) {
            // this loader was first but it didn't find it - try the parent
            if (ignoreBase) {
                url = (getRootLoader() == null) ? null
                    : getRootLoader().getResource(name);
            } else {
                url = (parent == null) ? super.getResource(name)
                    : parent.getResource(name);
            }
            if (url != null) {
                log("Resource " + name + " loaded from parent loader",
                    Project.MSG_DEBUG);
            }
        }

        if (url == null) {
            log("Couldn't load Resource " + name, Project.MSG_DEBUG);
        }

        return url;
    }

    /**
     * Returns an enumeration of URLs representing all the resources with the
     * given name by searching the class loader's classpath.
     *
     * @param name The resource name to search for.
     *             Must not be <code>null</code>.
     * @return an enumeration of URLs for the resources
     * @exception IOException if I/O errors occurs (can't happen)
     */
    protected Enumeration/*<URL>*/ findResources(String name) throws IOException {
        Enumeration/*<URL>*/ mine = new ResourceEnumeration(name);
        Enumeration/*<URL>*/ base;
        if (parent != null && parent != getParent()) {
            // Delegate to the parent:
            base = parent.getResources(name);
            // Note: could cause overlaps in case ClassLoader.this.parent has matches.
        } else {
            // ClassLoader.this.parent is already delegated to from
            // ClassLoader.getResources, no need:
            base = new CollectionUtils.EmptyEnumeration();
        }
        if (isParentFirst(name)) {
            // Normal case.
            return CollectionUtils.append(base, mine);
        } else if (ignoreBase) {
            return getRootLoader() == null
                ? mine
                : CollectionUtils.append(
                    mine, getRootLoader().getResources(name));
        } else {
            // Inverted.
            return CollectionUtils.append(mine, base);
        }
    }

    /**
     * Returns the URL of a given resource in the given file which may
     * either be a directory or a zip file.
     *
     * @param file The file (directory or jar) in which to search for
     *             the resource. Must not be <code>null</code>.
     * @param resourceName The name of the resource for which a stream
     *                     is required. Must not be <code>null</code>.
     *
     * @return a stream to the required resource or <code>null</code> if the
     *         resource cannot be found in the given file object.
     */
    protected URL getResourceURL(File file, String resourceName) {
        try {
            ZipFile zipFile = (ZipFile) zipFiles.get(file);
            if (zipFile == null && file.isDirectory()) {
                File resource = new File(file, resourceName);

                if (resource.exists()) {
                    try {
                        return FILE_UTILS.getFileURL(resource);
                    } catch (MalformedURLException ex) {
                        return null;
                    }
                }
            } else {
                if (zipFile == null) {
                    if (file.exists()) {
                        zipFile = new ZipFile(file);
                        zipFiles.put(file, zipFile);
                    } else {
                        return null;
                    }
                }
                ZipEntry entry = zipFile.getEntry(resourceName);
                if (entry != null) {
                    try {
                        return new URL("jar:" + FILE_UTILS.getFileURL(file)
                                       + "!/" + entry);
                    } catch (MalformedURLException ex) {
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Loads a class with this class loader.
     *
     * This class attempts to load the class in an order determined by whether
     * or not the class matches the system/loader package lists, with the
     * loader package list taking priority. If the classloader is in isolated
     * mode, failure to load the class in this loader will result in a
     * ClassNotFoundException.
     *
     * @param classname The name of the class to be loaded.
     *                  Must not be <code>null</code>.
     * @param resolve <code>true</code> if all classes upon which this class
     *                depends are to be loaded.
     *
     * @return the required Class object
     *
     * @exception ClassNotFoundException if the requested class does not exist
     * on the system classpath (when not in isolated mode) or this loader's
     * classpath.
     */
    protected synchronized Class loadClass(String classname, boolean resolve)
        throws ClassNotFoundException {
        // 'sync' is needed - otherwise 2 threads can load the same class
        // twice, resulting in LinkageError: duplicated class definition.
        // findLoadedClass avoids that, but without sync it won't work.

        Class theClass = findLoadedClass(classname);
        if (theClass != null) {
            return theClass;
        }

        if (isParentFirst(classname)) {
            try {
                theClass = findBaseClass(classname);
                log("Class " + classname + " loaded from parent loader "
                    + "(parentFirst)", Project.MSG_DEBUG);
            } catch (ClassNotFoundException cnfe) {
                theClass = findClass(classname);
                log("Class " + classname + " loaded from ant loader "
                    + "(parentFirst)", Project.MSG_DEBUG);
            }
        } else {
            try {
                theClass = findClass(classname);
                log("Class " + classname + " loaded from ant loader",
                    Project.MSG_DEBUG);
            } catch (ClassNotFoundException cnfe) {
                if (ignoreBase) {
                    throw cnfe;
                }
                theClass = findBaseClass(classname);
                log("Class " + classname + " loaded from parent loader",
                    Project.MSG_DEBUG);
            }
        }

        if (resolve) {
            resolveClass(theClass);
        }

        return theClass;
    }

    /**
     * Converts the class dot notation to a filesystem equivalent for
     * searching purposes.
     *
     * @param classname The class name in dot format (eg java.lang.Integer).
     *                  Must not be <code>null</code>.
     *
     * @return the classname in filesystem format (eg java/lang/Integer.class)
     */
    private String getClassFilename(String classname) {
        return classname.replace('.', '/') + ".class";
    }

    /**
     * Define a class given its bytes
     *
     * @param container the container from which the class data has been read
     *                  may be a directory or a jar/zip file.
     *
     * @param classData the bytecode data for the class
     * @param classname the name of the class
     *
     * @return the Class instance created from the given data
     *
     * @throws IOException if the class data cannot be read.
     */
    protected Class defineClassFromData(File container, byte[] classData,
                                        String classname) throws IOException {
        definePackage(container, classname);
        // XXX should instead make a new ProtectionDomain with a CodeSource
        // corresponding to container.toURI().toURL() and the same
        // PermissionCollection as Project.class.protectionDomain had
        return defineClass(classname, classData, 0, classData.length,
                           Project.class.getProtectionDomain());
    }

    /**
     * Define the package information associated with a class.
     *
     * @param container the file containing the class definition.
     * @param className the class name of for which the package information
     *        is to be determined.
     *
     * @exception IOException if the package information cannot be read from the
     *            container.
     */
    protected void definePackage(File container, String className)
        throws IOException {
        int classIndex = className.lastIndexOf('.');
        if (classIndex == -1) {
            return;
        }

        String packageName = className.substring(0, classIndex);
        if (getPackage(packageName) != null) {
            // already defined
            return;
        }

        // define the package now
        Manifest manifest = getJarManifest(container);

        if (manifest == null) {
            definePackage(packageName, null, null, null, null, null,
                          null, null);
        } else {
            definePackage(container, packageName, manifest);
        }
    }

    /**
     * Get the manifest from the given jar, if it is indeed a jar and it has a
     * manifest
     *
     * @param container the File from which a manifest is required.
     *
     * @return the jar's manifest or null is the container is not a jar or it
     *         has no manifest.
     *
     * @exception IOException if the manifest cannot be read.
     */
    private Manifest getJarManifest(File container) throws IOException {
        if (container.isDirectory()) {
            return null;
        }
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(container);
            return jarFile.getManifest();
        } finally {
            if (jarFile != null) {
                jarFile.close();
            }
        }
    }

    /**
     * Define the package information when the class comes from a
     * jar with a manifest
     *
     * @param container the jar file containing the manifest
     * @param packageName the name of the package being defined.
     * @param manifest the jar's manifest
     */
    protected void definePackage(File container, String packageName,
                                 Manifest manifest) {
        String sectionName = packageName.replace('.', '/') + "/";

        String specificationTitle = null;
        String specificationVendor = null;
        String specificationVersion = null;
        String implementationTitle = null;
        String implementationVendor = null;
        String implementationVersion = null;
        String sealedString = null;
        URL sealBase = null;

        Attributes sectionAttributes = manifest.getAttributes(sectionName);
        if (sectionAttributes != null) {
            specificationTitle
                = sectionAttributes.getValue(Name.SPECIFICATION_TITLE);
            specificationVendor
                = sectionAttributes.getValue(Name.SPECIFICATION_VENDOR);
            specificationVersion
                = sectionAttributes.getValue(Name.SPECIFICATION_VERSION);
            implementationTitle
                = sectionAttributes.getValue(Name.IMPLEMENTATION_TITLE);
            implementationVendor
                = sectionAttributes.getValue(Name.IMPLEMENTATION_VENDOR);
            implementationVersion
                = sectionAttributes.getValue(Name.IMPLEMENTATION_VERSION);
            sealedString
                = sectionAttributes.getValue(Name.SEALED);
        }

        Attributes mainAttributes = manifest.getMainAttributes();
        if (mainAttributes != null) {
            if (specificationTitle == null) {
                specificationTitle
                    = mainAttributes.getValue(Name.SPECIFICATION_TITLE);
            }
            if (specificationVendor == null) {
                specificationVendor
                    = mainAttributes.getValue(Name.SPECIFICATION_VENDOR);
            }
            if (specificationVersion == null) {
                specificationVersion
                    = mainAttributes.getValue(Name.SPECIFICATION_VERSION);
            }
            if (implementationTitle == null) {
                implementationTitle
                    = mainAttributes.getValue(Name.IMPLEMENTATION_TITLE);
            }
            if (implementationVendor == null) {
                implementationVendor
                    = mainAttributes.getValue(Name.IMPLEMENTATION_VENDOR);
            }
            if (implementationVersion == null) {
                implementationVersion
                    = mainAttributes.getValue(Name.IMPLEMENTATION_VERSION);
            }
            if (sealedString == null) {
                sealedString
                    = mainAttributes.getValue(Name.SEALED);
            }
        }

        if (sealedString != null
                && sealedString.toLowerCase(Locale.ENGLISH).equals("true")) {
            try {
                sealBase = new URL(FileUtils.getFileUtils().toURI(container.getAbsolutePath()));
            } catch (MalformedURLException e) {
                // ignore
            }
        }

        definePackage(packageName, specificationTitle, specificationVersion,
                      specificationVendor, implementationTitle,
                      implementationVersion, implementationVendor, sealBase);
    }


    /**
     * Reads a class definition from a stream.
     *
     * @param stream The stream from which the class is to be read.
     *               Must not be <code>null</code>.
     * @param classname The name of the class in the stream.
     *                  Must not be <code>null</code>.
     * @param container the file or directory containing the class.
     *
     * @return the Class object read from the stream.
     *
     * @exception IOException if there is a problem reading the class from the
     * stream.
     * @exception SecurityException if there is a security problem while
     * reading the class from the stream.
     */
    private Class getClassFromStream(InputStream stream, String classname,
                                     File container)
        throws IOException, SecurityException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int bytesRead = -1;
        byte[] buffer = new byte[BUFFER_SIZE];

        while ((bytesRead = stream.read(buffer, 0, BUFFER_SIZE)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }

        byte[] classData = baos.toByteArray();
        return defineClassFromData(container, classData, classname);
    }

    /**
     * Searches for and load a class on the classpath of this class loader.
     *
     * @param name The name of the class to be loaded. Must not be
     *             <code>null</code>.
     *
     * @return the required Class object
     *
     * @exception ClassNotFoundException if the requested class does not exist
     *                                   on this loader's classpath.
     */
    public Class findClass(String name) throws ClassNotFoundException {
        log("Finding class " + name, Project.MSG_DEBUG);

        return findClassInComponents(name);
    }

    /**
     * Indicate if the given file is in this loader's path
     *
     * @param component the file which is to be checked
     *
     * @return true if the file is in the class path
     */
    protected boolean isInPath(File component) {
        for (Enumeration e = pathComponents.elements(); e.hasMoreElements();) {
            File pathComponent = (File) e.nextElement();
            if (pathComponent.equals(component)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Finds a class on the given classpath.
     *
     * @param name The name of the class to be loaded. Must not be
     *             <code>null</code>.
     *
     * @return the required Class object
     *
     * @exception ClassNotFoundException if the requested class does not exist
     * on this loader's classpath.
     */
    private Class findClassInComponents(String name)
        throws ClassNotFoundException {
        // we need to search the components of the path to see if
        // we can find the class we want.
        InputStream stream = null;
        String classFilename = getClassFilename(name);
        try {
            Enumeration e = pathComponents.elements();
            while (e.hasMoreElements()) {
                File pathComponent = (File) e.nextElement();
                try {
                    stream = getResourceStream(pathComponent, classFilename);
                    if (stream != null) {
                        log("Loaded from " + pathComponent + " "
                            + classFilename, Project.MSG_DEBUG);
                        return getClassFromStream(stream, name, pathComponent);
                    }
                } catch (SecurityException se) {
                    throw se;
                } catch (IOException ioe) {
                    // ioe.printStackTrace();
                    log("Exception reading component " + pathComponent
                        + " (reason: " + ioe.getMessage() + ")",
                        Project.MSG_VERBOSE);
                }
            }

            throw new ClassNotFoundException(name);
        } finally {
        	FileUtils.close(stream);
        }
    }

    /**
     * Finds a system class (which should be loaded from the same classloader
     * as the Ant core).
     *
     * For JDK 1.1 compatibility, this uses the findSystemClass method if
     * no parent classloader has been specified.
     *
     * @param name The name of the class to be loaded.
     *             Must not be <code>null</code>.
     *
     * @return the required Class object
     *
     * @exception ClassNotFoundException if the requested class does not exist
     * on this loader's classpath.
     */
    private Class findBaseClass(String name) throws ClassNotFoundException {
        if (parent == null) {
            return findSystemClass(name);
        } else {
            return parent.loadClass(name);
        }
    }

    /**
     * Cleans up any resources held by this classloader. Any open archive
     * files are closed.
     */
    public synchronized void cleanup() {
        for (Enumeration e = zipFiles.elements(); e.hasMoreElements();) {
            ZipFile zipFile = (ZipFile) e.nextElement();
            try {
                zipFile.close();
            } catch (IOException ioe) {
                // ignore
            }
        }
        zipFiles = new Hashtable();
        if (project != null) {
            project.removeBuildListener(this);
        }
        project = null;
    }

    /**
     * Empty implementation to satisfy the BuildListener interface.
     *
     * @param event the buildStarted event
     */
    public void buildStarted(BuildEvent event) {
        // Not significant for the class loader.
    }

    /**
     * Cleans up any resources held by this classloader at the end
     * of a build.
     *
     * @param event the buildFinished event
     */
    public void buildFinished(BuildEvent event) {
        cleanup();
    }

    /**
     * Cleans up any resources held by this classloader at the end of
     * a subbuild if it has been created for the subbuild's project
     * instance.
     *
     * @param event the buildFinished event
     *
     * @since Ant 1.6.2
     */
    public void subBuildFinished(BuildEvent event) {
        if (event.getProject() == project) {
            cleanup();
        }
    }

    /**
     * Empty implementation to satisfy the BuildListener interface.
     *
     * @param event the buildStarted event
     *
     * @since Ant 1.6.2
     */
    public void subBuildStarted(BuildEvent event) {
        // Not significant for the class loader.
    }

    /**
     * Empty implementation to satisfy the BuildListener interface.
     *
     * @param event the targetStarted event
     */
    public void targetStarted(BuildEvent event) {
        // Not significant for the class loader.
    }

    /**
     * Empty implementation to satisfy the BuildListener interface.
     *
     * @param event the targetFinished event
     */
    public void targetFinished(BuildEvent event) {
        // Not significant for the class loader.
    }

    /**
     * Empty implementation to satisfy the BuildListener interface.
     *
     * @param event the taskStarted event
     */
    public void taskStarted(BuildEvent event) {
        // Not significant for the class loader.
    }

    /**
     * Empty implementation to satisfy the BuildListener interface.
     *
     * @param event the taskFinished event
     */
    public void taskFinished(BuildEvent event) {
        // Not significant for the class loader.
    }

    /**
     * Empty implementation to satisfy the BuildListener interface.
     *
     * @param event the messageLogged event
     */
    public void messageLogged(BuildEvent event) {
        // Not significant for the class loader.
    }

    /**
     * add any libraries that come with different java versions
     * here
     */
    public void addJavaLibraries() {
        Vector packages = JavaEnvUtils.getJrePackages();
        Enumeration e = packages.elements();
        while (e.hasMoreElements()) {
            String packageName = (String) e.nextElement();
            addSystemPackageRoot(packageName);
        }
    }

    /**
     * Returns a <code>String</code> representing this loader.
     * @return the path that this classloader has.
     */
    public String toString() {
        return "AntClassLoader[" + getClasspath() + "]";
    }

}