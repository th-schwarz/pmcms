package de.thischwa.pmcms.server;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestServlet extends AServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		OutputStream out = resp.getOutputStream();
//		out.write("TEST".getBytes());
//		out.flush();
//		out.close();
		
		ServletUtils.writeFile(resp, new File("webgui/example.xml"));
		resp.getOutputStream().flush();
	}

}
