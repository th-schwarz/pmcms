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
package de.thischwa.pmcms.gui;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.CloseWindowListener;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.browser.VisibilityWindowListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.configuration.resource.ImageHolder;
import de.thischwa.pmcms.exception.RenderingException;
import de.thischwa.pmcms.gui.treeview.TreeViewManager;
import de.thischwa.pmcms.livecycle.SiteHolder;
import de.thischwa.pmcms.model.IRenderable;
import de.thischwa.pmcms.model.InstanceUtil;
import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.domain.pojo.ASiteResource;
import de.thischwa.pmcms.model.tool.SitePersister;
import de.thischwa.pmcms.server.Action;
import de.thischwa.pmcms.server.ContextUtil;
import de.thischwa.pmcms.tool.Link;
import de.thischwa.pmcms.tool.swt.SWTUtils;
import de.thischwa.pmcms.view.ViewMode;
import de.thischwa.pmcms.view.renderer.VelocityRenderer;

/**
 * Manage the browser component.
 * 
 * @version $Id: BrowserManager.java 2213 2012-06-30 12:01:07Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
@Component
public class BrowserManager {
	private static Logger logger = Logger.getLogger(BrowserManager.class);
	private Browser mainBrowser = null;
	private static String browserType = "n/a";
	
	@Autowired private VelocityRenderer velocityRenderer;
	@Autowired private TreeViewManager treeViewManager;
	@Autowired private MainWindow mainWindow;
	
	@Autowired private SiteHolder siteHolder;

	private boolean isXulRunnerEnabled;
	
	private Shell parentShell;
	
	@Value("${pmcms.xulrunner}")
	private String xulRunnerProp;
	
	@Value("${baseurl}")
	private String baseUrl;
	
	public void init(final Composite parent) {
		logger.debug("Entered init!");
		this.isXulRunnerEnabled = (xulRunnerProp != null && Boolean.parseBoolean(xulRunnerProp));
		mainBrowser = getBrowser(parent);
		parentShell = parent.getShell();
		initializeBrowser(parentShell, parent.getDisplay(), mainBrowser);
	}

	private void initializeBrowser(final Shell shell, final Display display, final Browser browser) {
		final String urlToIgnore = "about:blank";
		final TreeViewManager treeViewManager = this.treeViewManager;
	
		browser.addLocationListener(new LocationListener() {
			@Override
			public void changing(LocationEvent event) {
				if (!event.location.equals(urlToIgnore)) 
					logger.debug("url is changing to: " + event.location);
			}
			
			@Override
			public void changed(LocationEvent event) {
				if (!event.location.equals(urlToIgnore)) {
					String url = event.location;
					logger.debug("url has changed to: " + url);
					Link link = InitializationManager.getBean(Link.class);
					link.init(event.location);
					if (!link.isPoormansRequest())
						return;
					String idParam = link.getParameter("id");
					if(idParam != null && idParam.equals(APoormansObject.UNSET_VALUE+""))
						idParam = null;
					APoormansObject<?> po = StringUtils.isBlank(idParam) ? null : ContextUtil.getpo(link);
					// synchronizing with the tree view
					treeViewManager.fillAndExpands(po);
					
					// persist the site, if a poormans object was saved	 
					if(url.endsWith(Constants.LINK_IDENTICATOR_SAVE)) {
						try {
							SitePersister.write(siteHolder.getSite());
						} catch (IOException e) {
							show(e);
						}
					}
				}
			}
		});

		// visibility handling
		browser.addVisibilityWindowListener(new VisibilityWindowListener() {
			@Override
			public void hide(WindowEvent event) {
				Browser browser = (Browser) event.widget;
				Shell shell = browser.getShell();
				shell.setVisible(false);
			}

			@Override
			public void show(WindowEvent event) {
				Browser browser = (Browser) event.widget;
				final Shell shell = browser.getShell();
				if (event.location != null)
					shell.setLocation(event.location);
				if (event.size != null) {
					Point size = event.size;
					shell.setSize(shell.computeSize(size.x, size.y));
				}
				shell.open();
			}
		});

		// setting title of the shell
		browser.addTitleListener(new org.eclipse.swt.browser.TitleListener() {
			@Override
			public void changed(org.eclipse.swt.browser.TitleEvent event) {
				final Browser tmpBrowser = (Browser)event.getSource();
				if (mainBrowser.equals(tmpBrowser) && StringUtils.isNotBlank(event.title)) {
					mainWindow.setTitle(event.title);
				}
			}
		});

		// BEGIN popup handling
		browser.addOpenWindowListener(new OpenWindowListener() {
			@Override
			public void open(WindowEvent event) {
				Shell shell = new Shell(display, SWT.APPLICATION_MODAL | SWT.CLOSE);
				shell.setImages(new Image[] { ImageHolder.SHELL_ICON_SMALL, ImageHolder.SHELL_ICON_BIG });
				shell.setLayout(new FillLayout());
				SWTUtils.center(shell, parentShell.getBounds());
				Browser browser = getBrowser(shell);
				initializeBrowser(shell, display, browser);
				final Browser tempBrowser = browser;
				
				browser.addTitleListener(new TitleListener() { // set title for the popup
					@Override
					public void changed(TitleEvent event) {
						tempBrowser.getShell().setText(event.title);
					}
				});
				
				browser.addVisibilityWindowListener(new VisibilityWindowListener() {
					@Override
					public void hide(WindowEvent event) {
						Browser browser = (Browser) event.widget;
						Shell shell = browser.getShell();
						shell.setVisible(false);
					}
					
					@Override
					public void show(WindowEvent event) {
						final Browser browser = (Browser) event.widget;
						Shell shell = browser.getShell();
						if (event.location != null)
							shell.setLocation(event.location);
						if (event.size != null) {
							Point size = event.size;
							// hack for bug with too big of safari popups
							if (StringUtils.containsIgnoreCase(browserType, "webkit") && (size.x > parentShell.getSize().x || size.y > parentShell.getSize().y)) {
								size.x = parentShell.getSize().x - 50;
								size.y = parentShell.getSize().y - 50;
								shell.setLocation(parentShell.getLocation().x+15, parentShell.getLocation().y+15);
								shell.setSize(shell.computeSize(size.x, size.y));
							} else
								shell.setSize(shell.computeSize(size.x, size.y));
						}
						shell.open();
					}
				});
				event.browser = browser;
			}
		});
		// END popup handling
		
		browser.addCloseWindowListener(new CloseWindowListener() {
			@Override
			public void close(WindowEvent event) {
				Browser browser = (Browser) event.widget;
				Shell shell = browser.getShell();
				shell.close();
			}
		});
	}

	/**
	 * Wrapper for setUrl of the SWT browser.
	 */
	public void setUrl(String url) {
		mainBrowser.setUrl(url);
	}

	public void view(final APoormansObject<?> po, ViewMode viewMode) {
		logger.debug(String.format("Show %s(%d), %s", po, po.getId(), viewMode));
		try {
			if(viewMode == ViewMode.EDIT) {
				String url = Link.buildUrl(baseUrl, po, Action.EDIT);
				mainBrowser.setUrl(url);
			} else { // PREVIEW
				String html;
				if(InstanceUtil.isRenderable(po))
					html = velocityRenderer.render((IRenderable)po, viewMode);
				else if(InstanceUtil.isSiteResource(po))
					html = velocityRenderer.render((ASiteResource)po);
				else {
					logger.warn(String.format("Don't know how to render [%s] in mode %s!", po, viewMode.toString()));
					return;
				}
				mainBrowser.setText(html);
				//System.out.println(html);
			}
		} catch (RenderingException e) {
			show(e);
		}
	}

	public void show(Throwable e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		show(sw.toString());
	}
	
	public void show(String msg) {
		mainBrowser.setText(String.format("<h1 style=\"color: red;\">%s</h1>", msg));
	}

	public void showHelp() {
		mainBrowser.setUrl(baseUrl + "help/index.html");
	}

	/**
	 * Simple factory method to initialize the browser with respect of the property 'pmcms.xulrunner'. If this property is
	 * <code>true</code> a mozilla browser will be initialized. (Depending on the existence of the directory 'xulrunner' inside the 
	 * application directory, the required system property is set.) Otherwise the swt standard browser of the underlying OS.
	 * 
	 * @param parent Parent component of the browser.
	 * @return Initialized browser.
	 */
	private Browser getBrowser(final Composite parent) {
		Browser browser;
		if (isXulRunnerEnabled) {
			logger.info("XULRUNNER property is set, trying to get a mozilla browser!");
			File xulDir = new File(Constants.APPLICATION_DIR, "xulrunner");
			if (xulDir.exists()) {
				logger.info("Found local XULRUNNER. Set required system property.");
				System.setProperty("org.eclipse.swt.browser.XULRunnerPath", xulDir.getAbsolutePath());
			}
			browser = new Browser(parent, SWT.MOZILLA);
		} else
			browser = new Browser(parent, SWT.NONE);
		browserType = browser.getBrowserType();
		logger.info("Constructed browser: " + browserType);
		return browser;
	}
	
	public static String getBrowserType() {
		return browserType;
	}
}