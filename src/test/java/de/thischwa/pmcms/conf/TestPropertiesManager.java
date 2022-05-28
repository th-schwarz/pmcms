package de.thischwa.pmcms.conf;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestPropertiesManager {

	
	private static PropertiesManager pm = new PropertiesManager();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Properties baseProps = new Properties();
		baseProps.load(TestPropertiesManager.class.getResourceAsStream("base.properties"));
		pm.setBaseProperties(baseProps);
		Properties siteProps = new Properties();
		siteProps.load(TestPropertiesManager.class.getResourceAsStream("site.properties"));
		pm.setSiteProperties(siteProps);
	}

	@Test
	public final void testGetProperty() {
		assertEquals("htm", pm.getProperty("pmcms.site.export.file.extension"));
	}

	@Test
	public final void testGetSiteProperty() {
		assertEquals("html", pm.getSiteProperty("pmcms.site.export.file.extension"));
	}

}
