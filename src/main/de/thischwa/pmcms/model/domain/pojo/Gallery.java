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
package de.thischwa.pmcms.model.domain.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Base object for a gallery.
 *
 * @version $Id: Gallery.java 2216 2012-07-14 15:48:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class Gallery extends Page {
	private int thumbnailMaxWidth;
	private int thumbnailMaxHeight;
	private Template imageTemplate;
	private int imageMaxWidth;
	private int imageMaxHeight;
	private List<Image> images = new ArrayList<Image>();
	
	public int getThumbnailMaxWidth() {
		return thumbnailMaxWidth;
	}
	public void setThumbnailMaxWidth(int thumbnailMaxWidth) {
		this.thumbnailMaxWidth = thumbnailMaxWidth;
	}
	
	public int getThumbnailMaxHeight() {
		return thumbnailMaxHeight;
	}
	public void setThumbnailMaxHeight(int thumbnailMaxHeight) {
		this.thumbnailMaxHeight = thumbnailMaxHeight;
	}
	
	public Template getImageTemplate() {
		return imageTemplate;
	}
	public void setImageTemplate(Template imageTemplate) {
		this.imageTemplate = imageTemplate;
	}
	
	public int getImageMaxWidth() {
		return imageMaxWidth;
	}
	public void setImageMaxWidth(int imageMaxWidth) {
		this.imageMaxWidth = imageMaxWidth;
	}
	
	public int getImageMaxHeight() {
		return imageMaxHeight;
	}
	public void setImageMaxHeight(int imageMaxHeight) {
		this.imageMaxHeight = imageMaxHeight;
	}
	
	public List<Image> getImages() {
		return images;
	}
	public void setImages(List<Image> images) {
		this.images = images;
	}
	public void add(Image image) {
		// TODO check if there is already an image with the same filename
		images.add(image);
	}
	public boolean remove(Image image) {
		return images.remove(image);
	}
	
	@Override
	public TemplateType getTemplateType() {
		return TemplateType.GALLERY;
	}
}
