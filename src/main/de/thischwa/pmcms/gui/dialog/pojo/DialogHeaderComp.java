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
package de.thischwa.pmcms.gui.dialog.pojo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

/**
 * Composite is part of {@link DialogCreator} and shows detailed information about the dialog.
 *
 * @version $Id: DialogHeaderComp.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class DialogHeaderComp extends Composite {
	private Label labelTitle;
	private Label labelDescription;
	
	public DialogHeaderComp(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		GridLayout gridLayoutMe = new GridLayout();
		gridLayoutMe.marginWidth = 10;
		gridLayoutMe.horizontalSpacing = 0;
		gridLayoutMe.verticalSpacing = 10;
		gridLayoutMe.marginHeight = 10;
		GridData gridDataTitle = new org.eclipse.swt.layout.GridData();
		gridDataTitle.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridDataTitle.grabExcessHorizontalSpace = true;
		gridDataTitle.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		GridData gridDataLabelHint = new org.eclipse.swt.layout.GridData();
		gridDataLabelHint.grabExcessHorizontalSpace = true;
		gridDataLabelHint.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridDataLabelHint.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		GridData gridDataMe = new org.eclipse.swt.layout.GridData();
		gridDataMe.grabExcessHorizontalSpace = true;
		gridDataMe.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridDataMe.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		this.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		this.setLayout(gridLayoutMe);
		this.setLayoutData(gridDataMe);
		labelTitle = new Label(this, SWT.NONE);
		labelTitle.setText("Label");
		labelTitle.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		labelTitle.setFont(new Font(Display.getDefault(), "DejaVu Sans", 16, SWT.NORMAL));
		labelTitle.setLayoutData(gridDataTitle);
		labelDescription = new Label(this, SWT.WRAP);
		labelDescription.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		labelDescription.setLayoutData(gridDataLabelHint);
		labelDescription.pack(true);
	}
	
	public void setTitle(String title) {
		labelTitle.setText(title);
	}
	
	public void setDescription(String description) {
		labelDescription.setText(description);
	}
}
