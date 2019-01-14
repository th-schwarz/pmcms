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
package de.thischwa.pmcms.tool.image;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import codes.thischwa.jii.exception.ReadException;
import de.thischwa.pmcms.livecycle.SiteHolder;

/**
 * AImageManipulation.java - TODO DOCUMENTME!
 *
 * @author Thilo Schwarz
 */
public abstract class AImageManipulation {
	private static Logger logger = Logger.getLogger(AImageManipulation.class);

	@Autowired
	protected SiteHolder siteHolder;

	public void resizeImage(final File srcImageFile, final File destImageFile, final Dimension dimension) throws Exception {
		resizeImage(srcImageFile, destImageFile, dimension, true);
	}
	
	public void resizeImage(final File srcImageFile, final File destImageFile, final Dimension dimension, boolean useJustRenderedCheck) throws Exception {
		if (useJustRenderedCheck && siteHolder.containsJustRendering(destImageFile)) {
			logger.debug(" *** Already in progress: " + destImageFile.getPath());
			return;
		}
		if (useJustRenderedCheck)
			siteHolder.addJustRendering(destImageFile);
		
		try {
			resize(srcImageFile, destImageFile, dimension);
		}  catch (Exception e) {
			logger.error(String.format("Error while trying to resize [].", srcImageFile.getPath()));
			throw new ReadException(e);
		} finally {
			if (useJustRenderedCheck)
				siteHolder.removeJustRendering(destImageFile);
		}
		
	}
	
	protected abstract void resize(final File srcImageFile, final File destImageFile, final Dimension dimension) throws Exception;
}
