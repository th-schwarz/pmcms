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
package de.thischwa.pmcms.view.context;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.log4j.Logger;

import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.livecycle.PojoHelper;
import de.thischwa.pmcms.model.InstanceUtil;
import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.domain.pojo.Content;
import de.thischwa.pmcms.model.domain.pojo.Gallery;
import de.thischwa.pmcms.model.domain.pojo.Image;
import de.thischwa.pmcms.model.domain.pojo.Page;
import de.thischwa.pmcms.view.ViewMode;

/**
 * Manages of the Velocity context objects. <br>
 * It contains the common pojo objects and initializes context objects managed by spring. 
 * These objects have to implemented one or more of these interfaces: {@link IContextObjectCommon}, 
 * {@link IContextObjectGallery}, {@link IContextObjectNeedViewMode},{@link IContextObjectNeedViewMode}, 
 * {@link IContextObjectNeedPojoHelper}.<br>
 * Objects which implements {@link IContextObjectCommon} will initialized automatically. All others have to 
 * initialized by calling {@link #configureContextObject(Object)}.
 */
public class ContextObjectManager {
	private static Logger logger = Logger.getLogger(ContextObjectManager.class);
	private PojoHelper pojoHelper;
	private ViewMode viewMode;
	private Map<String, Object> contextObjects = new HashMap<String, Object>();

	public ContextObjectManager(final PojoHelper pojoHelper, final ViewMode viewMode) {
		this.pojoHelper = pojoHelper;
		this.viewMode = viewMode;
		
		// collect the common objects, because these are always needed
		Map<String, IContextObjectCommon> beans = InitializationManager.getBeansOfType(IContextObjectCommon.class);
		contextObjects.putAll(beans); 
	}
	
	/**
	 * @return the contextObjects
	 */
	public Map<String, Object> getContextObjects() {
		return contextObjects;
	}

	/**
	 * Put an 'object' with the 'key' in the contextObjects. Duplicates will be overwritten.
	 */
	public void put(final String key, final Object object) {
		contextObjects.put(key, object);
	}

	/**
	 * Put every object in 'map' to the context objects. Duplicates will be overwritten.
	 */
	public void putAll(Map<String, Object> map) {
		contextObjects.putAll(map);
	}

	/**
	 * Initialize the context object implemented 'clazz'. If the object is already in the map, this one will be initialized. Otherwise a new
	 * object will be constructed.
	 */
	public <T> void  initializeContextObject(final Class<T> clazz) {
		logger.debug("Entered initializeContextObject, TYPE: ".concat(clazz.getName()));
		Map<String, T> beans = InitializationManager.getBeansOfType(clazz);
		for (String beanId : beans.keySet()) {
			Object contextObject;
			if (this.contextObjects.containsKey(beanId)) {
				contextObject = this.contextObjects.get(beanId);
				logger.debug("- found: ".concat(beanId));
			} else {
				contextObject = beans.get(beanId);
				logger.debug("- new: ".concat(beanId));
			}
			configureContextObject(contextObject);

			this.contextObjects.put(beanId, contextObject);
		}
	}

	private void configureContextObject(Object contextObject) {
		List<?> interfaces = Arrays.asList(contextObject.getClass().getInterfaces());
		if (interfaces.contains(IContextObjectNeedPojoHelper.class))
			((IContextObjectNeedPojoHelper) contextObject).setPojoHelper(pojoHelper);
		if (interfaces.contains(IContextObjectNeedViewMode.class))
			((IContextObjectNeedViewMode) contextObject).setViewMode(viewMode);
	}

	/**
	 * Set the velocity context objects needed for {@link Page}.
	 */
	private void putContextObjectsForPage(ContextObjectManager contextObjectManager, final Page page) {
		contextObjectManager.put("page", page);
		if (page.getContent() != null) {
			for (Content content : page.getContent()) {
				contextObjectManager.put(content.getName(), content.getValue()); 
			}
		}
	}

	/**
	 * Set the velocity context objects needed for {@link Gallery}.
	 */
	private void putContextObjectsForGallery(ContextObjectManager contextObjectManager, final Gallery gallery) {
		contextObjectManager.put("gallery", gallery);
		putContextObjectsForPage(contextObjectManager, gallery);
	}

	/**
	 * Static factory method to provide the required context objects depending on an {@link APoorMansObject}, 
	 * shipping with the {@link PojoHelper}, and the {@link ViewMode}.
	 * 
	 * @param pojoHelper
	 * @param viewMode
	 * @return Map of context objects.
	 */
	public static Map<String, Object> get(final PojoHelper pojoHelper, final ViewMode viewMode) {
		if (pojoHelper == null || viewMode == null)
			throw new IllegalArgumentException("Missed one or more basic parameters!");
		ContextObjectManager contextObjectManager = new ContextObjectManager(pojoHelper, viewMode);
		APoormansObject<?> renderable = pojoHelper.get();
	
		// Initializing depended on the TYPE of the IRenderable.
		if (InstanceUtil.isGallery(renderable)) {
			Gallery gallery = (Gallery) renderable;
			logger.debug("Gallery to render: ".concat(gallery.toString()));
			contextObjectManager.initializeContextObject(IContextObjectGallery.class);
			contextObjectManager.putContextObjectsForGallery(contextObjectManager, gallery);
		} else if (InstanceUtil.isImage(renderable)) {
			Image image = (Image) renderable;
			logger.debug("Image to render: ".concat(image.toString()));
			contextObjectManager.initializeContextObject(IContextObjectGallery.class);
			contextObjectManager.put("image", image);
			contextObjectManager.putContextObjectsForPage(contextObjectManager, image.getParent());
		} else if (InstanceUtil.isPage(renderable)) {
			Page page = (Page) renderable;			
			logger.debug("Page to render: ".concat(page.toString()));
			contextObjectManager.putContextObjectsForPage(contextObjectManager, page);
		} else 
			throw new IllegalArgumentException("Unknown type of PersitentPojo: " + renderable.getClass().getName());
	
		// initialization of the basic context objects
		contextObjectManager.initializeContextObject(IContextObjectNeedPojoHelper.class);
		contextObjectManager.initializeContextObject(IContextObjectNeedViewMode.class);
		
		return contextObjectManager.getContextObjects();
	}
}
