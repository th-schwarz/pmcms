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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.configuration.resource.LabelHolder;
import de.thischwa.pmcms.gui.BrowserManager;
import de.thischwa.pmcms.gui.treeview.TreeViewManager;
import de.thischwa.pmcms.livecycle.SiteHolder;
import de.thischwa.pmcms.model.domain.PoPathInfo;
import de.thischwa.pmcms.model.domain.pojo.Gallery;
import de.thischwa.pmcms.model.domain.pojo.Image;
import de.thischwa.pmcms.model.tool.SitePersister;
import de.thischwa.pmcms.tool.file.FileTool;
import de.thischwa.pmcms.view.ViewMode;

/**
 * Listener for starting the bulk import for {@link Image}s.
 * 
 * @version $Id: ListenerImageBulkImport.java 2213 2012-06-30 12:01:07Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class ListenerImageBulkImport implements SelectionListener {
	private static Logger logger = Logger.getLogger(ListenerImageBulkImport.class);
	private Gallery gallery;

	public ListenerImageBulkImport(final Gallery gallery) {
		if (gallery == null)
			throw new IllegalArgumentException("Gallery is null!");
		this.gallery = gallery;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		final Shell shell = e.display.getActiveShell();
		File galleryDirectory = PoPathInfo.getSiteGalleryDirectory(this.gallery);
		FileDialog fileDialog = new FileDialog(shell, SWT.MULTI);
		fileDialog.setText("Select one or more images ...");
		List<String> exts = new ArrayList<String>();
		for (String extension : InitializationManager.getAllowedImageExtensions()) {
			exts.add("*." + extension);
		}
		exts.add(0, StringUtils.join(exts.iterator(), ';'));
		fileDialog.setFilterExtensions(exts.toArray(new String[exts.size()]));
		if (galleryDirectory.exists())
			fileDialog.setFilterPath(galleryDirectory.getAbsolutePath());
		if (fileDialog.open() != null) {
			// collecting files
			List<File> filesToCopy = new ArrayList<File>(fileDialog.getFileNames().length);
			for (String fileName : fileDialog.getFileNames())
				filesToCopy.add(new File(fileDialog.getFilterPath(), fileName));
			List<File> copiedFiles = null;
			if (!(new File(fileDialog.getFilterPath()).getAbsolutePath().startsWith(galleryDirectory.getAbsolutePath()))) {
				try {
					copiedFiles = FileTool.copyToDirectoryUnique(filesToCopy, galleryDirectory);
				} catch (IOException e1) {
					MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
					mb.setText(LabelHolder.get("popup.error")); //$NON-NLS-1$
					mb.setMessage("Error while copying files:\n" + e1.getStackTrace().toString());
					mb.open();
					return;
				}
			} else {
				logger.debug("Image files are not copied, because they are in the right directory!");
				copiedFiles = filesToCopy;
			}
			
			SiteHolder siteHolder = InitializationManager.getBean(SiteHolder.class);
			for (File file : copiedFiles) {
				Image image = new Image();
				image.setParent(this.gallery);
				image.setFileName(FilenameUtils.getName(file.getAbsolutePath()));
				siteHolder.mark(image);
				this.gallery.add(image);
				logger.debug("Image added: ".concat(image.getDecorationString()));
			}

			TreeViewManager treeViewManager = InitializationManager.getBean(TreeViewManager.class);
			treeViewManager.fillAndExpands(this.gallery);
			BrowserManager browserManager = InitializationManager.getBean(BrowserManager.class);
			browserManager.view(this.gallery, ViewMode.PREVIEW);
			try {
				SitePersister.write(siteHolder.getSite());
			} catch (IOException e2) {
				throw new RuntimeException(e2);
			}
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}
}
