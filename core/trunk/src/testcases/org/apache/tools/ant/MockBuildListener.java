/*
 * Copyright  2001,2004 Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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

import java.util.Vector;

import junit.framework.Assert;

public class MockBuildListener extends Assert implements BuildListener {

    private final Vector buffer = new Vector();
    private final Project project;

    public MockBuildListener(final Project project) {
        this.project = project;
    }

    public void buildStarted(BuildEvent event) {}
    public void buildFinished(BuildEvent event) {}
    public void targetStarted(BuildEvent event) {}
    public void targetFinished(BuildEvent event) {}
    public void taskStarted(BuildEvent event) {}
    public void taskFinished(BuildEvent event) {}

    public void messageLogged(final BuildEvent actual) {
        if(actual.getPriority()==Project.MSG_DEBUG)
            return;
        assertTrue("unexpected messageLogged: "+actual.getMessage(), !buffer.isEmpty());
        assertEquals("unexpected project ", project, actual.getProject());

        BuildEvent expected = (BuildEvent) buffer.elementAt(0);
        buffer.removeElementAt(0);
        assertEquals("unexpected messageLogged ", expected.getMessage(), actual.getMessage());
        assertEquals("unexpected priority ", expected.getPriority(), actual.getPriority());
    }

    public void assertEmpty() {
        assertTrue("MockBuildListener is not empty", buffer.isEmpty());
    }

    public void addBuildEvent(final String message, final int priority) {
        final BuildEvent be = new BuildEvent(project);
        be.setMessage(message, priority);
        buffer.addElement(be);
    }

}
