package de.thischwa.pmcms.gui.samples;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class AppRight extends ApplicationWindow {

	public AppRight() {
		super(null);
		addMenuBar();
	}

	public void run() {
		setBlockOnOpen(true);
		open();
		Display.getCurrent().dispose();
	}

	public static void main(String[] args) {
		AppRight awin = new AppRight();
		awin.run();
	}

	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		GridLayout myLayout = new GridLayout();
		myLayout.numColumns = 1;
		myLayout.marginWidth = 0;
		myLayout.marginHeight = 0;
		getShell().setLayout(myLayout);
		getShell().setMinimumSize(600, 400);
		getShell().setSize(600, 400);
	}

	@Override
	protected Control createContents(Composite parent) {
		new MainComposite(parent, SWT.NONE);
		new StatusBar(parent, SWT.NONE);

		parent.pack(true);
		return parent;
	}

	@Override
	protected MenuManager createMenuManager() {
		MenuManager menuManager = new MenuManager();
		MenuManager fileMenu = new MenuManager("&File");
		fileMenu.add(new ExitAction(this));
		menuManager.add(fileMenu);
		return menuManager;
	}

	class MainComposite extends Composite {
		private Text textLog = null;

		protected MainComposite(final Composite parent, int style) {
			super(parent, style);
			initialize(parent);

			for (int i = 0; i < 99; i++) {
				textLog.append("Dummy text " + i + " / ");
			}
		}

		private void initialize(final Composite parent) {
			GridData gridData = new org.eclipse.swt.layout.GridData();
			gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
			gridData.grabExcessHorizontalSpace = true;
			gridData.grabExcessVerticalSpace = true;
			gridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
			textLog = new Text(this, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
			textLog.setEditable(true);
			textLog.setLayoutData(gridData);

			setLayout(new GridLayout(1, false));
			setLayoutData(gridData);
		}
	}

	class StatusBar extends Composite {
		private Label statusText;

		public StatusBar(Composite parent, int style) {
			super(parent, style);
			GridLayout gl = new GridLayout();
			gl.numColumns = 3;
			setLayout(gl);
			statusText = new Label(this, SWT.NULL);
			statusText.setText("Default Statusbar Text");

			GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
			setLayoutData(gd);
		}
	}

	class ExitAction extends Action {
		ApplicationWindow window;

		public ExitAction(ApplicationWindow window) {
			this.window = window;
			setText("Exit Ctrl+X");
			setToolTipText("Exit Application");
		}

		@Override
		public void run() {
			window.close();
		}
	}

}