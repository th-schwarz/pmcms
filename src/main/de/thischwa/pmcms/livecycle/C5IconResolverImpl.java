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

import java.nio.file.Paths;

import codes.thischwa.c5c.requestcycle.impl.FilemanagerIconResolver;
import codes.thischwa.c5c.PropertiesLoader;
import codes.thischwa.c5c.util.Path;
import de.thischwa.pmcms.Constants;

/**
 * It resolves the icons of the filemanager.<p>
 * 
 * The path of the filemanager will be redirected to the directory of the application.
 * That's necessary because the working directory of the server is the data directory. 
 */
public class C5IconResolverImpl extends FilemanagerIconResolver {
	
	@Override
	protected void collectIcons(String iconPath, java.nio.file.Path iconFolder, Path urlPath) {
		java.nio.file.Path iconFSPath = Paths.get(Constants.APPLICATION_DIR.getAbsolutePath(), iconPath);
		Path fileSystemPath = new Path(PropertiesLoader.getFilemanagerPath());
		fileSystemPath.addFolder(iconPath);
		super.collectIcons(iconPath, iconFSPath, urlPath);
	}
}
