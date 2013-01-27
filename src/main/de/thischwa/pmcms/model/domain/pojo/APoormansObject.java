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
package de.thischwa.pmcms.model.domain.pojo;



/**
 * The base object of all persist-able objects.
 *
 * @version $Id: APoormansObject.java 2216 2012-07-14 15:48:49Z th-schwarz $
 * @author Thilo Schwarz
 */
public abstract class APoormansObject<P> {
	public final static int UNSET_VALUE = -1;

	private int id = UNSET_VALUE;
	
	private P parent;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * @return The parent object.
	 */
	public P getParent() {
		return parent;
	}

	/**
	 * Set the parent of this {@link IPoorMansObject}.
	 */
	public void setParent(P directParent) {
		this.parent = directParent;
	}
	
	/**
	 * @return A title to view.
	 */
	public abstract String getDecorationString();

	@Override
	public int hashCode() {
		int result = 1;
		result += ((parent == null) ? 0 : parent.hashCode());
		result += id;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != getClass())
            return false;
        APoormansObject<?> po = (APoormansObject<?>)obj;
        return getId() == po.getId();
	}
	
	@Override
	public abstract String toString();
}
