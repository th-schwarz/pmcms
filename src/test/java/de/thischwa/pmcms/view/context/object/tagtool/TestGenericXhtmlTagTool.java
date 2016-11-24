package de.thischwa.pmcms.view.context.object.tagtool;

import static org.junit.Assert.*;

import java.util.TreeMap;

import org.junit.Test;

public class TestGenericXhtmlTagTool {
	
	private static GenericXhtmlTagTool build(final String tag) {
		 GenericXhtmlTagTool tt = new GenericXhtmlTagTool(tag);
		 tt.attributes = new TreeMap<String, String>(); // we have to ensure the ordering
		 return tt;
	}

	@Test(expected=IllegalArgumentException.class)
	public void testEmptyGenericXhtmlTagTool() {
		 GenericXhtmlTagTool a = new GenericXhtmlTagTool("a");
		 a.contructTag();
	}

	@Test
	public void testWithoutValue() {
		 GenericXhtmlTagTool a = build("a");
		 a.putAttr("src", "http://test.com");
		 a.putAttr("class", "css.class");
		 assertEquals("<a class=\"css.class\" src=\"http://test.com\" />", a.contructTag());
	}

	@Test
	public void testWithalue() {
		 GenericXhtmlTagTool a = build("a");
		 a.putAttr("src", "http://test.com");
		 a.putAttr("class", "css.class");
		 a.setValue("A nice site!");
		 assertEquals("<a class=\"css.class\" src=\"http://test.com\">A nice site!</a>", a.contructTag());
	}
}
