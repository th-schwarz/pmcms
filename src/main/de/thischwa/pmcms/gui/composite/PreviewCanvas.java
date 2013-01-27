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
package de.thischwa.pmcms.gui.composite;

import java.io.File;


import org.apache.log4j.Logger;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.configuration.resource.LabelHolder;
import de.thischwa.pmcms.tool.image.Dimension;
import de.thischwa.pmcms.tool.image.ImageTool;

public class PreviewCanvas extends Canvas {
	private static Logger logger = Logger.getLogger(PreviewCanvas.class);

	private File imageFile = null;
	
	public PreviewCanvas(Composite parent, int style) {
		super(parent, style);
		this.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent evt) {
				if(imageFile == null)
					evt.gc.drawString(LabelHolder.get("dialog.pojo.image.nopreview"), 5, 10);
				else {
					Image swtImage = null;
					try {
						final ImageTool imageTool = InitializationManager.getBean(ImageTool.class); //$NON-NLS-1$
						File prevFile = imageTool.getDialogPreview(imageFile, new Dimension(getSize().x, getSize().y));
						swtImage = new Image(Display.getCurrent(), prevFile.getAbsolutePath());
						evt.gc.drawImage(swtImage, 0, 0);
						swtImage.dispose();
					} catch (Exception e) {
						logger.warn(String.format("Problems while previewing [%s]: %s", imageFile.getPath(), e.getMessage()), e);
					} finally {
						if (swtImage != null)
							swtImage.dispose();
					}
				}
			}
		});
	}

	public void preview() {
		imageFile = null;
		this.redraw();
	}

	public void preview(final File imageFile) {
		this.imageFile = imageFile;
		this.redraw();
	}
}
