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
package de.thischwa.pmcms.tool;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Parsing the command line options and generates the corresponding help text.
 */
public class CliParser {
	private CommandLine line;
	private Options options = null;
	
	
	public CliParser(String[] args) throws Exception {
		buildOptions();
		CommandLineParser parser = new GnuParser();
		try {
			line = parser.parse(options, args);
		} catch (ParseException exp) {
			throw new Exception("Argument parsing failed. Reason: " + exp.getMessage());
		}
	}

	private void buildOptions() {
		Option help = new Option("help", "print this message");
		Option admin = new Option("admin", "start poormans in the admin mode");
		Option cleanUp = new Option("cleanup", "clean up the settings and site data, all data will be deleted !!!");
		Option dataDir = new Option("datadir", true, "full path of the data directory (required)");
		dataDir.setArgName("path");
		Option debug = new Option("debug", "print out debug statements on stdout while starting");
		options = new Options();
		options.addOption(help);
		options.addOption(admin);
		options.addOption(cleanUp);
		options.addOption(dataDir);
		options.addOption(debug);
	}
	
	/**
	 * Prints the help text to the system.out.
	 */
	public void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -jar start.jar", options);
	}
	
	/**
	 * Checks if the desired option is available.
	 * 
	 * @param opt
	 * @return True if the desired option is available, otherwise false.
	 */
	public boolean hasOption(String opt) {
		return line.hasOption(opt);
	}
	
	public String getOptionValue(String opt) {
		return line.getOptionValue(opt);
	}
}
