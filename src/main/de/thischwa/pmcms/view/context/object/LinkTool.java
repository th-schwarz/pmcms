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

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.thischwa.c5c.resource.Extension;
import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.exception.FatalException;
import de.thischwa.pmcms.livecycle.PojoHelper;
import de.thischwa.pmcms.model.InstanceUtil;
import de.thischwa.pmcms.model.domain.PoPathInfo;
import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.domain.pojo.Image;
import de.thischwa.pmcms.model.domain.pojo.Level;
import de.thischwa.pmcms.tool.PathTool;
import de.thischwa.pmcms.tool.file.FileTool;
import de.thischwa.pmcms.view.ViewMode;
import de.thischwa.pmcms.view.context.IContextObjectCommon;
import de.thischwa.pmcms.view.context.IContextObjectNeedPojoHelper;
import de.thischwa.pmcms.view.context.IContextObjectNeedViewMode;
import de.thischwa.pmcms.view.renderer.RenderData;
import de.thischwa.pmcms.wysisygeditor.CKResourceTool;

/**
 * Context object for building query strings for links. <br>
 * <pre>$linktool.addParameter('id', '1').addParameter('name', 'foo')</pre> generates: id=1&name=foo <br>
 * <br>
 * There is an additional method to get a link to a picture without the image rendering.
 * 
 * @version $Id: LinkTool.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
@Component("linktool")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class LinkTool implements IContextObjectCommon, IContextObjectNeedPojoHelper, IContextObjectNeedViewMode {
	private String linkTo = null;
	private Map<String, String> params = new HashMap<String, String>();
	private boolean isExportView;
	private PojoHelper pojoHelper;
	private APoormansObject<?> po;
	
	@Autowired
	private RenderData renderData;

	@Override
	public void setPojoHelper(final PojoHelper pojoHelper) {
		this.pojoHelper = pojoHelper;
		this.po = (APoormansObject<?>) this.pojoHelper.getRenderable();
	}

	@Override
	public void setViewMode(final ViewMode viewMode) {
		isExportView = viewMode.equals(ViewMode.EXPORT);
	}

	public LinkTool addParameter(String key, String value) {
		this.params.put(key, value);
		return this;
	}

	public LinkTool addParameter(String key, Serializable value) {
		addParameter(key, value.toString());
		return this;
	}

	public LinkTool setEditView() {
		clear();
		setLinkTo("/".concat(Constants.LINK_IDENTICATOR_EDIT));
		return this;
	}
	
	public LinkTool setSave() {
		clear();
		setLinkTo("/".concat(Constants.LINK_IDENTICATOR_SAVE));
		return this;
	}

	/**
	 * Using pictures without the normal image procedere.
	 */
	public LinkTool getPicture(String pictureRelativeToImageDirectory) {
		clear();
		Level level;
		if(InstanceUtil.isImage(po))
			level = ((Image)po).getParent().getParent();
		else
			level = (Level) po.getParent();
		if (isExportView) {
			String link = PathTool.getURLRelativePathToRoot(level).concat(CKResourceTool.getDir(Extension.IMAGE)).concat("/").concat(
			        pictureRelativeToImageDirectory);
			setLinkTo(link);

			File srcDir = PoPathInfo.getSiteResourceDirectory(pojoHelper.getSite(), Extension.IMAGE);
			File srcFile = new File(srcDir, pictureRelativeToImageDirectory);
			renderData.addCKResource(srcFile);
			File dstDir = PoPathInfo.getSiteExportResourceDirectory(pojoHelper.getSite(), Extension.IMAGE);
			File dstFile = new File(dstDir, pictureRelativeToImageDirectory);
			if(!dstFile.getParentFile().getAbsoluteFile().exists())
				dstFile.getParentFile().getAbsoluteFile().mkdirs();
			try {
				FileTool.copyFile(srcFile, dstFile);
			} catch (IOException e) {
				throw new FatalException("While copying: " + e.getMessage(), e);
			}
		} else {
			String link = PathTool.getURLFromFile(String.format("%s/%s/%s", Constants.LINK_IDENTICATOR_SITE_RESOURCE, CKResourceTool.getDir(Extension.IMAGE), pictureRelativeToImageDirectory));
			setLinkTo(link);
		}
		return this;
	}

	@Override
	public String toString() {
		StringBuilder link = new StringBuilder();
		String paramStr = null;

		if (this.linkTo == null)
			return "error_in_linktool-destination_not_set";

		if (!this.params.isEmpty()) {
			String amp = "&amp;";
			link.append("?");
			for (String key : this.params.keySet()) {
				link.append(key);
				link.append("=");
				link.append(this.params.get(key));
				link.append(amp);
			}
			paramStr = link.substring(0, link.length() - amp.length());
		}
		String linkString = this.linkTo.concat(StringUtils.defaultString(paramStr));
		clear();
		this.linkTo = "";
		return linkString;
	}

	private void setLinkTo(String linkTo) {
		clear();
		this.linkTo = linkTo;
	}

	private void clear() {
		this.params.clear();
	}
}
