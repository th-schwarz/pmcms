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
package de.thischwa.pmcms.gui.listener;


import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import de.thischwa.pmcms.conf.InitializationManager;
import de.thischwa.pmcms.gui.BrowserManager;
import de.thischwa.pmcms.gui.treeview.TreeViewManager;
import de.thischwa.pmcms.model.InstanceUtil;
import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.domain.pojo.Page;
import de.thischwa.pmcms.view.ViewMode;

/**
 * Listener for editing a {@link Page}.
 * 
 * @author Thilo Schwarz
 */
public class ListenerEditPage implements SelectionListener {

	@Override
	public void widgetSelected(SelectionEvent e) {
		TreeViewManager treeViewManager = InitializationManager.getBean(TreeViewManager.class);
		APoormansObject<?> po = treeViewManager.getSelectedTreeSitepo();
		if (po != null && InstanceUtil.isPage(po)) {
			Page page = (Page) po;
			BrowserManager browserManager = InitializationManager.getBean(BrowserManager.class);
			browserManager.view(page, ViewMode.EDIT);
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}
}
