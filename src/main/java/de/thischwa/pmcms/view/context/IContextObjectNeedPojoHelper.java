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
package de.thischwa.pmcms.view.context;

import de.thischwa.pmcms.livecycle.PojoHelper;

/**
 * Inteface for velocity context objects, managed by spring.
 * Have to be implemented by all beans needed in every template, which needs the {@link PojoHelper}.
 *
 * @author Thilo Schwarz
 */
public interface IContextObjectNeedPojoHelper {
	public void setPojoHelper(final PojoHelper pojoHelper);
}
