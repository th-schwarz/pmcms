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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.exception.FatalException;
import de.thischwa.pmcms.livecycle.PojoHelper;
import de.thischwa.pmcms.model.IRenderable;
import de.thischwa.pmcms.model.InstanceUtil;
import de.thischwa.pmcms.tool.PathTool;
import de.thischwa.pmcms.view.ViewMode;
import de.thischwa.pmcms.view.context.IContextObjectCommon;
import de.thischwa.pmcms.view.context.IContextObjectNeedPojoHelper;
import de.thischwa.pmcms.view.context.IContextObjectNeedViewMode;

/**
 * Context object to retrieve the view mode of an {@link IRenderable} and to provide required constants for building links and forms. 
 * It's mainly used for build internal forms and links.
 */
@Component("contexttool")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ContextTool implements IContextObjectCommon, IContextObjectNeedPojoHelper, IContextObjectNeedViewMode {
	private ViewMode viewMode;
    private PojoHelper pojoHelper; 

	@Value("${baseurl}")
	private String baseUrl;	

	@Override
	public void setPojoHelper(final PojoHelper pojoHelper) {
		this.pojoHelper = pojoHelper;
	}
	
	@Override
	public void setViewMode(final ViewMode viewMode) {
		this.viewMode = viewMode;
	}

	public boolean isEditView() {
		return isMode(ViewMode.EDIT);
	}
	
    public String getTypeDescriptor() {
    	return Constants.LINK_TYPE_DESCRIPTOR;
    }

    public String getED() {
        return Constants.LINK_EDITFIELDS_DESCRIPTOR;
    }
    
    /**
     * @return The type string of the current {@link IRenderable}.
     */
    public String getTypePage() {
    	IRenderable renderable = pojoHelper.getRenderable();
    	if (InstanceUtil.isImage(renderable))
    		return Constants.LINK_TYPE_IMAGE;
    	if (InstanceUtil.isGallery(renderable))
    		return Constants.LINK_TYPE_GALLERY;
    	if (InstanceUtil.isPage(renderable))
    		return Constants.LINK_TYPE_PAGE;
    	throw new FatalException("Unknown renderable");
    }
	
	private boolean isMode(ViewMode mode) {
		return (viewMode == null) ? false : viewMode == mode; 
	}
	
    /**
     * @return Additional tags need to run poormans
     */
    public String getAdditionalHeader() {
    	if(viewMode == null || isMode(ViewMode.EXPORT)) 
    		return "";
    	StringBuilder sb = new StringBuilder();
    	sb.append(String.format("<base href=\"%s\"/>", baseUrl));
    	if(isMode(ViewMode.EDIT))
    		sb.append(String.format("<link rel=\"stylesheet\" type=\"text/css\" href=\"%s\" />", PathTool.getURLFromFile(InitializationManager.getDefaultResourcesPath().concat("editor-button.css"))));
    	return sb.toString();
    }
}
