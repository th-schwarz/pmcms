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
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.collections.CollectionUtils;

/**
 * {@link FilenameFilter} which ignores all files/dirs in the submitted collection.
 *
 * @version $Id: FilenameFilterIgnoreFiles.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class FilenameFilterIgnoreFiles implements FilenameFilter {
	private Collection<String> filenamesToIgnore;
	
	public FilenameFilterIgnoreFiles(final Collection<File> filesToIgnore) {
		if (CollectionUtils.isEmpty(filesToIgnore))
			throw new IllegalArgumentException("Collection with files to skip shouldn't be null or empty!");
		this.filenamesToIgnore = new HashSet<String>(filesToIgnore.size());
		for (File file : filesToIgnore) {
			filenamesToIgnore.add(file.getAbsolutePath());
		}
	}

	/* (non-Javadoc)
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	@Override
	public boolean accept(final File dir, final String name) {
		File file = new File(dir, name);
		return (!(filenamesToIgnore.contains(file.getAbsolutePath())));
	}
}
