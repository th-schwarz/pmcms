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
package de.thischwa.pmcms.view.context.object.admin;

import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.configuration.PropertiesManager;
import de.thischwa.pmcms.model.InstanceUtil;
import de.thischwa.pmcms.model.domain.pojo.ASiteResource;

/**
 * Context tool for admin stuff.
 *
 * @version $Id: AdminTool.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class AdminTool {
	private ASiteResource siteResource;

	public AdminTool(final ASiteResource siteResource) {
		this.siteResource = siteResource;
	}
	
	public boolean isMacro() {
		return InstanceUtil.isMacro(siteResource);
	}
	
	public boolean isTemplate() {
		return InstanceUtil.isTemplate(siteResource);
	}
	
	public String getBaseurl() {
		return InitializationManager.getBean(PropertiesManager.class).getProperty("baseurl");
	}
}
