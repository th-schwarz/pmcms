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
import de.thischwa.pmcms.model.domain.pojo.Level;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.model.tool.SitePersister;

public class TestVirtualFile {
	
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
		VirtualFile vf = new VirtualFile(site, false);
		vf.consructFromTagFromView("/site/dir/test.zip");
	}

	@Test
	public void testCommon() {
		VirtualFile vf = new VirtualFile(site, false);
		vf.consructFromTagFromView("/site/file/test.zip");
		File actual = vf.getBaseFile();
		File expected = new File(PoPathInfo.getSiteDirectory(site), "file/test.zip");
		assertEquals(expected, actual);
	}

	@Test
	public void testCommonWithSubDir() {
		VirtualFile vf = new VirtualFile(site, false);
		vf.consructFromTagFromView("/site/file/sub/test.zip");
		File actual = vf.getBaseFile();
		File expected = new File(PoPathInfo.getSiteDirectory(site), "file/sub/test.zip");
		assertEquals(expected, actual);
	}

	@Test
	public void testLayout() {
		VirtualFile vf = new VirtualFile(site, true);
		vf.consructFromTagFromView("/site/layout/test.zip");
		File actual = vf.getBaseFile();
		File expected = new File(PoPathInfo.getSiteDirectory(site), "layout/test.zip");
		assertEquals(expected, actual);
	}

	@Test
	public void testLayoutWithSubDir() {
		VirtualFile vf = new VirtualFile(site, true);
		vf.consructFromTagFromView("/site/layout/dir/test.zip");
		File actual = vf.getBaseFile();
		File expected = new File(PoPathInfo.getSiteDirectory(site), "layout/dir/test.zip");
		assertEquals(expected, actual);
	}

	@Test
	public void testCommonExport() {
		VirtualFile vf = new VirtualFile(site, false);
		vf.consructFromTagFromView("/site/file/test.zip");
		File actual = vf.getExportFile();
		File expected = new File(PoPathInfo.getSiteDirectory(site), "site-export/file/test.zip");
		assertEquals(expected, actual);
	}

	@Test
	public void testCommonExportWithSubDir() {
		VirtualFile vf = new VirtualFile(site, false);
		vf.consructFromTagFromView("/site/file/sub/test.zip");
		File actual = vf.getExportFile();
		File expected = new File(PoPathInfo.getSiteDirectory(site), "site-export/file/sub/test.zip");
		assertEquals(expected, actual);
	}

	@Test
	public void testLayoutExport() {
		VirtualFile vf = new VirtualFile(site, true);
		vf.consructFromTagFromView("/site/layout/test.zip");
		File actual = vf.getExportFile();
		File expected = new File(PoPathInfo.getSiteDirectory(site), "site-export/layout/test.zip");
		assertEquals(expected, actual);
	}

	@Test
	public void testLayoutExportWithSubDir() {
		VirtualFile vf = new VirtualFile(site, true);
		vf.consructFromTagFromView("/site/layout/sub/test.zip");
		File actual = vf.getExportFile();
		File expected = new File(PoPathInfo.getSiteDirectory(site), "site-export/layout/sub/test.zip");
		assertEquals(expected, actual);
	}

	@Test
	public void testCommonPreviewTag() {
		VirtualFile vf = new VirtualFile(site, false);
		vf.consructFromTagFromView("/site/file/test.zip");
		String actual = vf.getTagSrcForPreview();
		assertEquals("/site/file/test.zip", actual);
	}

	@Test
	public void testCommonPreviewTagWithSubFolder() {
		VirtualFile vf = new VirtualFile(site, false);
		vf.consructFromTagFromView("/site/file/sub/test.zip");
		String actual = vf.getTagSrcForPreview();
		assertEquals("/site/file/sub/test.zip", actual);
	}

	@Test
	public void testLayoutPreviewTag() {
		VirtualFile vf = new VirtualFile(site, true);
		vf.consructFromTagFromView("/site/layout/test.zip");
		String actual = vf.getTagSrcForPreview();
		assertEquals("/site/layout/test.zip", actual);
	}
	
	@Test
	public void testLayoutPreviewTagWithSubFolder() {
		VirtualFile vf = new VirtualFile(site, true);
		vf.consructFromTagFromView("/site/layout/sub/test.zip");
		String actual = vf.getTagSrcForPreview();
		assertEquals("/site/layout/sub/test.zip", actual);
	}
	
	@Test
	public void testLayoutExportTagTag() {
		VirtualFile vf = new VirtualFile(site, true);
		vf.consructFromTagFromView("/site/layout/test.zip");
		Level level = (Level) siteHolder.get(20);
		String actual = vf.getTagSrcForExport(level);
		assertEquals("../../layout/test.zip", actual);
	}

	@Test
	public void testLayoutExportTagSubFolder() {
		VirtualFile vf = new VirtualFile(site, true);
		vf.consructFromTagFromView("/site/layout/sub/test.zip");
		Level level = (Level) siteHolder.get(20);
		String actual = vf.getTagSrcForExport(level);
		assertEquals("../../layout/sub/test.zip", actual);
	}
}
