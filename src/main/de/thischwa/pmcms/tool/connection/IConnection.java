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
package de.thischwa.pmcms.tool.connection;

import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.core.runtime.IProgressMonitor;


/**
 * Interface for all 'transfer classes'.
 * 
 * @version $Id: IConnection.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public interface IConnection {

	/**
	 * Downloads a target directly into an OutputStream. The stream has to close inside the implemented method.
	 * 
	 * @param targetPath
	 *            Full path of a target without server start dir.
	 * @param out
	 * @return True, if target exists and successful downloaded.
	 * @throws ConnectionRunningException
	 *             If download fails, not if the target doesn't exists
	 */
	public boolean download(final String targetPath, OutputStream out) throws ConnectionRunningException;

	/**
	 * Uploads a 'file' with the content of the InputStream. 'targetPath' will be interpreted as absolute to the 'start dir' and
	 * ends with the name, e.g. 'a/b/c/name.txt'.
	 * 
	 * @param targetPath
	 *            The target with absolute path, means absolute to server start dir.
	 * @param in
	 * @throws ConnectionRunningException
	 */
	public void uploadToStartDir(final String targetPath, InputStream in) throws ConnectionRunningException;

	/**
	 * Uploads the whole {@link UploadTree}.
	 */
	public void upload(final UploadTree targetTree) throws ConnectionRunningException;
	
	public void deleteFile(final String tagetPath) throws ConnectionRunningException;
	public void deleteDirectory(final String tagetPath) throws ConnectionRunningException;
	
	/**
	 * Closes the underlying {@link IConnectionManager} and makes necessary cleanups.
	 */
	public void close();	
	
	/**
	 * Setter for SWT's IProgressMonitor.
	 */
	public void setProgressMonitor(final IProgressMonitor progressMonitor);
	
	/**
	 * Checks, if the connection is establish. The implementation should try a reconnection, before throwing the Exception!
	 * @throws ConnectionException
	 */
	public void check() throws ConnectionException;
}
