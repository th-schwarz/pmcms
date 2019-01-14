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
package de.thischwa.pmcms.gui.treeview;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.model.domain.pojo.SiteResourceType;
import de.thischwa.pmcms.model.domain.pojo.Template;


/**
 * A dummy root object for the site treeviewer. A dummy root is needed, because TreeViewer doesn't view any root object,
 * but we need to see our {@link de.thischwa.pmcms.model.domain.pojo.Site} as root. It handles some extra objects to show
 * some resources in an extra section, e.g. macros and templates. 
 */
public class TreeViewRootNode extends APoormansObject<Object> {
	private Site site;
	private TreeViewTemplateNode templateNode;
	private TreeViewMacroNode macroNode;

	public TreeViewRootNode(final Site site) {
		this.site = site;
		List<Template> templates = new ArrayList<Template>();
		if(site.getLayoutTemplate() != null)
			templates.add(site.getLayoutTemplate());
		if(CollectionUtils.isNotEmpty(site.getTemplates()))
			templates.addAll(site.getTemplates());
		templateNode = new TreeViewTemplateNode(this, SiteResourceType.TEMPLATE, templates);
		macroNode = new TreeViewMacroNode(this, SiteResourceType.MACRO, site.getMacros());
	}

	public Site getSite() {
		return site;
	}
	
	public TreeViewTemplateNode getTemplateNode() {
		return templateNode;
	}
	
	public TreeViewMacroNode getMacroNode() {
		return macroNode;
	}
	
	@Override
	public APoormansObject<Object> getParent() {
		return null;
	}
	
	@Override
	public void setParent(Object parent) {
		super.setParent(null);
	}
	
	@Override
	public String getDecorationString() {
		return "I'm the dummy root object!";
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		return this.getClass().equals(obj.getClass());
	}

	@Override
	public String toString() {
		return getDecorationString();
	}

}
