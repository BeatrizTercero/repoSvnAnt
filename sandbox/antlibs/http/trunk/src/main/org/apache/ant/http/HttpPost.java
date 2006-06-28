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
import org.apache.tools.ant.Project;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLConnection;
import java.util.Vector;

/**
 * this class does post of form content or raw files. you can have one or the
 * other -as soon as a file is specified all the other properties are dropped on
 * the floor. a file post will have content type determined from the extension,
 * you can override it
 *
 * @created March 17, 2001
 */

public class HttpPost extends HttpTask {

    /**
     * file to upload. Null is ok
     */

    protected File postFile = null;

    /**
     * set the file to post.
     */
    public void setUploadFile(File postFile) {
        this.postFile = postFile;
    }

    /**
     * query the post file.
     *
     * @return the file or null for 'not defined'
     */
    public File getUploadFile() {
        return postFile;
    }

    /**
     * content type. ignored when the file is null, and even then we guess it if
     * aint specified
     */

    private String contentType;

    /**
     * set the content type. Recommended if a file is being uploaded
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * query the content type.
     *
     * @return the content type or null for 'not defined'
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * in a POST, params go in the payload, not in the URL.
     *
     * @return false always
     */

    protected boolean areParamsAddedToUrl() {
        return false;
    }

    /**
     * this override of the base connect pumps up the parameter vector as form
     * data.
     *
     * @param connection where to connect to
     * @throws BuildException build trouble
     * @throws IOException    IO trouble
     */
    protected URLConnection doConnect(URLConnection connection)
            throws BuildException, IOException {

        if (postFile == null) {
            return doConnectFormPost(connection);
        } else {
            return doConnectFilePost(connection);
        }
    }

    /**
     * feed up the parameter vector as form data.
     *
     * @param connection where to connect to
     * @throws BuildException build trouble
     * @throws IOException    IO trouble
     */
    protected URLConnection doConnectFormPost(URLConnection connection)
            throws BuildException, IOException {

        log("Posting data as a form", Project.MSG_VERBOSE);
        // Create the output payload
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(256);
        PrintWriter out = new PrintWriter(byteStream);
        writePostData(out);
        out.flush();
        out.close();
        byte[] data = byteStream.toByteArray();
        //send it

        return doConnectWithUpload(connection,
                "application/x-www-form-urlencoded",
                byteStream.size(),
                new ByteArrayInputStream(data));
    }

    /**
     * feed up the data file.
     *
     * @param connection where to connect to
     * @throws BuildException build trouble
     * @throws IOException    IO trouble
     */
    protected URLConnection doConnectFilePost(URLConnection connection)
            throws BuildException, IOException {
        int size = (int) postFile.length();
        log("Posting file " + postFile, Project.MSG_VERBOSE);
        InputStream instream = new FileInputStream(postFile);
        String type = contentType;
        if (type == null) {
            type = ContentGuesser.guessContentType(postFile.getName());
        }
        return doConnectWithUpload(connection,
                type,
                size,
                instream);
    }


    /**
     * write out post data in form mode.
     *
     * @param out Description of Parameter
     */
    protected void writePostData(PrintWriter out) {
        HttpRequestParameter param;
        Vector params = getRequestParameters();
        for (int i = 0; i < params.size(); i++) {
            if (i > 0) {
                out.print('&');
            }
            param = (HttpRequestParameter) params.get(i);
            out.print(param.toString());
            log("parameter : " + param.toString(), Project.MSG_DEBUG);
        }
    }

    /**
     * this must be overridden by implementations to set the request method to
     * GET, POST, whatever.
     *
     * @return the method string
     */
    public String getRequestMethod() {
        return "POST";
    }


}
