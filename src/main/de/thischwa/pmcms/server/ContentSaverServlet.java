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

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.io.IOExceptionWithCause;
import org.apache.log4j.Logger;

import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.exception.RenderingException;
import de.thischwa.pmcms.model.IRenderable;
import de.thischwa.pmcms.model.InstanceUtil;
import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.domain.pojo.ASiteResource;
import de.thischwa.pmcms.tool.Utils;
import de.thischwa.pmcms.view.ViewMode;
import de.thischwa.pmcms.view.renderer.VelocityRenderer;

/**
 * Saves content of pages an siteresources.
 * 
 * @version $Id: ContentSaverServlet.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class ContentSaverServlet extends AServlet {
	private static Logger logger = Logger.getLogger(ContentSaverServlet.class);
	private static final long serialVersionUID = 1L;
	private VelocityRenderer velocityRenderer = InitializationManager.getBean(VelocityRenderer.class);

	@Override
	protected void doRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, String> params = new HashMap<String, String>();
		Enumeration<?> paramNames = req.getParameterNames();
		while (paramNames.hasMoreElements()) {
			Object paramName = paramNames.nextElement();
			String name = Utils.stripNonValidXMLCharacters(paramName.toString());
			String value = Utils.stripNonValidXMLCharacters(req.getParameter(name));
			params.put(name, value);
		}
		APoormansObject<?> po = ContextUtil.updatepo(params);
		logger.info("Saved pojo: " + po.toString());

		try {
			if (InstanceUtil.isSiteResource(po))
				velocityRenderer.render(resp.getWriter(), (ASiteResource) po);
			else
				velocityRenderer.render(resp.getWriter(), (IRenderable) po, ViewMode.PREVIEW);
		} catch (RenderingException e) {
			throw new IOExceptionWithCause(e);
		}
		
		resp.setHeader("Cache-Control", "no-cache");
	}

}
