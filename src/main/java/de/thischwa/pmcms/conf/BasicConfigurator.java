/*******************************************************************************
 * Poor Man's CMS (pmcms) - A very basic CMS generating static html pages.
 * http://poormans.sourceforge.net
 * Copyright (C) 2004-2013 by Thilo Schwarz
 * 
 * == BEGIN LICENSE ==
 * 
 * Licensed under the terms of any of the following licenses at your
 * choice:
 * 
 *  - GNU Lesser General Public License Version 2.1 or later (the "LGPL")
 *    http://www.gnu.org/licenses/lgpl.html
 * 
 *  - Mozilla Public License Version 1.1 or later (the "MPL")
 *    http://www.mozilla.org/MPL/MPL-1.1.html
 * 
 * == END LICENSE ==
 ******************************************************************************/
package de.thischwa.pmcms.conf;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.tool.PropertiesTool;


/**
 * Adopt the basic configuration task.
 * <ol>
 * <li>Reads the common and user's properties.</li>
 * <li>Set special properties and environment variables.</li>
 * <li>Initializes the logger.</li>
 * <li>Initializes spring.</li>
 * </ol>
 */
public class BasicConfigurator {
	public static final String PROPERTIES_NAME = "pmcms.properties";
	private File dataDir;
	private AbstractApplicationContext context;
	private Properties props;

	public BasicConfigurator() {
		init();
	}

	public BasicConfigurator(File dataDir) {
		System.setProperty("data.dir", dataDir.getAbsolutePath());
		init();
	}
	
	private void init() {
		if (System.getProperty("data.dir") == null)
			throw new IllegalArgumentException("No data directory set!");
		dataDir = new File(System.getProperty("data.dir"));
		if (!dataDir.exists())
			throw new IllegalArgumentException(String.format("Data directory not found: %s", dataDir.getAbsolutePath()));
		
		// load and merge the properties 
		loadProperties();
		
		// build special props
		String baseUrl = String.format("http://%s:%s/", props.get("pmcms.jetty.host"), props.get("pmcms.jetty.port"));
		props.setProperty("baseurl", baseUrl);
		props.setProperty("data.dir", dataDir.getAbsolutePath());
		System.setProperty("content.types.user.table", new File(Constants.APPLICATION_DIR, "lib/content-types.properties").getAbsolutePath());
		System.setProperty("baseurl", baseUrl);  // just need it in VelocityUtils
		
		// init log4j
		LogManager.resetConfiguration();
		PropertyConfigurator.configure(PropertiesTool.getProperties(props, "log4j"));
		Logger logger = Logger.getLogger(BasicConfigurator.class);
		logger.info("*** log4j initialized!");

		// init the spring framework
		try {
			AnnotationConfigApplicationContext ctx =  new AnnotationConfigApplicationContext();
			ctx.scan("de.thischwa.pmcms");
			PropertySourcesPlaceholderConfigurer config = new PropertySourcesPlaceholderConfigurer();
			config.setProperties(props);
			config.postProcessBeanFactory(ctx.getDefaultListableBeanFactory());
			ctx.refresh();
			context = ctx;
			logger.info("*** Spring initialized!");
			PropertiesManager pm = context.getBean(PropertiesManager.class);
			pm.setProperties(props);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		logger.info("*** Basic configuration successful done.");
	}

	public File getDataDir() {
		return dataDir;
	}

	public AbstractApplicationContext getContext() {
		return context;
	}

	private void loadProperties() {
		props = new Properties();
		try {
			// the default props
			InputStream defaultIn = new BufferedInputStream(BasicConfigurator.class.getResourceAsStream("/default.properties"));

			// the user's props
			File propsFile = new File(dataDir, PROPERTIES_NAME);
			InputStream usersIn = new BufferedInputStream(new FileInputStream(propsFile));

			// load the props
			props = PropertiesTool.loadProperties(defaultIn, usersIn);

			// replace the data path
			for (Object key : props.keySet()) {
				String val = (String) props.get(key);
				if (val.contains("${datapath}")) {
					val = val.replace("${datapath}", dataDir.getAbsolutePath());
					props.setProperty((String) key, val.replace(File.separator, "/"));
				} 
			}
		} catch (Exception e) {
			props.clear();
			throw new RuntimeException("Can't read common.properties or pmcms.properties!", e);
		}
	}
}
