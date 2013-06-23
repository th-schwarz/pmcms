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

import java.io.InputStream;


/**
 * Container to hold the relevant infos for uploading.
 *
 * @author Thilo Schwarz
 */
public class UploadObject {
	private String name;
	private long bytes;
	private InputStream in;
	
	
	public UploadObject(final String name, final InputStream in, final long bytes) {
	    this.name = name;
	    this.bytes = bytes;
	    this.in = in;
    }
	
	public UploadObject(final String name, final InputStream in) {
		this(name, in, -1);
    }

	public String getName() {
    	return this.name;
    }

	public long getBytes() {
    	return this.bytes;
    }

	public InputStream getInputStream() {
    	return this.in;
    }
}
