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
package de.thischwa.pmcms.tool;

import java.io.File;
import java.io.IOException;


import com.twmacinta.util.MD5;

import de.thischwa.pmcms.exception.FatalException;

/**
 * Utility class to generate and compare checksums of files. It's only a simple wrapper object. So, it's easier to change the implementation
 * of the checksum calculation.
 * 
 * @author Thilo Schwarz
 */
public class Fingerprint {

	static {
		MD5.initNativeLibrary(false);
	}

	public String get(final File file) {
		String retVal = null;
		try {
			retVal = format(MD5.getHash(file));
		} catch (IOException e) {
			throw new FatalException("While getting hash: " + e.getMessage(), e);
		}
		return retVal;
	}

	/**
	 * Comparing of 2 fingerprints.
	 * 
	 * @return True, if the fingerprints are equal, otherwise false.
	 */
	public static boolean compare(final String fingerprint1, final String fingerprint2) {
		return fingerprint1.equals(fingerprint2);
	}

	private static String format(byte[] hash) {
		StringBuffer sb = new StringBuffer(hash.length * 2);
		for (int i = 0; i < hash.length; i++)
			sb.append(String.format("%02x", hash[i]));
		return sb.toString();
	}
}
