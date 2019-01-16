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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.conf.InitializationManager;
import de.thischwa.pmcms.exception.FatalException;
import de.thischwa.pmcms.model.domain.PoPathInfo;
import de.thischwa.pmcms.model.domain.pojo.Image;
import de.thischwa.pmcms.model.domain.pojo.Level;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.tool.Utils;
import de.thischwa.pmcms.tool.file.FileTool;
import de.thischwa.pmcms.tool.image.Dimension;

public class VirtualImage extends VirtualFile implements IVirtualImage {
	private static final Pattern cachedPattern = Pattern.compile("(.*)[_]([\\d]+)[x]([\\d]+)[.](.*)");
	private Dimension imageDimension;
	private boolean forGallery;
	
	/**
	 * @param site
	 * @param forLayout if true 'forGallery' will be ignored.
	 * @param forGallery
	 */
	public VirtualImage(final Site site, boolean forLayout, boolean forGallery) {
		super(site, forLayout);
		this.forGallery = forGallery;
		resourceFolder = (forLayout ? pm.getSiteProperty("pmcms.site.dir.resources.layout") 
				: (forGallery ? pm.getSiteProperty("pmcms.site.dir.resources.gallery") : pm.getSiteProperty("pmcms.site.dir.resources.other"))).concat("/");	
	}

	@Override
	public void analyse(File imgFile) {
		if (!InitializationManager.isImageExtention(FilenameUtils.getExtension(imgFile.getName())))
			throw new IllegalArgumentException("Image TYPE isn't supported! [" + imgFile.getPath() + "]");
		
		File cacheDir = PoPathInfo.getSiteImageCacheDirectory(site);
		if(FileTool.isInside(cacheDir, imgFile)) {
			String cachedPath = imgFile.getAbsolutePath().substring(cacheDir.getAbsolutePath().length());

			Matcher matcher = cachedPattern.matcher(cachedPath);
			if(matcher.matches()) {
				imageDimension = new Dimension(Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)));
				File resourceDirectory = new File(PoPathInfo.getSiteDirectory(site), resourceFolder);
				baseFile = new File(resourceDirectory, Utils.join(matcher.group(1), ".", matcher.group(4)));
			} else
				throw new FatalException("Can't analyse the cache file name!");
		} else {
			baseFile = imgFile;
		}
	}

	@Override
	public void consructFromTagFromView(String src) throws IllegalArgumentException {
		if (!InitializationManager.isImageExtention(FilenameUtils.getExtension(src)))
			throw new IllegalArgumentException("Image TYPE isn't supported! [" + src + "]");

		String cachePath = String.format("/%s/%s", Constants.LINK_IDENTICATOR_SITE_RESOURCE, pm.getProperty("pmcms.dir.site.imagecache"));
		if(src.startsWith(cachePath)) {
			String path = src.substring(cachePath.length()+1);
			// adapt the path
			if(forLayout) {
				String layoutFolder = pm.getSiteProperty("pmcms.site.dir.resources.layout");
				if(!path.startsWith(layoutFolder))
					throw new IllegalArgumentException(String.format("[%s] isn't well-formed", path));
				path = path.substring(layoutFolder.length()+1);
			} else {
				String folder = forGallery ? pm.getSiteProperty("pmcms.site.dir.resources.gallery") : pm.getSiteProperty("pmcms.site.dir.resources.other");
				if(!path.startsWith(folder))
					throw new IllegalArgumentException(String.format("[%s] isn't well-formed", path));
				path = path.substring(folder.length()+1);
			}
			
			
			File cacheDir = PoPathInfo.getSiteImageCacheDirectory(site);
			File cacheFile = new File(cacheDir, path);
			analyse(cacheFile);
		} else {
			super.consructFromTagFromView(src);
		}
	}
	
	@Override
	public void constructFromImage(Image image) {
		String link = String.format("/%s/%s/%s/%s", Constants.LINK_IDENTICATOR_SITE_RESOURCE, 
				pm.getSiteProperty("pmcms.site.dir.resources.gallery"), image.getParent().getName(), image.getFileName());
		consructFromTagFromView(link);
	}
	
	@Override
	public File getExportFile() {
		if (forLayout) {
			// if the image is for the layout, we expand the file with the dimension
			File resourceDirectory = new File(PoPathInfo.getSiteDirectory(site), resourceFolder);
			String filePath = baseFile.getAbsolutePath().substring(resourceDirectory.getAbsolutePath().length()+1).replace(File.separatorChar, '/');
			filePath = Dimension.expandPath(filePath, imageDimension);
			File exportFile = new File(new File(PoPathInfo.getSiteExportDirectory(site), resourceFolder), filePath);
			return exportFile;
		}
		
		String cacheFileName = getCacheFilename().substring(pm.getProperty("pmcms.dir.site.imagecache").length() + 1);
		File exportFile = new File(PoPathInfo.getSiteExportDirectory(site), cacheFileName);
		return exportFile;
	}

	/**
	 * @return The cache file name relative to the data dir.
	 */
	private String getCacheFilename() {
		if (imageDimension == null)
			throw new IllegalArgumentException("Dimension isn't set, can't create cache file name!");

		File resourceDirectory = PoPathInfo.getSiteDirectory(site);
		String filePath = baseFile.getAbsolutePath().substring(resourceDirectory.getAbsolutePath().length()+1).replace(File.separatorChar, '/');
		filePath = Dimension.expandPath(filePath, imageDimension);
		String cacheFolder = pm.getProperty("pmcms.dir.site.imagecache");
		filePath = Utils.join(cacheFolder, "/", filePath);
		return filePath;
	}
	
	@Override
	public String getTagSrcForExport(Level level) {
		String src = super.getTagSrcForExport(level);
		src = Dimension.expandPath(src, imageDimension);
		return src;
	}
	
	@Override
	public File getCacheFile() {
		String cacheFileName = getCacheFilename();
		File cacheFile = new File(PoPathInfo.getSiteDirectory(site), cacheFileName);
		return cacheFile;
	}
	
	/**
	 * Wrapper to {@link #setDimension(Dimension)}.
	 */
	public void setDimension(int width, int height) {
		setDimension(new Dimension(width, height));
	}

	/**
	 * Set the dimension of the 'new' image file name. The usage of this setter is only necessary, if you want to get the cached file or the
	 * src attribute for the img-tag of the exported version of the file.
	 */
	@Override
	public void setDimension(final Dimension dimension) {
		imageDimension = dimension;
	}

	@Override
	public Dimension getDimension() {
		return imageDimension;
	}
}
