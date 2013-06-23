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


import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.configuration.resource.LabelHolder;
import de.thischwa.pmcms.gui.dialog.DialogManager;
import de.thischwa.pmcms.livecycle.SiteHolder;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.model.tool.WriteBackup;

/**
 * Listener for backing up a {@link Site}.
 *
 * @author Thilo Schwarz
 */
public class ListenerBackupSite implements SelectionListener {
	private static Logger logger = Logger.getLogger(ListenerBackupSite.class);

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetSelected(SelectionEvent e) {
		final Shell shell = e.display.getActiveShell();
		SiteHolder siteHolder = InitializationManager.getBean(SiteHolder.class);
		Site site = siteHolder.getSite();
		if (site != null) {
			MessageBox mb = null;
			try {
				DialogManager.startProgressDialog(shell, new WriteBackup(site));
				mb = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
				mb.setText(LabelHolder.get("popup.info")); //$NON-NLS-1$
				mb.setMessage(LabelHolder.get("task.backup.ok")); //$NON-NLS-1$
			} catch (Exception e1) {
				logger.error("While backup a site: " + e1.getMessage(), e1);
				mb = new MessageBox(e.display.getActiveShell(), SWT.ICON_ERROR | SWT.OK);
				mb.setText(LabelHolder.get("popup.error")); //$NON-NLS-1$
				mb.setMessage(LabelHolder.get("task.backup.error").concat("\n").concat(e1.getMessage())); //$NON-NLS-1$
			}
			mb.open();
		} else 
			new IllegalArgumentException("Can't get the site object!");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}

}
