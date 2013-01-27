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
import java.io.IOException;


import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Shell;

import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.exception.FatalException;
import de.thischwa.pmcms.gui.BrowserManager;
import de.thischwa.pmcms.gui.dialog.DialogManager;
import de.thischwa.pmcms.gui.treeview.TreeViewManager;
import de.thischwa.pmcms.livecycle.SiteHolder;
import de.thischwa.pmcms.model.InstanceUtil;
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
 * Listener for adding {@link IPoorMansObject}'s except for {@link Site}s and {@link Content}s. The 'childClass' is needed to solve
 * ambiguous parent child relationships.
 * 
 * @version $Id: ListenerAddLevelOrPageOrGalleryOrImage.java 2213 2012-06-30 12:01:07Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class ListenerAddLevelOrPageOrGalleryOrImage implements SelectionListener {
	private static Logger logger = Logger.getLogger(ListenerAddLevelOrPageOrGalleryOrImage.class);
	private Class<?> childClass = null;
	private TreeViewManager treeViewManager = InitializationManager.getBean(TreeViewManager.class);

	public ListenerAddLevelOrPageOrGalleryOrImage(final Class<?> childClass) {
		this.childClass = childClass;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		logger.debug("SEL add");
		final Shell shell = e.display.getActiveShell();
		APoormansObject<?> selectedPojo = treeViewManager.getSelectedTreeSitepo();
		if (selectedPojo == null) {
			logger.warn("Can't get a selected tree object");
			return;
		}

		/** selected element is a container */
		if (InstanceUtil.isJustLevel(selectedPojo) || InstanceUtil.isSite(selectedPojo)) {
			Level parentLevel = (Level) selectedPojo;
			if (childClass == null
					|| (!childClass.equals(Level.class) && !childClass.equals(Page.class) && !childClass.equals(Gallery.class)))
				throw new FatalException("Null or wrong child class!");

			if (childClass.equals(Gallery.class)) {
				addGallery(shell, parentLevel);
			} else if (childClass.equals(Page.class)) {
				addPage(shell, parentLevel);
			} else if (childClass.equals(Level.class)) {
				addLevel(shell, parentLevel);
			} else
				throw new FatalException("Unknown child class");

			/** selected element is a gallery */
		} else if (InstanceUtil.isGallery(selectedPojo)) {
			if (childClass == null || !childClass.equals(Image.class))
				throw new FatalException("No child class for gallery!");
			Gallery gallery = (Gallery) selectedPojo;
			Image image = new Image();
			image.setParent(gallery);
			if (DialogManager.startDialogPersitentPojo(shell, image)) {
				logger.debug("A new Image will be constructed!");
				gallery.add(image);
				action(image);
				logger.debug("Image added!");
			}
		} else
			throw new FatalException("Unknown object TYPE!");

	}

	private void addLevel(Shell shell, Level parentLevel) {
		Level newLevel = new Level();
		newLevel.setParent(parentLevel);
		if (DialogManager.startDialogPersitentPojo(shell, newLevel)) {
			logger.debug("A new sublevel will be constructed!");
			parentLevel.add(newLevel);
			action(newLevel);
			logger.debug("Level added.");
		}
	}

	private void addPage(Shell shell, Level parentLevel) {
		Page page = new Page();
		page.setParent(parentLevel);
		if (CollectionUtils.isEmpty(parentLevel.getPages()))
			page.setName(InitializationManager.getProperty("poormans.pojo.page.name"));
		if (DialogManager.startDialogPersitentPojo(shell, page)) {
			logger.debug("A new page will be constructed!");
			parentLevel.add(page);
			action(page);
			logger.debug("Page added.");
		}
	}

	private void addGallery(Shell shell, Level parentLevel) {
		Gallery gallery = new Gallery();
		gallery.setParent(parentLevel);
		if (DialogManager.startDialogPersitentPojo(shell, gallery)) {
			logger.debug("A new gallery will be constructed!");
			parentLevel.add(gallery);
			action(gallery);
			File galleryPath = PoPathInfo.getSiteGalleryDirectory(gallery);
			if (!galleryPath.exists())
				galleryPath.mkdirs();
			logger.debug("Gallery added.");
		}
	}

	private void action(final APoormansObject<?> po) {
		SiteHolder siteHolder = InitializationManager.getBean(SiteHolder.class);
		siteHolder.mark(po);
		treeViewManager.fillAndExpands(po);
		if (InstanceUtil.isRenderable(po)) {
			BrowserManager browserManager = InitializationManager.getBean(BrowserManager.class);
			browserManager.view(po, ViewMode.PREVIEW);
		}
		try {
			SitePersister.write(siteHolder.getSite());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}

}
