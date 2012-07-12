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
package de.cesr.lara.components.container.memory.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.container.LaraCapacityManager;
import de.cesr.lara.components.container.exceptions.LRetrieveException;
import de.cesr.lara.components.container.memory.LaraBOMemory;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;

/**
 * The {@link LaraBOMemory} version of
 * {@link LDefaultLimitedCapacityOverwriteMemory}
 * 
 * @param <BO>
 *            the type of behavioural options to manage
 * 
 */
public class LDefaultLimitedCapacityOverwriteBoMemory<BO extends LaraBehaviouralOption<?, BO>>
		extends LDefaultLimitedCapacityOverwriteMemory<BO> implements
		LaraBOMemory<BO> {

	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger
			.getLogger(LDefaultLimitedCapacityOverwriteBoMemory.class);

	/**
	 * @param capacityManager
	 */
	public LDefaultLimitedCapacityOverwriteBoMemory(
			LaraCapacityManager<BO> capacityManager) {
		super(capacityManager);
	}

	/**
	 * @param capacityManager
	 * @param initialCapacity
	 */
	public LDefaultLimitedCapacityOverwriteBoMemory(
			LaraCapacityManager<BO> capacityManager, int initialCapacity) {
		super(capacityManager, initialCapacity);
	}

	/**
	 * @param capacityManager
	 * @param initialCapacity
	 * @param name
	 *            the memory's name
	 */
	public LDefaultLimitedCapacityOverwriteBoMemory(
			LaraCapacityManager<BO> capacityManager, int initialCapacity,
			String name) {
		super(capacityManager, initialCapacity, name);
	}

	/**
	 * @param capacityManager
	 * @param name
	 *            the memory's name
	 */
	public LDefaultLimitedCapacityOverwriteBoMemory(
			LaraCapacityManager<BO> capacityManager, String name) {
		super(capacityManager, name);
	}

	@Override
	public void memoriseAll(Set<BO> bos) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Memorise BOs: " + bos);
		}
		// LOGGING ->

		for (BO bo : bos) {
			memorize(bo);
		}
	}

	@Override
	public Set<BO> recallAllMostRecent() throws LRetrieveException {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Recall the most recent entry for every key.");
		}
		// LOGGING ->

		Set<BO> all = new HashSet<BO>();
		for (String key : this.getAllPropertyKeys()) {
			all.add(this.recall(key));
		}
		return all;
	}
}
