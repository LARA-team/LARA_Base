/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 29.09.2010
 */
package de.cesr.lara.components.util.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * This exception is used to produce a stack trace and log it.
 */
public class LIdentifyCallerException extends LaraException {

	private static final int NUM_STACK_TRACE_FRAMES = 6;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @see java.lang.Throwable#getStackTrace()
	 */
	@Override
	public StackTraceElement[] getStackTrace() {
		StackTraceElement[] result;
		StackTraceElement[] given = super.getStackTrace();
		if (given.length >= NUM_STACK_TRACE_FRAMES) {
			result = new StackTraceElement[NUM_STACK_TRACE_FRAMES];
			for (int i = 0; i < NUM_STACK_TRACE_FRAMES; i++) {
				result[i] = given[i];
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
