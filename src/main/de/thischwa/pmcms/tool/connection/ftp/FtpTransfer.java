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
package de.thischwa.pmcms.tool.connection.ftp;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.io.CopyStreamAdapter;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.Util;
import org.apache.log4j.Logger;

import de.thischwa.pmcms.tool.connection.AbstractTransfer;
import de.thischwa.pmcms.tool.connection.ConnectionException;
import de.thischwa.pmcms.tool.connection.ConnectionRunningException;
import de.thischwa.pmcms.tool.connection.IConnectionManager;
import de.thischwa.pmcms.tool.connection.PathAnalyzer;
import de.thischwa.pmcms.tool.connection.PathConstructor;
import de.thischwa.pmcms.tool.connection.UploadObject;
import de.thischwa.pmcms.tool.connection.UploadTree;
import de.thischwa.pmcms.tool.connection.UploadTreeNode;

/**
 * {@link AbstractTransfer} implementation for ftp. <br>
 * The underlying class is the <code>FTPClient</code> from <code>apache's commons-net</code>.<br>
 * <br>
 *
 * @version $Id: FtpTransfer.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class FtpTransfer extends AbstractTransfer {
	private static Logger logger = Logger.getLogger(FtpTransfer.class);

	/** The main object from commons-net to handle the ftp connection. */
	private FTPClient ftpClient;
	
	public FtpTransfer(final IConnectionManager connectionManager) {
		super(connectionManager);
	    this.ftpClient = (FTPClient) connectionManager.getUnderlyingObject();
	    if (this.ftpClient == null)
	    	throw new IllegalArgumentException("FTPClient is null!");
	}
	
	
	@Override
	public boolean download(final String sourcePath, OutputStream out) throws ConnectionRunningException {
		super.check();
		boolean ok = false;
		PathAnalyzer pathAnalyzer = new PathAnalyzer(sourcePath);
		if (chDirAbsolute(pathAnalyzer.getDir())) {
			InputStream serverIn = null;
			try {
				// 1. check, if Target exits on the server
				FTPFile serverFile = getByName(ftpClient.listFiles(), pathAnalyzer.getName());
				if (serverFile == null || serverFile.isDirectory()) {
					logger.debug("[FTP] '" + sourcePath + "' doesn't exists or is a directory, - can't get it!");
					return false;
				}
				
				// 2. retrieve the file
				if (!ftpClient.retrieveFile(sourcePath, out))
					throw new ConnectionRunningException("Error while download [" + sourcePath + "]!");
				ok = true;
				out.flush();
			} catch (Exception e) {
				logger.error("[FTP] While getting/copying streams: " + e.getMessage(), e);
				throw new ConnectionRunningException("[FTP] While getting/copying streams: " + e.getMessage(), e);
			} finally {
				IOUtils.closeQuietly(serverIn);
				IOUtils.closeQuietly(out);
			}
		} else
			throw new ConnectionRunningException("Couldn't change to target dir: " + pathAnalyzer.getDir());
		
		if (ok)
			logger.debug("[FTP] successfull downloaded: " + sourcePath);
		else
			logger.debug("[FTP] download failed!");
		return ok;
	}

	
	/* (non-Javadoc)
	 * @see de.thischwa.pmcms.tool.connection.AUpload#uploadToStartDir(java.lang.String, java.io.InputStream)
	 */
	@Override
	public void uploadToStartDir(final String targetPath, final InputStream in) throws ConnectionRunningException {
		if (StringUtils.isBlank(targetPath) || in == null)
			throw new IllegalArgumentException();
		PathAnalyzer ftt = new PathAnalyzer(targetPath);
		if (chDirsConstruct('/' + ftt.getDir())) {
			check();
			uploadToCurrentDir(ftt.getName(), in);
			logger.debug("[FTP] successfull uploaded: " + targetPath);
		}
	}


	/* (non-Javadoc)
	 * @see de.thischwa.pmcms.tool.connection.AbstractTransfer#upload(de.thischwa.pmcms.model.tool.UploadTree)
	 */
	@Override
	public void upload(final UploadTree targetTree) throws ConnectionRunningException {
	 	super.upload(targetTree);
		chDirAbsolute(null);
		check();
    	traverseUpload(targetTree.getRootNode());
    }

	@Override
	public void deleteDirectory(String targetPath) throws ConnectionRunningException {
		chDirAbsolute(null);
		check();
		try {
			if(!ftpClient.removeDirectory(targetPath))
				throw new ConnectionException("Couldn't delete dir: " + targetPath);
		} catch (Exception e) {
			throw new ConnectionException(e);
		}
		logger.debug("[FTP] dir successfull deleted: " + targetPath);
	}
	
	@Override
	public void deleteFile(String targetPath) throws ConnectionRunningException {
		chDirAbsolute(null);
		check();
		try {
			if(!ftpClient.deleteFile(targetPath))
				throw new ConnectionException("Couldn't delete file: " + targetPath);
		} catch (Exception e) {
			throw new ConnectionException(e);
		}
		logger.debug("[FTP] file successfull deleted: " + targetPath);
	}

	/**
	 * Uploads a 'file' with the content of the InputStream and the name 'name' to the current dir of FTPClient.
	 * If the 'file' exists, it will be deleted before.
	 *
	 * @throws ConnectionRunningException
	 */
	private void uploadToCurrentDir(final String name, final InputStream in) throws ConnectionRunningException {
		// TODO implement the server reconnect here!!
	    OutputStream serverOut = null;
	    try {
	    	// 1. check, if target file exists and delete it
	    	FTPFile serverFile = getByName(ftpClient.listFiles(), name);
	    	if (serverFile != null) {
	    		if (!ftpClient.deleteFile(name))
	    			throw new ConnectionRunningException("Couldn't delete existent file: " + name);
	    	}

	    	// 2. create the empty file 
	    	if (!ftpClient.storeFile(name, IOUtils.toInputStream("")))
	    		throw new ConnectionRunningException("Couldn't create and empty file for: " + name);

	    	// 3. copy stream
	    	serverOut = new BufferedOutputStream(ftpClient.storeFileStream(name), ftpClient.getBufferSize());
	    	Util.copyStream(in, serverOut, ftpClient.getBufferSize(), CopyStreamEvent.UNKNOWN_STREAM_SIZE, new CopyStreamAdapter() {
		    		@Override
					public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
		    			progressAddBytes(bytesTransferred);
		    		}
		    	});
	    	// not documented but necessary: flush and close the server stream !!!
	    	serverOut.flush();
	    	serverOut.close();
	    	if (!ftpClient.completePendingCommand()) { 
	    		connectionManager.close();
	    		throw new ConnectionRunningException("[FTP] While getting/copying streams: " + ftpClient.getReplyString());
	    	}
	    	
	    } catch (Exception e) {
	    	logger.error("[FTP] While getting/copying streams: " + e.getMessage(), e);
	    	if(!(e instanceof ConnectionException)) {
	    		connectionManager.close();
	    		throw new ConnectionRunningException("[FTP] While getting/copying streams: " + e.getMessage(), e);
	    	}
	    } finally {
	    	IOUtils.closeQuietly(in);
	    	progressSetSubTaskMessage("");
	    }
    }

	/**
	 * Just a wrapper to {@link #uploadToCurrentDir(String, InputStream)}.
	 */
	private void uploadToCurrentDir(UploadObject uploadObject) {
		progressSetSubTaskMessage(String.format("Upload file: %s (%sKB)", uploadObject.getName(), NumberFormat.getInstance().format(uploadObject.getBytes()/1024)));
		uploadToCurrentDir(uploadObject.getName(), uploadObject.getInputStream());
	}

	private void traverseUpload(final UploadTreeNode node) {
		if(node.hasChildren()) {
			for(UploadObject uo : node.getChildren()) {
				uploadToCurrentDir(uo);
			}
		}
		if(node.hasSubTrees()) {
			for(String dir : node.getSubTrees().keySet()) {
				chDirsConstruct(dir);
				traverseUpload(node.getSubTrees().get(dir));
				chParentDir();
			}
		}
    }

	
	private void chParentDir() {
		try {
            if (!ftpClient.changeToParentDirectory())
            	throw new ConnectionRunningException("Couldn't change to parent dir!");
            logger.debug("[FTP] successfull chdir to parent.");
        } catch (IOException e) {
        	throw new ConnectionRunningException("Error while change to parent dir: " + e.getMessage(), e);
        }
	}
	

	/**
	 * Changes to 'targetDir' absolute to the 'serverRootDir'.
	 * 
	 * @param targetDir Shouldn't start with '/'! Can have subdirs like a/b/c.
	 * @return True, if targetDir exists and successful changed.
	 * @throws ConnectionRunningException If any IOException was thrown by the FTPClient.
	 */
	private boolean chDirAbsolute(final String targetDir) throws ConnectionException {
		super.check();
		String absoluteDir = serverRootDir + StringUtils.defaultString(targetDir);
		try {
			boolean retval = ftpClient.changeWorkingDirectory(absoluteDir);
			if (retval)
				logger.debug("[FTP] successfull chdir to: " + absoluteDir);
			return retval;
		} catch (IOException e) {
			logger.error("Error while trying to chdir [" + absoluteDir + "]: " + e.getMessage(), e);
			throw new ConnectionException("Error while chDir to [" + absoluteDir + "]: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Changes to 'targetDir' and create the necessary subdirs.
	 * 
	 * @param targetDirs If starts with '/' it will be interpreted as absolute to 'serverRootDir'!
	 * @return True if was created/changed successful, otherwise false.
	 * @throws ConnectionRunningException If any IOException was thrown by the FTPClient.
	 */
	private boolean chDirsConstruct(final String targetDirs) throws ConnectionException {
		try {
			boolean isAbsolute = false;
			
			// 1. check, if targetDir begins with '/' -> begin with server root dir
			if (targetDirs.startsWith("/")) 
				isAbsolute = true;

			String tempDir;
			if (isAbsolute) 
				tempDir = new PathConstructor().add(serverRootDir).add(targetDirs).setAbsolute().toString();
			else
				tempDir = targetDirs;			
			// 2. quick try, if requested dir exits, so we doesn't need the time expensive traversing
			if (ftpClient.changeWorkingDirectory(tempDir)) {
				logger.debug("[FTP] successfull chdir to:".concat(tempDir));
				return true;
			} else {
				logger.debug("[FTP] couldn't change dir directly, have to traverse to: ".concat(tempDir));
			}
			
			// 3. traverse and create if necessary
			String dirs[] = new PathConstructor().add(targetDirs).getDirs();
			for (String dir : dirs) {
				if (StringUtils.isNotBlank(dir)) {
					FTPFile ftpFile = getByName(ftpClient.listFiles(), dir);
					if (ftpFile != null && ftpFile.isDirectory()) { // TODO Check if file exists with dir name!
						if (!ftpClient.changeWorkingDirectory(dir))
							throw new ConnectionRunningException("Couldn't change to dir: " + dir);
					}  else if (ftpFile == null) {
						if (!(ftpClient.makeDirectory(dir) && ftpClient.changeWorkingDirectory(dir)))
							throw new ConnectionRunningException("Couldn't create to dir: " + dir);
					}
				}
			}
		} catch (IOException e) {
			throw new ConnectionRunningException(e);
		}
		logger.debug("[FTP] Successfull change/create: ".concat(targetDirs));
		return true;
	}

	/**
	 * Searches a FTPFile by name from an array of FTPFiles.
	 * 
	 * @return FTPClient, if exists, or null.
	 */
	private FTPFile getByName(final FTPFile[] files, final String name) {
		if (StringUtils.isBlank(name))
			return null;
		for (FTPFile file : files)
			if (file.getName().equals(name))
				return file;
		return null;
	}
}
