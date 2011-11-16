/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 16.02.2011
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
