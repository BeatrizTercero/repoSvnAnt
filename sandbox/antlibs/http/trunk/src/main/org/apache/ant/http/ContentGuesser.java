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

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * this is a class to work around the fact a function (guessContentTypeFromName)
 * is protected in java1.3 and below. It is public since then, but as Ant runs
 * on older runtimes, this class exists to poke a hole in the security
 *
 * @created March 17, 2001
 */
public class ContentGuesser extends URLConnection {

    /**
     * stub Constructor for the ContentGuesser object.
     *
     * @param url Description of Parameter
     */
    ContentGuesser(URL url) {
        super(url);
    }


    /**
     * this stub is needed for the build.
     *
     * @throws IOException Description of Exception
     */
    public void connect()
            throws IOException {
    }


    /**
     * make a protected method public. This guesses file type from extension.
     * It's ok for very well known types...
     *
     * @param filename file to guess type of
     * @return what the system guessed
     */
    public static String guessContentType(String filename) {
        return guessContentTypeFromName(filename);
    }
}

