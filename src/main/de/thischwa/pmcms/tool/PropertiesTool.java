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
package de.thischwa.pmcms.tool;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Tool for loading, trimming and filtering properties.
 * 
 * @author Thilo Schwarz
 */
public class PropertiesTool {

	private PropertiesTool() {
	};

	/**
	 * Construct a new property object, which returns the requested properties. <code>filter</code> is the 1st part of a property string. If
	 * <code>trim</code> is true, this part will be cut. (Useful e.g. for velocity properties.) If <code>exactMatch</code> is true, only one
	 * property (if found) will be returned.
	 */
	public static Properties getProperties(final Properties properties, final String filter, boolean trim, boolean exactMatch) {
		Properties newProps = new Properties();

		for (Object keyObj : properties.keySet()) {
			String key = keyObj.toString();
			if ((!exactMatch && key.startsWith(filter)) || (exactMatch && key.equals(filter))) {
				String newKey = key;
				if (trim)
					newKey = key.substring(filter.length() + 1, key.length());
				newProps.setProperty(newKey, properties.getProperty(key));
				if (exactMatch && key.equals(filter))
					return newProps;
			}
		}
		return newProps;
	}

	/**
	 * Wrapper for {@link #getProperties(String, boolean, boolean)}.
	 */
	public static Properties getProperties(final Properties properties, final String filter, boolean trim) {
		return getProperties(properties, filter, trim, false);
	}

	/**
	 * Wrapper for {@link #getProperties(String, boolean, boolean)}.
	 */
	public static Properties getProperties(final Properties properties, final String filter) {
		return getProperties(properties, filter, false, false);
	}

	/**
	 * Load {@link Properties} from one or more {@link InputStream}s. If there are the same property in different sources, just that on from
	 * the last source will be applied.
	 * 
	 * @param inputStreams
	 * @return
	 */
	public static Properties loadProperties(InputStream... inputStreams) {
		try {
			Properties props = new Properties();
			for (InputStream in : inputStreams)
				props.load(in);
			return props;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Builds properties from a Map of Strings.
	 * 
	 * @param map
	 * @return
	 */
	public static Properties buildProperties(final Map<String, String> map) {
		Properties props = new Properties();
		props.putAll(map);
		return props;
	}
}
