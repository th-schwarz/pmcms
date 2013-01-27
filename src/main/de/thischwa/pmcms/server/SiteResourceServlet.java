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

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.livecycle.SiteHolder;

/**
 * Renders the requested site resources like images, css files and so on.
 *
 * @version $Id: SiteResourceServlet.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class SiteResourceServlet extends AServlet {
	private static Logger logger = Logger.getLogger(SiteResourceServlet.class);
	private static final long serialVersionUID = 1L;
	private String sitePathPart = "/".concat(Constants.LINK_IDENTICATOR_SITE_RESOURCE);
	private File baseFile; 
	private SiteHolder siteHolder = InitializationManager.getBean(SiteHolder.class);
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		baseFile = new File(config.getInitParameter("basePath"));
		if(!baseFile.exists())
			throw new ServletException("'basePath' not found!");
		logger.info("SiteResourceServlet initialized with: " + baseFile.getAbsolutePath());
	}

	/* (non-Javadoc)
	 * @see de.thischwa.pmcms.server.AServlet#doRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		File siteDir = new File(baseFile, siteHolder.getSiteUrl());
		logger.debug(String.format("Site dir: %s", siteDir.getAbsolutePath()));
		String reqPath = req.getPathInfo();
		if(reqPath.startsWith(sitePathPart))
			reqPath = reqPath.substring(sitePathPart.length());
		logger.debug(String.format("Requested path: [%s]", reqPath));
		
		ServletUtils.establishContentType(reqPath, resp);
		File reqFile = new File(siteDir, reqPath);
		ServletUtils.writeFile(resp, reqFile);		
	}

}
