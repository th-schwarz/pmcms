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
package de.thischwa.pmcms.livecycle;

import java.io.File;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.io.FilenameUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.thischwa.c5c.FilemanagerMessageResolver;
import de.thischwa.c5c.resource.PropertiesLoader;
import de.thischwa.c5c.util.Path;
import de.thischwa.pmcms.Constants;

/**
 * It resolves the messages of the filemanager. <br/> 
 * 
 * The path of the filemanager will be redirected to the directory of the application.
 * That's necessary because the working directory of the server is the data directory. 
 */
public class C5MessageResolverImpl extends FilemanagerMessageResolver {

	@Override
	public void setServletContext(ServletContext servletContext) throws RuntimeException {
		File msgFolder = new File(Constants.APPLICATION_DIR, new Path(PropertiesLoader.getFilemangerPath()).addFolder(scriptPath).toString());
		if(!msgFolder.exists())
			throw new RuntimeException(String.format("C5 scripts folder couldn't be found: %s", msgFolder.getAbsolutePath()));
		ObjectMapper mapper = new ObjectMapper();
		try {
			for(File file: msgFolder.listFiles(jsFilter)) {
				String lang = FilenameUtils.getBaseName(file.getName());
		        @SuppressWarnings("unchecked")
				Map<String, String> langData = mapper.readValue(file, Map.class);
				collectLangData(lang, langData);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
