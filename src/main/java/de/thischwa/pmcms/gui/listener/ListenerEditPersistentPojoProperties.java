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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Shell;

import de.thischwa.pmcms.conf.InitializationManager;
import de.thischwa.pmcms.gui.BrowserManager;
import de.thischwa.pmcms.gui.WorkspaceToolBarManager;
import de.thischwa.pmcms.gui.dialog.DialogManager;
import de.thischwa.pmcms.gui.treeview.TreeViewManager;
import de.thischwa.pmcms.model.InstanceUtil;
import de.thischwa.pmcms.model.domain.PoInfo;
import de.thischwa.pmcms.model.domain.PoPathInfo;
import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.domain.pojo.Content;
import de.thischwa.pmcms.model.domain.pojo.Gallery;
import de.thischwa.pmcms.model.domain.pojo.Image;
import de.thischwa.pmcms.model.domain.pojo.Level;
import de.thischwa.pmcms.model.domain.pojo.Page;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.model.tool.SitePersister;
import de.thischwa.pmcms.view.ViewMode;

/**
 * Listener for editing the properties of {@link IPoorMansObject} except for {@link Content}. Object specific checks or
 * tasks to trigger are respected.
 * 
 * @author Thilo Schwarz
 */
public class ListenerEditPersistentPojoProperties implements SelectionListener {
	private static Logger logger = Logger.getLogger(ListenerEditPersistentPojoProperties.class);
	private APoormansObject<?> po;
 
	public ListenerEditPersistentPojoProperties(final APoormansObject<?> po) {
		this.po = po;
	}
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		logger.debug("SEL edit");
		if (po == null)
			return;

		final Shell shell = e.display.getActiveShell();
		if (InstanceUtil.isSite(po)) {
			Site site = (Site) po; 
			edit(shell, site);
		} else if (InstanceUtil.isJustLevel(po)) {
			Level level = (Level) po;
			if (DialogManager.startDialogPersitentPojo(shell, level)) {
				actionAfterChangedProperties(level);
				logger.debug("Properties of a level are changed!");
			}
		} else if (InstanceUtil.isGallery(po)) { 
			Gallery gallery = (Gallery) po;
			edit(shell, gallery);
		} else if (InstanceUtil.isPage(po)) {
			Page page = (Page) po;
			if (DialogManager.startDialogPersitentPojo(shell, page)) {
				actionAfterChangedProperties(page);
				logger.debug("Properties of a page are changed!");
			}
		} else if (InstanceUtil.isImage(po)) {
			Image image = (Image) po;
			if (DialogManager.startDialogPersitentPojo(shell, image)) {
				actionAfterChangedProperties(image);
				logger.debug("Properties of an image are changed!");
			}
		} else
			logger.debug("Unknown object to edit.");
	}

	private void edit(Shell shell, Gallery gallery) {
		String oldGalleryName = gallery.getName();
		File gallerySrc = PoPathInfo.getSiteGalleryDirectory(gallery);
		if (DialogManager.startDialogPersitentPojo(shell, gallery)) {
			if (!oldGalleryName.equals(gallery.getName())) {
				Site site = PoInfo.getSite(gallery);
				File galleryDest = PoPathInfo.getSiteGalleryDirectory(gallery);
				gallerySrc.renameTo(galleryDest);
				File cacheSrc = new File(PoPathInfo.getSiteImageCacheDirectory(site), oldGalleryName);
				if (cacheSrc.exists()) {
					File cacheDest = new File(PoPathInfo.getSiteImageCacheDirectory(site), gallery.getName());
					cacheSrc.renameTo(cacheDest);
				}
				logger.debug("Gallery got a new name, dependent directories are renamed!");
			}
			actionAfterChangedProperties(gallery);
			logger.debug("Properties of a gallery are changed!");
		}
	}

	private void edit(Shell shell, Site site) {
		String oldSiteUrl = site.getUrl();
		if (DialogManager.startDialogPersitentPojo(shell, site)) {
			if (!StringUtils.equals(site.getUrl(), oldSiteUrl)) {
				File dir = PoPathInfo.getSiteDirectory(site);
				File oldDir = PoPathInfo.getSiteDirectory(oldSiteUrl);
				oldDir.renameTo(dir);	
				File oldDataFile = SitePersister.getDataFile(oldSiteUrl);
				oldDataFile.delete();
			}
			WorkspaceToolBarManager.actionAfterSiteRenamed(site);
			logger.debug("Properties of site are changed!");
		}		
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	private void actionAfterChangedProperties(final APoormansObject<?> po) {
		TreeViewManager treeViewManager = InitializationManager.getBean(TreeViewManager.class);
		treeViewManager.fillAndExpands(po);
		if (InstanceUtil.isRenderable(po)) {
			BrowserManager browserManager = InitializationManager.getBean(BrowserManager.class);
			browserManager.view(po, ViewMode.PREVIEW);
		}
	}
}
