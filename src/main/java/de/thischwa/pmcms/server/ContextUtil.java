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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.conf.InitializationManager;
import de.thischwa.pmcms.exception.FatalException;
import de.thischwa.pmcms.exception.RenderingException;
import de.thischwa.pmcms.livecycle.SiteHolder;
import de.thischwa.pmcms.model.IRenderable;
import de.thischwa.pmcms.model.domain.PoInfo;
import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.domain.pojo.ASiteResource;
import de.thischwa.pmcms.model.domain.pojo.Content;
import de.thischwa.pmcms.model.domain.pojo.Gallery;
import de.thischwa.pmcms.model.domain.pojo.Image;
import de.thischwa.pmcms.model.domain.pojo.Macro;
import de.thischwa.pmcms.model.domain.pojo.Page;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.model.domain.pojo.Template;
import de.thischwa.pmcms.model.domain.pojo.TemplateType;
import de.thischwa.pmcms.tool.Link;
import de.thischwa.pmcms.view.renderer.VelocityUtils;

/**
 * Static helper using in the viewing context.
 */
public class ContextUtil {
	private static Logger logger = Logger.getLogger(ContextUtil.class);
	private static SiteHolder siteHolder = InitializationManager.getBean(SiteHolder.class);

	public static Class<?> getClass(HttpServletRequest req) {
		String pojoType = req.getParameter(Constants.LINK_TYPE_DESCRIPTOR);
		return getClass(pojoType);
	}
	
	public static Class<?> getClass(final String pojoType) {
		if (pojoType.equals(Constants.LINK_TYPE_PAGE))
			return Page.class;
		if (pojoType.equals(Constants.LINK_TYPE_GALLERY))
			return Gallery.class;
		if (pojoType.equals(Constants.LINK_TYPE_IMAGE))
			return Image.class;
		if (pojoType.equals(Constants.LINK_TYPE_MACRO))
			return Macro.class;
		if (pojoType.equals(Constants.LINK_TYPE_TEMPLATE))
			return Template.class;
		else
			throw new IllegalArgumentException("Unknown pojo TYPE in link: " + pojoType);
	}

	public static String getTypDescriptor(final Class<?> clazz) {
		if(clazz.equals(Page.class))
			return Constants.LINK_TYPE_PAGE;
		if(clazz.equals(Gallery.class))
			return Constants.LINK_TYPE_GALLERY;
		if(clazz.equals(Image.class))
			return Constants.LINK_TYPE_IMAGE;
		if(clazz.equals(Macro.class))
			return Constants.LINK_TYPE_MACRO;
		if(clazz.equals(Template.class))
			return Constants.LINK_TYPE_TEMPLATE;
		throw new IllegalArgumentException("Unknown pojo class: " + clazz.getSimpleName());
	}
	
	public static APoormansObject<?> getpo(HttpServletRequest req) {
		String idString = req.getParameter("id");
		return getpo(idString);
	}
	public static APoormansObject<?> getpo(Link link) {
		String idString = link.getParameter("id");
		return getpo(idString);
	}
	public static APoormansObject<?> getpo(final String idString) {
		if (StringUtils.isBlank(idString) || !StringUtils.isNumeric(idString))
			throw new IllegalArgumentException("'id' not found!");

		int id = Integer.valueOf(idString);
		return siteHolder.get(id);
	}


	public static IRenderable getRenderable(HttpServletRequest req) {
		APoormansObject<?> po = getpo(req);
		return (!(po instanceof ASiteResource)) ? (IRenderable) po : null;
	}
	
	public static APoormansObject<?> updatepo(final Link link) throws FatalException {
		return updatepo(link.getParameters());
	}
	
	public static APoormansObject<?> updatepo(final Map<String, String> params) {
		String idString = params.get("id");
		String pojoType = params.get(Constants.LINK_TYPE_DESCRIPTOR);
		if (pojoType == null)
			throw new IllegalArgumentException("No type descriptor found!");

		Site site = siteHolder.getSite();
		APoormansObject<?> po = null;
		try {
			if(!idString.equals(APoormansObject.UNSET_VALUE+"")) 
				po = getpo(idString);
		} catch (IllegalArgumentException e) {
			throw new FatalException(String.format("Couldn't find PO for id=%s", idString));
		}

		if (pojoType.equals(Constants.LINK_TYPE_MACRO) || pojoType.equals(Constants.LINK_TYPE_TEMPLATE)) {
			String id = params.get("id");
			String name = params.get("name");
			String code = params.get("code");
			if (pojoType.equals(Constants.LINK_TYPE_MACRO)) {
				logger.debug("Identify a Macro.");
				Macro macro;
				if (StringUtils.isNotBlank(id) && !id.equals(APoormansObject.UNSET_VALUE+"")) {
					macro = (Macro) po;
				} else {
					macro = new Macro();
					macro.setParent(site);
					site.add(macro);
					siteHolder.mark(macro);
				}
				macro.setName(name);
				macro.setText(code);
				po = macro;
				logger.debug("Saved/updated Macro: " + macro.getDecorationString());
			} else if (pojoType.equals(Constants.LINK_TYPE_TEMPLATE) ) {
				logger.debug("Identify a Template.");
				Template template;
				if (StringUtils.isNotBlank(id) && !id.equals(APoormansObject.UNSET_VALUE+"")) {
					template = (Template) po;	
				} else {
					template = new Template();
					template.setParent(site);
					if(template.isLayoutTemplate())
						site.setLayoutTemplate(template);
					else
						site.add(template);
					siteHolder.mark(template);
				}
				TemplateType type = null;
				if (!name.equals("layout.html")) // TODO think about a nicer way to ensure layout.html has type null
					type = TemplateType.getType(params.get("type"));
				template.setType(type);
				template.setName(name);
				template.setText(code);
				po = template;
				logger.debug("Saved/updated Template: " + template.getDecorationString());
			}
			siteHolder.reconfigVelocityEngine();
			
		} else { // must be a page
			String editFieldDescriptor = params.get(Constants.LINK_EDITFIELDS_DESCRIPTOR);
			if (CollectionUtils.isEmpty(ContextUtil.getEditableFields(editFieldDescriptor))) {
				logger.warn("Can't resolve any form field!");
				return null;
			}

			Page page = (Page) po;
			for (String fieldName : ContextUtil.getEditableFields(editFieldDescriptor)) {
				String value = params.get(fieldName);

				Content content = PoInfo.getContentByName(page, fieldName);
				if (content == null) {
					content = new Content();
					content.setName(fieldName);
					content.setParent(page);
					page.add(content);
					logger.debug("No field was found, constructed a new one.");
				}

				// pre-render the content
				try {
					content.setValue(VelocityUtils.replaceTags(PoInfo.getSite(page), value));
				} catch (RenderingException e) {
					throw new FatalException(e);
				}
				
				logger.debug("Saved/updated field: " + content.getName());
			}
		}
		
		return po;
	}
	
	/**
	 * @return A list of editable field names of a form, or null.
	 */
	private static List<String> getEditableFields(String fieldDescriptor) {
		String[] paramFields = StringUtils.split(fieldDescriptor, ',');
		if (paramFields == null || paramFields.length < 1)
			return null;
		return Arrays.asList(paramFields);
	}

}
