package de.thischwa.pmcmstest.spring;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("master")
public class Masterbean implements InitializingBean {

	String txt = "";
	
	@Autowired
	private SimpleBean bean;
	
	public String getInnerMessage() {
		return txt + " inner: " + bean.getMessage();
	}
	@Override
	public void afterPropertiesSet() throws Exception {
		txt = "it works ...";
		
	}
}
