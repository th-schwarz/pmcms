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
package de.thischwa.pmcms.livecycle;

import javax.servlet.ServletContext;

import de.thischwa.c5c.requestcycle.Context;
import de.thischwa.c5c.requestcycle.UserPathBuilder;
import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.model.domain.PoPathInfo;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.tool.PathTool;

/**
 * Implementation of the CKeditor's UserBathBuilder to build the correct user path for different {@link Site}s.
 *
 * @version $Id: UserPathBuilderImpl.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class UserPathBuilderImpl implements UserPathBuilder {

	@Override
	public String getServerPath(String urlPath, Context ctx, ServletContext servletContext) {
		String userfilesPath = InitializationManager.getProperty("pmcms.filemanager.userfiles");
		String cleanedUrlPath = urlPath.equals(userfilesPath) ? "" : urlPath.substring(userfilesPath.length()-1);
		Site site = getSite();
		String path = (site != null) 
				? PathTool.getURLFromFile(PoPathInfo.getSiteDirectory(site).getAbsolutePath(), false) : null;
		return path.concat(cleanedUrlPath);
	}	
	
	private Site getSite() {
		SiteHolder siteHolder = InitializationManager.getBean(SiteHolder.class);
		Site site = siteHolder.getSite();
		return site;
	}

}