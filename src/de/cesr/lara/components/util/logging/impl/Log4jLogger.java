/**
 * This file is part of
 * 
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 * 
 * Copyright (C) 2012 Center for Environmental Systems Research, Kassel, Germany
 * 
 * LARA is free software: You can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * LARA is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.cesr.lara.components.util.logging.impl;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import de.cesr.lara.components.param.LBasicPa;
import de.cesr.parma.core.PmParameterManager;

/**
 * log4j logger
 */
public final class Log4jLogger {

	/**
	 * static reference to the log4j logger
	 */
	private static Logger logger = Logger.getRootLogger();

	private static boolean initialised = false;

	/**
	 * @param name
	 * @return the valid logger object
	 */
	public static Logger getLogger(Class<?> clazz) {
		return Logger.getLogger(clazz);
	}

	/**
	 * @param name
	 * @return the valid logger object
	 */
	public static Logger getLogger(String name) {
		return Logger.getLogger(name);
	}

	/**
	 * Initialises the log4j logging system and has to be called once before using.
	 * If {@link LBasicPa#LOG_PATH} is null, no logging is performed.
	 */
	public static void init() {
		if (false) { // if (!initialised) { not compatible with log4j2
			if (PmParameterManager
								.getParameter(LBasicPa.LOG_PATH) != null) {
				try {
					SimpleLayout layout = new SimpleLayout();
	
					// TODO remove static path?!
					DateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd_HH-mm-ss");
	
					// check if path exists and create if not:
					File outFile = new File(
							((String) PmParameterManager
									.getParameter(LBasicPa.LOG_PATH)));
	
					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug("Write logfile to " + outFile);
					}
					// LOGGING ->
					
					outFile.mkdirs();
	
					FileAppender fileAppender = new FileAppender(layout,
							outFile.getPath()
							+ File.separator + "lara_"
							+ dateFormat.format(Calendar.getInstance().getTime())
							+ ".log", false);
					logger.addAppender(fileAppender);
					// ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
				} catch (Exception ex) {
					System.err.println(ex);
				}
			}
			initialised = true;
		}
	}

}