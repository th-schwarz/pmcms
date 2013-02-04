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
package de.thischwa.pmcms.wysisygeditor;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.thischwa.ckeditor.CKEditor;
import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.configuration.PropertiesManager;
import de.thischwa.pmcms.model.domain.PoPathInfo;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.tool.PathTool;
import de.thischwa.pmcms.tool.Utils;

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
	private File configDir;
	private String urlConfigFolder;
	private HttpServletRequest httpServletRequest;

	public CKEditorWrapper(final Site site, final HttpServletRequest httpServletRequest) {
		if (site == null ||  httpServletRequest == null)
			throw new IllegalArgumentException("One or more arguments are null!");
		configDir = PoPathInfo.getSiteConfigurationDirectory(site);
		urlConfigFolder = Utils.join(Constants.LINK_IDENTICATOR_SITE_RESOURCE, "/", configDir.getName(), "/");
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
			editor.setToolbarName(toolbarName);
		editor.setProperty("customConfig", buildUrlFolder(urlConfigFolder, "ckconfig.js"));
		editor.setProperty("contentsCss", buildUrlFolder(Constants.LINK_IDENTICATOR_SITE_RESOURCE, "/format.css")); // TODO respect new layout dir!
		editor.setProperty("filebrowserBrowseUrl", InitializationManager.getBean(PropertiesManager.class).getProperty("pmcms.filemanager.url"));
		editor.setValue(value);
		return editor.createHtml();
	}
	
	private String buildUrlFolder(String path, String file) {
		String tmpPath = PathTool.getURLFromFile(path + file);
		return tmpPath;
	}
}
