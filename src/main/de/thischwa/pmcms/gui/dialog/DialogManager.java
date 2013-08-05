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
package de.thischwa.pmcms.gui.dialog;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.thischwa.pmcms.conf.resource.ImageHolder;
import de.thischwa.pmcms.exception.FatalException;
import de.thischwa.pmcms.exception.ProgressException;
import de.thischwa.pmcms.gui.IProgressViewer;
import de.thischwa.pmcms.gui.composite.UnusedImageComp;
import de.thischwa.pmcms.gui.dialog.pojo.DialogCreator;
import de.thischwa.pmcms.model.domain.PoPathInfo;
import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.tool.file.FileTool;
import de.thischwa.pmcms.tool.swt.SWTUtils;

/**
 * Helper object for various popup dialogs.
 * 
 * @author Thilo Schwarz
 */
public class DialogManager {
	private static Logger logger = Logger.getLogger(DialogManager.class);

	/**
	 * Open the new/edit popup dialog for all database pojos ({@link de.thischwa.pmcms.model.domain.pojo pojos}). It starts a new shell for
	 * {@link DialogCreator} and waits for its dispose.
	 * 
	 * @return False if the dialog was canceled, otherwise true.
	 */
	public static boolean startDialogPersitentPojo(final Shell parentShell, APoormansObject<?> po) {
		final Shell shell = new Shell(parentShell, SWT.APPLICATION_MODAL | SWT.TITLE);
		shell.setImages(new Image[] { ImageHolder.SHELL_ICON_SMALL, ImageHolder.SHELL_ICON_BIG });
		shell.setLayout(new FillLayout());
		DialogCreator application = new DialogCreator(shell, SWT.NONE, po);
		shell.pack();
		shell.open();
		SWTUtils.center(shell, parentShell.getBounds());

		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		shell.dispose();

		return !application.isCancel();
	}

	/**
	 * Open the dialog for choosing which unused image files should delete and delete those.
	 * 
	 * @param parentShell
	 * @param site
	 *            Current {@link Site}
	 * @param usedCkResources
	 *            Unused images of the last export.
	 */
	public static void startDialogUnusedImages(final Shell parentShell, final Site site, Collection<File> usedCkResources) {
		File galleryDir = PoPathInfo.getSiteResourceGalleryDirectory(site);
		File imageDir = PoPathInfo.getSiteResourceImageDirectory(site);
		File otherDir = PoPathInfo.getSiteResourceOtherDirectory(site);
		
		Collection<File> unusedCkResources = new HashSet<File>();
		if(galleryDir.exists())
			unusedCkResources.addAll(FileTool.collectFiles(galleryDir, usedCkResources));
		if(imageDir.exists())
			unusedCkResources.addAll(FileTool.collectFiles(imageDir, usedCkResources));
		if(otherDir.exists())
			unusedCkResources.addAll(FileTool.collectFiles(otherDir, usedCkResources));
		if (CollectionUtils.isEmpty(unusedCkResources)) {
			logger.info("No unused resources found.");
			return;
		}
		
		final Shell shell = new Shell(parentShell, SWT.APPLICATION_MODAL | SWT.TITLE);
		shell.setImages(new Image[] { ImageHolder.SHELL_ICON_SMALL, ImageHolder.SHELL_ICON_BIG });
		shell.setLayout(new GridLayout());

		new UnusedImageComp(shell, SWT.NONE, site, unusedCkResources);

		shell.pack();
		shell.open();
		SWTUtils.center(shell, parentShell.getBounds());

		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		if (CollectionUtils.isNotEmpty(unusedCkResources)) 
			FileTool.deleteFiles(unusedCkResources);
		shell.dispose();
	}

	/**
	 * Open the progress dialog and run a {@link IProgressViewer}.
	 */
	public static void startProgressDialog(final Shell parentShell, final IProgressViewer progressViewer) {
		IRunnableWithProgress runnableWithProgress = new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				progressViewer.setMonitor(monitor);
				try {
					progressViewer.run();
				} catch (Exception e) {
					logger.error("While IProgressViewer was running: " + e.getMessage(), e);
					throw new ProgressException(e);
				}
			}
		};

		ProgressMonitorDialog dialog = new ProgressMonitorDialog(parentShell);
		try {
			dialog.run(true, true, runnableWithProgress);
		} catch (Exception e) {
			throw new FatalException(e.getCause().getMessage(), e.getCause());
		}
	}
}
