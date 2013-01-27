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
package de.thischwa.pmcms.tool.compression;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Just for encapsulating the zip compression stuff.
 * 
 * @version $Id: Zip.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class Zip {

	/**
	 * Static method to compress all files based on the ImputStream in 'entries' into 'zip'. 
	 * Each entry has a InputStream and its String representation in the zip.
	 * 
	 * @param zip The zip file. It will be deleted if exists. 
	 * @param entries Map<File, String>
	 * @param monitor Must be initialized correctly by the caller.
	 * @throws IOException
	 */
	public static void compress(final File zip, final Map<InputStream, String> entries, final IProgressMonitor monitor) throws IOException {
		if (zip == null || entries == null || CollectionUtils.isEmpty(entries.keySet()))
			throw new IllegalArgumentException("One ore more parameters are empty!");
		if (zip.exists())
			zip.delete();
		else if (!zip.getParentFile().exists())
			zip.getParentFile().mkdirs();

		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zip)));
		out.setLevel(Deflater.BEST_COMPRESSION);
		try {
			for (InputStream inputStream : entries.keySet()) {
				// skip beginning slash, because can cause errors in other zip apps
				ZipEntry zipEntry = new ZipEntry(skipBeginningSlash(entries.get(inputStream))); 
				out.putNextEntry(zipEntry);
				IOUtils.copy(inputStream, out);
				out.closeEntry();
				inputStream.close();
				if (monitor != null)
					monitor.worked(1);
			}
		} finally { // cleanup
			IOUtils.closeQuietly(out);
		}
	}
	
	public static void compress(final File zip, final Map<InputStream, String> entries) throws IOException {
		compress(zip, entries, null);
	}
	
	/**
	 * A wrapper to {@link #compress(File, Map, IProgressMonitor)}.
	 * 
	 * @param zip
	 * @param entries
	 * @param monitor Must be initialized by the caller.
	 * @throws IOException
	 */
	public static void compressFiles(final File zip, final Map<File, String> entries, final IProgressMonitor monitor) throws IOException {
		if (zip == null || entries == null || CollectionUtils.isEmpty(entries.keySet()))
			throw new IllegalArgumentException("One ore more parameters are empty!");
		Map<InputStream, String> newEntries = new HashMap<InputStream, String>(entries.size());
		for (File file : entries.keySet()) {
			newEntries.put(new FileInputStream(file), entries.get(file));
		}
		compress(zip, newEntries, monitor);
	}

	/**
	 * Static method to compress all files in 'entries' into 'zip'. Each entry has a File and its String representation in the zip.
	 * Just a wrapper to {@link #compress(File, Map)}.
	 * 
	 * Usage:
	 * <pre>
	 * Map&lt;File, String&gt; entries = new HashMap&lt;File, String&gt;();
	 * entries.put(new File(&quot;/tmp/db.xml&quot;), &quot;data/db.xml&quot;);
	 * ZIP.compress(new File(&quot;/tmp/test.zip&quot;), entries);
	 * </pre>
	 * 
	 * @param zip The zip file. It will be deleted if exists. 
	 * @param entries Map<File, String>
	 * @throws IOException 
	 */
	public static void compressFiles(final File zip, final Map<File, String> entries) throws IOException {
		compressFiles(zip, entries, null);
	}
	
	private static String skipBeginningSlash(final String string) {
		return (StringUtils.isNotBlank(string) && string.startsWith("/")) ? string.substring(1) : string;
	}

	/**
	 * @return Instance of {@link ZipInfo}.
	 * 
	 * @throws IOException
	 */
	public static ZipInfo getEntryInfo(final File zip) throws IOException {
		return new ZipInfo(zip);
	}

	/**
	 * Method to extract all {@link ZipInfo}s into 'destDir'. Inner directory structure will be copied.
	 * 
	 * @param destDir
	 * @param zipInfo
	 * @param monitor must be initialized by the caller.
	 * @throws IOException
	 */
	public static void extract(final File destDir, final ZipInfo zipInfo, final IProgressMonitor monitor) throws IOException {
		if (!destDir.exists())
			destDir.mkdirs();

		for (String key : zipInfo.getEntryKeys()) {
			ZipEntry entry = zipInfo.getEntry(key);
			InputStream in = zipInfo.getInputStream(entry);
			File entryDest = new File(destDir, entry.getName());
			entryDest.getParentFile().mkdirs();
			if (!entry.isDirectory()) {
				OutputStream out = new FileOutputStream(new File(destDir, entry.getName()));
				try {
					IOUtils.copy(in, out);
					out.flush();
					if (monitor != null)
						monitor.worked(1);
				} finally { // cleanup
					IOUtils.closeQuietly(in);
					IOUtils.closeQuietly(out);
				}
			}
		}
		if (monitor != null)
			monitor.done();
	}
		
	/**
	 * @return An InputStream of the requested entry.
	 */
	public static InputStream getInputStream(final File zip, final String entryName) throws IOException {
		ZipInfo zipInfo = getEntryInfo(zip);
		return zipInfo.getInputStream(entryName);
	}
	
	public static void closeQuietly(final ZipFile zipFile) {
		try {
			if (zipFile != null)
				zipFile.close();
		} catch (IOException e) {
			// ignored
		}
	}
}
