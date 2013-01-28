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
import org.eclipse.swt.widgets.ProgressBar;


/**
 * Component to view the java heap size.
 * 
 * @version $Id: HeapSizeComp.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class HeapSizeComp extends Composite {

	private Label lblTotal;
	private Label lblUsed;
	private ProgressBar progressBar;

	public HeapSizeComp(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		GridLayout layout = new GridLayout();
		this.setLayout(layout);

		Composite compositeMain = new Composite(this, SWT.NONE);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		compositeMain.setLayoutData(gridData);
		GridLayout compositeMainLayout = new GridLayout();
		compositeMainLayout.marginWidth = 0;
		compositeMain.setLayout(compositeMainLayout);
		compositeMainLayout.numColumns = 2;
		
		Label lblTotalLbl = new Label(compositeMain, SWT.NONE);
		lblTotalLbl.setText("Total: ");
		lblTotal = new Label(compositeMain, SWT.NONE);
		GridData gd_lblTotal = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gd_lblTotal.grabExcessHorizontalSpace = true;
		gd_lblTotal.horizontalAlignment = SWT.FILL;
		lblTotal.setLayoutData(gd_lblTotal);
		lblTotal.setText("0 MB");
		lblTotal.setAlignment(SWT.RIGHT);

		Label lblUsedLbl = new Label(compositeMain, SWT.NONE);
		lblUsedLbl.setText("Used: ");
		lblUsed = new Label(compositeMain, SWT.NONE);
		GridData gd_lblUsed = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gd_lblUsed.grabExcessHorizontalSpace = true;
		gd_lblUsed.horizontalAlignment = SWT.FILL;
		lblUsed.setLayoutData(gd_lblUsed);
		lblUsed.setText("0 MB");
		lblUsed.setAlignment(SWT.RIGHT);

		GridData progressBarLData = new GridData();
		progressBarLData.horizontalAlignment = SWT.FILL;
		progressBarLData.grabExcessHorizontalSpace = true;
		progressBar = new ProgressBar(this, SWT.NONE);
		progressBar.setLayoutData(progressBarLData);
	}
	
	public void setHeap(long totalBytes, long freeBytes) {
		String format = "%8d MB";
		long totalSizeMB = totalBytes/1024l/1024l;
		long freeSizeMB = freeBytes/1024l/1024l;
		long usedSizeMB = totalSizeMB - freeSizeMB;
		
		lblTotal.setText(String.format(format, totalSizeMB));
		lblUsed.setText(String.format(format, usedSizeMB));
		progressBar.setMaximum((int) totalSizeMB);
		progressBar.setSelection((int) usedSizeMB);
	}
}
