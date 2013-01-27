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
package de.thischwa.pmcms.view.renderer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;


import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import de.thischwa.pmcms.model.IRenderable;
import de.thischwa.pmcms.tool.PathTool;
import de.thischwa.pmcms.view.ViewMode;

/**
 * Thread to render one {@link IRenderable}.
 * 
 * @version $Id: ExportRenderThread.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class ExportRenderThread extends Thread {
	private static Logger logger = Logger.getLogger(ExportRenderThread.class);
	private ExportThreadPoolController controller;
	private IRenderable renderable;
	private VelocityRenderer velocityRenderer;
	private String poExtension;

	ExportRenderThread(final ExportThreadPoolController controller, final IRenderable renderable, final VelocityRenderer velocityRenderer, final String poExtension) {
		this.renderable = renderable;
		this.controller = controller;
		this.velocityRenderer = velocityRenderer;
		this.poExtension = poExtension;
		setName("Thread-".concat(renderable.toString()));
	}

	@Override
	public void run() {
		if (controller.isCanceled()) {
			this.interrupt();
			return;
		}

		File outputFile = PathTool.getExportFile(renderable, poExtension);
		Writer fileWriter = null;
		try {
			fileWriter = new BufferedWriter(new FileWriter(outputFile));
			velocityRenderer.render(fileWriter, renderable, ViewMode.EXPORT);
			fileWriter.flush();
		} catch (Exception e) {
			controller.interrupt(this, e);
		} finally {
			IOUtils.closeQuietly(fileWriter);
		}
		logger.debug("Rendered: ".concat(renderable.toString()));
	}
}
