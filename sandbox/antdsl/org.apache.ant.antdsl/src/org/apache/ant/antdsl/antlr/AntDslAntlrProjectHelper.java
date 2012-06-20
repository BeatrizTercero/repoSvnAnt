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
package org.apache.ant.antdsl.antlr;

import java.io.IOException;
import java.io.InputStream;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.ant.antdsl.AbstractAntDslProjectHelper;
import org.apache.ant.antdsl.AntDslContext;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

public class AntDslAntlrProjectHelper extends AbstractAntDslProjectHelper {

    @Override
    protected void doParse(InputStream in, String buildFileName, Project project, AntDslContext context) throws IOException {
        AntDSLParser parser;
        try {
            parser = new AntDSLParser(new CommonTokenStream(new AntDSLLexer(new ANTLRInputStream(in))));
            parser.setProject(project);
            parser.setContext(context);
            parser.setProjectHelper(this);

            parser.project();

        } catch (RecognitionException e) {
            throw new BuildException("Syntax error while parsing the build file " + buildFileName, e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                // don't care
            }
        }
    }

    public String readString(String s) {
        if (s == null) {
            return null;
        }
        // first remove the quotes
        s = s.substring(1, s.length() - 1);
        // code copied from org.eclipse.xtext.util.Strings.convertFromJavaString(String, boolean)
        char[] in = s.toCharArray();
        int off = 0;
        int len = s.length();
        char[] convtBuf = new char[len];
        char aChar;
        char[] out = convtBuf;
        int outLen = 0;
        int end = off + len;

        while (off < end) {
            aChar = in[off++];
            if (aChar == '\\') {
                aChar = in[off++];
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    if (off + 4 > end)
                        throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
                    for (int i = 0; i < 4; i++) {
                        aChar = in[off++];
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
                        }
                    }
                    out[outLen++] = (char) value;
                } else {
                    if (aChar == 't') {
                        aChar = '\t';
                    } else if (aChar == 'r') {
                        aChar = '\r';
                    } else if (aChar == 'n') {
                        aChar = '\n';
                    } else if (aChar == 'f') {
                        aChar = '\f';
                    } else if (aChar == 'b') {
                        aChar = '\b';
                    } else if (aChar == '"') {
                        aChar = '\"';
                    } else if (aChar == '\'') {
                        aChar = '\'';
                    } else if (aChar == '\\') {
                        aChar = '\\';
                    } else {
                        throw new IllegalArgumentException("Illegal escape character \\" + aChar);
                    }
                    out[outLen++] = aChar;
                }
            } else {
                out[outLen++] = aChar;
            }
        }
        return new String(out, 0, outLen);
    }

    public String readDoc(String s) {
        if (s == null) {
            return null;
        }
        String[] split = s.split("\r?\n");
        StringBuilder builder = new StringBuilder();
        for (String line : split) {
            builder.append(line.substring(1)); // remove the leading %
            builder.append(' '); // replace the line end by a space
        }
        return builder.toString();
    }

}
