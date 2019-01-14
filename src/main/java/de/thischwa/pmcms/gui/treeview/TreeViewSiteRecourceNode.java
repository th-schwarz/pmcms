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
package de.thischwa.pmcms.gui.treeview;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;

import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.domain.pojo.ASiteResource;
import de.thischwa.pmcms.model.domain.pojo.SiteResourceType;

/**
 * Base-'Fake'-container to show special resources ({@link ASiteResource}s) in an extra section.
 */
public class TreeViewSiteRecourceNode<T extends ASiteResource> extends APoormansObject<TreeViewRootNode> {

	private SiteResourceType resourceType;
	private List<T> siteResources;

	TreeViewSiteRecourceNode(final TreeViewRootNode treeViewRoot, final SiteResourceType resourceType, final List<T> siteResources) {
		super.setParent(treeViewRoot);
		this.resourceType = resourceType;
		this.siteResources = siteResources;
	}

	public List<T> getSiteResources() {
		Collections.sort(siteResources, new ResourceComparator());
		return siteResources;
	}

	public SiteResourceType getResourceType() {
		return resourceType;
	}

	@Override
	public String getDecorationString() {
		return (resourceType == SiteResourceType.TEMPLATE) ? "Templates" : "Macros";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj.getClass() != getClass())
			return false;
		TreeViewSiteRecourceNode<?> res = (TreeViewSiteRecourceNode<?>) obj;
		return new EqualsBuilder().append(getDecorationString(), res.getDecorationString()).isEquals();
	}

	@Override
	public String toString() {
		return getDecorationString();
	}

	private class ResourceComparator implements Comparator<ASiteResource> {
		@Override
		public int compare(ASiteResource r1, ASiteResource r2) {
			return r1.getName().compareToIgnoreCase(r2.getName());
		}
	}
}