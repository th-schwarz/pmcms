package de.thischwa.pmcmstest.spring;

public class StandAloneBean implements IMessage {

	@Override
	public String getMessage() {
		return "Hello Standalone!";
	}
}
