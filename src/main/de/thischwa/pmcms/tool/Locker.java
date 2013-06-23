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
package de.thischwa.pmcms.tool;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import de.thischwa.pmcms.Constants;


/**
 * A very simple file locker. <br>
 * {@link #lock()} tries to create a lock file. If it's already locked, {@link #isLocked} is false.<br>
 * All possible errors are catched, unless if in {@link #lock()} an unexpected error is happened. Then 
 * a {@link RuntimeException} will be thrown.
 *
 * @author Thilo Schwarz
 */
public class Locker {

	/** File to lock for testing, if another instance is running. */
	private File lockFile = new File(Constants.TEMP_DIR, "pmcms.lck");

	/** Need for file locking. */
	private static FileChannel fileChannel = null;
	
	/** Need for file locking. */
	private FileLock fileLock = null;
	
	/** True, if another instance of poormans is running. */
	private boolean isLocked = false;
	
	
	public Locker(File lockFile) {
		this.lockFile = lockFile;
	}

	/**
	 * Creates a file based lock. 
	 * 
	 * @throws RuntimeException, if an exception is happened while creating the lock
	 * @return <code>true</code>, if a lock already exists, otherwise false.
	 */
	public boolean lock() {
		// check, if poormans runs
		try {
			if (!lockFile.exists()) {
				if (!lockFile.getParentFile().exists())
					lockFile.getParentFile().mkdirs();
				lockFile.createNewFile();
				fileChannel = new RandomAccessFile(lockFile, "rw").getChannel();
				fileLock = fileChannel.lock();
			} else {
				fileChannel = new RandomAccessFile(lockFile, "rw").getChannel();
				try {
					fileLock = fileChannel.tryLock();
					if (fileLock == null)
						isLocked = true;
				} catch (Exception e) {
					isLocked = true;
				}
			}
		} catch (Exception e) {
			System.out.println(e);
			throw new RuntimeException("Error while creating the lock [" + lockFile.getPath() + "]: " + e.getMessage(), e);
		}
		return isLocked;
	}
	
	public void unlock() {
		// close lock socket
		try {
			fileLock.release();
			fileChannel.close();
			lockFile.delete();
			isLocked = false;
		} catch (Exception e) {
		}		
	}
	
	public boolean isLocked() {
		return isLocked;
	}
}
