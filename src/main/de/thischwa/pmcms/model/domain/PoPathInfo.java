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
package de.thischwa.pmcms.model.domain;

import java.io.File;

import de.thischwa.c5c.resource.Extension;
import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.configuration.PropertiesManager;
import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.domain.pojo.Gallery;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.wysisygeditor.CKResourceTool;


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
	 * @return The base path for common resources inside the export directory of a site (file, media, flash).
	 */
	public static File getSiteExportResourceDirectory(final Site site) {
		return new File(getSiteExportDirectory(site), getSiteProperty("pmcms.site.dir.export.resources"));
	}
	
	/**
	 * @return The export path of a resource TYPE. Special path for the TYPE image is respected.
	 */
	public static File getSiteExportResourceDirectory(final Site site, final Extension ext) {
		 if (ext == Extension.IMAGE)
			 return new File(getSiteExportDirectory(site), CKResourceTool.getDir(ext));
		 else
			 return new File(getSiteExportResourceDirectory(site), CKResourceTool.getDir(ext));
	}

	/**
	 * @return The folder for the image cache path inside the site directory.
	 */
	public static File getSiteImageCacheDirectory(Site site) {
		return new File(getSiteDirectory(site), getProperty("pmcms.dir.site.imagecache"));
	}
 	
	/**
	 * @return The folder to the configuration directory of the given site.
	 */
	public static File getSiteConfigurationDirectory(Site site) {
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

	/**
	 * @return The directory to the resource directory of the type {@link Extension}.
	 */	
	public static File getSiteResourceDirectory(final Site site, final Extension ext) {
		return new File(getSiteDirectory(site), CKResourceTool.getDir(ext));
	}
	
	public static File getSiteGalleryDirectory(final Site site) {
		return new File(getSiteDirectory(site), getSiteProperty("pmcms.site.dir.resources.gallery"));
	}
	
	/**
	 * @return The directory to the desired {@link Gallery}.
	 */
	public static File getSiteGalleryDirectory(Gallery gallery) {
		return new File(getSiteGalleryDirectory(PoInfo.getSite(gallery)), gallery.getName());
	}
	
	private static String getProperty(final String key) {
		return InitializationManager.getBean(PropertiesManager.class).getProperty(key);
	}
	private static String getSiteProperty(final String key) {
		return InitializationManager.getBean(PropertiesManager.class).getSiteProperty(key);
	}
}
