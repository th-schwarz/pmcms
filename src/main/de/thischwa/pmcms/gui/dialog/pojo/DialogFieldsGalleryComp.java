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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import de.thischwa.pmcms.configuration.resource.LabelHolder;
import de.thischwa.pmcms.model.domain.PoInfo;
import de.thischwa.pmcms.model.domain.pojo.Gallery;
import de.thischwa.pmcms.model.domain.pojo.Page;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.model.domain.pojo.Template;
import de.thischwa.pmcms.model.domain.pojo.TemplateType;
import de.thischwa.pmcms.tool.swt.FileNameVerifier;

/**
 * Composite is part of {@link DialogCreator} and contains input fields and their validation method for {@link Gallery}.
 *
 * @version $Id: DialogFieldsGalleryComp.java 2216 2012-07-14 15:48:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class DialogFieldsGalleryComp extends Composite implements IDialogFieldsValidator {
	private Text textName = null;
	private Text textTitle = null;
	private static final int MAX_SPINNER_VALUE = 2000;
	private Spinner spinnerThumbnailMaxWidth = null;
	private Spinner spinnerThumbnailMaxHeight = null;
	private TemplateListViewer listTemplates = null;
	private TemplateListViewer listImageTemplates = null;
	private Spinner spinnerImageMaxWidth = null;
	private Spinner spinnerImageMaxHeight = null;
	
	private Gallery gallery = null;
	private DialogCreator dialogCreator = null;
	
	public DialogFieldsGalleryComp(Composite parent, final int style, Gallery gallery) {
		super(parent, style);
		this.dialogCreator = (DialogCreator) parent;
		this.gallery = gallery;
		initialize();
	}


	private void initialize() {
		Site site = PoInfo.getSite(gallery);
		
		GridData gridDataLabel = new GridData();
		gridDataLabel.widthHint = 220;
		gridDataLabel.verticalAlignment = GridData.CENTER;
		gridDataLabel.horizontalAlignment = GridData.END;
		GridData gridDataText = new GridData();
		gridDataText.heightHint = -1;
		gridDataText.widthHint = 150;
		GridLayout gridLayoutMy = new GridLayout();
		gridLayoutMy.numColumns = 2;
		gridLayoutMy.marginWidth = 25;
		gridLayoutMy.verticalSpacing = 5;
		gridLayoutMy.horizontalSpacing = 20;
		gridLayoutMy.marginHeight = 25;
		gridLayoutMy.makeColumnsEqualWidth = false;
		GridData gridDataMy = new GridData();
		gridDataMy.grabExcessHorizontalSpace = true;
		gridDataMy.verticalAlignment = GridData.CENTER;
		gridDataMy.horizontalSpan = 1;
		gridDataMy.horizontalAlignment = GridData.FILL;
		this.setLayoutData(gridDataMy);
		this.setLayout(gridLayoutMy);

		initializeTitleAndName();

		GridData gridDataList = new GridData();
		gridDataList.horizontalAlignment = GridData.FILL;
		gridDataList.grabExcessHorizontalSpace = true;
		gridDataList.heightHint = 60;
		gridDataList.verticalAlignment = GridData.CENTER;
		Label labelTemplate = new Label(this, SWT.RIGHT);
		labelTemplate.setText("*   ".concat(LabelHolder.get("dialog.pojo.page.fields.template"))); //$NON-NLS-1$ //$NON-NLS-2$
		labelTemplate.setLayoutData(gridDataLabel);
    	listTemplates = new TemplateListViewer(this, gridDataList);
    	java.util.List<Template> templates = PoInfo.getTemplates(site, gallery.getTemplateType());
    	listTemplates.setInput(templates);
    	listTemplates.setSelectedTemplate(gallery.getTemplate());
    	
		Label labelThumbnailMaxWidth = new Label(this, SWT.RIGHT);
		labelThumbnailMaxWidth.setText("*  ".concat(LabelHolder.get("dialog.pojo.gallery.fields.thumbnail.width"))); //$NON-NLS-1$
		labelThumbnailMaxWidth.setLayoutData(gridDataLabel);
		spinnerThumbnailMaxWidth = new Spinner(this, SWT.NONE);
		spinnerThumbnailMaxWidth.setMaximum(MAX_SPINNER_VALUE);
		spinnerThumbnailMaxWidth.setSelection(gallery.getThumbnailMaxWidth());
		spinnerThumbnailMaxWidth.setLayoutData(gridDataText);

		Label labelThumbnailMaxHeight = new Label(this, SWT.RIGHT);
		labelThumbnailMaxHeight.setText("*  ".concat(LabelHolder.get("dialog.pojo.gallery.fields.thumbnail.height"))); //$NON-NLS-1$
		labelThumbnailMaxHeight.setLayoutData(gridDataLabel);
		spinnerThumbnailMaxHeight = new Spinner(this, SWT.NONE);
		spinnerThumbnailMaxHeight.setMaximum(MAX_SPINNER_VALUE);
		spinnerThumbnailMaxHeight.setSelection(gallery.getThumbnailMaxHeight());
		spinnerThumbnailMaxHeight.setLayoutData(gridDataText);
		
		Label labelTemplates = new Label(this, SWT.RIGHT);
		labelTemplates.setText("*  ".concat(LabelHolder.get("dialog.pojo.gallery.fields.image.template"))); //$NON-NLS-1$
		labelTemplates.setLayoutData(gridDataLabel);
		listImageTemplates = new TemplateListViewer(this, gridDataList);
		java.util.List<Template> imgTemplates = PoInfo.getTemplates(site, TemplateType.IMAGE);
		listImageTemplates.setInput(imgTemplates);
		listImageTemplates.setSelectedTemplate(gallery.getImageTemplate());
		
		Label labelImageMaxWidth = new Label(this, SWT.RIGHT);
		labelImageMaxWidth.setText("*  ".concat(LabelHolder.get("dialog.pojo.gallery.fields.image.width"))); //$NON-NLS-1$
		labelImageMaxWidth.setLayoutData(gridDataLabel);
		spinnerImageMaxWidth = new Spinner(this, SWT.NONE);
		spinnerImageMaxWidth.setMaximum(MAX_SPINNER_VALUE);
		spinnerImageMaxWidth.setSelection(gallery.getImageMaxWidth());
		spinnerImageMaxWidth.setLayoutData(gridDataText);

		Label labelImageMaxHeight = new Label(this, SWT.RIGHT);
		labelImageMaxHeight.setText("*  ".concat(LabelHolder.get("dialog.pojo.gallery.fields.image.height"))); //$NON-NLS-1$
		labelImageMaxHeight.setLayoutData(gridDataLabel);
		spinnerImageMaxHeight = new Spinner(this, SWT.NONE);
		spinnerImageMaxHeight.setMaximum(MAX_SPINNER_VALUE);
		spinnerImageMaxHeight.setSelection(gallery.getImageMaxHeight());
		spinnerImageMaxHeight.setLayoutData(gridDataText);
	}

	private void initializeTitleAndName() {
		String filename = StringUtils.defaultString(gallery.getName());
		GridData gridDataLabel = new GridData();
		gridDataLabel.widthHint = 100;
		gridDataLabel.verticalAlignment = GridData.CENTER;
		gridDataLabel.horizontalAlignment = GridData.END;
		GridData gridDataText = new GridData();
		gridDataText.heightHint = -1;
		gridDataText.widthHint = 150;
		Label labelTitle = new Label(this, SWT.RIGHT);
		labelTitle.setText(LabelHolder.get("dialog.pojo.page.fields.title")); //$NON-NLS-1$
		labelTitle.setLayoutData(gridDataLabel);
		textTitle = new Text(this, SWT.BORDER);
		textTitle.setTextLimit(256);
		textTitle.setLayoutData(gridDataText);
		textTitle.setText(StringUtils.defaultString(gallery.getTitle()));
		Label labelName = new Label(this, SWT.RIGHT);
		labelName.setText("*   ".concat(LabelHolder.get("dialog.pojo.page.fields.name"))); //$NON-NLS-1$ //$NON-NLS-2$
		labelName.setLayoutData(gridDataLabel);
		textName = new Text(this, SWT.BORDER);
		textName.setTextLimit(256);
		textName.setLayoutData(gridDataText);
		textName.setText(filename);
		textName.addVerifyListener(new FileNameVerifier());
		textName.addModifyListener(new ModifyListenerClearErrorMessages(dialogCreator));

		Collection<String> forbiddenNames = new ArrayList<String>();
		Collection<Page> sisters = gallery.getParent().getPages();
		if(CollectionUtils.isNotEmpty(sisters))
			for(Page page : sisters)
				forbiddenNames.add(page.getName());

		Map<String, Gallery> galleries = PoInfo.getGalleries(PoInfo.getSite(gallery));
		if(galleries != null && !galleries.keySet().isEmpty()) {
			for(String name : galleries.keySet())
				forbiddenNames.add(name);
		}
		if(StringUtils.isNotBlank(filename))
			forbiddenNames.remove(filename);
		textTitle.addModifyListener(new FilenameSuggestorListener(dialogCreator, textName, forbiddenNames));
	}
	
	/* (non-Javadoc)
	 * @see de.thischwa.pmcms.gui.workspace.dialog.IDialogFieldsValidator#isValid()
	 */
	@Override
	public boolean isValid() {
		if (StringUtils.isBlank(textName.getText())) {
			dialogCreator.setErrorMessage(LabelHolder.get("dialog.pojo.page.error.name.notvalid")); //$NON-NLS-1$
			return false;
		} 
		if (existsName()) {
			dialogCreator.setErrorMessage(LabelHolder.get("dialog.pojo.page.error.name.exists")); //$NON-NLS-1$
			return false;
		}  
		if (spinnerThumbnailMaxWidth.getSelection() < 1) {
			dialogCreator.setErrorMessage(LabelHolder.get("dialog.pojo.gallery.error.thumbnail.width")); //$NON-NLS-1$
			return false;
		}
		if (!checkGalleryStuff()) {
			dialogCreator.setErrorMessage(LabelHolder.get("dialog.pojo.page.error.galleryexists")); //$NON-NLS-1$
			return false;
		}
		if (spinnerThumbnailMaxHeight.getSelection() < 1) {
			dialogCreator.setErrorMessage(LabelHolder.get("dialog.pojo.gallery.error.thumbnail.height")); //$NON-NLS-1$
			return false;
		}
		if (listTemplates.getSelectedTemplate() == null) {
			dialogCreator.setErrorMessage(LabelHolder.get("dialog.pojo.page.error.templatenotselect")); //$NON-NLS-1$
			return false;
		}
		if (listImageTemplates.getSelectedTemplate() == null) {
			dialogCreator.setErrorMessage(LabelHolder.get("dialog.pojo.gallery.error.notemplate")); //$NON-NLS-1$
			return false;
		}
		if (spinnerImageMaxWidth.getSelection() < 1) {
			dialogCreator.setErrorMessage(LabelHolder.get("dialog.pojo.gallery.error.image.width")); //$NON-NLS-1$
			return false;
		}
		if (spinnerImageMaxHeight.getSelection() < 1) {
			dialogCreator.setErrorMessage(LabelHolder.get("dialog.pojo.gallery.error.image.height")); //$NON-NLS-1$
			return false;
		}
		
		gallery.setName(textName.getText());
		gallery.setTitle(textTitle.getText());
		
		gallery.setThumbnailMaxWidth(spinnerThumbnailMaxWidth.getSelection());
		gallery.setThumbnailMaxHeight(spinnerThumbnailMaxHeight.getSelection());
		gallery.setImageTemplate(listImageTemplates.getSelectedTemplate());
		gallery.setTemplate(listTemplates.getSelectedTemplate());
		gallery.setImageMaxWidth(spinnerImageMaxWidth.getSelection());
		gallery.setImageMaxHeight(spinnerImageMaxHeight.getSelection());
		return true;
	}

	/**
	 * @return True, if the parent level contains a page with the same name, otherwise false.
	 */
	private boolean existsName() {
		if (gallery.getParent().getPages() == null)
			return false;
		for (Page otherPage : gallery.getParent().getPages()) {
			if (otherPage.getName().equals(textName.getText()) && !(otherPage.getId() == gallery.getId()))
				return true;
		}
		return false;
	}
	
	/**
	 * @return True, if page is a gallery and the naming convention is correct, otherwise false.
	 */
	private boolean checkGalleryStuff() {
		Map<String, Gallery> galleries = PoInfo.getGalleries(PoInfo.getSite(gallery));
		Gallery tmpGallery = galleries.get(textName.getText());
		return (tmpGallery == null 
				|| (tmpGallery.getName().equals(textName.getText()) && tmpGallery.getId() == gallery.getId()));
	}
}
