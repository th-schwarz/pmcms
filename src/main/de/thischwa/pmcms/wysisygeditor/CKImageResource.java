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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.log4j.Logger;

import de.thischwa.c5c.resource.Extension;
import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.exception.FatalException;
import de.thischwa.pmcms.model.domain.PoPathInfo;
import de.thischwa.pmcms.model.domain.pojo.Image;
import de.thischwa.pmcms.model.domain.pojo.Level;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.tool.PathTool;
import de.thischwa.pmcms.tool.Utils;
import de.thischwa.pmcms.tool.file.FileTool;
import de.thischwa.pmcms.tool.image.Dimension;

/**
 * Encapsulates the image path generation for different usage like export, caching ...<br>
 * The internal representation of the image file is {@link File}. There are different setters. So, e.g. it's possible to build the complete
 * image file for exporting from the src-attribute of an img-tag.
 */
public class CKImageResource extends ACKEditorResource implements ICKResource {
	private static Logger logger = Logger.getLogger(CKImageResource.class);
	private static final Pattern cachedPattern = Pattern.compile("(.*)[_]([\\d]+)[x]([\\d]+)[.](.*)");
	private String cacheFolder;
	private Dimension imageDimension;
	private boolean fromGallery = false;

	public CKImageResource(final Site site) {
		super(site, Extension.IMAGE);
		cacheFolder = PoPathInfo.getSiteImageCacheDirectory(site).getAbsolutePath().substring(InitializationManager.getDataDir().getAbsolutePath().length()+1) + File.separator;
	}

	public CKImageResource(final Site site, boolean fromGallery) {
		this(site);
		this.fromGallery = fromGallery;
	}
	
	/**
	 * Cached versions are respected!
	 * E.g. [datadir]/sites/test.org/cache/galleryname/floating-leaves_320x240.jpg
	 */
	public void analyse(final File imgFile) {
		if (imgFile == null) 
			throw new IllegalArgumentException("File shouldn't be null!");
		
		logger.debug("Entered constructFromFilesystem: " + imgFile.getAbsolutePath());

		if (!InitializationManager.isImageExtention(FilenameUtils.getExtension(imgFile.getName())))
			throw new FatalException("Image TYPE isn't supported! [" + imgFile + "]");

		// analyze the file name
		File cacheDir = PoPathInfo.getSiteImageCacheDirectory(site);
		if(FileTool.isInside(cacheDir, imgFile)) {
			logger.debug("cashed image found");
			
			// Analyze dimension
			String cleanedPath = imgFile.getAbsolutePath().substring(cacheDir.getAbsolutePath().length()+1);
			analyseCashedPath(cleanedPath);
		} else 
			file = imgFile;
		
		if(!file.exists())
			throw new FatalException("File doesn't exists: "+imgFile.getPath());
	}
	
	/**
	 * @param path cashed folder must be cut off.
	 */
	private void analyseCashedPath(String path) {
		Matcher matcher = cachedPattern.matcher(path);
		if(matcher.matches()) {
			imageDimension = new Dimension(Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)));
			file = new File(resourceDirectory, Utils.join(matcher.group(1), ".", matcher.group(4)));
		} else
			throw new FatalException("Can't analyse the cache file name!");
	}

	/**
	 * Additional function : set resource dir: E.g. gallery/floating-leaves.jpg -> image/gallery/floating-leaves.jpg
	 * E.g. site/cache/sflogo_88x31.png
	 * TODO check this, set file
	 */
	@Override
	public void consructFromTagFromView(final String srcString) {
		String path = UrlFolderTool.stripUrlSiteFolder(srcString);
		if(path.startsWith(UrlFolderTool.getImageCasheFolder())) {
			path = path.substring(UrlFolderTool.getImageCasheFolder().length());
			analyseCashedPath(path);
		} else {
			if(path.startsWith(UrlFolderTool.getImageFolder()))
				path = path.substring(UrlFolderTool.getImageFolder().length());
			path = path.replace("/", File.separator);
			File parent = (fromGallery) ? PoPathInfo.getSiteGalleryDirectory(site) : PoPathInfo.getSiteResourceDirectory(site, ext);
			file = new File(parent, path);
		}
	}

	/**
	 * Setter for {@link Image} object.
	 */
	public void constructFromImage(final Image image) {
		String link = String.format("/%s/%s/%s/%s", Constants.LINK_IDENTICATOR_SITE_RESOURCE, 
				CKResourceTool.getDir(ext), image.getParent().getName(), image.getFileName());
		consructFromTagFromView(link);
	}

	/**
	 * @return Src-tag needed for the exported site, relative path depending on the level's hierarchy is respected, e.g.
	 *         ../images/gallery/floating-leaves_320x240.jpg
	 */
	@Override
	public String getTagSrcForExport(final Level level) {
		String relativLevelPath = PathTool.getURLRelativePathToRoot(level);
		String imageFolder = UrlFolderTool.getResourceFolderForExport(ext);
		String cachePath = getCacheFilename().substring(cacheFolder.length()).replace(File.separatorChar, Constants.SEPARATOR_CHAR);
		int maxFilenameLength = relativLevelPath.length() + imageFolder.length() + cachePath.length() + 1;
		StringBuilder filename = new StringBuilder(maxFilenameLength);
		filename.append(relativLevelPath);
		if (StringUtils.isNotBlank(relativLevelPath) && !relativLevelPath.endsWith("/"))
			filename.append('/');
		filename.append(imageFolder);
		filename.append(cachePath);
		return filename.toString();
	}

	@Override
	public File getExportFile() {
		String filename = getCacheFilename().substring(cacheFolder.length());
		File exportFile = new File(PoPathInfo.getSiteExportResourceDirectory(site, ext), filename);
		return exportFile;
	}

	/**
	 * @return The cache file name relative to the data dir.
	 */
	public String getCacheFilename() {
		if (imageDimension == null)
			throw new IllegalArgumentException("Dimension isn't set, can't create cache file name!");

		String filename = file.getAbsolutePath().substring(resourceDirectory.getAbsolutePath().length()+1);
		String ext = FileTool.getExtension(filename);
		String filenameWithoutExt = filename.substring(0, filename.length() - ext.length() - 1);
		filename = Utils.join(cacheFolder, filenameWithoutExt, "_", imageDimension.toString(), ".", ext);

		return filename;
	}

	/**
	 * Wrapper to {@link #getCacheFilename()}.
	 * 
	 * @return The {@link File} of the cached image.
	 */
	public File getCacheFile() {
		String cacheFileName = getCacheFilename();
		File cacheFile = new File(InitializationManager.getDataDir(), cacheFileName);
		return cacheFile.getAbsoluteFile();
	}

	/**
	 * @return The basic file name with relative path to the image directory of a site. E.g. gallery/floating-leaves.jpg
	 */
	public String getBasicFilenameStrippedToSiteImageDirectory() {
		String basic = file.getAbsolutePath().substring(resourceDirectory.getAbsolutePath().length()+1);
		return basic;
	}

	public String getBasicFilename() {
		return file.getAbsolutePath();
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
	public void setDimension(final Dimension dimension) {
		imageDimension = dimension;
	}

	public Dimension getDimension() {
		return imageDimension;
	}

	public int getWidth() {
		if (imageDimension == null)
			return -1;
		return imageDimension.x;
	}

	public int getHeight() {
		if (imageDimension == null)
			return -1;
		return imageDimension.y;
	}

	/**
	 * @return Src-tag to the cashed image, absolute to the application directory, e.g.
	 *         /site/cache/gallery/floating-leaves_320x240.jpg
	 */
	@Override
	public String getTagSrcForPreview() {
		String cashedFilename = getCacheFilename().replace(File.separator, Constants.SEPARATOR);
		String siteFolder = UrlFolderTool.getSiteFolder(site);
		if(cashedFilename.startsWith(siteFolder))
			cashedFilename = cashedFilename.substring(siteFolder.length());
		cashedFilename = Utils.join(Constants.SEPARATOR, Constants.LINK_IDENTICATOR_SITE_RESOURCE, Constants.SEPARATOR, cashedFilename);
		return cashedFilename;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CKImageResource))
			return false;
		CKImageResource objImageResource = (CKImageResource) obj;
		return new EqualsBuilder().append(file, objImageResource.getFile()).append(imageDimension, objImageResource.getDimension())
				.isEquals();
	}
}
