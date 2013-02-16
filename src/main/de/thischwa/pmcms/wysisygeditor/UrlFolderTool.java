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
package de.thischwa.pmcms.wysisygeditor;

import de.thischwa.c5c.resource.Extension;
import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.configuration.PropertiesManager;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.tool.Utils;

/**
 * Helper tool to get special folders for building links in html. Folders are always ends with an separator char.
 */
public class UrlFolderTool {

	private static PropertiesManager pm = InitializationManager.getBean(PropertiesManager.class);

	static String getSitesFolder() {
		return Utils.join(pm.getProperty("pmcms.dir.sites"), Constants.SEPARATOR);
	}
	
	static String getSiteFolder(Site site) {
		return Utils.join(getSitesFolder(), site.getUrl(), Constants.SEPARATOR);
	}
	
	static String getResourceFolderForExport(Extension ext) {
		if(ext == Extension.IMAGE)
			return Utils.join(CKResourceTool.getDir(ext), Constants.SEPARATOR);
		return Utils.join(pm.getSiteProperty("pmcms.site.dir.export.resources"), Constants.SEPARATOR, CKResourceTool.getDir(ext), Constants.SEPARATOR);
	}
	
	static String getImageFolder() {
		return Utils.join(pm.getSiteProperty("pmcms.site.dir.resources.image"), Constants.SEPARATOR);		
	}
	
	static String getImageCasheFolder() {
		return Utils.join(pm.getSiteProperty("pmcms.site.dir.imagecache"), Constants.SEPARATOR);
	}

	static String stripUrlSiteFolder(String srcTag) {
		String temp = new String(srcTag);
		if(temp.startsWith("/"))
			temp = temp.substring(1);
		String urlSiteFolder = Constants.LINK_IDENTICATOR_SITE_RESOURCE.concat("/");
		if(temp.startsWith(urlSiteFolder))
			temp = temp.substring(urlSiteFolder.length());
		return temp;
	}
}
