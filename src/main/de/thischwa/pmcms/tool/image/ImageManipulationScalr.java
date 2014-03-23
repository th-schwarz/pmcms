package de.thischwa.pmcms.tool.image;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Component;

@Component
public class ImageManipulationScalr extends AImageManipulation {
	private static Logger logger = Logger.getLogger(ImageManipulationScalr.class);

	@Override
	protected void resize(File srcImageFile, File destImageFile, Dimension dimension) throws Exception {
		String ext = FilenameUtils.getExtension(srcImageFile.getName());
		BufferedImage srcImg = null;
		BufferedImage destImg = null;
		
		try {
			srcImg = ImageIO.read(srcImageFile);
			destImg = Scalr.resize(srcImg, Scalr.Method.QUALITY, dimension.x, dimension.y);
			ImageIO.write(destImg, ext, destImageFile);
			logger.debug(String.format("Resized [%s] to [%s] successful.", srcImageFile.getPath(), destImageFile.getPath()));
		} catch (Exception e) {
			logger.warn("Error while resizing with Scalr!", e);
		} finally {
			quietFlush(srcImg);
			quietFlush(destImg);
		}
	}
	
	private void quietFlush(BufferedImage bi) {
		try {
			bi.flush();
		} catch (Exception e) {
		}
	}

}
