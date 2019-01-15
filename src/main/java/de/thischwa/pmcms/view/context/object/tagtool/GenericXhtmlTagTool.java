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
package de.thischwa.pmcms.view.context.object.tagtool;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * Helper to construct a common xhtml-tag.
 */
public class GenericXhtmlTagTool {
	protected Map<String, String> attributes = new LinkedHashMap<String, String>();
	private String name = null;
	private String value = null;
	
	private boolean usedFromEditor = false;
	
	protected GenericXhtmlTagTool(final String name) {
		if(StringUtils.isBlank(name))
			throw new IllegalArgumentException("The name of the tag must be set!");
		this.name = name;
	}
	
	protected void setValue(final String value) {
		this.value = value;
	}
	
	protected void putAttr(final String key, final String value) {
		attributes.put(key, value);
	}
	
	protected String getAttr(final String key) {
		return attributes.get(key);
	}
	
	protected boolean isUsedFromEditor() {
		return usedFromEditor;
	}
	
	/**
	 * Setter to signal that the inherited tag-tool was used inside the wysiwyg-editor. 
	 * For internal use only! 
	 * 
	 * @param usedFromEditor
	 */
	protected void setUsedFromEditor(boolean usedFromEditor) {
		this.usedFromEditor = usedFromEditor;
	}
		
	/**
	 * Does the basic construction of the tag.
	 */
	protected String contructTag() {
		if (attributes.isEmpty())
			throw new IllegalArgumentException("Attributes are missing!");
		StringBuilder tag = new StringBuilder();
		tag.append("<");
		tag.append(name);
		for (String key : attributes.keySet()) {
			tag.append(String.format(" %s=\"%s\"", key, StringUtils.defaultString(attributes.get(key))));
		}
		if (StringUtils.isBlank(value))
			tag.append(" />");
		else 
			tag.append(">").append(value).append('<').append('/').append(name).append('>');
		
		attributes.clear();
		return tag.toString();
	}
}
