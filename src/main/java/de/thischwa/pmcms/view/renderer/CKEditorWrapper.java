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
package de.thischwa.pmcms.view.renderer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import codes.thischwa.ckeditor.CKEditor;
import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.conf.InitializationManager;
import de.thischwa.pmcms.conf.PropertiesManager;
import de.thischwa.pmcms.model.domain.pojo.Site;

/**
 * Construct the CKeditor.<br />
 * Following files are loading automatically:
 * <ul>
 * <li>configurationpath/ckconfig.js</li>
 * <li>format.css (= setting the EditorAreaCSS - css using inside the editor)</li>
 * </ul>
 */
public class CKEditorWrapper {
	private static Logger logger = Logger.getLogger(CKEditorWrapper.class);
	private String urlCustomConfig;
	private String urlDefaultCss;
	private HttpServletRequest httpServletRequest;
	private PropertiesManager pm = InitializationManager.getBean(PropertiesManager.class);

	public CKEditorWrapper(final Site site, final HttpServletRequest httpServletRequest) {
		if (site == null ||  httpServletRequest == null)
			throw new IllegalArgumentException("Base params are incomplete!");
		urlDefaultCss = String.format("%s/%s/format.css", Constants.LINK_IDENTICATOR_SITE_RESOURCE, pm.getSiteProperty("pmcms.site.dir.resources.layout"));
		urlCustomConfig = String.format("/%s/%s/ckconfig.js", Constants.LINK_IDENTICATOR_SITE_RESOURCE, pm.getProperty("pmcms.dir.site.configuration"));
		this.httpServletRequest = httpServletRequest;
	}

	public String get(final String fieldName, final String value) {
		return get(fieldName, value, null, null, null);
	}

	public String get(final String fieldName, final String value, final String width, final String height) {
		return get(fieldName, value, width, height, null);
	}

	public String get(final String fieldName, final String value, final String width, final String height, final String toolbarName) {
		logger.debug("Try to create editor for field: " + fieldName);
		CKEditor editor = new CKEditor(httpServletRequest, fieldName);
		if (StringUtils.isNotBlank(width) && StringUtils.isNotBlank(height))
			editor.setSize(width, height);
		if (StringUtils.isNotBlank(toolbarName))
			editor.setProperty("toolbar", toolbarName);
		else
			editor.removeProperty("toolbar");
		editor.setProperty("customConfig", urlCustomConfig);
		editor.setProperty("contentsCss", urlDefaultCss); 
		editor.setProperty("baseHref", pm.getProperty("baseurl"));
		editor.setProperty("filebrowserBrowseUrl", pm.getProperty("pmcms.filemanager.url"));
		// TODO set height and width of the filebrowser
		editor.setValue(value);
		return editor.toString();
	}
}
