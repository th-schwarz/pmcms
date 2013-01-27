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
package de.thischwa.pmcms.gui.listener;

import java.io.File;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.configuration.resource.LabelHolder;
import de.thischwa.pmcms.gui.BrowserManager;
import de.thischwa.pmcms.gui.WorkspaceToolBarManager;
import de.thischwa.pmcms.gui.dialog.DialogManager;
import de.thischwa.pmcms.gui.treeview.TreeViewManager;
import de.thischwa.pmcms.livecycle.SiteHolder;
import de.thischwa.pmcms.model.IRenderable;
import de.thischwa.pmcms.model.domain.PoInfo;
import de.thischwa.pmcms.model.domain.PoStructurTools;
import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.model.tool.ImportBackup;
import de.thischwa.pmcms.tool.DESCryptor;
import de.thischwa.pmcms.view.ViewMode;

/**
 * Listener for importing a {@link Site}.
 * 
 * @version $Id: ListenerImportSite.java 2216 2012-07-14 15:48:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class ListenerImportSite implements SelectionListener {
	private static Logger logger = Logger.getLogger(ListenerImportSite.class);

	
	@Override
	public void widgetSelected(SelectionEvent event) {
		final Shell shell = event.display.getActiveShell();
		FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
		fileDialog.setText(LabelHolder.get("task.import.openfile")); //$NON-NLS-1$
		fileDialog.setFilterPath(new File(InitializationManager.getProperty("poormans.dir.backup")).getAbsolutePath());
		fileDialog.setFilterExtensions(new String[]{ "*.".concat(Constants.BACKUP_EXTENSION).concat(";*.zip"),
				"*.".concat(Constants.BACKUP_EXTENSION), 
				"*.zip" });

		String zipFilePath = fileDialog.open();
		if (StringUtils.isNotEmpty(zipFilePath)) {
			try {
				File zipFile = new File(zipFilePath);
				if(PoStructurTools.siteExists(zipFile)) {
					MessageBox mb = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
					mb.setText(LabelHolder.get("popup.info")); //$NON-NLS-1$
					mb.setMessage(LabelHolder.get("task.import.error.exists")); //$NON-NLS-1$
					mb.open();
					return;
				}
				
				// import
				ImportBackup importer = new ImportBackup(zipFile);
				DialogManager.startProgressDialog(shell, importer);
				Site site = importer.getSite();
				SiteHolder siteHolder = InitializationManager.getBean(SiteHolder.class);
				siteHolder.setSite(site);
				TreeViewManager treeViewManager = InitializationManager.getBean(TreeViewManager.class);
				IRenderable firstRenderable = PoInfo.getFirstRenderable(site); 
				BrowserManager browserManager = InitializationManager.getBean(BrowserManager.class);
				if (firstRenderable != null) {
					treeViewManager.fillAndExpands((APoormansObject<?>) firstRenderable);
					browserManager.view((APoormansObject<?>) firstRenderable, ViewMode.PREVIEW);
				} else {
					treeViewManager.fillAndExpands(site);
					browserManager.showHelp();
				}
				
				// check if login password is encrypted
				try {
					DESCryptor cryptor = InitializationManager.getBean(DESCryptor.class);
					cryptor.decrypt(site.getTransferLoginPassword());
				} catch (DESCryptor.CryptorException e) {
					logger.warn("A non encrypted password found, delete it!");
					site.setTransferLoginPassword(null);
					MessageBox mb = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
					mb.setText(LabelHolder.get("popup.warning")); //$NON-NLS-1$
					mb.setMessage(LabelHolder.get("task.import.error.nocryptedpwd")); //$NON-NLS-1$
					mb.open();
				}
				
				WorkspaceToolBarManager.actionAfterSiteRenamed(site);
			} catch (Exception e) {
				logger.error("While importing a site: " + e.getMessage(), e);
				MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
				mb.setText(LabelHolder.get("popup.error")); //$NON-NLS-1$
				mb.setMessage(LabelHolder.get("task.import.error") + "\n" + e.getCause().getMessage()); //$NON-NLS-1$
				mb.open();
			}
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}

}
