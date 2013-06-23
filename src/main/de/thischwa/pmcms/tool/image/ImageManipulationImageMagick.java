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
import java.util.ArrayList;
import java.util.List;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.thischwa.pmcms.exception.RenderingException;
import de.thischwa.pmcms.tool.ProcessBuilderWrapper;

/**
 * Image manipulation for the commandline tool 'convert' provided by ImageMagick (http://www.imagemagick.org).
 * 
 * @author Thilo Schwarz
 */
@Component
public class ImageManipulationImageMagick extends AImageManipulation {
	private static Logger logger = Logger.getLogger(ImageManipulationImageMagick.class);

	@Value("${imagemagick.convert.command}")
	private String basicCommand;

	@Value("${imagemagick.resolution.export}")
	private String exportResolution;

	@Value("${imagemagick.convert.parameters}")
	private String paramString;

	@Override
	protected void resize(final File srcImageFile, final File destImageFile, final Dimension dimension, boolean useJustRenderedCheck)
			throws Exception {

		List<String> command = new ArrayList<String>();
		command.add(basicCommand);
		for (String additionalAttr : StringUtils.split(paramString))
			command.add(additionalAttr);
		command.add(srcImageFile.getAbsolutePath());
		command.add("-resize");
		command.add(dimension.toString());
		command.add("-density");
		command.add(String.format("%sx%s", exportResolution, exportResolution));
		command.add(destImageFile.getAbsolutePath());

		logger.debug("Command list: ".concat(command.toString()));
		ProcessBuilderWrapper processBuilder = null;
		processBuilder = new ProcessBuilderWrapper(command);
		logger.debug("Process terminated with exit value: " + processBuilder.getStatus());

		String errorOutput = processBuilder.getErrors();
		if (StringUtils.isNotBlank(errorOutput)) {
			logger.error("Error while resizing [" + srcImageFile.getName() + "]: " + errorOutput);
			throw new RenderingException("Error while resizing [" + srcImageFile.getName() + "]: " + errorOutput);
		}
	}
}
