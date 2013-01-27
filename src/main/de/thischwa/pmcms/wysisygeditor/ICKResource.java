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
package de.thischwa.pmcms.wysisygeditor;

import java.io.File;

import de.thischwa.pmcms.model.domain.pojo.Level;


/**
 * Interface for all resources of the FCKeditor.
 *
 * @version $Id: ICKResource.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public interface ICKResource {

	/**
	 * Constructs the {@link File} for export depending on the {@link CKResourceFileType} (detected by the implemented class).
	 * 
	 * @return File for export.
	 */
	public File getExportFile();

	/**
	 * Constructs the src-tag for export. {@link Level} is needed to construct relative links.
	 * 
	 * @param level
	 * @return Src-tag for export.
	 */
	public String getTagSrcForExport(final Level level);	
	
	/**
	 * Constructs the src-tag for the preview.
	 * 
	 * @return Source attribute for the preview.
	 */
	public String getTagSrcForPreview();

	/**
	 * Getter for the base file.
	 */
	public File getFile();

	/**
	 * Constructs the main file from the src-attribute of an img-tag. Examples:
	 * <ul>
	 * <li>file: /site/file/test.zip</li>
	 * </ul>
	 */
	public void consructFromTagFromView(String srcString);
}
