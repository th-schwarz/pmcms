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
package de.thischwa.pmcms.tool.image;

import java.io.File;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.thischwa.jii.IDimensionProvider;
import de.thischwa.jii.ImageType;
import de.thischwa.pmcms.exception.FatalException;

/**
 * Tool to get the {@link Dimension} of an image. 
 *
 * @version $Id: ImageInfo.java 2223 2012-09-24 07:45:33Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
@Component
public class ImageInfo {
	private static Logger logger = Logger.getLogger(ImageInfo.class);

	@Value("${pmcms.jii.dimension.class}")
	private String infoClass;
	
	private IDimensionProvider dp = null;

	public synchronized Dimension getDimension(final File imageFile) {
		try {
			if(dp == null)
				buildImpl();
			dp.set(imageFile);
			return new Dimension(dp.getDimension());
		} catch (Exception e) {
			logger.error("Error while getting image infos: " + e.getMessage(), e);
			throw new FatalException(e);
		}
	}
	
	public ImageType[] getSupportedImageTypes() {
		if(dp == null)
			buildImpl();
		return dp.getSupportedTypes();
	}

	private void buildImpl() {
		try {
			Class<?> cls = Class.forName(infoClass);
			dp = (IDimensionProvider) cls.newInstance();
		} catch (Exception e) {
			throw new FatalException(e);
		}
	}

}
