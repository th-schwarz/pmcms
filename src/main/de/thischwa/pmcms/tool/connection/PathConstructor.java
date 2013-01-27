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
package de.thischwa.pmcms.tool.connection;

import org.apache.commons.lang.StringUtils;

/**
 * Simple string tool to construct paths like a/b/c.
 *
 * @version $Id: PathConstructor.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class PathConstructor {
	private static char separator = '/';
	private StringBuilder sb = new StringBuilder();
	
	
	public PathConstructor setAbsolute() {
		if (!startWithSeparator())
			sb.insert(0, separator);
		return this;
	}
	
	public PathConstructor add(final String path) {
		if (StringUtils.isBlank(path) || path.equals(""+separator))
			return this;
		if (sb.length() == 0) {
			sb.append(path);
			return this;
		}
		String tempString;
		if (path.startsWith(""+separator))
			tempString = path.substring(1);
		else 
			tempString = path;
		if (!endWithSeparator())
			sb.append(separator);
		sb.append(tempString);
		return this;
	}
	
	public boolean isAbsolute() {
		return startWithSeparator();
	}
	
	public String[] getDirs() {
		if (sb.length() == 0 || sb.toString().equals(""+separator))
			return new String[]{};
		String string = sb.toString();
		if (!string.contains(""+separator))
			return new String[]{string};
		return StringUtils.split(string, separator);
	}
	
	private boolean startWithSeparator() {
		return (sb.length() == 0) ? false : (sb.charAt(0) == separator);
	}
	
	private boolean endWithSeparator() {
		return (sb.length() == 0) ? false : (sb.charAt(sb.length()-1) == separator);		
	}
	
	/* Constructs the whole path.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
	    return sb.toString();
	}
}
