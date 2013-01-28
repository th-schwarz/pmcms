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
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.tool.Utils;

/**
 * Helper tool to get special folders for building links in html. Folders are always ends with an separator char.
 *
 * @version $Id: LinkFolderTool.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class LinkFolderTool {

	public static final char SEPARATOR_CHAR = '/';
	public static final String SEPARATOR = String.valueOf(SEPARATOR_CHAR);
	
	public static String getSitesFolder() {
		return Utils.join(InitializationManager.getProperty("pmcms.dir.sites"), SEPARATOR);
	}
	
	public static String getSiteFolder(Site site) {
		return Utils.join(getSitesFolder(), site.getUrl(), SEPARATOR);
	}
	
	public static String getResourceFolderForExport(Extension ext) {
		if(ext == Extension.IMAGE)
			return Utils.join(CKResourceTool.getDir(ext), SEPARATOR);
		return Utils.join(InitializationManager.getProperty("pmcms.site.dir.export.resources"), SEPARATOR, CKResourceTool.getDir(ext), SEPARATOR);
	}
	
	public static String getImageFolder() {
		return Utils.join(InitializationManager.getProperty("pmcms.site.dir.resources.image"), SEPARATOR);		
	}
	
	public static String getImageCasheFolder() {
		return Utils.join(InitializationManager.getProperty("pmcms.site.dir.imagecache"), SEPARATOR);
	}

	public static String stripUrlSiteFolder(String srcTag) {
		String temp = new String(srcTag);
		if(temp.startsWith("/"))
			temp = temp.substring(1);
		String urlSiteFolder = Constants.LINK_IDENTICATOR_SITE_RESOURCE.concat("/");
		if(temp.startsWith(urlSiteFolder))
			temp = temp.substring(urlSiteFolder.length());
		return temp;
	}
}
