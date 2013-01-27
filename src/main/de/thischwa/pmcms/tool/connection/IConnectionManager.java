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


/**
 * This interface has to implemented by all 'connection handler'. 
 *
 * @version $Id: IConnectionManager.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public interface IConnectionManager {
	
	/**
	 * Starts the connection. This method called also for reconnection!
	 * 
	 * @throws ConnectionAuthentificationException If login fails.
	 * @throws ConnectionRunningException If an exception was happened except login fails.
	 */
	public void start() throws ConnectionAuthentificationException, ConnectionRunningException;
	
	/**
	 * @return True, if active/established.
	 */
	public boolean isConnected();
	
	/**
	 * Closes the connection in 'quiet' mode (no exception are throwing).
	 */
	public void close();
	
	/**
	 * @return The underlying object of the connection implementation.
	 */
	public Object getUnderlyingObject();
	
	/**
	 * @return The root directory of the server. The 'remoteStartDir' is included, if set.
	 */
	public String getRootDir();
}
