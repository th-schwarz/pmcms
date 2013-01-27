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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.io.IOExceptionWithCause;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.exception.RenderingException;
import de.thischwa.pmcms.livecycle.PojoHelper;
import de.thischwa.pmcms.livecycle.SiteHolder;
import de.thischwa.pmcms.model.InstanceUtil;
import de.thischwa.pmcms.model.domain.PoInfo;
import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.domain.pojo.ASiteResource;
import de.thischwa.pmcms.model.domain.pojo.Page;
import de.thischwa.pmcms.view.ViewMode;
import de.thischwa.pmcms.view.context.ContextObjectManager;
import de.thischwa.pmcms.view.renderer.VelocityRenderer;
import de.thischwa.pmcms.wysisygeditor.CKEditorWrapper;

/**
 * Servlet to manage the different web editors.
 *
 * @version $Id: EditorServlet.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class EditorServlet extends AServlet {
	private static Logger logger = Logger.getLogger(EditorServlet.class);
	private VelocityRenderer velocityRenderer = InitializationManager.getBean(VelocityRenderer.class);
	private SiteHolder siteHolder = InitializationManager.getBean(SiteHolder.class);
	private static final long serialVersionUID = 1L;
	 
	@Override
	protected void doRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String idString = req.getParameter("id");			
		Integer id = (StringUtils.isNotBlank(idString)) ? Integer.valueOf(idString) : null;
		Class<?> cls = ContextUtil.getClass(req);

		APoormansObject<?> po;
		if (id != null && id != APoormansObject.UNSET_VALUE)
			po = siteHolder.get(id);
		else
			try {
				po = (APoormansObject<?>) cls.newInstance();
			} catch (Exception e) {
				throw new IOExceptionWithCause(e);
			}
			
		if(InstanceUtil.isSiteResource(po)) {
			try {
				String html = velocityRenderer.render((ASiteResource)po);
				resp.getWriter().append(html);
			} catch (RenderingException e) {
				throw new IOExceptionWithCause(e);
			}
		} else {
			Page page = (Page) po;
			logger.debug("Try to create an editor for: " + page);
			PojoHelper pojoHolder = new PojoHelper();
			pojoHolder.putpo(page);
			Map<String, Object> addObjs = new HashMap<String, Object>();
			addObjs.put("editor", new CKEditorWrapper(PoInfo.getSite(page), req));
			Map<String, Object> ctxObjs = ContextObjectManager.get(pojoHolder, ViewMode.EDIT);
			ctxObjs.putAll(addObjs);
			
			try {
				velocityRenderer.render(resp.getWriter(), page, ViewMode.EDIT, ctxObjs);
			} catch (RenderingException e) {
				throw new IOExceptionWithCause(e);
			}
		}

		resp.setHeader("Cache-Control", "no-cache");
	}
}