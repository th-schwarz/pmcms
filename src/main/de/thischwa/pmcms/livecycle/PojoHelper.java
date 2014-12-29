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
package de.thischwa.pmcms.livecycle;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.thischwa.pmcms.model.IRenderable;
import de.thischwa.pmcms.model.InstanceUtil;
import de.thischwa.pmcms.model.domain.PoInfo;
import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.domain.pojo.ASiteResource;
import de.thischwa.pmcms.model.domain.pojo.Gallery;
import de.thischwa.pmcms.model.domain.pojo.Image;
import de.thischwa.pmcms.model.domain.pojo.Level;
import de.thischwa.pmcms.model.domain.pojo.Page;
import de.thischwa.pmcms.model.domain.pojo.Site;

/**
 * Helper class to provide infos about the current rendered {@link APoorMansObject}.
 * 
 * @author Thilo Schwarz
 */
public class PojoHelper {
	private static Logger logger = Logger.getLogger(PojoHelper.class);

	private Site site = null;
	private Level level = null;
	private Page page = null;
	private Gallery gallery = null;
	private Image image = null;
	private ASiteResource siteResource = null;

	public void deletepo(final APoormansObject<?> po) {
		if (InstanceUtil.isSite(po))
			deleteSite();
		else if (InstanceUtil.isJustLevel(po))
			deleteLevel();
		else if (InstanceUtil.isPage(po))
			deletePage();
		else if (InstanceUtil.isGallery(po))
			deleteGallery();
		else if (InstanceUtil.isImage(po))
			deleteImage();
		else if (InstanceUtil.isSiteResource(po))
			deleteSiteResource();
		else {
			logger.warn("Unknown object.");
			return;
		}
	}

	public void putpo(final APoormansObject<?> po) {
		if (po == null)
			throw new IllegalArgumentException("Can't work with object is null!");
		logger.debug("Entered putPersitentPojo with: ".concat(po.toString()));
		if (InstanceUtil.isSite(po))
			putSite((Site) po);
		else if (InstanceUtil.isJustLevel(po))
			putLevel((Level) po);
		else if (InstanceUtil.isPage(po))
			putPage((Page) po);
		else if (InstanceUtil.isGallery(po))
			putGallery((Gallery) po);
		else if (InstanceUtil.isImage(po))
			putImage((Image) po);
		else if (InstanceUtil.isSiteResource(po))
			putSiteResource((ASiteResource) po);
		else {
			logger.warn("Unknown object or content: " + po.getClass().getName());
			return;
		}
	}

	private void putSite(final Site site) {
		deleteLevel();
		this.site = site;
	}

	private void putLevel(final Level level) {
		if (level == null) {
			logger.warn("Can't handle a Level = null !");
			return;
		}
		site = PoInfo.getSite(level);
		this.level = level;
		deletePage();
		deleteGallery();
		deleteSiteResource();
	}

	private void putPage(final Page page) {
		if (page == null) {
			logger.warn("Can't handle a Page = null !");
			return;
		}
		site = PoInfo.getSite(page);
		level = page.getParent();
		this.page = page;
		deleteGallery();
		deleteSiteResource();
	}

	private void putGallery(final Gallery gallery) {
		if (gallery == null) {
			logger.warn("Can't handle a Gallery = null !");
			return;
		}
		site = PoInfo.getSite(gallery);
		level = gallery.getParent();
		this.gallery = gallery;
		deletePage();
		deleteSiteResource();
	}

	private void putImage(final Image image) {
		if (image == null) {
			logger.warn("Can't handle an Image = null !");
			return;
		}
		gallery = image.getParent();
		putGallery(gallery);
		this.image = image;
		deleteSiteResource();
	}

	private void putSiteResource(final ASiteResource siteResource) {
		this.siteResource = siteResource;
	}

	/**
	 * @return The {@link Site} currently working on.
	 */
	public Site getSite() {
		return site;
	}

	/**
	 * @return The {@link Level} currently working on.
	 */
	public Level getLevel() {
		return level;
	}

	/**
	 * @return The {@link Page} currently working on.
	 */
	public Page getPage() {
		return page;
	}

	/**
	 * @return The {@link Gallery} currently working on.
	 */
	public Gallery getGallery() {
		return gallery;
	}

	/**
	 * @return The {@link de.thischwa.pmcms.model.domain.pojo.Image} currently working on.
	 */
	public Image getImage() {
		return image;
	}

	public void deleteSite() {
		site = null;
		level = null;
		page = null;
		gallery = null;
		image = null;
	}

	public void deleteLevel() {
		level = null;
		page = null;
		gallery = null;
		image = null;
	}

	public void deletePage() {
		page = null;
		image = null;
	}

	public void deleteGallery() {
		gallery = null;
		image = null;
	}

	public void deleteImage() {
		image = null;
	}

	public void deleteSiteResource() {
		siteResource = null;
	}

	/**
	 * @return The 'deepest' available {@link IRenderable}.
	 */
	public IRenderable getRenderable() {
		if (image != null)
			return image;
		if (gallery != null)
			return image;
		if (page != null)
			return page;
		return null;
	}

	/**
	 * @return The 'deepest' available {@link APoormansObject}.
	 */
	public APoormansObject<?> get() {
		if (image != null)
			return image;
		if (gallery != null)
			return gallery;
		if (page != null)
			return page;
		if (level != null)
			return level;
		if (site != null)
			return site;
		return null;
	}

	/**
	 * @return The 'deepest' available {@link ASiteResource}.
	 */
	public ASiteResource getSiteResource() {
		return siteResource;
	}

	/**
	 * Wrapper for {@link PoInfo#getBreadcrumbs(IPoorMansObject)} to get the breadcrumbs objects of the 'deepest' current object.
	 */
	public List<APoormansObject<?>> getCurrentBreadcrumbs() {
		if (image != null)
			return PoInfo.getBreadcrumbs(getImage());
		else if (gallery != null)
			return PoInfo.getBreadcrumbs(getGallery());
		else if (page != null)
			return PoInfo.getBreadcrumbs(getPage());
		else if (level != null)
			return PoInfo.getBreadcrumbs(getLevel());
		else
			return new ArrayList<APoormansObject<?>>();
	}

	public boolean containsInBreadcrumbs(final APoormansObject<?> po) {
		return getCurrentBreadcrumbs().contains(po);
	}
}
