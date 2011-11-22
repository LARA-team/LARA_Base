/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 22.03.2010
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
public class LDefaultLimitedCapacityBOMemory<BOType extends LaraBehaviouralOption<?, ?>> extends
		LDefaultLimitedCapacityMemory<BOType> implements LaraBOMemory<BOType> {

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
