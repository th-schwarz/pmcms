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
package de.thischwa.pmcms.model.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import de.thischwa.pmcms.configuration.resource.LabelHolder;
import de.thischwa.pmcms.exception.FatalException;
import de.thischwa.pmcms.model.IRenderable;
import de.thischwa.pmcms.model.InstanceUtil;
import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.domain.pojo.Content;
import de.thischwa.pmcms.model.domain.pojo.Gallery;
import de.thischwa.pmcms.model.domain.pojo.Image;
import de.thischwa.pmcms.model.domain.pojo.Level;
import de.thischwa.pmcms.model.domain.pojo.Page;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.model.domain.pojo.Template;
import de.thischwa.pmcms.model.domain.pojo.TemplateType;

/**
 * Helper to provide some informations of an {@link APoormansObject}, which you can get without a database call.
 * All methods are null save!
 *
 * @author Thilo Schwarz
 */
public class PoInfo {

	/**
	 * Get the value of a field.
	 * 
	 * @param fieldName
	 * @return Value of the field or null, if not found.
	 */
	public static String getValue(final Page page, final String fieldName) {
		if (page == null || StringUtils.isBlank(fieldName) || CollectionUtils.isEmpty(page.getContent()))
			return null;

		for (Content content : page.getContent()) {
			if (content.getName().equalsIgnoreCase(fieldName))
				return content.getValue();
		}
		return null;
	}
	
	/**
	 * Check if 'child' can have 'newParent' as new parent. Not only the TYPE of the new parent is respected, but also the id too!
	 * 
	 * @param newParent
	 * @param child
	 * @return True, if 'child' can have 'newParent' as new parent, otherwise false.
	 */
	public static boolean checkParentChildRelationship(final APoormansObject<?> newParent, final APoormansObject<?> child) {
		if (newParent == null || child == null)
			return false;
		if (InstanceUtil.isSite(newParent) && InstanceUtil.isPage(child)) {
			Site site = (Site) newParent;
			return site.getPages().isEmpty();
		}
		if (!(((InstanceUtil.isLevel(newParent)) && InstanceUtil.isLevel(child))
				|| (InstanceUtil.isLevel(newParent) && InstanceUtil.isRenderable(child) && !InstanceUtil.isImage(child))
				|| (InstanceUtil.isGallery(newParent) && InstanceUtil.isImage(child))
				))
			return false;
		return newParent.equals(child) ? false : true;
	}	
		
	public static Content getContentByName(final Page page, final String fieldName) {
		if (StringUtils.isBlank(fieldName) || page == null || page.getContent() == null)
			return null;
		for (Content field : page.getContent()) {
			if (field.getName().equals(fieldName))
				return field;
		}
		return null;
	}
	
	public static Page getPageByName(final Level level, final String pageName) {
		if(level == null)
			throw new IllegalArgumentException("Level is null!");
		if (CollectionUtils.isEmpty(level.getPages()) || StringUtils.isBlank(pageName))
			return null;
		for (Page page : level.getPages()) {
			if (page.getName().equals(pageName))
				return page;
		}
		return null;
	}
	
	/**
	 * Collects all (iterative) sublevels of the specified level.
	 * 
	 * @param level
	 * @return all sublevels of the specified level.
	 */
	public static List<Level> getAllLevels(final Level level) {
		List<Level> levels = new ArrayList<Level>();
		collectLevels(level, levels);
		return levels;
	}
	
	private static void collectLevels(final Level level, List<Level> levels) {
		if (!InstanceUtil.isSite(level))
			levels.add(level);
		for (Level sublevel : level.getSublevels())
			collectLevels(sublevel, levels);
	}
	
	/**
	 * Tries to get a 'direct' sublevel of the desired level by name.
	 *  
	 * @param level
	 * @param name
	 * @return The sublevel of the desired level with the desired name.
	 */
	public static Level getLevelByName(final Level level, final String name) {
		if(level == null)
			throw new IllegalArgumentException("Level is null!");
		if (StringUtils.isBlank(name) || level == null)
			return null;
		for (Level tmpLevel : level.getSublevels())
			if (tmpLevel.getName().equals(name))
				return tmpLevel;
		
		throw new FatalException("No level named [" + name + "] found!");
	}

	public static Level getRootLevel(final Level level) {
		return (level.getSublevels().isEmpty()) ? null : level.getSublevels().iterator().next();
	}

	public static int getMaxChildContainerRank(final Level level) {
		return (CollectionUtils.isEmpty(level.getSublevels())) ? -1 : level.getSublevels().size();
	}

	public static Page getRootPage(final Level level) {
		return (CollectionUtils.isEmpty(level.getPages())) ? null : level.getPages().iterator().next();
	}
	
	private static void collectGalleries(final Level level, boolean recursive,  Map<String, Gallery> galleries) {
		if (level.getPages() != null)
			for (Page page : level.getPages()) 
				if (InstanceUtil.isGallery(page))
					galleries.put(page.getName(), (Gallery) page);
		if (recursive && level.getSublevels() != null) 
			for (Level tmpLevel : level.getSublevels()) 
				collectGalleries(tmpLevel, recursive, galleries);
	}
	
	public static Map<String, Gallery> getGalleries(final Level level) {
		Map<String, Gallery> galleries = new HashMap<String, Gallery>();
		if (InstanceUtil.isSite(level))
			collectGalleries(level, true, galleries);
		else
			collectGalleries(level, false, galleries);
		return galleries;
	}

	public static Set<IRenderable> collectRenderables(final Level level) {
		return collectRenderables(level, null);
	}
	
	public static Set<IRenderable> collectRenderables(final Level level, StringBuilder messages) {
		Set<IRenderable> renderables = new HashSet<IRenderable>();
		collectRenderables(level, renderables, messages);
		return renderables;
	}
	
	private static void collectRenderables(final Level level, Set<IRenderable> renderables, StringBuilder messages) {
		for (Level tmpContainer : level.getSublevels()) {
			if (messages != null && !InstanceUtil.isSite(level) && CollectionUtils.isEmpty(level.getPages())) {
				messages.append(LabelHolder.get("task.export.error.pojo.levelhasnopage")); //$NON-NLS-1$
				messages.append(level.getDecorationString());
				messages.append('\n');
				renderables.clear();
				return;
			}
			collectRenderables(tmpContainer, renderables, messages);
		}
		for (Page page : level.getPages()) {
			renderables.add(page);
			if (InstanceUtil.isGallery(page)) {
				List<Image> images = ((Gallery)page).getImages();
				if (renderables != null && CollectionUtils.isEmpty(images)) {
					messages.append(LabelHolder.get("task.export.error.pojo.galleryhasnoimage")); //$NON-NLS-1$
					messages.append(page.getDecorationString());
					messages.append('\n');
					renderables.clear();
					return;
				} else
					renderables.addAll(images);
			}
		}
	}
	
	public static Site getSite(final APoormansObject<?> po) {
		if (po == null)
			return null;
		if (InstanceUtil.isSite(po))
			return (Site) po;
		APoormansObject<?> parent = (APoormansObject<?>) po.getParent();
		while (parent.getParent() != null)
			parent = (APoormansObject<?>) parent.getParent();
		if (!(parent instanceof Site))
			throw new IllegalArgumentException("Fatal hierarchy error!");
		return (Site) parent;
	}
	
	private static Site getSite(final IRenderable renderable) {
		return getSite((APoormansObject<?>)renderable);
	}
	
	public static IRenderable getFirstRenderable(final Site site) {
		if (site == null) 
			return null;
		if (!CollectionUtils.isEmpty(site.getPages()))
			return site.getPages().iterator().next();
		Level level = getRootLevel(site);
		return (level == null) ? null : getRootPage(level);
	}
	
	public static boolean hasFileTranferInfo(final Site site) {
		return (StringUtils.isNotBlank(site.getTransferHost()) 
				&& StringUtils.isNotBlank(site.getTransferLoginUser()) 
				&& StringUtils.isNotBlank(site.getTransferLoginPassword()));
	}
	
	public static boolean isWelcomePage(final Page page) {
		return (page != null && InstanceUtil.isSite(page.getParent()));
	}
	
	public static String getTemplateContent(final IRenderable renderable) {
		return (renderable == null) ? null : renderable.getTemplate().getText();
	
	}
	
	public static String[] getTemplateNames(IRenderable renderable) {
		List<String> names = new ArrayList<String>();
		for (Template template : getTemplates(getSite(renderable), renderable.getTemplateType()))
			names.add(template.getName());
		return names.toArray(new String[0]);
	}
	
	public static List<Template> getTemplates(final Site site, TemplateType type) {
		List<Template> templates = new ArrayList<Template>();
		if(site == null)
			return templates;
		for (Template template : site.getTemplates()) {
			if (type == template.getType())
				templates.add(template);
		}
		
		return templates;
	}

	/**
	 * Generate all breadcrumbs objects for persitentPojo (bottom-up).
	 * 
	 * @param po
	 */
	public static List<APoormansObject<?>> getBreadcrumbs(final APoormansObject<?> po) {
		List<APoormansObject<?>> list = new ArrayList<APoormansObject<?>>();
		if (po != null) {
			APoormansObject<?> temppo = po;
			list.add(temppo);
			while (temppo.getParent() != null) {
				temppo = (APoormansObject<?>) temppo.getParent();
				list.add(temppo);
			}
		}
		Collections.reverse(list);
		return list;
	}
}
