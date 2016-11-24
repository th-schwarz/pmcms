package de.thischwa.pmcms.server;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.conf.BasicConfigurator;
import de.thischwa.pmcms.conf.InitializationManager;

public class StartJetty {

	public static void main(String[] args) throws Exception {

		InitializationManager.start(new BasicConfigurator(Constants.APPLICATION_DIR), true);
		
		while(true)
			Thread.sleep(500);
	}

}
