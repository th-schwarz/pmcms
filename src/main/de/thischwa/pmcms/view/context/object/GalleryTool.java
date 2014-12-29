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
package de.thischwa.pmcms.view.context.object;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.thischwa.pmcms.model.domain.pojo.Gallery;
import de.thischwa.pmcms.model.domain.pojo.Image;
import de.thischwa.pmcms.view.context.IContextObjectCommon;

/**
 * Context object to build the 'infrastructure' of a {@link Gallery}.
 * 
 * @author Thilo Schwarz
 */
@Component("gallerytool")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class GalleryTool implements IContextObjectCommon {
	private static Logger logger = Logger.getLogger(GalleryTool.class);
	
	/**
	 * Prepare a {@link List} of {@link Image images} used to construct the thumbnail page of a {@link Gallery}. The
	 * list contains a list per row. All images are in the requested order.
	 */
	public List<List<Image>> getThumbnailMatrix(final Gallery gallery, final int maxCol) {
		if (gallery == null || CollectionUtils.isEmpty(gallery.getImages()))
			return new ArrayList<List<Image>>();
		int rows = Math.round(((float) gallery.getImages().size() / (float) maxCol) + 0.5f);
		logger.info("Dimension of the thumbnail matrix: ".concat(Integer.toString(maxCol)).concat(" x ").concat(Integer.toBinaryString(rows)));

		List<List<Image>> matrix = new ArrayList<List<Image>>(rows);
		List<Image> matrixCol = new ArrayList<Image>(maxCol);
		int colIndex = 0;
		for (Image image : gallery.getImages()) {
			colIndex++;
			if (colIndex > maxCol) {
				matrix.add(matrixCol);
				matrixCol = new ArrayList<Image>(maxCol);
				colIndex = 1;
			}
			matrixCol.add(image);
		}
		if (!CollectionUtils.isEmpty(matrixCol))
			matrix.add(matrixCol);

		return matrix;
	}
	
	/**
	 * @return the first image of 'gallery' or null if gallery is null or has no image(s).
	 */
	public static Image getFirstImage(final Gallery gallery) {
		return (gallery == null || CollectionUtils.isEmpty(gallery.getImages())) ? null : gallery.getImages().iterator().next();
	}
	
	/**
	 * @return An image with the requested 'filename' in 'gallery' or null.
	 */
	public static Image getImageByFilename(final Gallery gallery, final String filename) {
		for (Image image : gallery.getImages()) 
			if (image.getFileName().equals(filename))
				return image;
		return null;
	}
}
