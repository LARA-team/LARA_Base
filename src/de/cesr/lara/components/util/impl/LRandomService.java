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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import cern.jet.random.AbstractDistribution;
import cern.jet.random.Normal;
import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import de.cesr.lara.components.util.LaraRandom;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;

/**
 * Uses the {@link MersenneTwister} as generator.
 * 
 * TODO substitute by URaNuS (or clear generators)
 * TODO document (SH) TODO methods to check if given distribution name is of
 * given class
 */
public class LRandomService implements LaraRandom {

	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger.getLogger(LRandomService.class);

	private Map<String, AbstractDistribution> distributions;

	private Map<String, RandomEngine> generators;

	private int seed;

	/**
	 * Initialise a new instance with the given random seed.
	 * 
	 * @param seed
	 */
	public LRandomService(int seed) {
		this.seed = seed;
		generators = new HashMap<String, RandomEngine>();
		generators.put(null, new MersenneTwister(seed));

		distributions = new HashMap<String, AbstractDistribution>();

		if (logger.isDebugEnabled()) {
			distributions.put(UNIFORM_DEFAULT, new LUniformController(
					generators.get(null)));
		} else {
			distributions.put(UNIFORM_DEFAULT,
					new Uniform(generators.get(null)));
		}
	}

	/**
	 * @see de.cesr.lara.components.util.LaraRandom#createNormal(double, double)
	 */
	@Override
	public Normal createNormal(double mean, double std) {
		if (isDebugEnabled()) {
			distributions.put(NORMAL_DEFAULT, new LNormalController(mean, std,
					generators.get(null)));
		} else {
			distributions.put(NORMAL_DEFAULT,
					new Normal(mean, std, generators.get(null)));
		}

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Create normal distribution with mean " + mean
					+ " and std.dev. " + std + "(default Generator: "
					+ generators.get(null).getClass().getName() + ")");
		}
		// LOGGING ->

		return (Normal) distributions.get(NORMAL_DEFAULT);
	}

	/**
	 * @see de.cesr.lara.components.util.LaraRandom#getCustomNormal(double,
	 *      double, java.lang.String)
	 */
	@Override
	public Normal getCustomNormal(double mean, double std, String name) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Create custom normal distribution with mean " + mean
					+ " and std.dev. " + std + "(Generator: "
					+ generators.get(name).getClass().getName() + ")");
		}
		// LOGGING ->

		if (isDebugEnabled()) {
			return new LNormalController(mean, std, generators.get(name));
		} else {
			return new Normal(mean, std, generators.get(name));
		}
	}

	/**
	 * @see de.cesr.lara.components.util.LaraRandom#getDistribution(java.lang.String)
	 */
	@Override
	public AbstractDistribution getDistribution(String name) {
		return distributions.get(name);
	}

	/**
	 * @see de.cesr.lara.components.util.LaraRandom#getGenerator(java.lang.String)
	 */
	@Override
	public RandomEngine getGenerator(String name) {
		return generators.get(name);
	}

	/**
	 * @see de.cesr.lara.components.util.LaraRandom#getNormal()
	 */
	@Override
	public Normal getNormal() {
		if (!distributions.containsKey(NORMAL_DEFAULT)) {
			logger.warn("Normal distributions has not been created!");
		}
		return (Normal) distributions.get(NORMAL_DEFAULT);
	}

	/**
	 * @see de.cesr.lara.components.util.LaraRandom#getSeed()
	 */
	@Override
	public int getSeed() {
		return this.seed;
	}

	/**
	 * @see de.cesr.lara.components.util.LaraRandom#getUniform()
	 */
	@Override
	public Uniform getUniform() {
		return (Uniform) distributions.get(UNIFORM_DEFAULT);
	}

	/**
	 * @see de.cesr.lara.components.util.LaraRandom#isDebugEnabled()
	 */
	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	/**
	 * @see de.cesr.lara.components.util.LaraRandom#registerDistribution(cern.jet.random.AbstractDistribution,
	 *      java.lang.String)
	 */
	@Override
	public void registerDistribution(AbstractDistribution dist, String name) {
		distributions.put(name, dist);
	}

	/**
	 * @see de.cesr.lara.components.util.LaraRandom#registerGenerator(java.lang.String,
	 *      cern.jet.random.engine.RandomEngine)
	 */
	@Override
	public void registerGenerator(String name, RandomEngine generator) {
		generators.put(name, generator);
	}

	/**
	 * @see de.cesr.lara.components.util.LaraRandom#setSeed(int)
	 */
	@Override
	public void setSeed(int seed) {
		this.seed = seed;
		invalidateDistributions();
		generators.put(null, new MersenneTwister(seed));
		if (logger.isDebugEnabled()) {
			distributions.put(UNIFORM_DEFAULT, new LUniformController(
					generators.get(null)));
		} else {
			distributions.put(UNIFORM_DEFAULT,
					new Uniform(generators.get(null)));
		}
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Set seed: " + this.seed + "(default generator: "
					+ generators.get(null).getClass().getName() + ")");
		}
		// LOGGING ->

	}

	private void invalidateDistributions() {
		distributions.clear();
	}
	
}