/*******************************************************************************
 * Poor Man's CMS (poormans) - A very basic CMS generating static html pages.
 * http://poormans.sourceforge.net
 * Copyright (C) 2004-2011 by Thilo Schwarz
 * 
 * Licensed under the terms of any of the GNU General Public License Version 2
 * or later (the "GPL")
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.thischwa.pmcms.tool;


import org.junit.BeforeClass;
import org.junit.Test;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.configuration.BasicConfigurator;
import de.thischwa.pmcms.tool.DESCryptor;

import static org.junit.Assert.*;

public class TestDESCryptor {

	private static DESCryptor cryptor;

	@BeforeClass
	public static void init() {
		BasicConfigurator config = new BasicConfigurator(Constants.APPLICATION_DIR);
		cryptor = config.getContext().getBean(DESCryptor.class);
	}

	@Test
	public final void testCrypt() throws Exception {
		String plain = "some text";
		String encrypted = cryptor.encrypt(plain);
		String actual = cryptor.decrypt(encrypted);
		assertEquals(plain, actual);
	}

}
