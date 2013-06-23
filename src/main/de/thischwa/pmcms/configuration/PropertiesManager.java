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
package de.thischwa.pmcms.configuration;

import java.util.Properties;

import org.springframework.stereotype.Component;

import de.thischwa.pmcms.tool.PropertiesTool;

@Component
public class PropertiesManager {

	private Properties props;
	private Properties defaultSiteProps;
	private Properties siteProps;
	
	public void setProperties(final Properties props) {
		defaultSiteProps = PropertiesTool.getProperties(props, "pmcms.site");
		siteProps = new Properties(defaultSiteProps);
		this.props = props;
	}
	
	public void setSiteProperties(final Properties siteProps) {
		this.siteProps = new Properties(defaultSiteProps);
		this.siteProps.putAll(siteProps);
	}
	
	public String getProperty(final String key) {
		return props.getProperty(key);
	}
	
	public String getSiteProperty(final String key) {
		return siteProps.getProperty(key);
	}
	
	public Properties getVelocityProperties() {
		return PropertiesTool.getProperties(props, "velocity", true);
	}
	
	Properties getAllProperties() {
		return props;
	}
}
