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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.support.AbstractApplicationContext;

import de.thischwa.c5c.impl.LocalConnector;
import de.thischwa.c5c.requestcycle.impl.EnabledUserAction;
import de.thischwa.c5c.resource.PropertiesLoader;
import de.thischwa.ckeditor.CKPropertiesLoader;
import de.thischwa.jii.ImageType;
import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.configuration.resource.LabelHolder;
import de.thischwa.pmcms.livecycle.SiteHolder;
import de.thischwa.pmcms.livecycle.UserPathBuilderImpl;
import de.thischwa.pmcms.model.domain.PoPathInfo;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.model.thread.ThreadController;
import de.thischwa.pmcms.model.tool.SitePersister;
import de.thischwa.pmcms.tool.InternalAntTool;
import de.thischwa.pmcms.tool.Locker;
import de.thischwa.pmcms.tool.PropertiesTool;
import de.thischwa.pmcms.tool.image.ImageInfo;
import de.thischwa.pmcms.tool.image.ImageTool;

/**
 * Main object, doing the basic initialization and provides access to the basic configuration data. <br>
 * Jobs:
 * <ul>
 * <li>loading properties and providing methods to get it</li>
 * <li>checking, if another instance is running</li>
 * <li>creation of some basic directories, if not exists</li>
 * <li>start and stop tasks which implements {@link IApplicationLiveCycleListener}</li>
 * <li>stop all threads managed by {@link ThreadController}</li>
 * </ul>
 * 
 * @version $Id: InitializationManager.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class InitializationManager {
	private static Logger logger = Logger.getLogger(InitializationManager.class);

	/** Locker to check if another instance of poormans is running. */
	private static Locker locker = new Locker(new File(Constants.TEMP_DIR, "pmcms.lck"));

	/** The 'global' manager of the properties. */
	private static PropertiesManager pm;

	/** Directory, which contains the backups of the sites. */
	private static File sitesBackupDir;

	/** Directory, which contains the data. */
	private static File dataDir;

	/** List of image file name extensions using in pmcms. */
	private static List<String> allowedImageExtensions;

	/** True, if we are in the admin mode. */
	private static boolean isAdmin = false;

	/** True, if we have to call the cleanup task while starting. */
	private static boolean hasToCleanup = false;

	/** The spring context. */
	private static AbstractApplicationContext context;

	private static SiteHolder siteHolder;
	
	private static boolean imageRenderingEnabled;

	private static boolean enableTasksStart = true;

	static {
		Thread.currentThread().setName("poormans");
	}

	/**
	 * Creates the file based lock.
	 * 
	 * @return <code>true</code>, if a lock already exists, otherwise <code>false</code>.
	 * @see Locker#lock()
	 */
	public static boolean lock() {
		return locker.lock();
	}

	/**
	 * Does all required initialization jobs, needed to run the application.
	 * @param configurator
	 */
	public static void start(final BasicConfigurator configurator) {
		start(configurator, true);
	}
	
	/**
	 * Does all initialization jobs.
	 * 
	 * @param configurator
	 * @param enableTasksStart If false, all {@link IApplicationLiveCycleListener} didn't start. It's only used for testing.
	 */
	public static void start(final BasicConfigurator configurator, boolean enableTasksStart) {
		InitializationManager.enableTasksStart = enableTasksStart;
		if (configurator.getContext() == null)
			throw new RuntimeException("No context found.");
		dataDir = configurator.getDataDir();
		context = configurator.getContext();
		pm = new PropertiesManager(configurator.getProps());
		siteHolder = getBean(SiteHolder.class);

		logger.info("Language: ".concat(LabelHolder.getLocale().getLanguage()));
		logger.info("Application dir: " + Constants.APPLICATION_DIR);
		logger.info("Data dir: " + dataDir.getAbsolutePath());

		sitesBackupDir = new File(pm.getProperty("pmcms.dir.backup"));

		// check some directories
		if (!Constants.TEMP_DIR.exists())
			Constants.TEMP_DIR.mkdirs();
		File tempDir = new File(dataDir, getProperty("pmcms.dir.sites"));
		if (!tempDir.exists())
			tempDir.mkdirs();
		tempDir = new File(getProperty("log4j.appender.FILE.file"));
		if (!tempDir.exists())
			tempDir.mkdirs();
		if (!sitesBackupDir.exists())
			sitesBackupDir.mkdirs();

		// check if we have to trigger a cleanup
		if (hasToCleanup)
			try {
				InternalAntTool.cleanup(dataDir, configurator.getProps());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		// preparing the image extensions
		allowedImageExtensions = new ArrayList<String>();
		ImageInfo ii = getBean(ImageInfo.class);
		for(ImageType type : ii.getSupportedImageTypes()) {
			allowedImageExtensions.addAll(Arrays.asList(type.getExtensions()));
		}
		String imageExts = StringUtils.join(allowedImageExtensions, '|');
		imageRenderingEnabled = getBean(ImageTool.class).isRenderingAvailable();

		// preparing C5Connector.Java
		PropertiesLoader.setProperty("connector.impl", LocalConnector.class.getName());
		PropertiesLoader.setProperty("connector.userActionImpl", EnabledUserAction.class.getName());
		PropertiesLoader.setProperty("connector.userPathBuilderImpl", UserPathBuilderImpl.class.getName());
		PropertiesLoader.setProperty("connector.resourceType.image.extensions.allowed", imageExts);
		
		// preparing CKEditor.Java
		CKPropertiesLoader.setProperty("ckeditor.height", "450px");
		CKPropertiesLoader.setProperty("ckeditor.width", "100%");
		CKPropertiesLoader.setProperty("ckeditor.toolbar", "Default");

		// start all tasks
		if (enableTasksStart) {
			Map<String, IApplicationLiveCycleListener> liveCycleListeners = getBeansOfType(IApplicationLiveCycleListener.class);
			for (String liveCycleListenerName : liveCycleListeners.keySet()) {
				IApplicationLiveCycleListener liveCycleListener = (IApplicationLiveCycleListener) context.getBean(liveCycleListenerName);
				try {
					liveCycleListener.onApplicationStart();
					logger.info("*** Start task: ".concat(liveCycleListenerName));
				} catch (Exception e) {
					logger.error("Error while starting [" + liveCycleListenerName + "]: " + e.getMessage());
					throw new RuntimeException(e);
				}
			}
		}

		// check, if rendering is possible
		if (!isImageRenderingEnabled())
			logger.warn("Requirements for image rendering aren't fulfilled! Images won't be recalc!");
	}

	/**
	 * Tear down.
	 */
	public static void end() {
		// save the current site
		if (siteHolder != null && siteHolder.getSite() != null) {
			Site site = siteHolder.getSite();
			try {
				SitePersister.write(site);
			} catch (IOException e) {
				logger.warn("Error while try to write site: " + site.getUrl(), e);
			}
		}

		// end all tasks
		if (enableTasksStart && !locker.isLocked()) {
			Map<String, IApplicationLiveCycleListener> liveCycleListeners = getBeansOfType(IApplicationLiveCycleListener.class);
			for (String liveCycleListenerName : liveCycleListeners.keySet()) {
				IApplicationLiveCycleListener liveCycleListener = (IApplicationLiveCycleListener) context.getBean(liveCycleListenerName);
				try {
					liveCycleListener.onApplicationEnd();
					logger.info("*** Shut down task: " + liveCycleListenerName);
				} catch (Exception e) {
					logger.error("Error while shut down [" + liveCycleListenerName + "]: " + e.getMessage(), e);
				}
			}

			// close lock socket
			locker.unlock();

			// delete temp directory
			try {
				FileUtils.deleteDirectory(Constants.TEMP_DIR);
			} catch (IOException e) {
				logger.error("Error while deleting poormans' temp dir: " + e.getMessage(), e);
			}
		}
		
		context.close();
	}

	/**
	 * @return A spring managed bean of the desired type.
	 */
	public static <T> T getBean(final Class<T> requiredType) {
		return context.getBean(requiredType);
	}

	/**
	 * @return All spring managed beans of the desired type.
	 */
	public static final <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
		return context.getBeansOfType(type, true, true);
	}

	/**
	 * @return True, if extension is an allowed image extension, or false if not or extension is null.
	 */
	public static boolean isImageExtention(final String extention) {
		if (StringUtils.isBlank(extention))
			return false;
		return InitializationManager.allowedImageExtensions.contains(extention.toLowerCase());
	}

	/**
	 * @return Allowed image extentions.
	 */
	public static final List<String> getAllowedImageExtensions() {
		return allowedImageExtensions;
	}

	/**
	 * @return Directory for backup of sites.
	 */
	public static final File getSitesBackupDir() {
		return sitesBackupDir;
	}

	/**
	 * Wrapper for {@link Properties#getProperty(String)}.
	 */
	public static String getProperty(final String key) {
		return pm.getProperty(key);
	}
	
	public static void loadSiteProperties(final Site site) {
		File configDir = PoPathInfo.getSiteConfigurationDirectory(site); 
		File propertiesFile = new File(configDir, "site.properties");
		
		if(!propertiesFile.exists()) {
			logger.debug(String.format("no properties found for [%s]", site.getUrl()));
			return;
		}
		try {
			pm.setSiteProperties(PropertiesTool.loadProperties(new BufferedInputStream(new FileInputStream(propertiesFile))));
			logger.info(String.format("Properties for [%s] successful loaded.", site.getUrl()));
		} catch (FileNotFoundException e) {
		}
	}

	public static Properties getVelocityProperties() {
		return pm.getVelocityProperties();
	}

	public static String getDefaultResourcesPath() {
		return pm.getProperty("pmcms.dir.defaultresources").concat(File.separator);
	}

	public static String getSourceEditorPath() {
		return pm.getProperty("pmcms.dir.sourceeditor").concat(File.separator);
	}

	/**
	 * @return The path of the error page.
	 */
	public static String getErrorPage() {
		return new File("defaults/error.html").getPath();
	}

	public static boolean isAdmin() {
		return isAdmin;
	}

	public static void setAdmin(boolean isAdmin) {
		InitializationManager.isAdmin = isAdmin;
	}

	public static void setHasToCleanup(boolean hasToCleanup) {
		InitializationManager.hasToCleanup = hasToCleanup;
	}

	public static File getDataDir() {
		return dataDir;
	}

	public static File getSitesDir() {
		return new File(getDataDir(), getProperty("pmcms.dir.sites"));
	}

	public static boolean isImageRenderingEnabled() {
		return imageRenderingEnabled;
	}
}
