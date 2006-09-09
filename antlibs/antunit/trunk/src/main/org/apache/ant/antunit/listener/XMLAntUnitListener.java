/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.apache.ant.antunit.listener;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import org.apache.ant.antunit.AssertionFailedException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.DateUtils;
import org.apache.tools.ant.util.DOMElementWriter;
import org.apache.tools.ant.util.DOMUtils;
import org.apache.tools.ant.util.StringUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A test listener for &lt;antunit&gt; modelled after the XML JUnit
 * test listener that is part of Ant.
 */
public class XMLAntUnitListener extends BaseAntUnitListener {
    private static final String INDENT = "  ";
    private OutputStream out = null;
    private Writer wri;
    private DOMElementWriter domWri = new DOMElementWriter(true);
    private Document doc;
    private Element root;
    private Element currentTest;

    public XMLAntUnitListener() {
        super(new BaseAntUnitListener.SendLogTo(SendLogTo.FILE), "xml");
    }

    public void startTestSuite(Project testProject, String buildFile) {
        try {
            super.startTestSuite(testProject, buildFile);
            wri = new OutputStreamWriter(getOut(buildFile), "UTF8");
            doc = DOMUtils.newDocument();
            root = doc.createElement(XMLConstants.TESTSUITE);
            // if we want to (ab)use <junitreport> name needs to
            // follow the structure expected by that task:
            // package.class.  package will be the directory holding
            // the build file (file separators replaced by dots) and
            // class the build file name with the last dot replaced
            // by an underscore
            root.setAttribute(XMLConstants.ATTR_NAME, normalize(buildFile));
            root.setAttribute(XMLConstants.BUILD_FILE, buildFile);

            //add the timestamp
            String timestamp = DateUtils.format(new Date(),
                                                DateUtils
                                                .ISO8601_DATETIME_PATTERN);
            root.setAttribute(XMLConstants.TIMESTAMP, timestamp);
            //and the hostname.
            root.setAttribute(XMLConstants.HOSTNAME, getHostname());

            domWri.writeXMLDeclaration(wri);
            domWri.openElement(root, wri, 0, INDENT, true);
            wri.write(StringUtils.LINE_SEP);
        } catch (IOException ex) {
            throw new BuildException(ex);
        }
    }

    public void endTestSuite(Project testProject, String buildFile) {
        try {
            Element e = DOMUtils.createChildElement(root,
                                                    XMLConstants.ATTR_TESTS);
            DOMUtils.appendText(e, String.valueOf(runCount));
            domWri.write(e, wri, 1, INDENT);
            e = DOMUtils.createChildElement(root, XMLConstants.ATTR_FAILURES);
            DOMUtils.appendText(e, String.valueOf(failureCount));
            domWri.write(e, wri, 1, INDENT);
            e = DOMUtils.createChildElement(root, XMLConstants.ATTR_ERRORS);
            DOMUtils.appendText(e, String.valueOf(errorCount));
            domWri.write(e, wri, 1, INDENT);
            e = DOMUtils.createChildElement(root, XMLConstants.ATTR_TIME);
            DOMUtils.appendText(e,
                                String.valueOf((System.currentTimeMillis()
                                                - start)
                                               / 1000.0));
            domWri.write(e, wri, 1, INDENT);

            domWri.closeElement(root, wri, 0, INDENT, true);

            wri.flush();
        } catch (IOException ex) {
            throw new BuildException(ex);
        } finally {
            close(out);
        }
    }

    public void startTest(String target) {
        try {
            super.startTest(target);
            currentTest = DOMUtils.createChildElement(root,
                                                      XMLConstants.TESTCASE);
            currentTest.setAttribute(XMLConstants.ATTR_NAME, target);
            domWri.openElement(currentTest, wri, 1, INDENT, true);
            wri.write(StringUtils.LINE_SEP);
        } catch (IOException ex) {
            throw new BuildException(ex);
        }
    }

    public void endTest(String target) {
        try {
            Element e = DOMUtils.createChildElement(currentTest,
                                                    XMLConstants.ATTR_TIME);
            DOMUtils.appendText(e,
                                String.valueOf((System.currentTimeMillis()
                                                - testStart)
                                               / 1000.0));
            domWri.write(e, wri, 2, INDENT);
            domWri.closeElement(currentTest, wri, 1, INDENT, true);
        } catch (IOException ex) {
            throw new BuildException(ex);
        }
    }

    public void addFailure(String target, AssertionFailedException ae) {
        super.addFailure(target, ae);
        formatError(XMLConstants.FAILURE, ae);
    }
    public void addError(String target, Throwable ae) {
        super.addError(target, ae);
        formatError(XMLConstants.ERROR, ae);
    }

    private void formatError(String type, Throwable t) {
        try {
            Element e = DOMUtils.createChildElement(currentTest, type);
            Location l = getLocation(t);
            if (l.getLineNumber() != 0) {
                e.setAttribute(XMLConstants.ATTR_LINE,
                               String.valueOf(l.getLineNumber()));
            }
            if (l.getColumnNumber() != 0) {
                e.setAttribute(XMLConstants.ATTR_COLUMN,
                               String.valueOf(l.getColumnNumber()));
            }
            String message = t.getMessage();
            if (message != null && message.length() > 0) {
                e.setAttribute(XMLConstants.ATTR_MESSAGE, t.getMessage());
            }
            e.setAttribute(XMLConstants.ATTR_TYPE, t.getClass().getName());
            DOMUtils.appendText(e, StringUtils.getStackTrace(t));
            domWri.write(e, wri, 2, INDENT);
        } catch (IOException ex) {
            throw new BuildException(ex);
        }
    }

    /**
     * get the local hostname - stolen from junit.XMLJUnitResultFormatter
     * @return the name of the local host, or "localhost" if we cannot
     * work it out
     */
    private String getHostname()  {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "localhost";
        }
    }

}