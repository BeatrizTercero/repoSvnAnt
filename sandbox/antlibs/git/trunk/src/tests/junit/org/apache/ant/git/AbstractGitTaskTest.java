package org.apache.ant.git;

import junit.framework.Assert;
import junit.framework.TestCase;

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
	
	private class TestGitTask extends AbstractGitTask {
		
	}

}
