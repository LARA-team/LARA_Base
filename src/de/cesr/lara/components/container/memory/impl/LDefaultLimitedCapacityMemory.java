package de.cesr.lara.components.container.memory.impl;

import java.util.Iterator;

import de.cesr.lara.components.LaraProperty;
import de.cesr.lara.components.container.LaraCapacityManageable;
import de.cesr.lara.components.container.LaraCapacityManagementView;
import de.cesr.lara.components.container.LaraCapacityManager;

/**
 * @param <PropertyType>
 */
public class LDefaultLimitedCapacityMemory<PropertyType extends LaraProperty<?>>
		extends LDefaultMemory<PropertyType> implements
		LaraCapacityManageable<PropertyType> {

	/**
	 * The memory's initial capacity in amount of entries
	 */
	public static final int DEFAULT_INITIAL_CAPACITY = 50;

	private int capacity = DEFAULT_INITIAL_CAPACITY;

	private LaraCapacityManager<PropertyType> capacityManager = null;

	/**
	 * @param capacityManager
	 */
	public LDefaultLimitedCapacityMemory(
			LaraCapacityManager<PropertyType> capacityManager) {
		super();
		this.capacityManager = capacityManager;
	}

	/**
	 * @param capacityManager
	 * @param initialCapacity
	 */
	public LDefaultLimitedCapacityMemory(
			LaraCapacityManager<PropertyType> capacityManager,
			int initialCapacity) {
		this(capacityManager);
		this.capacity = initialCapacity;
	}

	/**
	 * @param capacityManager
	 * @param initialCapacity
	 * @param name
	 *            the memory's name
	 */
	public LDefaultLimitedCapacityMemory(
			LaraCapacityManager<PropertyType> capacityManager,
			int initialCapacity, String name) {
		this(capacityManager, name);
		this.capacity = initialCapacity;
	}

	/**
	 * @param capacityManager
	 * @param name
	 *            the memory's name
	 */
	public LDefaultLimitedCapacityMemory(
			LaraCapacityManager<PropertyType> capacityManager, String name) {
		super(name);
		this.capacityManager = capacityManager;
	}

	@Override
	public int getCapacity() {
		return capacity;
	}

	/**
	 * TODO why is this necessary?? (SH)
	 * 
	 * @see de.cesr.lara.components.container.LaraCapacityManageable#getCapacityManagementView()
	 */
	@Override
	public LaraCapacityManagementView<PropertyType> getCapacityManagementView() {
		return new LaraCapacityManagementView<PropertyType>() {

			@Override
			public int getCapacity() {
				return LDefaultLimitedCapacityMemory.this.getCapacity();
			}

			@Override
			public int getSize() {
				return LDefaultLimitedCapacityMemory.this.getSize();
			}

			@Override
			public boolean isEmpty() {
				return LDefaultLimitedCapacityMemory.this.isEmpty();
			}

			@Override
			public boolean isFull() {
				return LDefaultLimitedCapacityMemory.this.isFull();
			}

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

	@Override
	public LaraCapacityManager<PropertyType> getCapacityManager() {
		return capacityManager;
	}

	@Override
	public boolean isFull() {
		return capacity == UNLIMITED_CAPACITY ? false : getSize() == capacity;
	}

	@Override
	public void memorize(PropertyType propertyToMemorize) {
		if (isFull()) {
			if (!capacityManager.manage(this)) {
				return;
			}
		}
		super.memorize(propertyToMemorize);
	}

	@Override
	public void memorize(PropertyType propertyToMemorize, int retentionTime) {
		if (isFull()) {
			if (!capacityManager.manage(this)) {
				return;
			}
		}
		super.memorize(propertyToMemorize, retentionTime);
	}

	@Override
	public boolean setCapacity(int capacity) {

		if (capacity >= 0) {
			// TODO Is this implementation ok?! Discuss! (ME)
			if (this.capacity == UNLIMITED_CAPACITY || capacity < this.capacity) {
				LaraCapacityManagementView<PropertyType> view = getCapacityManagementView();
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

}
