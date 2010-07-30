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
package org.apache.ant.groovyfront.cache;

import groovy.lang.Binding;
import groovy.lang.Script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.FileLock;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.util.FileUtils;
import org.codehaus.groovy.runtime.InvokerHelper;

public class CachedGroovyScriptLoader extends GroovyScriptLoader {

    // for the hex encoder
    private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    static final String TIMESTAMP_FILE = ".timestamp";

    private File cacheDir;
    private MessageDigest md5Digester;

    public CachedGroovyScriptLoader(File cacheDir) {
        initDigester();
        this.cacheDir = cacheDir;
        checkCacheDir();
    }

    private void initDigester() {
        try {
            md5Digester = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new BuildException(
                    "The MD5 hash algorithm is needed in the jvm to use the groovyfront cache",
                    e);
        }
    }

    private void checkCacheDir() {
        if (cacheDir.exists()) {
            if (!cacheDir.isDirectory()) {
                throw new BuildException(
                        "The groovyfront cache is not a directory: "
                                + cacheDir.getAbsolutePath());
            }
        } else {
            boolean created = cacheDir.mkdirs();
            if (!created) {
                throw new BuildException(
                        "The groovyfront cache could not be created at: "
                                + cacheDir.getAbsolutePath());
            }
        }
    }

    public Script loadScript(Resource r, Binding binding, ClassLoader parent) {
        long timestamp = r.getLastModified();
        if (timestamp == Resource.UNKNOWN_DATETIME) {
            // we cannot cache resource which don't have a modification time
            // if is especially true for dynamically build resources
            return parseScript(r, binding, parent, null);
        }

        // try to get a unique identifier from a resource
        // name + implementation + hash code
        byte[] md5 = md5Digester
                .digest((r.getName() + r.getClass().getName() + r.hashCode())
                        .getBytes());
        String encodedMD5 = encodeHex(md5);

        File scriptCache = new File(cacheDir, encodedMD5);
        if (scriptCache.exists()) {
            return checkAndLoadFromCache(r, timestamp, binding, parent,
                    scriptCache);
        }

        File tmpScriptCache = new File(cacheDir, encodedMD5 + ".tmp");
        FileLock fileLock = creatAndLockTmpCache(r, tmpScriptCache);

        if (scriptCache.exists()) {
            // the cache has been created by another thread or another jvm
            return checkAndLoadFromCache(r, timestamp, binding, parent,
                    scriptCache);
        }

        try {
            // do build the cache
            Script script = buildCache(r, binding, parent, tmpScriptCache);

            // promote the build as the real cache
            // hopefully this will get atomic
            // TODO check if on Windows we can move a locked folder
            tmpScriptCache.renameTo(scriptCache);

            return script;
        } finally {
            ThreadSafeFileLocker.releaseDir(tmpScriptCache, fileLock);
        }
    }

    private Script checkAndLoadFromCache(Resource r, long timestamp,
            Binding binding, ClassLoader parent, File scriptCache) {
        if (!scriptCache.isDirectory()) {
            throw new BuildException(
                    "The groovyfront cache for the build file '" + r
                            + "' is not a directory: '"
                            + scriptCache.getAbsolutePath()
                            + "'. You should delete that file and retry.");
        }

        // ensure that only one thread is reading that particular cache entry
        FileLock fileLock;
        try {
            fileLock = ThreadSafeFileLocker.lockDir(scriptCache);
        } catch (FileNotFoundException e) {
            throw new BuildException(
                    "A folder has been deleted while tryin to access it: "
                            + scriptCache.getAbsolutePath());
        } catch (IOException e) {
            throw new BuildException(
                    "A lock could not be obtained on the folder "
                            + scriptCache.getAbsolutePath());
        }

        try {
            long cachedTimestamp = readTimestamp(r, scriptCache);
            if (cachedTimestamp == timestamp) {
                return loadFromCache(r, scriptCache, binding, parent);
            }

            // out dated cache, remove old stuff and recreate it
            try {
                cleanCacheDir(scriptCache);
            } catch (IOException e) {
                throw new BuildException(e.getMessage());
            }

            return buildCache(r, binding, parent, scriptCache);
        } finally {
            ThreadSafeFileLocker.releaseDir(scriptCache, fileLock);
        }
    }

    static void cleanCacheDir(File scriptCache) throws IOException {
        File[] files = scriptCache.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                cleanCacheDir(files[i]);
                files[i].delete();
            } else if (!FileUtils.getFileUtils().tryHardToDelete(files[i])) {
                throw new IOException("Unable to delete old cached file "
                        + files[i].getAbsolutePath());
            }
        }
    }

    private FileLock creatAndLockTmpCache(Resource r, File tmpScriptCache) {
        boolean created = tmpScriptCache.mkdirs();
        if (!created && !tmpScriptCache.exists()) {
            throw new BuildException(
                    "The groovyfront cache for the build file '" + r
                            + "' could not be created at: "
                            + tmpScriptCache.getAbsolutePath());
        }
        try {
            return ThreadSafeFileLocker.lockDir(tmpScriptCache);
        } catch (FileNotFoundException e) {
            throw new BuildException("A folder just created has been deleted: "
                    + tmpScriptCache.getAbsolutePath());
        } catch (IOException e) {
            throw new BuildException(
                    "A lock could not be obtained on the folder "
                            + tmpScriptCache.getAbsolutePath());
        }
    }

    /**
     * Build the cached version of the build file
     * 
     * @param r
     * @param binding
     * @param parent
     * @param cacheDir
     * @return
     */
    private Script buildCache(Resource r, Binding binding, ClassLoader parent,
            File cacheDir) {
        writeTimestamp(r, cacheDir);
        return parseScript(r, binding, parent, cacheDir);
    }

    /**
     * Load the build file from the already compile classes in the cache
     * 
     * @param r
     * @param scriptCache
     * @param binding
     * @param parent
     * @return
     */
    private Script loadFromCache(Resource r, File scriptCache, Binding binding,
            ClassLoader parent) {
        URLClassLoader scriptLoader;
        try {
            scriptLoader = new URLClassLoader(new URL[] { scriptCache.toURI()
                    .toURL() }, parent);
        } catch (MalformedURLException e) {
            // should not happen
            throw new RuntimeException(e);
        }
        Class scriptClass;
        try {
            scriptClass = scriptLoader.loadClass(asJavaClass(r.getName()));
        } catch (ClassNotFoundException e) {
            throw new BuildException(
                    "The cache is corrupted, the script could not be loaded", e);
        }
        return InvokerHelper.createScript(scriptClass, binding);
    }

    /**
     * Write the timestamp of the resource in the cache.
     * 
     * @param r
     * @param scriptCache
     */
    private void writeTimestamp(Resource r, File scriptCache) {
        File scriptInfoFile = new File(scriptCache, TIMESTAMP_FILE);
        long timestamp = r.getLastModified();
        PrintWriter writer;
        try {
            writer = new PrintWriter(new OutputStreamWriter(
                    new FileOutputStream(scriptInfoFile)), false);
        } catch (FileNotFoundException e) {
            throw new BuildException(
                    "The groovyfront cache for the build file '"
                            + r
                            + "' could not be created, impossible to create the file '"
                            + scriptInfoFile.getAbsolutePath() + "'");
        }
        try {
            writer.println(timestamp);
        } finally {
            writer.close();
        }
        if (writer.checkError()) {
            throw new BuildException(
                    "The groovyfront cache for the build file '"
                            + r
                            + "' could not be created, error encountered while writing the file '"
                            + scriptInfoFile.getAbsolutePath() + "'");
        }
    }

    /**
     * Read the timestamp stored in the cache. On any error,
     * Resource.UNKNOWN_DATETIME is returned.
     * 
     * @param r
     * @param scriptCache
     * @return
     */
    private long readTimestamp(Resource r, File scriptCache) {
        File scriptInfoFile = new File(scriptCache, TIMESTAMP_FILE);
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(scriptInfoFile));
        } catch (FileNotFoundException e) {
            return Resource.UNKNOWN_DATETIME;
        }
        try {
            return Long.parseLong(reader.readLine());
        } catch (NumberFormatException e) {
            return Resource.UNKNOWN_DATETIME;
        } catch (IOException e) {
            return Resource.UNKNOWN_DATETIME;
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                // don't care
            }
            // touch the file to make the cache cleaner consider this file as
            // recently used
            scriptInfoFile.setLastModified(System.currentTimeMillis());
        }
    }

    /**
     * Turn the build file name into a compatible Java class name, the same way
     * as it is mapped into {@link #asGroovyClass(String)}
     * 
     * @param filename
     * @return
     */
    private String asJavaClass(String filename) {
        String groovyClass = asGroovyClass(filename);
        if (groovyClass.toLowerCase().endsWith(".groovy")) {
            return groovyClass.substring(0, groovyClass.length() - 7);
        }
        return groovyClass;
    }

    /**
     * Encode a byte array into hexadecimal encoding, a safe encoding for case
     * insensitive file systems
     * 
     * @param data
     * @return
     */
    private String encodeHex(byte[] data) {
        // code from apache commons-codec
        int l = data.length;
        char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = HEX_DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = HEX_DIGITS[0x0F & data[i]];
        }
        return new String(out);
    }

}
