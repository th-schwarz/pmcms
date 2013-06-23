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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.server.Action;
import de.thischwa.pmcms.server.ContextUtil;

/**
 * Wrapper object for {@link URI} to find out, if a link is an internal or an external one. 
 * The parameters are decoded with {@link Constants#STANDARD_ENCODING}.
 */
@Component()
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class Link {
	private static Logger logger = Logger.getLogger(Link.class);
	private boolean isExternal = false;
	private boolean isMailto = false;
	private boolean isFile = false;
	private String path = null;
	private Map<String, String> parameters = new HashMap<String, String>();

	@Value("${pmcms.jetty.host}")
	private String host;

	public void init(final String link) {
		try {
			URI uri = new URI(link);
			String schema = uri.getScheme();
			if (schema != null) {
				if (schema.equalsIgnoreCase("file")) {
					isFile = true;
				} else if (schema.equalsIgnoreCase("javascript")) {
					isExternal = true;
				} else if (schema.equalsIgnoreCase("mailto")) {
					isMailto = true;
					isExternal = true;
				} else if (schema.equalsIgnoreCase("http") && uri.getHost() != null && uri.getHost().equalsIgnoreCase(host)) {
					isExternal = false;
				} else if (schema.equalsIgnoreCase("http") || schema.equalsIgnoreCase("https") || schema.equalsIgnoreCase("ftp") || schema.equalsIgnoreCase("ftps"))
					isExternal = true;
			}
			path = uri.getPath();
		} catch (Exception e) {
			logger.warn("While trying to get the URI: " + e.getMessage(), e);
		}

		try {
			URL url = new URL(link);
			if (path == null)
				path = url.getPath();
			String query = url.getQuery();
			if (query != null) {
				String[] parameterPairs = StringUtils.split(query, "&");
				for (String parameterPair : parameterPairs) {
					if (StringUtils.isNotBlank(parameterPair)) {
						KeyValue kv = new KeyValue(parameterPair);
						String val = decodeQuietly(kv.val);
						parameters.put(kv.key, val);
					}
				}
			}
		} catch (Exception e) {
			if (path == null)
				throw new IllegalArgumentException(e);
		}
	}

	public boolean isExternal() {
		return isExternal;
	}

	public boolean hasParameter() {
		return !parameters.isEmpty();
	}

	public Map<String, String> getParameters() {
		return this.parameters;
	}

	public String getParameter(final String name) {
		return parameters.get(name);
	}

	public String getPath() {
		return path;
	}

	public boolean isMailTo() {
		return isMailto;
	}
	
	public boolean isFile() {
		return isFile;
	}
	
	public boolean pathStartsWith(String str) {
		return (path != null && path.startsWith("/"+str));
	}
	
	public boolean isPoormansRequest() {
		return !isExternal && !isFile 
				&& (pathStartsWith(Constants.LINK_IDENTICATOR_EDIT) || pathStartsWith(Constants.LINK_IDENTICATOR_PREVIEW) || pathStartsWith(Constants.LINK_IDENTICATOR_SAVE));
	}

	private String decodeQuietly(String str) {
		try {
			return URLDecoder.decode(str, Constants.STANDARD_ENCODING);
		} catch (UnsupportedEncodingException e) {
			return "Couldn't decode! Charset not found!";
		}
	}
	
	public static String buildUrl(final String baseUrl, final APoormansObject<?> po, final Action action) {
		if(po == null || action == null)
			throw new IllegalArgumentException("all params must be set");
		StringBuilder sb = new StringBuilder(baseUrl);
		if(!baseUrl.endsWith("/"))
			sb.append("/");
		sb.append(action.getName());
		sb.append("?");
		sb.append("id=").append(po.getId());
		sb.append("&");
		sb.append(Constants.LINK_TYPE_DESCRIPTOR).append("=").append(ContextUtil.getTypDescriptor(po.getClass()));
		return sb.toString();
	}
	
	
	private class KeyValue {
		String key;
		String val = "";

		KeyValue(String pair) {
			int pos = pair.indexOf("=");
			if (pos == -1) {
				key = pair;
				return;
			}
			key = pair.substring(0, pos);
			if (pos < pair.length() - 1)
				val = pair.substring(pos + 1, pair.length());
		}
	}
}
