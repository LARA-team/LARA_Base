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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;
import org.apache.log4j.Logger;

import de.cesr.lara.components.LaraProperty;
import de.cesr.lara.components.container.LaraCapacityManagementView;
import de.cesr.lara.components.container.LaraCapacityManager;
import de.cesr.lara.components.container.exceptions.LContainerFullException;
import de.cesr.lara.components.container.exceptions.LInvalidTimestampException;
import de.cesr.lara.components.container.exceptions.LRemoveException;
import de.cesr.lara.components.container.exceptions.LRetrieveException;
import de.cesr.lara.components.container.memory.LaraMemoryListener;
import de.cesr.lara.components.container.memory.LaraMemoryListener.MemoryEvent;
import de.cesr.lara.components.container.memory.LaraOverwriteMemory;
import de.cesr.lara.components.container.storage.LaraOverwriteStorage;
import de.cesr.lara.components.container.storage.LaraStorageListener;
import de.cesr.lara.components.container.storage.impl.LDefaultSimpleStorage;
import de.cesr.lara.components.util.impl.LCapacityManagers;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;

/**
 * TODO check if an interface split is appropriate (because step-related queries
 * are not applicable to this kind of memory) (SH)
 * 
 * TODO check: observation mechanism: at refresh, first input and then check
 * capacity....
 * 
 * @author Sascha Holzhauer
 * @date 18.05.2010
 * 
 * @param <PropertyType>
 */
public class LDefaultLimitedCapacityOverwriteMemory<PropertyType extends LaraProperty<PropertyType, ?>>
		implements LaraOverwriteMemory<PropertyType>, LaraStorageListener {

	/**
	 * The memory's initial capacity in amount of entries
	 */
	public static final int DEFAULT_INITIAL_CAPACITY = 50;

	private static int counter = 0;

	private int capacity = DEFAULT_INITIAL_CAPACITY;

	/**
	 * Logger
	 */
	private Logger logger;
	static private boolean logindividual =
			Log4jLogger.getLogger(LDefaultLimitedCapacityOverwriteMemory.class + ".individual").isDebugEnabled();

	private String name;

	// observer management:
	private final MultiMap<LaraMemoryListener.MemoryEvent, LaraMemoryListener> propertyListeners = new MultiHashMap<LaraMemoryListener.MemoryEvent, LaraMemoryListener>();

	private LaraOverwriteStorage<PropertyType> storage;

	/**
	 * 
	 */
	public LDefaultLimitedCapacityOverwriteMemory() {
		this(LCapacityManagers.<PropertyType> makeFIFO(), UNLIMITED_CAPACITY,
				"memory" + counter++);
	}

	/**
	 * @param capacityManager
	 */
	public LDefaultLimitedCapacityOverwriteMemory(
			LaraCapacityManager<PropertyType> capacityManager) {
		this(capacityManager, UNLIMITED_CAPACITY, "memory" + counter++);
	}

	/**
	 * @param capacityManager
	 * @param capacity
	 */
	public LDefaultLimitedCapacityOverwriteMemory(
			LaraCapacityManager<PropertyType> capacityManager, int capacity) {
		this(capacityManager, capacity, "memory" + counter++);
	}

	/**
	 * @param capacityManager
	 * @param capacity
	 * @param name
	 *            the memory's name
	 */
	public LDefaultLimitedCapacityOverwriteMemory(
			LaraCapacityManager<PropertyType> capacityManager, int capacity,
			String name) {
		this.name = name;
		this.capacity = capacity;
		this.logger = Log4jLogger
				.getLogger(LDefaultLimitedCapacityOverwriteMemory.class + (logindividual ? "." + getName() : ""));
		this.storage = createBackingStorage(capacityManager);
	}

	/**
	 * @param capacityManager
	 * @param name
	 *            the memory's name
	 */
	public LDefaultLimitedCapacityOverwriteMemory(
			LaraCapacityManager<PropertyType> capacityManager, String name) {
		this(capacityManager, UNLIMITED_CAPACITY, name);
	}

	/**
	 * @param name
	 *            the memory's name
	 */
	public LDefaultLimitedCapacityOverwriteMemory(String name) {
		this(LCapacityManagers.<PropertyType> makeFIFO(), UNLIMITED_CAPACITY,
				name);
	}

	/**
	 * @see de.cesr.lara.components.container.memory.LaraMemory#addMemoryPropertyObserver(de.cesr.lara.components.container.memory.LaraMemoryListener.MemoryEvent,
	 *      de.cesr.lara.components.container.memory.LaraMemoryListener)
	 */
	@Override
	public void addMemoryPropertyObserver(MemoryEvent eventType,
			LaraMemoryListener listener) {
		propertyListeners.put(eventType, listener);
	}

	/**
	 * @see de.cesr.lara.components.container.memory.LaraMemory#clear()
	 */
	@Override
	public void clear() throws LRemoveException {
		storage.clear();
		logger.info(getName() + " was cleared");
	}

	/**
	 * @see de.cesr.lara.components.container.memory.LaraMemory#contains(java.lang.Class,
	 *      java.lang.String)
	 */
	@Override
	public boolean contains(Class<?> propertyType, String key) {
		return storage.contains(propertyType, key);
	}

	/**
	 * @see de.cesr.lara.components.container.memory.LaraMemory#contains(de.cesr.lara.components.LaraProperty)
	 */
	@Override
	public boolean contains(PropertyType property) {
		return storage.contains(property);
	}

	/**
	 * @see de.cesr.lara.components.container.memory.LaraMemory#contains(de.cesr.lara.components.LaraProperty,
	 *      java.lang.String)
	 */
	@Override
	public boolean contains(PropertyType property, String key) {
		return storage.contains(property, key);
	}

	/**
	 * TODO test
	 * 
	 * @see de.cesr.lara.components.container.memory.LaraMemory#contains(de.cesr.lara.components.LaraProperty,
	 *      java.lang.String, int)
	 */
	@Override
	public boolean contains(Class<?> propertyType, String key, int timestamp) {
		if (storage.contains(propertyType, key)) {
			return storage.fetch(key).getTimestamp() == timestamp;
		} else {
			return false;
		}
	}

	/**
	 * @see de.cesr.lara.components.container.memory.LaraMemory#contains(java.lang.String)
	 */
	@Override
	public boolean contains(String key) {
		return storage.contains(key);
	}

	/**
	 * Checks whether this memories contains a property with the given key that
	 * has the given time-stamp.
	 * 
	 * @see de.cesr.lara.components.container.memory.LaraMemory#contains(java.lang.String,
	 *      int)
	 */
	@Override
	public boolean contains(String key, int timestamp) {
		if (storage.contains(key)) {
			return storage.fetch(key).getTimestamp() == timestamp;
		} else {
			return false;
		}
	}

	@Override
	public PropertyType forget(PropertyType propertyToRemove)
			throws LRemoveException {
		PropertyType removedProperty = storage.remove(propertyToRemove);
		if (logger.isDebugEnabled()) {
			logger.debug(getName() + ": Forgot property" + removedProperty);
		}
		return removedProperty;
	}

	@Override
	public PropertyType forget(String key) throws LRemoveException {
		return storage.remove(key);
	}

	@Override
	public PropertyType forget(String key, int step) throws LRemoveException {
		throw new RuntimeException(
				"This memory does only store one property per key!");
	}

	@Override
	public Collection<PropertyType> forgetAll(
			Collection<PropertyType> propertiesToBeRemoved)
			throws LRemoveException {
		for (PropertyType prop : propertiesToBeRemoved) {
			// observer notification through storage observation!
			if (logger.isDebugEnabled()) {
				logger.debug(getName() + ": Forgot property" + prop);
			}
		}
		return storage.removeAll(propertiesToBeRemoved);
	}

	/**
	 * Forwarding to forget(key) since there is at maximum one entry. However,
	 * the specification requires wrapping by a collection.
	 * 
	 * @see de.cesr.lara.components.container.memory.LaraMemory#forgetAll(java.lang.String)
	 */
	@Override
	public Collection<PropertyType> forgetAll(String key)
			throws LRemoveException {
		Collection<PropertyType> props = new ArrayList<PropertyType>(1);
		props.add(forget(key));
		return props;
	}

	@Override
	public Set<String> getAllPropertyKeys() {
		return storage.getAllPropertyKeys();
	}

	@Override
	public int getCapacity() {
		return this.capacity;
	}

	@Override
	public LaraCapacityManagementView<PropertyType> getCapacityManagementView() {
		return storage.getCapacityManagementView();
	}

	@Override
	public LaraCapacityManager<PropertyType> getCapacityManager() {
		return storage.getCapacityManager();
	}

	@Override
	public int getDefaultRetentionTime() {
		throw new RuntimeException("Method not implemented!");
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int getRetentionTime(PropertyType property) {
		throw new RuntimeException("Method not implemented!");
	}

	@Override
	public int getSize() {
		return storage.getSize();
	}

	@Override
	public boolean isEmpty() {
		return storage.isEmpty();
	}

	@Override
	public boolean isFull() {
		return storage.isFull();
	}

	@Override
	public Iterator<PropertyType> iterator() {
		return storage.iterator();
	}

	@Override
	public void memorize(PropertyType propertyToMemorize)
			throws LContainerFullException, LInvalidTimestampException {
		if (logger.isDebugEnabled()) {
			logger.debug(getName() + ": Memorize property" + propertyToMemorize);
			logger.debug(this);
		}
		storage.store(propertyToMemorize);
	}

	/**
	 * Retention time is ignored!
	 * 
	 * @see de.cesr.lara.components.container.memory.LaraMemory#memorize(de.cesr.lara.components.LaraProperty,
	 *      int)
	 */
	@Override
	public void memorize(PropertyType propertyToMemorize, int retentionTime)
			throws LContainerFullException, LInvalidTimestampException {
		storage.store(propertyToMemorize);
	}

	/**
	 * @see de.cesr.lara.components.container.memory.LaraMemory#recall(java.lang.Class,
	 *      java.lang.String)
	 */
	@Override
	public <RequestPropertyType extends PropertyType> RequestPropertyType recall(
			Class<RequestPropertyType> propertyType, String key)
			throws LRetrieveException {
		return storage.fetch(propertyType, key);
	}

	/**
	 * @see de.cesr.lara.components.container.memory.LaraMemory#recall(java.lang.Class,
	 *      java.lang.String, int)
	 */
	@Override
	public <RequestPropertyType extends PropertyType> RequestPropertyType recall(
			Class<RequestPropertyType> propertyType, String key, int step)
			throws LRetrieveException {
		return storage.fetch(propertyType, key);
	}

	/**
	 * @see de.cesr.lara.components.container.memory.LaraMemory#recall(java.lang.String)
	 */
	@Override
	public PropertyType recall(String key) throws LRetrieveException {
		return storage.fetch(key);
	}

	/**
	 * Retention time is ignored!
	 * 
	 * @see de.cesr.lara.components.container.memory.LaraMemory#recall(java.lang.String,
	 *      int)
	 */
	@Override
	public PropertyType recall(String key, int step) throws LRetrieveException {
		return storage.fetch(key);
	}

	/**
	 * @see de.cesr.lara.components.container.memory.LaraMemory#recallAll(java.lang.Class)
	 */
	@Override
	public <RequestPropertyType extends PropertyType> Collection<RequestPropertyType> recallAll(
			Class<RequestPropertyType> propertyType) throws LRetrieveException {
		return storage.fetchAll(propertyType);
	}

	/**
	 * @see de.cesr.lara.components.container.memory.LaraMemory#recallAll(java.lang.Class,
	 *      java.lang.String)
	 */
	@Override
	public <RequestPropertyType extends PropertyType> Collection<RequestPropertyType> recallAll(
			Class<RequestPropertyType> propertyType, String key)
			throws LRetrieveException {
		Collection<RequestPropertyType> properties = new HashSet<RequestPropertyType>();
		properties.add(storage.fetch(propertyType, key));
		return properties;
	}

	@Override
	public Collection<PropertyType> recallAll(String key)
			throws LRetrieveException {
		Collection<PropertyType> props = new ArrayList<PropertyType>(1);
		props.add(recall(key));
		return props;
	}

	/**
	 * Since the Overwrite Memory only stores one property per key the refresh
	 * method is equal to memorise.
	 * 
	 * @see de.cesr.lara.components.container.memory.LaraMemory#refresh(de.cesr.lara.components.LaraProperty)
	 */
	@Override
	public void refresh(PropertyType propertyToMemorize)
			throws LContainerFullException, LInvalidTimestampException {

		if (!contains(propertyToMemorize.getKey())) {
			logger.warn("Property " + propertyToMemorize
					+ " shall be refreshed but is not contained in memory!");
		}
		storage.store(propertyToMemorize);
	}

	@Override
	public void refresh(PropertyType propertyToMemorize, int retentionTime)
			throws LInvalidTimestampException, LRemoveException {
		// nothing to do
	}

	/**
	 * Since the Overwrite Memory only stores one property per key the refresh
	 * method is equal to memorise. Since there is no retention time, the
	 * refresh method does not need to do anything.
	 * 
	 * @see de.cesr.lara.components.container.memory.LaraMemory#refresh(java.lang.String)
	 */
	@Override
	public void refresh(String key) throws LRemoveException {
		// nothing to do!
	}

	/**
	 * Since the Overwrite Memory only stores one property per key the refresh
	 * method is equal to memorise. Since there is no retention time, the
	 * refresh method does not need to do anything.
	 * 
	 * @see de.cesr.lara.components.container.memory.LaraMemory#refresh(java.lang.String,
	 *      int)
	 */
	@Override
	public void refresh(String key, int step) throws LRemoveException {
		// nothing to do
	}

	@Override
	public void refresh(String key, int step, int retentionTime)
			throws LInvalidTimestampException, LRemoveException {
		// nothing to do
	}

	@Override
	public void removeMemoryPropertyObserver(MemoryEvent eventType,
			LaraMemoryListener listener) {
		propertyListeners.remove(eventType, listener);
	}

	@Override
	public boolean setCapacity(int capacity) {
		this.capacity = capacity;
		return storage.setCapacity(capacity);
	}

	@Override
	public void setCapacityManager(LaraCapacityManager<PropertyType> manager) {
		storage.setCapacityManager(manager);
	}

	@Override
	public void setDefaultRetentionTime(int defaultRetentionTime) {
		// nothing to do
	}

	@Override
	public void storageEventOccured(StorageEvent event,
			LaraProperty<?, ?> property) {
		switch (event) {
		case PROPERTY_REMOVED:
		case PROPERTY_AUTO_REMOVED: {
			if (propertyListeners
					.containsKey(LaraMemoryListener.MemoryEvent.PROPERTY_FORGOTTEN)) {
				for (LaraMemoryListener listener : propertyListeners
						.get(LaraMemoryListener.MemoryEvent.PROPERTY_FORGOTTEN)) {
					listener.memoryEventOccured(
							LaraMemoryListener.MemoryEvent.PROPERTY_FORGOTTEN,
							property);
				}
			}
			break;
		}
		case PROPERTY_FETCHED: {
			if (propertyListeners
					.containsKey(LaraMemoryListener.MemoryEvent.PROPERTY_RECALLED)) {
				for (LaraMemoryListener listener : propertyListeners
						.get(LaraMemoryListener.MemoryEvent.PROPERTY_RECALLED)) {
					listener.memoryEventOccured(
							LaraMemoryListener.MemoryEvent.PROPERTY_RECALLED,
							property);
				}
			}
			break;
		}
		case PROPERTY_STORED: {
			if (propertyListeners
					.containsKey(LaraMemoryListener.MemoryEvent.PROPERTY_MEMORIZED)) {
				for (LaraMemoryListener listener : propertyListeners
						.get(LaraMemoryListener.MemoryEvent.PROPERTY_MEMORIZED)) {
					listener.memoryEventOccured(
							LaraMemoryListener.MemoryEvent.PROPERTY_MEMORIZED,
							property);
				}
			}
			break;
		}
		case PROPERTY_OVERWRITTEN: {
			if (propertyListeners
					.containsKey(LaraMemoryListener.MemoryEvent.REFRESHED_PROPERTY_FORGOTTEN)) {
				for (LaraMemoryListener listener : propertyListeners
						.get(LaraMemoryListener.MemoryEvent.REFRESHED_PROPERTY_FORGOTTEN)) {
					listener.memoryEventOccured(
							LaraMemoryListener.MemoryEvent.REFRESHED_PROPERTY_FORGOTTEN,
							property);
				}
			}
			break;
		}
		case PROPERTY_RESTORED: {
			if (propertyListeners
					.containsKey(LaraMemoryListener.MemoryEvent.REFRESHED_PROPERTY_MEMORIZED)) {
				for (LaraMemoryListener listener : propertyListeners
						.get(LaraMemoryListener.MemoryEvent.REFRESHED_PROPERTY_MEMORIZED)) {
					listener.memoryEventOccured(
							LaraMemoryListener.MemoryEvent.REFRESHED_PROPERTY_MEMORIZED,
							property);
				}
			}
			break;
		}
		}
	}

	/**
	 * @return the entries of this memory
	 */
	@Override
	public String toString() {
		final String NEWLINE = System.getProperty("line.separator");
		return "\t> " + getName() + "(" + storage.getSize() + " entries)"
				+ NEWLINE + storage.toString();
	}

	/**
	 * Overwrite this method in order to change the storage to be used by the
	 * memory.
	 * 
	 * TODO introduce an All-PropertyEvent-Constant....
	 * 
	 * @return
	 */
	protected LaraOverwriteStorage<PropertyType> createBackingStorage(
			LaraCapacityManager<PropertyType> capacityManager) {
		LaraOverwriteStorage<PropertyType> storage = new LDefaultSimpleStorage<PropertyType>(
				this.capacity);
		storage.setCapacityManager(capacityManager);
		storage.addStoragePropertyObserver(StorageEvent.PROPERTY_AUTO_REMOVED,
				this);
		storage.addStoragePropertyObserver(StorageEvent.PROPERTY_FETCHED, this);
		storage.addStoragePropertyObserver(StorageEvent.PROPERTY_OVERWRITTEN,
				this);
		storage.addStoragePropertyObserver(StorageEvent.PROPERTY_REMOVED, this);
		storage.addStoragePropertyObserver(StorageEvent.PROPERTY_RESTORED, this);
		storage.addStoragePropertyObserver(StorageEvent.PROPERTY_STORED, this);
		return storage;
	}
}
