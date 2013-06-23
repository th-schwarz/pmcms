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
package de.thischwa.pmcms.view.context.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import de.thischwa.pmcms.view.context.IContextObjectCommon;

/**
 * Context object to provide tool methods. A good place for various static helper methods.
 * 
 * @author Thilo Schwarz
 */
@Component("utils")
public class Utils implements IContextObjectCommon {

	/**
	 * Reverse the order of a {@link Collection}. It's null-save. If the desired {@link Collection} is null or empty, 
	 * an empty one will returned.
	 * @param <T>
	 * @param col The collection to reverse. Could be null or empty.
	 * 
	 * @return An empty collection if the desired one was empty or null, or the desired collection with the reversed order.
	 */
	public static <T> Collection<T> reverseCollection(Collection<T> col) {
		if (CollectionUtils.isEmpty(col))
			return new ArrayList<T>();
		ArrayList<T> arrayList = new ArrayList<T>(col);
		Collections.reverse(arrayList);
		return arrayList;
	}

	/**
	 * @return True, is string is empty, otherwise false.
	 */
	public static boolean isEmpty(final String string) {
		return StringUtils.isBlank(string);
	}

	/**
	 * @return Html escaped 'string', or '', if string is empty.
	 */
	public static String escape(final String string) {
		return StringUtils.defaultString(StringEscapeUtils.escapeHtml(string));
	}

	public static String skipBeginningString(final String string, final String stringToSkip) {
		return StringUtils.substring(string, stringToSkip.length());
	}
}
