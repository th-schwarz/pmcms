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

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.DemuxOutputStream;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.taskdefs.Mkdir;
import org.apache.tools.ant.types.Commandline.Argument;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.tool.OS.OSType;

/**
 * Internal ant tool. Implemented functions:
 * <ul>
 * <li>Start of pmcms. OS specific (JVM) arguments are respected.
 * <li>Cleanup: Deletes all settings and data.
 * </ul>
 *
 * @version $Id: InternalAntTool.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class InternalAntTool {

	/**
	 * Starts poormans with respect of the current OS. Dependent on that, special JVM settings and environment variables
	 * are set.
	 * 
	 * @param os The current os type.
	 * @param dataDir Data directory to use for.
	 * @param props Properties to use for.
	 * @param starterClass
	 * @param printDebug if true additional logging informations will be print out at stdio.
	 * @param additionalArgs Additional poormans commandline arguments. Can be <tt>null</tt> or empty.
	 */
	public static void start(final OSType os, final File dataDir, final Properties props, final String starterClass, boolean printDebug, final String[] additionalArgs) {
		Project project = buildProject();	
		List<String> jvmArgs = new ArrayList<String>();
		if(props.getProperty("jvm.arguments") != null) {
			String args = props.getProperty("jvm.arguments");
			if(printDebug)
				project.log("Raw JVM argements: " + args);
			jvmArgs = new ArrayList<String>(Arrays.asList(StringUtils.split(args)));
		}
		Throwable caught = null;
		String propLib = props.getProperty("pmcms.dir.lib");
		if(printDebug) {
			project.log("OS: " + os);
			project.log("Starter class: " + starterClass);
			project.log("Lib-folder-property: " + propLib);
		}
		int retVal = -1;
		try {
			/** initialize an java task **/
			Java javaTask = new Java();
			javaTask.setNewenvironment(true);
			javaTask.setTaskName("runjava");
			javaTask.setProject(project);
			javaTask.setFork(true);
			javaTask.setFailonerror(true);
			javaTask.setClassname(starterClass);
			
			/** set the class path */
			Path classPath = new Path(project);
			classPath.setPath(new File(Constants.APPLICATION_DIR, propLib).getPath());
			FileSet fileSet = new FileSet();
			fileSet.setDir(new File(Constants.APPLICATION_DIR, propLib));
			fileSet.setIncludes("**/*.jar");
			classPath.addFileset(fileSet);
			javaTask.setClasspath(classPath);
			if(printDebug)
				project.log("Classpath: " + classPath);
			
			/** add some vm args dependent on the os */
			switch (os) {
				case MAC:
					if(!jvmArgs.contains("-XstartOnFirstThread"))
						jvmArgs.add("-XstartOnFirstThread");
					break;
				case WIN:
					jvmArgs.add("-Djava.library.path=${pmcms.dir.lib}/swt");
					break;
				case LINUX:
					// no special properties required, setting be done by jvm args 
					break;
				default:
					project.log("Error: Unknown OS.");
					System.exit(3);
					break;
			}
			// add jvm args if exists
			if(!jvmArgs.isEmpty()) {
				// clean-up the jvm-args and ensure that each one starts with '-D'
				List<String> tmpArgs = new ArrayList<String>();
				for (String arg : jvmArgs) {
					if(arg.startsWith("-D") || arg.startsWith("-d") || arg.startsWith("-X") || arg.startsWith("-x"))
						tmpArgs.add(arg);
				}
				jvmArgs.clear();
				jvmArgs.addAll(tmpArgs);
				// build the args line and replace inline-variables
				String jvmArgsLine = StringUtils.join(jvmArgs, ' ').replace("${pmcms.dir.lib}", propLib);
				Argument argument = javaTask.createJvmarg();
				argument.setLine(jvmArgsLine);
				if(printDebug)
					project.log("JVM args: " + jvmArgsLine);
			}
			
			// add some program args if exists
			String strArgs = String.format("-datadir \"%s\"", dataDir);
			if(additionalArgs != null && additionalArgs.length > 0)
				strArgs += " " + StringUtils.join(additionalArgs, ' ');
			Argument taskArgs = javaTask.createArg();
			taskArgs.setLine(strArgs);
			if(printDebug)
				project.log("Program-args: " + strArgs);
			
			// call the java task
			javaTask.init();
			retVal = javaTask.executeJava();
		} catch (Exception e) {
			caught = e;
		}
		if(caught != null)
			project.log("Error while starting poormans: " + caught.getMessage(), caught, Project.MSG_ERR);
		else if(retVal != 0 && caught == null)
			project.log("finished with code: " + retVal);
		else 
			project.log("successful finished");
		project.fireBuildFinished(caught);	
		
	}
	
	/**
	 * Task to clean up all the generated data and setting. The database will be rebuild. 
	 * 
	 * @param dataDir Data directory to use for.
	 * @param props Properties to use for.
	 */
	public static void cleanup(final File dataDir, final Properties props) {
		Project project = buildProject();
		Throwable caught = null;
		try {			
			File sitesDir = new File(dataDir, props.getProperty("pmcms.dir.sites"));
			
			// delete the files
			deleteFile(project, new File(dataDir, ".settings.properties"));
			deleteDir(project, sitesDir);
			
			// make the required directories
			mkdir(project, new File(dataDir, "sites"));
		} catch (Exception e) {
			caught = e;
		}
		if(caught != null)
			project.log("Error while cleanung up: " + caught.getMessage(), caught, Project.MSG_ERR);
		else 
			project.log("successful finished");
		project.fireBuildFinished(caught);
	}
	
	/**
	 * Initializes an ant project. The base directory is the current working directory.
	 * 
	 * @return Ant project.
	 */
	private static Project buildProject() {
		Project project = new Project();
		project.setBasedir(System.getProperty("user.dir"));
		project.init();
		DefaultLogger logger = new DefaultLogger();
		project.addBuildListener(logger);
		logger.setOutputPrintStream(System.out);
		logger.setErrorPrintStream(System.err);
		logger.setMessageOutputLevel(Project.MSG_VERBOSE);
		System.setOut(new PrintStream(new DemuxOutputStream(project, false)));
		System.setErr(new PrintStream(new DemuxOutputStream(project, true)));
		project.fireBuildStarted();
		return project;
	}

	/*
	private static Environment.Variable buildVar(final String key, final String val) {
		Environment.Variable var = new Environment.Variable();
		var.setKey(key);
		var.setValue(val);
		return var;
	}
	*/
	
	private static void deleteDir(Project project, File dir) {
		Delete deleteTask = new Delete();
		deleteTask.setProject(project);
		deleteTask.setTaskName("delete dir");
		deleteTask.setDir(dir);
		deleteTask.execute();
	}
	
	private static void deleteFile(Project project, File file) {
		Delete deleteTask = new Delete();
		deleteTask.setProject(project);
		deleteTask.setTaskName("delete file");
		deleteTask.setFile(file);
		deleteTask.execute();
	}
	
	private static void mkdir(Project project, File dir) {
		Mkdir mkdir = new  Mkdir();
		mkdir.setProject(project);
		mkdir.setTaskName("mkdir");
		mkdir.setDir(dir);
		mkdir.execute();
	}
}