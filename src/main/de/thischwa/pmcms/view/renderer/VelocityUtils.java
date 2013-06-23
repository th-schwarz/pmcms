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

import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.configuration.PropertiesManager;
import de.thischwa.pmcms.exception.FatalException;
import de.thischwa.pmcms.exception.RenderingException;
import de.thischwa.pmcms.livecycle.SiteHolder;
import de.thischwa.pmcms.model.domain.pojo.Macro;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.tool.Link;
import de.thischwa.pmcms.tool.file.FileTool;
import de.thischwa.pmcms.tool.image.Dimension;
import de.thischwa.pmcms.view.renderer.resource.VirtualFile;
import de.thischwa.pmcms.view.renderer.resource.VirtualImage;

/**
 * Place for static helper methods with respect to velocity.
 *
 * @author Thilo Schwarz
 */
public class VelocityUtils {
	private static Logger logger = Logger.getLogger(VelocityUtils.class);

	private static final String ampReplacer = "_amp-replacer_";
	
	/** Layout properties for the xhtml output. */
	static OutputFormat sourceFormat;

	static {
		// format properties for the xhtml field value
		sourceFormat = new OutputFormat();
		sourceFormat.setEncoding(Constants.STANDARD_ENCODING);
		sourceFormat.setNewlines(false);
	}
	
	private VelocityUtils() {}
	
	/**
	 * Initialization of a VelocityEngine for a {@link Site}. <i>It is necessary to have different {@link VelocityEngine}s because we need different
	 * configurations for each site, e.g. velocity macros. </i> <br>
	 * The VelocityEngines are managed by the {@link SiteHolder}, so the engine can live and die with the site object.
	 * 
	 * @param site
	 * @return VelocityEngine
	 */
	public static VelocityEngine getSiteEngine(final Site site) {
		logger.debug("Entered initEngine.");
		Properties commonProperties = InitializationManager.getBean(PropertiesManager.class).getVelocityProperties();
		VelocityEngine velocityEngine = new VelocityEngine();
	
		try {
			velocityEngine.init(commonProperties);
			StringResourceRepository repo = StringResourceLoader.getRepository();
	
			// add site specific macros
			if (site.getMacros() != null)
				for (Macro macro : site.getMacros())
					repo.putStringResource(macro.getName(), macro.getText());
			
			// add content.vm
			repo.putStringResource("content.vm", FileTool.toString(new File(InitializationManager.getDefaultResourcesPath(), "content.vm")));
			logger.info("*** VelocityEngine is initialized for: ".concat(site.getUrl()));
		} catch (Exception e) {
			throw new FatalException("Error initializing a VelocityEngine for: " + site.getUrl(), e);
		}
		return velocityEngine;
	}

	/**
	 * Replace the img-tag and a-tag with the equivalent velocity macro. Mainly used before saving a field value to the database.
	 * 
	 * @throws RenderingException
	 *             If any exception was thrown while replacing the tags.
	 */
	@SuppressWarnings("unchecked")
	public static String replaceTags(final Site site, final String oldValue) throws RenderingException {
		if (StringUtils.isBlank(oldValue))
			return null;
	
		// 1. add a root element (to have a proper xml) and replace the ampersand
		String newValue = String.format("<dummytag>\n%s\n</dummytag>", StringUtils.replace(oldValue, "&", ampReplacer));
		Map<String, String> replacements = new HashMap<String, String>();
	
		try {
			Document dom = DocumentHelper.parseText(newValue);
			dom.setXMLEncoding(Constants.STANDARD_ENCODING);
	
			// 2. Collect the keys, identify the img-tags.
			List<Node> imgs = dom.selectNodes("//img", ".");
			for (Node node : imgs) {
				Element element = (Element) node;
				if (element.attributeValue("src").startsWith("/")) // only internal links have to replaced with a velocity macro
					replacements.put(node.asXML(), generateVelocityImageToolCall(site, element.attributeIterator()));
			}
	
			// 3. Collect the keys, identify the a-tags
			List<Node> links = dom.selectNodes("//a", ".");
			for (Node node : links) {
				Element element = (Element) node;
				if (element.attributeValue("href").startsWith("/")) // only internal links have to replaced with a velocity macro
					replacements.put(element.asXML(), generateVelocityLinkToolCall(site, element));
			}
	
			// 4. Replace the tags with the velomacro.
			StringWriter stringWriter = new StringWriter();
			XMLWriter writer = new XMLWriter(stringWriter, sourceFormat);
			writer.write(dom.selectSingleNode("dummytag"));
			writer.close();
			newValue = stringWriter.toString();
			for (String stringToReplace : replacements.keySet())
				newValue = StringUtils.replace(newValue, stringToReplace, replacements.get(stringToReplace));
			newValue = StringUtils.replace(newValue, "<dummytag>", "");
			newValue = StringUtils.replace(newValue, "</dummytag>", "");
	
		} catch (Exception e) {
			throw new RenderingException("While preprocessing the field value: " + e.getMessage(), e);
		}
	
		return StringUtils.replace(newValue, ampReplacer, "&");
	}

	/**
	 * Construct the {@link de.thischwa.pmcms.view.context.object.tagtool.LinkTagTool}-call.
	 */
	@SuppressWarnings("unchecked")
	private static String generateVelocityLinkToolCall(final Site site, final Element tagElement) {
		StringBuilder veloMacro = new StringBuilder();
		final Pattern aTagPattern = Pattern.compile("<a\\b[^>]*>(.*?)</a>");
		Map<String, String> attr = new HashMap<String, String>();
		veloMacro.append("$linktagtool");
		for (Iterator<Attribute> iter = tagElement.attributeIterator(); iter.hasNext();) {
			Attribute attribute = iter.next();
			attr.put(attribute.getName(), attribute.getValue());
		}
	
		String href = attr.get("href");
		Link link = InitializationManager.getBean(Link.class);
		link.init(href);
		if (link.isExternal()) {
			veloMacro.append(".setHref(\"").append(href).append("\")");
		} else {
			VirtualFile fileResource = new VirtualFile(site, false);
			fileResource.consructFromTagFromView(href);
			veloMacro.append(".setHref(\"").append(fileResource.getTagSrcForPreview()).append("\")");
		}
	
		String value;
		Matcher matcher = aTagPattern.matcher(tagElement.asXML());
		if (matcher.matches())
			value = matcher.group(1);
		else
			value = tagElement.getText();
		veloMacro.append(".setTagValue(\"").append(value).append("\")");
	
		for (String key : attr.keySet()) {
			if (!key.equals("href"))
				veloMacro.append(".setAttribute(\"").append(key).append("\", \"").append(attr.get(key)).append("\")");
		}
	
		return veloMacro.toString();
	}

	/**
	 * Construct the {@link de.thischwa.pmcms.view.context.object.tagtool.ImageTagTool}-call.
	 * @throws RenderingException 
	 */
	private static String generateVelocityImageToolCall(final Site site, final Iterator<Attribute> attrIter) throws RenderingException {
		StringBuilder veloMacro = new StringBuilder();
		Map<String, String> attr = new HashMap<String, String>();
		veloMacro.append("$imagetagtool");
		veloMacro.append(".usedFromEditor()");
		for (Iterator<Attribute> iter = attrIter; iter.hasNext();) {
			Attribute attribute = iter.next();
			attr.put(attribute.getName(), attribute.getValue());
		}
	
		Dimension dim = Dimension.getDimensionFromAttr(attr);
		VirtualImage imageResource = new VirtualImage(site, false, false);
		imageResource.consructFromTagFromView(attr.get("src"));
		imageResource.setDimension(dim);
		veloMacro.append(".setSrc(\"").append(imageResource.getTagSrcForPreview()).append("\")");
		
		for (String key : attr.keySet()) {
			if (!key.equals("src"))
				veloMacro.append(".putAttribute(\"").append(key).append("\", \"").append(attr.get(key)).append("\")");
		}
		return veloMacro.toString();
	}

}
