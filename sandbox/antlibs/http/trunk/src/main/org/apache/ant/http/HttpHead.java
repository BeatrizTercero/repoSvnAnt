/*
 * Copyright  2001-2006 The Apache Software Foundation
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

package org.apache.ant.http;

import org.apache.tools.ant.BuildException;

/**
 * Head is a get with a different method and the notion of destination file
 * missing. Why would anyone want to make a HEAD request? a) side effects on the
 * server and b) polling for stuff
 *
 * @created March 17, 2001
 */

public class HttpHead extends HttpTask {

    /**
     * this must be overridden by implementations to set the request method to
     * GET, POST, whatever.
     *
     * @return HEAD always
     */
    public String getRequestMethod() {
        return "HEAD";
    }

    /**
     * in HEAD requests, parameters go after the URL.
     *
     * @return true always
     */

    protected boolean areParamsAddedToUrl() {
        return true;
    }

    /**
     * add a check for all the destination settings being null; nothing else
     * makes sense for a HEAD.
     *
     * @throws BuildException only throw this when the failonerror flag is true
     */

    protected void verifyArguments()
            throws BuildException {
        if (getDestFile() != null || getDestinationProperty() != null) {
            throw new BuildException(
                    "destination properties must not be defined for a HEAD request");
        }
        super.verifyArguments();
    }

}
