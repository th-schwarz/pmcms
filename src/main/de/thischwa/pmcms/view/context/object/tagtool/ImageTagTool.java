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
package de.thischwa.pmcms.view.context.object.tagtool;

import java.io.File;
import java.io.IOException;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.thischwa.pmcms.exception.FatalException;
import de.thischwa.pmcms.livecycle.PojoHelper;
import de.thischwa.pmcms.model.domain.pojo.Image;
import de.thischwa.pmcms.tool.PathTool;
import de.thischwa.pmcms.tool.file.FileTool;
import de.thischwa.pmcms.tool.image.Dimension;
import de.thischwa.pmcms.tool.image.ImageTool;
import de.thischwa.pmcms.view.ViewMode;
import de.thischwa.pmcms.view.context.IContextObjectCommon;
import de.thischwa.pmcms.view.context.IContextObjectNeedPojoHelper;
import de.thischwa.pmcms.view.context.IContextObjectNeedViewMode;
import de.thischwa.pmcms.view.renderer.RenderData;
import de.thischwa.pmcms.wysisygeditor.CKImageResource;

/**
 * Construct an img-tag and initiate the image rendering. 
 */
@Component(value="imagetagtool")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ImageTagTool extends GenericXhtmlTagTool implements IContextObjectCommon, IContextObjectNeedViewMode, IContextObjectNeedPojoHelper {
	public static Logger logger = Logger.getLogger(ImageTagTool.class);
	private boolean fitToSize = false;
	private boolean pathOnly = false;
	private boolean isExportView;
	private PojoHelper pojoHelper;
	
	@Autowired
	private RenderData renderData;

	@Autowired
	private ImageTool imageTool;
	
	public ImageTagTool() {
		super("img");
	}

	@Override
	public void setViewMode(final ViewMode viewMode) {
		isExportView = viewMode.equals(ViewMode.EXPORT);
	}

	@Override
	public void setPojoHelper(final PojoHelper pojoHelper) {
		this.pojoHelper = pojoHelper;
	}

	public ImageTagTool setHeight(int height) {
		return this.setHeight(Integer.toString(height));
	}

	public ImageTagTool setHeight(final String height) {
		return putAttribute("height", height);
	}

	public ImageTagTool setWidth(int width) {
		return this.setWidth(Integer.toString(width));
	}

	public ImageTagTool setWidth(final String width) {
		return putAttribute("width", width);
	}

	public ImageTagTool setAlign(final String align) {
		return putAttribute("align", align);
	}

	public ImageTagTool setSrc(final String src) {
		return putAttribute("src", PathTool.encodePath(src));
	}

	public ImageTagTool setAlt(final String alt) {
		return putAttribute("alt", alt);
	}
	
	public ImageTagTool setImage(final Image image) {
		return setSrc(image.getParent().getName().concat("/").concat(image.getFileName()));
	}
	
	public ImageTagTool fitToSize() {
		fitToSize = true;
		return this;
	}
	
	public ImageTagTool pathOnly() {
		pathOnly = true;
		return this;
	}

	public ImageTagTool putAttribute(final String name, final String value) {
		putAttr(name, value);
		return this;
	}

	/**
	 * Signal that the inherited tag-tool was used inside the wysiwyg-editor. 
	 * For internal use only! 
	 * 
	 * @return
	 */
	public ImageTagTool usedFromEditor() {
		super.setUsedFromEditor(true);
		return this;
	}

	/**
	 * Construct the img-tag and tricker the image rendering.
	 * 
	 * @see GenericXhtmlTagTool#contructTag()
	 */
	@Override
	public String toString() {
		String srcString = getAttr("src");
		Dimension dim = Dimension.getDimensionFromAttr(attributes);
		String widthString = String.valueOf(dim.x);
		String heightString = String.valueOf(dim.y);
		
		// 1. check, if the base attributes are set
		if (StringUtils.isBlank(srcString) || StringUtils.isBlank(widthString) || StringUtils.isBlank(heightString))
			throw new IllegalArgumentException("One or more base attributes are not set !");
		
		
		// 2. image rendering
		CKImageResource imageFile = new CKImageResource(this.pojoHelper.getSite());
		imageFile.consructFromTagFromView(srcString);
		int width = Integer.parseInt(widthString);
		int height = Integer.parseInt(heightString);
		if (fitToSize) {
			Dimension realDimension = imageTool.getDimension(imageFile.getFile());
			imageFile.setDimension(realDimension.getScaledToFixSize(width, height));
		} else
			imageFile.setDimension(width, height);
		imageTool.createCashedImage(imageFile);

		// 3. construct the tag for preview and export
		if (isExportView) {
			try {
				File srcFile = imageFile.getCacheFile();
				File dstFile = imageFile.getExportFile();
				if(!dstFile.exists())
					FileTool.copyFile(srcFile, dstFile);
				renderData.addCKResource(imageFile);
			} catch (IOException e) {
				String msg = String.format("Error while copying cashed file [%s] to the export dir: %s", imageFile.getCacheFile().getPath(), e.getMessage());
				logger.error(msg, e);
				throw new FatalException(msg, e);
			}
			this.setSrc(imageFile.getTagSrcForExport(this.pojoHelper.getLevel()));
			logger.debug("ImageTagTool: build src-link for: ".concat(imageFile.getFile().getAbsolutePath()));
		} else {
			this.setSrc(imageFile.getTagSrcForPreview());
		}
		this.setWidth(String.valueOf(imageFile.getWidth()));
		this.setHeight(String.valueOf(imageFile.getHeight()));
		
		// clean up
		boolean tmpPathOnly = pathOnly;
		fitToSize = false;
		pathOnly = false;
		
		if(tmpPathOnly)
			return super.getAttr("src");
		else
			return super.contructTag();
	}
}
