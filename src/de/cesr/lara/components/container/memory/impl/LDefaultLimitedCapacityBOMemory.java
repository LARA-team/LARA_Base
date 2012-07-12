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

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.container.LaraCapacityManager;
import de.cesr.lara.components.container.exceptions.LRetrieveException;
import de.cesr.lara.components.container.memory.LaraBOMemory;

/**
 * @param <BOType>
 *            the type of behavioural options this memory may store
 */
public class LDefaultLimitedCapacityBOMemory<BOType extends LaraBehaviouralOption<?, ? extends BOType>>
		extends LDefaultLimitedCapacityMemory<BOType> implements
		LaraBOMemory<BOType> {

	/**
	 * @param capacityManager
	 */
	public LDefaultLimitedCapacityBOMemory(
			LaraCapacityManager<BOType> capacityManager) {
		super(capacityManager);
	}

	/**
	 * @param capacityManager
	 * @param initialCapacity
	 */
	public LDefaultLimitedCapacityBOMemory(
			LaraCapacityManager<BOType> capacityManager, int initialCapacity) {
		super(capacityManager, initialCapacity);
	}

	/**
	 * @param capacityManager
	 * @param initialCapacity
	 * @param name
	 *            the memory's name
	 */
	public LDefaultLimitedCapacityBOMemory(
			LaraCapacityManager<BOType> capacityManager, int initialCapacity,
			String name) {
		super(capacityManager, initialCapacity, name);
	}

	/**
	 * @param capacityManager
	 * @param name
	 *            the memory's name
	 */
	public LDefaultLimitedCapacityBOMemory(
			LaraCapacityManager<BOType> capacityManager, String name) {
		super(capacityManager, name);
	}

	/**
	 * @see de.cesr.lara.components.container.memory.LaraBOMemory#memoriseAll(java.util.Set)
	 */
	@Override
	public void memoriseAll(Set<BOType> bos) {
		for (BOType bo : bos) {
			memorize(bo);
		}
	}

	/**
	 * @see de.cesr.lara.components.container.memory.LaraBOMemory#recallAllMostRecent()
	 */
	@Override
	public Set<BOType> recallAllMostRecent() throws LRetrieveException {
		Set<BOType> all = new HashSet<BOType>();
		for (String key : this.getAllPropertyKeys()) {
			all.add(this.recall(key));
		}
		return all;
	}

}