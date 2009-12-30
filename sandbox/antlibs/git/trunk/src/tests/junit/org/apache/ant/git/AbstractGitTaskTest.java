package org.apache.ant.git;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tools.ant.BuildException;

public class AbstractGitTaskTest extends TestCase {
	
	//TODO - need to consider special cases like 'origin'
	// or check .git/config for any short names
	public void testValidGitRepo() {
		String[] validRepos = {
				"rsync://host.xz/path/to/repo.git",
				"http://host.xz[:port]/path/to/repo.git",
				"https://host.xz[:port]/path/to/repo.git",
				"git://host.xz[:port]/path/to/repo.git",
				"git://host.xz[:port]/~user/path/to/repo.git",
				"ssh://[user@]host.xz[:port]/path/to/repo.git",
				"ssh://[user@]host.xz/path/to/repo.git",
				"ssh://[user@]host.xz/~user/path/to/repo.git",
				"ssh://[user@]host.xz/~/path/to/repo.git"
		};
		TestGitTask t = new TestGitTask();
		for(int i=0; i<validRepos.length;i++) {
			t.setGitRepo(validRepos[i]);
			Assert.assertTrue(t.validGitRepo());
		}
	}
	
	public void testConfiguredShortNames() throws Exception {
		
	}
	
	public void testSpecialShortNames() throws Exception {
		
	}
	
	public void testValidate() throws Exception {
		TestGitTask t = new TestGitTask();
		try {
			t.validate();
		} catch(BuildException e) {
			Assert.assertEquals("You must specify a gitRepo value",e.getMessage());
		} catch(Exception e) {
			Assert.fail("Expected BuildException");
		}
	}
	
	private class TestGitTask extends AbstractGitTask {
		
	}

}
