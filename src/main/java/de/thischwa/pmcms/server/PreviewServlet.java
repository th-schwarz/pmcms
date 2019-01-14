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
package de.thischwa.pmcms.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOExceptionWithCause;
import org.apache.commons.lang.StringUtils;

import de.thischwa.pmcms.conf.InitializationManager;
import de.thischwa.pmcms.exception.RenderingException;
import de.thischwa.pmcms.livecycle.SiteHolder;
import de.thischwa.pmcms.model.IRenderable;
import de.thischwa.pmcms.model.InstanceUtil;
import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.domain.pojo.ASiteResource;
import de.thischwa.pmcms.view.ViewMode;
import de.thischwa.pmcms.view.renderer.VelocityRenderer;

/**
 * Servlet to process the preview.
 *
 * @author Thilo Schwarz
 */
public class PreviewServlet extends AServlet {
	private static final long serialVersionUID = 1L;
	private VelocityRenderer velocityRenderer = InitializationManager.getBean(VelocityRenderer.class);

	@Override
	protected void doRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String idString = req.getParameter("id");
		if (StringUtils.isBlank(idString) || !StringUtils.isNumeric(idString))
			throw new IllegalArgumentException("'id' or type descriptor not found!");
		Integer id = Integer.valueOf(idString);
		
		SiteHolder siteHolder = InitializationManager.getBean(SiteHolder.class);
		APoormansObject<?> po = siteHolder.get(id);
		if(po == null)
			throw new NullPointerException("Can't work with po is null!");
		if(InstanceUtil.isSiteResource(po)) {
			try {
				String html = velocityRenderer.render((ASiteResource)po);
				resp.getWriter().append(html);
			} catch (RenderingException e) {
				throw new IOExceptionWithCause(e);
			}
		} else {
			try {
				velocityRenderer.render(resp.getWriter(), (IRenderable)po, ViewMode.PREVIEW);
			} catch (RenderingException e) {
				throw new IOExceptionWithCause(e);
			}
		}
	}

}
