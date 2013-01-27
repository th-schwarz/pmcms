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
package de.thischwa.pmcms.server;

import java.io.File;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.servlet.ServletHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import de.thischwa.c5c.ConnectorServlet;
import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.configuration.AInitializingTask;
import de.thischwa.pmcms.configuration.IApplicationLiveCycleListener;

/**
 * Configure and start of Jetty.
 * 
 * @version $Id: JettyLauncher.java 2220 2012-09-21 18:39:19Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
@Service("jettyLauncher")
public class JettyLauncher extends AInitializingTask implements IApplicationLiveCycleListener {

	/** Local reference of the jetty server. */
	private static Server server = null;

	@Value("${data.dir}")
	private String dataDirStr;
	
	@Value("${poormans.jetty.host}")
	private String host;

	@Value("${poormans.jetty.port}")
	private String port;
	
	@Override
	public void onApplicationStart() {
		File dataDir = new File(dataDirStr);
		try {
			ClassLoader classLoader = ClassUtils.getDefaultClassLoader(); // spring class loader
			server = new Server();
			Connector connector = new SelectChannelConnector();
			connector.setHost(host);
			connector.setPort(Integer.parseInt(port));
			connector.setMaxIdleTime(Integer.MAX_VALUE);
			connector.setLowResourceMaxIdleTime(Integer.MAX_VALUE);
			server.setConnectors(new Connector[] { connector });

			Context context = new Context(server, "/", Context.SESSIONS);
			context.setClassLoader(classLoader);
			context.setResourceBase(dataDir.getAbsolutePath());

			ServletHolder holderDefaults = new ServletHolder(DefaultServlet.class);
			holderDefaults.setInitParameter("basePath", Constants.APPLICATION_DIR.getAbsolutePath() + "/defaults");
			holderDefaults.setInitParameter("aliases", "false");
			context.addServlet(holderDefaults, "/defaults/*");

			ServletHolder holderHelp = new ServletHolder(DefaultServlet.class);
			holderHelp.setInitParameter("basePath", Constants.APPLICATION_DIR.getAbsolutePath() + "/help");
			holderHelp.setInitParameter("aliases", "false");
			context.addServlet(holderHelp, "/help/*");
			
			ServletHolder holderFilemanger = new ServletHolder(DefaultServlet.class);
			holderFilemanger.setInitParameter("basePath", Constants.APPLICATION_DIR.getAbsolutePath() + "/filemanager");
			holderFilemanger.setInitParameter("aliases", "false");
			context.addServlet(holderFilemanger, "/filemanager/*");

			context.addServlet(new ServletHolder(EditorServlet.class), "/" + Constants.LINK_IDENTICATOR_EDIT + "/*");
			context.addServlet(new ServletHolder(ContentSaverServlet.class), "/" + Constants.LINK_IDENTICATOR_SAVE + "/*");
			context.addServlet(new ServletHolder(PreviewServlet.class), "/" + Constants.LINK_IDENTICATOR_PREVIEW + "/*");

			context.addServlet(new ServletHolder(ConnectorServlet.class), "/filemanager/connectors/java/*");

			ServletHolder holderCodeMirror = new ServletHolder(ZipProxyServlet.class);
			holderCodeMirror.setInitParameter("file", "sourceeditor/codemirror.zip");
			holderCodeMirror.setInitParameter("zipPathToSkip", "codemirror-2.34");
			context.addServlet(holderCodeMirror, "/codemirror/*");

			ServletHolder holderSiteResource = new ServletHolder(SiteResourceServlet.class);
			holderSiteResource.setInitParameter("basePath", new File(dataDir, "sites").getAbsolutePath());
			context.addServlet(holderSiteResource, String.format("/%s/*", Constants.LINK_IDENTICATOR_SITE_RESOURCE));

			ServletHolder holderCKEditor = new ServletHolder(ZipProxyServlet.class);
			holderCKEditor.setInitParameter("file", "ckeditor_4.0.1_standard.zip");
			holderCKEditor.setInitParameter("zipPathToSkip", "ckeditor");
			context.addServlet(holderCKEditor, "/ckeditor/*");
			
			/*ServletHolder holderFilemanager = new ServletHolder(ZipProxyServlet.class);
			holderFilemanager.setInitParameter("file", "Filemanager.zip");
			holderFilemanager.setInitParameter("zipPathToSkip", "Filemanager-master");
			context.addServlet(holderFilemanager, "/filemanager/*");*/
			
			server.start();
		} catch (Exception e) {
			throw new RuntimeException("Start of jetty failed: " + e.getMessage(), e);
		}
		super.onApplicationStart();
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
