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
package de.thischwa.pmcms.view.context.object;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.configuration.PropertiesManager;
import de.thischwa.pmcms.exception.RenderingException;
import de.thischwa.pmcms.livecycle.PojoHelper;
import de.thischwa.pmcms.model.domain.OrderableInfo;
import de.thischwa.pmcms.model.domain.PoInfo;
import de.thischwa.pmcms.model.domain.PoPathInfo;
import de.thischwa.pmcms.model.domain.pojo.Gallery;
import de.thischwa.pmcms.model.domain.pojo.Image;
import de.thischwa.pmcms.model.domain.pojo.Level;
import de.thischwa.pmcms.tool.PathTool;
import de.thischwa.pmcms.tool.compression.Zip;
import de.thischwa.pmcms.view.ViewMode;
import de.thischwa.pmcms.view.context.IContextObjectGallery;
import de.thischwa.pmcms.view.context.IContextObjectNeedPojoHelper;
import de.thischwa.pmcms.view.context.IContextObjectNeedViewMode;
import de.thischwa.pmcms.view.renderer.resource.VirtualImage;

/**
 * Context object to build a link to a desired {@link Image} and to build a
 * zip file with all images of a gallery and it's link. It is only for {@link Gallery galleries} and {@link Image images}.
 */
@Component("gallerylinktool")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class GalleryLinkTool implements IContextObjectGallery, IContextObjectNeedPojoHelper, IContextObjectNeedViewMode {
	private String linkToString = null;
	private boolean isExportView;
	private PojoHelper pojoHelper;
	
	@Autowired private PropertiesManager pm;

	public void setExportView(boolean isExportView) {
		this.isExportView = isExportView;
	}

	@Override
	public void setPojoHelper(final PojoHelper pojoHelper) {
		this.pojoHelper = pojoHelper;
	}

	
	@Override
	public void setViewMode(final ViewMode viewMode) {
		isExportView = viewMode.equals(ViewMode.EXPORT);
	}

	/**
	 * Getting the link tool to the {@link Gallery} of the desired {@link Image}.
	 *  
	 * @param image Desired {@link Image} to get the link tool for.
	 * @return Link tool for the desired {@link Image}. If the {@link Image} is null, the link contains 'NO_IMAGE'.
	 */
	public GalleryLinkTool getGallery(final Image image) {
		if (image == null)
			setLink("NO_IMAGE");
		else {
			Gallery gallery = image.getParent();
			if (isExportView) {
				if (OrderableInfo.isFirst(gallery)) {
					setLink(pm.getSiteProperty("pmcms.site.export.file.welcome"));
				} else {
					setLink(gallery.getName().concat(".")
					        .concat(pm.getSiteProperty("pmcms.site.export.extension")));
				}
			} else
				setGalleryForPreview(gallery);
		}
		return this;
	}

	/**
	 * Getting the link tool to the zip file containing the {@link Image}s of the desired gallery. The zip file will
	 * be built.
	 * 
	 * @param gallery
	 * @param siteRelativePath
	 * @return Link tool to the zip file containing the {@link Image}s of the desired gallery. 
	 * @throws RenderingException
	 */
	public GalleryLinkTool getLinkToZip(final Gallery gallery, final String siteRelativePath) throws RenderingException {
		if (isExportView && CollectionUtils.isNotEmpty(gallery.getImages())) {
			String urlPathToZip = PathTool.getURLRelativePathToRoot(gallery.getParent()).concat(siteRelativePath)
			        .concat("/").concat(gallery.getName()).concat(".zip");
			setLink(urlPathToZip);
			Map<File, String> zipEntries = new HashMap<File, String>();
			for (Image image : gallery.getImages()) { // TODO check it: order the images, if not, the hash of the zip is always different
				VirtualImage imageFile = new VirtualImage(PoInfo.getSite(gallery), false, true);
				imageFile.constructFromImage(image);
				zipEntries.put(imageFile.getBaseFile(), "/".concat(FilenameUtils.getName(imageFile.getBaseFile().getAbsolutePath())));
			}
			try {
				String zipName = siteRelativePath.concat(File.separator).concat(gallery.getName()).concat(".zip");
				File zipFile = new File(PoPathInfo.getSiteExportDirectory(PoInfo.getSite(gallery)), zipName);
				zipFile.getParentFile().mkdirs();
				Zip.compressFiles(zipFile.getAbsoluteFile(), zipEntries);
			} catch (IOException e) {
				throw new RenderingException("While creating image zip file: ".concat(e.getMessage()), e);
			}
		} else {
			setLink("javascript:alert('Zip will be constructed not until export!');");
		}
		return this;
	}

	/**
	 * Set link to the desired {@link Image}.
	 */
	public GalleryLinkTool getImage(final Image imageToLink) {
		if (imageToLink == null)
			setLink("NO_IMAGE_FOUND");
		else if (isExportView) {
			Level currentLevel = pojoHelper.getLevel();
			Level levelLinkTo = imageToLink.getParent().getParent();
			String path = PathTool.getURLRelativePathToLevel(currentLevel, levelLinkTo)
					.concat(StringUtils.defaultIfEmpty(PathTool.getExportBaseFilename(imageToLink, pm.getSiteProperty("pmcms.site.export.file.extension")), "IMAGE_NOT_EXISTS"));
			path = PathTool.encodePath(path);
			setLink(path);
		} else
			setImageForPreview(imageToLink);
		return this;
	}

	/**
	 * Set link to the previous of the desired {@link Image}.
	 */
	public GalleryLinkTool getPrevious(final Image imageToLink) {
		if (imageToLink == null)
			setLink("NO_IMAGE_FOUND");
		else if (OrderableInfo.hasPrevious(imageToLink)) 
			return getImage((Image) OrderableInfo.getPrevious(imageToLink));
		else
			setLink("NO_IMAGE_FOUND");
		return this;
	}

	/**
	 * Set link to the next of the desired {@link Image}.
	 */
	public GalleryLinkTool getNext(final Image imageToLink) {
		if (imageToLink == null)
			setLink("NO_IMAGE_FOUND");
		else if (OrderableInfo.hasNext(imageToLink)) 
			return getImage((Image) OrderableInfo.getNext(imageToLink));
		else
			setLink("NO_IMAGE_FOUND");
		return this;
	}
	
	@Override
	public String toString() {
		return linkToString;
	}

	private void setLink(String link) {
		linkToString = link;
	}
	private void setGalleryForPreview(Gallery gallery) {
		StringBuilder link = new StringBuilder();
		link.append("/").append(Constants.LINK_IDENTICATOR_PREVIEW).append("?id=").append(gallery.getId()).append("&amp;");
		link.append(Constants.LINK_TYPE_DESCRIPTOR).append("=").append(Constants.LINK_TYPE_GALLERY).append("&amp;");
		setLink(link.toString());
	}
	
	private void setImageForPreview(final Image image) {
		StringBuilder link = new StringBuilder();
		link.append("/").append(Constants.LINK_IDENTICATOR_PREVIEW).append("?id=").append(image.getId()).append("&amp;");
		link.append(Constants.LINK_TYPE_DESCRIPTOR).append("=").append(Constants.LINK_TYPE_IMAGE).append("&amp;");
		setLink(link.toString());
	}
}
