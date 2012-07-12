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
package de.cesr.lara.components.util.impl;

import org.apache.log4j.Logger;

import cern.jet.random.Uniform;
import cern.jet.random.engine.RandomEngine;
import de.cesr.lara.components.util.exceptions.LIdentifyCallerException;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;

/**
 *  The LUniformController logs every random number that is requested - it
 *  also logs the call's origin by throwing Exceptions.
 *  This way it is possible to check whether several model runs with same random seed
 *  have equal behaviour regarding random number generation (i.e. it may be tested whether
 *  there is a hidden call for a random generator in one version that should not be).
 *  
 *  NOTE: Also disabling / enabling logging could make a difference!
 */
/**
 * 
 */
public class LUniformController extends Uniform {

	static private Logger logger = Log4jLogger.getLogger(LRandomService.class);
	/**
	 * Logger
	 */
	static private Logger logger_st = Log4jLogger
			.getLogger(LRandomService.class.getName() + ".stacktrace");

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param arg2
	 */
	public LUniformController(RandomEngine arg2) {
		super(arg2);
	}

	@Override
	public boolean nextBoolean() {
		boolean rand = super.nextBoolean();
		logger.debug("Random number: " + rand);
		logger_st.error("Stack trace: ", new LIdentifyCallerException());
		return rand;
	}

	@Override
	public double nextDouble() {
		double rand = super.nextDouble();
		logger.debug("Random number: " + rand);
		logger_st.error("Stack trace: ", new LIdentifyCallerException());
		return rand;
	}

	/**
	 * Returns a uniformly distributed random number in the open interval
	 * <tt>(from,to)</tt> (excluding <tt>from</tt> and <tt>to</tt>). Pre
	 * conditions: <tt>from &lt;= to</tt>.
	 */
	@Override
	public double nextDoubleFromTo(double from, double to) {
		double rand = super.nextDoubleFromTo(from, to);
		logger.debug("Random number: " + rand);
		logger_st.error("Stack trace: ", new LIdentifyCallerException());
		return rand;
	}

	/**
	 * Returns a uniformly distributed random number in the open interval
	 * <tt>(from,to)</tt> (excluding <tt>from</tt> and <tt>to</tt>). Pre
	 * conditions: <tt>from &lt;= to</tt>.
	 */
	@Override
	public float nextFloatFromTo(float from, float to) {
		float rand = super.nextFloatFromTo(from, to);
		logger.debug("Random number: " + rand);
		logger_st.error("Stack trace: ", new LIdentifyCallerException());
		return rand;
	}

	/**
	 * Returns a uniformly distributed random number in the closed interval
	 * <tt>[min,max]</tt> (including <tt>min</tt> and <tt>max</tt>).
	 */
	@Override
	public int nextInt() {
		int rand = super.nextInt();
		logger.debug("Random number: " + rand);
		logger_st.error("Stack trace: ", new LIdentifyCallerException());
		return rand;
	}

	/**
	 * @see cern.jet.random.Uniform#nextIntFromTo(int, int)
	 */
	@Override
	public int nextIntFromTo(int from, int to) {
		int rand = super.nextIntFromTo(from, to);
		logger.debug("Random number: " + rand);
		logger_st.error("Stack trace: ", new LIdentifyCallerException());
		return rand;
	}

	/**
	 * Returns a uniformly distributed random number in the closed interval
	 * <tt>[from,to]</tt> (including <tt>from</tt> and <tt>to</tt>). Pre
	 * conditions: <tt>from &lt;= to</tt>.
	 */
	@Override
	public long nextLongFromTo(long from, long to) {
		long rand = super.nextLongFromTo(from, to);
		logger.debug("Random number: " + rand);
		logger_st.error("Stack trace: ", new LIdentifyCallerException());
		return rand;
	}

}