package org.apache.ant.vss;

import org.apache.tools.ant.BuildFileTest;

public class MSVSSTest extends BuildFileTest {

    public MSVSSTest() {
        this("MSVSSTest");
    }
    
    public MSVSSTest(String name) {
        super(name);
    }
    
    public void setUp() throws Exception {
        configureProject("src/etc/testcases/msvss.xml");
    }

    public void tearDown() throws Exception {
        executeTarget("cleanup");
    }
    
    public void testMSVSS() throws Exception {
        executeTarget("test-all");
    }
    
    public void testMSVSSMOVE() throws Exception {
        executeTarget("test-move");
    }
    
    public void testMSVSSCLOAK() throws Exception {
        executeTarget("test-cloak");
    }
    
    public void testMSVSSDELETE() throws Exception {
        executeTarget("test-delete");
    }
    
    public void testMSVSSLOCATE() throws Exception {
        executeTarget("test-locate");
    }
}
