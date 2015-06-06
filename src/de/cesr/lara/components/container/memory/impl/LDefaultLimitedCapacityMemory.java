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


import java.util.Iterator;

import org.apache.log4j.Logger;

import de.cesr.lara.components.LaraProperty;
import de.cesr.lara.components.container.LaraCapacityManageableContainer;
import de.cesr.lara.components.container.LaraCapacityManagementView;
import de.cesr.lara.components.container.LaraCapacityManager;
import de.cesr.lara.components.container.exceptions.LContainerFullException;
import de.cesr.lara.components.model.LaraModel;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;


/**
 * @param <PropertyType>
 */
public class LDefaultLimitedCapacityMemory<PropertyType extends LaraProperty<? extends PropertyType, ?>> extends
		LDefaultMemory<PropertyType> implements LaraCapacityManageableContainer<PropertyType> {

	/**
	 * Logger
	 */
	static private Logger						logger						=
																					Log4jLogger
																							.getLogger(LDefaultLimitedCapacityMemory.class);

	/**
	 * The memory's initial capacity in amount of entries
	 */
	public static final int						DEFAULT_INITIAL_CAPACITY	= 50;

	private int									capacity					= DEFAULT_INITIAL_CAPACITY;

	private LaraCapacityManager<PropertyType>	capacityManager				= null;

	/**
	 * @param lmodel 
	 * @param capacityManager
	 */
	public LDefaultLimitedCapacityMemory(LaraModel lmodel,
			LaraCapacityManager<PropertyType> capacityManager) {
		super(lmodel);
		this.capacityManager = capacityManager;
	}

	/**
	 * @param lmodel 
	 * @param capacityManager
	 * @param initialCapacity
	 */
	public LDefaultLimitedCapacityMemory(LaraModel lmodel,
			LaraCapacityManager<PropertyType> capacityManager,
			int initialCapacity) {
		this(lmodel, capacityManager);
		this.capacity = initialCapacity;
	}

	/**
	 * @param lmodel 
	 * @param capacityManager
	 * @param initialCapacity
	 * @param name
	 *        the memory's name
	 */
	public LDefaultLimitedCapacityMemory(LaraModel lmodel,
			LaraCapacityManager<PropertyType> capacityManager,
			int initialCapacity,
			String name) {
		this(lmodel, capacityManager, name);
		this.capacity = initialCapacity;
	}

	/**
	 * @param lmodel 
	 * @param capacityManager
	 * @param name
	 *        the memory's name
	 */
	public LDefaultLimitedCapacityMemory(LaraModel lmodel,
			LaraCapacityManager<PropertyType> capacityManager, String name) {
		super(lmodel, name);
		this.capacityManager = capacityManager;
	}

	/**
	 * @see de.cesr.lara.components.container.memory.impl.LDefaultMemory#getCapacity()
	 */
	@Override
	public int getCapacity() {
		return capacity;
	}

	/**
	 * @see de.cesr.lara.components.container.LaraCapacityManageableContainer#getCapacityManagementView()
	 */
	@Override
	public LaraCapacityManagementView<PropertyType> getCapacityManagementView() {
		return new LaraCapacityManagementView<PropertyType>() {

			@Override
			public Iterator<PropertyType> iterator() {
				return LDefaultLimitedCapacityMemory.this.iterator();
			}

			@Override
			public void remove(PropertyType item) {
				LDefaultLimitedCapacityMemory.this.forget(item);
			}
		};
	}

	/**
	 * @see de.cesr.lara.components.container.LaraCapacityManageableContainer#getCapacityManager()
	 */
	@Override
	public LaraCapacityManager<PropertyType> getCapacityManager() {
		return capacityManager;
	}

	/**
	 * @see de.cesr.lara.components.container.memory.impl.LDefaultMemory#isFull()
	 */
	@Override
	public boolean isFull() {
		return capacity == UNLIMITED_CAPACITY ? false : getSize() == capacity;
	}

	/**
	 * @see de.cesr.lara.components.container.memory.impl.LDefaultMemory#memorize(de.cesr.lara.components.LaraProperty)
	 */
	@Override
	public void memorize(PropertyType propertyToMemorize) {
		if (isFull()) {
			if (!capacityManager.freeSpace(this.getCapacityManagementView())) {
				// <- LOGGING
				logger.error("Memory could free space!");
				// LOGGING ->
				throw new LContainerFullException("Memory could free space!");
			}
		}
		super.memorize(propertyToMemorize);
	}

	/**
	 * @see de.cesr.lara.components.container.memory.impl.LDefaultMemory#memorize(de.cesr.lara.components.LaraProperty,
	 *      int)
	 */
	@Override
	public void memorize(PropertyType propertyToMemorize, int retentionTime) {
		if (isFull()) {
			if (!capacityManager.freeSpace(this.getCapacityManagementView())) {
				// <- LOGGING
				logger.error("Memory could free space!");
				// LOGGING ->
				throw new LContainerFullException("Memory could free space!");
			}
		}
		super.memorize(propertyToMemorize, retentionTime);
	}

	/**
	 * @see de.cesr.lara.components.container.LaraCapacityManageableContainer#setCapacity(int)
	 */
	@Override
	public boolean setCapacity(int capacity) {
		if (capacity >= 0) {
			if (this.capacity == UNLIMITED_CAPACITY || capacity < this.capacity) {
				int sizeBefore, sizeAfter;
				do {
					sizeBefore = getSize();
					capacityManager.freeSpace(this.getCapacityManagementView());
					sizeAfter = getSize();
				} while (sizeBefore != sizeAfter && getSize() > capacity);
			}

		} else if (capacity != UNLIMITED_CAPACITY) {
			return false;
		}
		this.capacity = capacity;
		return true;
	}

	/**
	 * @see de.cesr.lara.components.container.LaraCapacityManageableContainer#setCapacityManager(de.cesr.lara.components.container.LaraCapacityManager)
	 */
	@Override
	public void setCapacityManager(LaraCapacityManager<PropertyType> manager) {
		this.capacityManager = manager;
	}

}
