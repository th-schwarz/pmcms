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
package de.thischwa.pmcms.tool.image;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import de.thischwa.pmcms.tool.Utils;
import de.thischwa.pmcms.tool.XY;
import de.thischwa.pmcms.tool.file.FileTool;

/**
 * Helper to handle and calculate the dimension of an image.
 * 
 * @author Thilo Schwarz
 */
public class Dimension extends XY {
	private static final long serialVersionUID = 1L;
	private static final Pattern styleHeightPattern = Pattern.compile(".*height:(\\d+).*");		
	private static final Pattern styleWidthPattern = Pattern.compile(".*width:(\\d+).*");

	public Dimension(int x, int y) {
		super(x, y);
	}
	
	public Dimension(java.awt.Dimension dim) {
		this(dim.width, dim.height);
	}

	/**
	 * Scale a dimension to a fix size. The ratio is keeping alive!
	 * 
	 * @param maxX
	 * @param maxY
	 * @return Dimension scaled to the fix size.
	 */
	public Dimension getScaledToFixSize(int maxX, int maxY) {
		if (maxX < 1 || maxY < 1)
			throw new IllegalArgumentException("Committed value shouldn't be null!");
		float nPercent = 0;
		float nPercentW = ((float) maxX / (float) x);
		float nPercentH = ((float) maxY / (float) y);
		if (nPercentH < nPercentW)
			nPercent = nPercentH;
		else
			nPercent = nPercentW;

		int destWidth = (int) (x * nPercent);
		int destHeight = (int) (y * nPercent);
		return new Dimension(destWidth, destHeight);
	}

	/**
	 * Wrapper to {@link #getScaledToFixSize(int, int)}.
	 */
	public Dimension getScaledToFixSize(Dimension maxDimension) {
		if (maxDimension == null)
			throw new IllegalArgumentException("Max dimension shouldn't be null!");
		return getScaledToFixSize(maxDimension.x, maxDimension.y);
	}

	@Override
	public String toString() {
		return String.format("%sx%s", x, y);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Dimension))
			return false;
		return super.equals(obj);
	}

	public static String expandPath(String filePath, Dimension imageDimension) {
		String ext = FileTool.getExtension(filePath);
		String pathWithoutExt = filePath.substring(0, filePath.length() - ext.length() - 1);
		filePath = Utils.join(pathWithoutExt, "_", imageDimension.toString(), ".", ext);
		return filePath;
	}

	/**
	 * Grab the {@link Dimension} from the attributes of an img-tag. If height/width attribute doesn't exists, the style attribute will be
	 * scanned.
	 * 
	 * @param attr {@link Map} of the attributes of the img-tag. The key is the name of the attribute.
	 * @return {@link Dimension} grabbed from the attributes of an img-tag.
	 * @throws IllegalArgumentException If the {@link Dimension} can't grab from the attributes. 
	 */
	public static Dimension getDimensionFromAttr(Map<String, String> attr) throws IllegalArgumentException {
		String heightStr = attr.get("height");
		String widthStr = attr.get("width");
		if (heightStr != null && widthStr != null)
			return new Dimension(Integer.parseInt(widthStr), Integer.parseInt(heightStr));

		if (StringUtils.isBlank(attr.get("style")))
			throw new IllegalArgumentException("Couldn't get the dimension from img-tag attributes!");
		String style = StringUtils.replace(attr.get("style"), " ", "");
		int height = -1;
		int width = -1;
		
		Matcher matcher = styleHeightPattern.matcher(style);
		if (matcher.matches())
			height = Integer.parseInt(matcher.group(1));

		matcher = styleWidthPattern.matcher(style);
		if (matcher.matches())
			width = Integer.parseInt(matcher.group(1));

		if (width == -1 || height == -1)
			throw new IllegalArgumentException("Couldn't get the dimension from style-attribute!");
		return new Dimension(width, height);
	}
}
