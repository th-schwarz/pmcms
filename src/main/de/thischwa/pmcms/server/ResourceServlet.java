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
package de.thischwa.pmcms.server;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Servlet to stream the requested resource. The content type is set according to the extension of the requested source.
 */
public class ResourceServlet extends AServlet {
	private static Logger logger = Logger.getLogger(ResourceServlet.class);
	private static final long serialVersionUID = 1L;
	private File basePath;

	@Override
	public void init(ServletConfig config) throws ServletException {
		basePath = new File(config.getInitParameter("basePath"));
		if(!basePath.exists())
			throw new ServletException("'basePath' not found!");
		logger.info("ResourceServlet initialized for the directory: " + basePath);
	}
	
	
	@Override
	protected void doRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String reqPath = req.getPathInfo();
		logger.debug(String.format("Requested path: [%s]", reqPath));
		
		ServletUtils.establishContentType(reqPath, resp);
		File reqFile = new File(basePath, reqPath);
		ServletUtils.writeFile(resp, reqFile);		
	}
}
