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

import de.thischwa.pmcms.gui.GuiPropertiesManager;
import de.thischwa.pmcms.gui.composite.HeapSizeComp;
import de.thischwa.pmcms.tool.Utils;
import de.thischwa.pmcms.tool.swt.SWTUtils;

/**
 * Non-modal dialog to view the heap size of java.
 *
 * @version $Id: HeapSizeDialog.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class HeapSizeDialog extends SimpleDialog {
	private boolean isRunning = true;
	private HeapSizeComp heapSizeComp;
	
	public HeapSizeDialog() {
		this(null);
	}
	
	public HeapSizeDialog(Shell parentShell) {
		super(parentShell, SWT.DIALOG_TRIM);
		SWTUtils.asyncExec(new HeapSizeThread(), shell.getDisplay());
	}
	
	@Override
	protected void init() {
		shell.setText("Heap-Size-Viewer");
		heapSizeComp = new HeapSizeComp(shell, SWT.NONE);
		setLocation(GuiPropertiesManager.getHeapLocation());
		shell.pack();
		
		shell.addListener(SWT.Move, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (isInitialized) 
					GuiPropertiesManager.setHeapLocation(SWTUtils.convert(shell.getLocation()));
			}
		});
	}
	
	@Override
	protected void beforeDispose() {
		super.beforeDispose();
		isRunning = false;
		Utils.quietlyDelay(1000);
	}

	private class HeapSizeThread extends Thread {
		@Override
		public void run() {
			try {
				while (isRunning) {
					final long freeSizeBytes = Runtime.getRuntime().freeMemory();
					final long totalSizeBytes = Runtime.getRuntime().totalMemory();
					shell.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							heapSizeComp.setHeap(totalSizeBytes, freeSizeBytes);
						}
					});
					Utils.quietlyDelay(500);
				}
			} catch (Exception e) {
				interrupt();
			}
		}
	}

}
