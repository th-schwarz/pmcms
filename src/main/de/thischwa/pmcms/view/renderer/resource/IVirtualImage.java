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
package de.thischwa.pmcms.view.renderer.resource;

import java.io.File;

import de.thischwa.pmcms.model.domain.pojo.Image;
import de.thischwa.pmcms.tool.image.Dimension;

public interface IVirtualImage extends IVirtualFile {

	/**
	 * Analyzes if desired image file is a cached one and sets the corresponding base file.
	 * 
	 * @param imgFile E.g.[datadir]/sites/test.org/cache/galleryname/floating-leaves_320x240.jpg
	 */
	void analyse(final File imgFile);

	public void constructFromImage(final Image image);
	
	/**
	 * Set the dimension of the 'new' image file name. The usage of this setter is only necessary, if you want to get the cached file or the
	 * src attribute for the img-tag of the exported version of the file.
	 */
	public void setDimension(final Dimension dimension);

	public Dimension getDimension();
	
	public File getCacheFile();
}
