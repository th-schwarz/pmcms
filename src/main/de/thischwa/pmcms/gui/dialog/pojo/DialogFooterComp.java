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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * Composite is part of {@link DialogCreator} and contains the ok and cancel button. User's action will be reported to {@link DialogCreator}. 
 *
 * @version $Id: DialogFooterComp.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class DialogFooterComp extends Composite {
	private Button buttonOk;
	private Button buttonCancel;
	private DialogCreator dialogCreator;
	
	
	public DialogFooterComp(DialogCreator dialogCreator, int style) {
		super(dialogCreator, style);
		this.dialogCreator = dialogCreator;
		initialize();
	}
	
	private void initialize() {
		GridData gridDataCancel = new GridData();
		gridDataCancel.widthHint = 70;
		GridData gridDataOk = new GridData();
		gridDataOk.widthHint = 70;
		GridLayout gridLayoutMy = new GridLayout();
		gridLayoutMy.numColumns = 2;
		GridData gridDataMy = new org.eclipse.swt.layout.GridData();
		gridDataMy.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
		gridDataMy.grabExcessHorizontalSpace = true;
		gridDataMy.grabExcessVerticalSpace = true;
		gridDataMy.verticalAlignment = org.eclipse.swt.layout.GridData.END;
		this.setLayoutData(gridDataMy);
		this.setLayout(gridLayoutMy);
		buttonOk = new Button(this, SWT.NONE);
		buttonOk.setText("ok");
		buttonOk.setLayoutData(gridDataOk);
		buttonOk.addSelectionListener(new SelectionListener() { 
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (dialogCreator.getCompositeFields().isValid()) {
					getShell().dispose();
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		buttonCancel = new Button(this, SWT.NONE);
		buttonCancel.setText("cancel");
		buttonCancel.setLayoutData(gridDataCancel);
		buttonCancel.addSelectionListener(new SelectionListener() { 
			@Override
			public void widgetSelected(SelectionEvent e) {
				dialogCreator.setCancel(true);
				getShell().dispose();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {				
			}
		});
	}
}
