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
package de.thischwa.pmcms.tool.connection;

import org.apache.commons.lang.StringUtils;

/**
 * String utility to split the target path into name and directory. Beginning slash will be ignored!
 * 
 * @version $Id: PathAnalyzer.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class PathAnalyzer {
	private String dir;
	private String name;

	public PathAnalyzer(final String targetPath) {
		if (StringUtils.isBlank(targetPath))
			throw new IllegalArgumentException("TargetPath shouldn't be null!");
		int pos = StringUtils.lastIndexOf(targetPath, '/');
		if (pos == -1 || pos == 0) {
			dir = "";
			if (pos == -1)
				name = targetPath;
			else
				name = targetPath.substring(1);
		} else {
			dir = targetPath.substring(0, StringUtils.lastIndexOf(targetPath, '/'));
			name = targetPath.substring(StringUtils.lastIndexOf(targetPath, '/'), targetPath.length());
			if (name.startsWith("/"))
				name = name.substring(1);
			if (dir.startsWith("/"))
				dir = dir.substring(1);
		}
	}

	public String getDir() {
		return dir;
	}

	public String getName() {
		return name;
	}
	
	public String getPath() {
		return (dir.equals("")) ? name : (dir + '/' + name);
	}
}
