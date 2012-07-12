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