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
package de.thischwa.pmcms.view.renderer;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import de.thischwa.pmcms.wysisygeditor.ICKResource;


/**
 * Data collector for rendering.
 *
 * @version $Id: RenderData.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
@Component
public class RenderData {
	private Set<File> ckResources;
	
	private Set<File> files;
	private Set<File> dirs;
	
	public RenderData() {
		ckResources = Collections.synchronizedSet(new HashSet<File>());
		files = Collections.synchronizedSet(new HashSet<File>());
		dirs = Collections.synchronizedSet(new HashSet<File>());
	}
	
	public void clear() {
		ckResources.clear();
		files.clear();
		dirs.clear();
	}
	
	public synchronized void addCKResource(final File file) {
		if(!ckResources.contains(file.getAbsoluteFile()))
			ckResources.add(file.getAbsoluteFile());
	}
	
	public void addCKResource(final ICKResource resource) {
		addCKResource(resource.getFile().getAbsoluteFile());
	}
	
	public Set<File> getCkResources() {
		return ckResources;
	}
	
	public void addFile(final File file) {
		if(file.isDirectory())
			dirs.add(file.getAbsoluteFile());
		else
			files.add(file.getAbsoluteFile());
	}
	
	public Set<File> getFilesToCopy() {
		// we just need files whose parent folder wasn't added
		Set<File> cleanedFiles = new HashSet<File>(dirs);
		for(File tmpFile : files) {
			if(tmpFile.isFile()) {
				File parent = tmpFile.getParentFile().getAbsoluteFile();
				if(!cleanedFiles.contains(parent))
					cleanedFiles.add(tmpFile.getAbsoluteFile());
			}
		}

		return cleanedFiles;
	}
}
