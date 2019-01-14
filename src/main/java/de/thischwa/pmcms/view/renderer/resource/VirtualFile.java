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

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.conf.InitializationManager;
import de.thischwa.pmcms.conf.PropertiesManager;
import de.thischwa.pmcms.model.domain.PoPathInfo;
import de.thischwa.pmcms.model.domain.pojo.Level;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.tool.PathTool;

public class VirtualFile implements IVirtualFile {
	protected Site site;
	protected boolean forLayout;
	protected File baseFile;
	protected PropertiesManager pm;
	protected String resourceFolder;

	public VirtualFile(final Site site, boolean forLayout) {
		this.site = site;
		this.forLayout = forLayout;
		pm = InitializationManager.getBean(PropertiesManager.class);
		resourceFolder = (forLayout ? pm.getSiteProperty("pmcms.site.dir.resources.layout") : pm.getSiteProperty("pmcms.site.dir.resources.other")).concat("/");
	}
	
	@Override
	public File getExportFile() {
		File exportDir = PoPathInfo.getSiteExportDirectory(site);
		String exportPath = baseFile.getAbsolutePath().substring(PoPathInfo.getSiteDirectory(site).getAbsolutePath().length());
		File exportFile = new File(exportDir, exportPath);
		return exportFile;
	}

	@Override
	public String getTagSrcForExport(final Level level) {
		String path = PathTool.getURLFromFile(baseFile.getAbsolutePath().substring(PoPathInfo.getSiteDirectory(site).getAbsolutePath().length()));
		StringBuilder tag = new StringBuilder(PathTool.getURLRelativePathToRoot(level));
		tag.append(path.substring(1)); // cut obsolete slash
		return tag.toString();
	}
	
	@Override
	public String getTagSrcForPreview() {
		String path = PathTool.getURLFromFile(baseFile.getAbsolutePath().substring(PoPathInfo.getSiteDirectory(site).getAbsolutePath().length()));
		String link = String.format("/%s%s", Constants.LINK_IDENTICATOR_SITE_RESOURCE, path);
		return link;
	}

	@Override
	public File getBaseFile() {
		return baseFile;
	}

	@Override
	public void consructFromTagFromView(final String src) throws IllegalArgumentException {
		String urlResourcePath = String.format("/%s/%s", Constants.LINK_IDENTICATOR_SITE_RESOURCE, resourceFolder);
		if(!src.startsWith(urlResourcePath))
			throw new IllegalArgumentException("Unknown resource folder!");
		String path = PathTool.decodePath(src.substring(Constants.LINK_IDENTICATOR_SITE_RESOURCE.length()+2, src.length()));
		baseFile = new File(PoPathInfo.getSiteDirectory(site), path);
	}
	
	@Override
	public boolean isForLayout() {
		return forLayout;
	}
}
