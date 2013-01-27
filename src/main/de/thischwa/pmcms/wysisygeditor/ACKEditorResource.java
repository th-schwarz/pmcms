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

import de.thischwa.c5c.resource.Extension;
import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.model.domain.PoPathInfo;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.server.SiteResourceServlet;
import de.thischwa.pmcms.view.context.object.tagtool.CommonXhtmlTagTool;


/**
 * Basic (abstract) class for handling the different file resources of the FCKeditor.
 *
 * @version $Id: ACKEditorResource.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 * 
 * TODO make null-save for use with fckeditor templates. (Img-tags are empty skeletons!!)
 */
abstract class ACKEditorResource {
	protected File file = null;
	protected final Site site;
	protected Extension ext;
	protected File resourceDirectory = null;
	
	protected ACKEditorResource(final Site site) {
		if (site == null)
			throw new IllegalArgumentException("Param 'site' is  null!");
		this.site = site;
	}

	protected ACKEditorResource(final Site site, final Extension ext) {
		this(site);
		setResourceFileType(ext);
	}

	public void setResourceFileType(final Extension ext) {
		this.ext = ext;
		this.resourceDirectory = PoPathInfo.getSiteResourceDirectory(this.site, ext);
	}
	
	/**
	 * @return The tag-src-tag for using in the {@link CommonXhtmlTagTool}s needed for preview. This method is called 
	 * if the editor's content contains links. <br/>
	 * The tag should have the following pattern to trigger the {@link SiteResourceServlet}:
	 * site/file/test.zip or site/image/sflogo.png
	 */
	public String getPreviewTagSrcForTagTool() {
		String path = this.file.getAbsolutePath().replace(File.separator, LinkFolderTool.SEPARATOR);
		String resourcePath = PoPathInfo.getSiteResourceDirectory(this.site, this.ext).getAbsolutePath().replace(File.separator, LinkFolderTool.SEPARATOR);
		if (path.startsWith(resourcePath))
			 path = path.substring(resourcePath.length()+1);
		path = String.format("/%s/%s/%s", Constants.LINK_IDENTICATOR_SITE_RESOURCE, CKResourceTool.getDir(ext), path);
		return path;
	}
	
	public File getFile() {
		return file;
	}
}
