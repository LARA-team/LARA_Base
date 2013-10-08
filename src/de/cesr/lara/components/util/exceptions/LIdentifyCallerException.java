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
package de.cesr.lara.components.util.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * This exception is used to produce a stack trace and log it.
 */
public class LIdentifyCallerException extends LaraException {

	private static final int NUM_STACK_TRACE_FRAMES = 6;
	private static final int START_STACK_TRACE_FRAMES = 1;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int numStackTraceElements = NUM_STACK_TRACE_FRAMES;
	private int startStackTraceElements = START_STACK_TRACE_FRAMES;

	/**
	 * 
	 */
	public LIdentifyCallerException() {
		super();
	}

	/**
	 * @param numStackTraceElements
	 */
	public LIdentifyCallerException(int startStackTraceElements, int numStackTraceElements) {
		super();
		this.startStackTraceElements = startStackTraceElements;
		this.numStackTraceElements = numStackTraceElements;
	}

	/**
	 * @see java.lang.Throwable#getStackTrace()
	 */
	@Override
	public StackTraceElement[] getStackTrace() {
		StackTraceElement[] result;
		StackTraceElement[] given = super.getStackTrace();
		if (given.length >= numStackTraceElements) {
			result = new StackTraceElement[numStackTraceElements];
			for (int i = 0; i < numStackTraceElements; i++) {
				result[i] = given[i + (startStackTraceElements - 1)];
			}
			return result;
		} else {
			return given;
		}
	}

	/**
	 * @see java.lang.Throwable#printStackTrace(java.io.PrintStream)
	 */
	@Override
	public void printStackTrace(PrintStream writer) {
		for (StackTraceElement e : getStackTrace()) {
			writer.print("\t" + e);
		}
		writer.flush();
	}

	/**
	 * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
	 */
	@Override
	public void printStackTrace(PrintWriter writer) {
		for (StackTraceElement e : getStackTrace()) {
			writer.print("\t" + e);
		}
		writer.flush();
	}

}