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
package de.thischwa.pmcms.tool.connection;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * Main object for the tree which handles all files to upload. Measurements such number of files and bytes are counted here!
 *
 * @author Thilo Schwarz
 */
public class UploadTree {
	private static Logger logger = Logger.getLogger(UploadTree.class);
	public static final char PATH_SEPARATOR = '/';

	private UploadTreeNode root = new UploadTreeNode();
	
	/** Total size in bytes from the underlying files. */
	private long totalSizeInBytes = 0;

	private int numberOfFiles = 0;


	public void add(String path, File file) {
		try {
			String name = getNameFromPath(path);
			long bytes = file.length();
			InputStream in = new BufferedInputStream(new FileInputStream(file));

			UploadObject uo = new UploadObject(name, in, bytes);
			root.add(path, uo);
			numberOfFiles++;
			totalSizeInBytes += bytes;
		} catch (Exception e) {
			// should never be happend
			logger.error("While constructing an UploadObject: " + e.getMessage(), e);
		}
	}


	public int getNumberOfFiles() {
		return numberOfFiles;
	}
	public long getTotalSizeInBytes() {
		return totalSizeInBytes;
	}
	public UploadTreeNode getRootNode() {
		return root;
	}
	

	protected static String getNameFromPath(String path) {
		if(!path.contains(""+PATH_SEPARATOR))
			return path;
		return path.substring(path.lastIndexOf(UploadTree.PATH_SEPARATOR)+1, path.length());
	}
}
