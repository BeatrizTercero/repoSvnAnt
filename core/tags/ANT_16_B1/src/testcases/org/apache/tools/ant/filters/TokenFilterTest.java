/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "Ant" and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.tools.ant.filters;

import java.io.File;
import java.io.Reader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.tools.ant.BuildFileTest;
import org.apache.tools.ant.util.FileUtils;

/**
 * @author Peter Reilly
 */
public class TokenFilterTest extends BuildFileTest {

    public TokenFilterTest(String name) {
        super(name);
    }

    public void setUp() {
        configureProject("src/etc/testcases/filters/tokenfilter.xml");
        executeTarget("init");
    }

    public void tearDown() {
        executeTarget("cleanup");
    }

    /** make sure tokenfilter exists */
    public void testTokenfilter() throws IOException {
        executeTarget("tokenfilter");
    }

    public void testTrimignore() throws IOException {
        expectLogContaining("trimignore", "Hello-World");
    }

    public void testStringTokenizer() throws IOException {
        expectLogContaining(
            "stringtokenizer", "#This#is#a#number#of#words#");
    }

    public void testUnixLineOutput() throws IOException {
        expectFileContains(
            "unixlineoutput", "result/unixlineoutput",
            "\nThis\nis\na\nnumber\nof\nwords\n");
    }

    public void testDosLineOutput() throws IOException {
        expectFileContains(
            "doslineoutput", "result/doslineoutput",
            "\r\nThis\r\nis\r\na\r\nnumber\r\nof\r\nwords\r\n");
    }

    public void testFileTokenizer() throws IOException {
        String contents = getFileString(
            "filetokenizer", "result/filetokenizer");
        assertStringContains(contents, "   of words");
        assertStringNotContains(contents, " This is");
    }

    public void testReplaceString() throws IOException {
        expectFileContains(
            "replacestring", "result/replacestring",
            "this is the moon");
    }

    public void testContainsString() throws IOException {
        String contents = getFileString(
            "containsstring", "result/containsstring");
        assertStringContains(contents, "this is a line contains foo");
        assertStringNotContains(contents, "this line does not");
    }

    public void testReplaceRegex() throws IOException {
        if (! hasRegex("testReplaceRegex"))
            return;
        String contents = getFileString(
            "replaceregex", "result/replaceregex");
        assertStringContains(contents, "world world world world");
        assertStringContains(contents, "dog Cat dog");
        assertStringContains(contents, "moon Sun Sun");
        assertStringContains(contents, "found WhiteSpace");
        assertStringContains(contents, "Found digits [1234]");
        assertStringNotContains(contents, "This is a line with digits");
    }

    public void testFilterReplaceRegex() throws IOException {
        if (! hasRegex("testFilterReplaceRegex"))
            return;
        String contents = getFileString(
            "filterreplaceregex", "result/filterreplaceregex");
        assertStringContains(contents, "world world world world");
    }

    public void testHandleDollerMatch() throws IOException {
        if (! hasRegex("testFilterReplaceRegex"))
            return;
        executeTarget("dollermatch");
    }
    
    public void testTrimFile() throws IOException {
        String contents = getFileString(
            "trimfile", "result/trimfile");
        assertTrue("no ws at start", contents.startsWith("This is th"));
        assertTrue("no ws at end", contents.endsWith("second line."));
        assertStringContains(contents, "  This is the second");
    }

    public void testTrimFileByLine() throws IOException {
        String contents = getFileString(
            "trimfilebyline", "result/trimfilebyline");
        assertFalse("no ws at start", contents.startsWith("This is th"));
        assertFalse("no ws at end", contents.endsWith("second line."));
        assertStringNotContains(contents, "  This is the second");
        assertStringContains(contents, "file.\nThis is the second");
    }

    public void testFilterReplaceString() throws IOException {
        String contents = getFileString(
            "filterreplacestring", "result/filterreplacestring");
        assertStringContains(contents, "This is the moon");
    }

    public void testContainsRegex() throws IOException {
        if (! hasRegex("testContainsRegex"))
            return;
        String contents = getFileString(
            "containsregex", "result/containsregex");
        assertStringContains(contents, "hello world");
        assertStringNotContains(contents, "this is the moon");
        assertStringContains(contents, "World here");
    }

    public void testFilterContainsRegex() throws IOException {
        if (! hasRegex("testFilterContainsRegex"))
            return;
        String contents = getFileString(
            "filtercontainsregex", "result/filtercontainsregex");
        assertStringContains(contents, "hello world");
        assertStringNotContains(contents, "this is the moon");
        assertStringContains(contents, "World here");
    }

    public void testContainsRegex2() throws IOException {
        if (! hasRegex("testContainsRegex2"))
            return;
        String contents = getFileString(
            "containsregex2", "result/containsregex2");
        assertStringContains(contents, "void register_bits();");
    }

    public void testDeleteCharacters() throws IOException {
        String contents = getFileString(
            "deletecharacters", "result/deletechars");
        assertStringNotContains(contents, "#");
        assertStringNotContains(contents, "*");
        assertStringContains(contents, "This is some ");
    }

    public void testScriptFilter() throws IOException {
        if (! hasScript("testScriptFilter"))
            return;

        expectFileContains("scriptfilter", "result/scriptfilter",
                           "HELLO WORLD");
    }


    public void testScriptFilter2() throws IOException {
        if (! hasScript("testScriptFilter"))
            return;

        expectFileContains("scriptfilter2", "result/scriptfilter2",
                           "HELLO MOON");
    }

    public void testCustomTokenFilter() throws IOException {
        expectFileContains("customtokenfilter", "result/custom",
                           "Hello World");
    }

    // ------------------------------------------------------
    //   Helper methods
    // -----------------------------------------------------
    private boolean hasScript(String test) {
        try {
            executeTarget("hasscript");
        }
        catch (Throwable ex) {
            System.out.println(
                test + ": skipped - script not present ");
            return false;
        }
        return true;
    }

    private boolean hasRegex(String test) {
        try {
            executeTarget("hasregex");
            expectFileContains("result/replaceregexp", "bye world");
        }
        catch (Throwable ex) {
            System.out.println(test + ": skipped - regex not present "
                               + ex);
            return false;
        }
        return true;
    }

    private void assertStringContains(String string, String contains) {
        assertTrue("[" + string + "] does not contain [" + contains +"]",
                   string.indexOf(contains) > -1);
    }

    private void assertStringNotContains(String string, String contains) {
        assertTrue("[" + string + "] does contain [" + contains +"]",
                   string.indexOf(contains) == -1);
    }

    private String getFileString(String filename)
        throws IOException
    {
        Reader r = null;
        try {
            r = new FileReader(getProject().resolveFile(filename));
            return  FileUtils.newFileUtils().readFully(r);
        }
        finally {
            try {r.close();} catch (Throwable ignore) {}
        }

    }

    private String getFileString(String target, String filename)
        throws IOException
    {
        executeTarget(target);
        return getFileString(filename);
    }

    private void expectFileContains(String name, String contains)
        throws IOException
    {
        String content = getFileString(name);
        assertTrue(
            "expecting file " + name + " to contain " + contains +
            " but got " + content, content.indexOf(contains) > -1);
    }

    private void expectFileContains(
        String target, String name, String contains)
        throws IOException
    {
        executeTarget(target);
        expectFileContains(name, contains);
    }

    public static class Capitalize
        implements TokenFilter.Filter
    {
        public String filter(String token) {
            if (token.length() == 0)
                return token;
            return token.substring(0, 1).toUpperCase() +
                token.substring(1);
        }
    }

}
