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
package de.cesr.lara.components.container.storage.impl;

import java.util.Iterator;

import de.cesr.lara.components.LaraProperty;
import de.cesr.lara.components.container.LaraCapacityManageableContainer;
import de.cesr.lara.components.container.LaraCapacityManagementView;
import de.cesr.lara.components.container.LaraCapacityManager;
import de.cesr.lara.components.container.storage.LaraStorageListener;
import de.cesr.lara.components.container.storage.LaraStorageListener.StorageEvent;

/**
 * TODO implement observer management for other events than AUTO_REMOVED!
 * 
 * @param <PropertyType>
 */
public class LDefaultLimitedCapacityStorage<PropertyType extends LaraProperty<PropertyType, ?>>
		extends LDefaultStorage<PropertyType> implements
		LaraCapacityManageableContainer<PropertyType> {

	/**
	 * The storage's initial capacity in amount of entries
	 */
	public static final int DEFAULT_INITIAL_CAPACITY = 50;

	private int capacity = DEFAULT_INITIAL_CAPACITY;

	private LaraCapacityManager<PropertyType> capacityManager = null;

	/**
	 * @param capacityManager
	 */
	public LDefaultLimitedCapacityStorage(
			LaraCapacityManager<PropertyType> capacityManager) {
		super();
		this.capacityManager = capacityManager;
	}

	/**
	 * @param capacityManager
	 * @param initialCapacity
	 */
	public LDefaultLimitedCapacityStorage(
			LaraCapacityManager<PropertyType> capacityManager,
			int initialCapacity) {
		this(capacityManager);
		this.capacity = initialCapacity;
	}

	/**
	 * @see de.cesr.lara.components.container.storage.impl.LDefaultStorage#getCapacity()
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

			/**
			 * @see de.cesr.lara.components.container.LaraCapacityManagementView#remove(de.cesr.lara.components.LaraProperty)
			 */
			@Override
			public void remove(PropertyType item) {
				if (propListenersContainsEventKey(StorageEvent.PROPERTY_AUTO_REMOVED)) {
					for (LaraStorageListener listener : getPropertyListeners(LaraStorageListener.StorageEvent.PROPERTY_AUTO_REMOVED)) {
						listener.storageEventOccured(
								LaraStorageListener.StorageEvent.PROPERTY_AUTO_REMOVED,
								item);
					}
				}
				LDefaultLimitedCapacityStorage.this.remove(item);
			}

			/**
			 * @see de.cesr.lara.components.container.LaraCapacityManagementView#iterator()
			 */
			@Override
			public Iterator<PropertyType> iterator() {
				return LDefaultLimitedCapacityStorage.this.iterator();
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
	 * @see de.cesr.lara.components.container.storage.impl.LDefaultStorage#isFull()
	 */
	@Override
	public boolean isFull() {
		return capacity == UNLIMITED_CAPACITY ? false : getSize() == capacity;
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

	/**
	 * @see de.cesr.lara.components.container.storage.impl.LDefaultStorage#store(de.cesr.lara.components.LaraProperty)
	 */
	@Override
	public void store(PropertyType propertyToStore) {
		if (isFull()) {
			if (!capacityManager.freeSpace(this.getCapacityManagementView())) {
				return;
			}
		}
		super.store(propertyToStore);
	}

}
