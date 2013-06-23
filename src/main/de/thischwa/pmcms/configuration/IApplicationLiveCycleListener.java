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
package de.thischwa.pmcms.configuration;


/**
 * Interface for each task which have to start and stop with the application.
 * 
 * @author  <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public interface IApplicationLiveCycleListener
{
    /**
     * Called on application start.
     */
    public void onApplicationStart();
    
    /**
     * Called on application end.
     */
    public void onApplicationEnd();
}
