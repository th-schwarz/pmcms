package de.thischwa.pmcms.model.domain.pojo;

import org.junit.Test;

import de.thischwa.pmcms.model.domain.pojo.TemplateType;
import static org.junit.Assert.*; 


public class TestTemplateType {

	@Test
	public void testName01() {
		TemplateType templateType = TemplateType.getType("Page");
		assertEquals(TemplateType.PAGE, templateType);
	}
}
