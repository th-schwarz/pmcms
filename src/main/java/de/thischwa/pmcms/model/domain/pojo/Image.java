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
package de.thischwa.pmcms.model.domain.pojo;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import de.thischwa.pmcms.conf.InitializationManager;
import de.thischwa.pmcms.model.IOrderable;
import de.thischwa.pmcms.model.IRenderable;

/**
 * Base object of an image, - images of a {@link Gallery}
 *
 * @author Thilo Schwarz
 */
public class Image extends APoormansObject<Gallery> implements IRenderable, IOrderable<Image> {
	private String fileName;
	private String title;
	private String description;
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(final String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(final String title) {
		this.title = title;
	}
	
	@Override
	public String getDecorationString() {
		String deco = StringUtils.defaultIfEmpty(title, fileName); 
		if(InitializationManager.isAdmin())
			deco = String.format("%s#%d", deco, getId()); 
		return deco;
	}
	
	/**
	 * Just a wrapper to get a better handling in templates.
	 * 
	 * @return the parent
	 */
	public Gallery getGallery() {
		return getParent();
	}
	
	@Override
	public Template getTemplate() {
		return (getParent() == null) ? null : getParent().getImageTemplate();
	}

	@Override
	public TemplateType getTemplateType() {
		return TemplateType.IMAGE;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("title", getTitle()).append("file",getFileName()).toString();
	}
	
	@Override
	public List<Image> getFamily() {
		return getParent().getImages();
	}
}
