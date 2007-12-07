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

package org.apache.commons.net.bsd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RExecClient {
    public static int DEFAULT_PORT = 0;
    public void connect(String s, int i) throws IOException {}
    public void disconnect() throws IOException {}
    public OutputStream getOutputStream() {
        return null;
    }
    public InputStream getInputStream() {
        return null;
    }
    public boolean isConnected() {
        return false;
    }
    public void rexec(String s1, String s2, String s3) throws IOException {}
}