/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 19.05.2010
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
 * @param <BO>
 * 
 */
public class LDefaultLimitedCapacityOverwriteBoMemory<BO extends LaraBehaviouralOption>
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
		for (BO bo : bos) {
			memorize(bo);
		}
	}

	@Override
	public Set<BO> recallAllMostRecent() throws LRetrieveException {
		Set<BO> all = new HashSet<BO>();
		for (String key : this.getAllPropertyKeys()) {
			all.add(this.recall(key));
		}
		return all;
	}
}
