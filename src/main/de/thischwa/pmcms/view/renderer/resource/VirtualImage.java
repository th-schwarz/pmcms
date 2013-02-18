package de.thischwa.pmcms.view.renderer.resource;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.exception.FatalException;
import de.thischwa.pmcms.model.domain.PoPathInfo;
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
				: (forGallery ? pm.getSiteProperty("pmcms.site.dir.resources.gallery") : pm.getSiteProperty("pmcms.site.dir.resources.image"))).concat("/");	
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
				String folder = forGallery ? pm.getSiteProperty("pmcms.site.dir.resources.gallery") : pm.getSiteProperty("pmcms.site.dir.resources.image");
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
	public File getExportFile() {
		String cacheFileName = getCacheFilename();
		File cacheFile = new File(PoPathInfo.getSiteDirectory(site), cacheFileName);
		return cacheFile;
	}

	/**
	 * @return The cache file name relative to the data dir.
	 */
	private String getCacheFilename() {
		if (imageDimension == null)
			throw new IllegalArgumentException("Dimension isn't set, can't create cache file name!");

		File resourceDirectory = PoPathInfo.getSiteDirectory(site);
		String filePath = baseFile.getAbsolutePath().substring(resourceDirectory.getAbsolutePath().length()+1).replace(File.separatorChar, '/');
		String ext = FileTool.getExtension(filePath);
		String pathWithoutExt = filePath.substring(0, filePath.length() - ext.length() - 1);
		String cacheFolder = pm.getProperty("pmcms.dir.site.imagecache");
		filePath = Utils.join(cacheFolder, "/", pathWithoutExt, "_", imageDimension.toString(), ".", ext);

		return filePath;
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
