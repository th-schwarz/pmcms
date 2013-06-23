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

// TODO: Auto-generated Javadoc
/**
 * This Exception is used for fatal errors, on which pmcms should stop. If this throws, the renderer will show an
 * error page.
 * 
 * @author Thilo Schwarz
 */
public class FatalException extends RuntimeException {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new fatal exception.
     *
     * @param message the message
     */
    public FatalException(String message) {
        super(message);
    }

    /**
     * Instantiates a new fatal exception.
     *
     * @param message the message
     * @param cause the cause
     */
    public FatalException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new fatal exception.
     *
     * @param cause the cause
     */
    public FatalException(Throwable cause) {
        super(cause);
    }
}
