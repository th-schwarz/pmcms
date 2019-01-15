package de.thischwa.pmcms.view.context.object.tagtool;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.conf.BasicConfigurator;
import de.thischwa.pmcms.conf.InitializationManager;
import de.thischwa.pmcms.livecycle.PojoHelper;
import de.thischwa.pmcms.livecycle.SiteHolder;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.model.tool.SitePersister;
import de.thischwa.pmcms.view.ViewMode;

public class TestImageTagTool {

	private static Site site;
	private static SiteHolder siteHolder;
	private static PojoHelper pojoHelper;

	@BeforeClass
	public static void setUp() throws Exception {
		InitializationManager.start(new BasicConfigurator(Constants.APPLICATION_DIR), false);
		site = SitePersister.read("pmcms.demo.site");
		siteHolder = InitializationManager.getBean(SiteHolder.class);
		siteHolder.setSite(site);
		pojoHelper = new PojoHelper();
		pojoHelper.putpo(siteHolder.get(11));
	}

	@AfterClass
	public static void tearDown() throws Exception {
		InitializationManager.getBean(SiteHolder.class).deleteSite();
		InitializationManager.end();
	}

	@Test
	public void testLayout() {
		ImageTagTool itt = InitializationManager.getBean(ImageTagTool.class);
		itt.setPojoHelper(pojoHelper);
		itt.setHeight(100);
		itt.setWidth(150);
		itt.setSrc("test.png");
		String actual = itt.contructTag();
		assertEquals("<img height=\"100\" width=\"150\" src=\"/site/layout/test.png\" />", actual);
	}

	@Test
	public void testExportLayout() {
		ImageTagTool itt = InitializationManager.getBean(ImageTagTool.class);
		itt.setPojoHelper(pojoHelper);
		itt.setHeight(100);
		itt.setWidth(150);
		itt.setSrc("test.png");
		itt.setViewMode(ViewMode.EXPORT);
		String actual = itt.contructTag();
		assertEquals("<img height=\"100\" width=\"150\" src=\"/site/layout/test_150x100.png\" />", actual);
	}

	@Test
	public void testUsedFromEditor() {
		ImageTagTool itt = InitializationManager.getBean(ImageTagTool.class);
		itt.setPojoHelper(pojoHelper);
		itt.usedFromEditor();
		itt.setHeight(100);
		itt.setWidth(150);
		itt.setSrc("/site/file/test.png");
		String actual = itt.contructTag();
		assertEquals("<img height=\"100\" width=\"150\" src=\"/site/file/test.png\" />", actual);
	}
}
