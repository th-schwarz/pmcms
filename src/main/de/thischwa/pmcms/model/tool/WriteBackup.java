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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.eclipse.core.runtime.IProgressMonitor;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.conf.InitializationManager;
import de.thischwa.pmcms.conf.resource.LabelHolder;
import de.thischwa.pmcms.exception.FatalException;
import de.thischwa.pmcms.gui.IProgressViewer;
import de.thischwa.pmcms.model.InstanceUtil;
import de.thischwa.pmcms.model.domain.PoInfo;
import de.thischwa.pmcms.model.domain.PoPathInfo;
import de.thischwa.pmcms.model.domain.pojo.Content;
import de.thischwa.pmcms.model.domain.pojo.Gallery;
import de.thischwa.pmcms.model.domain.pojo.Image;
import de.thischwa.pmcms.model.domain.pojo.Level;
import de.thischwa.pmcms.model.domain.pojo.Macro;
import de.thischwa.pmcms.model.domain.pojo.Page;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.model.domain.pojo.Template;
import de.thischwa.pmcms.tool.compression.Zip;
import de.thischwa.pmcms.tool.file.FileTool;

/**
 * Class to backup a {@link Site}.
 * 
 * @author Thilo Schwarz
 */
public class WriteBackup implements IProgressViewer {
	private static Logger logger = Logger.getLogger(WriteBackup.class);
	private Site site = null;
	private IProgressMonitor monitor = null;
	private int pageCount = 0;

	public WriteBackup(final Site site) {
		if (site == null)
			throw new IllegalArgumentException("Site shouldn't be null!");
		this.site = site;
		pageCount = PoInfo.collectRenderables(site).size();
	}

	@Override
	public void run() {
		logger.debug("Try to backup [" + site.getUrl() + "].");
		if (monitor != null)
			monitor.beginTask(LabelHolder.get("task.backup.monitor").concat(" ").concat(String.valueOf(pageCount)), 
					pageCount * 2 + 1); 
	

		// Create file infrastructure.
		File dataBaseXml = null;
		try {
			dataBaseXml = File.createTempFile("database", ".xml", Constants.TEMP_DIR.getAbsoluteFile());
		} catch (IOException e1) {
			throw new RuntimeException("Can't create temp file for the database xml file because: " + e1.getMessage(), e1);
		}

		Document dom = DocumentHelper.createDocument();
		dom.setXMLEncoding(Constants.STANDARD_ENCODING);

		Element siteEl = dom.addElement("site")
				.addAttribute("version", IBackupParser.DBXML_2)
				.addAttribute("url", site.getUrl());
		siteEl.addElement("title").addCDATA(site.getTitle());
		
		Element elementTransfer = siteEl.addElement("transfer");
		elementTransfer.addAttribute("host", site.getTransferHost())
				.addAttribute("user", site.getTransferLoginUser())
				.addAttribute("password", site.getTransferLoginPassword())
				.addAttribute("startdir", site.getTransferStartDirectory());
		
		for (Macro macro : site.getMacros()) {
			Element marcoEl = siteEl.addElement("macro");
			marcoEl.addElement("name").addCDATA(macro.getName());
			marcoEl.addElement("text").addCDATA(macro.getText());
		}
		
		if(site.getLayoutTemplate() != null) {
			Template template = site.getLayoutTemplate();
			Element templateEl = siteEl.addElement("template");
			init(templateEl, template);
		}
		for (Template template : site.getTemplates()) {
			Element templateEl = siteEl.addElement("template");
			init(templateEl, template);
		}

		if (!CollectionUtils.isEmpty(site.getPages()))
			for (Page page : site.getPages())
				addPageToElement(siteEl, page);
		
		for (Level level : site.getSublevels())
			addLevelToElement(siteEl, level);

		OutputStream out = null;
		try {
			// It's really important to use the XMLWriter instead of the FileWriter because the FileWriter takes the default
			// encoding of the OS. This my cause some trouble with special chars on some OSs!
			out = new BufferedOutputStream(new FileOutputStream(dataBaseXml));
			OutputFormat outformat = OutputFormat.createPrettyPrint();
			outformat.setEncoding(Constants.STANDARD_ENCODING);
			XMLWriter writer = new XMLWriter(out, outformat);
			writer.write(dom);
			writer.flush();
		} catch (IOException e) {
			throw new FatalException("While exporting the database xml: " + e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(out);
		}

		// Generate the zip file, cache and export dir will be ignored.
		File backupZip = new File(InitializationManager.getSitesBackupDir(), site.getUrl().concat("_")
				.concat(new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()).concat(".").concat(Constants.BACKUP_EXTENSION)));
		List<File> filesToIgnore = new ArrayList<File>();
		filesToIgnore.add(PoPathInfo.getSiteImageCacheDirectory(site).getAbsoluteFile());
		filesToIgnore.add(PoPathInfo.getSiteExportDirectory(site).getAbsoluteFile());
		Collection<File> filesToBackup = FileTool.collectFiles(PoPathInfo.getSiteDirectory(site).getAbsoluteFile(), filesToIgnore);
		File sitesDir = InitializationManager.getSitesDir();
		Map<File, String> zipEntries = new HashMap<File, String>();
		for (File file : filesToBackup) {
			String entryName = file.getAbsolutePath().substring(sitesDir.getAbsolutePath().length()+1);
			entryName = StringUtils.replace(entryName, File.separator, "/"); // Slashes are zip conform
			zipEntries.put(file, entryName);
			incProgressValue();
		}
		zipEntries.put(dataBaseXml, "db.xml");
		try {
			if (monitor != null)
				monitor.beginTask(LabelHolder.get("zip.compress").concat(String.valueOf(zipEntries.size())), 
						zipEntries.size()); 
			Zip.compressFiles(backupZip, zipEntries, monitor);
		} catch (IOException e) {
			throw new FatalException("While generating zip: " + e.getMessage(), e);
		}
		dataBaseXml.delete();
		logger.info("Site backuped successfull to [".concat(backupZip.getAbsolutePath()).concat("]!"));
	}
	
	private void init(Element templateEl, Template template) {
		templateEl.addAttribute("id", String.valueOf(template.getId())); // id of a template must be stored because a page needs its reference!
		templateEl.addAttribute("type", (template.getType() != null) ? template.getType().toString().toLowerCase() : null);
		templateEl.addElement("name").addCDATA(template.getName());
		templateEl.addElement("text").addCDATA(template.getText());
	}

	/**
	 * Recursive method to add a {@link Level} to the xml.
	 * 
	 * @param parentElement
	 * @param level
	 */
	private void addLevelToElement(Element parentElement, final Level level) {
		Element containerElement = parentElement.addElement("level").addAttribute("name", level.getName());
		containerElement.addElement("title").addCDATA(level.getTitle());

		for (Page page : level.getPages())
			addPageToElement(containerElement, page);

		if (!CollectionUtils.isEmpty(level.getSublevels()))
			for (Level subContainer : level.getSublevels())
				addLevelToElement(containerElement, subContainer);
	}

	/**
	 * Helper to add a {@link Page} to the xml.
	 * 
	 * @param parentElement
	 * @param page
	 */
	private void addPageToElement(Element parentElement, final Page page) {
		String elementName = (InstanceUtil.isGallery(page)) ? "gallery" : "page";
		Element pageElement = parentElement.addElement(elementName).addAttribute("name", page.getName())
				.addAttribute("templateID", String.valueOf(page.getTemplate().getId()));
		pageElement.addElement("title").addCDATA(page.getTitle());

		for (Content content : page.getContent()) {
			Element contentElement = pageElement.addElement("content").addAttribute("name", content.getName());
			contentElement.addElement("value").addCDATA(content.getValue());
		}

		incProgressValue();
		if (InstanceUtil.isGallery(page)) {
			Gallery gallery = (Gallery) page;
			pageElement.addAttribute("thumbnailMaxWidth", String.valueOf(gallery.getThumbnailMaxWidth()))
				.addAttribute("thumbnailMaxHeight", String.valueOf(gallery.getThumbnailMaxHeight()))
				.addAttribute("imageMaxWidth", String.valueOf(gallery.getImageMaxWidth()))
				.addAttribute("imageMaxHeight",	String.valueOf(gallery.getImageMaxHeight()));

			pageElement.addAttribute("imageTemplateID", String.valueOf(gallery.getImageTemplate().getId()));
			if (gallery.getImages() != null)
				for (Image image : gallery.getImages()) {
					Element elementImage = pageElement.addElement("image").addAttribute("fileName", image.getFileName());
					elementImage.addElement("title").addCDATA(image.getTitle());
					elementImage.addElement("description").addCDATA(image.getDescription());
					incProgressValue();
				}
		}
	}
	
	@Override
	public void setMonitor(final IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	private void incProgressValue() {
		if (monitor != null)
			monitor.worked(1);
	}
}
