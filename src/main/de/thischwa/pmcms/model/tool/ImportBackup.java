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
package de.thischwa.pmcms.model.tool;

import java.io.File;
import java.io.IOException;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.core.runtime.IProgressMonitor;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.configuration.resource.LabelHolder;
import de.thischwa.pmcms.exception.FatalException;
import de.thischwa.pmcms.gui.IProgressViewer;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.tool.Utils;
import de.thischwa.pmcms.tool.compression.Zip;
import de.thischwa.pmcms.tool.compression.ZipInfo;

/**
 * Class to import a backup of a {@link Site}.
 * 
 * @version $Id:Import.java 461 2006-08-28 16:08:30Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class ImportBackup implements IProgressViewer {
	private static Logger logger = Logger.getLogger(ImportBackup.class);
	private IProgressMonitor monitor = null;
	private File zipFile = null;
	private Site site = null;

	public ImportBackup(final File zipFile) {
		if (zipFile == null || !zipFile.exists())
			throw new IllegalArgumentException("File is null or doesn't exist!");
		this.zipFile = zipFile;
	}

	@Override
	public void run() {
		logger.info("Try to import site from file: " + zipFile.getName());
		ZipInfo zipInfo;
		try {
			zipInfo = Zip.getEntryInfo(zipFile);
		} catch (IOException e1) {
			throw new FatalException("While getting infos for zip entries: " + e1.getMessage(), e1);
		}
		SAXReader reader = new SAXReader();
		reader.setEncoding(Constants.STANDARD_ENCODING);
		try {
			String xml = IOUtils.toString(zipInfo.getInputStream("db.xml"));
			// clean up invalid xml chars
			xml = Utils.stripNonValidXMLCharacters(xml);

			Document document = reader.read(IOUtils.toInputStream(xml));
			Element root = document.getRootElement();
			IBackupParser backupParser;
			String version = root.attributeValue("version");
			if(StringUtils.isBlank(version)) {
				backupParser = new BackupParser_old(root);
			} else if(version.equals(IBackupParser.DBXML_1)) {
				backupParser = new BackupParser_1(root);
			} else {
				throw new RuntimeException(String.format("No backup parser found for version %s.", version));
			}
			backupParser.setMonitor(monitor);
			backupParser.run();
			site = backupParser.getSite();
		} catch (Exception e) {
			e.printStackTrace();
			throw new FatalException("While reading db.xml: " + e.getMessage(), e);
		}

		if (this.monitor != null)
			this.monitor.done();

		// unzip
		try {
			zipInfo.removeEntry("db.xml");
			if (monitor != null)
				monitor.beginTask(
						LabelHolder.get("zip.extract").concat(String.valueOf(zipInfo.getEntryKeys().size())), zipInfo.getEntryKeys().size()); //$NON-NLS-1$
			Zip.extract(InitializationManager.getSitesDir(), zipInfo, monitor);
		} catch (IOException e) {
			throw new FatalException("While unzipping files: " + e.getMessage(), e);
		}

		logger.debug("[" + site.getUrl() + "] successfull imported.");
	}

	@Override
	public void setMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	public Site getSite() {
		return site;
	}
}
