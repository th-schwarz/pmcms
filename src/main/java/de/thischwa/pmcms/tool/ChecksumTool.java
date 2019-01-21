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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.digester.Digester;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.core.runtime.IProgressMonitor;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.exception.FatalException;
import de.thischwa.pmcms.tool.compression.Zip;
import de.thischwa.pmcms.tool.file.FileComparator;

/**
 * Static helper tool to handle and generate the checksums of files.
 * 
 * @author Thilo Schwarz
 */
public class ChecksumTool {
	private static Logger logger = Logger.getLogger(ChecksumTool.class);

	/**
	 * Wrapper to {@link #get(InputStream)}.
	 */
	public static Map<String, String> get(final File checksumXmlFile) {
		try {
			return get(new BufferedInputStream(new FileInputStream(checksumXmlFile)));
		} catch (Exception e) {
			throw new FatalException("Error while reading checksum file: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Reading the checksums directly from a zip file.
	 */
	public static Map<String, String> getFromZip(final File zip, final String filename) {
		try {
			return get(Zip.getInputStream(zip, filename));
		} catch (IOException e) {
			throw new FatalException("Error while reading checksum zip file: " + e.getMessage(), e);
		}
	}
	

	/**
	 * Reading the checksum file (xml) from an InputSream and put it in a Map.
	 * 
	 * @param in
	 * @return A Map with the file name as key and the checksum as value.
	 */
	public static Map<String, String> get(final InputStream in) {
		Digester digester = new Digester();
		digester.setValidating(false);
		digester.addObjectCreate("checksums", HashMap.class);
		digester.addCallMethod("checksums/file", "put", 2);
		digester.addCallParam("checksums/file/name", 0);
		digester.addCallParam("checksums/file/checksum", 1);
		try {
			@SuppressWarnings("unchecked")
			Map<String, String> checksums = (Map<String, String>) digester.parse(in);
			return checksums;
		} catch (Exception e) {
			throw new FatalException("Error while parsing checksums: " + e.getMessage(), e);
		}
	}

	/**
	 * Wrapper to {@link #get(Collection, String, IProgressMonitor)}.
	 */
	public static Map<String, String> get(final Collection<File> files, final String pathPrefix) {
		return get(files, pathPrefix, null);
	}

	/**
	 * Converting a set of files to a map. The path of the file is the key of the map and is net of 'pathPrefix'.
	 * 
	 * @param files
	 * @param pathPrefix
	 *            has to be an absolute path !!!
	 * @param monitor
	 * @return Map with files
	 */
	public static Map<String, String> get(final Collection<File> files, final String pathPrefix, final IProgressMonitor monitor) {
		if (StringUtils.isBlank(pathPrefix))
			throw new IllegalArgumentException("Path prefix shouldn't be null!");
		Map<String, String> hashes = new HashMap<String, String>(files.size());
		Fingerprint fingerprint = new Fingerprint();
		for (File file : files) {
			String name = file.getAbsolutePath().toString().substring(pathPrefix.length() + 1).replace(File.separatorChar, '/');
			try {
				hashes.put(name, fingerprint.get(file));
			} catch (Exception e) {
				throw new FatalException("While getting the checksum: " + e.getMessage(), e);
			}
			if (monitor != null)
				monitor.worked(1);
		}
		return hashes;
	}

	/**
	 * Converting a Map with the file checksums to a dom.
	 */
	public static Document getDomChecksums(final Map<String, String> checksums) {
		Document dom = DocumentHelper.createDocument();
		dom.setXMLEncoding(Constants.STANDARD_ENCODING);
		Element root = dom.addElement("checksums");
		for (String name : checksums.keySet()) {
			Element fileElement = root.addElement("file");
			Element nameElement = fileElement.addElement("name");
			nameElement.addCDATA(name);
			Element hashElement = fileElement.addElement("checksum");
			hashElement.addText(checksums.get(name));
		}
		return dom;
	}

	/**
	 * Comparing the Map with the file checksums from the server with the local one.
	 * 
	 * @return A sorted collection of files, which have to be copied to the server.
	 */
	public static Collection<File> merge(final String pathPart, final Map<String, String> localChecksums,
			final Map<String, String> serverChecksums) {
		List<File> files = new ArrayList<File>();
		for (String key : serverChecksums.keySet()) {
			String localChecksum = localChecksums.get(key);
			if (StringUtils.isNotBlank(localChecksum)) {
				String serverChecksum = serverChecksums.get(key);
				if (!StringUtils.equals(serverChecksum, localChecksum)) {
					files.add(new File(pathPart, key).getAbsoluteFile());
					logger.debug("Merge is diff: ".concat(key).concat(" cause ").concat(localChecksum).concat(" ").concat(serverChecksum));
				}
			}
		}
		for (String key : localChecksums.keySet())
			if (!serverChecksums.containsKey(key)) {
				files.add(new File(pathPart, key).getAbsoluteFile());
				logger.debug("Merge is new: " + key);
			}

		Collections.sort(files, new FileComparator());
		return files;
	}
}
