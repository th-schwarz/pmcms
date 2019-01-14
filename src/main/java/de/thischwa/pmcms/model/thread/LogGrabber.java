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
package de.thischwa.pmcms.model.thread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import de.thischwa.pmcms.gui.ILogAppender;

/**
 * Grab the log for the gui.
 * 
 * @author Thilo Schwarz
 */
public class LogGrabber extends Thread {
	private static Logger logger = Logger.getLogger(LogGrabber.class);
	private String logFileName = null;
	private ILogAppender logAppender = null;

	
	public LogGrabber(final String logFileName, final ILogAppender logAppender) {
		setName("Thread-LogGrabber");
		this.logFileName = logFileName;
		this.logAppender = logAppender;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		BufferedReader reader = null;
		final File logFile = new File(logFileName);
		try {
			reader = new BufferedReader(new FileReader(logFile));
			reader.skip(logFile.length());
		} catch (FileNotFoundException e1) {
			logger.warn("Log file not found. End file logging.");
			interrupt();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// Main loop.
		while (!isInterrupted()) {
			String line;
			try {
				if (reader != null) {
	                line = reader.readLine();
	                while (line != null && !logAppender.getDisplay().isDisposed()) {
		                final String tmp = line;
		                logAppender.getDisplay().syncExec(new Runnable() {
			                @Override
							public void run() {
				                logAppender.appendToLog(tmp);
				                logAppender.appendToLog("\n");
			                }
		                });
		                line = reader.readLine();
	                }
                }
			} catch (IOException e1) {
				logger.error("While logging: " + e1.getMessage(), e1);
				interrupt();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.debug("Thread interrupted.");
				interrupt();
			}
		}

		IOUtils.closeQuietly(reader);
	}

}
