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
package de.thischwa.pmcms.conf;

import javax.servlet.http.HttpSession;

/**
 * A Task is an object, initializing stuff on application start and do garbage collection (if needed)
 * on application end.
 * All tasks have to extend this class and have to follow the singleton pattern! 
 * 
 * @author Thilo Schwarz
 */
public abstract class AInitializingTask implements IApplicationLiveCycleListener {
	
    /** Indicate, if a task is initialized or not. */
    private static boolean initialized = false;
    
    /* 
     * Have to be overridden from classes, which extends this one.<br>
     * DON'T FORGET TO CALL SUPER!!!
     * 
     * @see de.thischwa.pmcms.conf.IApplicationLiveCycleListener#onApplicationStart()
     */
    @Override
	public void onApplicationStart() {
        setInitialized(true);
    }

    /* 
     * Have to be overridden from classes, which extends this one.<br>
     * DON'T FORGET TO CALL SUPER!!!
     * 
     * @see de.thischwa.pmcms.conf.IApplicationLiveCycleListener#onApplicationEnd()
     */
    @Override
	public void onApplicationEnd() {
        setInitialized(false);
    }

    /**
     * @return True if purpose is initialized, otherwise false.
     */
    public static boolean isInitialized() {
        return initialized;
    }

    /**
     * @param initialized
     */
    private static void setInitialized(boolean initialized) {
        AInitializingTask.initialized = initialized;
    }
    

    

    /**
     * NOT NEEDED YET!
     * @param session
     */
    public void onSessionStart(HttpSession session) {
    }

    /**
     * NOT NEEDED YET!
     * @param session
     */
    public void onSessionEnd(HttpSession session) {
    }

}
