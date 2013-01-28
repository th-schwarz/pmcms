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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.configuration.resource.LabelHolder;
import de.thischwa.pmcms.tool.swt.SWTUtils;


/**
 * The status bar.
 *
 * @version $Id: StatusBarComp.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class StatusBarComp extends Composite {

	public StatusBarComp(Composite parent, int style) {
		super(parent, style);
		GridLayout myLayout = new GridLayout();
		myLayout.numColumns = 2;
		myLayout.marginHeight = 1;
		myLayout.marginWidth = 1; 
		myLayout.marginBottom = 2;
		this.setLayout(myLayout);

		GridData myLayoutData = new GridData();
		myLayoutData.grabExcessHorizontalSpace = true;
		myLayoutData.horizontalAlignment = GridData.FILL;
		setLayoutData(myLayoutData);
		initComponents();
		pack();
	}

	private void initComponents() {
		if (InitializationManager.isAdmin()) {
			Composite comp = getCell(SWT.BORDER);
			Label lblAdmin = new Label(comp, SWT.NONE);
			lblAdmin.setText(LabelHolder.get("statusbar.adminmode"));
			SWTUtils.changeFontSizeRelativ(lblAdmin, -2);
		}
	}
	
	private Composite getCell(int style) {
		Composite comp = new Composite(this, style);
		GridLayout compLayout = new GridLayout();
		compLayout.verticalSpacing = 0;
		compLayout.marginWidth = 1;
		compLayout.marginHeight = 2;
		compLayout.horizontalSpacing = 2;
		comp.setLayoutData(new GridData());
		comp.setLayout(compLayout);		
		return comp;
	}
}
