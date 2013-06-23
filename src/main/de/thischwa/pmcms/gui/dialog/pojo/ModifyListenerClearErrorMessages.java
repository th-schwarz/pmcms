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
package de.thischwa.pmcms.gui.dialog.pojo;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

/**
 * ModifyListener to clear the error messages of the pojo dialog.
 *
 * @see DialogCreator#clearErrorMessage()
 * @author Thilo Schwarz
 */
public class ModifyListenerClearErrorMessages implements ModifyListener {
	private DialogCreator dialogCreator;

	
	protected ModifyListenerClearErrorMessages(DialogCreator dialogCreator) {
		this.dialogCreator = dialogCreator;
	}

	@Override
	public void modifyText(ModifyEvent e) {
		dialogCreator.clearErrorMessage();
	}

}
