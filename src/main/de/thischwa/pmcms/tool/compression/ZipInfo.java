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
package de.thischwa.pmcms.tool.compression;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Container object to provide infos about the zip entries.
 *
 * @author Thilo Schwarz
 */
public class ZipInfo {

	private ZipFile zipFile;
	
	private Map<String, ZipEntry> zipEntryInfo;
	
	public ZipInfo(final File file) throws ZipException, IOException, IllegalArgumentException {
		if (!file.exists())
			throw new IllegalArgumentException(String.format("Zip file [%s] doesn't exists.", file.getPath()));
		this.zipFile = new ZipFile(file);
		zipEntryInfo = new HashMap<String, ZipEntry>();
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry zipEntry = entries.nextElement();
			zipEntryInfo.put(zipEntry.getName(), zipEntry);
		}
	}
	
	public Set<String> getEntryKeys() {
		return zipEntryInfo.keySet();
	}
	
	public ZipEntry getEntry(final String key) {
		return zipEntryInfo.get(key);
	}
	
	public void removeEntry(final String key) {
		zipEntryInfo.remove(key);
	}
	
	public InputStream getInputStream(final String key) throws IllegalArgumentException, IOException {
		ZipEntry zipEntry = zipEntryInfo.get(key);
		if (zipEntry == null)
			throw new IllegalArgumentException("Couldn't find ZipEntry named: " + key);
		return getInputStream(zipEntry);
	}
	
	public InputStream getInputStream(final ZipEntry entry) throws IOException, IllegalArgumentException {
		if (!zipEntryInfo.values().contains(entry))
			throw new IllegalArgumentException("ZipEntry doesn't exists in this zip file!");
		return zipFile.getInputStream(entry);
	}
}
