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
package de.thischwa.pmcms.view.context.object;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.view.context.IContextObjectCommon;

/**
 * Context object for building query strings for links. <br>
 * <pre>$linktool.addParameter('id', '1').addParameter('name', 'foo')</pre> generates: id=1&name=foo 
 */
@Component("linktool")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class LinkTool implements IContextObjectCommon {
	private String linkTo = null;
	private Map<String, String> params = new HashMap<String, String>();
	
	public LinkTool addParameter(String key, String value) {
		this.params.put(key, value);
		return this;
	}

	public LinkTool addParameter(String key, Serializable value) {
		addParameter(key, value.toString());
		return this;
	}

	public LinkTool setEditView() {
		clear();
		setLinkTo("/".concat(Constants.LINK_IDENTICATOR_EDIT));
		return this;
	}
	
	public LinkTool setSave() {
		clear();
		setLinkTo("/".concat(Constants.LINK_IDENTICATOR_SAVE));
		return this;
	}

	@Override
	public String toString() {
		StringBuilder link = new StringBuilder();
		String paramStr = null;

		if (this.linkTo == null)
			return "error_in_linktool-destination_not_set";

		if (!this.params.isEmpty()) {
			String amp = "&amp;";
			link.append("?");
			for (String key : this.params.keySet()) {
				link.append(key);
				link.append("=");
				link.append(this.params.get(key));
				link.append(amp);
			}
			paramStr = link.substring(0, link.length() - amp.length());
		}
		String linkString = this.linkTo.concat(StringUtils.defaultString(paramStr));
		clear();
		this.linkTo = "";
		return linkString;
	}

	private void setLinkTo(String linkTo) {
		clear();
		this.linkTo = linkTo;
	}

	private void clear() {
		this.params.clear();
	}
}
