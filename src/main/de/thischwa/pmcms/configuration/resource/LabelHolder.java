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
package de.thischwa.pmcms.configuration.resource;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

/**
 * Object to hold the {@link ResourceBundle}.
 *
 * @version $Id: LabelHolder.java 2226 2012-10-21 11:02:31Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class LabelHolder {

	/** Resourcebundle for i18n messages. */
    public static ResourceBundle resourceBundle;
    
    /** used locale. */
    private static Locale locale = null;
    
    /** default language string. */
    private static String defaultLanguage = "en";

    static {
		// load the ResourceBundle
		resourceBundle = ResourceBundle.getBundle("de.thischwa.pmcms.configuration.resource/labels");
    	
		// take care of the default language
		if (StringUtils.isBlank(resourceBundle.getLocale().getLanguage()))
			locale = new Locale(defaultLanguage);
		else
			locale = resourceBundle.getLocale();
    }

	/**
     * Just a wrapper to {@link ResourceBundle#getString(String)}.
     * 
     * @param key The key for the desired string.
     * @return The string for the given key or the key itself, if it doesn't exists.
     */
    public static String get(final String key) {
    	try {
    		return resourceBundle.getString(key);
    	} catch (MissingResourceException e) {
    		return '!' + key + '!';
    	}
    }
    
    /**
     * Returns the locale of this resource bundle or it's fallback.<br />
     * <i>Take care:</i> only the language is set with guarantee!
     * 
     * @return The used locale for this resource bundle. 
     */
    public static Locale getLocale() {
    	return locale;
    }
}
