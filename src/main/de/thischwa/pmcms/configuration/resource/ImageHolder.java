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
package de.thischwa.pmcms.configuration.resource;

import java.net.MalformedURLException;
import java.net.URL;


import org.eclipse.swt.graphics.Image;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.gui.treeview.TreeViewSiteRecourceNode;
import de.thischwa.pmcms.model.InstanceUtil;
import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.tool.swt.SWTUtils;

// TODO: Auto-generated Javadoc
/**
 * Hold icons using in the gui.
 *
 * @version $Id: ImageHolder.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class ImageHolder {
	
	/**
	 * Builds the url.
	 *
	 * @param srcImg the src img
	 * @return the uRL
	 * @throws MalformedURLException the malformed url exception
	 */
	private static URL buildUrl(final String srcImg) throws MalformedURLException {
		String location = String.format("file:%s/gfx/%s", Constants.APPLICATION_DIR.getAbsolutePath(), srcImg);
		return new URL(location);
	}
	
	static {
		try {
			SWTUtils.putImage("shell_icon_16x16", buildUrl("icon_16x16.gif"));
			SWTUtils.putImage("shell_icon_32x32", buildUrl("icon_32x32.gif"));

			SWTUtils.putImage("site", buildUrl("tango/site.png"));
			SWTUtils.putImage("folder", buildUrl("tango/folder.png"));
			SWTUtils.putImage("page", buildUrl("tango/page.png"));
			SWTUtils.putImage("gallery", buildUrl("tango/gallery.png"));
			SWTUtils.putImage("image", buildUrl("tango/image.png"));

			SWTUtils.putImage("macro_folder", buildUrl("tango/macro.png"));
			SWTUtils.putImage("template_folder", buildUrl("tango/template.png"));
			SWTUtils.putImage("siteresource", buildUrl("tango/siteresource-file.png"));

			SWTUtils.putImage("toolbar_backup", buildUrl("tango/backup.png"));
			SWTUtils.putImage("toolbar_export", buildUrl("tango/export.png"));
			SWTUtils.putImage("toolbar_transfer", buildUrl("tango/transfer.png"));
		} catch (MalformedURLException e) {
			new RuntimeException("Can't initialize the icons, because: " + e.getMessage(), e);
		}
	}
		
	/** The Constant SHELL_ICON_SMALL. */
	public static final Image SHELL_ICON_SMALL = SWTUtils.getImage("shell_icon_16x16");
	
	/** The Constant SHELL_ICON_BIG. */
	public static final Image SHELL_ICON_BIG = SWTUtils.getImage("shell_icon_32x32");
	
	/**
	 * Gets the image.
	 *
	 * @param po the po
	 * @return {@link Image} for the desired {@link APoorMansObject}.
	 */
	public static Image getImage(APoormansObject<?> po) {
		if (InstanceUtil.isSite(po))
			return SWTUtils.getImage("site");
		else if (InstanceUtil.isJustLevel(po))
			return SWTUtils.getImage("folder");
		else if (InstanceUtil.isGallery(po))
			return SWTUtils.getImage("gallery");
		else if (InstanceUtil.isImage(po))
			return SWTUtils.getImage("image");
		else if (InstanceUtil.isPage(po)) 
			return SWTUtils.getImage("page");
		else if (InstanceUtil.isSiteResource(po))
			return SWTUtils.getImage("siteresource");
		else if (po instanceof TreeViewSiteRecourceNode<?>) {
			TreeViewSiteRecourceNode<?> container = (TreeViewSiteRecourceNode<?>) po;
			switch(container.getResourceType()) {
				case TEMPLATE:
					return SWTUtils.getImage("template_folder");
				case MACRO:
					return SWTUtils.getImage("macro_folder");
				default:
					throw new IllegalArgumentException("Unknown type of site resource.");
			}
		}
		else 
			return null;
	}
}
