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
package de.thischwa.pmcms.tool.OS;


/**
 * Very basic OS detection!
 *
 * @version $Id: OSDetector.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class OSDetector {
	private static final String osName = System.getProperty("os.name"); 
	private static final String osVersion = System.getProperty("os.version");
	
	public final static OSType getType() {
		if (osName.startsWith("Linux"))
			return OSType.LINUX;
		if (osName.startsWith("Windows"))
			return OSType.WIN;
		if (osName.startsWith("Mac") && osVersion.startsWith("10.")) // don't run with Mac OS classic
			return OSType.MAC;
		throw new RuntimeException("Your OS isn't support by poormans: " + osName + " " + osVersion);
	}
	
	public final static String getOSString() {
		return osName;
	}
}
