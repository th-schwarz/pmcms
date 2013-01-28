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
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.thischwa.pmcms.configuration.resource.ImageHolder;
import de.thischwa.pmcms.tool.XY;


/**
 * Simple dialog with a content area and an optional button bar with a close button. The bar will be created, 
 * if 'labelOfCloseButton' is set.
 *
 * @version $Id: SimpleDialog.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public abstract class SimpleDialog implements ISimpleDialog {	
	protected Shell shell;
	protected Shell parentShell;
	protected String labelOfCloseButton = null;
	protected boolean isInitialized = false;


	protected SimpleDialog(Shell parentShell, int style) {
		this(parentShell, style, null);
	}

	protected SimpleDialog(Shell parentShell, int style, XY initialSize) {
		this.parentShell = parentShell;
		if (parentShell == null)
			shell = new Shell(style);
		else
			shell = new Shell(parentShell, style);
		shell.setImages(new Image[] {ImageHolder.SHELL_ICON_SMALL, ImageHolder.SHELL_ICON_BIG});
		if (initialSize != null)
			setSize(initialSize);
		GridLayout myLayout = new GridLayout();
		myLayout.numColumns = 1;
		myLayout.marginWidth = 0;
		myLayout.marginHeight = 0;
		shell.setLayout(myLayout);
		shell.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				beforeDispose();
			}
		});
		
		init();
		isInitialized = true;
	}
	
	protected void beforeDispose() {}
	
	/* (non-Javadoc)
	 * @see de.thischwa.pmcms.gui.ISimpleDialog#close()
	 */
	@Override
	public void close() {
		shell.close();
	}
	
	protected void setLocation(XY location) {
		if (location != null)
			shell.setLocation(location.x, location.y);
	}
	
	protected void setSize(XY size) {
		if (size != null)
			shell.setSize(size.x, size.y);
	}
	
	protected abstract void init();
	
	protected void addCloseButton(final String label) {
		GridLayout myLayout = new GridLayout();
		myLayout.numColumns = 1;
		myLayout.marginWidth = 0;
		myLayout.marginHeight = 5;
		myLayout.marginRight = 10;
		Composite buttonBar = new Composite(shell, SWT.NONE);
		buttonBar.setLayout(myLayout);
		buttonBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_END));
		
		Button button = new Button(buttonBar, SWT.NONE);
		button.setText(label);
		
		button.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		
		shell.pack();
	}
	
	/* (non-Javadoc)
	 * @see de.thischwa.pmcms.gui.ISimpleDialog#run()
	 */
	@Override
	public void run() {
		if (labelOfCloseButton != null)
			addCloseButton(labelOfCloseButton);
		shell.open();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		shell.dispose();		
	}
	
}
