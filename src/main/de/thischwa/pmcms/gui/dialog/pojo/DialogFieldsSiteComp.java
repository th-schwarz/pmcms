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


import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.configuration.PropertiesManager;
import de.thischwa.pmcms.configuration.resource.LabelHolder;
import de.thischwa.pmcms.model.domain.PoStructurTools;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.tool.DESCryptor;

/**
 * Composite is part of {@link DialogCreator} and contains input fields and their validation method for {@link Site}.
 * 
 * @version $Id: DialogFieldsSiteComp.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class DialogFieldsSiteComp extends Composite implements IDialogFieldsValidator {
	private Text textUrl = null;
	private Text textTitle = null;
	private Text textHost = null;
	private Text textLoginUser = null;
	private Text textLoginPassword = null;
	private Text textStartDirectory = null;
	private Site site = null;
	private String oldSiteUrl = null;
	private DialogCreator dialogCreator = null;
	private Label label = null;
	private DESCryptor cryptor;

	public DialogFieldsSiteComp(Composite parent, int style, Site site) {
		super(parent, style);
		dialogCreator = (DialogCreator) parent;
		this.site = site;
		oldSiteUrl = site.getUrl();
		cryptor = new DESCryptor(InitializationManager.getBean(PropertiesManager.class).getProperty("pmcms.crypt.key"));
		initialize();
	}

	private void initialize() {
		GridData gridDataLabel = new GridData();
		gridDataLabel.horizontalAlignment = GridData.END;
		gridDataLabel.verticalAlignment = GridData.CENTER;
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
		Label label1 = new Label(this, SWT.RIGHT);
		label1.setText("*  ".concat(LabelHolder.get("dialog.pojo.site.fields.url"))); //$NON-NLS-1$
		label1.setLayoutData(gridDataLabel);
		textUrl = new Text(this, SWT.BORDER);
		textUrl.setTextLimit(256);
		textUrl.setLayoutData(gridDataText);
		textUrl.setText(StringUtils.defaultString(site.getUrl()));
		Label label2 = new Label(this, SWT.RIGHT);
		label2.setText(LabelHolder.get("dialog.pojo.site.fields.title")); //$NON-NLS-1$
		label2.setLayoutData(gridDataLabel);
		textTitle = new Text(this, SWT.BORDER);
		textTitle.setTextLimit(256);
		textTitle.setLayoutData(gridDataText);
		textTitle.setText(StringUtils.defaultString(site.getTitle()));

		label = new Label(this, SWT.NONE);
		label.setText(LabelHolder.get("dialog.pojo.site.fields.ftp.host")); //$NON-NLS-1$
		label.setLayoutData(gridDataLabel);
		textHost = new Text(this, SWT.BORDER);
		textHost.setTextLimit(256);
		textHost.setLayoutData(gridDataText);
		textHost.setText(StringUtils.defaultString(site.getTransferHost()));
		label = new Label(this, SWT.NONE);
		label.setText(LabelHolder.get("dialog.pojo.site.fields.ftp.user")); //$NON-NLS-1$
		label.setLayoutData(gridDataLabel);
		textLoginUser = new Text(this, SWT.BORDER);
		textLoginUser.setTextLimit(256);
		textLoginUser.setLayoutData(gridDataText);
		textLoginUser.setText(StringUtils.defaultString(site.getTransferLoginUser()));
		label = new Label(this, SWT.NONE);
		label.setText(LabelHolder.get("dialog.pojo.site.fields.ftp.password")); //$NON-NLS-1$
		label.setLayoutData(gridDataLabel);
		textLoginPassword = new Text(this, SWT.BORDER);
		textLoginPassword.setTextLimit(256);
		textLoginPassword.setLayoutData(gridDataText);
		String plainPwd = cryptor.decrypt(site.getTransferLoginPassword());
		textLoginPassword.setText(StringUtils.defaultString(plainPwd));

		label = new Label(this, SWT.NONE);
		label.setText(LabelHolder.get("dialog.pojo.site.fields.ftp.startdir")); //$NON-NLS-1$
		label.setLayoutData(gridDataLabel);
		textStartDirectory = new Text(this, SWT.BORDER);
		textStartDirectory.setTextLimit(256);
		textStartDirectory.setLayoutData(gridDataText);
		textStartDirectory.setText(StringUtils.defaultString(site.getTransferStartDirectory()));
	}

	@Override
	public boolean isValid() {
		String url = StringUtils.deleteWhitespace(StringUtils.lowerCase(textUrl.getText()));
		if (!checkUrl(url)) {
			dialogCreator.setErrorMessage(LabelHolder.get("dialog.pojo.site.error.url")); //$NON-NLS-1$
			return false;
		}
		site.setUrl(url);
		if (StringUtils.isNotEmpty(textTitle.getText()))
			site.setTitle(textTitle.getText());
		site.setTransferHost(textHost.getText());
		site.setTransferLoginUser(textLoginUser.getText());
		site.setTransferLoginPassword(cryptor.encrypt(textLoginPassword.getText()));
		site.setTransferStartDirectory(textStartDirectory.getText());
		return true;
	}

	private boolean checkUrl(String url) {
		if(StringUtils.isBlank(url))
			return false;
		if(StringUtils.equals(url, oldSiteUrl))
			return true;
		for (String siteUrl : PoStructurTools.getAllSites()) {
			if (siteUrl.equalsIgnoreCase(url))
				return false;
		}
		return true;
	}
}
