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

package org.apache.ant.http;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * trivial task to get the hostname of a box; as IPaddr, hostname, or fullname.
 *
 * @created 07 January 2002
 */

public class Hostname extends Task {

    /**
     * Property to set.
     */
    private String property;

    /**
     * Should we fail on an error?
     */
    private boolean failonerror = true;

    /**
     * The address to look up
     */
    private boolean address = false;


    /**
     * Sets the FailOnError attribute.
     *
     * @param failonerror The new FailOnError value
     */
    public void setFailOnError(boolean failonerror) {
        this.failonerror = failonerror;
    }


    /**
     * Sets the Address attribute.
     *
     * @param address The new Address value
     */
    public void setAddress(boolean address) {
        this.address = address;
    }

    /**
     * name the property to set to the result.
     *
     * @param property the property name
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * Does the work.
     *
     * @throws BuildException Thrown in unrecoverable error.
     */
    public void execute()
            throws BuildException {
        if (property == null) {
            throw new BuildException("Property attribute must be defined");
        }
        String result;
        Throwable exception = null;
        try {
            if (address) {
                result = getAddress();
            } else {
                result = getHostname();
            }
            project.setNewProperty(property, result);
        } catch (UnknownHostException e) {
            exception = e;
        } catch (SecurityException e) {
            exception = e;
        }
        if (exception != null) {
            if (failonerror) {
                throw new BuildException("resolving hostname", exception);
            } else {
                log("failed to resolve local hostname", Project.MSG_ERR);
            }
        }
    }


    /**
     * Gets the Address attribute of the Hostname object
     *
     * @return The Address value
     * @throws SecurityException    Description of Exception
     * @throws UnknownHostException Description of Exception
     */
    public String getAddress()
            throws SecurityException, UnknownHostException {
        return getLocalHostAddress().getHostAddress();
    }


    /**
     * Gets the Hostname attribute of the Hostname object
     *
     * @return The Hostname value
     * @throws SecurityException    Description of Exception
     * @throws UnknownHostException Description of Exception
     */
    public String getHostname()
            throws SecurityException, UnknownHostException {
        return getLocalHostAddress().getHostName();
    }


    /**
     * Gets the LocalHostAddress attribute of the Hostname object
     *
     * @return The LocalHostAddress value
     * @throws UnknownHostException Description of Exception
     */
    public static InetAddress getLocalHostAddress()
            throws UnknownHostException {
        return InetAddress.getLocalHost();
    }

}

