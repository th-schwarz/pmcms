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
package de.thischwa.pmcms.exception;

import de.thischwa.pmcms.gui.IProgressViewer;

// TODO: Auto-generated Javadoc
/**
 * Exception to indicate, that an Exception was thrown while an {@link IProgressViewer} is running.
 *
 * @author Thilo Schwarz
 */
public class ProgressException extends RuntimeException {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new progress exception.
	 *
	 * @param messages the messages
	 * @param cause the cause
	 */
	public ProgressException(String messages, Throwable cause) {
		super(messages, cause);
	}

	/**
	 * Instantiates a new progress exception.
	 *
	 * @param message the message
	 */
	public ProgressException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new progress exception.
	 *
	 * @param cause the cause
	 */
	public ProgressException(Throwable cause) {
		super(cause);
	}
}
