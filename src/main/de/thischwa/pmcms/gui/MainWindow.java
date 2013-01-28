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

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.configuration.resource.ImageHolder;
import de.thischwa.pmcms.configuration.resource.LabelHolder;
import de.thischwa.pmcms.gui.composite.EditComp;
import de.thischwa.pmcms.gui.composite.StatusBarComp;
import de.thischwa.pmcms.gui.dialog.HeapSizeDialog;
import de.thischwa.pmcms.gui.dialog.ISimpleDialog;
import de.thischwa.pmcms.gui.dialog.InfoDialog;
import de.thischwa.pmcms.gui.dialog.LoggerDialog;
import de.thischwa.pmcms.model.thread.ThreadController;
import de.thischwa.pmcms.tool.swt.SWTUtils;

/**
 * Main application windows.
 * 
 * @version $Id: MainWindow.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
@Component
public class MainWindow extends ApplicationWindow {
	private static Logger logger = Logger.getLogger(MainWindow.class);
	private EditComp editComp;
	private boolean isShellInitialized = false;
	private Set<ISimpleDialog> dialogsToClose;
	private HeapSizeDialogAction heapSizeDialogAction = null;
	
	@Value("${pmcms.title}")
	private String defaultTitle;

	public MainWindow() {
		super(null);
		addMenuBar();
		dialogsToClose = new HashSet<ISimpleDialog>();
		logger.info("*** Application started!");
	}
	
	public void run() {
		setBlockOnOpen(true);
		open();
		if(Display.getCurrent() != null) // need it because of spring initialization
			Display.getCurrent().dispose();
	}

	public void setTitle(final String title) {		
		Shell shell = getShell();
		if (shell == null) // need it because of spring initialization
			return;
		if (StringUtils.isBlank(title))
			shell.setText(defaultTitle);
		else
			shell.setText(String.format("%s   -   [%s]", defaultTitle, title));
	}
	
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setImages(new Image[] {ImageHolder.SHELL_ICON_SMALL, ImageHolder.SHELL_ICON_BIG});
		
		final Shell tmpShell = shell;
		shell.addListener(SWT.Move, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (isShellInitialized)
					GuiPropertiesManager.setShellLocation(SWTUtils.convert(tmpShell.getLocation()));
			}
		});
		shell.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (isShellInitialized) 
					GuiPropertiesManager.setShellSize(SWTUtils.convert(tmpShell.getSize()));
			}
		});
	}

	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		GridLayout myLayout = new GridLayout();
		myLayout.numColumns = 1;
		myLayout.marginWidth = 0;
		myLayout.marginHeight = 0;
		getShell().setLayout(myLayout);
		
		Point shellSize = GuiPropertiesManager.getShellSize();
		getShell().setSize(shellSize.x, shellSize.y);
		Point shellLocation = GuiPropertiesManager.getShellLocation();
		if (shellLocation != null)
			getShell().setLocation(shellLocation.x, shellLocation.y);
		
		getShell().setMinimumSize(800, 600);
		isShellInitialized = true;
	}

	@Override
	public boolean close() {		
		// save gui settings
		GuiPropertiesManager.store();
 
		// end all threads (must place here, because of threads which access swt components !!!)
		ThreadController.getInstance().stopAllThreads();
		
		// close all open non-modal dialogs
		for(ISimpleDialog dlg : dialogsToClose)
			dlg.close();
		
		// dispose swt resources
		SWTUtils.disposeResources();
		
		logger.debug("*** Application is closing!");

		return super.close();
	}

	@Override
	protected Control createContents(Composite parent) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		editComp = new EditComp(parent, SWT.NONE);
		editComp.setLayout(gridLayout);
		
		GridData gridData = new org.eclipse.swt.layout.GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		editComp.setLayoutData(gridData);
		
		new StatusBarComp(parent, SWT.NONE);

		parent.pack();
		parent.layout();
		
		return parent;
	}

	@Override
	protected MenuManager createMenuManager() {  
		MenuManager menuManager = new MenuManager();
		
		MenuManager fileMenu = new MenuManager(LabelHolder.get("mainmenu.file"));
		fileMenu.add(new QuitAction());
		
		MenuManager helpMenu = new MenuManager("&?");
		helpMenu.add(new InfoDialogAction());
		helpMenu.add(new LoggerDialogAction());
		if(InitializationManager.isAdmin()) {
			heapSizeDialogAction = new HeapSizeDialogAction();
			helpMenu.add(heapSizeDialogAction);
		}
		
		menuManager.add(fileMenu);
		menuManager.add(helpMenu); 
		return menuManager;
	}

	private class InfoDialogAction extends Action {
		public InfoDialogAction() {
			setText("&Info");
		}		
		@Override
		public void run() {
			ISimpleDialog id = new InfoDialog(getShell());
			id.run();
		}
	}
	
	private class LoggerDialogAction extends Action {		
		public LoggerDialogAction() {
			setText("&Logger");
		}		
		@Override
		public void run() {
			ISimpleDialog dialog = new LoggerDialog();
			dialogsToClose.add(dialog);
			dialog.run();
			dialogsToClose.remove(dialog);
		}
	}
	
	private class HeapSizeDialogAction extends Action {
		public HeapSizeDialogAction() {
			setText("&Heap size viewer");
		}
		@Override
		public void run() {
			ISimpleDialog dialog = new HeapSizeDialog();
			heapSizeDialogAction.setEnabled(false);
			dialogsToClose.add(dialog);
			dialog.run();
			dialogsToClose.remove(dialog);
			heapSizeDialogAction.setEnabled(true);
		}
	}
	
	private class QuitAction extends Action {
		public QuitAction() {
			setText(LabelHolder.get("mainmenu.file.quit"));
		}
		@Override
		public void run() {
			close();
		}
	}
}
