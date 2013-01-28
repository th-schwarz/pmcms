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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.gui.IProgressViewer;
import de.thischwa.pmcms.model.domain.PoPathInfo;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.tool.ChecksumTool;
import de.thischwa.pmcms.tool.connection.IConnection;
import de.thischwa.pmcms.tool.connection.UploadTree;

/**
 * Object to wrap all the stuff needed to upload a site on a server.
 * 
 * @version $Id: Upload.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class Upload implements IProgressViewer {
	private static Logger logger = Logger.getLogger(Upload.class);
	private IProgressMonitor monitor = null;
	private IConnection transfer;	
	private Map<String, String> localHashes;
	private Map<String, String> serverHashes;

	private Set<String> localDirs;
	private List<String> serverDirs;

	private File siteExportDir;
	private File localHashesFile;
	
	
	public Upload(final Site site, final IConnection transfer, String checkumsFileBasename) {
		siteExportDir = PoPathInfo.getSiteExportDirectory(site);
		this.transfer = transfer;
		serverHashes = new HashMap<String, String>();
		serverDirs = new ArrayList<String>();
		localDirs = new HashSet<String>();
		
		// collect the exported files
		localHashesFile = new File(siteExportDir, FilenameUtils.getBaseName(checkumsFileBasename) + ".zip");
		localHashes = ChecksumTool.getFromZip(localHashesFile, checkumsFileBasename);
		for (String path : localHashes.keySet()) {
			// collect the local dirs
			if(path.contains(UploadTree.PATH_SEPARATOR+"")) {
				String parent = path.substring(0, path.lastIndexOf(UploadTree.PATH_SEPARATOR));
				localDirs.add(parent);
			}
		}
		
		// download and read the server hashes
		try {
			File serverHashFile = File.createTempFile("checksums", ".zip", Constants.TEMP_DIR);
			if (transfer.download(FilenameUtils.getName(localHashesFile.getName()), new BufferedOutputStream(new FileOutputStream(serverHashFile)))) {
				serverHashes = ChecksumTool.getFromZip(serverHashFile, checkumsFileBasename);
			}
		} catch (Exception e) {
			logger.warn("Error while getting the server hashes: " + e.getMessage(), e);
		}
		// collect the server dirs
		for(String path : serverHashes.keySet()) {
			if(path.contains(UploadTree.PATH_SEPARATOR+"")) {
				String parent = path.substring(0, path.lastIndexOf(UploadTree.PATH_SEPARATOR));
				if(!serverDirs.contains(parent))
					serverDirs.add(parent);
			}
		}
	}

	@Override
	public void run() throws Exception {
		UploadTree uploadTree = getUploadTree();
		if(uploadTree == null) {
			logger.debug("Nothing to upload!");
			return;
		}
		transfer.setProgressMonitor(monitor);
		transfer.upload(uploadTree);
		
		// the tree was uploaded successful so we can upload the hashes
		try {
			transfer.uploadToStartDir(localHashesFile.getName(), new BufferedInputStream(new FileInputStream(localHashesFile)));
		} catch (Exception e) {
			logger.error("Error while uploading the hash file: " + e.getMessage(), e);
		}
		
		// TODO monitoring deleted files
		
		// delete obsolete files
		Set<String> serverFiles = serverHashes.keySet();
		for(String file : serverFiles) {
			if(!localHashes.containsKey(file)) {
				try {
					transfer.deleteFile(file);
				} catch (Exception e) {
					logger.error(String.format("While trying to delete file [%s]: %s", file, e.getMessage()), e);
				}
			}
		}
		
		// delete obsolete directories
		Collections.sort(serverDirs, new PathComparator());
		for(String dir : serverDirs) {
			if(!localDirs.contains(dir)) {
				try {
					transfer.deleteDirectory(dir);
				} catch (Exception e) {
					logger.error(String.format("While trying to delete dir [%s]: %s", dir, e.getMessage()), e);					
				}
			}
		}
		
		transfer.close();
	}

	@Override
	public void setMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	private UploadTree getUploadTree() {
		logger.debug("entered getUploadTree");
		
		// collect, what files have to be uploaded
		Map<String, File> filesToUpload = new HashMap<String, File>();
		for(String key : serverHashes.keySet()) {
			String serverChecksum = serverHashes.get(key);
			String localChecksum = localHashes.get(key);
			if(localChecksum != null && !StringUtils.equals(localChecksum, serverChecksum)) {
				filesToUpload.put(key, new File(siteExportDir, key));
				logger.debug(String.format("Merge - different checksums for %s", key));
			}
		}
		for(String key: localHashes.keySet()) {
			if(!serverHashes.containsKey(key)) {
				filesToUpload.put(key, new File(siteExportDir, key));
				logger.debug(String.format("Merge - is new %s", key));
			}
		}
		
		if(filesToUpload.isEmpty())
			return null;
		
		// build the UplaadTree
		UploadTree tree = new UploadTree();
		for(String key : filesToUpload.keySet()) {
			File fileToUpload = filesToUpload.get(key);
			tree.add(key, fileToUpload);
		}
		
		return tree;
	}
}
