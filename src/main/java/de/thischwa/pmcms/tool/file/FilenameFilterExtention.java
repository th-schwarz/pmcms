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
package de.thischwa.pmcms.tool.file;

import java.io.File;
import java.io.FilenameFilter;

/**
 * {@link FilenameFilter} which accepts files with the submitted extension only. Lower and upper writing isn't respected.
 * 
 * @author Thilo Schwarz
 */
public class FilenameFilterExtention implements FilenameFilter {
	private final String fileExtention;

	public FilenameFilterExtention(final String extention) {
		fileExtention = extention.toLowerCase();
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	@Override
	public boolean accept(final File dir, final String name) {
		return name.toLowerCase().endsWith(fileExtention);
	}

}
