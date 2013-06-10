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
package de.thischwa.pmcms.gui.treeview;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.model.InstanceUtil;
import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.domain.pojo.Gallery;
import de.thischwa.pmcms.model.domain.pojo.Level;
import de.thischwa.pmcms.model.domain.pojo.Site;

/**
 * Content provider for the site tree view.
 *
 * @version $Id: TreeViewContentProvider.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class TreeViewContentProvider implements ITreeContentProvider {

	private TreeViewRootNode treeRoot;
	
	public void setTreeViewRoot(final TreeViewRootNode treeRoot) {
		this.treeRoot = treeRoot;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof TreeViewRootNode) {
			List<APoormansObject<?>> children = new ArrayList<APoormansObject<?>>();
			TreeViewRootNode root = (TreeViewRootNode) parentElement;
			children.add(root.getSite());
			if(InitializationManager.isAdmin()) {
				children.add(root.getMacroNode());
				children.add(root.getTemplateNode());
			}
			return children.toArray();
		}
		if (parentElement instanceof TreeViewMacroNode) {
			TreeViewMacroNode recourceContainer = (TreeViewMacroNode) parentElement;
			return new ArrayList<Object>(recourceContainer.getSiteResources()).toArray();
		}
		if (parentElement instanceof TreeViewTemplateNode) {
			TreeViewTemplateNode recourceContainer = (TreeViewTemplateNode) parentElement;
			return new ArrayList<Object>(recourceContainer.getSiteResources()).toArray();
		}
		if (InstanceUtil.isSite(parentElement)) {
			List<APoormansObject<?>> children = new ArrayList<APoormansObject<?>>();
			Site site = (Site) parentElement;
			if (!CollectionUtils.isEmpty(site.getPages()))
				children.addAll(site.getPages());
			if (!CollectionUtils.isEmpty(site.getSublevels()))
				children.addAll(site.getSublevels());
			return children.toArray();
		} 
		if (InstanceUtil.isJustLevel(parentElement))  {
			Level level = (Level) parentElement;
			List<APoormansObject<?>> children = new ArrayList<APoormansObject<?>>();
			if (!CollectionUtils.isEmpty(level.getPages()))
				children.addAll(level.getPages());
			if (!CollectionUtils.isEmpty(level.getSublevels()))
				children.addAll(level.getSublevels());
			return children.toArray();
		} 
		if (InstanceUtil.isGallery(parentElement)) {
			Gallery gallery = (Gallery) parentElement;
			return (CollectionUtils.isEmpty(gallery.getImages())) ? new Object[0] : gallery.getImages().toArray();
		} 
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof TreeViewRootNode)
			return null;
		if (InstanceUtil.isTemplate(element))
			return treeRoot.getTemplateNode();
		if (InstanceUtil.isPoormansObject(element))
			return ((APoormansObject<?>) element).getParent();
		throw new IllegalArgumentException("Unknown object TYPE in tree!");
	}

	@Override
	public boolean hasChildren(Object element) {
		return (getChildren(element) != null && getChildren(element).length > 0) ? true : false;
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public void dispose() {		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
