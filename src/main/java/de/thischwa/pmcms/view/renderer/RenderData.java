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

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import de.thischwa.pmcms.view.renderer.resource.IVirtualFile;


/**
 * Data collector for rendering.
 */
@Component
public class RenderData {
	
	private Set<File> files;
	
	public RenderData() {
		files = Collections.synchronizedSet(new HashSet<File>());
	}
	
	public void clear() {
		files.clear();
	}
	
	public void addFile(final IVirtualFile vf) {
		addFile(vf.getBaseFile());
	}
	
	public void addFile(final File file) {
		if(!file.isDirectory())
			files.add(file.getAbsoluteFile());
	}
	
	public Set<File> getFilesToCopy() {
		return files;
	}
}
