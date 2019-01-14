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
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import de.thischwa.pmcms.conf.InitializationManager;
import de.thischwa.pmcms.conf.resource.LabelHolder;
import de.thischwa.pmcms.gui.BrowserManager;
import de.thischwa.pmcms.gui.WorkspaceToolBarManager;
import de.thischwa.pmcms.gui.treeview.TreeViewManager;
import de.thischwa.pmcms.livecycle.PojoHelper;
import de.thischwa.pmcms.livecycle.SiteHolder;
import de.thischwa.pmcms.model.InstanceUtil;
import de.thischwa.pmcms.model.domain.PoInfo;
import de.thischwa.pmcms.model.domain.PoPathInfo;
import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.domain.pojo.ASiteResource;
import de.thischwa.pmcms.model.domain.pojo.Content;
import de.thischwa.pmcms.model.domain.pojo.Gallery;
import de.thischwa.pmcms.model.domain.pojo.Image;
import de.thischwa.pmcms.model.domain.pojo.Level;
import de.thischwa.pmcms.model.domain.pojo.Page;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.model.tool.SitePersister;
import de.thischwa.pmcms.view.ViewMode;

/**
 * Listener for deleting {@link IPoorMansObject}'s except for {@link Content}s. 
 * 
 * @author Thilo Schwarz
 */
public class ListenerDeletePersitentPojo implements SelectionListener {
	private static Logger logger = Logger.getLogger(ListenerDeletePersitentPojo.class);
	private APoormansObject<?> po;
	private TreeViewManager treeViewManager;
	private BrowserManager browserManager;
	private PojoHelper pojoHelper;
	
	public ListenerDeletePersitentPojo(APoormansObject<?> po) {
		this.po = po;
		treeViewManager = InitializationManager.getBean(TreeViewManager.class);
		browserManager = InitializationManager.getBean(BrowserManager.class);
		pojoHelper = new PojoHelper();
		if(po != null)
			pojoHelper.putpo(po);
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		logger.debug("SEL del"); 
		if (po == null) 
			return;

		treeViewManager.removeAll();
		final Shell shell = e.display.getActiveShell();
		APoormansObject<?> parent = (APoormansObject<?>) po.getParent();
		MessageBox mb = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		mb.setText(LabelHolder.get("popup.question")); //$NON-NLS-1$
		mb.setMessage(LabelHolder.get("dialog.operation.delete.hint") + po.getDecorationString()); //$NON-NLS-1$
		if (mb.open() == SWT.YES) {
			if (InstanceUtil.isJustLevel(po)) {
				Level level = (Level) po;
				delete(shell, level);
			} else if (InstanceUtil.isSite(po)) {
				Site site = (Site)po;
				delete(site);
			} else if (InstanceUtil.isImage(po)) {
				Image image = (Image)po;
				parent = delete(shell, image);
			} else if (InstanceUtil.isGallery(po)) {
				Gallery gallery = (Gallery)po;
				delete(shell, gallery);
			} else if (InstanceUtil.isPage(po)) {
				Page page = (Page)po;
				page.getParent().remove(page);
				page.setParent(null);
			} else if (InstanceUtil.isSiteResource(po)) {
				ASiteResource res = (ASiteResource)po;
				res.getParent().remove(res);
			}

			if (parent != null) {
				pojoHelper.putpo(parent);
			}
			parent = null;
			if (InstanceUtil.isGallery(pojoHelper.getRenderable()))
				browserManager.view((APoormansObject<?>) pojoHelper.getRenderable(), ViewMode.PREVIEW);
			else
				browserManager.showHelp();
			SiteHolder siteHolder = InitializationManager.getBean(SiteHolder.class);
			try {
				if(!InstanceUtil.isSite(po)) {
					SitePersister.write(siteHolder.getSite());
				} else {
					siteHolder.clear();
				}
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
			po = null;
		}
		treeViewManager.fillAndExpands(pojoHelper.get());
	}

	private void delete(Shell shell, Level level) {
		Map<String, Gallery> levelGalleries = PoInfo.getGalleries(level);
		if (!levelGalleries.isEmpty()) { 
			Collection<Gallery> galleries = levelGalleries.values();
			MessageBox mbDelGal = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			mbDelGal.setText(LabelHolder.get("popup.question")); //$NON-NLS-1$
			mbDelGal.setMessage(LabelHolder.get("dialog.operation.delete.level.galleryhint")); //$NON-NLS-1$
			if (mbDelGal.open() == SWT.YES) {
				for (Gallery gal : galleries) {
					File galdir = PoPathInfo.getSiteGalleryDirectory(gal);
					level.remove(gal);
					gal.setParent(null);
					try {
						FileUtils.deleteDirectory(galdir);
					} catch (IOException e1) {
						throw new RuntimeException("Can't delete gallery path: " + e1.getMessage(), e1);
					}
				}
			}
		}
		Level parentLevel = level.getParent();
		parentLevel.remove(level);
		level.setParent(null);		
	}

	private void delete(Shell shell, Gallery gallery) {
		File galleryDirectory = PoPathInfo.getSiteGalleryDirectory(gallery);
		MessageBox mbGallery = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		mbGallery.setText(LabelHolder.get("popup.question")); //$NON-NLS-1
		mbGallery.setMessage(LabelHolder.get("dialog.operation.delete.gallery.deleteimages")); //$NON-NLS-1
		if (mbGallery.open() == SWT.YES) {
			try {
				FileUtils.deleteDirectory(galleryDirectory);
			} catch (IOException e1) {
				logger.error("While deleting gallery directory: " + e1.getMessage(), e1);
			}
			logger.info("Deleted gallery");
		}
		Level parentLevel = gallery.getParent();
		parentLevel.remove(gallery);
		gallery.setParent(null);
	}

	private Gallery delete(final Shell shell, Image image) {
		Gallery parent = image.getParent();
		File galleryDirectory = PoPathInfo.getSiteGalleryDirectory(parent);
		File imageFile = new File(galleryDirectory, image.getFileName());
		parent.getImages().remove(image);
		image.setParent(null);
		imageFile.delete();
		if (parent.getImages().isEmpty()) {
			MessageBox mbGallery = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			mbGallery.setText(LabelHolder.get("popup.question")); //$NON-NLS-1$
			mbGallery.setMessage(LabelHolder.get("dialog.operation.delete.gallery.hint")); //$NON-NLS-1$
			if (mbGallery.open() == SWT.YES) {
				try {
					FileUtils.deleteDirectory(galleryDirectory);
				} catch (IOException e1) {
					logger.error("While deleting gallery directory: " + e1.getMessage(), e1);
				}
				parent = null;
				pojoHelper.deleteGallery();
				logger.info("Deleted empty gallery");
			}
		}		
		return parent;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	private void delete(final Site site) {
		File siteDir = PoPathInfo.getSiteDirectory(site);
		pojoHelper.deleteSite();
		SiteHolder siteHolder = InitializationManager.getBean(SiteHolder.class);
		siteHolder.deleteSite();
		try {
			FileUtils.deleteDirectory(siteDir);
			SitePersister.getDataFile(site).delete();
		} catch (IOException e1) {
			throw new RuntimeException("Can't delete site path: " + e1.getMessage(), e1);
		}
		browserManager.showHelp();
		// we have to delete the old one because it could be override a fresh imported one
		WorkspaceToolBarManager.deleteOldSite(); 
		WorkspaceToolBarManager.fillComboSiteSelection();
	}
}
