
package org.apache.ant.antdsl.xtext;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class AntDSLStandaloneSetup extends AntDSLStandaloneSetupGenerated{

	public static void doSetup() {
		new AntDSLStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

