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
package de.thischwa.pmcms.gui.dialog.pojo;

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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.configuration.resource.LabelHolder;
import de.thischwa.pmcms.gui.composite.PreviewCanvas;
import de.thischwa.pmcms.model.domain.PoInfo;
import de.thischwa.pmcms.model.domain.PoPathInfo;
import de.thischwa.pmcms.model.domain.pojo.Image;
import de.thischwa.pmcms.tool.file.FileTool;
import de.thischwa.pmcms.wysisygeditor.CKImageResource;

/**
 * Composite is part of {@link DialogCreator} and contains input fields and their validation method for {@link Image}. 
 */
public class DialogFieldsImageComp extends Composite implements IDialogFieldsValidator {
	private static Logger logger = Logger.getLogger(DialogFieldsImageComp.class);
	private Text textTitle = null;
	private Text textDescription = null;
	private Text textFilename = null;
	private PreviewCanvas previewCanvas = null;
	private Image image = null;
	private DialogCreator dialogCreator = null;
	private File chosenImageFile = null;

	
	public DialogFieldsImageComp(Composite parent, int style, Image image) {
		super(parent, style);
		dialogCreator = (DialogCreator) parent;
		this.image = image;
		initialize();
		pack();
		if (StringUtils.isNotBlank(this.image.getFileName())) {
			CKImageResource imageFile = new CKImageResource(PoInfo.getSite(image));
			imageFile.consructFromTagFromView(image.getParent().getName().concat(File.separator).concat(this.image.getFileName()));
			previewCanvas.preview(imageFile.getFile().getAbsoluteFile());
		} else
			previewCanvas.preview();
	}
	
	private void initialize() {
		GridLayout gridLayoutMy = new GridLayout();
		gridLayoutMy.numColumns = 2;
		gridLayoutMy.marginWidth = 5;
		gridLayoutMy.verticalSpacing = 5;
		gridLayoutMy.horizontalSpacing = 5;
		gridLayoutMy.marginHeight = 5;
		gridLayoutMy.makeColumnsEqualWidth = false;
		GridData gridDataMy = new org.eclipse.swt.layout.GridData();
		gridDataMy.grabExcessHorizontalSpace = true;
		gridDataMy.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridDataMy.horizontalSpan = 1;
		gridDataMy.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		this.setLayoutData(gridDataMy);
		this.setLayout(gridLayoutMy);

		createCanvasPreview();
		createCompositeFields();
	}
	
	private void createCanvasPreview() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.heightHint = 200;
		gridData.widthHint = 200;
		previewCanvas = new PreviewCanvas(this, SWT.BORDER);
		previewCanvas.setLayoutData(gridData); 
	}

	
	private void createCompositeFields() {
		Composite composite = new Composite(this, SWT.NONE);
		GridLayout gridLayoutMy = new GridLayout();
		gridLayoutMy.numColumns = 2;
		gridLayoutMy.marginWidth = 10;
		gridLayoutMy.verticalSpacing = 5;
		gridLayoutMy.horizontalSpacing = 20;
		gridLayoutMy.marginHeight = 25;
		gridLayoutMy.makeColumnsEqualWidth = false;
		GridData gridDataMy = new org.eclipse.swt.layout.GridData();
		gridDataMy.grabExcessHorizontalSpace = false;
		gridDataMy.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridDataMy.horizontalSpan = 1;
		gridDataMy.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
		composite.setLayout(gridLayoutMy);
		composite.setLayoutData(gridDataMy);
		
		GridData gridDataLabel = new GridData();
		gridDataLabel.widthHint = 100;
		gridDataLabel.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridDataLabel.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
		GridData gridDataText = new GridData();
		gridDataText.heightHint = -1;
		gridDataText.widthHint = 150;
		
		Label labelTitle = new Label(composite, SWT.RIGHT);
		labelTitle.setText(LabelHolder.get("dialog.pojo.image.fields.title")); //$NON-NLS-1$
		labelTitle.setLayoutData(gridDataLabel);
		textTitle = new Text(composite, SWT.BORDER);
		textTitle.setTextLimit(256);
		textTitle.setLayoutData(gridDataText);
		textTitle.setText(StringUtils.defaultString(image.getTitle()));
		textTitle.addModifyListener(new ModifyListenerClearErrorMessages(dialogCreator));
		
		Label labelDescription = new Label(composite, SWT.RIGHT);
		labelDescription.setText(LabelHolder.get("dialog.pojo.image.fields.description")); //$NON-NLS-1$
		labelDescription.setLayoutData(gridDataLabel);
		textDescription = new Text(composite, SWT.BORDER);
		textDescription.setTextLimit(256);
		textDescription.setLayoutData(gridDataText);
		textDescription.setText(StringUtils.defaultString(image.getDescription()));
		textDescription.addModifyListener(new ModifyListenerClearErrorMessages(dialogCreator));
		
		GridData gridDataCompositeFile = new org.eclipse.swt.layout.GridData();
		gridDataCompositeFile.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridDataCompositeFile.grabExcessHorizontalSpace = true;
		gridDataCompositeFile.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		
		Label labelFilename = new Label(composite, SWT.RIGHT);
		labelFilename.setText("*  ".concat(LabelHolder.get("dialog.pojo.image.fields.file"))); //$NON-NLS-1$
		labelFilename.setLayoutData(gridDataLabel);
		Composite compositeFile = new Composite(composite, SWT.NONE);
		GridLayout gridLayoutCompositeFile = new GridLayout();
		gridLayoutCompositeFile.makeColumnsEqualWidth = false;
		gridLayoutCompositeFile.horizontalSpacing = 6;
		gridLayoutCompositeFile.marginHeight = 0;
		gridLayoutCompositeFile.marginWidth = 0;
		gridLayoutCompositeFile.numColumns = 2;
		compositeFile.setLayout(gridLayoutCompositeFile);
		compositeFile.setLayoutData(gridDataCompositeFile);
		GridData gridDataTextFile = new GridData();
		gridDataTextFile.widthHint = 120;
		textFilename = new Text(compositeFile, SWT.BORDER);
		textFilename.setLayoutData(gridDataTextFile);
		textFilename.setEditable(false);
		textFilename.setText(StringUtils.defaultString(image.getFileName()));
		
		Button button = new Button(compositeFile, SWT.NONE);
		button.setText("..."); //$NON-NLS-1$
		button.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(e.display.getActiveShell(), SWT.OPEN);
				fileDialog.setText(LabelHolder.get("dialog.pojo.image.fields.image")); //$NON-NLS-1$
				List<String> exts = new ArrayList<String>();
				for (String extension : InitializationManager.getAllowedImageExtensions()) {
					exts.add("*.".concat(extension)); //$NON-NLS-1$
				}
				exts.add(0, StringUtils.join(exts.iterator(), ';'));
				fileDialog.setFilterExtensions(exts.toArray(new String[exts.size()]));
				fileDialog.setFilterPath(PoPathInfo.getSiteGalleryDirectory(image.getParent()).getAbsolutePath());
				String chosenImageFilename = fileDialog.open();
				if (StringUtils.isNotBlank(chosenImageFilename)) {
					chosenImageFile = new File(chosenImageFilename);
					previewCanvas.preview(chosenImageFile);
					if (StringUtils.isNotBlank(chosenImageFilename)) {
						String tempName = FilenameUtils.getName(chosenImageFilename);
						textFilename.setText(tempName);
					} else
						previewCanvas.preview();
				} else {
					chosenImageFile = null;
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {				
			}			
		});
	}
	
	@Override
	public boolean isValid() {
		if (StringUtils.isBlank(textFilename.getText())) {
			dialogCreator.setErrorMessage(LabelHolder.get("dialog.pojo.image.error.file")); //$NON-NLS-1$
			return false;
		}
		image.setTitle(textTitle.getText());
		image.setDescription(textDescription.getText());
		File galleryDir = PoPathInfo.getSiteGalleryDirectory(image.getParent());
		if (chosenImageFile != null && !FileTool.isInside(galleryDir, chosenImageFile)) { // image isn't inside the gallery dir
			try {
				File imageFile = FileTool.copyToDirectoryUnique(chosenImageFile, galleryDir);
				chosenImageFile = imageFile.getAbsoluteFile();
				textFilename.setText(imageFile.getName());
			} catch (IOException e1) {
				logger.error("While copying the image file inside gallery's directory: " + e1.getMessage(), e1); //$NON-NLS-1$
				dialogCreator.setErrorMessage(String.format("Error while copying [%s] to [%s]: %s", chosenImageFile.getPath(), galleryDir.getPath()));
				return false;
			}
		}
		image.setFileName(textFilename.getText());
		return true;
	}

}
