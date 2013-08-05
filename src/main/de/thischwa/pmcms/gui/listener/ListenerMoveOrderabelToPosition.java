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

import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import de.thischwa.pmcms.conf.InitializationManager;
import de.thischwa.pmcms.gui.BrowserManager;
import de.thischwa.pmcms.gui.treeview.TreeViewManager;
import de.thischwa.pmcms.livecycle.SiteHolder;
import de.thischwa.pmcms.model.IOrderable;
import de.thischwa.pmcms.model.InstanceUtil;
import de.thischwa.pmcms.model.domain.PoStructurTools;
import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.tool.SitePersister;
import de.thischwa.pmcms.view.ViewMode;

/**
 * Listener for moving an {@link IOrderable} to a position.
 *
 * @author Thilo Schwarz
 */
public class ListenerMoveOrderabelToPosition implements SelectionListener {
	private static Logger logger = Logger.getLogger(ListenerMoveOrderabelToPosition.class);
	private IOrderable<?> orderable;
	private int pos;
		
	public ListenerMoveOrderabelToPosition(IOrderable<?> orderable, int pos) {
		this.orderable = orderable;
		this.pos = pos;
	}

	@Override
	public void widgetSelected(SelectionEvent event) {
		logger.debug("SEL move to postion");
		TreeViewManager treeViewManager = InitializationManager.getBean(TreeViewManager.class);
		PoStructurTools.moveOrderableTo(orderable, pos);

		treeViewManager.fillAndExpands((APoormansObject<?>) orderable);
		if (InstanceUtil.isRenderable(orderable)) {
			BrowserManager browserManager = InitializationManager.getBean(BrowserManager.class);
			browserManager.view((APoormansObject<?>) orderable, ViewMode.PREVIEW);
			SiteHolder siteHolder = InitializationManager.getBean(SiteHolder.class);
			try {
				SitePersister.write(siteHolder.getSite());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent event) {
	}
}
