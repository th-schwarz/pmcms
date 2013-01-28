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
package de.thischwa.pmcms.gui.listener;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.configuration.resource.LabelHolder;
import de.thischwa.pmcms.exception.ProgressException;
import de.thischwa.pmcms.gui.dialog.DialogManager;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.model.tool.Upload;
import de.thischwa.pmcms.tool.DESCryptor;
import de.thischwa.pmcms.tool.connection.ConnectionAuthentificationException;
import de.thischwa.pmcms.tool.connection.ConnectionException;
import de.thischwa.pmcms.tool.connection.ConnectionFactory;

/**
 * Listener that triggers a site transfer without triggering an export.
 *
 * @version $Id: ListenerUploadSiteWithoutExport.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class ListenerUploadSiteWithoutExport implements SelectionListener {
	private Site site;
	
	
	public ListenerUploadSiteWithoutExport(final Site site) {
	    this.site = site;
    }

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetSelected(SelectionEvent event) {
		final Shell shell = event.display.getActiveShell();
		String checkumsFileBasename = InitializationManager.getProperty("pmcms.filename.checksums");

		DESCryptor cryptor = new DESCryptor(InitializationManager.getSiteProperty("pmcms.site.crypt.key"));
		String plainPwd = cryptor.decrypt(site.getTransferLoginPassword());
		Upload transferer = new Upload(site, ConnectionFactory.getFtp(site.getTransferHost(), site.getTransferLoginUser(), plainPwd,
				site.getTransferStartDirectory()), checkumsFileBasename);
		try {
			DialogManager.startProgressDialog(shell, transferer);
			MessageBox mb = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
			mb.setText(LabelHolder.get("popup.info")); //$NON-NLS-1$
			mb.setMessage("Site [" + site.getDecorationString() + "] uploaded successfull.");
			mb.open();
		} catch (ProgressException e) {
			MessageBox msg = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			msg.setText(LabelHolder.get("popup.error")); //$NON-NLS-1$
			if (e.getCause() instanceof ConnectionAuthentificationException)
				msg.setMessage("Ftp login failed. \nPlease check your login properties!");
			else
				msg.setMessage("An unknown error was happend: " + e.getMessage() + "\n\n" + e.getStackTrace());
			msg.open();
			return;
		}catch (ConnectionException cone) {
			MessageBox msg = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			msg.setText(LabelHolder.get("popup.error")); //$NON-NLS-1$
			msg.setMessage("While trying to establish the transfer connection: " + cone.getMessage());
			msg.open();
			return;
		} catch (Exception e) {
			MessageBox msg = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			msg.setText(LabelHolder.get("popup.error")); //$NON-NLS-1$
			msg.setMessage("While transfering the files, the following error was happend: " + e.getMessage() + "\n" + e.getStackTrace());
			msg.open();
			
		}
	}

}