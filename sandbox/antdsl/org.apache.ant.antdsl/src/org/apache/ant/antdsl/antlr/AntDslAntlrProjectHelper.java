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
