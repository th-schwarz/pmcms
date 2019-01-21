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

import java.awt.Point;

import org.apache.commons.lang3.StringUtils;

/**
 * Helper for managing xy-coordinates.
 *
 * @author Thilo Schwarz
 */
public class XY extends Point {
	private static final long serialVersionUID = 1L;


	public XY(int x, int y) {
		setLocation(x, y);
	}
	
	public XY(Point p) {
		this(p.x, p.y);
	}
	
	public XY(int[] xy) {
		if (xy.length != 2)
			throw new IllegalArgumentException("xy array has the wrong size!");
		setLocation(xy[0], xy[1]);
	}
	
	
	/**
	 * Constructor to interpret to comma-separated values, e.g.: <code>1,2</code>
	 * 
	 * @param str
	 */
	public XY(final String str) {
		if (StringUtils.isEmpty(str))
			return;
		String tmp = StringUtils.strip(str);
		String values[] = StringUtils.split(tmp, ",");
		if (values.length != 2 && !(StringUtils.isNumeric(values[0]) && StringUtils.isNumeric(values[1])))
			throw new IllegalArgumentException("Can't interpret coordinate string!");
		setLocation(Integer.parseInt(values[0].trim()), Integer.parseInt(values[1].trim()));
	}

	public int[] toArray() {
		return new int[] {x, y};
	}
	
	@Override
	public String toString() {
		return String.format("%s,%s", x, y);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof XY))
			return false;
		XY xyObj = (XY) obj;
		return (x == xyObj.x && y == xyObj.y);
	}
}
