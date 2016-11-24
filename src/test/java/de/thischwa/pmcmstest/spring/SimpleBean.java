package de.thischwa.pmcmstest.spring;

import org.springframework.stereotype.Repository;

@Repository
public class SimpleBean implements IMessage {

	@Override
	public String getMessage() {
		return "Hello";
	}
}
