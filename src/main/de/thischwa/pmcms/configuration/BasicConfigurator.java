/*******************************************************************************
 * Poor Man's CMS (pmcms) - A very basic CMS generating static html pages.
 * http://pmcms.sourceforge.net
 * Copyright (C) 2004-2013 by Thilo Schwarz
 * 
 * == BEGIN LICENSE ==
 * 
 * Licensed under the terms of any of the following licenses at your
 * choice:
 * 
 *  - GNU General Public License Version 2 or later (the "GPL")
 *    http://www.gnu.org/licenses/gpl.html
 * 
 *  - GNU Lesser General Public License Version 2.1 or later (the "LGPL")
 *    http://www.gnu.org/licenses/lgpl.html
 * 
 *  - Mozilla Public License Version 1.1 or later (the "MPL")
 *    http://www.mozilla.org/MPL/MPL-1.1.html
 * 
 * == END LICENSE ==
 ******************************************************************************/
package de.thischwa.pmcms.configuration;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.tool.PropertiesTool;


/**
 * Adopt the basic configuration task. 1) Reads the common and user's properties and copy it into the {@link MyStringProperties}
 * which will be inject in all objects which requires these these properties. 2) Initializing spring. 
 * 
 * @version $Id: BasicConfigurator.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
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
			throw new IllegalArgumentException("No data directory found!");
		
		// load and merge the properties 
		loadProperties();
		
		// build special props
		boolean renderingAvailable = StringUtils.isNotEmpty(props.getProperty("imagemagick.convert.command"))
				&& StringUtils.isNotEmpty(props.getProperty("imagemagick.convert.parameters"))
				&& StringUtils.isNotEmpty(props.getProperty("imagemagick.resolution.export"))
				&& new File(props.getProperty("imagemagick.convert.command")).exists();
		props.setProperty("rendering.available", renderingAvailable ? "true" : "false");
		String baseUrl = String.format("http://%s:%s/", props.get("pmcms.jetty.host"), props.get("pmcms.jetty.port"));
		props.setProperty("baseurl", baseUrl);
		props.setProperty("data.dir", dataDir.getAbsolutePath());
		System.setProperty("content.types.user.table", new File(Constants.APPLICATION_DIR, "lib/content-types.properties").getAbsolutePath());
		
		// init log4j
		LogManager.resetConfiguration();
		PropertyConfigurator.configure(PropertiesTool.getProperties(props, "log4j"));
		Logger logger = Logger.getLogger(BasicConfigurator.class);
		logger.info("*** log4j initialized!");

		// init the spring framework
		try {
			AnnotationConfigApplicationContext ctx =  new AnnotationConfigApplicationContext();
			ctx.scan("de.thischwa.pmcms");
			PropertyPlaceholderConfigurer config = new PropertyPlaceholderConfigurer();
			config.setProperties(props);
			config.postProcessBeanFactory(ctx.getDefaultListableBeanFactory());
			ctx.refresh();
			context = ctx;
			logger.info("*** Spring initialized!");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		logger.info("*** Basic configuration successful ended.");
	}

	public File getDataDir() {
		return dataDir;
	}

	public Properties getProps() {
		return props;
	}

	public AbstractApplicationContext getContext() {
		return context;
	}

	private void loadProperties() {
		props = new Properties();
		try {
			// the common props
			InputStream commonIn = new BufferedInputStream(BasicConfigurator.class.getResourceAsStream("common.properties"));

			// the user's props
			File propsFile = new File(dataDir, PROPERTIES_NAME);
			InputStream usersIn = new BufferedInputStream(new FileInputStream(propsFile));

			// load the props
			props = PropertiesTool.loadProperties(commonIn, usersIn);

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
