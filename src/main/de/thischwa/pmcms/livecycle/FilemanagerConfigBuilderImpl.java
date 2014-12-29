package de.thischwa.pmcms.livecycle;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import codes.thischwa.c5c.requestcycle.impl.GlobalFilemanagerConfig;
import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.conf.InitializationManager;
import de.thischwa.pmcms.conf.PropertiesManager;

/**
 * FilemanagerConfigBuilderImpl.java - TODO DOCUMENTME!
 */
public class FilemanagerConfigBuilderImpl extends GlobalFilemanagerConfig {
	private static Logger logger = Logger.getLogger(FilemanagerConfigBuilderImpl.class);

	@Override
	protected void postLoadConfigFileHook() {
		PropertiesManager pm = InitializationManager.getBean(PropertiesManager.class);
		String[] allowedDocs = StringUtils.split(pm.getProperty("pmcms.filemanager.alloweddocs"), '|');
		List<String> extensions = InitializationManager.getAllowedImageExtensions();
		config.getImages().setExtensions(new HashSet<>(extensions));
		extensions.addAll(Arrays.asList(allowedDocs));
		config.getSecurity().setAllowedExtensions(new HashSet<>(extensions));
		config.getOptions().setFileRoot("/");
		String relPath = String.format("%s/%s/", Constants.LINK_IDENTICATOR_SITE_RESOURCE, pm.getProperty("pmcms.site.dir.resources.other"));
		config.getOptions().setRelPath(relPath);
		config.setComment("Built by pmcms.");
		
		config.getUpload().setFileSizeLimit(1);

		logger.debug("pmcms related configuration done.");
	}
}
