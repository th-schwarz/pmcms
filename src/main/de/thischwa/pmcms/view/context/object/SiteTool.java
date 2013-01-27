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
package de.thischwa.pmcms.view.context.object;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.thischwa.pmcms.livecycle.PojoHelper;
import de.thischwa.pmcms.model.IOrderable;
import de.thischwa.pmcms.model.InstanceUtil;
import de.thischwa.pmcms.model.domain.OrderableInfo;
import de.thischwa.pmcms.model.domain.PoInfo;
import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.domain.pojo.Gallery;
import de.thischwa.pmcms.model.domain.pojo.Image;
import de.thischwa.pmcms.model.domain.pojo.Level;
import de.thischwa.pmcms.model.domain.pojo.Page;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.view.ViewMode;
import de.thischwa.pmcms.view.context.IContextObjectCommon;
import de.thischwa.pmcms.view.context.IContextObjectNeedPojoHelper;
import de.thischwa.pmcms.view.context.IContextObjectNeedViewMode;
import de.thischwa.pmcms.view.context.object.tagtool.ImageTagTool;
import de.thischwa.pmcms.view.context.object.tagtool.LinkTagTool;
import de.thischwa.pmcms.view.renderer.VelocityRenderer;

/**
 * Context object to provide information about the current site.
 * 
 * @version $Id: SiteTool.java 2212 2012-06-30 11:52:07Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
@Component("sitetool")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SiteTool implements IContextObjectCommon, IContextObjectNeedPojoHelper, IContextObjectNeedViewMode {
	private static Logger logger = Logger.getLogger(SiteTool.class);
	private PojoHelper pojoHelper;
	private Site site;
	private APoormansObject<?> po;
	private ViewMode viewMode;
	@Autowired private VelocityRenderer velocityRenderer;
	@Autowired private ImageTagTool imageTagTool;
	@Autowired private LinkTagTool linkTagTool; 
	
	/*
	 * For internal use only.
	 * 
	 * @see de.thischwa.pmcms.view.context.IContextObjectNeedPojoHelper#setPojoHelper(de.thischwa.pmcms.model.PojoHelper)
	 */
	@Override
	public void setPojoHelper(final PojoHelper pojoHelper) {
		this.pojoHelper = pojoHelper;
		this.po = (APoormansObject<?>) this.pojoHelper.getRenderable();
		site = PoInfo.getSite(po);
	}

	@Override
	public void setViewMode(final ViewMode viewMode) {
		this.viewMode = viewMode;
	}

	/**
	 * @return The current {@link Site}.
	 */
	public Site getSite() {
		return site;
	}

	/**
	 * @return The current {@link Level} or null.
	 */
	public Level getLevel() {
		return this.pojoHelper.getLevel();
	}

	/**
	 * It tries to get a 'direct' sublevel of the current level by name.
	 * 
	 * @param name
	 *            Name of the desired sublevel.
	 * @return Sublevel with the desired name.
	 * @see PoInfo#getLevelByName(Level, String)
	 */
	public Level getLevelByName(String name) {
		return PoInfo.getLevelByName(getLevel(), name);
	}

	/**
	 * It tries to get a 'direct' sublevel of the desired level by name.
	 * 
	 * @param level The parent level.
	 * @param name
	 *            Name of the desired sublevel.
	 * @return Sublevel with the desired name.
	 * @see PoInfo#getLevelByName(Level, String)
	 */	
	public Level getLevelByName(Level level, String name) {
		return PoInfo.getLevelByName(level, name);
	}

	/**
	 * Tries to find a {@link Level} by name from all levels of a {@link Site}. If there are more levels with the same name, the first one
	 * is taken.
	 * 
	 * @param name
	 *            Name of the desired {@link Level}.
	 * @return A {@link Level} with the desired name or null.
	 */
	public Level getLevelByNameGlobal(final String name) {
		List<Level> levels = PoInfo.getAllLevels(site);
		for (Level level : levels)
			if (level.getName().equals(name))
				return level;
		return null;
	}

	/**
	 * Tries to find a {@link Page} by name of a {@link Level}.
	 * 
	 * @param level
	 *            {@link Level} where to find the {@link Page}.
	 * @param pageName
	 *            Desired name of the {@link Page}.
	 * @return The {@link Page} with the desired name or null.
	 * @see PoInfo#getPageByName(Level, String).
	 */
	public Page getPageByName(final Level level, final String pageName) {
		return PoInfo.getPageByName(level, pageName);
	}

	/**
	 * @return The current {@link Page} or null.
	 */
	public Page getPage() {
		return this.pojoHelper.getPage();
	}

	/**
	 * @return The current {@link Image} or null.
	 */
	public Image getImage() {
		return this.pojoHelper.getImage();
	}

	/**
	 * @return True if the {@link IOrderable} has a previous one.
	 */
	public boolean getHasPrevious(final IOrderable<?> orderable) {
		return OrderableInfo.hasPrevious(orderable);
	}

	/**
	 * @return True if the {@link IOrderable} has a next one.
	 */
	public boolean getHasNext(final IOrderable<?> orderable) {
		return OrderableInfo.hasNext(orderable);
	}

	public IOrderable<?> getNext(final IOrderable<?> orderable) {
		return OrderableInfo.getNext(orderable);
	}

	public IOrderable<?> getPrevious(final IOrderable<?> orderable) {
		return OrderableInfo.getPrevious(orderable);
	}

	/**
	 * @return The content field with the desired name of the current {@link Page}.
	 */
	public String getContent(final String fieldName) {
		if (this.pojoHelper.getPage() == null) {
			logger.warn("No current page found!");
			return "";
		}
		if (CollectionUtils.isEmpty(this.pojoHelper.getPage().getContent())) {
			logger.debug("Page has no content!");
			return "";
		}

		String value = PoInfo.getValue(this.pojoHelper.getPage(), fieldName);
		if (value == null) {
			logger.warn("No value found for content named: ".concat(fieldName));
			return "";
		}

		imageTagTool.setViewMode(viewMode);
		imageTagTool.setPojoHelper(this.pojoHelper);
		linkTagTool.setViewMode(viewMode);
		linkTagTool.setPojoHelper(this.pojoHelper);
		Map<String, Object> ctxObjs = new HashMap<String, Object>(2);
		ctxObjs.put("imagetagtool", imageTagTool);
		ctxObjs.put("linktagtool", linkTagTool);
		return velocityRenderer.renderString(value, ctxObjs);
	}

	/**
	 * @param po
	 * @return True, if the desired object is inside the current object path.
	 */
	public boolean hierarchyContains(APoormansObject<?> po) {
		return this.pojoHelper.containsInBreadcrumbs(po);
	}

	/**
	 * @return True if the current {@link Page} is a {@link Gallery}. Remember: gallery is inherited from page.
	 */
	public boolean getPageIsGallery() {
		return getPageIsGallery(this.pojoHelper.getPage());
	}
	
	/**
	 * Check if the desired {@link Page} if a {@link Gallery}.
	 * @param page
	 * 
	 * @return True if the desired {@link Page} is a {@link Gallery}, otherwise false.
	 */
	public boolean getPageIsGallery(final Page page) {
		return InstanceUtil.isGallery(page);
	}

	/**
	 * @return Welcome page of the {@link Site} or null.
	 */
	public Page getWelcomePage() {
		List<Page> pages = this.pojoHelper.getSite().getPages();
		return (CollectionUtils.isEmpty(pages)) ? null : pages.get(0);
	}

	/**
	 * @return A list of breadcrumbs without the beginning site object.
	 */
	public List<APoormansObject<?>> getBreadcrumbs() {
		List<APoormansObject<?>> crumbs = this.pojoHelper.getCurrentBreadcrumbs();
		if (!crumbs.isEmpty())
			crumbs.remove(0);
		return crumbs;
	}

	/**
	 * @return True, if pojo is an {@link Image}.
	 */
	public boolean isImage(final APoormansObject<?> pojo) {
		return InstanceUtil.isImage(pojo);
	}
	
	public boolean isGallery(final APoormansObject<?> pojo) {
		return InstanceUtil.isGallery(pojo);
	}
}
