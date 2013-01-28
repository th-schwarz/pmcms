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
package de.thischwa.pmcms.gui.dialog;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.gui.GuiPropertiesManager;
import de.thischwa.pmcms.gui.composite.LoggerComp;
import de.thischwa.pmcms.model.thread.LogGrabber;
import de.thischwa.pmcms.model.thread.ThreadController;
import de.thischwa.pmcms.tool.swt.SWTUtils;

/**
 * Views constantly the log results.
 *
 * @version $Id: LoggerDialog.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
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
		final Thread logGrabberThread = new LogGrabber(InitializationManager.getProperty("log4j.appender.FILE.file"), loggerComp);
		
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
