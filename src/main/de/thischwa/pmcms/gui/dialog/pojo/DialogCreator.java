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


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import de.thischwa.pmcms.conf.resource.LabelHolder;
import de.thischwa.pmcms.model.InstanceUtil;
import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.domain.pojo.Gallery;
import de.thischwa.pmcms.model.domain.pojo.Image;
import de.thischwa.pmcms.model.domain.pojo.Level;
import de.thischwa.pmcms.model.domain.pojo.Page;
import de.thischwa.pmcms.model.domain.pojo.Site;

/**
 * Composite of the new/edit dialog popup for all database pojos. Depending on the TYPE of the {@link IPoorMansObject}
 * the dialog will be constructed in the right way. There are three main composites:
 * <ul>
 * <li>{@link DialogHeaderComp}: shows information about the fields of the dialog.</li>
 * <li>CompositeDialogFields[Ipo]: contains all input fields and validation method for the {@link IPoorMansObject}.</li>
 * <li>{@link DialogFooterComp}: holds the ok and cancel button.</li>
 * </ul>
 * 
 * @author Thilo Schwarz
 */
public class DialogCreator extends Composite {
	private DialogHeaderComp headerComp = null;
	private Composite fieldsComp = null;
	private APoormansObject<?> po = null;
	private Label labelError = null;
	private boolean isCancel = false;

	public DialogCreator(Composite parent, int style, APoormansObject<?> po) {
		super(parent, style);
		this.po = po;
		initialize();
	}

	private void initialize() {
		if (InstanceUtil.isSite(po))
			getShell().setText(LabelHolder.get("dialog.pojo.site.title"));
		else if (InstanceUtil.isJustLevel(po))
			getShell().setText(LabelHolder.get("dialog.pojo.level.title"));
		else if (InstanceUtil.isGallery(po))
			getShell().setText(LabelHolder.get("dialog.pojo.gallery.title"));
		else if (InstanceUtil.isPage(po))
			getShell().setText(LabelHolder.get("dialog.pojo.page.title"));
		else if (InstanceUtil.isImage(po))
			getShell().setText(LabelHolder.get("dialog.pojo.image.title"));

		GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.makeColumnsEqualWidth = false;
		this.setLayout(gridLayout);

		headerComp = new DialogHeaderComp(this, SWT.NONE);
		String labelRequiredFields = LabelHolder.get("dialog.pojo.requiredfields");
		if (po instanceof Site) {
			headerComp.setTitle(LabelHolder.get("dialog.pojo.site.title"));
			headerComp.setDescription("* ".concat(labelRequiredFields)
					.concat("\n").concat(LabelHolder.get("dialog.pojo.site.description")));
			fieldsComp = new DialogFieldsSiteComp(this, SWT.NONE, (Site) po);
		} else if (InstanceUtil.isJustLevel(po)) {
			headerComp.setTitle(LabelHolder.get("dialog.pojo.level.title"));
			headerComp.setDescription("* ".concat(labelRequiredFields)
					.concat("\n").concat(LabelHolder.get("dialog.pojo.level.description")));
			fieldsComp = new DialogFieldsLevelComp(this, SWT.NONE, (Level) po);
		} else if (InstanceUtil.isGallery(po)) {
			headerComp.setTitle(LabelHolder.get("dialog.pojo.gallery.title"));
			headerComp.setDescription("* ".concat(labelRequiredFields)
					.concat("\n").concat(LabelHolder.get("dialog.pojo.gallery.description")));
			fieldsComp = new DialogFieldsGalleryComp(this, SWT.NONE, (Gallery) po);
		} else if (InstanceUtil.isPage(po)) {
			headerComp.setTitle(LabelHolder.get("dialog.pojo.page.title"));
			headerComp.setDescription("* ".concat(labelRequiredFields)
					.concat("\n").concat(LabelHolder.get("dialog.pojo.page.description")));
			fieldsComp = new DialogFieldsPageComp(this, SWT.NONE, (Page) po);
		} else if (InstanceUtil.isImage(po)) {
			headerComp.setTitle(LabelHolder.get("dialog.pojo.image.title"));
			headerComp.setDescription("* ".concat(labelRequiredFields));
			fieldsComp = new DialogFieldsImageComp(this, SWT.NONE, (Image) po);
			this.getShell().setMinimumSize(500, 0);
		}
		createCompositeError();
		new DialogFooterComp(this, SWT.NONE);
	}

	private void createCompositeError() {
		GridLayout gridLayoutCompositeError = new GridLayout();
		gridLayoutCompositeError.marginWidth = 20;
		GridData gridDataLabelError = new org.eclipse.swt.layout.GridData();
		gridDataLabelError.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridDataLabelError.grabExcessHorizontalSpace = true;
		gridDataLabelError.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		GridData gridDataCompositeError = new org.eclipse.swt.layout.GridData();
		gridDataCompositeError.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridDataCompositeError.grabExcessHorizontalSpace = true;
		gridDataCompositeError.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(gridDataCompositeError);
		composite.setLayout(gridLayoutCompositeError);
		labelError = new Label(composite, SWT.NONE);
		labelError.pack(true);
		labelError.setForeground(new Color(Display.getCurrent(), 255, 0, 0));
		labelError.setLayoutData(gridDataLabelError);
	}

	/**
	 * @return Returns the true, if dialog was canceled.
	 */
	public boolean isCancel() {
		return this.isCancel;
	}

	/**
	 * @param isCancel
	 *            The isCancel to set.
	 */
	public void setCancel(boolean isCancel) {
		this.isCancel = isCancel;
	}

	public void setErrorMessage(String msg) {
		labelError.setText(msg);
	}
	public void clearErrorMessage() {
		setErrorMessage("");
	}

	public IDialogFieldsValidator getCompositeFields() {
		return (IDialogFieldsValidator) fieldsComp;
	}
}
