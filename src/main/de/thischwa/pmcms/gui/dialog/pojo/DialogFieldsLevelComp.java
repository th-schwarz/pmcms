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

import de.thischwa.pmcms.configuration.resource.LabelHolder;
import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.domain.pojo.Level;
import de.thischwa.pmcms.tool.swt.FileNameVerifier;

/**
 * Composite is part of {@link DialogCreator} and contains input fields and their validation method for {@link Level}.
 * 
 * @version $Id: DialogFieldsLevelComp.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class DialogFieldsLevelComp extends Composite implements IDialogFieldsValidator {
	private Text textName = null;
	private Text textTitle = null;
	private Level level = null;
	private DialogCreator dialogCreator = null;

	public DialogFieldsLevelComp(final Composite parent, int style, Level level) {
		super(parent, style);
		dialogCreator = (DialogCreator) parent;
		this.level = level;
		initialize();
	}

	private void initialize() {
		String filename = StringUtils.defaultString(level.getName());
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
		Label labelTitle = new Label(this, SWT.RIGHT);
		labelTitle.setText("*   ".concat(LabelHolder.get("dialog.pojo.level.fields.title"))); //$NON-NLS-1$
		labelTitle.setLayoutData(gridDataLabel);
		textTitle = new Text(this, SWT.BORDER);
		textTitle.setTextLimit(256);
		textTitle.setLayoutData(gridDataText);
		textTitle.setText(StringUtils.defaultString(level.getTitle()));
		Label labelName = new Label(this, SWT.RIGHT);
		labelName.setText("*   ".concat(LabelHolder.get("dialog.pojo.level.fields.name"))); //$NON-NLS-1$
		labelName.setLayoutData(gridDataLabel);
		textName = new Text(this, SWT.BORDER);
		textName.setTextLimit(256);
		textName.setLayoutData(gridDataText);
		textName.setText(filename);
		textName.addVerifyListener(new FileNameVerifier());
		textName.addModifyListener(new ModifyListenerClearErrorMessages(dialogCreator));
		
		Collection<String> forbiddenNames = new ArrayList<String>();
		Collection<Level> sisters = level.getParent().getSublevels();
		if (CollectionUtils.isNotEmpty(sisters))
			for (Level otherLevel : sisters) 
				forbiddenNames.add(otherLevel.getName());
		if(StringUtils.isNotBlank(filename))
			forbiddenNames.remove(filename);
		if(level.getId() == APoormansObject.UNSET_VALUE) // suggestion of the file name should work just with new  objects
			textTitle.addModifyListener(new FilenameSuggestorListener(dialogCreator, textName, forbiddenNames));
	}

	/*
	 * (non-Javadoc)
	 * @see de.thischwa.pmcms.gui.workspace.dialog.IValidator#isValid()
	 */
	@Override
	public boolean isValid() {
		if (StringUtils.isBlank(textName.getText())) {
			dialogCreator.setErrorMessage(LabelHolder.get("dialog.pojo.level.error.name.notvalid")); //$NON-NLS-1$
			return false;
		} else if (existsName()) {
			dialogCreator.setErrorMessage(LabelHolder.get("dialog.pojo.level.error.name.exists")); //$NON-NLS-1$
			return false;
		}
		if (StringUtils.isBlank(textTitle.getText())) {
			dialogCreator.setErrorMessage(LabelHolder.get("dialog.pojo.level.error.title")); //$NON-NLS-1$
			return false;
		}
		level.setName(textName.getText());
		level.setTitle(textTitle.getText());
		return true;
	}
	
	private boolean existsName() {
		Collection<Level> sisters = level.getParent().getSublevels();
		if (CollectionUtils.isEmpty(sisters))
			return false;
		for (Level otherLevel : sisters) {
			if (otherLevel.getName().equals(textName.getText()) && !(otherLevel.getId() == level.getId()))
				return true;
		}
		return false;
	}
}
