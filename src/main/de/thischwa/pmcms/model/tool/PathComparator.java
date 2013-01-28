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
package de.thischwa.pmcms.model.tool;

import java.util.Comparator;

import org.apache.commons.lang.StringUtils;

/**
 * {@link Comparator} to sort deeper paths to the begin of the list. Useful get the correct order for deletion. 
 *
 * @version $Id: PathComparator.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class PathComparator implements Comparator<String> {

	@Override
	public int compare(String str1, String str2) {
		int count1 = StringUtils.countMatches(str1, "/");
		int count2 = StringUtils.countMatches(str2, "/");
		if(count1 == count2)
			return 0;
		if(count1 < count2)
			return 1;
		return -1;
	}
}