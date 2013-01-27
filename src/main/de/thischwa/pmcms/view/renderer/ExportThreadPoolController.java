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
package de.thischwa.pmcms.view.renderer;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.tool.ThreadPool;

/**
 * Initializes and feed the thread pool and handle the individual export threads. Just 'maxRunningThreads' are running simultaneous. It provides methods for
 * status information and exception handling.
 * 
 * @version $Id: ExportThreadPoolController.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class ExportThreadPoolController extends Thread {
	private static Logger logger = Logger.getLogger(ExportThreadPoolController.class);
	private ThreadPool threadPool;
	private Collection<Thread> threads;
	private boolean isError = false;
	private Exception threadException = null;
	private boolean isCanceled = false;

	ExportThreadPoolController(int threadMaxCountPerCore) {
		int threadPoolSize = 1;
		if (threadMaxCountPerCore != 0)
			threadPoolSize = threadMaxCountPerCore * Constants.CPU_COUNT;
		threadPool = new ThreadPool(threadPoolSize, threadPoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

		logger.info("ExportThreadPoolController thread pool size initialized with: " + threadPoolSize);
	}

	void addAll(final Collection<Thread> threads) {
		this.threads = threads;
	}

	@Override
	public void run() {
		if (CollectionUtils.isEmpty(threads)) {
			logger.warn("No render threads found.");
			return;
		}

		// feed the pool
		for (Thread thread : threads)
			threadPool.execute(thread);

		threadPool.shutdown();

		logger.debug("Thread pool initialized!");
	}

	int getTerminatedThreadCount() {
		return ((threadPool == null) ? 0 : (int) threadPool.getCompletedTaskCount());
	}

	int getTotalThreadCount() {
		return (threads == null) ? 0 : threads.size();
	}

	boolean isTerminated() {
		return threadPool.isTerminated();
	}

	boolean isError() {
		return isError;
	}

	Exception getThreadException() {
		return threadException;
	}

	synchronized void interrupt(final Thread thread, final Exception e) {
		threadException = e;
		isError = true;
		shutdownNow();
		logger.error(thread.getName().concat(" causes an error: ").concat(e.getMessage()), e);
	}

	boolean isCanceled() {
		return isCanceled;
	}

	void cancel() {
		isCanceled = true;
		shutdownNow();
	}

	private void shutdownNow() {
		try {
			threadPool.shutdownNow(); // Cancel currently executing tasks
			// Wait a while for tasks to respond to being cancelled
			if (!threadPool.awaitTermination(60, TimeUnit.SECONDS))
				logger.error("ThreadPool did not terminate");
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			threadPool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}
}
