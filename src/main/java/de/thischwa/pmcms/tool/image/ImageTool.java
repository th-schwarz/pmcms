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

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.exception.FatalException;
import de.thischwa.pmcms.view.renderer.resource.VirtualImage;

/**
 * Tool which contains all image processing functions.<br>
 * Managed by spring!
 * 
 * @author Thilo Schwarz
 */
@Service
public class ImageTool {
	private static Logger logger = Logger.getLogger(ImageTool.class);

	@Autowired
	private ImageInfo imageInfo;

	@Autowired()
	private AImageManipulation imageManipulation;

	public void createCashedImage(final VirtualImage imageFile) {
		File cachedFile = imageFile.getCacheFile();
		File srcFile = imageFile.getBaseFile();

		if (cachedFile.exists() && srcFile.lastModified() <= cachedFile.lastModified()) {
			logger.info("Image exists and isn't modified, don't need to render again: ".concat(cachedFile.getAbsolutePath()));
			return;
		}

		if (!cachedFile.getParentFile().exists())
			cachedFile.getParentFile().mkdirs();
		try {
			imageManipulation.resizeImage(srcFile, cachedFile, imageFile.getDimension());
		} catch (Exception e) {
			throw new FatalException("Error while resizing image!", e);
		}
	}

	public File getDialogPreview(final File srcImageFile, final Dimension previewDimension) {
		if (srcImageFile == null)
			return null;
		File destImageFile = new File(Constants.TEMP_DIR, "swt_dialog_preview".concat(File.separator).concat(
				FilenameUtils.getName(srcImageFile.getAbsolutePath())));
		if (!destImageFile.getParentFile().exists())
			destImageFile.getParentFile().mkdirs();

		try {
			if (destImageFile.exists())
				destImageFile.delete();
			imageManipulation.resize(srcImageFile, destImageFile, previewDimension);
		} catch (Exception e) {
			throw new FatalException("Error while resizing image!", e);
		}
		return destImageFile;
	}

	public Dimension getDimension(final File imageFile) {
		return imageInfo.getDimension(imageFile);
	}

	// just for dev
	public void resizeImage(final File srcImageFile, final File destImageFile, final Dimension dimension) throws Exception {
		imageManipulation.resizeImage(srcImageFile, destImageFile, dimension);
	}
}
