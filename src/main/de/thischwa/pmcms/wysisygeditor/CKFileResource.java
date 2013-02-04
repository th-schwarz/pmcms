/*******************************************************************************
 * Poor Man's CMS (pmcms) - A very basic CMS generating static html pages.
 * http://pmcms.sourceforge.net
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

import org.apache.commons.lang.StringUtils;

import de.thischwa.c5c.resource.Extension;
import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.model.domain.PoPathInfo;
import de.thischwa.pmcms.model.domain.pojo.Level;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.tool.PathTool;
import de.thischwa.pmcms.tool.file.FileTool;


/**
 * Implementation of the {@link ACKEditorResource} for the resource TYPE file.
 */
public class CKFileResource extends ACKEditorResource implements ICKResource {

	public CKFileResource(final Site site) {
		super(site);
	}
	
	public CKFileResource(final Site site, final Extension ext) {
		super(site, ext);
	}
		
	@Override
	public void consructFromTagFromView(final String srcString) {
		String temp = LinkFolderTool.stripUrlSiteFolder(srcString);
		super.setResourceFileType(CKResourceTool.getResourceType(FileTool.getExtension(srcString)));
		file = getFSPathOfResource(site, temp);
	}

	@Override
	public String getTagSrcForExport(final Level level) {
		StringBuilder tag = new StringBuilder(PathTool.getURLRelativePathToRoot(level));
		tag.append(LinkFolderTool.getResourceFolderForExport(ext));
		tag.append(file.getName());
		return tag.toString();
	}
	
	@Override
	public File getExportFile() {
		String filename = file.getAbsolutePath().substring(resourceDirectory.getAbsolutePath().length()+1);
		return new File(PoPathInfo.getSiteExportResourceDirectory(site, ext), filename);
	}
	
	@Override
	public String getTagSrcForPreview() {
		String temp = file.getAbsolutePath();
		if (temp.startsWith(resourceDirectory.getAbsolutePath()))
			temp = temp.substring(resourceDirectory.getAbsolutePath().length()+1);
		temp = temp.replace(File.separatorChar, LinkFolderTool.SEPARATOR_CHAR);
		String siteFolder = LinkFolderTool.getSiteFolder(site);
		if(temp.startsWith(siteFolder))
			temp = temp.substring(siteFolder.length());
		temp = String.format("/%s/%s/%s", Constants.LINK_IDENTICATOR_SITE_RESOURCE, CKResourceTool.getDir(ext), temp);
		return temp;
	}

    /**
     * Changes an absolute url path to a absolute file path. 
     * 
     * @param resourcePath
     * @return Return the decoded path of the relative to application's path
     */
    private File getFSPathOfResource(final Site site, final String resourcePath) {
        if (StringUtils.isBlank(resourcePath))
            return null;
        
        // decode and clean up        
        String temp = PathTool.decodePath(resourcePath);
        Extension ext = CKResourceTool.getResourceType(FileTool.getExtension(resourcePath));
        if(ext == Extension.IMAGE)
        	throw new IllegalArgumentException("Methode shouldn't called for image resources");
        if(temp.startsWith("/"))
        	temp = temp.substring(1);
        if(temp.startsWith(Constants.LINK_IDENTICATOR_SITE_RESOURCE))
        	temp = temp.substring(Constants.LINK_IDENTICATOR_SITE_RESOURCE.length()+1);
        String resourceFolder = CKResourceTool.getDir(ext);
        if(temp.startsWith(resourceFolder))
        	temp = temp.substring(resourceFolder.length()+1);
        temp = temp.replace('/', File.separatorChar);
        return new File(PoPathInfo.getSiteResourceDirectory(site, ext), temp);
    }
}
