/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Ant", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.tools.ant.taskdefs.optional;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.DTDLocation;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.XMLCatalog;
import org.apache.tools.ant.util.JAXPUtils;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.ParserAdapter;

/**
 * Checks XML files are valid (or only well formed). The
 * task uses the SAX2 parser implementation provided by JAXP by default
 * (probably the one that is used by Ant itself), but one can specify any
 * SAX1/2 parser if needed
 * @author Raphael Pierquin <a href="mailto:raphael.pierquin@agisphere.com">raphael.pierquin@agisphere.com</a>
 * @author Nick Pellow <a href="mailto:nick@svana.org">nick@svana.org</a>
 */
public class XMLValidateTask extends Task {

    protected static String INIT_FAILED_MSG =
        "Could not start xml validation: ";

    // ant task properties
    // defaults
    protected boolean failOnError = true;
    protected boolean warn = true;
    protected boolean lenient = false;
    protected String  readerClassName = null;

    protected File file = null; // file to be validated
    protected Vector filesets = new Vector(); // sets of file to be validated
    protected Path classpath;


    /**
     * the parser is viewed as a SAX2 XMLReader. If a SAX1 parser is specified,
     * it's wrapped in an adapter that make it behave as a XMLReader.
     * a more 'standard' way of doing this would be to use the JAXP1.1 SAXParser
     * interface.
     */
    protected XMLReader xmlReader = null; // XMLReader used to validation process
    protected ValidatorErrorHandler errorHandler
        = new ValidatorErrorHandler(); // to report sax parsing errors

    /** The vector to store all attributes (features) to be set on the parser. **/
    private Vector attributeList = new Vector();


    private XMLCatalog xmlCatalog = new XMLCatalog();

    /**
     * Specify how parser error are to be handled.
     * Optional, default is <code>true</code>.
     * <p>
     * If set to <code>true</code> (default), throw a buildException if the
     * parser yields an error.
     */
    public void setFailOnError(boolean fail) {

        failOnError = fail;
    }

    /**
     * Specify how parser error are to be handled.
     * <p>
     * If set to <code>true</true> (default), log a warn message for each SAX warn event.
     */
    public void setWarn(boolean bool) {

        warn = bool;
    }

    /**
     * Specify whether the parser should be validating. Default is <code>true</code>.
     * <p>
     * If set to false, the validation will fail only if the parsed document is not well formed XML.
     * <p>
     * this option is ignored if the specified class with {@link #setClassName(String)} is not a SAX2
     * XMLReader.
     */
    public void setLenient(boolean bool) {

        lenient = bool;
    }

    /**
     * Specify the class name of the SAX parser to be used. (optional)
     * @param className should be an implementation of SAX2 <code>org.xml.sax.XMLReader</code>
     * or SAX2 <code>org.xml.sax.Parser</code>.
     * <p> if className is an implementation of <code>org.xml.sax.Parser</code>, {@link #setLenient(boolean)},
     * will be ignored.
     * <p> if not set, the default will be used.
     * @see org.xml.sax.XMLReader
     * @see org.xml.sax.Parser
     */
    public void setClassName(String className) {

        readerClassName = className;
    }


    /**
     * Specify the classpath to be searched to load the parser (optional)
     */
    public void setClasspath(Path classpath) {

        if (this.classpath == null) {
            this.classpath = classpath;
        } else {
            this.classpath.append(classpath);
        }
    }

    /**
     * @see #setClasspath
     */
    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(getProject());
        }
        return this.classpath.createPath();
    }

    /**
     * Where to find the parser class; optional.
     * @see #setClasspath
     */
    public void setClasspathRef(Reference r) {
        createClasspath().setRefid(r);
    }

    /**
     * specify the file to be checked; optional.
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * add an XMLCatalog as a nested element; optional.
     */
    public void addConfiguredXMLCatalog(XMLCatalog catalog) {
        xmlCatalog.addConfiguredXMLCatalog(catalog);
    }

    /**
     * specify a set of file to be checked
     */
    public void addFileset(FileSet set) {
        filesets.addElement(set);
    }

    /**
     * Add an attribute nested element. This is used for setting arbitrary
     * features of the SAX parser.
     * Valid attributes
     * <a href=http://www.saxproject.org/apidoc/org/xml/sax/package-summary.html#package_description">include</a>
     * @since ant1.6
     */
    public Attribute createAttribute() {
        final Attribute feature = new Attribute();
        attributeList.addElement(feature);
        return feature;
    }

    public void init() throws BuildException {
        super.init();
        xmlCatalog.setProject(getProject());
    }

    /**
     * Create a DTD location record; optional.
     * This stores the location of a DTD. The DTD is identified
     * by its public Id.
     */
    public DTDLocation createDTD() {
        DTDLocation dtdLocation = new DTDLocation();
        xmlCatalog.addDTD(dtdLocation);
        return dtdLocation;
    }

    protected EntityResolver getEntityResolver() {
        return xmlCatalog;
    }

    public void execute() throws BuildException {

        int fileProcessed = 0;
        if (file == null && (filesets.size() == 0)) {
            throw new BuildException("Specify at least one source - a file or a fileset.");
        }

        initValidator();

        if (file != null) {
            if (file.exists() && file.canRead() && file.isFile())  {
                doValidate(file);
                fileProcessed++;
            } else {
                String errorMsg = "File " + file + " cannot be read";
                if (failOnError) {
                    throw new BuildException(errorMsg);
                } else {
                    log(errorMsg, Project.MSG_ERR);
                }
            }
        }

        for (int i = 0; i < filesets.size(); i++) {

            FileSet fs = (FileSet) filesets.elementAt(i);
            DirectoryScanner ds = fs.getDirectoryScanner(getProject());
            String[] files = ds.getIncludedFiles();

            for (int j = 0; j < files.length ; j++)  {
                File srcFile = new File(fs.getDir(getProject()), files[j]);
                doValidate(srcFile);
                fileProcessed++;
            }
        }
        log(fileProcessed + " file(s) have been successfully validated.");
    }

    /**
     * init the parser :
     * load the parser class, and set features if necessary
     */
    private void initValidator() {

        Object reader = null;
        if (readerClassName == null) {
            try {
                reader = JAXPUtils.getXMLReader();
            } catch (BuildException exc) {
                reader = JAXPUtils.getParser();
            }
        } else {

            Class readerClass = null;
            try {
                // load the parser class
                if (classpath != null) {
                    AntClassLoader loader = new AntClassLoader(getProject(), classpath);
                    readerClass = loader.loadClass(readerClassName);
                    AntClassLoader.initializeClass(readerClass);
                } else {
                    readerClass = Class.forName(readerClassName);
                }

                reader = readerClass.newInstance();
            } catch (ClassNotFoundException e) {
                throw new BuildException(INIT_FAILED_MSG + readerClassName, e);
            } catch (InstantiationException e) {
                throw new BuildException(INIT_FAILED_MSG + readerClassName, e);
            } catch (IllegalAccessException e) {
                throw new BuildException(INIT_FAILED_MSG + readerClassName, e);
            }
        }

        // then check it implements XMLReader
        if (reader instanceof XMLReader) {
            xmlReader = (XMLReader) reader;
            log("Using SAX2 reader " + reader.getClass().getName(),
                Project.MSG_VERBOSE);
        } else {

            // see if it is a SAX1 Parser
            if (reader instanceof Parser) {
                xmlReader = new ParserAdapter((Parser) reader);
                log("Using SAX1 parser " + reader.getClass().getName(),
                    Project.MSG_VERBOSE);
            }  else {
                throw new BuildException(INIT_FAILED_MSG + readerClassName
                                         + " implements nor SAX1 Parser nor SAX2 XMLReader.");
            }
        }

        xmlReader.setEntityResolver(getEntityResolver());
        xmlReader.setErrorHandler(errorHandler);

        if (!(xmlReader instanceof ParserAdapter)) {
            // turn validation on
            if (!lenient) {
                boolean ok = setFeature("http://xml.org/sax/features/validation", true, true);
                if (!ok) {
                    throw new BuildException(INIT_FAILED_MSG
                                             + readerClassName
                                             + " doesn't provide validation");
                }
            }
            // set the feature from the attribute list
            for (int i = 0; i < attributeList.size(); i++) {
                Attribute feature = (Attribute) attributeList.elementAt(i);
                setFeature(feature.getName(),
                           feature.getValue(),
                           true);

            }
        }
    }

    /**
     * Set a feature on the parser.
     * @param feature the name of the feature to set
     * @param value the value of the feature
     * @param warn whether to war if the parser does not support the feature

     */
    private boolean setFeature(String feature, boolean value, boolean warn) {

        boolean  toReturn = false;
        try {
            xmlReader.setFeature(feature, value);
            toReturn = true;
        } catch (SAXNotRecognizedException e) {
            if (warn) {
                log("Could not set feature '"
                    + feature
                    + "' because the '" +
                       readerClassName + "' parser doesn't recognize it",
                    Project.MSG_WARN);
            }
        } catch (SAXNotSupportedException  e) {
            if (warn) {
                log("Could not set feature '"
                    + feature
                    + "' because the '" +
                        readerClassName + "' parser doesn't support it",
                    Project.MSG_WARN);
            }
        }
        return toReturn;
    }

    /**
     * parse the file
     */
    private void doValidate(File afile) {
        try {
            log("Validating " + afile.getName() + "... ", Project.MSG_VERBOSE);
            errorHandler.init(afile);
            InputSource is = new InputSource(new FileReader(afile));
            String uri = "file:" + afile.getAbsolutePath().replace('\\', '/');
            for (int index = uri.indexOf('#'); index != -1;
                 index = uri.indexOf('#')) {
                uri = uri.substring(0, index) + "%23"
                    + uri.substring(index + 1);
            }
            is.setSystemId(uri);
            xmlReader.parse(is);
        } catch (SAXException ex) {
            if (failOnError) {
                throw new BuildException("Could not validate document "
                    + afile);
            }
        } catch (IOException ex) {
            throw new BuildException("Could not validate document " + afile,
                ex);
        }

        if (errorHandler.getFailure()) {
            if (failOnError) {
                throw new BuildException(afile + " is not a valid XML document.");
            } else {
                log(afile + " is not a valid XML document", Project.MSG_ERR);
            }
        }
    }

    /**
     * ValidatorErrorHandler role :
     * <ul>
     * <li> log SAX parse exceptions,
     * <li> remember if an error occured
     * </ul>
     */
    protected class ValidatorErrorHandler implements ErrorHandler {

        protected File currentFile = null;
        protected String lastErrorMessage = null;
        protected boolean failed = false;

        public void init(File file) {
            currentFile = file;
            failed = false;
        }

        // did an error happen during last parsing ?
        public boolean getFailure() {

            return failed;
        }

        public void fatalError(SAXParseException exception) {
            failed = true;
            doLog(exception, Project.MSG_ERR);
        }

        public void error(SAXParseException exception) {
            failed = true;
            doLog(exception, Project.MSG_ERR);
        }

        public void warning(SAXParseException exception) {
            // depending on implementation, XMLReader can yield hips of warning,
            // only output then if user explicitely asked for it
            if (warn) {
                doLog(exception, Project.MSG_WARN);
            }
        }

        private void doLog(SAXParseException e, int logLevel) {

            log(getMessage(e), logLevel);
        }

        private String getMessage(SAXParseException e) {
            String sysID = e.getSystemId();
            if (sysID != null) {
                try {
                    int line = e.getLineNumber();
                    int col = e.getColumnNumber();
                    return new URL(sysID).getFile() +
                        (line == -1 ? "" : (":" + line +
                                            (col == -1 ? "" : (":" + col)))) +
                        ": " + e.getMessage();
                } catch (MalformedURLException mfue) {
                }
            }
            return e.getMessage();
        }
    }

    /**
     * The class to create to set a feature of the parser.
     * @since ant1.6
     * @author <a href="mailto:nick@svana.org">Nick Pellow</a>
     */
    public class Attribute {
        /** The name of the attribute to set.
         *
         * Valid attributes <a href=http://www.saxproject.org/apidoc/org/xml/sax/package-summary.html#package_description">include.</a>
         */
        private String attributeName = null;

        /**
         * The value of the feature.
         **/
        private boolean attributeValue;

        /**
         * Set the feature name.
         * @param name the name to set
         */
        public void setName(String name) {
            attributeName = name;
        }
        /**
         * Set the feature value to true or false.
         * @param value
         */
        public void setValue(boolean value) {
            attributeValue = value;
        }

        /**
         * Gets the attribute name.
         * @return the feature name
         */
        public String getName() {
            return attributeName;
        }

        /**
         * Gets the attribute value.
         * @return the featuree value
         */
        public boolean getValue() {
            return attributeValue;
        }
    }
}
