package de.cesr.lara.components.util.logging;

/**
 * logger
 */
public interface LaraLogger {

	/**
	 * log debug message
	 * 
	 * @param message
	 */
	public void debug(String message);

	/**
	 * log error message
	 * 
	 * @param message
	 */
	public void error(String message);

	/**
	 * log fatal message
	 * 
	 * @param message
	 */
	public void fatal(String message);

	/**
	 * log info message
	 * 
	 * @param message
	 */
	public void info(String message);

	/**
	 * initialises the logging system has to be called once before using the
	 * logging system
	 */
	public void init();

	/**
	 * log warn message
	 * 
	 * @param message
	 */
	public void warn(String message);

}
