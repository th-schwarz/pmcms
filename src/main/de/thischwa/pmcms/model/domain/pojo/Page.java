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
import de.thischwa.pmcms.model.IRenderable;

/**
 * Base object for a page.
 * {@link Gallery} is inherited from this object. 
 * 
 * @version $Id: Page.java 2216 2012-07-14 15:48:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class Page extends APoormansObject<Level> implements IRenderable, IOrderable<Page> {
	protected String name;
	protected String title;
	private Template template;
	private List<Content> content = new ArrayList<Content>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public Template getTemplate() {
		return this.template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

	public List<Content> getContent() {
		return this.content;
	}

	public void setContent(List<Content> content) {
		this.content = content;
	}
	
	public void add(Content content) {
		this.content.add(content);
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
	
	@Override
	public TemplateType getTemplateType() {
		return TemplateType.PAGE;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public List<Page> getFamily() {
		return getParent().getPages();
	}
}
