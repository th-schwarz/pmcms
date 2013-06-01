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
package de.thischwa.pmcms.server;

import java.io.File;

import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import de.thischwa.c5c.ConnectorServlet;
import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.configuration.AInitializingTask;
import de.thischwa.pmcms.configuration.IApplicationLiveCycleListener;

/**
 * Configure and start of Jetty.
 */
@Service("jettyLauncher")
public class JettyLauncher extends AInitializingTask implements IApplicationLiveCycleListener {

	/** Local reference of the jetty server. */
	private static Server server = null;

	@Value("${data.dir}")
	private String dataDirStr;
	
	@Value("${pmcms.dir.sites}")
	private String sitesDir;
	
	@Value("${pmcms.jetty.host}")
	private String host;

	@Value("${pmcms.jetty.port}")
	private String port;
	
	@Override
	public void onApplicationStart() {
		File dataDir = new File(dataDirStr);
		String applicationPath = Constants.APPLICATION_DIR.getAbsolutePath().replace('\\', '/');
		if(!applicationPath.endsWith("/"))
			applicationPath += "/";
		try {
			ClassLoader classLoader = ClassUtils.getDefaultClassLoader(); // spring class loader
			server = new Server();
			Connector connector = new SelectChannelConnector();
			connector.setHost(host);
			connector.setPort(Integer.parseInt(port));
			connector.setMaxIdleTime(Integer.MAX_VALUE);
			connector.setLowResourceMaxIdleTime(Integer.MAX_VALUE);
			server.setConnectors(new Connector[] { connector });

			ServletContextHandler contextDataDir = new ServletContextHandler(server, "/", ServletContextHandler.NO_SECURITY);
			contextDataDir.setClassLoader(classLoader);
			contextDataDir.setResourceBase(dataDir.getAbsolutePath());

			ServletHolder holderDefaults = new ServletHolder(ResourceServlet.class);
			holderDefaults.setInitParameter("basePath", "defaults");
			holderDefaults.setInitParameter("aliases", "false");
			contextDataDir.addServlet(holderDefaults, "/defaults/*");

			ServletHolder holderHelp = new ServletHolder(ResourceServlet.class);
			holderHelp.setInitParameter("basePath", "help");
			holderHelp.setInitParameter("aliases", "false");
			contextDataDir.addServlet(holderHelp, "/help/*");
			
			ServletHolder holderFilemanger = new ServletHolder(ResourceServlet.class);
			holderFilemanger.setInitParameter("basePath", new File(Constants.APPLICATION_DIR, "filemanager").getAbsolutePath());
			contextDataDir.addServlet(holderFilemanger, "/filemanager/*");
			
			contextDataDir.addServlet(buildLoadOnStart(EditorServlet.class), "/" + Constants.LINK_IDENTICATOR_EDIT + "/*");
			contextDataDir.addServlet(buildLoadOnStart(ContentSaverServlet.class), "/" + Constants.LINK_IDENTICATOR_SAVE + "/*");
			contextDataDir.addServlet(buildLoadOnStart(PreviewServlet.class), "/" + Constants.LINK_IDENTICATOR_PREVIEW + "/*");
			
			ServletHolder holderConnector = new ServletHolder(ConnectorServlet.class);
			holderConnector.setInitOrder(1);
			holderConnector.getRegistration().setMultipartConfig(
					new MultipartConfigElement(Constants.TEMP_DIR.getAbsolutePath()));
			contextDataDir.addServlet(holderConnector, "/filemanager/connectors/java/*");
			
			ServletHolder holderCodeMirror = new ServletHolder(ZipProxyServlet.class);
			holderCodeMirror.setInitParameter("file", "sourceeditor/CodeMirror-2013-02-09.zip");
			holderCodeMirror.setInitParameter("zipPathToSkip", "CodeMirror-master");
			contextDataDir.addServlet(holderCodeMirror, "/codemirror/*");

			ServletHolder holderSiteResource = new ServletHolder(SiteResourceServlet.class);
			holderSiteResource.setInitParameter("basePath", new File(dataDir, sitesDir).getAbsolutePath());
			contextDataDir.addServlet(holderSiteResource, String.format("/%s/*", Constants.LINK_IDENTICATOR_SITE_RESOURCE));

			ServletHolder holderCKEditor = new ServletHolder(ZipProxyServlet.class);
			holderCKEditor.setInitParameter("file", "ckeditor_4.0.2_standard.zip");
			holderCKEditor.setInitParameter("zipPathToSkip", "ckeditor");
			contextDataDir.addServlet(holderCKEditor, "/ckeditor/*");
			
			/*ServletHolder holderFilemanager = new ServletHolder(ZipProxyServlet.class);
			holderFilemanager.setInitParameter("file", "Filemanager.zip");
			holderFilemanager.setInitParameter("zipPathToSkip", "Filemanager-master");
			context.addServlet(holderFilemanager, "/filemanager/*");*/
			
			ServletHolder holderTest = new ServletHolder(TestServlet.class);
			contextDataDir.addServlet(holderTest, "/webgui/*");

			ServletHolder holderResourceWebgui = new ServletHolder(ResourceServlet.class);
			holderResourceWebgui.setInitParameter("basePath", Constants.APPLICATION_DIR.getAbsolutePath() + "/webgui/resources");
			contextDataDir.addServlet(holderResourceWebgui, "/resc/*");
			
			server.start();
		} catch (Exception e) {
			throw new RuntimeException("Start of jetty failed: " + e.getMessage(), e);
		}
		super.onApplicationStart();
	}
	
	private ServletHolder buildLoadOnStart(Class<? extends Servlet> servlet) {
		ServletHolder sh = new ServletHolder(servlet);
		sh.setInitOrder(1);
		return sh;
	}

	@Override
	public void onApplicationEnd() {
		if (server != null) {
			try {
				server.stop();
				server = null;
			} catch (Exception e) {
				throw new RuntimeException("Error while stopping jetty: " + e.getMessage(), e);
			}
		}
		super.onApplicationEnd();
	}
}
