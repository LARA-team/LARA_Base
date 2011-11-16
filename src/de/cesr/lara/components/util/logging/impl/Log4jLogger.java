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

	/**
	 * @param name
	 * @return the valid logger object Created by Sascha Holzhauer on 02.12.2009
	 */
	public static Logger getLogger(Class<?> name) {
		return Logger.getLogger(name);
	}

	/**
	 * @param name
	 * @return the valid logger object Created by Sascha Holzhauer on 02.12.2009
	 */
	public static Logger getLogger(String name) {
		return Logger.getLogger(name);
	}

	/**
	 * initialises the log4j logging system has to be called once before using
	 * the logging system
	 */
	public static void init() {
		try {
			SimpleLayout layout = new SimpleLayout();

			// TODO remove static path?!
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
			FileAppender fileAppender = new FileAppender(layout, "log"
					+ File.separator + "lara_" + dateFormat.format(Calendar.getInstance().getTime())
					+ ".log", false);
			logger.addAppender(fileAppender);
			// ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
		} catch (Exception ex) {
			System.err.println(ex);
		}
	}

}
