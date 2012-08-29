package org.apache.ant.antdsl;

import java.util.List;

import org.apache.tools.ant.ArgumentProcessor;
import org.apache.tools.ant.Project;

public class AntDSLArgProcessor implements ArgumentProcessor {

    public int readArgument(String arg) {
        if (arg.equals("-update-build")) {
            return 1;
        }
        return 0;
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

    public void printUsage(StringBuffer msg, String lSep) {
        msg.append("  -update-build          launch a resolve of the ant path" + lSep);
    }

}
