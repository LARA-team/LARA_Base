package de.cesr.lara.components.util.logging.impl;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

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
	 * initialises the log4j logging system has to be called once before using
	 * No entry found. Memory is empty. the logging system
	 */
	public static void init() {
		if (!initialised) {
			try {
				SimpleLayout layout = new SimpleLayout();
	
				// TODO remove static path?!
				DateFormat dateFormat = new SimpleDateFormat(
						"yyyy-MM-dd_HH-mm-ss");
				FileAppender fileAppender = new FileAppender(layout, "log"
						+ File.separator + "lara_" + dateFormat.format(Calendar.getInstance().getTime())
						+ ".log", false);
				logger.addAppender(fileAppender);
				// ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
			} catch (Exception ex) {
				System.err.println(ex);
			}
			initialised = true;
		}
	}
}
