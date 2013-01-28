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
package de.thischwa.pmcms.model.domain;

import java.util.List;

import de.thischwa.pmcms.model.IOrderable;
import de.thischwa.pmcms.model.InstanceUtil;
import de.thischwa.pmcms.model.domain.pojo.Page;


/**
 * Helper to provide some static informations of an {@link IOrderable}, which you can get without a database call.
 *
 * @version $Id: OrderableInfo.java 2213 2012-06-30 12:01:07Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class OrderableInfo {

	public static boolean isFirst(final IOrderable<?> orderable) {
		return (orderable != null && orderable.getFamily().indexOf(orderable) == 0) ? true : false;
	}

	public static boolean hasNext(final IOrderable<?> orderable) {
		if (orderable == null)
			return false;
		if (InstanceUtil.isPage(orderable) && PoInfo.isWelcomePage((Page)orderable))
			return false;
		
		int pos = orderable.getFamily().indexOf(orderable);
		return (pos < orderable.getFamily().size()-1);
	}

	public static IOrderable<?> getNext(final IOrderable<?> orderable) {
		if (!hasNext(orderable))
			return null;

		@SuppressWarnings("unchecked")
		List<IOrderable<?>> family = (List<IOrderable<?>>) orderable.getFamily();
		int wantedPos = family.indexOf(orderable) + 1;
		return family.get(wantedPos);
	}

	public static boolean hasPrevious(final IOrderable<?> orderable) {
		if (orderable == null)
			return false;
		return (!isFirst(orderable));
	}

	public static IOrderable<?> getPrevious(final IOrderable<?> orderable) {
		if (!hasPrevious(orderable))
			return null;
		
		@SuppressWarnings("unchecked")
		List<IOrderable<?>> family = (List<IOrderable<?>>) orderable.getFamily();
		int wantedPos = family.indexOf(orderable) - 1;
		return family.get(wantedPos);
	}

}
