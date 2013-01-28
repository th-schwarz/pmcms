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
package de.thischwa.pmcms.gui;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;


import org.apache.log4j.Logger;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.tool.XY;

/**
 * Properties manager for managing the gui settings.
 *
 * @version $Id: GuiPropertiesManager.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class GuiPropertiesManager {
	private static Logger logger = Logger.getLogger(GuiPropertiesManager.class);
	private static Properties properties = new Properties();
	private static final String keyShellSize = "shell.size";
	private static final String keyShellLocation = "shell.location";
	private static final String keyLoggerSize = "logger.size";
	private static final String keyLoggerLocation = "logger.location";
	private static final String keyWorkspaceSplitterWeights = "workspace.splitterweights";
	private static final String keyHeapSizeViewerLocation = "heap.location";
	private static File settingsFile = null;

	static {
		settingsFile = new File(InitializationManager.getDataDir(), ".settings.properties");
		try {
			if (settingsFile.exists()) {
				properties.load(new BufferedInputStream(new FileInputStream(settingsFile)));
				logger.info("GUI settings loaded successful from: " + settingsFile.getAbsolutePath());
			} else {
				properties.setProperty(keyShellSize, Constants.DEFAULT_SHELL_SIZE.toString());
				properties.setProperty(keyLoggerSize, Constants.DEFAULT_LOGGER_SIZE.toString());
				properties.setProperty(keyWorkspaceSplitterWeights, Constants.DEFAULT_WORKSPACE_SPLITTER_WEIGHT.toString());
				properties.setProperty(keyHeapSizeViewerLocation, Constants.DEFAULT_HEAP_LOCATION.toString());
			}
		} catch (Exception e) {
			throw new RuntimeException("Error while reading setting: " + e.getMessage(), e);
		}
	}
	
	public static XY getShellSize() {
		return new XY(properties.getProperty(keyShellSize));
	}
	
	public static void setShellSize(final XY xy) {
		properties.setProperty(keyShellSize, xy.toString());
	}
	
	public static XY getShellLocation() {
		return (properties.containsKey(keyShellLocation)) ? new XY(properties.getProperty(keyShellLocation)) : null;
	}
	
	public static void setShellLocation(final XY xy) {
		properties.setProperty(keyShellLocation, xy.toString());
	}
	
	public static XY getWorkspaceSplitterWeight() {
		return new XY(properties.getProperty(keyWorkspaceSplitterWeights));
	}
	
	public static void setWorkspaceSplitterWeight(final XY xy) {
		properties.setProperty(keyWorkspaceSplitterWeights, xy.toString());
	}

	public static XY getLoggerSize() {
		return new XY(properties.getProperty(keyLoggerSize));
	}
	
	public static void setLoggerSize(final XY xy) {
		properties.setProperty(keyLoggerSize, xy.toString());
	}
	
	public static XY getLoggerLocation() {
		return (properties.containsKey(keyLoggerLocation)) ? new XY(properties.getProperty(keyLoggerLocation)) : null;
	}
	
	public static void setLoggerLocation(final XY xy) {
		properties.setProperty(keyLoggerLocation, xy.toString());
	}
	
	public static XY getHeapLocation() {
		return (properties.containsKey(keyHeapSizeViewerLocation)) ? new XY(properties.getProperty(keyHeapSizeViewerLocation)) : null;
	}
	
	public static void setHeapLocation(final XY xy) {
		properties.setProperty(keyHeapSizeViewerLocation, xy.toString());
	}
	
	public static void store() {
		if(settingsFile == null)
			return;
		try {
			properties.store(new BufferedOutputStream(new FileOutputStream(settingsFile)), 
					"Settings for the GUI. NEVER TOUCH THIS FILE!");
			logger.info("GUI settings stored successful in: " + settingsFile.getAbsolutePath());
		} catch (Exception e) {
			logger.error("Error while storing gui settings: " + e.getMessage(), e);
		}
	}
}
