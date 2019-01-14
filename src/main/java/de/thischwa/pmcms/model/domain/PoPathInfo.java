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

import java.io.File;

import de.thischwa.pmcms.conf.InitializationManager;
import de.thischwa.pmcms.conf.PropertiesManager;
import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.domain.pojo.Gallery;
import de.thischwa.pmcms.model.domain.pojo.Site;


/**
 * Static helper methods to provide path based infos of {@link APoormansObject}.
 */
public class PoPathInfo {
	
	private PoPathInfo() {
	}

	/**
	 * @return The path to the export directory of the given site.
	 */
	public static File getSiteExportDirectory(final Site site) {
		return new File(getSiteDirectory(site), getProperty("pmcms.dir.site.export"));
	}

	/**
	 * @return The folder for the image cache path inside the site directory.
	 */
	public static File getSiteImageCacheDirectory(final Site site) {
		return new File(getSiteDirectory(site), getProperty("pmcms.dir.site.imagecache"));
	}
 	
	/**
	 * @return The folder to the configuration directory of the given site.
	 */
	public static File getSiteConfigurationDirectory(final Site site) {
		return new File(getSiteDirectory(site), getProperty("pmcms.dir.site.configuration"));
	}

	/**
	 * @return The directory to the desired site.
	 */	
	public static File getSiteDirectory(final Site site) {
		return getSiteDirectory(site.getUrl());
	}
	
	/**
	 * @return The directory to the desired site-url.
	 */	
	public static File getSiteDirectory(final String url) {
		return new File(InitializationManager.getSitesDir(), url);
	}
	
	public static File getSiteResourceGalleryDirectory(final Site site) {
		return new File(getSiteDirectory(site), getSiteProperty("pmcms.site.dir.resources.gallery"));
	}

	public static File getSiteResourceImageDirectory(final Site site) {
		return new File(getSiteDirectory(site), getSiteProperty("pmcms.site.dir.resources.other"));
	}
	
	public static File getSiteResourceOtherDirectory(final Site site) {
		return new File(getSiteDirectory(site), getSiteProperty("pmcms.site.dir.resources.other"));
	}

	public static File getSiteResourceLayoutDirectory(final Site site) {
		return new File(getSiteDirectory(site), getSiteProperty("pmcms.site.dir.resources.layout"));
	}

	public static File getSiteResourceGalleryExportDirectory(final Site site) {
		return new File(getSiteExportDirectory(site), getSiteProperty("pmcms.site.dir.resources.gallery"));
	}
	
	/**
	 * @return The directory to the desired {@link Gallery}.
	 */
	public static File getSiteGalleryDirectory(Gallery gallery) {
		return new File(getSiteResourceGalleryDirectory(PoInfo.getSite(gallery)), gallery.getName());
	}
	
	private static String getProperty(final String key) {
		return InitializationManager.getBean(PropertiesManager.class).getProperty(key);
	}
	private static String getSiteProperty(final String key) {
		return InitializationManager.getBean(PropertiesManager.class).getSiteProperty(key);
	}
}
