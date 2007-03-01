package org.apache.ant.manual4eclipse;

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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.JAXPUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * This task creates the files needed for generating an Eclipse Help PlugIn
 * from Ant's manual. Mainly the toc.xml and the plugin.xml.
 */
public class Manual4EclipseTask extends Task {


    /** Line Separator. */
    String BR = System.getProperty("line.separator");

    /** Ant Property: The output directory for generated files. */
    private File dir;

    /** Ant Property: The directory where the Ant manual (sources) is. */
    private File manualDir;
    

    /** List of files which are already processed. */
    List<File> processedFiles = new ArrayList<File>();

    /** List of Link references <tt><a href="LINK"></tt> to ignore. */
    List<String> ignoreLinks = new ArrayList<String>();

    /** List of Link references <tt><a href="LINK"></tt> to ignore. */
    List<String> ignoreTargets = new ArrayList<String>();

    /** List of Link references <tt><a href="LINK"></tt> to ignore. */
    List<String> ignoreText = new ArrayList<String>();


    public Manual4EclipseTask() {
        ignoreLinks.add("api/index.html");
        ignoreTargets.add("_main");
        ignoreText.add("Ant API");
        ignoreText.add("Table of Contents");
    }


    @Override
    public void execute() throws BuildException {
        validate();
        log("Generate Eclipse plugin files");
        try {
            createToc(new File(manualDir, "toc.html"));
            createPluginXml();
        } catch (Exception e) {
            throw new BuildException(e, getLocation());
        }
    }


    private void validate() {
        if (dir==null) {
            throw new BuildException("'dir' must point to a directory where to generate the files.");
        }
        if (manualDir==null) {
            throw new BuildException("'manualDir' must point to the directory of the Ant Manual.");
        }
        if (!dir.canWrite()) {
            throw new BuildException("Output directory '" + dir + "' is not writable.");
        }
        if (!manualDir.canRead()) {
            throw new BuildException("Manual directory '" + manualDir + "' is not readable.");
        }
    }


    private void createPluginXml() throws IOException {
        log("generating plugin.xml", Project.MSG_INFO);
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>").append(BR);
        sb.append("<?eclipse version=\"3.2\"?>").append(BR);
        sb.append("<plugin>").append(BR);
        sb.append("    <extension point=\"org.eclipse.help.toc\">").append(BR);
        sb.append("        <toc file=\"toc.xml\" primary=\"true\" />").append(BR);
        sb.append("        <index path=\"index\"/>").append(BR);
        sb.append("    </extension>").append(BR);
        sb.append("    <extension point=\"org.eclipse.help.base.luceneSearchParticipants\">").append(BR);
        sb.append("        <binding participantId=\"org.eclipse.help.base.xhtml\"/>").append(BR);
        sb.append("    </extension>").append(BR);
        sb.append("</plugin>").append(BR);
        write(sb, new File(dir, "plugin.xml"));
    }



    private void createToc(File listHtml) throws IOException, SAXException {
        log("generating toc.xml", Project.MSG_INFO);
        StringBuffer toc = new StringBuffer();
        toc.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>").append(BR);
        toc.append("<?NLS TYPE=\"org.eclipse.help.toc\"?>").append(BR);
        toc.append("<toc label=\"Ant Manual\" topic=\"cover.html\">").append(BR);
        createTopic(toc, listHtml);
        toc.append("</toc>").append(BR);
        write(toc, new File(dir, "toc.xml"));
    }



    private void createTopic(StringBuffer toc, File listHtml)
            throws SAXException, IOException {
        if (processedFiles.contains(listHtml)) {
            return;
        }
        log("Processing " + listHtml, Project.MSG_VERBOSE);
        processedFiles.add(listHtml);
        Document document = JAXPUtils.getDocumentBuilder().parse(listHtml);
        NodeList tagsA = document.getElementsByTagName("a");
        for (int i = 0; i < tagsA.getLength(); i++) {
            Topic topic = new Topic(tagsA.item(i));
            if (topic.isIgnorable()) continue;

            if (topic.hasNestedTopics()) {
                // start recursion
                toc.append("<").append(topic).append(">").append(BR);
                if ("anttaskslist.html".equals(topic.href)) {
                    createTopic4TaskOverview(toc);
                } else {
                    createTopic(toc, new File(manualDir, topic.href));
                }
                toc.append("</topic>").append(BR);
            } else {
                toc.append("<").append(topic).append("/>").append(BR);
            }
        }
    }



    /**
     * Generate topics for the core and optional tasks.
     * Because the task lists (overview, core, optional) have a TOC-header,
     * the generic recursion-algorithm does not work. So we have to deal
     * with that.
     * @param toc    StringBuffer with the toc.xml in progress
     * @param label  Label for the root node
     * @throws IOException 
     * @throws SAXException 
     * @throws BuildException 
     */
    private void createTopic4TaskOverview(StringBuffer toc) throws BuildException, SAXException, IOException {
        File taskListFile = new File(manualDir, "anttaskslist.html");
        File coreTaskFile = new File(manualDir, "coretasklist.html");
        File optionalTask = new File(manualDir, "optionaltasklist.html");
        processedFiles.add(taskListFile);
        processedFiles.add(coreTaskFile);
        processedFiles.add(optionalTask);

        log("Using hard coded TOC-structure instead of " + taskListFile, Project.MSG_VERBOSE);
        
        toc.append("<topic label=\"Overview of Ant Tasks\" href=\"tasksoverview.html\"/>").append(BR);
        toc.append("<topic label=\"Core Tasks\">").append(BR);
        // Core Tasks
        createTopics4TaskList(toc, coreTaskFile);
        toc.append("</topic>").append(BR);
        toc.append("<topic label=\"Optional Tasks\">").append(BR);
        // Optional Tasks
        toc.append("<topic label=\"Library Dependencies\" href=\"install.html#librarydependencies\"/>").append(BR);
        createTopics4TaskList(toc, optionalTask);
        toc.append("</topic>").append(BR);
    }



    /**
     * Creates a <topic> element (with nested) for Task list.
     * The task lists are placed after a &lt;h3> header and there are
     * &lt;a> tags before that. So we have to do more work and search
     * for the right are.
     * @param toc    StringBuffer with the toc.xml in progress
     * @param file   The file with the task list
     * @throws BuildException
     * @throws SAXException
     * @throws IOException
     */
    private void createTopics4TaskList(StringBuffer toc, File file) throws BuildException, SAXException, IOException {
        log("Processing " + file, Project.MSG_VERBOSE);
        Document doc = JAXPUtils.getDocumentBuilder().parse(file);
        // Access to the HTML root element
        Element html = doc.getDocumentElement();
        // search for the <body> element
        Element body = null;
        NodeList childNodes = html.getChildNodes();
        for (int i=0; i<childNodes.getLength(); i++) {
            Node n = childNodes.item(i);
            if (n.getNodeName().equalsIgnoreCase("body")) {
                body = (Element)n;
            }
        }
        // search in the body for the task listing
        childNodes = body.getChildNodes();
        boolean needSection = false;
        for (int i=0; i<childNodes.getLength(); i++) {
            Node n = childNodes.item(i);
            // After the H3-section we'll get the task listing
            if (needSection) {
                if (n.getNodeName().equalsIgnoreCase("a")) {
                    Topic t = new Topic(n);
                    toc.append("<").append(t.toString()).append("/>").append(BR);
                }
            }
            if (n.getNodeName().equalsIgnoreCase("h3")) {
                needSection = true;
            }
        }
    }




    /**
     * Writes a StringBuffer to file.
     * @param sb   The StringBuffer to write
     * @param file The file to write into
     * @throws IOException if writing throws an IOException
     */
    private void write(StringBuffer sb, File file) throws IOException {
        FileWriter out = new FileWriter(file);
        out.write(sb.toString());
        out.close();
    }



    public void setDir(File dir) {
        this.dir = dir;
    }

    public void setManualDir(File manualDir) {
        this.manualDir = manualDir;
    }




    /**
     * This class encapsulates the information of a <tt><a></tt> tag from a 
     * toc-listing from the Ant manual.
     */
    public class Topic {
        /** <topic>s label attribute gets the value from &lt;a>s text content. */
        String label;
        /** Internal value coming from &lt;a>s target attribute. */
        String target;
        /** <topic>s href attribute gets the value from &lt;a>s href attribute. */
        String href;
        
        /**
         * Constructor analyzes a DOMNode and creates a Topic object.
         * @param n the DOMNode
         */
        public Topic(Node n) {
            Node item = n.getAttributes().getNamedItem("href");
            href   = (item!=null) ? item.getTextContent() : "";
            item = n.getAttributes().getNamedItem("target");
            target = (item!=null) ? item.getTextContent() : "";
            label  = n.getTextContent().trim();
        }
        /**
         * Checks the data against the three ignore-lists.
         * @return <i>true</i> if one (or more) of the lists matches
         */
        public boolean isIgnorable() {
            return ignoreLinks.contains(href)
                || ignoreTargets.contains(target)
                || ignoreText.contains(label);
        }
        /**
         * You have to create nested <topic> elements if the link-target
         * would open the linked page in the navigation frame. This method
         * does the check.
         * @return <i>true</i> if nestd <topic> elements could come.
         */
        public boolean hasNestedTopics() {
            return "navFrame".equals(target);
        }
        @Override
        public String toString() {
            StringBuffer rv = new StringBuffer("topic");
            if (label.length()>0) rv.append(" label=\"").append(label).append("\"");
            if (href.length()>0 && !hasNestedTopics())  rv.append(" href=\"").append(href).append("\"");
            return rv.toString().replaceAll("&", "&amp;");
        }
    }

}
