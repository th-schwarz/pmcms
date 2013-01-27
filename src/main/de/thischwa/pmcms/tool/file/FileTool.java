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
package de.thischwa.pmcms.tool.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.tool.Utils;
import de.thischwa.pmcms.view.context.object.tagtool.ImageTagTool;

/**
 * Helper tool for file stuff.
 * 
 * @version $Id: FileTool.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class FileTool {
	private static final Logger logger = Logger.getLogger(FileTool.class);
	private static final int maxCopyTries = 10;
	private static Map<Integer, String> specialCharReplacement;
	
	static {
		specialCharReplacement = new HashMap<Integer, String>();
		specialCharReplacement.put(Integer.valueOf(String.valueOf("\u00E4").charAt(0)), "ae");
		specialCharReplacement.put(Integer.valueOf(String.valueOf("\u00FC").charAt(0)), "ue");
		specialCharReplacement.put(Integer.valueOf(String.valueOf("\u00F6").charAt(0)), "oe");
		specialCharReplacement.put(Integer.valueOf(String.valueOf("\u00DF").charAt(0)), "ss");
		specialCharReplacement.put(Integer.valueOf(' '), "_");
	}
	
	/**
	 * Deletes all submitted files. Directories will be ignored.
	 * 
	 * @param files
	 *            {@link File}s to delete.
	 */
	public static void deleteFiles(final Collection<File> files) {
		for (File file : files)
			if (file.isFile())
				if (file.delete())
					logger.debug(String.format("Deleted file: %s", file.getPath()));
				else
					logger.warn(String.format("File couldn't delete: %s", file.getPath()));
	}

	/**
	 * Collects all files in <code>startDir</code> and skips all files in <code>toIgnore</code>.
	 * 
	 * @param startDir
	 *            Directory to start with.
	 * @param toIgnore
	 *            An optional collection of files to ignore, can be null or empty.
	 * @return Collection of files. It's ordered in a way that can be used to traverse directories.
	 */
	public static Collection<File> collectFiles(final File startDir, final Collection<File> toIgnore) {
		if (!startDir.exists())
			throw new IllegalArgumentException("Path [" + startDir + "] doesn't exists!");
		List<File> fileCollection = new ArrayList<File>();
		FilenameFilter filter = null;
		if (CollectionUtils.isNotEmpty(toIgnore))
			filter = new FilenameFilterIgnoreFiles(toIgnore);
		collect(startDir, fileCollection, filter);
		return fileCollection;
	}

	/**
	 * Collects all files in <code>startDir</code> with respect of the {@link FilenameFilter} <code>filter</code>.
	 * 
	 * @param startDir
	 *            Directory to start with.
	 * @param filter
	 *            An optional {@link FilenameFilter}, it can be null.
	 * @return Collection of files. It's ordered in a way that can be used to traverse directories.
	 */
	public static Collection<File> collectFilteredFiles(final File startDir, final FilenameFilter filter) {
		if (filter == null)
			throw new IllegalArgumentException("Filter shouldn't be null!");
		if (!startDir.exists())
			throw new IllegalArgumentException("Path [" + startDir + "] doesn't exists!");
		List<File> fileCollection = new ArrayList<File>();
		collect(startDir, fileCollection, filter);
		return fileCollection;
	}

	/**
	 * A wrapper to {@link #collectFiles(File, Collection)}.
	 */
	public static Collection<File> collectFiles(final File startDir) {
		return collectFiles(startDir, null);
	}

	/**
	 * Recursive file collector.
	 * 
	 * @param dir
	 *            Directory to start with.
	 * @param filesToCollect
	 *            {@link List} to hold the collected {@link File}s
	 * @param filter
	 *            Optional {@link FilenameFilter}, can be null.
	 */
	private static void collect(final File dir, List<File> filesToCollect, final FilenameFilter filter) {
		List<File> dirList = new ArrayList<File>();
		List<File> fileList = new ArrayList<File>();
		File[] files = (filter != null) ? dir.listFiles(filter) : dir.listFiles();

		for (File file : files) {
			if (file.isDirectory())
				dirList.add(file.getAbsoluteFile());
			else
				fileList.add(file.getAbsoluteFile());
		}
		Collections.sort(fileList);
		Collections.sort(dirList);

		filesToCollect.addAll(fileList);

		for (File subdir : dirList)
			collect(subdir.getAbsoluteFile(), filesToCollect, filter);
	}

	/**
	 * Just a wrapper to {@link #copyToDirectoryUnique(File, File)}.
	 * 
	 * @param filesToCopy
	 * @param directory
	 * @return Collection of the copied files.
	 * @throws IOException
	 */
	public static List<File> copyToDirectoryUnique(final Collection<File> filesToCopy, final File directory) throws IOException {
		if (filesToCopy.isEmpty())
			return null;
		List<File> copiedFiles = new ArrayList<File>(filesToCopy.size());
		for (File srcFile : filesToCopy)
			copiedFiles.add(copyToDirectoryUnique(srcFile, directory));
		return copiedFiles;
	}

	/**
	 * Copies 'srcFile' to 'destDir'. If there is a file with the same name, 'srcFile' will be renamed. E.g.:<br>
	 * <code><pre>
	 * filename.ext -&gt; filename_1.ext
	 * </pre></code>
	 * The extension will be converted to lower case.
	 * 
	 * @param srcFile
	 * @param destDir
	 * @return the dest file.
	 * @throws IOException
	 */
	public static File copyToDirectoryUnique(final File srcFile, final File destDir) throws IOException {
		String basename = FilenameUtils.getBaseName(srcFile.getAbsolutePath());
		String extension = StringUtils.lowerCase(FilenameUtils.getExtension(srcFile.getAbsolutePath()));
		File destFile = getUniqueFile(destDir, normalizeFileName(basename) + '.' + extension);
		FileUtils.copyFile(srcFile, destFile);
		return destFile;
	}

	/**
	 * Getter for an unique file in <code>directory</code>. If <code>fileName</code> exists, a number will be add on the base name. E.g.:<br>
	 * <code>basename_1.ext</code>
	 * 
	 * @param directory
	 * @param fileName
	 * @return An unique {@link File} based on <code>fileName</code>.
	 */
	private static File getUniqueFile(final File directory, final String fileName) {
		File tempFile = new File(directory, fileName);
		String name = FilenameUtils.getBaseName(tempFile.getAbsolutePath());
		String extension = FilenameUtils.getExtension(tempFile.getAbsolutePath());
		if (extension.length() > 1)
			extension = ".".concat(extension.toLowerCase());
		int i = 0;
		while (tempFile.exists()) {
			String newName = name.concat("_").concat(String.valueOf(i)).concat(extension);
			tempFile = new File(directory, newName);
			i++;
		}
		return tempFile;
	}

	/**
	 * Replaces the german special chars and spaces in 'filename'.
	 * 
	 * @param filename
	 * @return normalized file name TODO Restrict special chars, replace unknown with something like that: -sp-
	 */
	public static String normalizeFileName(final String filename) {
		StringBuilder sb = new StringBuilder();
		String tmp = filename.toLowerCase();
		tmp = Normalizer.normalize(tmp, Normalizer.Form.NFC);
		for(char c : tmp.toCharArray()) {
			String replaceStr = specialCharReplacement.get(Integer.valueOf(c));
			if(replaceStr != null)
				sb.append(replaceStr);
			else if(Constants.ALLOWED_CHARS_FOR_FILES.contains(c+""))
				sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * Return the content of a file as string. Encoding is utf-8.
	 */
	public static String toString(File file) throws IOException {
		StringBuffer fileData = new StringBuffer(2048*5);
		Reader reader = null;
		try {
			reader = new InputStreamReader(new BufferedInputStream(new FileInputStream(file)), "utf-8");
			char[] buf = new char[2048];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				fileData.append(buf, 0, numRead);
			}
		} catch (IOException e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(reader);
		}
		return fileData.toString();

	}

	// to prevent a bug where in some situations you can't copy a 'fresh' rendered image
	public synchronized static void copyFile(File src, File dest) throws IOException {
		int tries = 0;
		boolean isError = false;
		do {
			isError = false;
			try {
				FileUtils.copyFile(src, dest);
			} catch (IOException e) {
				isError = true;
			}
			if (isError)
				Utils.quietlyDelay(250);
			tries++;
		} while (isError && tries <= maxCopyTries);
		if (tries >= maxCopyTries) {
			String msg = String.format("Couldn't copy [%s] to [%s]", src.getPath(), dest.getPath());
			ImageTagTool.logger.error(msg);
			throw new IOException(msg);
		}
	}
	
	public static boolean isInside(File dir, File dirOrFile) {
		if(dir.equals(dirOrFile))
			return false;
		return (dirOrFile.getAbsolutePath().length() > dir.getAbsolutePath().length() 
				&& dirOrFile.getAbsolutePath().startsWith(dir.getAbsolutePath()));
	}
	
	/**
	 * TODO DOCUMENT ME!
	 * 
	 * @param filename
	 * @return
	 */
	public static String getExtension(final String filename) {
		if(filename == null || filename.trim().length() == 0 || !filename.contains("."))
			return null;
		int pos = filename.lastIndexOf(".");
		return filename.substring(pos + 1); 
	}

	/**
	 * TODO DOCUMENT ME!
	 * 
	 * @param file
	 * @return
	 */
	public static String getExtension(final File file) {
		return getExtension(file.getName());
	}
}
