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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import de.thischwa.pmcms.tool.connection.ftp.FtpConnectionManager;
import de.thischwa.pmcms.tool.connection.ftp.FtpTransfer;

/**
 * Factory to initialize the connection objects and its corresponding transfer objects.
 *
 * @author Thilo Schwarz
 */
public class ConnectionFactory {
	private static Logger logger = Logger.getLogger(ConnectionFactory.class);

	
	public static IConnection get(String serverUri) {
		URI uri;
		try {
			uri = new URI(serverUri);
		} catch (URISyntaxException e) {
			throw new ConnectionException(e);
		}
		
		// TODO VALIDATION
		String scheme = uri.getScheme().toLowerCase();
		String host = uri.getHost();
		int port = uri.getPort();
		String[] auth = StringUtils.split(uri.getUserInfo(), ':');
		String loginName = auth[0];
		String loginPassword = auth[1];
		String baseDir = uri.getPath();
		
		switch(scheme) {
			case "ftp":
				return new FtpTransfer(new FtpConnectionManager(host, port, loginName, loginPassword, baseDir));

			default:
				throw new ConnectionException("Unsupported schema: " + scheme);
		}
	}
	
	public static boolean isValid(String serverUri) {
		URI uri;
		try {
			uri = new URI(serverUri);
		} catch (URISyntaxException e) {
			logger.error("Error while building an URI.", e);
			return false;
		}
		
		String scheme = uri.getScheme().toLowerCase();
		String host = uri.getHost();
		
		return StringUtils.isNoneBlank(scheme, host, uri.getUserInfo()) && Arrays.asList("ftp").contains(scheme);
	}
}
