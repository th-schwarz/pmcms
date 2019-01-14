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
package de.thischwa.pmcms.model;

import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.domain.pojo.ASiteResource;
import de.thischwa.pmcms.model.domain.pojo.Content;
import de.thischwa.pmcms.model.domain.pojo.Gallery;
import de.thischwa.pmcms.model.domain.pojo.Image;
import de.thischwa.pmcms.model.domain.pojo.Level;
import de.thischwa.pmcms.model.domain.pojo.Macro;
import de.thischwa.pmcms.model.domain.pojo.Page;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.model.domain.pojo.Template;

/**
 * Static helper class to check the instance of the database pojos.
 * 
 * @author Thilo Schwarz
 */
public class InstanceUtil {

	public static boolean isPoormansObject(Object obj) {
		return (obj instanceof APoormansObject<?>);
	}

	public static boolean isSiteResource(Object obj) {
		return (obj instanceof ASiteResource);
	}

	public static boolean isMacro(Object obj) {
		return (obj instanceof Macro);
	}

	public static boolean isTemplate(Object obj) {
		return (obj instanceof Template);
	}

	public static boolean isRenderable(Object obj) {
		return (obj instanceof IRenderable);
	}

	public static boolean isContent(Object obj) {
		return (obj instanceof Content);
	}

	public static boolean isPage(Object obj) {
		return (obj instanceof Page);
	}

	public static boolean isJustLevel(Object obj) {
		return (isLevel(obj) && !isSite(obj));
	}

	public static boolean isLevel(Object obj) {
		return (obj instanceof Level);
	}

	public static boolean isSite(Object obj) {
		return (obj instanceof Site);
	}

	public static boolean isGallery(Object obj) {
		return (obj instanceof Gallery);
	}

	public static boolean isImage(Object obj) {
		return (obj instanceof Image);
	}
}
