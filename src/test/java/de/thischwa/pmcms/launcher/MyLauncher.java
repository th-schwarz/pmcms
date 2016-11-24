package de.thischwa.pmcms.launcher;

import java.io.File;
import java.io.PrintStream;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.DemuxOutputStream;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Commandline.Argument;

public class MyLauncher {

	public static void main(String[] args) {
		// global ant project settings
		Project project = new Project();
		project.setBaseDir(new File(System.getProperty("user.dir")));
		project.init();
		DefaultLogger logger = new DefaultLogger();
		project.addBuildListener(logger);
		logger.setOutputPrintStream(System.out);
		logger.setErrorPrintStream(System.err);
		logger.setMessageOutputLevel(Project.MSG_INFO);
		System.setOut(new PrintStream(new DemuxOutputStream(project, false)));
		System.setErr(new PrintStream(new DemuxOutputStream(project, true)));
		project.fireBuildStarted();

		Throwable caught = null;
		try {
			// an echo example
			Echo echo = new Echo();
			echo.setTaskName("Echo");
			echo.setProject(project);
			echo.init();
			echo.setMessage("Launching ...");
			echo.execute();

			
			/** initialize an java task **/
			Java javaTask = new Java();
			javaTask.setNewenvironment(true);
			javaTask.setTaskName("runjava");
			javaTask.setProject(project);
			javaTask.setFork(true);
			javaTask.setFailonerror(true);
			javaTask.setClassname(MyClassToLaunch.class.getName());
			
			// add some vm args
			Argument jvmArgs = javaTask.createJvmarg();
			jvmArgs.setLine("-Xms512m -Xmx512m");
			
			// added some args for to class to launch
			Argument taskArgs = javaTask.createArg();
			taskArgs.setLine("bla path=/tmp/");
			
			Path classPath = new Path(project, new File(System.getProperty("user.dir") + "/bin/test").getAbsolutePath());
			javaTask.setClasspath(classPath);
			
			javaTask.init();
			int ret = javaTask.executeJava();
			System.out.println("return code: " + ret);

		} catch (BuildException e) {
			caught = e;
		}
		project.log("finished");
		project.fireBuildFinished(caught);
	}
}
