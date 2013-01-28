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
package de.thischwa.pmcms.tool.connection;

import java.io.InputStream;
import java.io.OutputStream;


import org.eclipse.core.runtime.IProgressMonitor;

import de.thischwa.pmcms.configuration.resource.LabelHolder;
import de.thischwa.pmcms.tool.Utils;


/**
 * Abstract class which has to be extended by all concrete 'transfer classes'.
 *
 * @version $Id:AUpload.java 1001 2007-09-16 10:31:30Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public abstract class AbstractTransfer implements IConnection {
	protected IConnectionManager connectionManager;
	protected String serverRootDir;
	private IProgressMonitor monitor = null;

	protected AbstractTransfer(final IConnectionManager connectionManager) throws ConnectionAuthentificationException, ConnectionException {
		if (connectionManager == null)
    		throw new IllegalArgumentException("ConnectionManager is null!");
		if (!connectionManager.isConnected())
			connectionManager.start();
		this.serverRootDir = connectionManager.getRootDir();
		this.connectionManager = connectionManager;
	}
	
	@Override
	public abstract boolean download(final String targetPath, OutputStream out) throws ConnectionRunningException;
	
	@Override
	public abstract void uploadToStartDir(final String targetPath, final InputStream in) throws ConnectionRunningException;

	@Override
	public abstract void deleteFile(final String tagetPath) throws ConnectionRunningException;

	@Override
	public abstract void deleteDirectory(final String tagetPath) throws ConnectionRunningException;
	
	
	/* Initializes the IProgressMonitor. <br> 
	 * Have to be overwritten form the extended class! But don't forget to call super!!!
	 * @see de.thischwa.pmcms.tool.connection.IUpload#upload(de.thischwa.pmcms.tool.connection.UploadTree)
	 */
	@Override
	public void upload(final UploadTree targetTree) throws ConnectionRunningException {
		if (monitor != null) 
			monitor.beginTask(Utils.join(LabelHolder.get("task.transfer.monitor.upload"), String.valueOf(targetTree.getNumberOfFiles()), "/", String.valueOf((int) (targetTree.getTotalSizeInBytes()/1024))), 
					(int) targetTree.getTotalSizeInBytes());
	}
	
	@Override
	public void close() {
		if (monitor != null)
			monitor.done();
		connectionManager.close();
	}

    @Override
	public void check() throws ConnectionException {
	    if (!connectionManager.isConnected())
	    	connectionManager.start(); // throws the required exception
    }

	@Override
	public void setProgressMonitor(final IProgressMonitor progressMonitor) {
	    this.monitor = progressMonitor;
    }

	/**
	 * Adds <code>bytes</code> to the current progress.
	 * 
	 * @param bytes
	 */
	protected void progressAddBytes(int bytes) {
		if (this.monitor != null)
			this.monitor.worked(bytes);
	}
	
	/**
	 * Detailed dub info, e.g. file name / size.
	 * 
	 * @param msg
	 */
	protected void progressSetSubTaskMessage(final String msg) {
		if (this.monitor != null)
			this.monitor.subTask(msg);
	}
}
