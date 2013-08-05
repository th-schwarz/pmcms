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

import de.thischwa.pmcms.conf.InitializationManager;



/**
 * Base class for resources of a {@link Site}.
 *
 * @author Thilo Schwarz
 */
public abstract class ASiteResource extends APoormansObject<Site> {
	private String name;
	private String text;

	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	@Override
	public String getDecorationString() {
		String deco = new String(name);
		if(InitializationManager.isAdmin())
			deco = String.format("%s#%d", deco, getId());
		return deco;
	}
	
	@Override
	public String toString() {
		return String.format("%s#%s", name, getResourceType());
	}
	
	public abstract SiteResourceType getResourceType();
}

