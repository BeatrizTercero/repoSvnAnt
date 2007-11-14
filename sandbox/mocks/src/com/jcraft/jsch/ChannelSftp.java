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

package com.jcraft.jsch;

import java.io.File;
import java.util.Vector;

public class ChannelSftp extends ChannelExec {
    public static int SSH_FX_NO_SUCH_FILE;
    public class LsEntry {
        public A getAttrs() {
            return null;
        }
        public String getFilename() {
            return null;
        }
    }
    public void cd(String s) {}
    public void get(String s1, String s2, SftpProgressMonitor p) {}
    public Vector ls(String remoteFile) {
        return null;
    }
    public void mkdir(String dir) {}
    public void put(String absolutePath, String remotePath, SftpProgressMonitor monitor) {}
    public String pwd() {
        return null;
    }
    public SftpATTRS stat(String remoteFile) throws SftpException {
        return null;
    }
    
    public class A extends File {
        private static final long serialVersionUID = 0L;
        public A() {
            super("");
        }
        public long getSize() {
            return 0;
        }
        public boolean isDir() {
            return false;
        }
    }
}
