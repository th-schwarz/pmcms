package de.thischwa.pmcms.tool.connection.sftp;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import de.thischwa.pmcms.tool.connection.ConnectionRunningException;
import de.thischwa.pmcms.tool.connection.IConnectionManager;
import de.thischwa.pmcms.tool.connection.ftp.FtpConnectionManager;

/**
 * {@link IConnectionManager} implementation for sftp. <br>
 * The underlying class is the {@link ChannelSftp} from <code>JSch</code>.
 * 
 * @author Thilo Schwarz
 */
public class SftpConnectionManager implements IConnectionManager {
	private static Logger logger = Logger.getLogger(FtpConnectionManager.class);

	private SftpClient sftpClient;
	private String host;
	private int port;
	private String loginName;
	private String loginPassword;
	private String remoteStartDir;

	private Session session;

	/** Root dir of the server, ends with '/'. */
	private String serverRootDir;

	/** Default SFTP port. */
	public final static int DEFAULT_PORT = 22;

	public SftpConnectionManager(final String host, int port, final String loginName, final String loginPassword,
			final String remoteStartDir) {
		this.host = host;
		this.port = (port == -1) ? DEFAULT_PORT : port;
		this.loginName = loginName;
		this.loginPassword = loginPassword;
		this.remoteStartDir = remoteStartDir;
	}

	@Override
	public void start() {
		try {
			JSch.setConfig("StrictHostKeyChecking", "no");

			// 1. create a session
			session = new JSch().getSession(loginName, host, port);
			session.setPassword(loginPassword);

			// 2. open the connection
			session.connect();

			// 3. build and connect the client
			Channel channel = session.openChannel("sftp");
			channel.connect();
			sftpClient = new SftpClient((ChannelSftp) channel);
			logger.debug("[SFTP] Connected to " + host + ":" + port);
			logger.debug("[SFTP] Server version is " + session.getServerVersion());

			// 4. change to the remote start directory
			if(StringUtils.isNotBlank(remoteStartDir)) {
				sftpClient.client.cd(remoteStartDir);
				logger.debug("[SFTP] Start directory set: " + sftpClient.client.pwd());
			}

			// 5. save the root dir
			serverRootDir = sftpClient.client.pwd();
			if(!serverRootDir.endsWith("/"))
				serverRootDir = serverRootDir.concat("/");

			logger.debug("[SFTP] Login procedere completely successfull!");
		} catch (SftpException | JSchException e) {
			close();
			throw new ConnectionRunningException("While login procedere: " + e.getMessage(), e);
		}
	}

	@Override
	public void close() {
		if(sftpClient != null) {
			// 1. try to logout
			try {
				sftpClient.client.disconnect();
			} catch (Exception e) {
			}
			sftpClient = null;
		}
		logger.debug("[SFTP] Connection closed.");
	}

	@Override
	public boolean isConnected() {
		return sftpClient != null && sftpClient.client.isConnected();
	}

	@Override
	public Object getUnderlyingObject() {
		return sftpClient;
	}

	@Override
	public String getRootDir() {
		return serverRootDir;
	}

}
