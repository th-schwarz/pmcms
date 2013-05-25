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
package de.thischwa.pmcms.gui.dialog;

import java.util.Map;


import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.configuration.PropertiesManager;
import de.thischwa.pmcms.gui.BrowserManager;
import de.thischwa.pmcms.tool.ToolVersionInfo;
import de.thischwa.pmcms.tool.swt.SWTUtils;

/**
 * Dialog to display basic properties and 3rd-party library info.
 *
 * @version $Id: InfoDialog.java 2218 2012-07-27 09:12:33Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class InfoDialog extends SimpleDialog {

	public InfoDialog(Shell parentShell) {
		super(parentShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}

	@Override
	protected void init() {
		shell.setText("Info");
		new InfoComp(shell, SWT.NONE);
		labelOfCloseButton = "ok";
		SWTUtils.center(shell, parentShell.getBounds());
	}

	
	private class InfoComp extends Composite {
		private Composite composite3rdParty = null;
		private Composite compositeProperties = null;

		public InfoComp(final Composite parent, int style) {
			super(parent, style);
			PropertiesManager pm = InitializationManager.getBean(PropertiesManager.class);
			initialize();
			add3rdPartyTool(ToolVersionInfo.getJava());
			add3rdPartyTool(ToolVersionInfo.getSpring());
			add3rdPartyTool(ToolVersionInfo.getDom4J());
			add3rdPartyTool(ToolVersionInfo.getVelocity());
			add3rdPartyTool(ToolVersionInfo.getCKEditor());
			add3rdPartyTool(ToolVersionInfo.getC5Connector());
			add3rdPartyTool(ToolVersionInfo.getJII());
			add3rdPartyTool(ToolVersionInfo.getSwt());
			
			addProperty("OS arch", System.getProperty("os.arch"));
			addProperty("Directory", Constants.APPLICATION_DIR.getAbsolutePath());
			addProperty("Data Directory", InitializationManager.getDataDir().getAbsolutePath());
			addProperty("Temp Directory", Constants.TEMP_DIR.getAbsolutePath());
			addProperty("Backup Directory", pm.getProperty("pmcms.dir.backup"));
			addProperty("Host", pm.getProperty("pmcms.jetty.host"));
			addProperty("Port", pm.getProperty("pmcms.jetty.port"));
			addProperty("ImageMagick", StringUtils.defaultString(pm.getProperty("imagemagick.convert.command"), "not set"));
			addProperty("Browser", BrowserManager.getBrowserType());
		}

		private void initialize() {
			createCompositeHeader();
			createCompositeMain();
			setLayout(new GridLayout());
		}

		/**
		 * This method initializes compositeHeader	
		 */
		private void createCompositeHeader() {
			GridData gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			gridData.grabExcessHorizontalSpace = true;
			gridData.verticalAlignment = GridData.CENTER;
			Composite compositeHeader = new Composite(this, SWT.BORDER);
			compositeHeader.setLayoutData(gridData);
			FillLayout fillLayoutHeader = new FillLayout();
			fillLayoutHeader.type = org.eclipse.swt.SWT.VERTICAL;
			fillLayoutHeader.marginWidth = 10;
			fillLayoutHeader.marginHeight = 10;
			fillLayoutHeader.spacing = 10;
			compositeHeader.setLayout(fillLayoutHeader);
			
			PropertiesManager pm = InitializationManager.getBean(PropertiesManager.class);
			Label labelHeaderTitle = new Label(compositeHeader, SWT.CENTER);
			labelHeaderTitle.setText(pm.getProperty("pmcms.title") + " - Version " + pm.getProperty("pmcms.version"));
			SWTUtils.changeFontStyle(labelHeaderTitle, SWT.BOLD);
			SWTUtils.changeFontSizeRelativ(labelHeaderTitle, 2);
			Label labelHeaderSubTitle = new Label(compositeHeader, SWT.CENTER);
			labelHeaderSubTitle.setText("A very basic CMS running as a swt application and generating static html pages.");
			Label labelHeaderText = new Label(compositeHeader, SWT.NONE);
			labelHeaderText.setText("Concept and implementation by Thilo Schwarz (th-schwarz@users.sourceforge.net).\n" +
					"Copyright by Thilo Schwarz");
		}

		/**
		 * This method initializes compositeMain	
		 */
		private void createCompositeMain() {
			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 2;
			gridLayout.marginWidth = 0;
			gridLayout.horizontalSpacing = 10;
			gridLayout.marginHeight = 0;
			gridLayout.marginTop = 10;
			GridData gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			gridData.verticalAlignment = GridData.CENTER;
			Composite compositeMain = new Composite(this, SWT.NONE);
			createCompositeVersionsMain(compositeMain);
			compositeMain.setLayout(gridLayout);
			createCompositePropertiesMain(compositeMain);
			compositeMain.setLayoutData(gridData);
		}

		/**
		 * This method initializes compositeMainVersions	
		 */
		private void createCompositeVersionsMain(Composite parent) {
			GridData gridData = new GridData();
			gridData.horizontalAlignment = SWT.FILL;
			gridData.verticalAlignment = SWT.FILL;
			gridData.grabExcessHorizontalSpace = true;
			Composite compositeVersionsMain = new Composite(parent, SWT.BORDER);
			compositeVersionsMain.setLayout(new GridLayout());
			compositeVersionsMain.setLayoutData(gridData);
			
			Label labelHeader = new Label(compositeVersionsMain, SWT.NONE);
			SWTUtils.changeFontStyle(labelHeader, SWT.BOLD);
			labelHeader.setText("Versions of basic 3rd party tools:");
			
			createComposite3rdParty(compositeVersionsMain);
		}
		
		private void createComposite3rdParty(Composite parent) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 2;
			gridLayout.marginWidth = 0;
			gridLayout.horizontalSpacing = 10;
			composite3rdParty = new Composite(parent, SWT.NONE);
			composite3rdParty.setLayout(gridLayout);
		}
		
		/**
		 * This method initializes compositePropertiesMain	
		 */
		private void createCompositePropertiesMain(Composite parent) {
			GridData gridData = new GridData();
			gridData.horizontalAlignment = SWT.FILL;
			gridData.verticalAlignment = SWT.FILL;
			gridData.grabExcessHorizontalSpace = true;
			Composite compositePropertiesMain = new Composite(parent, SWT.BORDER);
			compositePropertiesMain.setLayout(new GridLayout());
			compositePropertiesMain.setLayoutData(gridData);
			
			Label labelHeader = new Label(compositePropertiesMain, SWT.NONE);
			SWTUtils.changeFontStyle(labelHeader, SWT.BOLD);
			labelHeader.setText("Properties:");
			
			createCompositeProperties(compositePropertiesMain);
		}

		private void createCompositeProperties(Composite parent) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 2;
			gridLayout.marginWidth = 0;
			gridLayout.horizontalSpacing = 10;
			compositeProperties = new Composite(parent, SWT.NONE);
			compositeProperties.setLayout(gridLayout);
		}
		
		private void add3rdPartyTool(Map<ToolVersionInfo.TYPE, String> info) {
			addItem(composite3rdParty, info.get(ToolVersionInfo.TYPE.title), info.get(ToolVersionInfo.TYPE.version));
		}
		
		private void addProperty(String labelText, String version) {
			addItem(compositeProperties, labelText, version);
		}
		
		private void addItem(Composite parent, String labelText, String version) {
			addItem(parent, labelText, version, null);
		}
		
		private void addItem(Composite parent, String labelText, String version, String tooltip) {
			Label labelLabel = new Label(parent, SWT.NONE);
			labelLabel.setText(StringUtils.defaultString(labelText, "n/a"));
			Label labelVersion = new Label(parent, SWT.NONE);
			labelVersion.setText(StringUtils.defaultString(version, "n/a"));
			if (StringUtils.isNotBlank(tooltip))
				labelVersion.setToolTipText(tooltip);
		}
	}

}
