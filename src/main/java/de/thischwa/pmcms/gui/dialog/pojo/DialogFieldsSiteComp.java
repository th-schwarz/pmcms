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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.thischwa.pmcms.conf.resource.LabelHolder;
import de.thischwa.pmcms.model.domain.PoStructurTools;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.tool.connection.ConnectionFactory;

/**
 * Composite is part of {@link DialogCreator} and contains input fields and their validation method for {@link Site}.
 * 
 * @author Thilo Schwarz
 */
public class DialogFieldsSiteComp extends Composite implements IDialogFieldsValidator {
	private Text textUrl = null;
	private Text textTitle = null;
	private Text textServerUri = null;
	private Site site = null;
	private String oldSiteUrl = null;
	private DialogCreator dialogCreator = null;
	private Label label = null;

	public DialogFieldsSiteComp(Composite parent, int style, Site site) {
		super(parent, style);
		dialogCreator = (DialogCreator) parent;
		this.site = site;
		oldSiteUrl = site.getUrl();
		initialize();
	}

	private void initialize() {
		GridData gridDataLabel = new GridData();
		gridDataLabel.horizontalAlignment = GridData.END;
		gridDataLabel.verticalAlignment = GridData.CENTER;
		GridData gridDataText = new GridData();
		gridDataText.heightHint = -1;
		gridDataText.widthHint = 300;
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
		label.setText("*  ".concat(LabelHolder.get("dialog.pojo.site.fields.uri"))); //$NON-NLS-1$
		label.setLayoutData(gridDataLabel);
		textServerUri = new Text(this, SWT.BORDER);
		textServerUri.setTextLimit(256);
		textServerUri.setLayoutData(gridDataText);
		textServerUri.setText(StringUtils.defaultString(site.getProperty(Site.PROPKEY_SERVERURI)));
	}

	@Override
	public boolean isValid() {
		String serverUri = textServerUri.getText();
		if(!ConnectionFactory.isValid(serverUri)) {
			dialogCreator.setErrorMessage(LabelHolder.get("dialog.pojo.site.error.serveruri")); //$NON-NLS-1$
			return false;			
		}
		String url = StringUtils.deleteWhitespace(StringUtils.lowerCase(textUrl.getText()));
		if(!checkUrl(url)) {
			dialogCreator.setErrorMessage(LabelHolder.get("dialog.pojo.site.error.url")); //$NON-NLS-1$
			return false;
		}
		site.setUrl(url);
		if(StringUtils.isNotEmpty(textTitle.getText()))
			site.setTitle(textTitle.getText());

		// TODO verify the server-uri
		if(StringUtils.isNotEmpty(textServerUri.getText()))
			site.addProperty(Site.PROPKEY_SERVERURI, textServerUri.getText());
		return true;
	}

	private boolean checkUrl(String url) {
		if(StringUtils.isBlank(url))
			return false;
		if(StringUtils.equals(url, oldSiteUrl))
			return true;
		for(String siteUrl : PoStructurTools.getAllSites()) {
			if(siteUrl.equalsIgnoreCase(url))
				return false;
		}
		return true;
	}
}
