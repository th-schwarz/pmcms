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
package de.thischwa.pmcms.gui.listener;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import de.thischwa.pmcms.conf.InitializationManager;
import de.thischwa.pmcms.conf.PropertiesManager;
import de.thischwa.pmcms.exception.FatalException;
import de.thischwa.pmcms.gui.BrowserManager;
import de.thischwa.pmcms.gui.WorkspaceToolBarManager;
import de.thischwa.pmcms.gui.dialog.DialogManager;
import de.thischwa.pmcms.gui.treeview.TreeViewManager;
import de.thischwa.pmcms.livecycle.SiteHolder;
import de.thischwa.pmcms.model.domain.PoPathInfo;
import de.thischwa.pmcms.model.domain.pojo.Macro;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.model.domain.pojo.Template;
import de.thischwa.pmcms.model.domain.pojo.TemplateType;
import de.thischwa.pmcms.tool.file.FileTool;

/**
 * Listener for adding a new {@link Site}.
 * 
 * @author Thilo Schwarz
 */
public class ListenerAddSite implements SelectionListener {
	private static Logger logger = Logger.getLogger(ListenerAddSite.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	// TODO rewrite: take a 'default' backup
	@Override
	public void widgetSelected(SelectionEvent e) {
		logger.debug("SEL add site");
		Site site = new Site();
		if (DialogManager.startDialogPersitentPojo(e.display.getActiveShell(), site)) {
			File defaultResourceDir = new File(InitializationManager.getBean(PropertiesManager.class).getProperty("pmcms.dir.defaultresources"));
			File srcDir = new File(defaultResourceDir.getAbsoluteFile(), "sites");
			File srcConfigDir = new File(srcDir, "configuration");
			File destDir = PoPathInfo.getSiteDirectory(site);
			File destConfigDir = new File(destDir, "configuration");
			destConfigDir.mkdirs();
			try {
				// copy required files
				FileUtils.copyFileToDirectory(new File(srcDir, "format.css"), destDir);
				FileUtils.copyFileToDirectory(new File(srcConfigDir, "fckconfig.js"), destConfigDir);
				FileUtils.copyFileToDirectory(new File(srcConfigDir, "fckstyles.xml"), destConfigDir);

				// read the templates
				site.add(buildTemplate(srcDir, "layout.html", site, null));
				File srcTemplatedir = new File(srcDir, "templates");
				site.add(buildTemplate(srcTemplatedir, "gallery.html", site, TemplateType.GALLERY));
				site.add(buildTemplate(srcTemplatedir, "image.html", site, TemplateType.IMAGE));
				site.add(buildTemplate(srcTemplatedir, "page.html", site, TemplateType.PAGE));

				// read the macro
				Macro macro = new Macro();
				macro.setParent(site);
				macro.setName("user_menu.vm");
				macro.setText(FileTool.toString(new File(srcConfigDir, "user_menu.vm")));
			} catch (IOException e1) {
				throw new FatalException("While construct the default file structure of a site: " + e1.getMessage(), e1);
			}

			SiteHolder siteHolder = InitializationManager.getBean(SiteHolder.class);
			siteHolder.setSite(site);
			TreeViewManager treeViewManager = InitializationManager.getBean(TreeViewManager.class);
			treeViewManager.fillAndExpands(site);
			BrowserManager browserManager = InitializationManager.getBean(BrowserManager.class);
			browserManager.showHelp();

			WorkspaceToolBarManager.actionAfterSiteRenamed(site);
		}
	}

	private Template buildTemplate(File templatedir, String templateName, Site site, TemplateType type) throws IOException {
		File templateFile = new File(templatedir, templateName);
		Template template = new Template();
		template.setParent(site);
		template.setName(templateName);
		template.setType(type);
		template.setText(FileTool.toString(templateFile));
		return template;
	}
}
