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
package de.thischwa.pmcms.tool.swt;

import java.net.URL;


import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.thischwa.pmcms.tool.XY;

/**
 * Provides some static SWT helper.
 *
 * @version $Id: SWTUtils.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class SWTUtils {
	
	private static ImageRegistry imageRegistry = new ImageRegistry();

	public static void putImage(String key, URL url) {
		imageRegistry.put(key, ImageDescriptor.createFromURL(url));
	}
	
	public static Image getImage(String key) {
		return imageRegistry.get(key);
	}
	
	public static void disposeResources() {
		imageRegistry.dispose();
	}

	public static void changeFontStyle(final Control control, int style) {
		FontData fontData = control.getFont().getFontData()[0];
		control.setFont(new Font(Display.getDefault(), fontData.getName(), fontData.getHeight(), style));
	}
	
	public static void changeFontSizeRelativ(final Control control, int sizeOffset) {
		FontData fontData = control.getFont().getFontData()[0];		
		control.setFont(new Font(Display.getDefault(), fontData.getName(), fontData.getHeight()+sizeOffset, fontData.getStyle()));
	}

	public static void changeFontSize(final Control control, int size) {
		FontData fontData = control.getFont().getFontData()[0];		
		control.setFont(new Font(Display.getDefault(), fontData.getName(), size, fontData.getStyle()));
	}
	
	public static void asyncExec(final Thread thread, Display display) {
		if (display != null)
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					thread.start();
				}
			});
		else
			thread.start();
	}
	
	public static void center(final Shell shell, final Rectangle parentBounds) {
		Rectangle currentBounds = shell.getBounds();
		
		// center of the parent
		Point parentCenter = new Point(parentBounds.x + (parentBounds.width / 2), parentBounds.y + (parentBounds.height / 2));
		
		int newX = parentCenter.x - Math.abs(currentBounds.width / 2);
		int newY = parentCenter.y - Math.abs(currentBounds.height / 2);
		shell.setLocation(newX, newY);
	}
	
	public static XY convert(Point point) {
		return new XY(point.x, point.y);
	}
}
