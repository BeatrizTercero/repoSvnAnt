package org.apache.ant.antdsl;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class RefTask extends Task {

    private String name;

    private Object value;

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(Object value) {
        this.value = value;
    }
    
    @Override
    public void execute() throws BuildException {
        getProject().addReference(name, value);
    }
}
