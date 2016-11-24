package de.thischwa.pmcms.gui.samples;


import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.conf.BasicConfigurator;
import de.thischwa.pmcms.conf.InitializationManager;
import de.thischwa.pmcms.model.thread.ThreadController;

public class HTMLRenderRelativeMemory {

	public static void main(String[] args) {
		if (InitializationManager.lock())
			System.exit(1);
		System.setProperty("data.dir", Constants.APPLICATION_DIR.getAbsolutePath());
		InitializationManager.start(new BasicConfigurator());
		/* Relative links: use the HTML base tag */
		String html = "<html><head>" + "<base href=\"http://127.0.0.1:8080/\" >" + "<title>HTML AutowiredConfigurationTests</title>"
				+ " <link rel=\"stylesheet\" type=\"text/css\" href=\"sites/s-th.info/format.css\" /></head>"
				+ "<body><a href=\"changelog\">local link</a></body></html>";

		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		Browser browser = new Browser(shell, SWT.NONE);
		browser.setText(html);
		shell.open();
		System.out.println(html);
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();

		ThreadController.getInstance().stopAllThreads();
		InitializationManager.end();
	}
}
