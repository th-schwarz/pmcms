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
package de.thischwa.pmcms.gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.configuration.resource.LabelHolder;
import de.thischwa.pmcms.exception.FatalException;
import de.thischwa.pmcms.gui.listener.ListenerAddSite;
import de.thischwa.pmcms.gui.listener.ListenerBackupSite;
import de.thischwa.pmcms.gui.listener.ListenerExportSite;
import de.thischwa.pmcms.gui.listener.ListenerImportSite;
import de.thischwa.pmcms.gui.listener.ListenerUploadSite;
import de.thischwa.pmcms.gui.treeview.TreeViewManager;
import de.thischwa.pmcms.livecycle.SiteHolder;
import de.thischwa.pmcms.model.IRenderable;
import de.thischwa.pmcms.model.domain.PoInfo;
import de.thischwa.pmcms.model.domain.PoStructurTools;
import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.model.tool.SitePersister;
import de.thischwa.pmcms.tool.swt.SWTUtils;
import de.thischwa.pmcms.view.ViewMode;

/**
 * Manager of the main toolbar.
 * 
 * @version $Id: WorkspaceToolBarManager.java 2208 2012-06-17 10:08:47Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class WorkspaceToolBarManager {
	private static Logger logger = Logger.getLogger(WorkspaceToolBarManager.class);
	private static WorkspaceToolBarManager myInstance = null;
	private static Composite compositeHeader = null;
	private Button buttonAddSite = null;
	private Button buttonImportSite = null;
	private static Label labelSiteTitle = null;
	private static Combo comboSiteSelection = null;
	private static ToolBar toolBarSite = null;
	
	private static Site oldSite = null;

	private WorkspaceToolBarManager(final Composite parent, final int style) {
		compositeHeader = new Composite(parent, style);
		initialize();
	}

	public static void init(final Composite parent, final int style) {
		if (myInstance == null)
			myInstance = new WorkspaceToolBarManager(parent, style);
	}

	private void initialize() {
		GridLayout gridLayoutHeader = new GridLayout();
		gridLayoutHeader.numColumns = 5;
		gridLayoutHeader.makeColumnsEqualWidth = false;
		GridData gridDataCompositeHeader = new GridData();
		gridDataCompositeHeader.horizontalAlignment = GridData.FILL;
		gridDataCompositeHeader.verticalAlignment = GridData.CENTER;
		if (InitializationManager.isAdmin()) {
			buttonAddSite = new Button(compositeHeader, SWT.NONE);
			buttonAddSite.setText(LabelHolder.get("toolbar.addsite.text")); //$NON-NLS-1$
			buttonAddSite.setToolTipText(LabelHolder.get("toolbar.addsite.hint")); //$NON-NLS-1$
			buttonAddSite.addSelectionListener(new ListenerAddSite());
		}
		buttonImportSite = new Button(compositeHeader, SWT.NONE);
		buttonImportSite.setText(LabelHolder.get("toolbar.importsite.text")); //$NON-NLS-1$
		buttonImportSite.setToolTipText(LabelHolder.get("toolbar.importsite.hint")); //$NON-NLS-1$
		buttonImportSite.addSelectionListener(new ListenerImportSite());

		GridData gridDataCompositeSiteSelection = new GridData();
		gridDataCompositeSiteSelection.widthHint = 175;
		gridDataCompositeSiteSelection.verticalAlignment = GridData.CENTER;
		gridDataCompositeSiteSelection.horizontalAlignment = GridData.FILL;
		Composite compositeSiteSelection = new Composite(compositeHeader, SWT.NONE);
		compositeSiteSelection.setLayout(new FillLayout());
		compositeSiteSelection.setLayoutData(gridDataCompositeSiteSelection);
		createCombo(compositeSiteSelection);

		GridData gridDataCompsiteLabelSiteTitle = new GridData();
		gridDataCompsiteLabelSiteTitle.horizontalAlignment = GridData.FILL;
		gridDataCompsiteLabelSiteTitle.grabExcessHorizontalSpace = true;
		gridDataCompsiteLabelSiteTitle.verticalAlignment = GridData.CENTER;
		Composite compositeLabelSiteTitle = new Composite(compositeHeader, SWT.NONE);
		compositeLabelSiteTitle.setLayout(new FillLayout());
		compositeLabelSiteTitle.setLayoutData(gridDataCompsiteLabelSiteTitle);
		labelSiteTitle = new Label(compositeLabelSiteTitle, SWT.NONE);

		createToolBarSite();

		compositeHeader.setLayout(gridLayoutHeader);
		compositeHeader.setLayoutData(gridDataCompositeHeader);
	}

	/**
	 * This method initializes comboSiteSelection.
	 */
	private void createCombo(Composite parent) {
		comboSiteSelection = new Combo(parent, SWT.READ_ONLY);
		comboSiteSelection.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent event) {
				logger.debug("Entered widgetSelect (site select)."); //$NON-NLS-1$
				SiteHolder siteHolder = InitializationManager.getBean(SiteHolder.class);
				Site tempSite = null;
				for (String siteUrl : PoStructurTools.getAllSites()) {
					if (comboSiteSelection.getText().equals(siteUrl)) {
						if(oldSite != null) {
							// we want to save the current site before loading another one
							try {
								SitePersister.write(oldSite);
							} catch (IOException e) {
								throw new FatalException(e);
							}
						}
						tempSite = SitePersister.read(siteUrl);
						oldSite = tempSite;
					}
				}
				BrowserManager browserManager = InitializationManager.getBean(BrowserManager.class);
				if (tempSite == null) {
					browserManager.showHelp();
					return;
				}

				logger.debug("Selected site: " + tempSite); //$NON-NLS-1$
				siteHolder.setSite(tempSite);
				TreeViewManager treeViewManager = InitializationManager.getBean(TreeViewManager.class);
				IRenderable firstRenderable = PoInfo.getFirstRenderable(tempSite);
				if (firstRenderable != null) {
					treeViewManager.fillAndExpands((APoormansObject<?>) firstRenderable);
					browserManager.view((APoormansObject<?>) firstRenderable, ViewMode.PREVIEW);
				} else
					treeViewManager.fillAndExpands(tempSite);
				actionAfterSiteRenamed(tempSite);
			}

			@Override
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});
		comboSiteSelection.pack();
	}

	/**
	 * This method initializes toolBarSite.
	 */
	private void createToolBarSite() {
		Image imgBackup = SWTUtils.getImage("toolbar_backup");
		Image imgExport = SWTUtils.getImage("toolbar_export");
		Image imgTransfer = SWTUtils.getImage("toolbar_transfer");

		ToolItem seperator;
		GridData gridDataToolBarSite = new GridData();
		gridDataToolBarSite.horizontalAlignment = GridData.END;
		gridDataToolBarSite.grabExcessHorizontalSpace = false;
		gridDataToolBarSite.verticalAlignment = GridData.CENTER;
		toolBarSite = new ToolBar(compositeHeader, SWT.BORDER);
		toolBarSite.setLayoutData(gridDataToolBarSite);
		ToolItem toolItemSiteTransfer = new ToolItem(toolBarSite, SWT.PUSH);
		toolItemSiteTransfer.setImage(imgTransfer);
		toolItemSiteTransfer.setToolTipText(LabelHolder.get("toolbar.transfersite.hint")); //$NON-NLS-1$
		toolItemSiteTransfer.addSelectionListener(new ListenerUploadSite());
		seperator = new ToolItem(toolBarSite, SWT.SEPARATOR);
		seperator.setWidth(10);
		ToolItem toolItemBackupSite = new ToolItem(toolBarSite, SWT.PUSH);
		toolItemBackupSite.setImage(imgBackup);
		toolItemBackupSite.setToolTipText(LabelHolder.get("toolbar.backupsite.hint")); //$NON-NLS-1$
		toolItemBackupSite.addSelectionListener(new ListenerBackupSite());
		seperator = new ToolItem(toolBarSite, SWT.SEPARATOR);
		seperator.setWidth(10);
		ToolItem toolItemSiteExport = new ToolItem(toolBarSite, SWT.PUSH);
		toolItemSiteExport.setImage(imgExport);
		toolItemSiteExport.setToolTipText(LabelHolder.get("toolbar.exportsite.hint")); //$NON-NLS-1$
		toolItemSiteExport.addSelectionListener(new ListenerExportSite(true));
		toolBarSite.setEnabled(false);
	}

	public static void fillComboSiteSelection() {
		logger.debug("Entered fillComboSiteSelection."); //$NON-NLS-1$
		if (comboSiteSelection != null) {
			comboSiteSelection.removeAll();
			Map<String, Integer> index = new HashMap<String, Integer>();
			int counter = 0;
			for (String siteUrl : PoStructurTools.getAllSites()) {
				index.put(siteUrl, counter);
				counter++;
				comboSiteSelection.add(siteUrl);
			}
			comboSiteSelection.setData(index);
			labelSiteTitle.setText(""); //$NON-NLS-1$
		}
	}

	public static void actionAfterSiteRenamed(final Site site) {
		if (site != null) {
			try {
				SitePersister.write(site);
			} catch (IOException e) {
				throw new FatalException(e);
			}
		}
		fillComboSiteSelection();
		if(site != null) {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			Map<String, Integer> index = (Map) comboSiteSelection.getData();
			comboSiteSelection.select(index.get(site.getUrl()));
			labelSiteTitle.setText(site.getDecorationString());
			toolBarSite.setEnabled(true);
		}
	}
	
	public static void deleteOldSite() {
		oldSite = null;
	}
}
