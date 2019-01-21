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

import org.apache.commons.lang3.StringUtils;

/**
 * Base object for the templates of a {@link Site}.
 *
 * @author Thilo Schwarz
 */
public class Template extends ASiteResource {
	public final String LAYOUT = "layout.html";
	
	private TemplateType type;
	
	public TemplateType getType() {
		return type;
	}
	public void setType(TemplateType type) {
		this.type = type;
	}
	
	@Override
	public SiteResourceType getResourceType() {
		return SiteResourceType.TEMPLATE;
	}
	
	public boolean isLayoutTemplate() {
		return StringUtils.equals(LAYOUT, getName());
	}
}
