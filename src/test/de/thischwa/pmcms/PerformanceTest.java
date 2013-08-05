package de.thischwa.pmcms;


import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.conf.BasicConfigurator;
import de.thischwa.pmcms.conf.InitializationManager;
import de.thischwa.pmcms.livecycle.SiteHolder;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.model.thread.ThreadController;
import de.thischwa.pmcms.model.tool.SitePersister;
import de.thischwa.pmcms.view.renderer.ExportRenderer;

public class PerformanceTest {
	private static Logger logger = Logger.getLogger(PerformanceTest.class);

	public static void main(String[] args) throws Exception {
		if (InitializationManager.lock()) 
			System.exit(1);
		System.setProperty("data.dir", Constants.APPLICATION_DIR.getAbsolutePath());
		InitializationManager.start(new BasicConfigurator());
		SiteHolder siteHolder = InitializationManager.getBean(SiteHolder.class);
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		Site site = SitePersister.read("s-th.info");
		siteHolder.setSite(site);
	    ExportRenderer exportRenderer = InitializationManager.getBean(ExportRenderer.class);
		exportRenderer.setSite(site);
		exportRenderer.init();
		exportRenderer.run();
		stopWatch.stop();
		logger.info("+++++ Export time: ".concat(stopWatch.toString()));
		
		ThreadController.getInstance().stopAllThreads();
		InitializationManager.end();
	}

}
