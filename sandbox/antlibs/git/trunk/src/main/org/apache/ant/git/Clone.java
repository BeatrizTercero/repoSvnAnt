package org.apache.ant.git;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;

public class Clone extends AbstractGitTask {

	private boolean noCheckout;
	private boolean bare;
	
	public void execute() throws BuildException {
		validate();
        Commandline c = new Commandline();
        c.setExecutable("git");
        c.createArgument(true).setValue("clone");
        if(isNoCheckout()) {
        	c.createArgument(false).setValue("--no-checkout");
        }
        if(isBare()) {
        	c.createArgument(false).setValue("--bare");
        }
        c.createArgument(false).setValue(getGitRepo());
        if (null != getDest()) {
        	c.createArgument(false).setValue(getDest().getAbsolutePath());
        }
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
	
	public boolean isNoCheckout() {
		return noCheckout;
	}

	public void setNoCheckout(boolean noCheckout) {
		this.noCheckout = noCheckout;
	}

	public boolean isBare() {
		return bare;
	}

	public void setBare(boolean bare) {
		this.bare = bare;
	}
}
