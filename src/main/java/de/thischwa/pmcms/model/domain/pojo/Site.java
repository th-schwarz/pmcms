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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.thischwa.pmcms.model.InstanceUtil;


/**
 * Base object for the site.
 */
public class Site extends Level {
	public static final String PROPKEY_SERVERURI = "serveruri";
	
	private String url;
	private List<Template> templates = new ArrayList<Template>();
	private List<Macro> macros = new ArrayList<Macro>();
	private Template layoutTemplate;
	
	@Override
	public String getDecorationString() {
		String deco = StringUtils.defaultIfEmpty(title, name);
		return deco;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Template> getTemplates() {
		return templates;
	}

	public void setTemplates(List<Template> templates) {
		this.templates = templates;
	}

	public List<Macro> getMacros() {
		return macros;
	}

	public void setMacros(List<Macro> macros) {
		this.macros = macros;
	}

	public void add(Template template) {
		// TODO check if there is already a template with the same name
		templates.add(template);
	}

	public void add(Macro macro) {
		// TODO check if there is already a macro with the same name
		macros.add(macro);
	}
	
	public Template getLayoutTemplate() {
		return layoutTemplate;
	}
	
	public void setLayoutTemplate(Template layoutTemplate) {
		this.layoutTemplate = layoutTemplate;
	}

	public void remove(ASiteResource res) {
		if (InstanceUtil.isTemplate(res))
			templates.remove(res);
		else if (InstanceUtil.isMacro(res))
			macros.remove(res);
		else
			throw new IllegalArgumentException("unknown site resource type");
	}

	@Override
	public String toString() {
		return url;
	}
}
