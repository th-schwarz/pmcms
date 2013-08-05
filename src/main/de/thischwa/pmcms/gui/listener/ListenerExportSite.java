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
 *  - GNU Lesser General Public License Version 2.1 or later (the "LGPL")
 *    http://www.gnu.org/licenses/lgpl.html
 * 
 *  - Mozilla Public License Version 1.1 or later (the "MPL")
 *    http://www.mozilla.org/MPL/MPL-1.1.html
 * 
 * == END LICENSE ==
 ******************************************************************************/
package de.thischwa.pmcms.gui.listener;

import java.io.File;
import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import de.thischwa.pmcms.conf.InitializationManager;
import de.thischwa.pmcms.conf.resource.LabelHolder;
import de.thischwa.pmcms.gui.dialog.DialogManager;
import de.thischwa.pmcms.livecycle.SiteHolder;
import de.thischwa.pmcms.model.domain.PoPathInfo;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.view.renderer.ExportRenderer;
import de.thischwa.pmcms.view.renderer.RenderData;

/**
 * Listener for exporting a {@link Site}.
 * 
 * @author Thilo Schwarz
 */
public class ListenerExportSite implements SelectionListener {
	private static Logger logger = Logger.getLogger(ListenerExportSite.class);
	private boolean failed = true;
	
	/** If true, export infos are displayed to the user. This is needed because this listener can be called from other listeners. */
	private boolean showExportInfo;
	private boolean isInterruptByUser = false;
	
	
	public ListenerExportSite() {
		this(false);
	}
	
	public ListenerExportSite(boolean showExportInfo) {
		this.showExportInfo = showExportInfo;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		RenderData renderData = InitializationManager.getBean(RenderData.class);
		SiteHolder siteHolder = InitializationManager.getBean(SiteHolder.class);
		ExportRenderer exportRenderer = InitializationManager.getBean(ExportRenderer.class); 
		final Shell shell = e.display.getActiveShell();
		StringBuilder messages = new StringBuilder();
		Site site = siteHolder.getSite();
		exportRenderer.setSite(site);
		exportRenderer.setMessages(messages);
		exportRenderer.setDisplay(e.display);
		exportRenderer.init();
		if (!exportRenderer.isValidToExport()) {
			MessageBox mbNotValid = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			mbNotValid.setText(LabelHolder.get("popup.error")); //$NON-NLS-1$
			mbNotValid.setMessage(LabelHolder.get("task.export.error.notvalid") + "\n\n" + messages.toString()); //$NON-NLS-1$
			mbNotValid.open();
			return;
		}
		try {
			StopWatch watch = new StopWatch();
			watch.start();
			DialogManager.startProgressDialog(shell, exportRenderer);
			watch.stop();
			isInterruptByUser = exportRenderer.isInterruptByUser();
			if (!isInterruptByUser)
				logger.info("Time to export: " + watch);
			if (showExportInfo) {
				if (isInterruptByUser) {
					MessageBox mbInterrupted = new MessageBox(shell);
					mbInterrupted.setText(LabelHolder.get("popup.info")); //$NON-NLS-1$
					mbInterrupted.setMessage(LabelHolder.get("task.export.interrupted"));  //$NON-NLS-1$
					mbInterrupted.open();
				} else {
					MessageBox mbExported = new MessageBox(shell);
					mbExported.setText(LabelHolder.get("popup.info")); //$NON-NLS-1$
					mbExported.setMessage(LabelHolder.get("task.export.ok") + PoPathInfo.getSiteExportDirectory(site).getAbsolutePath()); //$NON-NLS-1$
					mbExported.open();
				}
			}
			failed = false;
			if (!isInterruptByUser) {
				Collection<File> usedFiles = renderData.getFilesToCopy();
				if (CollectionUtils.isNotEmpty(usedFiles)) {
					DialogManager.startDialogUnusedImages(e.display.getActiveShell(), site, usedFiles);
				}
			}
			renderData.clear();
		} catch (Exception ex) {
			logger.error("While eport: " + ex.getMessage(), ex);
			ex.printStackTrace();
			messages = new StringBuilder();
			messages.append(LabelHolder.get("task.export.error.exception") + "\n\n");
			messages.append(ex.toString());
			MessageBox mbNotValid = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			mbNotValid.setText(LabelHolder.get("popup.warning")); //$NON-NLS-1$
			mbNotValid.setMessage(messages.toString());
			mbNotValid.open();
		}
	}

	/**
	 * @return True, if there was an error while the listener was running, otherwise false.
	 */
	public boolean isFailed() {
		return failed;
	}

	/**
	 * @return True, if export was interrupted by the user, otherwise false.
	 */
	public boolean isInterruptByUser() {
		return isInterruptByUser;
	}
}
