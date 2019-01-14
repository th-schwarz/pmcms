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
package de.thischwa.pmcms.tool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

public class ThreadPool extends ThreadPoolExecutor {
	private static final Logger logger = Logger.getLogger(ThreadPool.class);
	private final AtomicInteger startedThreads = new AtomicInteger();
	private final AtomicInteger terminatedThreads = new AtomicInteger();

	public ThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}
	
	public ThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}
	
	public ThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
	}
	
	public ThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
	}
	
	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		super.beforeExecute(t, r);
		logger.debug(String.format("Thread started: %s", r));
		startedThreads.getAndIncrement();
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);
		logger.debug(String.format("Thread ended: %s", r));
		terminatedThreads.getAndIncrement();
	}

	@Override
	protected void terminated() {
		super.terminated();
		logger.info(String.format("Terminated: number of executed tasks %d", terminatedThreads.get()));
	}
	
	public int getStartedThreads() {
		return startedThreads.get();
	}
	
	public int getTerminatedThreads() {
		return terminatedThreads.get();
	}
}
