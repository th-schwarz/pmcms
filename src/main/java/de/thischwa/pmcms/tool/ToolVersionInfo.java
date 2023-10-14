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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.app.Velocity;
import org.dom4j.DocumentFactory;
import org.eclipse.swt.SWT;
import org.springframework.core.SpringVersion;

/**
 * Some static methods to get version infos about the 3rd party tools.
 * 
 * @author Thilo Schwarz
 */
public class ToolVersionInfo {
	public enum TYPE {
		title, version
	}

	public static Map<TYPE, String> getJava() {
		Map<TYPE, String> info = new HashMap<>(2);
		info.put(TYPE.title, "Java");
		info.put(TYPE.version, Runtime.version().toString());
		return info;
	}

	public static Map<TYPE, String> getSpring() {
		return generateInfo(SpringVersion.class);
	}

	public static Map<TYPE, String> getDom4J() {
		return generateInfo(DocumentFactory.class);
	}
	
	public static Map<TYPE, String> getCKEditor()  {
		return getInfoFromMetaPom("codes.thischwa.ckeditor", "ckeditor-java");
	}

	public static Map<TYPE, String> getC5Connector()  {
		return getInfoFromMetaPom("codes.thischwa.c5c", "c5connector-java");
	}
	
	public static Map<TYPE, String> getVelocity() {
		return generateInfo(Velocity.class);
	}
	
	public static Map<TYPE, String> getJII() {
		return getInfoFromMetaPom("codes.thischwa.jii", "java-image-info");
	}
	
	public static Map<TYPE, String> getSwt() {
		Map<TYPE, String> info = new HashMap<>(2);
		info.put(TYPE.title, "SWT-" + SWT.getPlatform());
		info.put(TYPE.version, String.valueOf(SWT.getVersion()));
		return info;
	}
	
	private static Map<TYPE, String> generateInfo(final Class<?> cls) {
		Map<TYPE, String> info = new HashMap<>(2);
		info.put(TYPE.title, StringUtils.defaultIfEmpty(cls.getPackage().getImplementationTitle(), "Unknown"));
		String version = StringUtils.defaultIfEmpty(cls.getPackage().getImplementationVersion(), cls.getPackage().getSpecificationVersion());
		info.put(TYPE.version, StringUtils.defaultIfEmpty(version, "n/n"));
		return info;
	}

	private static Map<TYPE, String> getInfoFromMetaPom(final String groupId, final String artifactId) {
		String pomPath = String.format("META-INF/maven/%s/%s/pom.properties", groupId, artifactId);
		Map<TYPE, String> info = new HashMap<>(2);
		info.put(TYPE.title, "unknown");
		info.put(TYPE.version, "n/n");

		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(pomPath);
		if (in != null) {
			Properties properties = new Properties();
			try {
				in = new BufferedInputStream(in);
				properties.load(in);
			} catch (IOException ex) {
			}

			String title = properties.getProperty("artifactId");
			String version = properties.getProperty("version");
			if (title != null)
				info.put(TYPE.title, title);
			if (version != null)
				info.put(TYPE.version, version);
		}
		return info;
	}
}