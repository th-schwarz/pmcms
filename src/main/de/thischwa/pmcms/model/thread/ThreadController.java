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
package de.thischwa.pmcms.model.thread;

import java.util.List;
import java.util.Vector;


import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;

import de.thischwa.pmcms.tool.Utils;

/**
 * A light-weight thread controller for starting and stopping simple threads.<br>
 * HINT: Threads should take care of SWT's threading model! They have to check, if {@link Display#getCurrent()} exists. Depending on it,
 * they have to run its subtasks with {@link Display#asyncExec(Runnable)} or {@link Display#syncExec(Runnable)}.
 * 
 * @version $Id: ThreadController.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class ThreadController {
	private static Logger logger = Logger.getLogger(ThreadController.class);
	private static ThreadController myInstance;
	private static List<Thread> threadContainer = new Vector<Thread>();

	public static ThreadController getInstance() {
		if (myInstance == null)
			myInstance = new ThreadController();
		return myInstance;
	}

	private ThreadController() {
		logger.debug("ThreadController initialized!");
		threadContainer = new Vector<Thread>();
	}

	public void runThread(final Thread thread) {
		if (threadContainer.contains(thread))
			throw new RuntimeException("Thread already exits [" + thread.getName() + "]!");
		thread.start();
		logger.debug("Thread successful started: ".concat(thread.getName()));
		threadContainer.add(thread);
	}

	public synchronized void stopAllThreads() {
		int size = threadContainer.size();
		if (size == 0)
			return;
		for (int i = size - 1; i == 0; i--) {
			Thread thread = threadContainer.get(i);
			stopThread(thread);
		}
	}

	public void stopThread(Thread thread) {
		threadContainer.remove(thread);
		logger.debug("Stop thread: " + thread.getName());
		thread.interrupt();
		Utils.quietlyDelay(500);
	}
}
