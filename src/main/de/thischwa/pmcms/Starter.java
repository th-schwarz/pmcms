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
package de.thischwa.pmcms;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import de.thischwa.pmcms.conf.BasicConfigurator;
import de.thischwa.pmcms.conf.InitializationManager;
import de.thischwa.pmcms.conf.resource.LabelHolder;
import de.thischwa.pmcms.gui.MainWindow;
import de.thischwa.pmcms.tool.CliParser;
import de.thischwa.pmcms.tool.OS.OSDetector;
import de.thischwa.pmcms.tool.launcher.Launcher;

/**
 * Basic starter class. It is called from {@link Launcher}. A possible lock will be checked.
 */
public class Starter {
	private static final Logger logger = Logger.getLogger(Starter.class);
	
	public static void main(String[] args) {
		BasicConfigurator configurator = null;
		CliParser cliParser = null;
		File dataDir = null;
		try {
			// analyze the command-line-params 
			cliParser = new CliParser(args);
			if(cliParser.hasOption("help")) {
				cliParser.printHelp();
				System.exit(0);
			}
			if(!cliParser.hasOption("portable") && !cliParser.hasOption("datadir")) {
				System.out.println("Parameter '-datadir' is missing!");
				cliParser.printHelp();
				System.exit(2);
			}
			if(cliParser.hasOption("portable")) 
				dataDir = Constants.APPLICATION_DIR;
			else {
				dataDir = (cliParser.hasOption("datadir")) 
						? new File(cliParser.getOptionValue("datadir")) : new File(Constants.HOME_DIR, Constants.NAME);
			}
			configurator = new BasicConfigurator(dataDir);
			InitializationManager.setAdmin(cliParser.hasOption("admin"));
			InitializationManager.setHasToCleanup(cliParser.hasOption("cleanup"));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println();
			if(cliParser != null) 
				cliParser.printHelp();
			System.exit(2);				
		}
		
		boolean isLocked = InitializationManager.lock();
		if (isLocked) {
			MessageBox mb = new MessageBox(new Shell(), SWT.ICON_WARNING | SWT.CANCEL);
			mb.setText(LabelHolder.get("popup.warning")); //$NON-NLS-1$
			mb.setMessage("Seems another instance is running!");
			mb.open();
			System.exit(1);
		}

		boolean isInit = false;
		try {
			InitializationManager.start(configurator);
			if (!InitializationManager.isImageRenderingEnabled()) {
				MessageBox mb = new MessageBox(new Shell(), SWT.ICON_WARNING | SWT.OK);
				mb.setText(LabelHolder.get("popup.warning")); //$NON-NLS-1$
				mb.setMessage("Requirements for image rendering aren't fulfilled! Images won't be recalc!");
				mb.open();
			}
			isInit = true;
			logger.debug("*** Try to init the main window ...");
			MainWindow mainWindow = (MainWindow) InitializationManager.getBean(MainWindow.class);
			mainWindow.run();
		} catch (Exception e) {
			if (!isInit) {
				System.out.println("Error while initialization: " + e.getMessage());
				e.printStackTrace();
			} else {
				if ((e instanceof SWTException) && (OSDetector.getType() == OSDetector.Type.MAC)) {
					logger.warn("Known SWT exception on OS X!", e);
				} else {
					logger.error("While configuration: " + e.getMessage(), e);
					MessageBox mb = new MessageBox(new Shell(), SWT.ICON_ERROR | SWT.CANCEL);
					mb.setText(LabelHolder.get("popup.error")); //$NON-NLS-1$
					mb.setMessage("Configuration has failed! Application will closed.\nCause: ".concat(e.getMessage()));
					e.printStackTrace();
					mb.open();
					System.exit(1);
				}
			}
		} finally {
			InitializationManager.end();
			// because of an osx swt issue we must exit here
			if(OSDetector.getType() == OSDetector.Type.MAC)
				System.exit(0);
		}
	}
}
