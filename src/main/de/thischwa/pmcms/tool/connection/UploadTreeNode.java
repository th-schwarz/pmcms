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
package de.thischwa.pmcms.tool.connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * Node of the {@link UploadTree}. It handles the addition of {@link UploadObject}s itself. They will be automatically 
 * add at the correct point in the hierarchy. 
 *
 * @version $Id: UploadTreeNode.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class UploadTreeNode {
	private Map<String, UploadTreeNode> subTrees;
	private List<UploadObject> children;
	
	protected UploadTreeNode() {
		subTrees = new HashMap<String, UploadTreeNode>();
		children = new ArrayList<UploadObject>();
	}
	
	protected void add(String pathName, UploadObject uo) {
		String[] keys = splitPath(pathName);
		if(keys == null) {
			children.add(uo);
			return;
		}
		if(!subTrees.containsKey(keys[0]))
			subTrees.put(keys[0], new UploadTreeNode());
		subTrees.get(keys[0]).add(keys[1], uo);
	}
	
	public Map<String, UploadTreeNode> getSubTrees() {
		return subTrees;
	}
	
	public List<UploadObject> getChildren() {
		return children;
	}
	
	public boolean hasChildren() {
		return !children.isEmpty();
	}
	
	public boolean hasSubTrees() {
		return !subTrees.isEmpty();
	}
	
	protected static String[] splitPath(String path) {
		if(!path.contains(""+UploadTree.PATH_SEPARATOR) || StringUtils.isEmpty(path))
			return null;
		String[] keys = new String[2];
		keys[0] = path.substring(0, path.indexOf(UploadTree.PATH_SEPARATOR));
		keys[1] = path.substring(path.indexOf(UploadTree.PATH_SEPARATOR)+1, path.length());
		return keys;
	}
}
