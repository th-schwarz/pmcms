package de.thischwa.pmcms.tool.connection;

import static org.junit.Assert.*;

import java.net.URI;

import org.junit.Test;

public class TestUri {

	@Test
	public void test01() throws Exception {
		String uriStr = "ftp://login:password@myhost:21/basedir";
		URI uri = new URI(uriStr);
		assertEquals("ftp", uri.getScheme());
		assertEquals("login:password", uri.getUserInfo());
		assertEquals("myhost", uri.getHost());
		assertEquals(21, uri.getPort());
		assertEquals("/basedir", uri.getPath());
	}

	@Test
	public void test02() throws Exception {
		String uriStr = "sftp://login:password@myhost";
		URI uri = new URI(uriStr);
		assertEquals("sftp", uri.getScheme());
		assertEquals("login:password", uri.getUserInfo());
		assertEquals("myhost", uri.getHost());
		assertEquals(-1, uri.getPort());
		assertEquals("", uri.getPath());
	}
}
