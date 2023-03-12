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
package de.thischwa.pmcms.tool.OS;


/**
 * Very basic OS detection!
 *
 * @author Thilo Schwarz
 */
public class OSDetector {
	private static final String osName = System.getProperty("os.name"); 
	private static final String osVersion = System.getProperty("os.version");
	private static final String jvmArch = System.getProperty("os.arch").toLowerCase();
	
	public enum Type {
		WIN, LINUX, MAC;
		
		private boolean is64Bit = false;
		
		public boolean is64Bit() {
			return is64Bit;
		}
	}

	public final static Type getType() {
		Type type = detect();
		if(jvmArch.contains("64"))
			type.is64Bit = true;
		return type;
	}
	
	private final static Type detect() {
		if (osName.startsWith("Linux"))
			return Type.LINUX;
		if (osName.startsWith("Windows"))
			return Type.WIN;
		if (osName.startsWith("Mac"))
			return Type.MAC;
		throw new RuntimeException("Your OS isn't support by poormans: " + osName + " " + osVersion);
	}
	
	public final static String getOSString() {
		return osName;
	}
}
