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
package de.thischwa.pmcms.gui.dialog.pojo;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.thischwa.pmcms.conf.resource.LabelHolder;
import de.thischwa.pmcms.model.domain.PoInfo;
import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.domain.pojo.Page;
import de.thischwa.pmcms.model.domain.pojo.Template;
import de.thischwa.pmcms.tool.swt.FileNameVerifier;

/**
 * Composite is part of {@link DialogCreator} and contains input fields and their validation method for {@link Page}.
 *
 * @author Thilo Schwarz
 */
public class DialogFieldsPageComp extends Composite implements IDialogFieldsValidator {
	private Text textName = null;
	private Text textTitle = null;
	private Page page = null;
	private DialogCreator dialogCreator = null;
	private TemplateListViewer listTemplates = null;
	
	public DialogFieldsPageComp(final Composite parent, int style, final Page page) {
		super(parent, style);
		dialogCreator = (DialogCreator) parent;
		this.page = page;
		initialize();
	}

	
	private void initialize() {
		GridData gridDataList = new GridData();
		gridDataList.horizontalAlignment = GridData.FILL;
		gridDataList.grabExcessHorizontalSpace = true;
		gridDataList.heightHint = 60;
		gridDataList.verticalAlignment = GridData.CENTER;
		GridData gridDataLabel = new GridData();
		gridDataLabel.widthHint = 100;
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
		
		Label labelTemplate = new Label(this, SWT.RIGHT);
		labelTemplate.setText("*   ".concat(LabelHolder.get("dialog.pojo.page.fields.template"))); //$NON-NLS-1$ //$NON-NLS-2$
		labelTemplate.setLayoutData(gridDataLabel);
		listTemplates = new TemplateListViewer(this, gridDataList);
		java.util.List<Template> templates = PoInfo.getTemplates(PoInfo.getSite(page), page.getTemplateType());
		listTemplates.setInput(templates);
		listTemplates.setSelectedTemplate(page.getTemplate());
	}


	private void initializeTitleAndName() {
		String filename = StringUtils.defaultString(page.getName());
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
		textTitle.setText(StringUtils.defaultString(page.getTitle()));
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
		Collection<Page> sisters = page.getParent().getPages();
		if(CollectionUtils.isNotEmpty(sisters))
			for(Page page : sisters)
				forbiddenNames.add(page.getName());
		if(StringUtils.isNotBlank(filename))
			forbiddenNames.remove(filename);
		if(page.getId() == APoormansObject.UNSET_VALUE) // suggestion of the file name should work just with new  objects
			textTitle.addModifyListener(new FilenameSuggestorListener(dialogCreator, textName, forbiddenNames));
	}


	/* (non-Javadoc)
	 * @see de.thischwa.pmcms.gui.workspace.dialog.IValidator#isValid()
	 */
	@Override
	public boolean isValid() {
		if (StringUtils.isBlank(textName.getText())) {
			dialogCreator.setErrorMessage(LabelHolder.get("dialog.pojo.page.error.name.notvalid")); //$NON-NLS-1$
			return false;
		} else if (existsName()) {
			dialogCreator.setErrorMessage(LabelHolder.get("dialog.pojo.page.error.name.exists")); //$NON-NLS-1$
			return false;
		}  
		if (listTemplates.getSelectedTemplate() == null) {
			dialogCreator.setErrorMessage(LabelHolder.get("dialog.pojo.page.error.templatenotselect")); //$NON-NLS-1$
			return false;
		}
		page.setName(textName.getText());
		page.setTemplate(listTemplates.getSelectedTemplate());
		page.setTitle(textTitle.getText());
		return true;
	}
	
	/**
	 * @return True, if the parent level contains a page with the same name, otherwise false.
	 */
	private boolean existsName() {
		if (CollectionUtils.isEmpty(page.getParent().getPages()))
			return false; 
		for (Page otherPage : page.getParent().getPages()) {
			if (otherPage.getName().equals(textName.getText()) && !(otherPage.getId() == page.getId()))
				return true;
		}
		return false;
	}
}
