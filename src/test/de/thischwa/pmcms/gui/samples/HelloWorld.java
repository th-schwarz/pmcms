package de.thischwa.pmcms.gui.samples;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class HelloWorld extends ApplicationWindow {

	public HelloWorld() {
		super(null);
	}

	public void run() {
		setBlockOnOpen(true);
		open();
		Display.getCurrent().dispose();
	}


	public static void main(String[] args) {
		HelloWorld awin = new HelloWorld();
		awin.run();
	}

	@Override
	protected Control createContents(Composite parent) {
		Label helloText = new Label(parent, SWT.CENTER);
		helloText.setText("Hello SWT and JFace!");
		parent.pack();
		return parent;
	}
}