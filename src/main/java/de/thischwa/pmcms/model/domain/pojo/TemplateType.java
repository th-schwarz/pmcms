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
package de.thischwa.pmcms.model.domain.pojo;


import org.apache.commons.lang.StringUtils;

import de.thischwa.pmcms.model.IRenderable;

/**
 * Type of template for {@link IRenderable}s.
 *
 * @author Thilo Schwarz
 */
public enum TemplateType {
	PAGE,
	GALLERY,
	IMAGE;
		
	public static TemplateType getType(final String name) {
		if (StringUtils.isEmpty(name))
			return null;
		for (TemplateType type : TemplateType.values()) {
			if (type.toString().toLowerCase().equals(name.toLowerCase()))
				return type;
		}
		return null;
	}
}
