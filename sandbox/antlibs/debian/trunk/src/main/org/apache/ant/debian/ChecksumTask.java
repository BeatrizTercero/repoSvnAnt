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
package org.apache.ant.debian;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.Iterator;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.StringUtils;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.types.resources.Restrict;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.selectors.Type;

/**
 * Creates a single file with the checksums (MD5 by default)
 * of the files specified. Note different from the core
 * &lt;checksum&gt; task. 
 */
public class ChecksumTask extends Task {

    private String algorithm = "MD5";
    
    private File output;
    
    /**
     * Resource Collection.
     */
    private FileUnion resources = null;
    
    private MessageDigest messageDigest;
    
    public void execute() throws BuildException {
        validate();
        try {
            messageDigest = MessageDigest.getInstance(algorithm);
        } catch(NoSuchAlgorithmException e) {
            throw new BuildException(e);
        }
        if (messageDigest == null) {
            throw new BuildException("Unable to create Message Digest", getLocation());
        }
        createChecksums();
    }
    
    private void validate() throws BuildException {
        if(resources == null || resources.size() == 0) {
            throw new BuildException("You must specify some resources to create the checksums for.");
        }
        if(!resources.isFilesystemOnly()) {
            throw new BuildException("Cannot create a checksum for non file-based resources.");
        }
        
        if (output == null) {
            throw new BuildException("You must specify an output file.");
        }
        //TODO add more checks
    }
    
    /**
     * Creates the checksums and writes them to the output file
     * This is an adaptation & simplyfied version of the standard
     * Checksum task in Ant.
     * @throws BuildException
     */
    private void createChecksums() throws BuildException {
        FileOutputStream os = null;
        FileInputStream is = null;
        DigestInputStream dis = null;
        byte[] buffer = new byte[8 * 1024];
        try {
            os = new FileOutputStream(output);
            for(Iterator i = resources.iterator(); i.hasNext();) {
                messageDigest.reset();
                File src = ((FileResource)i.next()).getFile();
                is = new FileInputStream(src);
                dis = new DigestInputStream(is, messageDigest);
                while (dis.read(buffer, 0, 8 * 1024) != -1) { /* do nothing */}
                dis.close();
                is.close();
                is = null;
                byte[] fileDigest = messageDigest.digest ();
                String checksum = createDigestString(fileDigest);
                os.write((checksum + " " + src.getName()).getBytes());
                os.write(StringUtils.LINE_SEP.getBytes());
            }
        } catch (Exception e) {
            throw new BuildException("Problem occured creating checksums",e);
        } finally {
            FileUtils.close(dis);
            FileUtils.close(is);
            FileUtils.close(os);
        }
    }
    
    private String createDigestString(byte[] fileDigest) {
        StringBuffer checksumSb = new StringBuffer();
        for (int i = 0; i < fileDigest.length; i++) {
            String hexStr = Integer.toHexString(0x00ff & fileDigest[i]);
            if (hexStr.length() < 2) {
                checksumSb.append("0");
            }
            checksumSb.append(hexStr);
        }
        return checksumSb.toString();
    }
    
    /**
     * Sets the digest algorithm, by default it's MD5
     * @param a algorithm to set
     */
    public void setAlgorithm(String a) {
        algorithm = a;
    }
    
    public void setOutput(File o) {
        output = o;
    }
    
    /**
     * Files to generate checksums for.
     * @param set a fileset of files to generate checksums for.
     */
    public void addFileset(FileSet set) {
        add(set);
    }
    
    /**
     * Add a resource collection.
     * @param rc the ResourceCollection to add.
     */
    public void add(ResourceCollection rc) {
        if (rc == null) {
            return;
        }
        resources = (resources == null) ? new FileUnion() : resources;
        resources.add(rc);
    }
    
    private static class FileUnion extends Restrict {
        private Union u;
        FileUnion() {
            u = new Union();
            super.add(u);
            super.add(Type.FILE);
        }
        public void add(ResourceCollection rc) {
            u.add(rc);
        }
    }
}