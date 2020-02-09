package de.thischwa.pmcms.tool.connection.sftp;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import de.thischwa.pmcms.tool.connection.AbstractTransfer;
import de.thischwa.pmcms.tool.connection.ConnectionAuthentificationException;
import de.thischwa.pmcms.tool.connection.ConnectionException;
import de.thischwa.pmcms.tool.connection.ConnectionRunningException;
import de.thischwa.pmcms.tool.connection.IConnectionManager;
import de.thischwa.pmcms.tool.connection.PathAnalyzer;
import de.thischwa.pmcms.tool.connection.PathConstructor;
import de.thischwa.pmcms.tool.connection.UploadObject;
import de.thischwa.pmcms.tool.connection.UploadTree;
import de.thischwa.pmcms.tool.connection.UploadTreeNode;

/**
 * {@link AbstractTransfer} implementation for sftp. <br>
 * The underlying class is {@link SftpClient}, a wrapper for {@link ChannelSftp}.
 */
public class SftpTransfer extends AbstractTransfer {
	private static Logger logger = Logger.getLogger(SftpTransfer.class);

	private SftpClient sftpClient;

	public SftpTransfer(IConnectionManager connectionManager) throws ConnectionAuthentificationException, ConnectionException {
		super(connectionManager);
		sftpClient = (SftpClient) connectionManager.getUnderlyingObject();
		if(sftpClient == null)
			throw new IllegalArgumentException("ChannelSftp is null!");
	}

	@Override
	public boolean download(String targetPath, OutputStream out) throws ConnectionRunningException {
		super.check();
		boolean ok = false;
		PathAnalyzer pathAnalyzer = new PathAnalyzer(targetPath);
		if(chDirAbsolute(pathAnalyzer.getDir())) {
			InputStream serverIn = null;
			try {
				String downloadTarget = serverRootDir + targetPath;
				// 1. check, if Target exits on the server
				if(!sftpClient.exists(downloadTarget)) {
					logger.debug("[SFTP] '" + targetPath + "' doesn't exists or is a directory, - can't get it!");
					return false;
				}

				// 2. retrieve the file
				try {
					sftpClient.client.get(downloadTarget, out);
				} catch (SftpException e) {
					throw new ConnectionRunningException("Error while download [" + targetPath + "]!");
				}
				ok = true;
				out.flush();
			} catch (Exception e) {
				logger.error("[SFTP] While getting/copying streams: " + e.getMessage(), e);
				throw new ConnectionRunningException("[SFTP] While getting/copying streams: " + e.getMessage(), e);
			} finally {
				IOUtils.closeQuietly(serverIn);
				IOUtils.closeQuietly(out);
			}
		} else
			throw new ConnectionRunningException("[SFTP] Couldn't change to target dir: " + pathAnalyzer.getDir());

		if(ok)
			logger.debug("[SFTP] successfull downloaded: " + targetPath);
		else
			logger.debug("[SFTP] download failed!");
		return ok;
	}

	@Override
	public void uploadToStartDir(String targetPath, InputStream in) throws ConnectionRunningException {
		if(StringUtils.isBlank(targetPath) || in == null)
			throw new IllegalArgumentException();
		PathAnalyzer ftt = new PathAnalyzer(targetPath);
		if(chDirsConstruct('/' + ftt.getDir())) {
			check();
			uploadToCurrentDir(ftt.getName(), in);
			logger.debug("[SFTP] successfull uploaded: " + targetPath);
		}
	}

	@Override
	public void deleteFile(String tagetPath) throws ConnectionRunningException {
		try {
			sftpClient.client.rm(tagetPath);
		} catch (SftpException e) {
			throw new ConnectionRunningException(String.format("Unexpected exception during 'rm' on sftp: [%d:%s]", e.id, e.getMessage()), e);
		}

	}

	@Override
	public void deleteDirectory(String tagetPath) throws ConnectionRunningException {
		deleteFile(tagetPath);
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
	
	/**
	 * Uploads a 'file' with the content of the InputStream and the name 'name' to the current dir of FTPClient.
	 * If the 'file' exists, it will be deleted before.
	 *
	 * @throws ConnectionRunningException
	 */
	private void uploadToCurrentDir(final String name, final InputStream in) throws ConnectionRunningException {
		// TODO implement the server reconnect here!!
	    try {
	    	// 1. check, if target file exists and delete it
	    	if (sftpClient.exists(name)) {
	    		sftpClient.rm(name);
	    	}

	    	// 2. copy the file
	    	sftpClient.client.put(in, name);	    	
	    } catch (Exception e) {
	    	logger.error("[SFTP] While getting/copying streams: " + e.getMessage(), e);
	    	if(!(e instanceof ConnectionException)) {
	    		connectionManager.close();
	    		throw new ConnectionRunningException("[FTP] While getting/copying streams: " + e.getMessage(), e);
	    	}
	    } finally {
	    	IOUtils.closeQuietly(in);
	    	progressSetSubTaskMessage("");
	    }
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

	/**
	 * Just a wrapper to {@link #uploadToCurrentDir(String, InputStream)}.
	 */
	private void uploadToCurrentDir(UploadObject uploadObject) {
		progressSetSubTaskMessage(String.format("Upload file: %s (%sKB)", uploadObject.getName(), NumberFormat.getInstance().format(uploadObject.getBytes()/1024)));
		uploadToCurrentDir(uploadObject.getName(), uploadObject.getInputStream());
	}

	private void chParentDir() {
		boolean ok = sftpClient.cd("..");
		if(!ok)
			throw new ConnectionRunningException("Unexcpected exception during 'cd ..' on sftp.");
			logger.debug("[SFTP] successfull chdir to the parent directory");
	}

	/**
	 * Changes to 'targetDir' absolute to the 'serverRootDir'.
	 * 
	 * @param targetDir
	 *            Shouldn't start with '/'! Can have subdirs like a/b/c.
	 * @return True, if targetDir exists and successful changed.
	 * @throws ConnectionRunningException
	 *             If any IOException was thrown by the FTPClient.
	 */
	private boolean chDirAbsolute(final String targetDir) throws ConnectionException {
		super.check();
		String absoluteDir = serverRootDir + StringUtils.defaultString(targetDir);
		boolean ok = sftpClient.cd(absoluteDir);
		if(ok)
			logger.debug("[SFTP] successfull chdir to: " + absoluteDir);
		return true;
	}

	/**
	 * Changes to 'targetDir' and create the necessary subdirs.
	 * 
	 * @param targetDirs
	 *            If starts with '/' it will be interpreted as absolute to 'serverRootDir'!
	 * @return True if was created/changed successful, otherwise false.
	 * @throws ConnectionRunningException
	 *             If any IOException was thrown by the FTPClient.
	 */
	private boolean chDirsConstruct(final String targetDirs) throws ConnectionException {
		boolean isAbsolute = false;

		// 1. check, if targetDir begins with '/' -> begin with server root dir
		if(targetDirs.startsWith("/"))
			isAbsolute = true;
		String tempDir;
		if(isAbsolute)
			tempDir = new PathConstructor().add(serverRootDir).add(targetDirs).setAbsolute().toString();
		else
			tempDir = targetDirs;

		// 2. quick try, if requested dir exits, so we doesn't need the time expensive traversing
		if(sftpClient.cd(tempDir)) {
			logger.debug("[SFTP] successfull chdir to:".concat(tempDir));
			return true;
		}
		logger.debug("[SFTP] couldn't change dir directly, have to traverse to: ".concat(tempDir));

		// 3. traverse and create if necessary
		String dirs[] = new PathConstructor().add(targetDirs).getDirs();
		for(String dir : dirs) {
			if(StringUtils.isNotBlank(dir)) {
				SftpATTRS ftpFile = sftpClient.getAttrByName(dir);
				if(ftpFile != null && ftpFile.isDir()) { // TODO Check if file exists with dir name!
					sftpClient.cd(dir);
				} else if(ftpFile == null) {
					if(!(sftpClient.mkdir(dir) && sftpClient.cd(dir)))
						throw new ConnectionRunningException("Couldn't create to dir: " + dir);
				}
			}
		}

		logger.debug("[SFTP] Successfull change/create: ".concat(targetDirs));
		return true;
	}
}
