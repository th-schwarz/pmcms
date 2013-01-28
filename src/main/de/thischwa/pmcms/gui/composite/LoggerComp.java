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
import org.eclipse.swt.widgets.Text;

import de.thischwa.pmcms.gui.ILogAppender;
import de.thischwa.pmcms.tool.swt.SWTUtils;

/**
 * It contains the 'viewer' for logging.
 * 
 * @version $Id: LoggerComp.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class LoggerComp extends Composite implements ILogAppender {
	private Text textLog = null;

	public LoggerComp(final Composite parent, int style) {
		super(parent, style); 
		initialize(parent);
	}

	/*
	 * (non-Javadoc)
	 * @see de.thischwa.pmcms.gui.ILogAppender#appendToLog(java.lang.String)
	 */
	@Override
	public void appendToLog(String text) {
		if (!textLog.isDisposed())
			textLog.append(text);
	}

	private void initialize(final Composite parent) {
		GridData gridData = new org.eclipse.swt.layout.GridData();
		gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		textLog = new Text(this, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
		textLog.setEditable(false);
		textLog.setLayoutData(gridData);
		SWTUtils.changeFontSize(textLog, 10);

		setLayoutData(gridData);
		setLayout(new GridLayout());
	}
}
