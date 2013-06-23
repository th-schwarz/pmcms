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

/**
 * Exception to indicate an error during file transfer.
 *
 * @author Thilo Schwarz
 */
public class ConnectionRunningException extends ConnectionException {
	private static final long serialVersionUID = 1L;
	
	public ConnectionRunningException(String message) {
		super(message);
	}
	public ConnectionRunningException(String message, Throwable cause) {
		super(message, cause);
	}
	public ConnectionRunningException(Throwable cause) {
		super(cause);
	}
}
