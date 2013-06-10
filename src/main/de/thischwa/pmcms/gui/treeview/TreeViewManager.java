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
package de.thischwa.pmcms.gui.treeview;


import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.thischwa.pmcms.gui.BrowserManager;
import de.thischwa.pmcms.livecycle.SiteHolder;
import de.thischwa.pmcms.model.InstanceUtil;
import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.domain.pojo.ASiteResource;
import de.thischwa.pmcms.model.domain.pojo.Image;
import de.thischwa.pmcms.model.domain.pojo.Level;
import de.thischwa.pmcms.model.domain.pojo.Page;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.view.ViewMode;

/**
 * Manage the tree view for the site structure.
 * 
 * @version $Id: TreeViewManager.java 2216 2012-07-14 15:48:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
@Component()
public class TreeViewManager {
	private static Logger logger = Logger.getLogger(TreeViewManager.class);
	private TreeViewer treeViewer = null;
	private TreeViewRootNode treeRoot = null;
	private final TreeViewContentProvider contentProvider = new TreeViewContentProvider();
	
	@Autowired private BrowserManager browserManager;
	
	@Autowired private SiteHolder siteHolder;

	public void init(final Composite parent, final int style) {
		treeViewer = new TreeViewer(parent, style);
		final Menu menu = new Menu(parent.getShell(), SWT.POP_UP);
		menu.addListener(SWT.Show, new Listener() {
			@Override
			public void handleEvent(Event event) {
				logger.debug("Entered handleEvent.");

				APoormansObject<?> po = getSelectedTreeSitepo();
				SiteTreeContextMenuManager menuManager = new SiteTreeContextMenuManager(menu);

				if (po != null) {
					if (InstanceUtil.isSite(po))
						menuManager.buildMenuForSite((Site) po);
					else if (InstanceUtil.isSiteResource(po)) {
						menuManager.buildForSiteResource((ASiteResource) po);
					} else if (po instanceof TreeViewSiteRecourceNode<?>) {
						TreeViewSiteRecourceNode<?> siteRecourceContainer = (TreeViewSiteRecourceNode<?>) po;
						switch (siteRecourceContainer.getResourceType()) {
						case MACRO:
							menuManager.buildForMacro();
							break;
						case TEMPLATE:
							menuManager.buildForTemplate();
							break;
						default:
							logger.warn("Unknown object siteResourceTYPE");
						}
					} else if (InstanceUtil.isPage(po) || InstanceUtil.isGallery(po)) {
						menuManager.buildMenuForPageOrGallery((Page) po);
					} else if (InstanceUtil.isJustLevel(po)) {
						menuManager.buildMenuForLevel((Level) po);
					} else if (InstanceUtil.isImage(po)) {
						menuManager.buildMenuForImage((Image) po);
					} else {
						logger.warn("Unknown object TYPE");
					}

				} else
					logger.error("Selected object is null!");
			}
		});
		treeViewer.getTree().setMenu(menu);
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setLabelProvider(new TreeViewLabelProvider());

		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				APoormansObject<?> po = getSelectedTreeSitepo();
				if (InstanceUtil.isPage(po)) {
					Page page = (Page) po;
					browserManager.view(page, ViewMode.EDIT);
				}
			}
		});
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				APoormansObject<?> po = getSelectedTreeSitepo();
				if (po == null) {
					logger.warn("selected treeviewer object is null!");
					return;
				}
				if (InstanceUtil.isSiteResource(po)) {
					browserManager.view(po, ViewMode.EDIT);
				} else if (InstanceUtil.isRenderable(po))
					browserManager.view(po, ViewMode.PREVIEW);
			}
		});
	}

	public TreeViewRootNode getTreeRoot() {
		return treeRoot;
	}

	/**
	 * Fills and expands the tree to the given {@link IPoorMansObject}.
	 * 
	 * @param po
	 */
	public void fillAndExpands(final APoormansObject<?> po) {
		Site site = siteHolder.getSite();
		if (site == null) {
			removeAll();
			return;
		}
		treeRoot = new TreeViewRootNode(site);
		treeViewer.setInput(treeRoot);
		contentProvider.setTreeViewRoot(treeRoot);
		treeViewer.collapseAll();
		if (po != null) {
			if (InstanceUtil.isSiteResource(po)) {
				if(InstanceUtil.isMacro(po))
					treeViewer.expandToLevel(treeRoot.getMacroNode(), 1);
				else
					treeViewer.expandToLevel(treeRoot.getTemplateNode(), 1);
			} else
				treeViewer.expandToLevel(po, 1);
		} 
		treeViewer.expandToLevel(site, 1);
	}

	public void removeAll() {
		treeViewer.setInput(null);
	}

	public APoormansObject<?> getSelectedTreeSitepo() {
		return (treeViewer.getTree().getSelectionCount() < 1) ? null
				: (APoormansObject<?>) treeViewer.getTree().getSelection()[0].getData();
	}

	public void setState(boolean state) {
		if (treeViewer != null)
			treeViewer.getTree().setEnabled(state);
	}
}
