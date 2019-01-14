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

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import de.thischwa.pmcms.conf.InitializationManager;
import de.thischwa.pmcms.exception.FatalException;
import de.thischwa.pmcms.model.domain.pojo.Site;

/**
 * Stores and read the {@link Site} object. Used format: xml.
 * 
 * @author Thilo Schwarz
 */
public class SitePersister {
	private static Logger logger = Logger.getLogger(SitePersister.class);
	
	public static Site read(String siteUrl) {
		File dataFile = getDataFile(siteUrl);
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(dataFile));
			Site site = read(in);
			return site;
		} catch (Exception e) {
			throw new FatalException(e);
		}
	}

	public static Site read(InputStream in) throws IOException {
		XMLDecoder decoder = new XMLDecoder(in, new Site());
		Site site;
		try {
			site = (Site) decoder.readObject();
			decoder.close();
			return site;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
	
	public static void write(Site site) throws IOException {
		File dataFile = getDataFile(site.getUrl());
		try {
			OutputStream out = new BufferedOutputStream(new FileOutputStream(dataFile));
			write(site, out);
		} catch (Exception e) {
			throw new IOException(e);
		}		
	}

	private static void write(Site site, OutputStream out) {
		XMLEncoder encoder = new XMLEncoder(out);
		encoder.writeObject(site);
		encoder.close();
		logger.info(String.format("Site '%s' successful written.", site.getUrl()));
	}
	

	public static File getDataFile(Site site) {
		return getDataFile(site.getUrl());
	}
	
	public static File getDataFile(String siteUrl) {
		return new File(InitializationManager.getSitesDir(), siteUrl + ".xml");
	}
}
