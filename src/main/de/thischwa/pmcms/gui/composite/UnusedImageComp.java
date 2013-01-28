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
package de.thischwa.pmcms.gui.composite;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.thischwa.c5c.resource.Extension;
import de.thischwa.pmcms.gui.dialog.DialogManager;
import de.thischwa.pmcms.model.domain.pojo.Site;

/**
 * Composite to show the user the unused images of a rendering pass. The caller is 
 * {@link DialogManager#startDialogUnusedImages(org.eclipse.swt.widgets.Shell, Site, Collection)}.
 *
 * @version $Id: UnusedImageComp.java 2228 2013-01-06 10:50:17Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class UnusedImageComp extends Composite {
	private Composite mainComp = null;
	private Composite buttonBar = null;
	private Button okBtn = null;
	private Button cancelButton = null;
	private Composite listComp = null;
	private ListViewer unusedFileList = null;
	private PreviewCanvas previewCanvas = null;
	private Composite delListButtonBarComp = null;
	private Button moveToDelBtn = null;
	private Button moveToUnusedBtn = null;
	private ListViewer delFileList = null;
	private boolean listsInUse = false;
	private Collection<File> unusedFiles;

	/**
	 * @param unusedFiles Files which weren't used in the last rendering. If the dialog was canceled these collection is empty, otherwise
	 * 		it contains the files, which has to delete.
	 */
	public UnusedImageComp(Composite parent, int style, final Site site, Collection<File> unusedFiles) {
		super(parent, style);
		initialize();
		this.unusedFiles = unusedFiles;
		
		// sort the unused files
		List<File> sortedUnsedFiles = new ArrayList<File>(unusedFiles);
		Collections.sort(sortedUnsedFiles);
		
		unusedFileList.add(sortedUnsedFiles.toArray(new File[0]));
	}

	private void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		this.setLayout(gridLayout);
		this.setLayoutData(gridData);
		createMainComp();
		createButtonBar();

		initializeListener();
	}

	/**
	 * This method initializes mainComp
	 * 
	 */
	private void createMainComp() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		mainComp = new Composite(this, SWT.NONE);
		mainComp.setLayoutData(gridData);
		mainComp.setLayout(gridLayout);
		createListComp();

		GridData gridDataPreviewCanavas = new GridData();
		gridDataPreviewCanavas.heightHint = 200;
		gridDataPreviewCanavas.widthHint = 200;
		previewCanvas = new PreviewCanvas(mainComp, SWT.BORDER);
		previewCanvas.setLayoutData(gridDataPreviewCanavas);
	}

	/**
	 * This method initializes listComp
	 */
	private void createListComp() {
		GridLayout compGridLayout = new GridLayout();
		compGridLayout.numColumns = 3;
		GridData listGridData = new GridData();
		listGridData.widthHint = 200;
		listGridData.verticalAlignment = GridData.FILL;
		listGridData.grabExcessVerticalSpace = true;
		listGridData.grabExcessHorizontalSpace = true;
		listGridData.horizontalAlignment = GridData.FILL;
		GridData compGridData = new GridData();
		compGridData.widthHint = -1;
		compGridData.verticalAlignment = GridData.FILL;
		compGridData.grabExcessVerticalSpace = true;
		compGridData.horizontalAlignment = GridData.BEGINNING;
		listComp = new Composite(mainComp, SWT.NONE);
		listComp.setLayoutData(compGridData);
		listComp.setLayout(compGridLayout);
		Label unusedFileListLbl = new Label(listComp, SWT.NONE);
		unusedFileListLbl.setText("Unused files:");
		@SuppressWarnings("unused")
		Label filler1 = new Label(listComp, SWT.NONE);
		Label delFileListLbl = new Label(listComp, SWT.NONE);
		delFileListLbl.setText("Files to delete:");
		unusedFileList = new ListViewer(listComp, SWT.V_SCROLL | SWT.MULTI);
		unusedFileList.setLabelProvider(new FileLabelProvider());
		unusedFileList.getList().setLayoutData(listGridData);
		createDelListBarComp();
		delFileList = new ListViewer(listComp, SWT.V_SCROLL | SWT.MULTI);
		delFileList.setLabelProvider(new FileLabelProvider());
		delFileList.getList().setLayoutData(listGridData);
	}

	/**
	 * This method initializes delListBarComp
	 */
	private void createDelListBarComp() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = false;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.END;
		delListButtonBarComp = new Composite(listComp, SWT.NONE);
		delListButtonBarComp.setLayoutData(gridData);
		delListButtonBarComp.setLayout(gridLayout);
		moveToDelBtn = new Button(delListButtonBarComp, SWT.NONE);
		moveToDelBtn.setText("->");
		moveToUnusedBtn = new Button(delListButtonBarComp, SWT.NONE);
		moveToUnusedBtn.setText("<-");
	}

	/**
	 * This method initializes buttonBar
	 */
	private void createButtonBar() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.END;
		buttonBar = new Composite(this, SWT.NONE);
		buttonBar.setLayoutData(gridData);
		buttonBar.setLayout(gridLayout);
		okBtn = new Button(buttonBar, SWT.NONE);
		okBtn.setText("ok");
		cancelButton = new Button(buttonBar, SWT.NONE);
		cancelButton.setText("cancel");

		okBtn.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				unusedFiles.clear();
				for(int i=0; i<delFileList.getList().getItemCount(); i++) {
					File file = (File) delFileList.getElementAt(i);
					unusedFiles.add(file);
				}
				getShell().dispose();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		cancelButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				unusedFiles.clear();
				getShell().dispose();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	/**
	 * initialize the listener for the 2 file lists and the 'move'-buttons
	 */
	private void initializeListener() {
		delFileList.addDoubleClickListener(new ListDoubleClickListener(delFileList, unusedFileList));
		delFileList.addSelectionChangedListener(new ListSelectionListener(delFileList));
		unusedFileList.addDoubleClickListener(new ListDoubleClickListener(unusedFileList, delFileList));
		unusedFileList.addSelectionChangedListener(new ListSelectionListener(unusedFileList));

		moveToDelBtn.addSelectionListener(new ButtonSelectionListener(unusedFileList, delFileList));
		moveToUnusedBtn.addSelectionListener(new ButtonSelectionListener(delFileList, unusedFileList));
	}
	
	private void move(ListViewer srcList, ListViewer destList, int[] indices) {
		List<Object> objsToMove = new ArrayList<Object>(indices.length);
		for (int index : indices) {
			objsToMove.add(srcList.getElementAt(index));
		}
		for (Object obj : objsToMove) {
			destList.add(obj);
			srcList.remove(obj);
		}
	}

	private class ButtonSelectionListener implements SelectionListener {
		private ListViewer srcList;
		private ListViewer destList;

		public ButtonSelectionListener(ListViewer srcList, ListViewer destList) {
			this.srcList = srcList;
			this.destList = destList;
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			action();
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			action();
		}
		
		private void action() {
			move(srcList, destList, srcList.getList().getSelectionIndices());			
		}
	}
	
	private class FileLabelProvider extends LabelProvider {
		@Override
		public String getText(Object element) {
			if(element instanceof File)
				return ((File)element).getName();
			return super.getText(element);
		}
	}
	
	private class ListDoubleClickListener implements IDoubleClickListener {
		private ListViewer srcList;
		private ListViewer destList;

		public ListDoubleClickListener(ListViewer srcList, ListViewer destList) {
			this.srcList = srcList;
			this.destList = destList;
		}
		@Override
		public void doubleClick(DoubleClickEvent evt) {
			if (!listsInUse)
				move(srcList, destList, srcList.getList().getSelectionIndices());		
		}
	}

	private class ListSelectionListener implements ISelectionChangedListener {
		private ListViewer srcList;
		
		public ListSelectionListener(ListViewer srcList) {
			this.srcList = srcList;
		}
		
		@Override
		public void selectionChanged(SelectionChangedEvent evt) {
			if(evt.getSelection().isEmpty())
				return;
			listsInUse = true;
			int selected = srcList.getList().getSelectionIndex();
			if (selected == -1)
				previewCanvas.preview();
			else {
				File file = (File)srcList.getElementAt(selected);
				String ext = FilenameUtils.getExtension(file.getName());
				if(StringUtils.isBlank(ext) || !Extension.IMAGE.isAllowedExtension(ext)) {
					previewCanvas.preview();
				} else {
					previewCanvas.preview(file);
				}
			}
			listsInUse = false;
		}
	}
}
