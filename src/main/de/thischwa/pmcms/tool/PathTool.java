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
package de.thischwa.pmcms.tool;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;


import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.configuration.PropertiesManager;
import de.thischwa.pmcms.model.IRenderable;
import de.thischwa.pmcms.model.InstanceUtil;
import de.thischwa.pmcms.model.domain.OrderableInfo;
import de.thischwa.pmcms.model.domain.PoInfo;
import de.thischwa.pmcms.model.domain.PoPathInfo;
import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.domain.pojo.Gallery;
import de.thischwa.pmcms.model.domain.pojo.Image;
import de.thischwa.pmcms.model.domain.pojo.Level;
import de.thischwa.pmcms.model.domain.pojo.Page;


/**
 * Helper for constructing, converting paths and urls.<br>
 * All methods are null-safe and return an empty string if one or more arguments are null.
 */
public class PathTool {
	
	private PathTool() {}
	
	/**
	 * Generates the path segment of the hierarchical container, needed e.g. as part of the export path.
	 * 
	 * @param level
	 * @return Hierarchical path segment.
	 */
	public static String getFSHierarchicalContainerPathSegment(final Level level) {
		if (level == null)
			return "";
		StringBuilder path = new StringBuilder();
		if (!InstanceUtil.isSite(level))
			path.append(level.getName());
		Level tmpContainer = level.getParent();
		while (tmpContainer != null && !InstanceUtil.isSite(tmpContainer)) {
			path.insert(0, tmpContainer.getName().concat(File.separator));
			tmpContainer = tmpContainer.getParent();
		}		
		return path.toString();
	}

    public static String getURLFromFile(final String fileName) {
    	return getURLFromFile(fileName, true);
    }

    /**
     * Changes an application context relative file to an absolute url.
     * 
     * @param fileName
     * @return url
     */
    public static String getURLFromFile(final String fileName, boolean encode) {
        File file = new File(fileName);
        String temp = file.getPath().replace(File.separatorChar, '/');
        if(encode)
        	temp = encodePath(temp);
        return (temp.startsWith("/")) ? temp : "/".concat(temp);
    }

    /**
     * Generates the file name of an {@link IRenderable}.<br>
     * If 'renderable' is an {@link Image}, the name will be constructed by the following pattern: <br>
     * <code>[gallery name]-[image base name].[ext]</code>.
     * 
     * @param renderable
     * @param extension file extension
     * @return Return the file name without path!
     */
    public static String getExportBaseFilename(final IRenderable renderable, String extension) {
    	if (renderable == null)
    		throw new IllegalArgumentException("Can't handle IRenderable is null!");
    	StringBuilder name = new StringBuilder();
    	if (InstanceUtil.isPage(renderable)) {
    		Page page = (Page) renderable;
            if (OrderableInfo.isFirst(page))
            	name.append(InitializationManager.getBean(PropertiesManager.class).getSiteProperty("pmcms.site.export.file.welcome"));
            else {
                name.append(page.getName());
            	name.append('.');
            	name.append(extension);
            }
    	} else if (InstanceUtil.isImage(renderable)) {
    		Image image = (Image) renderable;
        	name.append(image.getParent().getName());   
        	name.append('-');
        	name.append(FilenameUtils.getBaseName(image.getFileName())); 
        	name.append('.');    
        	name.append(extension);		
    	} else
    		throw new IllegalArgumentException("Unknown object TYPE!");

    	return name.toString();
    }
    
	
	/**
	 * @return Export file path of an {@link IRenderable}.
	 */
	public static File getExportFile(final IRenderable renderable, final String extension) {
		File exportDirectory = PoPathInfo.getSiteExportDirectory(PoInfo.getSite((APoormansObject<?>)renderable));
		Level parent;
		APoormansObject<?> po = (APoormansObject<?>) renderable;
		if (InstanceUtil.isImage(renderable)) {
			Gallery gallery = (Gallery) po.getParent();
			parent = (Level) gallery.getParent();
		} else
			parent = (Level) po.getParent();
		String containerPath = getFSHierarchicalContainerPathSegment(parent);
		File outDir;
		if (StringUtils.isNotBlank(containerPath)) 
			outDir = new File(exportDirectory, containerPath);
		else
			outDir = exportDirectory;
		
		File outFile = new File(outDir, PathTool.getExportBaseFilename(renderable, extension));
		return outFile;
	}
	
	
    /**
     * Generates the relative path to the root of the site.
     * 
     * @param level
     * @return Relative path to the root, starting point is container.
     */
    public static String getURLRelativePathToRoot(final Level level) {
    	if (level == null || InstanceUtil.isSite(level))
			return "";
		StringBuilder link = new StringBuilder();
		for (int i = 1; i < level.getHierarchy(); i++) {
			link.append("../");
		}
		return link.toString();
    }
    
    /**
     * Generates a relative path from one {@link Level} to another.
     * 
     * @param startLevel
     * @param destLevel
     * @return Relative path.
     */
    public static String getURLRelativePathToLevel(final Level startLevel, final Level destLevel) {
		if ((startLevel == null && destLevel == null))
			return "";
		if (destLevel == null)
			return getURLRelativePathToRoot(startLevel);
		if (destLevel.equals(startLevel))
			return "";
		StringBuilder link = new StringBuilder();
		link.append(PathTool.getURLRelativePathToRoot(startLevel));
		for (APoormansObject<?> po : PoInfo.getBreadcrumbs(destLevel)) {
			if (InstanceUtil.isJustLevel(po)) {
				Level level = (Level) po;
				link.append(level.getName());
				link.append("/");
			}
		}
		return link.toString();
	}

	/**
	 * Encodes a path for using in links.
	 * 
	 * @param path
	 * @return Encoded path.
	 * @throws RuntimeException, if the (internal used) {@link URI} throws an {@link URISyntaxException}.
	 */
	public static String encodePath(final String path) {
		try {
			URI uri = new URI(null, path, null);
			return uri.toASCIIString();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Decodes an {@link URI}-encoded path. UTF-8 encoding is using.
	 * 
	 * @param path
	 * @return Decoded path.
	 * @throws RuntimeException, if the (internal used) {@link URLDecoder} throws an {@link UnsupportedEncodingException}. 
	 */
	public static String decodePath(final String path) {
		try {
			return URLDecoder.decode(path, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}
