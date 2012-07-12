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

import cern.jet.random.Normal;
import cern.jet.random.engine.RandomEngine;
import de.cesr.lara.components.util.exceptions.LIdentifyCallerException;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;

/**
 * 
 */
public class LNormalController extends Normal {

	static private Logger logger = Log4jLogger.getLogger(LRandomService.class);

	/**
	 * Logger
	 */
	static private Logger logger_st = Log4jLogger
			.getLogger(LRandomService.class.getName() + ".stacktrace");

	/**
	 * 
	 */
	private static final long serialVersionUID = -6348861089817698692L;

	public static double staticNextDouble(double mean, double std) {
		double rand = Normal.staticNextDouble(mean, std);
		logger.debug("Normal static (mean: " + mean + "/std: " + std
				+ ")> Random number: " + rand);
		logger_st.error("Stack trace: ", new LIdentifyCallerException());
		return rand;
	}

	/**
	 * @param mean
	 * @param std
	 * @param engine
	 */
	public LNormalController(double mean, double std, RandomEngine engine) {
		super(mean, std, engine);
	}

	@Override
	public double nextDouble() {
		double rand = super.nextDouble();
		logger.debug("Normal > Random number: " + rand);
		logger_st.error("Stack trace: ", new LIdentifyCallerException());
		return rand;
	}

	/**
	 * Returns a uniformly distributed random number in the open interval
	 * <tt>(from,to)</tt> (excluding <tt>from</tt> and <tt>to</tt>). Pre
	 * conditions: <tt>from &lt;= to</tt>.
	 */
	@Override
	public double nextDouble(double mean, double std) {
		double rand = super.nextDouble(mean, std);
		logger.debug("Normal (mean: " + mean + "/std: " + std
				+ ")> Random number: " + rand);
		logger_st.error("Stack trace: ", new LIdentifyCallerException());
		return rand;
	}

}