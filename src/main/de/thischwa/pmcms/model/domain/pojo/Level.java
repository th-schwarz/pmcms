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
package de.thischwa.pmcms.model.domain.pojo;

import java.util.ArrayList;
import java.util.List;


import org.apache.commons.lang.StringUtils;

import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.model.IOrderable;

/**
 * Base object for the level of a {@link Site}, a structural container element with no content. 
 * {@link Site} is inherited from this object.
 *
 * @version $Id: Level.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class Level extends APoormansObject<Level> implements IOrderable<Level> {
	protected String name;
	protected String title;
	protected List<Level> sublevels = new ArrayList<Level>();
	protected List<Page> pages = new ArrayList<Page>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


	@Override
	public String getDecorationString() {
		String deco = StringUtils.defaultIfEmpty(title, name);
		if(InitializationManager.isAdmin())
			deco = String.format("%s#%d", deco, getId());
		return deco;
	}

	public List<Level> getSublevels() {
		return sublevels;
	}
	public void setSublevels(List<Level> sublevels) {
		this.sublevels = sublevels;
	}
	
	public boolean hasSublevels() {
		return sublevels.size() > 0;
	}
	public void add(Level level) {
		// TODO check if there is already a level with the same name
		sublevels.add(level);
	}
	public boolean remove(Level level) {
		return sublevels.remove(level);
	}
	
	public List<Page> getPages() {
		return pages;
	}
	public void setPages(List<Page> pages) {
		this.pages = pages;
	}
	public void add(Page page) {
		// TODO check if there is already a page with the same name
		pages.add(page);
	}
	public boolean remove(Page page) {
		return pages.remove(page);
	}
		
	/**
	 * @return The number of the hierarchy. (Starts with 1.)
	 */
	public Integer getHierarchy() {
		return (getParent() == null) ? Integer.valueOf(1) : Integer.valueOf(getParent().getHierarchy().intValue() + 1);
	}
	
	@Override
	public String toString() {
		return getDecorationString();
	}
	
	@Override
	public List<Level> getFamily() {
		if(getParent() == null)
			return new ArrayList<Level>();
		return getParent().getSublevels();
	}
}
