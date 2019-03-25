package de.thischwa.pmcms.tool.connection.sftp;

import static com.jcraft.jsch.ChannelSftp.SSH_FX_NO_SUCH_FILE;

import java.util.Vector;

import org.apache.log4j.Logger;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import de.thischwa.pmcms.tool.connection.ConnectionRunningException;

/**
 * A wrapper for {@link ChannelSftp}.
 */
final class SftpClient {
	private static Logger logger = Logger.getLogger(SftpClient.class);

	protected ChannelSftp client;

	SftpClient(ChannelSftp client) {
		this.client = client;
	}
	
	SftpATTRS getAttrByName(String path) {
		try {
			return client.lstat(path);
		} catch (SftpException e) {
			if(e.id == SSH_FX_NO_SUCH_FILE) {
				return null;
			}
			throw new ConnectionRunningException(String.format("Unexpected exception during 'lstat' on sftp: [%d:%s]", e.id, e.getMessage()), e);
		}
	}

	boolean cd(String path) {
		if(!exists(path))
			return false;
		try {
			client.cd(path);
			return true;
		} catch (SftpException e) {
			if(e.id == SSH_FX_NO_SUCH_FILE) {
				return false;
			}
			throw new ConnectionRunningException(String.format("Unexpected exception during 'cd' on sftp: [%d:%s]", e.id, e.getMessage()), e);
		}
	}
	

	boolean mkdir(String path) {
		try {
			client.mkdir(path);
			return true;
		} catch (SftpException e) {
			if(e.id == SSH_FX_NO_SUCH_FILE) {
				return false;
			}
			throw new ConnectionRunningException(String.format("Unexpected exception during 'mkdir' on sftp: [%d:%s]", e.id, e.getMessage()), e);
		}
	}

	boolean rm(String path) {
		try {
			client.rm(path);
			return true;
		} catch (SftpException e) {
			if(e.id == SSH_FX_NO_SUCH_FILE) {
				return false;
			}
			throw new ConnectionRunningException(String.format("Unexpected exception during 'rm' on sftp: [%d:%s]", e.id, e.getMessage()), e);
		}
	}


	boolean exists(String path) {
		Vector<?> res = null;
		try {
			res = client.ls(path);
		} catch (SftpException e) {
			if(e.id == SSH_FX_NO_SUCH_FILE) {
				return false;
			}
			logger.error(String.format("Unexpected exception during 'ls' files on sftp: [%d:%s]", e.id, e.getMessage()), e);
		}
		return res != null && !res.isEmpty();
	}

}
