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
package de.thischwa.pmcms.exception;

/**
 * This Exception should be used in context, pmcms shouldn't determinate. Basically used to identify errors happend during rendering.
 * 
 * @version $Id: RenderingException.java 2219 2012-08-27 11:30:27Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class RenderingException extends Exception {
	
	private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new rendering exception.
     *
     * @param message the message
     */
    public RenderingException(String message) {
        super(message);
    }

    /**
     * Instantiates a new rendering exception.
     *
     * @param message the message
     * @param cause the cause
     */
    public RenderingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new rendering exception.
     *
     * @param cause the cause
     */
    public RenderingException(Throwable cause) {
        super(cause);
    }
}
