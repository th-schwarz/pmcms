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

import de.thischwa.pmcms.tool.connection.ftp.FtpConnectionManager;
import de.thischwa.pmcms.tool.connection.ftp.FtpTransfer;

/**
 * Factory to initialize the connection objects and its corresponding transfer objects.
 *
 * @version $Id: ConnectionFactory.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class ConnectionFactory {

	public static IConnection getFtp(final String host, int port, final String loginName, final String loginPassword, final String remoteStartDir) {
		return new FtpTransfer(new FtpConnectionManager(host, port, loginName, loginPassword, remoteStartDir));
	}

	public static IConnection getFtp(final String host, final String loginName, final String loginPassword, final String remoteStartDir) {
		return getFtp(host, FtpConnectionManager.DEFAULT_PORT, loginName, loginPassword, remoteStartDir);
	}
}
