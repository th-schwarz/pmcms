package de.thischwa.pmcms.tool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URLEncoder;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.configuration.BasicConfigurator;
import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.model.domain.pojo.Macro;
import de.thischwa.pmcms.server.Action;
import de.thischwa.pmcms.tool.Link;

public class TestLink {
	
	private static BasicConfigurator config;
	private static Link link;

	@BeforeClass
	public static void init() {
		config = new BasicConfigurator(Constants.APPLICATION_DIR);
		InitializationManager.start(config, false);
		link = InitializationManager.getBean(Link.class);
	}
	
	@AfterClass
	public static void tearDown() {
		InitializationManager.end();
	}
	
	@Test
	public void testFile() {
		link.init("file:///Development/java/PoorMansCMS/help/index.html");
		assertFalse(link.isExternal());
		assertFalse(link.isMailTo());
	}

	@Test
	public void testExternalLink() throws Exception {
		link.init("http://ix.de");
		assertTrue(link.isExternal());
		assertFalse(link.isMailTo());
	}
	
	@Test
	public void testDecodeParam() throws Exception {
		String baseUrl = "http://127.0.0.1:8080";
		String code = "<div id=\"content\">&nbsp;</div>";
		
		String url = String.format("%s/save?x=1&code=%s", baseUrl, URLEncoder.encode(code, "utf-8"));
		link.init(url);
		
		assertEquals(code, link.getParameter("code"));
		assertEquals("1", link.getParameter("x"));
	}
	
	@Test
	public void testBuildUrl() {
		Macro macro = new Macro();
		macro.setId(1);
		String url = Link.buildUrl("http://127.0.0.1:8080", macro, Action.EDIT);
		assertEquals("http://127.0.0.1:8080/edit?id=1&t=macro", url);
	}

	@Test
	public void testFileWOProtocol() throws Exception {
		String path = "files/file.zip";
		link.init(path);
		assertEquals(path, link.getPath());
		assertFalse(link.isExternal());
		assertFalse(link.isMailTo());
	}
	
}
