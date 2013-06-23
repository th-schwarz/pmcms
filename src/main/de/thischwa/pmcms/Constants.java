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
package de.thischwa.pmcms;

import java.io.File;

import de.thischwa.pmcms.server.Action;
import de.thischwa.pmcms.tool.XY;

/**
 * Some global constants.
 */
public interface Constants {
	
	/** Name of the parameter for setting the view mode. (See VIEMODE_*) */
	public static final String LINK_VIEWMODE_DESCRIPTOR = "poormans_vm";
	
	/** Value to set export mode. */
	public static final String LINK_VIEWMODE_EXPORT = "vm_ex";
	
	/** Name of the parameter which contains the TYPE.  */
	public static final String LINK_TYPE_DESCRIPTOR = "t";
	
	/** Value to indicate a Page. */
	public static final String LINK_TYPE_PAGE = "page";
	
	/** Value to indicate a Gallery. */
	public static final String LINK_TYPE_GALLERY = "gallery";
	
	/** Value to indicate an Image. */
	public static final String LINK_TYPE_IMAGE = "image";

	/** Value to indicate a Macro. */
	public static final String LINK_TYPE_MACRO = "macro";

	/** Value to indicate Template. */
	public static final String LINK_TYPE_TEMPLATE = "templ";
	
	/** Name of the parameter which contains a comma separated list of form field names in the request. */
	public static final String LINK_EDITFIELDS_DESCRIPTOR = "pmcms_ef";
	
	/** Name of the path indicates to preview. */
	public static final String LINK_IDENTICATOR_PREVIEW = Action.PREVIEW.getName();
	
	/** Name of the path indicates to save.*/
	public static final String LINK_IDENTICATOR_SAVE = Action.SAVE.getName();
	
	/** Name of the path indicates to edit. */
	public static final String LINK_IDENTICATOR_EDIT = Action.EDIT.getName();

	/** Name of the path to indicate a resource request of a site */
	public static final String LINK_IDENTICATOR_SITE_RESOURCE = "site";
	
	/** Poormans' temp directory. */
	public static final File TEMP_DIR = new File(System.getProperty("java.io.tmpdir"), "PoorMansCMS");

	/** Poormans' application directory. */
	public static final File APPLICATION_DIR = new File(System.getProperty("user.dir"));
	
	/** User's home directory. */ 
	public static final File HOME_DIR = new File(System.getProperty("user.home"));
	
	/** Default data directory. */
	public static final File DEFAULT_DATA_DIR = new File(HOME_DIR, "PoorMansCMS");
	
	/** Standard http port. */
	public static final int STANDARD_PORT_HTTP = 80;
	
	/** Extension of the backup file name. */
	public static final String BACKUP_EXTENSION = "poormans_backup";

	/** Allowed chars to build names for the file system. */
	public static final String ALLOWED_CHARS_FOR_FILES = ".abcdefghijklmnopqrstuvwxyz_-0123456789";

	/** Count of CPU cores */
	public static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
	
	/** Default size of the main window. */
	public static final XY DEFAULT_SHELL_SIZE = new XY(800, 600); 
	
	/** Default splitter weight for the SashForm in workspace. */
	public static final XY DEFAULT_WORKSPACE_SPLITTER_WEIGHT = new XY(1, 3);

	/** Default size of the logger window. */
	public static final XY DEFAULT_LOGGER_SIZE = new XY(400, 600); 
	
	/** Default location of the heap size viewer popup. */
	public static final XY DEFAULT_HEAP_LOCATION = new XY(1, 1);
	
	/** Default encoding. */
	public static final String STANDARD_ENCODING = "UTF-8";

	public static final char SEPARATOR_CHAR = '/';

	public static final String SEPARATOR = String.valueOf(SEPARATOR_CHAR);
}
