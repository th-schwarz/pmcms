package de.thischwa.pmcms.livecycle;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import codes.thischwa.c5c.requestcycle.impl.GlobalFilemanagerLibConfig;
import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.conf.InitializationManager;
import de.thischwa.pmcms.conf.PropertiesManager;

/**
 * FilemanagerConfigBuilderImpl.java - TODO DOCUMENTME!
 */
public class FilemanagerConfigBuilderImpl extends GlobalFilemanagerLibConfig {
	private static Logger logger = Logger.getLogger(FilemanagerConfigBuilderImpl.class);

	@Override
	protected void postLoadConfigFileHook() {
		PropertiesManager pm = InitializationManager.getBean(PropertiesManager.class);
		String uloadLimit = pm.getProperty("pmcms.filemanager.upload.limit");
		if(StringUtils.isNumeric(uloadLimit)) {
			userConfig.getUpload().setFileSizeLimit(Integer.parseUnsignedInt(uloadLimit));
		} else  {
			userConfig.getUpload().setFileSizeLimit(); // auto			
		}
		String[] allowedDocs = StringUtils.split(pm.getProperty("pmcms.filemanager.alloweddocs"), '|');
		List<String> extensions = InitializationManager.getAllowedImageExtensions();
		userConfig.getImages().setExtensions(new HashSet<String>(extensions));
		extensions.addAll(Arrays.asList(allowedDocs));
		userConfig.getSecurity().setAllowedExtensions(new HashSet<String>(extensions));
		String relPath = String.format("/%s/%s/", Constants.LINK_IDENTICATOR_SITE_RESOURCE, pm.getProperty("pmcms.site.dir.resources.other"));
		userConfig.getOptions().setFileRoot(relPath);
		userConfig.getOptions().setServerRoot(true);
		userConfig.setComment("Built by pmcms.");
		logger.debug("pmcms related configuration done.");
	}
}
