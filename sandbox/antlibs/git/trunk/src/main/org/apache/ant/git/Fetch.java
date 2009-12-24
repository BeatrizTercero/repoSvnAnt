package org.apache.ant.git;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;

public class Fetch extends AbstractGitTask {

	private boolean prune = false;
	private boolean noTags = false;
	
	public void execute() throws BuildException {
		validate();
        Commandline c = new Commandline();
        c.setExecutable("git");
        c.createArgument(true).setValue("fetch");
        if(isDryrun()) {
        	c.createArgument(false).setValue("--no-checkout");
        }
        if(isPrune()) {
        	c.createArgument(false).setValue("--prune");
        }
        if(isNoTags()) {
        	c.createArgument(false).setValue("--no-tags");
        }
        c.createArgument(false).setValue(getGitRepo());
        this.addConfiguredCommandline(c);
        super.execute();
	}
	
	private void validate() throws BuildException {
		if(null == getGitRepo()) {
			throw new BuildException("You must specify a gitRepo value");
		}
		if(!validGitRepo()) {
			throw new BuildException("The repository [ "+ getGitRepo()+ " ] specified is not recognised");
		}
	}

	public boolean isPrune() {
		return prune;
	}

	public void setPrune(boolean prune) {
		this.prune = prune;
	}

	public boolean isNoTags() {
		return noTags;
	}

	public void setNoTags(boolean noTags) {
		this.noTags = noTags;
	}
	
}
