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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.configuration.PropertiesManager;
import de.thischwa.pmcms.exception.RenderingException;
import de.thischwa.pmcms.model.domain.OrderableInfo;
import de.thischwa.pmcms.model.domain.PoInfo;
import de.thischwa.pmcms.model.domain.PoPathInfo;
import de.thischwa.pmcms.model.domain.pojo.Gallery;
import de.thischwa.pmcms.model.domain.pojo.Image;
import de.thischwa.pmcms.tool.PathTool;
import de.thischwa.pmcms.tool.compression.Zip;
import de.thischwa.pmcms.view.ViewMode;
import de.thischwa.pmcms.view.context.IContextObjectGallery;
import de.thischwa.pmcms.view.context.IContextObjectNeedViewMode;
import de.thischwa.pmcms.wysisygeditor.CKImageResource;

/**
 * Context object to build a link to a desired {@link Image} and to build a
 * zip file with all images of a gallery and it's link. It is only for {@link Gallery galleries} and {@link Image images}.
 */
@Component("gallerylinktool")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class GalleryLinkTool implements IContextObjectGallery, IContextObjectNeedViewMode {
	private String linkToString = null;
	private boolean isExportView;
	
	@Autowired private PropertiesManager pm;

	public void setExportView(boolean isExportView) {
		this.isExportView = isExportView;
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
	public GalleryLinkTool get(final Image image) {
		if (image == null)
			setGallery("NO_IMAGE");
		else {
			Gallery gallery = image.getParent();
			if (isExportView) {
				if (OrderableInfo.isFirst(gallery)) {
					setGallery(pm.getSiteProperty("pmcms.site.export.file.welcome"));
				} else {
					setGallery(gallery.getName().concat(".")
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
			setGallery(urlPathToZip);
			Map<File, String> zipEntries = new HashMap<File, String>();
			for (Image image : gallery.getImages()) { // TODO check it: order the images, if not, the hash of the zip is always different
				CKImageResource imageFile = new CKImageResource(PoInfo.getSite(gallery), true);
				imageFile.constructFromImage(image);
				zipEntries.put(imageFile.getFile(), "/".concat(FilenameUtils.getName(imageFile.getFile().getAbsolutePath())));
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
			setGallery("javascript:alert('Zip will be constructed not until export!');");
		}
		return this;
	}

	@Override
	public String toString() {
		return linkToString;
	}

	private void setGallery(String gallery) {
		linkToString = gallery;
	}

	private void setGalleryForPreview(Gallery gallery) {
		StringBuilder link = new StringBuilder();
		link.append("/").append(Constants.LINK_IDENTICATOR_PREVIEW).append("?id=").append(gallery.getId()).append("&amp;");
		link.append(Constants.LINK_TYPE_DESCRIPTOR).append("=").append(Constants.LINK_TYPE_GALLERY).append("&amp;");
		setGallery(link.toString());
	}
}
