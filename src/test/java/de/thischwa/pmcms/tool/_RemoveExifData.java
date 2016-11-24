package de.thischwa.pmcms.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.sanselan.formats.jpeg.exifRewrite.ExifRewriter;

public class _RemoveExifData {

	
	public static void main(String[] args) throws Exception {
		final List<String> allowed = Arrays.asList("jfif", "jpg", "jpeg");
		File srcDir = new File(args[0]);
		File destDir =  new File(srcDir.getParentFile(), srcDir.getName() + "_rmexif");
		if(destDir.exists())
			FileUtils.deleteDirectory(destDir);
		FileUtils.forceMkdir(destDir);
		
		for(File f : srcDir.listFiles( new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				String ext = FilenameUtils.getExtension(name).toLowerCase(); 
				return allowed.contains(ext);
			}
		})) {
			System.out.println(f.getAbsolutePath());
			File dest = new File(destDir, f.getName());
			OutputStream out = new FileOutputStream(dest);
			new ExifRewriter().removeExifMetadata(f, out);
			out.flush();
			out.close();
		}
	}
}
