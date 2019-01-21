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
package de.thischwa.pmcms.tool.swt;


import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

import de.thischwa.pmcms.Constants;

/**
 * Verify listener for swt components like {@link Text} to ensure, that the input of a text control only contains numbers, lower cased chars 'a'...'z', '_' and '-'.
 * Used for file base names.
 * 
 * @author Thilo Schwarz
 */
public class FileNameVerifier implements VerifyListener {

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.VerifyListener#verifyText(org.eclipse.swt.events.VerifyEvent)
	 */
	@Override
	public void verifyText(VerifyEvent e) {
		String string = e.text;
		char[] chars = new char[string.length()];
		string.getChars(0, chars.length, chars, 0);
		for (char chr : chars) {
			if (!(StringUtils.contains(Constants.ALLOWED_CHARS_FOR_FILES, chr))) {
				e.doit = false;
				return;
			}
		}
	}
}
