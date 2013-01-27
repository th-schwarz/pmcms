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
package de.thischwa.pmcms.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import de.thischwa.pmcms.Constants;


/**
 * Wrapper to the {@link ProcessBuilder}. It reads all {@link InputStream}s which can cause an hanging {@link Process}.
 *
 * @see http://thilosdevblog.wordpress.com/2011/11/21/proper-handling-of-the-processbuilder/
 * @version $Id: ProcessBuilderWrapper.java 2198 2012-06-09 13:12:20Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class ProcessBuilderWrapper {
	private StringWriter infos;
	private StringWriter errors;
	private int status;
	
	public ProcessBuilderWrapper(File directory, List<String> command) throws Exception {
		infos = new StringWriter();
		errors = new StringWriter();
		ProcessBuilder pb = new ProcessBuilder(command);      
		if(directory != null)
			pb.directory(directory);
		Process process = pb.start();
		StreamBoozer seInfo = new StreamBoozer(process.getInputStream(), new PrintWriter(infos, true));
		StreamBoozer seError = new StreamBoozer(process.getErrorStream(), new PrintWriter(errors, true));
		seInfo.start();
		seError.start();
		status = process.waitFor();		
	}

	public ProcessBuilderWrapper(List<String> command) throws Exception {
		this(null, command);
	}
	
	public String getErrors() {
		return errors.toString();
	}
	
	public String getInfos() {
		return infos.toString();
	}
	
	public int getStatus() {
		return status;
	}

	private class StreamBoozer extends Thread {
		private InputStream in;
		private PrintWriter pw;
		
		StreamBoozer(InputStream in, PrintWriter pw) {
			this.in = in;
			this.pw = pw;
		}
		
		@Override
		public void run() {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(in, Constants.STANDARD_ENCODING));
				String line = null;
	            while ( (line = br.readLine()) != null) {
	            	pw.println(line);
	            }
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}	
}
