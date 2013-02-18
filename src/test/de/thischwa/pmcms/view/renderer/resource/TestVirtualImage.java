package de.thischwa.pmcms.view.renderer.resource;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.thischwa.pmcms.Constants;
import de.thischwa.pmcms.configuration.BasicConfigurator;
import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.livecycle.SiteHolder;
import de.thischwa.pmcms.model.domain.PoPathInfo;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.model.tool.SitePersister;

public class TestVirtualImage {
	
	private static Site site;
	private static SiteHolder siteHolder;

	@BeforeClass
	public static void setUp() throws Exception {
		InitializationManager.start(new BasicConfigurator(Constants.APPLICATION_DIR), false);
		site = SitePersister.read("pmcms.demo.site");
		siteHolder = InitializationManager.getBean(SiteHolder.class);
		siteHolder.setSite(site);
	}

	@AfterClass
	public static void tearDown() throws Exception {
		InitializationManager.getBean(SiteHolder.class).deleteSite();
		InitializationManager.end();
	}

	@Test(expected=IllegalArgumentException.class)
	public void testWrongPath() {
		VirtualImage vi = new VirtualImage(site, false, false);
		vi.consructFromTagFromView("/site/dir/test.png");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testWrongExt() {
		VirtualImage vi = new VirtualImage(site, false, false);
		vi.consructFromTagFromView("/site/image/test.ext");
	}
	
	@Test
	public void testCommon() {
		VirtualImage vi = new VirtualImage(site, false, false);
		vi.consructFromTagFromView("/site/image/test.png");
		File actual = vi.getBaseFile();
		File expected = new File(PoPathInfo.getSiteDirectory(site), "image/test.png");
		assertEquals(expected, actual);
	}

	@Test
	public void testLayout() {
		VirtualImage vi = new VirtualImage(site, true, false);
		vi.consructFromTagFromView("/site/layout/test.png");
		File actual = vi.getBaseFile();
		File expected = new File(PoPathInfo.getSiteDirectory(site), "layout/test.png");
		assertEquals(expected, actual);
	}

	@Test
	public void testGallery() {
		VirtualImage vi = new VirtualImage(site, false, true);
		vi.consructFromTagFromView("/site/gallery/name/test.png");
		File actual = vi.getBaseFile();
		File expected = new File(PoPathInfo.getSiteDirectory(site), "gallery/name/test.png");
		assertEquals(expected, actual);
	}

	@Test
	public void testCommonExportFile() {
		VirtualImage vi = new VirtualImage(site, false, false);
		vi.consructFromTagFromView("/site/image/test.png");
		vi.setDimension(100, 200);
		File actual = vi.getExportFile();
		File expected = new File(PoPathInfo.getSiteDirectory(site), "cache/image/test_100x200.png");
		assertEquals(expected, actual);
	}

	@Test
	public void testGalleryExportFile() {
		VirtualImage vi = new VirtualImage(site, false, true);
		vi.consructFromTagFromView("/site/gallery/name/test.png");
		vi.setDimension(100, 200);
		File actual = vi.getExportFile();
		File expected = new File(PoPathInfo.getSiteDirectory(site), "cache/gallery/name/test_100x200.png");
		assertEquals(expected, actual);
	}

	@Test
	public void testLayoutExportFile() {
		VirtualImage vi = new VirtualImage(site, true, false);
		vi.consructFromTagFromView("/site/layout/test.png");
		vi.setDimension(100, 200);
		File actual = vi.getExportFile();
		File expected = new File(PoPathInfo.getSiteDirectory(site), "cache/layout/test_100x200.png");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testLayoutCacheTag() {
		VirtualImage vi = new VirtualImage(site, true, false);
		vi.consructFromTagFromView("/site/cache/layout/test_100x200.png");
		File actual = vi.getBaseFile();
		File expected = new File(PoPathInfo.getSiteDirectory(site), "layout/test.png");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCommonCacheTag() {
		VirtualImage vi = new VirtualImage(site, false, false);
		vi.consructFromTagFromView("/site/cache/image/test_100x200.png");
		File actual = vi.getBaseFile();
		File expected = new File(PoPathInfo.getSiteDirectory(site), "image/test.png");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGalleryCacheTag() {
		VirtualImage vi = new VirtualImage(site, false, true);
		vi.consructFromTagFromView("/site/cache/gallery/name/test_100x200.png");
		File actual = vi.getBaseFile();
		File expected = new File(PoPathInfo.getSiteDirectory(site), "gallery/name/test.png");
		assertEquals(expected, actual);
	}
}
