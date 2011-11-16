/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * 
 */
package de.cesr.lara.components.container.storage.impl;

import java.util.Iterator;

import de.cesr.lara.components.LaraProperty;
import de.cesr.lara.components.container.LaraCapacityManageable;
import de.cesr.lara.components.container.LaraCapacityManagementView;
import de.cesr.lara.components.container.LaraCapacityManager;
import de.cesr.lara.components.container.storage.LaraStorageListener;
import de.cesr.lara.components.container.storage.LaraStorageListener.StorageEvent;

/**
 * TODO implement observer management for other events than AUTO_REMOVED!
 * 
 * @param <PropertyType>
 */
public class LDefaultLimitedCapacityStorage<PropertyType extends LaraProperty<?>>
		extends LDefaultStorage<PropertyType> implements
		LaraCapacityManageable<PropertyType> {

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

	@Override
	public int getCapacity() {
		return capacity;
	}

	@Override
	public LaraCapacityManagementView<PropertyType> getCapacityManagementView() {
		return new LaraCapacityManagementView<PropertyType>() {

			@Override
			public int getCapacity() {
				return LDefaultLimitedCapacityStorage.this.getCapacity();
			}

			@Override
			public int getSize() {
				return LDefaultLimitedCapacityStorage.this.getSize();
			}

			@Override
			public boolean isEmpty() {
				return LDefaultLimitedCapacityStorage.this.isEmpty();
			}

			@Override
			public boolean isFull() {
				return LDefaultLimitedCapacityStorage.this.isFull();
			}

			@Override
			public Iterator<PropertyType> iterator() {
				return LDefaultLimitedCapacityStorage.this.iterator();
			}

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
		};
	}

	@Override
	public LaraCapacityManager<PropertyType> getCapacityManager() {
		return capacityManager;
	}

	@Override
	public boolean isFull() {
		return capacity == UNLIMITED_CAPACITY ? false : getSize() == capacity;
	}

	@Override
	public boolean setCapacity(int capacity) {

		if (capacity >= 0) {
			// TODO Is this implementation ok?! Discuss! (ME)
			if (this.capacity == UNLIMITED_CAPACITY || capacity < this.capacity) {
				int sizeBefore, sizeAfter;
				do {
					sizeBefore = getSize();
					capacityManager.manage(this);
					sizeAfter = getSize();
				} while (sizeBefore != sizeAfter && getSize() > capacity);
			}

		} else if (capacity != UNLIMITED_CAPACITY) {
			return false;
		}
		this.capacity = capacity;
		return true;
	}

	@Override
	public void setCapacityManager(LaraCapacityManager<PropertyType> manager) {
		this.capacityManager = manager;
	}

	@Override
	public void store(PropertyType propertyToStore) {
		if (isFull()) {
			if (!capacityManager.manage(this)) {
				return;
			}
		}
		super.store(propertyToStore);
	}

}
