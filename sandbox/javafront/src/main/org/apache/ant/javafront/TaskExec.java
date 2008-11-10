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
package org.apache.ant.javafront;

import java.util.Hashtable;
import java.util.Properties;
import java.util.Map;
import java.util.Stack;

import org.apache.tools.ant.launch.*;
import org.apache.tools.ant.*;

import org.apache.ant.javafront.builder.TagBuilder;
import org.apache.ant.javafront.builder.Tag;


public class TaskExec implements AntMain {

    private enum NextStatementIs {
        TAG, ATTRIBUTE, TEXT;
    }

    public void startAnt(String[] args, Properties additionalUserProperties, ClassLoader coreLoader) {
        // Initializing
        Project    project = initProject();
        TagBuilder builder = TagBuilder.forProject(project);

        // Process the arguments
        Stack<Tag> tags = new Stack<Tag>();
        NextStatementIs nextIs = NextStatementIs.TAG;
        Tag current = null;
        StringBuilder text = new StringBuilder();
        for(int i=0; i<args.length; i++) {
            String arg = args[i];
            if (arg.equals("+")) {
                nextIs = NextStatementIs.TAG;
            } else if (arg.equals("-")) {
                debug("CLOSE: " + tags.pop());
                current = tags.peek();
                nextIs = NextStatementIs.ATTRIBUTE;
            } else if (arg.equals("#")) {
                nextIs = NextStatementIs.TEXT;
            } else {
                switch (nextIs) {
                    case TAG :
                         Tag newTag = builder.tag(arg);
                         tags.push(newTag);
                         if (current != null) {
                             // This is not a root element so add it to its parent.
                             current.withChild(newTag);
                         }
                         current = newTag;
                         nextIs = NextStatementIs.ATTRIBUTE;
                         debug("TAG  : " + arg);
                         break;
                    case ATTRIBUTE :
                         String key = arg;
                         String value = args[++i];
                         current.withAttribute(key, value);
                         debug("ATTR : " + key + "=" + value);
                         break;
                    case TEXT :
                         // We have to add spaces becaused they are removed while passing
                         // from the command line to Java. So first store the text word by word,
                         // add spaces if nessessary and finally add the text if the last
                         // text was found.
                         if (text.length() > 0) {
                            text.append(" ");
                         }
                         text.append(arg);
                         debug("TEXT : += '" + arg + "'");
                         break;
                    default :
                         // no-op
                }
                if (!(nextIs == NextStatementIs.TEXT) && text.length()>0) {
                    // We have stored and no further text, so add it to the element.
                    debug("XXSTORE: " + text.toString());
                    current.withNestedText(text.toString());
                    text = new StringBuilder();
                }
            }
        }
        
        // Close text
        if (text.length()>0) {
            current.withNestedText(text.toString());
            debug("STORE: " + text.toString());
        }

        // Close all open nested elements
        debug("Closing all open nested elements.");
        for(int i=tags.size(); i>1; i--) {
            debug("CLOSE: " + tags.peek().toString());
            tags.pop().build();
        }
        
        // Run the task
        debug("RUN  : " + tags.firstElement());
        debug("Current Configuration:");
        debug(0, tags.firstElement().getUE());

        tags.firstElement().execute();
        
    }

    private Project initProject() {
        DefaultLogger logger = new DefaultLogger();
        logger.setOutputPrintStream(System.out);
        logger.setErrorPrintStream(System.err);
        logger.setMessageOutputLevel(Project.MSG_INFO);

        Project rv = new Project();
        rv.addBuildListener(logger);
        rv.init();

        return rv;
    }
    
    
    // ========== Debug facilities ==========
    

    private boolean debug = true;

    private void debug(String s) {
        if (debug) {
            System.out.println(s);
        }
    }
    private void debug(int indent, UnknownElement ue) {
        if (debug) {
            // Visualize the hierarchy.
            for(int i=0; i<indent-1; i++) {
                System.out.print("|  ");
            }
            if (indent > 0) {
                System.out.print("+--");
            }
            // Print the information of the current UE.
            System.out.println(debug(ue));
            // Print the nested elements.
            java.util.List<UnknownElement> children = ue.getChildren();
            if (children != null) {
                for(UnknownElement o : children) {
                    debug(indent + 1, o);
                }
            }
        }
    }
    private String debug(UnknownElement ue) {
        StringBuilder sb = new StringBuilder();
        sb.append(ue.getTag());
        Map m = ue.getWrapper().getAttributeMap();
        for (java.util.Iterator i = m.entrySet().iterator(); i.hasNext();) {
            sb.append(" ").append(i.next());
        }
        return sb.toString();
    }
}