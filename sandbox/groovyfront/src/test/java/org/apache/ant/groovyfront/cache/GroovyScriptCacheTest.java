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
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

import junit.framework.TestCase;

import org.apache.ant.groovyfront.cache.CachedGroovyScriptLoader;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.FileUtils;

public class GroovyScriptCacheTest extends TestCase {

    private File groovyFile;
    private File helloFile;
    private File tmpDir;

    protected void setUp() throws Exception {
        tmpDir = File.createTempFile("GroovyScriptCacheTest", "");
        tmpDir.delete();
        tmpDir.mkdir();
        System.out.println("tmp dir: " + tmpDir.getAbsolutePath());

        groovyFile = new File(tmpDir, "test.groovy");
        helloFile = new File(tmpDir, "hello.txt");

        PrintWriter writer = new PrintWriter(new FileOutputStream(groovyFile));
        writer.println("new File(\"" + helloFile.getAbsolutePath()
                + "\").write(\"Hello world\")");
        writer.close();
    }

    protected void tearDown() throws Exception {
        clean(tmpDir);
    }

    private void clean(File dir) throws IOException {
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                clean(files[i]);
                files[i].delete();
            } else {
                FileUtils.getFileUtils().tryHardToDelete(files[i]);
            }
        }
    }

    public void testCache() throws Exception {
        assertFalse(helloFile.exists());

        long t = System.currentTimeMillis();
        GroovyShell shell = new GroovyShell();
        System.out.println("groovy init: " + (System.currentTimeMillis() - t)
                + " ms");
        t = System.currentTimeMillis();
        shell.evaluate(groovyFile);
        System.out.println("hadoc run: " + (System.currentTimeMillis() - t)
                + " ms");

        assertTrue(helloFile.exists());
        Reader reader = new FileReader(helloFile);
        String content = FileUtils.readFully(reader);
        reader.close();
        assertEquals("Hello world", content);

        helloFile.delete();
        assertFalse(helloFile.exists());

        CachedGroovyScriptLoader cache = new CachedGroovyScriptLoader(tmpDir);
        t = System.currentTimeMillis();
        Script script = cache.loadScript(new FileResource(groovyFile),
                new Binding(), this.getClass().getClassLoader());
        script.run();
        System.out.println("cache miss run: "
                + (System.currentTimeMillis() - t) + " ms");

        assertTrue(helloFile.exists());
        reader = new FileReader(helloFile);
        content = FileUtils.readFully(reader);
        reader.close();
        assertEquals("Hello world", content);

        helloFile.delete();
        assertFalse(helloFile.exists());

        t = System.currentTimeMillis();
        script = cache.loadScript(new FileResource(groovyFile), new Binding(),
                this.getClass().getClassLoader());
        script.run();
        System.out.println("cache hit run: " + (System.currentTimeMillis() - t)
                + " ms");

        assertTrue(helloFile.exists());
        reader = new FileReader(helloFile);
        content = FileUtils.readFully(reader);
        reader.close();
        assertEquals("Hello world", content);
    }
}
