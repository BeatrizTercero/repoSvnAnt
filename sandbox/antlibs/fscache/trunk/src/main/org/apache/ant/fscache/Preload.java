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
package org.apache.ant.fscache;

import java.util.Iterator;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Resources;

/**
 * Task to force cached resources to be written to disk, for use with
 * tasks that operate on a directory structure rather than individual files (e.g. <javac>).
 */
public class Preload extends Task {
    private static final String INCOMPLETE = "At least one CachedCollection or CachedResource is required";
    private static final String WRONG_TYPE = "Only CachedResources are supported";

    private Resources resources;

    /**
     * Add the specified ResourceCollection<CachedResource>.
     * @param resourceCollection to add
     */
    public void addConfigured(ResourceCollection resourceCollection) {
        if (resourceCollection == null) {
            throw new IllegalArgumentException("Cannot add null ResourceCollection");
        }
        synchronized (this) {
            if (resources == null) {
                resources = new Resources();
            }
        }
        resources.add(resourceCollection);
    }

    /**
     * {@inheritDoc}
     */
    public void execute() {
        if (this.resources == null) {
            throw new BuildException(INCOMPLETE);
        }
        for (Iterator iter = resources.iterator(); iter.hasNext();) {
            Object o = iter.next();
            if (o instanceof CachedResource == false) {
                throw new BuildException(WRONG_TYPE);
            }
            //force the file to be written:
            ((CachedResource) o).getFile();
        }
    }
}
