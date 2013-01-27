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
package de.thischwa.pmcms.model.domain;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.log4j.Logger;

import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.exception.FatalException;
import de.thischwa.pmcms.model.IOrderable;
import de.thischwa.pmcms.model.InstanceUtil;
import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.domain.pojo.Gallery;
import de.thischwa.pmcms.model.domain.pojo.Image;
import de.thischwa.pmcms.model.domain.pojo.Level;
import de.thischwa.pmcms.model.domain.pojo.Page;
import de.thischwa.pmcms.tool.file.FileTool;
import de.thischwa.pmcms.wysisygeditor.CKImageResource;

/**
 * Structural helper methods for {@link APoormansObject}s.
 *
 * @version $Id: PoStructurTools.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class PoStructurTools {
	private static Logger logger = Logger.getLogger(PoStructurTools.class);
	private static final Pattern BACKUP_FILENAME_PATTERN = Pattern.compile("(.*)_\\d{8}-\\d{6}\\..*");

	public static void changeParent(APoormansObject<?> po, APoormansObject<?> newParent) {
		logger.debug("Entered changeParent");
		if (newParent == null || po == null) {
			logger.warn("NullPointerException!");
			return;
		} else if (!PoInfo.checkParentChildRelationship(newParent, po)) {
			logger.warn("Wrong combination of parent and child! Parent: " + newParent + " - child: " + po);
			return;
		}

		APoormansObject<?> oldParent = (APoormansObject<?>) po.getParent();

		// mode 1
		if ((InstanceUtil.isLevel(newParent)) && InstanceUtil.isJustLevel(po)) {
			Level level = (Level) po;
			Level newParentLevel = (Level) newParent;
			Level oldParentLevel = (Level) oldParent;
			oldParentLevel.remove(level);
			newParentLevel.add(level);
			level.setParent(newParentLevel);
			
			// mode 2
		} else if (InstanceUtil.isLevel(newParent) && InstanceUtil.isPage(po)) {
			Page page = (Page) po;
			Level newParentLevel = (Level) newParent;
			Level oldParentLevel = (Level) oldParent;
			oldParentLevel.remove(page);
			newParentLevel.add(page);
			page.setParent(newParentLevel);

			// mode 3
		} else if (InstanceUtil.isGallery(newParent) && InstanceUtil.isImage(po)) {
			Gallery newParentGallery = (Gallery) newParent;
			Image image = (Image) po;
			CKImageResource tempImageResource = new CKImageResource(PoInfo.getSite(image));
			tempImageResource.constructFromImage(image);
			File srcFile = tempImageResource.getFile();
			Gallery oldParentGallery = image.getParent();
			oldParentGallery.remove(image);
			newParentGallery.add(image);
			image.setParent(newParentGallery);
			// copy files
			tempImageResource = new CKImageResource(PoInfo.getSite(image));
			tempImageResource.constructFromImage(image);
			File destFile = tempImageResource.getFile();
			File destDir = destFile.getParentFile();
			try {
				if (!destDir.exists())
					destDir.mkdirs();
				File reallyDestFile = FileTool.copyToDirectoryUnique(srcFile, destDir);
				if (!reallyDestFile.equals(destFile)) { // set new file name
					tempImageResource.analyse(reallyDestFile);
					image.setFileName(tempImageResource.getFile().getName());
				}
				srcFile.delete();
				logger.debug("Copied image file [" + srcFile.getPath() + "] to [" + destFile.getPath() + "]!");
			} catch (Exception e) {
				throw new FatalException(e);
			}
		}
	}
	
	public static void moveOrderableTo(IOrderable<?> orderable, int newIndex) {
		logger.debug(String.format("Try to move [%s] to pos %d", orderable, newIndex));
		@SuppressWarnings("unchecked")
		List<IOrderable<?>> family = (List<IOrderable<?>>) orderable.getFamily();
		if(!family.remove(orderable))
			throw new IllegalArgumentException("couldn't remove IOrderable from its family");
		family.add(newIndex, orderable);
	}
	
	public static String[] getAllSites() {
		List<String> sites = new ArrayList<String>();
		File[] siteDirs = InitializationManager.getSitesDir().listFiles(new FileFilter() {			
			@Override
			public boolean accept(File file) {
				return !file.isHidden() && file.isFile() && file.getName().endsWith(".xml");
			}
		});
		for (File dir : siteDirs) 
			sites.add(dir.getName().substring(0, dir.getName().length()-4));
		return sites.toArray(new String[]{});
	}

	public static boolean siteExists(final File backup) {
		String name = backup.getName();
		Matcher matcher = BACKUP_FILENAME_PATTERN.matcher(name);
		if(!matcher.matches())
			return false;
		name = matcher.group(1);
		return Arrays.asList(getAllSites()).contains(name);
	}
}
