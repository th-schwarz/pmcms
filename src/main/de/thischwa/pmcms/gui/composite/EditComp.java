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
package de.thischwa.pmcms.gui.composite;



import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.gui.BrowserManager;
import de.thischwa.pmcms.gui.GuiPropertiesManager;
import de.thischwa.pmcms.gui.WorkspaceToolBarManager;
import de.thischwa.pmcms.gui.treeview.TreeViewManager;
import de.thischwa.pmcms.tool.XY;

/**
 * Composite 'edit', the main component.
 * 
 * @version $Id: EditComp.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class EditComp extends Composite {

	public EditComp(final Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		WorkspaceToolBarManager.init(this, SWT.NONE);
		createSashForm();
		
		// initialize action
		WorkspaceToolBarManager.fillComboSiteSelection();
	}

	/**
	 * This method initializes sashForm.
	 */
	private void createSashForm() {
		GridData gridData = new org.eclipse.swt.layout.GridData();
		gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		final SashForm sashForm = new SashForm(this, SWT.HORIZONTAL);
		sashForm.setLayoutData(gridData);
		createCompositeWorkingArea(sashForm);
		BrowserManager browserManager = InitializationManager.getBean(BrowserManager.class);
		browserManager.init(sashForm);
		browserManager.showHelp();
		sashForm.setWeights(GuiPropertiesManager.getWorkspaceSplitterWeight().toArray());
		
		sashForm.addListener(SWT.Paint, new Listener() {
			@Override
			public void handleEvent(Event event) {
				GuiPropertiesManager.setWorkspaceSplitterWeight(new XY(sashForm.getWeights()));
			}
		});
	}


	/**
	 * This method initializes composite.
	 */
	private void createCompositeWorkingArea(Composite parent) {
		GridLayout gridLayoutCompositeWorkspace = new GridLayout();
		gridLayoutCompositeWorkspace.horizontalSpacing = 0;
		gridLayoutCompositeWorkspace.marginWidth = 0;
		gridLayoutCompositeWorkspace.marginHeight = 0;
		gridLayoutCompositeWorkspace.verticalSpacing = 0;
		Composite compositeWorkingspace = new Composite(parent, SWT.NONE);
		compositeWorkingspace.setLayout(gridLayoutCompositeWorkspace);
		TreeViewManager treeViewManager = InitializationManager.getBean(TreeViewManager.class);
		treeViewManager.init(compositeWorkingspace, SWT.NONE);
	}
}
