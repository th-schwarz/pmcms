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
package de.thischwa.pmcms.view.context.object;


import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.livecycle.PojoHelper;
import de.thischwa.pmcms.model.domain.OrderableInfo;
import de.thischwa.pmcms.model.domain.pojo.Image;
import de.thischwa.pmcms.model.domain.pojo.Level;
import de.thischwa.pmcms.tool.PathTool;
import de.thischwa.pmcms.view.ViewMode;
import de.thischwa.pmcms.view.context.IContextObjectGallery;
import de.thischwa.pmcms.view.context.IContextObjectNeedPojoHelper;
import de.thischwa.pmcms.view.context.IContextObjectNeedViewMode;

/**
 * Context object for {@link Image images} to provide methods for typical image related links to the previous, 
 * to the next and to the image itself.
 *
 * @version $Id: ImageLinkTool.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
@Component("imagelinktool")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ImageLinkTool implements IContextObjectGallery, IContextObjectNeedPojoHelper, IContextObjectNeedViewMode {
	private String linkToString;
	private boolean isExportView;
	private PojoHelper pojoHelper;
	
	@Override
	public void setPojoHelper(final PojoHelper pojoHelper) {
		this.pojoHelper = pojoHelper;
	}

	@Override
	public void setViewMode(final ViewMode viewMode) {
		isExportView = viewMode.equals(ViewMode.EXPORT);
	}

	
	/**
	 * Set link to the desired {@link Image}.
	 */
	public ImageLinkTool getImage(final Image imageToLink) {
		if (imageToLink == null)
			setImage("NO_IMAGE_FOUND");
		else if (isExportView) {
			Level currentLevel = pojoHelper.getLevel();
			Level levelLinkTo = imageToLink.getParent().getParent();
			String path = PathTool.getURLRelativePathToLevel(currentLevel, levelLinkTo)
					.concat(StringUtils.defaultIfEmpty(PathTool.getExportBaseFilename(imageToLink, Constants.RENDERED_EXT), "IMAGE_NOT_EXISTS"));
			path = PathTool.encodePath(path);
			setImage(path);
		} else
			setImageForPreview(imageToLink);
		return this;
	}

	/**
	 * Set link to the previous of the desired {@link Image}.
	 */
	public ImageLinkTool getPrevious(final Image imageToLink) {
		if (imageToLink == null)
			setImage("NO_IMAGE_FOUND");
		else if (OrderableInfo.hasPrevious(imageToLink)) 
			return getImage((Image) OrderableInfo.getPrevious(imageToLink));
		else
			setImage("NO_IMAGE_FOUND");
		return this;
	}

	/**
	 * Set link to the next of the desired {@link Image}.
	 */
	public ImageLinkTool getNext(final Image imageToLink) {
		if (imageToLink == null)
			setImage("NO_IMAGE_FOUND");
		else if (OrderableInfo.hasNext(imageToLink)) 
			return getImage((Image) OrderableInfo.getNext(imageToLink));
		else
			setImage("NO_IMAGE_FOUND");
		return this;
	}
	
	@Override
	public String toString() {
		return linkToString;
	}
	private void setImage(final String image) {
		linkToString = image;
	}
	private void setImageForPreview(final Image image) {
		StringBuilder link = new StringBuilder();
		link.append("/").append(Constants.LINK_IDENTICATOR_PREVIEW).append("?id=").append(image.getId()).append("&amp;");
		link.append(Constants.LINK_TYPE_DESCRIPTOR).append("=").append(Constants.LINK_TYPE_IMAGE).append("&amp;");
		setImage(link.toString());
	}
}
