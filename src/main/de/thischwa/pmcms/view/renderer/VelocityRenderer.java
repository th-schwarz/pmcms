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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.exception.FatalException;
import de.thischwa.pmcms.exception.RenderingException;
import de.thischwa.pmcms.livecycle.PojoHelper;
import de.thischwa.pmcms.livecycle.SiteHolder;
import de.thischwa.pmcms.model.IRenderable;
import de.thischwa.pmcms.model.InstanceUtil;
import de.thischwa.pmcms.model.domain.PoInfo;
import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.domain.pojo.ASiteResource;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.view.ViewMode;
import de.thischwa.pmcms.view.context.ContextObjectManager;
import de.thischwa.pmcms.view.context.object.ContextTool;
import de.thischwa.pmcms.view.context.object.Utils;
import de.thischwa.pmcms.view.context.object.admin.AdminTool;

/**
 * The velocity rendering methods.
 * 
 * TODO rewrite exception handling. all methods should throw just the RenderingException
 */
@Service()
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class VelocityRenderer {
	private static final Logger logger = Logger.getLogger(VelocityRenderer.class);	
	
	@Autowired private SiteHolder siteHolder;

	@Value("${baseurl}")
	private String baseUrl;


	/**
	 * The main render method. It renders a string with respect of possible context objects.
	 * 
	 * @param writer
	 *            Contains the rendered string. It has to be flushed and closed by the caller!
	 * @param stringToRender
	 *            A string to render.
	 * @param contextObjects
	 *            Contains the context objects. It could be null or empty too.
	 */
	public void renderString(Writer writer, final String stringToRender, final Map<String, Object> contextObjects) {
		if (StringUtils.isBlank(stringToRender))
			return;

		VelocityContext ctx = new VelocityContext(contextObjects);
		try {
			siteHolder.getVelocityEngine().evaluate(ctx, writer, "StaticRenderfield", stringToRender);
		} catch (Exception e) {
			throw new FatalException("While string rendering: " + e.getMessage(), e);
		}
	}

	/**
	 * Renders a string with respect of possible context objects.
	 * 
	 * @param stringToRender
	 *            A string to render.
	 * @param contextObjects
	 *            Context object. It could be null or empty too.
	 * @return The rendered string.
	 */
	public String renderString(final String stringToRender, final Map<String, Object> contextObjects) {
		if (StringUtils.isBlank(stringToRender))
			return "";

		StringWriter stringWriter = new StringWriter();
		renderString(stringWriter, stringToRender, contextObjects);
		stringWriter.flush();
		IOUtils.closeQuietly(stringWriter);
		return stringWriter.toString();
	}

	/**
	 * Renders an {@link IRenderable} with respect of the {@link ViewMode} and possible context objects into <code>writer</code>.
	 * 
	 * @param writer
	 *            Contains the rendered string. It has to be flushed and closed by the caller!
	 * @param renderable
	 *            The {@link IRenderable} to render.
	 * @param viewMode
	 *            The {@link ViewMode} to respect.
	 * @param additionalContextObjects
	 *            Contains the context objects. It could be null or empty.
	 * @throws RenderingException 
	 */
	public void render(Writer writer, final IRenderable renderable, final ViewMode viewMode,
			final Map<String, Object> additionalContextObjects) throws RenderingException {
		logger.debug("Try to render: " + renderable);
		PojoHelper pojoHelper = new PojoHelper();
		pojoHelper.putpo((APoormansObject<?>) renderable);
		Site site = pojoHelper.getSite();
		Map<String, Object> contextObjects = ContextObjectManager.get(pojoHelper, viewMode);
		if (logger.isDebugEnabled()) {
			logger.debug("context objects:");
			for (String objName : contextObjects.keySet()) {
				logger.debug(" - Object class: " + objName + " - " + contextObjects.get(objName).getClass());
			}
		}
		if (additionalContextObjects != null && !additionalContextObjects.isEmpty())
			contextObjects.putAll(additionalContextObjects);
		try {
			String templateContent = PoInfo.getTemplateContent(renderable);
			StringWriter contentWriter = new StringWriter();
			renderString(contentWriter, templateContent, contextObjects);

			String layoutContent = site.getLayoutTemplate().getText();
			if (layoutContent != null) {
				contentWriter.flush();
				contextObjects.put("content", contentWriter.toString());
				renderString(writer, layoutContent, contextObjects);
			} else
				writer.write(contentWriter.toString());
		} catch (IOException e) {
			throw new RenderingException(e);
		}
	}

	/**
	 * Renders a {@link ASiteResource}.
	 * 
	 * @param writer Writer where to write out the rendered result.
	 * @param siteResource
	 *            Must be pre-configured, shouldn't be null!
	 * @throws IOException
	 */
	public void render(Writer writer, final ASiteResource siteResource) throws IOException {
		logger.debug("Try to render a SiteResource.");

		File editorDir = new File(InitializationManager.getSourceEditorPath());
		Map<String, Object> contextObjects = new HashMap<String, Object>();
		contextObjects.put("admintool", new AdminTool(siteResource));
		contextObjects.put("sr", siteResource);
		contextObjects.put("utils", new Utils());
		contextObjects.put("contexttool", new ContextTool());
		if(siteResource.getId() != 0) {
			String link = String.format("%s?id=%d", Constants.LINK_IDENTICATOR_SAVE, siteResource.getId());
			contextObjects.put("link", link);
		}
		if (InstanceUtil.isMacro(siteResource)) {
			contextObjects.put("type", Constants.LINK_TYPE_MACRO);
		} else if (InstanceUtil.isTemplate(siteResource)) {
			contextObjects.put("type", Constants.LINK_TYPE_TEMPLATE);
		} else
			throw new IllegalArgumentException("Unknown type of siteresource!");

		File editor;
		editor = new File(editorDir, "editor.html");
		if (!editor.exists())
			throw new IOException("Didn't find the main source editor file!");
		String templateContent = IOUtils.toString(new BufferedInputStream(new FileInputStream(editor)));
		renderString(writer, templateContent, contextObjects);
	}


	/**
	 * A wrapper to {@link #render(Writer, ASiteResource)}.
	 */
	public String render(final ASiteResource siteResource) throws RenderingException {
		StringWriter writer = new StringWriter();
		try {
			render(writer, siteResource);
		} catch (IOException e) {
			throw new RenderingException(e);
		}
		return writer.toString();
	}
	
	/**
	 * A wrapper to {@link #render(Writer, IRenderable, ViewMode, Map)}.
	 */
	public void render(Writer writer, final IRenderable renderable, final ViewMode viewMode) throws RenderingException {
		render(writer, renderable, viewMode, null);
	}
	
	/**
	 * A wrapper to {@link #render(Writer, IRenderable, ViewMode)}
	 */
	public String render(final IRenderable renderable, final ViewMode viewMode) throws RenderingException {
		StringWriter writer = new StringWriter();
		render(writer, renderable, viewMode);
		
		return writer.toString();
	}
}
