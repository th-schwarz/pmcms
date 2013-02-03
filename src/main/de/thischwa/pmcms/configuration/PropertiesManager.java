package de.thischwa.pmcms.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import de.thischwa.pmcms.tool.PropertiesTool;

public class PropertiesManager {

	private Properties props = new Properties();
	private Properties defaultSiteProps;
	private List<IPropertiesChangeListener> changeListeners = new ArrayList<IPropertiesChangeListener>();
	
	
	PropertiesManager(final Properties props) {
		defaultSiteProps = PropertiesTool.getProperties(props, "pmcms.site");
		this.props = props;
	}
	
	String getProperty(final String key) {
		return props.getProperty(key);
	}
	
	Properties getVelocityProperties() {
		return PropertiesTool.getProperties(props, "velocity", true);
	}
	
	public void registerListener(IPropertiesChangeListener listener) {
		changeListeners.add(listener);
	}
	
	void setSiteProperties(final Properties siteProperties) {
		// 1. restore the default site properties
		props.putAll(defaultSiteProps);
		
		// 2. add the new site properties
		props.putAll(siteProperties);
		
		notifyListener();
	}	
	
	private void notifyListener() {
		for (IPropertiesChangeListener listener : changeListeners) {
			listener.hasChanged(props);
		}
	}
}
