package org.apache.ant.antdsl;

import java.io.PrintStream;
import java.util.List;

import org.apache.tools.ant.ArgumentProcessor;
import org.apache.tools.ant.Project;

public class AntDSLArgProcessor implements ArgumentProcessor {

    public int readArguments(String[] args, int pos) {
        if (args[pos].equals("-update-build")) {
            return pos + 1;
        }
        return -1;
    }

    public boolean handleArg(List<String> extraArgs) {
        return false;
    }

    public void prepareConfigure(Project project, List<String> extraArgs) {
        project.addReference(AbstractAntDslProjectHelper.REFID_UPDATE_BUILD, true);
    }

    public boolean handleArg(Project project, List<String> arg) {
        return true;
    }

    public void printUsage(PrintStream writer) {
        writer.println("  -update-build          launch a resolve of the ant path");
    }

}
