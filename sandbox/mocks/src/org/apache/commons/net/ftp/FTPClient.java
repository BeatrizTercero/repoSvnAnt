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

package org.apache.commons.net.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FTPClient {
    public void changeToParentDirectory() {}
    public boolean changeWorkingDirectory(String s) throws IOException {
        return false;
    }
    public void connect(String s, int i) {}
    public void configure(FTPClientConfig cfg) {}
    public void cwd(String s) {}
    public boolean deleteFile(Object object) {
        return false;
    }
    public void disconnect() throws IOException {}
    public int getReplyCode() {
        return 0;
    }
    public void enterLocalPassiveMode() {
    }
    public String getReplyString() {
        return null;
    }
    public boolean isConnected() {
        return false;
    }
    public String[] getReplyStrings() {
        return null;
    }
    public FTPFile[] listFiles() throws IOException {
        return null;
    }
    public FTPFile[] listFiles(String name) {
        return null;
    }
    public boolean login(String userid, String password, String account) {
        return false;
    }
    public boolean login(String userid, String password) throws IOException {
        return false;
    }
    public void logout() {}
    public boolean makeDirectory(String name) {
        return false;
    }
    public String printWorkingDirectory() throws IOException {
        return null;
    }
    public boolean removeDirectory(String resolveFile) {
        return false;
    }
    public void retrieveFile(String resolveFile, OutputStream outstream) {}
    public boolean sendSiteCommand(String theCMD) {
        return false;
    }
    public void setFileType(int image_file_type) {}
    public void storeFile(String name, InputStream instream) {}
}