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
package de.cesr.lara.components.util;

import cern.jet.random.AbstractDistribution;
import cern.jet.random.Normal;
import cern.jet.random.Uniform;
import cern.jet.random.engine.RandomEngine;

/**
 * 
 */
public interface LaraRandom {

	/**
	 * Identifier for default normal stream (not necessarily standard!)
	 */
	public final String NORMAL_DEFAULT = "Normal default";

	/**
	 * Identifier for default uniform stream
	 */
	public final String UNIFORM_DEFAULT = "Uniform default";

	/**
	 * Create a normal distribution with the given parameters using the default
	 * random generator.
	 * 
	 * @param mean
	 * @param stdDev
	 * @return normal distribution with the given parameters
	 */
	public Normal createNormal(double mean, double stdDev);

	/**
	 * @param mean
	 * @param std
	 * @param name
	 *            name of the generator to use from stored generators
	 * 
	 * @return normal distribution
	 */
	public Normal getCustomNormal(double mean, double std, String name);

	/**
	 * Returns the {@link AbstractDistribution} that is registered for the given
	 * name.
	 * 
	 * @param name
	 * @return registered distribution
	 */
	public AbstractDistribution getDistribution(String name);

	/**
	 * @param name
	 * @return random engine that is associated with the given String
	 */
	public RandomEngine getGenerator(String name);

	/**
	 * Returns the {@link Normal} distribution that was created at last
	 * (therefore, mean and standard deviation is not defined). Equivalent to
	 * <code>LaraRandom#getDistribution(LaraRandom.NORMAL_DEFAULT)</code>.
	 * Returns null if no normal distribution has been created yet.
	 * 
	 * @return latest created normal distribution
	 */
	public Normal getNormal();

	/**
	 * Get the seed currently used for the default distributions.
	 * 
	 * @return current seed
	 */
	public int getSeed();

	/**
	 * Return the default uniform distribution. Equivalent to
	 * <code>LaraRandom#getDistribution(LaraRandom.UNIFORM_DEFAULT)</code>.
	 * 
	 * @return the default uniform distribution
	 */
	public Uniform getUniform();

	/**
	 * Checks if the logger of LRandomService is set to debug.
	 * 
	 * @return true if debug is enabled.
	 */
	public boolean isDebugEnabled();

	/**
	 * Registers a custom {@link AbstractDistribution} at the given name.
	 * 
	 * @param dist
	 * @param name
	 */
	public void registerDistribution(AbstractDistribution dist, String name);

	/**
	 * @param name
	 *            to associte the given generator with
	 * @param generator
	 *            to register
	 */
	public void registerGenerator(String name, RandomEngine generator);

	/**
	 * Sets the seed for all random number generators. If this method is called,
	 * a new random number generator is created, all registered distributions
	 * (also customly registered) are deleted and the default distributions are
	 * renewed.
	 * 
	 * @param seed
	 */
	public void setSeed(int seed);

}