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
package de.thischwa.pmcms.view.renderer.resource;

import java.io.File;

import de.thischwa.pmcms.model.domain.pojo.Level;

/**
 * Interface for all file-based resources.
 */
public interface IVirtualFile {

	/**
	 * Constructs the {@link File} for export depending on the {@link CKResourceFileType} (detected by the implemented class).
	 * 
	 * @return File for export.
	 */
	public File getExportFile();

	/**
	 * Constructs the src-tag for export. 
	 * 
	 * @param level {@link Level} is needed to construct relative links.
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
	public File getBaseFile();

	/**
	 * Constructs the main file from the src-attribute of an a-tag. Examples:
	 * <ul>
	 * <li>/site/[pmcms.site.dir.resources]/test.zip</li>
	 * <li>/site/[pmcms.site.dir.resources.layout]/test.zip</li>
	 * </ul>
	 * @throws IllegalArgumentException TODO
	 */
	public void consructFromTagFromView(final String src) throws IllegalArgumentException;
	
	boolean isForLayout();
}
