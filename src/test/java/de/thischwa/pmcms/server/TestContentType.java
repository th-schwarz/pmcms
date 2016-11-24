package de.thischwa.pmcms.server;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;


import org.junit.BeforeClass;
import org.junit.Test;

import de.thischwa.pmcms.Constants;

public class TestContentType {
	
	private static FileNameMap fileNameMap;
	
	@BeforeClass
	public static void init() {
		System.setProperty("content.types.user.table", new File(Constants.APPLICATION_DIR, "lib/content-types.properties").getAbsolutePath());
		fileNameMap = URLConnection.getFileNameMap();
	}
	
	@Test
	public void testMimeType() {
		assertEquals("image/gif", fileNameMap.getContentTypeFor("image.gif"));
		assertEquals("application/pdf", fileNameMap.getContentTypeFor("file.pDf"));
		assertEquals("text/css", fileNameMap.getContentTypeFor("format.css"));
		assertEquals("application/x-javascript", fileNameMap.getContentTypeFor("script.js"));
	}

}
