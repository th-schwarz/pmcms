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
package de.thischwa.pmcms.gui.dialog;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import de.thischwa.pmcms.conf.InitializationManager;
import de.thischwa.pmcms.conf.PropertiesManager;
import de.thischwa.pmcms.gui.GuiPropertiesManager;
import de.thischwa.pmcms.gui.composite.LoggerComp;
import de.thischwa.pmcms.model.thread.LogGrabber;
import de.thischwa.pmcms.model.thread.ThreadController;
import de.thischwa.pmcms.tool.swt.SWTUtils;

/**
 * Views constantly the log results.
 *
 * @author Thilo Schwarz
 */
public class LoggerDialog extends SimpleDialog {

	public LoggerDialog() {
		this(null);
	}
	
	public LoggerDialog(Shell parentShell) {
		super(parentShell, SWT.SHELL_TRIM, GuiPropertiesManager.getLoggerSize());
	}

	@Override
	public void init() {
		LoggerComp loggerComp = new LoggerComp(shell, SWT.NONE);
		final Thread logGrabberThread = new LogGrabber(InitializationManager.getBean(PropertiesManager.class).getProperty("log4j.appender.FILE.file"), loggerComp);
		
		shell.setText("Logger");
		shell.setMinimumSize(300, 400);
		setLocation(GuiPropertiesManager.getLoggerLocation());
		
		shell.addListener(SWT.Move, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (isInitialized) 
					GuiPropertiesManager.setLoggerLocation(SWTUtils.convert(shell.getLocation()));
			}
		});
		shell.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (isInitialized) 
					GuiPropertiesManager.setLoggerSize(SWTUtils.convert(shell.getSize()));
			}
		});
		shell.addListener(SWT.Show, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (isInitialized) {
					ThreadController.getInstance().runThread(logGrabberThread);
				}
			}
		});
		shell.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (isInitialized) {
					ThreadController.getInstance().stopThread(logGrabberThread);
				}
			}
		});
	}
}
