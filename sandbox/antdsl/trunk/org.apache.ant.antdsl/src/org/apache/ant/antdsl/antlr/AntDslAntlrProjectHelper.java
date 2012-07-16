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

}
