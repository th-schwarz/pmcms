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
package de.thischwa.pmcms.model.tool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Node;
import org.eclipse.core.runtime.IProgressMonitor;

import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.configuration.resource.LabelHolder;
import de.thischwa.pmcms.livecycle.SiteHolder;
import de.thischwa.pmcms.model.domain.pojo.Content;
import de.thischwa.pmcms.model.domain.pojo.Gallery;
import de.thischwa.pmcms.model.domain.pojo.Image;
import de.thischwa.pmcms.model.domain.pojo.Level;
import de.thischwa.pmcms.model.domain.pojo.Macro;
import de.thischwa.pmcms.model.domain.pojo.Page;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.model.domain.pojo.Template;
import de.thischwa.pmcms.model.domain.pojo.TemplateType;

/**
 * Backup parser for db.xml without a version number (<2.4.2, !={@link IBackupParser#DBXML_1}).
 *
 * @author Thilo Schwarz
 */
public class BackupParser_old implements IBackupParser {
	private static Logger logger = Logger.getLogger(BackupParser_old.class);
	private SiteHolder siteHolder = InitializationManager.getBean(SiteHolder.class);
	private IProgressMonitor monitor = null;
	private Map<String, Template> templateCache = new HashMap<String, Template>(); 
	private Site site;
	private Element root;
	
	public BackupParser_old(final Element root) {
		logger.info("BackupParser_old entered.");
		this.root = root;
	}

	@Override
	public void setMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	@Override
	public void run() throws Exception {
		int pageCount = 0;
		siteHolder.clear();
		
		// counting elements
		pageCount += root.selectNodes("//page", ".").size();
		pageCount += root.selectNodes("//gallery", ".").size();
		pageCount += root.selectNodes("//image", ".").size();
		if (monitor != null)
			monitor.beginTask(LabelHolder.get("task.site.backup.read.monitor").concat(String.valueOf(pageCount)), pageCount);

		Element transfer = (Element) root.selectSingleNode("transfer");
		site = new Site();
		site.setUrl(root.attributeValue("url"));
		site.setTitle(root.selectSingleNode("title").getText());
		site.setTransferHost(transfer.attributeValue("host"));
		site.setTransferLoginUser(transfer.attributeValue("user"));
		site.setTransferLoginPassword(transfer.attributeValue("password"));
		site.setTransferStartDirectory(transfer.attributeValue("startdir"));

		@SuppressWarnings("unchecked")
		List<Node> macros = root.selectNodes("macro");
		for (Node node : macros) {
			Macro macro = new Macro();
			macro.setName(node.selectSingleNode("name").getText());
			macro.setText(node.selectSingleNode("text").getText());
			macro.setParent(site);
			site.add(macro);
		}

		@SuppressWarnings("unchecked")
		List<Node> nodes = root.selectNodes("template");
		for (Node node : nodes) {
			Template template = new Template();
			template.setType(TemplateType.getType(((Element) node).attributeValue("type")));
			template.setName(node.selectSingleNode("name").getText());
			template.setText(node.selectSingleNode("text").getText());
			template.setParent(site);
			if(template.isLayoutTemplate())
				site.setLayoutTemplate(template);
			else {
				site.add(template);
				templateCache.put(template.getName(), template);
			}
		}

		Element welcomePage = (Element) root.selectSingleNode("page");
		if (welcomePage != null) {
			importPage(site, welcomePage);
		} else {
			welcomePage = (Element) root.selectSingleNode("gallery");
			if(welcomePage != null)
				importGallery(site, welcomePage);
		}

		@SuppressWarnings("unchecked")
		List<Node> levels = root.selectNodes("level");
		for (Node node : levels) {
			importLevel(site, node);
		}
	}

	private void importLevel(Level parentLevel, Node levelNode) {
		Element levelElement = (Element) levelNode;
		Level level = new Level();
		level.setName(levelElement.attributeValue("name"));
		level.setTitle(levelElement.selectSingleNode("title").getText());
		level.setParent(parentLevel);
		parentLevel.add(level);

		@SuppressWarnings("unchecked")
		List<Node> pages = levelNode.selectNodes("page|gallery");
		for (Node node : pages) {
			if (node.getName().equals("page"))
				importPage(level, node);
			else
				importGallery(level, node);
		}

		@SuppressWarnings("unchecked")
		List<Node> subLevels = levelNode.selectNodes("level");
		for (Node node : subLevels) {
			importLevel(level, node);
		}
	}

	private void importGallery(Level parent, Node galleryNode) {
		Element galleryElement = (Element) galleryNode;
		Gallery gallery = new Gallery();
		gallery.setParent(parent);
		parent.add(gallery);
		gallery.setName(galleryElement.attributeValue("name"));
		if (galleryElement.selectSingleNode("title") != null && StringUtils.isNotBlank(galleryElement.selectSingleNode("title").getText()))
			gallery.setTitle(galleryElement.selectSingleNode("title").getText());
		gallery.setTemplate(templateCache.get(galleryElement.attributeValue("templateFile")));
		gallery.setThumbnailMaxWidth(Integer.parseInt(galleryElement.attributeValue("thumbnailMaxWidth")));
		gallery.setThumbnailMaxHeight(Integer.parseInt(galleryElement.attributeValue("thumbnailMaxHeight")));
		gallery.setImageTemplate(templateCache.get(galleryElement.selectSingleNode("imageTemplate").getText()));
		gallery.setImageMaxWidth(Integer.parseInt(galleryElement.attributeValue("imageMaxWidth")));
		gallery.setImageMaxHeight(Integer.parseInt(galleryElement.attributeValue("imageMaxHeight")));
		incProgressValue();
		
		@SuppressWarnings("unchecked")
		List<Node> contentNodes = galleryNode.selectNodes("content");
		fillContent(gallery, contentNodes);

		@SuppressWarnings("unchecked")
		List<Node> images = galleryElement.selectNodes("image");
		for (Node imageNode : images) {
			Element imageElement = (Element) imageNode;
			Image image = new Image();
			image.setTitle(imageElement.selectSingleNode("title").getText());
			image.setDescription(imageElement.selectSingleNode("description").getText());
			image.setFileName(imageElement.attributeValue("fileName"));
			image.setParent(gallery);
			gallery.add(image);
			incProgressValue();
		}
	}

	private void importPage(Level parent, Node pageNode) {
		Element pageElement = (Element) pageNode;

		Page page = new Page();
		page.setParent(parent);
		parent.add(page);
		page.setName(pageElement.attributeValue("name"));
		page.setTemplate(templateCache.get(pageElement.attributeValue("templateFile")));
		if (pageElement.selectSingleNode("title") != null)
			page.setTitle(pageElement.selectSingleNode("title").getText());

		incProgressValue();

		@SuppressWarnings("unchecked")
		List<Node> contentNodes = pageNode.selectNodes("content");
		fillContent(page, contentNodes);
	}

	private void fillContent(Page page, List<Node> contentNodes) {
		for (Node contentNode : contentNodes) {
			Element contentElement = (Element) contentNode;
			Content content = new Content();
			content.setName(contentElement.attributeValue("name"));
			if (contentElement.selectSingleNode("value") != null)
				content.setValue(contentElement.selectSingleNode("value").getText());
			content.setParent(page);
			page.add(content);
		}
	}

	@Override
	public Site getSite() {
		return site;
	}
	
	private void incProgressValue() {
		if (monitor != null)
			monitor.worked(1);
	}
}
