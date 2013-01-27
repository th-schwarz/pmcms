package de.thischwa.pmcms.tool.connection;

import static org.junit.Assert.*;


import org.junit.Test;

import de.thischwa.pmcms.tool.connection.UploadTreeNode;

public class TestUploadTreeNode {

	@Test
	public final void testSplitPath() {
		String path = "dir/subdir/file.ext";
		String[] expecteds = new String[] { "dir", "subdir/file.ext" };
		assertArrayEquals(expecteds, UploadTreeNode.splitPath(path));
	}

}
