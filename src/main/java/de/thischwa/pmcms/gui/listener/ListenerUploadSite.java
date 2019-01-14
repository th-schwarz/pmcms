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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import de.thischwa.pmcms.conf.InitializationManager;
import de.thischwa.pmcms.conf.PropertiesManager;
import de.thischwa.pmcms.conf.resource.LabelHolder;
import de.thischwa.pmcms.exception.ProgressException;
import de.thischwa.pmcms.gui.dialog.DialogManager;
import de.thischwa.pmcms.livecycle.SiteHolder;
import de.thischwa.pmcms.model.domain.PoInfo;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.model.tool.Upload;
import de.thischwa.pmcms.tool.DESCryptor;
import de.thischwa.pmcms.tool.connection.ConnectionAuthentificationException;
import de.thischwa.pmcms.tool.connection.ConnectionException;
import de.thischwa.pmcms.tool.connection.ConnectionFactory;
import de.thischwa.pmcms.tool.connection.IConnection;
import de.thischwa.pmcms.view.renderer.RenderData;

/**
 * Listener that triggers the transfer of a {@link Site}.
 *
 * @author Thilo Schwarz
 */
public class ListenerUploadSite implements SelectionListener {

	@Override
	public void widgetSelected(SelectionEvent event) {
		final Shell shell = event.display.getActiveShell();
		SiteHolder siteHolder = InitializationManager.getBean(SiteHolder.class);
		PropertiesManager pm = InitializationManager.getBean(PropertiesManager.class);
		Site site = siteHolder.getSite();
		if (!PoInfo.hasFileTranferInfo(site)) {
			MessageBox msg = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
			msg.setText(LabelHolder.get("popup.warning")); //$NON-NLS-1$
			msg.setMessage(LabelHolder.get("task.transfer.error.nologindata"));  //$NON-NLS-1$
			msg.open();
			return;
		}
		
		// export
		ListenerExportSite exportSelectionListener = new ListenerExportSite(false);
		exportSelectionListener.widgetSelected(event);
		if (exportSelectionListener.isFailed()) {
			MessageBox msg = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			msg.setText(LabelHolder.get("popup.error")); //$NON-NLS-1$
			msg.setMessage(LabelHolder.get("task.transfer.error.export")); //$NON-NLS-1$
			msg.open();
			return;
		} else if (exportSelectionListener.isInterruptByUser()) {
			MessageBox msg = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			msg.setText(LabelHolder.get("popup.error")); //$NON-NLS-1$
			msg.setMessage(LabelHolder.get("task.transfer.error.exportinterrupted")); //$NON-NLS-1$
			msg.open();
			return;
		}
		
		// transfer
		try {
			DESCryptor cryptor = new DESCryptor(pm.getProperty("pmcms.crypt.key"));
			String plainPwd = cryptor.decrypt(site.getTransferLoginPassword());
			IConnection transfer = ConnectionFactory.getFtp(site.getTransferHost(), site.getTransferLoginUser(), 
					plainPwd, site.getTransferStartDirectory());
			String checkumsFileBasename = pm.getProperty("pmcms.filename.checksums");
			DialogManager.startProgressDialog(shell, new Upload(site, transfer, checkumsFileBasename));
			MessageBox mb = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
			mb.setText(LabelHolder.get("popup.info")); //$NON-NLS-1$
			mb.setMessage(LabelHolder.get("task.transfer.ok"));  //$NON-NLS-1$
			mb.open();
		} catch (ConnectionException cone) {
			MessageBox msg = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			msg.setText(LabelHolder.get("popup.error")); //$NON-NLS-1$
			msg.setMessage("While trying to establish the transfer connection: " + cone.getMessage());
			msg.open();
			return;
		} catch (ProgressException e) {
			MessageBox msg = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			msg.setText(LabelHolder.get("popup.error")); //$NON-NLS-1$
			if (e.getCause() instanceof ConnectionAuthentificationException)
				msg.setMessage("Ftp login failed. \nPlease check your login properties!");
			else
				msg.setMessage("An unknown error was happend: " + e.getMessage() + "\n\n" + e.getStackTrace());
			msg.open();
			return;
		} catch (Exception e) {
			MessageBox msg = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			msg.setText(LabelHolder.get("popup.error")); //$NON-NLS-1$
			msg.setMessage("While transfering the files, the following error was happend: " + e.getMessage() + "\n" + e.getStackTrace());
			msg.open();
		}
		
		RenderData renderData = InitializationManager.getBean(RenderData.class);
		Collection<File> unusedImages = renderData.getFilesToCopy();
		if (CollectionUtils.isNotEmpty(unusedImages)) {
			DialogManager.startDialogUnusedImages(shell, site, unusedImages, InitializationManager.getAllowedImageExtensions());
		}
		renderData.clear();
	}
	
	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}
}
