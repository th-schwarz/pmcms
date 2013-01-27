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
package de.thischwa.pmcms.tool.file;

import java.io.File;
import java.util.Comparator;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Comparator for generating a traverseable file list e.g. to reduce change directory commands while copying. 
 * The sort order is 1st the full path of the dir an 2nd the name of the file.
 *
 * @version $Id: FileComparator.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class FileComparator implements Comparator<File> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(final File f1, final File f2) {
		if (f1.getAbsolutePath().equals(f2.getAbsolutePath()))
			return 0;
		
		String path1 = FilenameUtils.getFullPath(f1.getAbsolutePath());
		String path2 = FilenameUtils.getFullPath(f2.getAbsolutePath());
		String name1 = FilenameUtils.getName(f1.getAbsolutePath());
		String name2 = FilenameUtils.getName(f2.getAbsolutePath());
		if (path1.equals(path2) || (StringUtils.isBlank(path1) && StringUtils.isBlank(path2)))
			return name1.compareTo(name2);
		String[] pathParts1 = StringUtils.split(FilenameUtils.getFullPathNoEndSeparator(path1), File.separatorChar);
		String[] pathParts2 = StringUtils.split(FilenameUtils.getFullPathNoEndSeparator(path2), File.separatorChar);
		
		if (pathParts1.length < pathParts2.length)
			return -1;
		if (pathParts1.length > pathParts2.length)
			return +1;
		
		int i = 0;
		while (i < pathParts1.length && i < pathParts2.length) {
			if (!pathParts1[i].equals(pathParts2[i]))
				return pathParts1[i].compareTo(pathParts2[i]);
			i++;
		}
		return 0;
	}	
}
