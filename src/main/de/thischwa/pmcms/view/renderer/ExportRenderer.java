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
package de.thischwa.pmcms.view.renderer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.configuration.PropertiesManager;
import de.thischwa.pmcms.configuration.resource.LabelHolder;
import de.thischwa.pmcms.exception.FatalException;
import de.thischwa.pmcms.exception.RenderingException;
import de.thischwa.pmcms.gui.IProgressViewer;
import de.thischwa.pmcms.model.IRenderable;
import de.thischwa.pmcms.model.domain.PoInfo;
import de.thischwa.pmcms.model.domain.PoPathInfo;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.tool.ChecksumTool;
import de.thischwa.pmcms.tool.PathTool;
import de.thischwa.pmcms.tool.compression.Zip;
import de.thischwa.pmcms.tool.file.FileTool;
import de.thischwa.pmcms.tool.swt.SWTUtils;

/**
 * Generates the static pages to the export directory of a site.
 * 
 * <b>Export rules: </b>
 * <ul>
 * <li>Every level is a directory.</li>
 * <li>The main welcome file (at the root directory of a site) redirects to the welcome file in the root level.</li>
 * <li>All welcome pages named with the index file name of the site.</li>
 * </ul>
 * The following pattern is required:
 * <pre>
 *	ExportRenderer exportRenderer = InitializationManager.getBean("exportRenderer");
 *	exportRenderer.setSite(site);
 *	exportRenderer.setMessages(messages);
 *	exportRenderer.init();
 *	if (!exportRenderer.isValidToExport())
 *		WARNING
 *	else
 *		DialogManager.startProgressDialog(shell, exportRenderer);
 * </pre>
 * 
 * @author Thilo Schwarz
 */
@Service()
public class ExportRenderer implements IProgressViewer {
	private static Logger logger = Logger.getLogger(ExportRenderer.class);
	private Site site;
	private File exportDir;
	private Set<IRenderable> renderableObjects;
	private IProgressMonitor monitor = null;
	private StringBuilder messages;
	private boolean isInterruptByUser = false;
	private Display display = null;
	private ExportThreadPoolController exportController;
	
	@Autowired private RenderData renderData;
	@Autowired private VelocityRenderer renderer;
	@Autowired private PropertiesManager pm;

	@Value("${pmcms.export.maxthreadspercore}")
	private int maxThreadsPerCount;
	
	@Value("${pmcms.filename.checksums}")
	private String checksumFilename;
	
	private String poExtension;
		
	public void setSite(final Site site) {
		this.site = site;
		this.exportDir = PoPathInfo.getSiteExportDirectory(this.site);
		this.poExtension = pm.getSiteProperty("pmcms.site.export.file.extension");
	}

	public void setMessages(final StringBuilder messages) {
		this.messages = messages;
	}
	
	public void setDisplay(Display display) {
		this.display = display;
	}
	
	public void init() {
		try {
			if (!this.exportDir.exists())
				this.exportDir.mkdirs();
			else
				FileUtils.cleanDirectory(this.exportDir);
		} catch (IOException e) {
			throw new RuntimeException("While checking/cleaning the export path:" + e.getMessage(), e);
		}
	
		// collect renderable / validation		
		renderableObjects = PoInfo.collectRenderables(site, messages);

		exportController = new ExportThreadPoolController(maxThreadsPerCount);
		
		logger.info("Site successfully exported.");
	}
	
	/**
	 * Start the export of the static html pages.
	 * 
	 * @throws RuntimeException if an exception is happened during rendering.
	 */
	@Override
	public void run() {
		logger.debug("Entered run.");
		File siteDir = PoPathInfo.getSiteDirectory(this.site);
		if (monitor != null)
			monitor.beginTask(String.format("%s: %d", LabelHolder.get("task.export.monitor"), this.renderableObjects.size()), 
					this.renderableObjects.size()); //$NON-NLS-1$


		if (CollectionUtils.isEmpty(site.getPages()))
			renderRedirector();

		try {
			FileUtils.cleanDirectory(exportDir);
			
			// build the directory structure for the renderables
			for(IRenderable ro : renderableObjects) {
				File dir = PathTool.getExportFile(ro, poExtension).getParentFile();
				if(!dir.exists())
					dir.mkdirs();
			}
			
			// loop through the renderable objects
			renderRenderables();
			
			if (!exportController.isError() && !isInterruptByUser) {
				logger.debug("Static export successfull!");

				// extra files to copy
				Set<File> filesToCopy = renderData.getFilesToCopy();
				for (File srcFile : filesToCopy) {
					String exportPathPart = srcFile.getAbsolutePath().substring(siteDir.getAbsolutePath().length()+1);
					File destFile = new File(exportDir, exportPathPart);
					if(srcFile.isFile())
						FileUtils.copyFile(srcFile, destFile);
				}			
				logger.debug("Extra files successful copied!");
				
				// generate hashes
				Collection<File> exportedFiles = FileTool.collectFiles(exportDir);
				if (monitor != null) {
					monitor.done();
					monitor.beginTask("Calculate checksums", exportedFiles.size());
				}
				Document dom = ChecksumTool.getDomChecksums(ChecksumTool.get(exportedFiles, exportDir.getAbsolutePath(), monitor));
				ByteArrayOutputStream out = new ByteArrayOutputStream(); 
				OutputFormat outformat = OutputFormat.createPrettyPrint();
				outformat.setEncoding(Constants.STANDARD_ENCODING);
				XMLWriter writer = new XMLWriter(out, outformat);
				writer.write(dom);
				writer.flush();
				String formatedDomString = out.toString();
				InputStream in = new ByteArrayInputStream(formatedDomString.getBytes());
				Map<InputStream, String> toCompress = new HashMap<InputStream, String>();
				toCompress.put(in, checksumFilename);
				File zipFile = new File(PoPathInfo.getSiteExportDirectory(site), 
						FilenameUtils.getBaseName(checksumFilename) + ".zip");
				Zip.compress(zipFile, toCompress);			
				zipFile = null;
			} else
				FileUtils.cleanDirectory(exportDir);
		} catch (Exception e) {
			logger.error("Error while export: " + e.getMessage(), e);
			throw new FatalException("Error while export " + this.site.getUrl() + e.getMessage(), e);
		} finally {
			if (monitor != null)
				monitor.done();
		}
	}


	private void renderRenderables() throws RenderingException {
		exportController.addAll(buildThreads(exportController, renderableObjects));
		SWTUtils.asyncExec(exportController, display);
		int oldThreadCount = 0;
		do {
			isInterruptByUser = (monitor != null && monitor.isCanceled());
			try {
				Thread.sleep(25);
			} catch (InterruptedException e) {
				logger.debug("Controller interrupted.");
			}
			int threadCount = exportController.getTerminatedThreadCount();
			if (oldThreadCount < threadCount) {
				incProgressValue(threadCount - oldThreadCount);
				oldThreadCount = threadCount;
			}
		} while (!exportController.isError() && !exportController.isTerminated() && !isInterruptByUser);
		
		if (exportController.isError())
			throw new RenderingException(exportController.getThreadException());
		if (isInterruptByUser) {
			exportController.cancel();
			try {
				FileUtils.cleanDirectory(exportDir);
			} catch (IOException e) {
				logger.error("While cleaning the export directory: " + e.getLocalizedMessage(), e);
			}
			logger.debug("Export was interrupt by user, export dir will be deleted!");
			
		}
	}
	
	/**
	 * Create a static html page, which redirects to the welcome page of the root level.
	 */
	private void renderRedirector() {
		File redFile = new File(InitializationManager.getDefaultResourcesPath(), "redirector.html");
		if (!redFile.exists())
			throw new RuntimeException("Default redirector not found: " + redFile);
		String welcomeFileName = pm.getSiteProperty("pmcms.site.export.file.welcome");
		File outputFile = new File(this.exportDir, welcomeFileName);
		String linkToRootPage = PoInfo.getRootLevel(this.site).getName().concat("/").concat(welcomeFileName);

		try {
			Map<String, Object> ctxObjs = new HashMap<String, Object>(1);
			ctxObjs.put("linktorootpage", linkToRootPage);
			String renderedRed = renderer.renderString(FileUtils.readFileToString(redFile), ctxObjs);
			FileUtils.writeStringToFile(outputFile, renderedRed);
		} catch (IOException e) {
			throw new FatalException("Error while writing the redirector: " + e.getMessage(), e);
		}
	}	
	
	public boolean isValidToExport() {
		return (renderableObjects.size() > 0 && messages.length() == 0);
	}
	
	public boolean isInterruptByUser() {
		return isInterruptByUser;
	}
	
	@Override
	public void setMonitor(final IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	private void incProgressValue(int worked) {
		if (monitor != null) {
			monitor.worked(worked);
		}
	}

	private Collection<Thread> buildThreads(final ExportThreadPoolController controller, final Collection<IRenderable> renderables) {
		Set<Thread> objs = new HashSet<Thread>(renderables.size());
		for (IRenderable renderable : renderables) 
			objs.add(new ExportRenderThread(controller, renderable, renderer, poExtension));
		return objs;
	}	
}
