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
package de.thischwa.pmcms.wysisygeditor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.thischwa.c5c.resource.Extension;
import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.tool.file.FileTool;

/**
 * Static helper tool for CKEditor resources.
 *
 * @version $Id: CKResourceTool.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class CKResourceTool {
	private static Map<Extension, String> folders = new HashMap<Extension, String>(4);	
	
	static {
		String folderOther = InitializationManager.getProperty("poormans.site.dir.resources.other");
		folders.put(Extension.IMAGE, InitializationManager.getProperty("poormans.site.dir.resources.image"));
		folders.put(Extension.ARCHIVE, folderOther);
		folders.put(Extension.DOC, folderOther);
		folders.put(Extension.OTHER, folderOther);
	}

	public static String getDir(final Extension type) {
		String dir = folders.get(type);
		if (dir == null)
			throw new IllegalArgumentException("Unknown resource file TYPE!");
		return dir;
	}
	
	public static Extension getResourceType(final File file) {
		String ext = FileTool.getExtension(file);
		return getResourceType(ext);
	}
	
	public static Extension getResourceType(String ext) {
		if(StringUtils.isBlank(ext))
			return null;
		if(InitializationManager.isImageExtention(ext))
			return Extension.IMAGE;
		if(Extension.ARCHIVE.isAllowedExtension(ext))
			return Extension.ARCHIVE;
		if(Extension.DOC.isAllowedExtension(ext))
			return Extension.DOC;
		return Extension.OTHER;
	}
		
	public static ICKResource getResource(final Extension ext, final Site site) {
		if (ext == Extension.IMAGE)
			return new CKImageResource(site);
		else 
			return new CKFileResource(site, ext);
	}
}
