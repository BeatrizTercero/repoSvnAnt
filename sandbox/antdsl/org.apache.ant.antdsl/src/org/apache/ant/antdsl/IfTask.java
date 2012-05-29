package org.apache.ant.antdsl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ant.antdsl.IfTask.ConditionnalSequential;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Sequential;
import org.apache.tools.ant.taskdefs.condition.Condition;

public class IfTask extends Task {

    public static final class ConditionnalSequential extends Sequential {

        private Condition condition;

        public void setCondition(Condition condition) {
            this.condition = condition;
        }

        public boolean eval() throws BuildException {
            return condition.eval();
        }

    }

    private List<ConditionnalSequential> elseIfs = new ArrayList<ConditionnalSequential>();

    private Sequential elses;

    private ConditionnalSequential main;

    public void setMain(ConditionnalSequential main) {
        this.main = main;
    }

    public void addElseIf(ConditionnalSequential elseif) {
        elseIfs.add(elseif);
    }

    public void setElse(Sequential elses) {
        this.elses = elses;
    }

    public void execute() throws BuildException {
        if (main.eval()) {
            main.execute();
        } else {
            boolean done = false;
            Iterator<ConditionnalSequential> itElseIf = elseIfs.iterator();
            while (!done && itElseIf.hasNext()) {
                ConditionnalSequential elseIf = itElseIf.next();
                if (elseIf.eval()) {
                    done = true;
                    elseIf.execute();
                }
            }

            if (!done && elses != null) {
                elses.execute();
            }
        }
    }

}
