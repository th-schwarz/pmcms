package de.thischwa.pmcmstest.spring.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TestBean {

	@Value("${properties.file}")
	private String filename;

	@Value("${text}")
	private String text;

	public String getText() {
		return text;
	}

	public String getFilename() {
		return filename;
	}
}
