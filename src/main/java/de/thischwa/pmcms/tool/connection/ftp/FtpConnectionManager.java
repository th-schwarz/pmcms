/*******************************************************************************
 * Poor Man's CMS (pmcms) - A very basic CMS generating static html pages.
 * http://poormans.sourceforge.net
 * Copyright (C) 2004-2013 by Thilo Schwarz
 * 
 * == BEGIN LICENSE ==
 * 
 * Licensed under the terms of any of the following licenses at your
 * choice:
 * 
 *  - GNU Lesser General Public License Version 2.1 or later (the "LGPL")
 *    http://www.gnu.org/licenses/lgpl.html
 * 
 *  - Mozilla Public License Version 1.1 or later (the "MPL")
 *    http://www.mozilla.org/MPL/MPL-1.1.html
 * 
 * == END LICENSE ==
 ******************************************************************************/
package de.thischwa.pmcms.tool.connection.ftp;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import de.thischwa.pmcms.tool.connection.ConnectionAuthentificationException;
import de.thischwa.pmcms.tool.connection.ConnectionRunningException;
import de.thischwa.pmcms.tool.connection.IConnectionManager;

/**
 * {@link IConnectionManager} implementation for ftp. <br>
 * The underlying class is the <code>FTPClient</code> from <code>apache's commons-net</code>.<br>
 * <br>
 * TODO not really required in the current scenario, but for production environment a 'keepAlive' method is needed!
 * 
 * @author Thilo Schwarz
 */
public class FtpConnectionManager implements IConnectionManager {
	private static Logger logger = Logger.getLogger(FtpConnectionManager.class);

	private FTPClient ftpClient;
	private String host;
	private int port;
	private String loginName;
	private String loginPassword;
	private String remoteStartDir;

	/** Root dir of the	server, ends with '/'. */ 
	private String serverRootDir;
	
	/** Default FTP encoding. */
    private final static String DEFAULT_ENCODING = "UTF-8";

    /** Default FTP port. */
	public final static int DEFAULT_PORT = 21;
	
	/** Default FTP file TYPE. */
	private final static int DEFAULT_FILE_TYPE = FTPClient.BINARY_FILE_TYPE;

	public FtpConnectionManager(final String host, int port, final String loginName, final String loginPassword, final String remoteStartDir) {
	    this.host = host;
	    this.port = (port == -1) ? DEFAULT_PORT : port;
	    this.loginName = loginName;
	    this.loginPassword = loginPassword;
	    this.remoteStartDir = remoteStartDir;
    }

    @Override
	public void start() {
    	ftpClient = new FTPClient();
    	try {
    		// 1. set the port
    		ftpClient.setDefaultPort(port);
    		
    		// 2. set the encoding, FTPClient() default is ISO-8859-1, which isn't well
    		//    this step has to be done before login !!
    		ftpClient.setControlEncoding(DEFAULT_ENCODING);
    		
    		// 3. connect to the server
    		ftpClient.connect(host);
    		checkReply(); // throws exception, if server replied an error
			logger.debug("[FTP] Connected to " + host + ":" + port);
    		
    		// 4. login
    		ftpClient.login(loginName, loginPassword);
    		checkReply();
			logger.debug("[FTP] Remote system is " + ftpClient.getSystemType());
    		
    		// 5. set the passive mode because most clients are behind a firewall
			ftpClient.enterLocalPassiveMode();
    		
    		// 6. set the default file TYPE
    		ftpClient.setFileType(DEFAULT_FILE_TYPE);
    		
    		// 7. change to the remote start directory
    		if (StringUtils.isNotBlank(remoteStartDir)) {
    			ftpClient.changeWorkingDirectory(remoteStartDir);
    			checkReply();
    			logger.debug("[FTP] Start directory set: " + ftpClient.getReplyString());
    		}
    		
    		// 8. save the root dir
			serverRootDir = ftpClient.printWorkingDirectory();
			if (!serverRootDir.endsWith("/"))
				serverRootDir = serverRootDir.concat("/");
			
	        logger.debug("[FTP] Login procedere completely successfull!");
        } catch (IOException e) {
        	close();
        	throw new ConnectionRunningException("While login procedere: " + e.getMessage(), e);
        }
    }


    @Override
	public void close() {
    	if (ftpClient != null) {
    		// 1. try to logout
    		try {ftpClient.logout();} catch (Exception e) {}
    		// 2. try to disconnect
    		try {ftpClient.disconnect();} catch (Exception e) {}
    		
    		ftpClient = null;
    	}
    	logger.debug("[FTP] Connection closed.");
    }

    @Override
	public boolean isConnected() {
	    return ftpClient != null && ftpClient.isConnected();
    }

    @Override
	public Object getUnderlyingObject() {
	    return ftpClient;
    }

    @Override
	public String getRootDir() {
	    return serverRootDir;
    }


	private void checkReply() {
		int replyCode = ftpClient.getReplyCode();

		// close connection, if dropped, so that isConnected() return false
		if (replyCode == FTPReply.SERVICE_NOT_AVAILABLE)
			close();

		logger.debug("[FTP] " + ftpClient.getReplyString());
		
		// throw exceptions depending on the replyCode, if is not positiv
		if (!FTPReply.isPositiveCompletion(replyCode)) {
			if (replyCode == FTPReply.CODE_503 || replyCode == FTPReply.NEED_PASSWORD || replyCode == FTPReply.NOT_LOGGED_IN)
				throw new ConnectionAuthentificationException(ftpClient.getReplyString());
			else
				throw new ConnectionRunningException(ftpClient.getReplyString());
		}
	}
}
