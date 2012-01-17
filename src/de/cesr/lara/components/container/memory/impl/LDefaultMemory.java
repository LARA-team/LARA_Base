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


import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;
import org.apache.log4j.Logger;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import de.cesr.lara.components.LaraProperty;
import de.cesr.lara.components.container.exceptions.LContainerFullException;
import de.cesr.lara.components.container.exceptions.LInvalidTimestampException;
import de.cesr.lara.components.container.exceptions.LRemoveException;
import de.cesr.lara.components.container.exceptions.LRetrieveException;
import de.cesr.lara.components.container.memory.LaraMemory;
import de.cesr.lara.components.container.memory.LaraMemoryListener;
import de.cesr.lara.components.container.memory.LaraMemoryListener.MemoryEvent;
import de.cesr.lara.components.container.storage.LaraStorage;
import de.cesr.lara.components.container.storage.LaraStorageListener;
import de.cesr.lara.components.container.storage.impl.LDefaultStorage;
import de.cesr.lara.components.model.impl.LModel;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;


/**
 * 
 * TODO memory observer pattern!
 * 
 * @param <PropertyType>
 */
public class LDefaultMemory<PropertyType extends LaraProperty<?>> implements LaraMemory<PropertyType>,
		LaraStorageListener {

	/**
	 * Logger
	 */
	private Logger																					logger;

	private int																						defaultRetentionTime	= UNLIMITED_RETENTION;

	/**
	 * Used to indicate in maps that a property hat no "time of death" to be removed from memory
	 */
	private static int																				NO_DEATH				= -1;

	private LaraStorage<PropertyType>																storage;

	// tod = time of death
	private Map<Integer, Set<PropertyType>>															tod2properties			= new HashMap<Integer, Set<PropertyType>>();
	private Map<PropertyType, Integer>																properties2tod			= new HashMap<PropertyType, Integer>();

	// observer management:
	private final MultiMap<LaraMemoryListener.MemoryEvent, LaraMemoryListener>	propertyListeners		= new MultiHashMap<LaraMemoryListener.MemoryEvent, LaraMemoryListener>();

	private static int																				counter					= 0;
	private String																					name;

	/**
	 * 
	 */
	public LDefaultMemory() {
		this("memory" + counter++);
	}

	/**
	 * @param name
	 *        the memory's name
	 * 
	 */
	public LDefaultMemory(String name) {
		this.name = name;
		storage = createBackingStorage();
		logger = Log4jLogger.getLogger(LDefaultMemory.class.getName() + "." + getName());
	}

	/**
	 * @param defaultRetentionTime
	 */
	public LDefaultMemory(int defaultRetentionTime) {
		this.defaultRetentionTime = defaultRetentionTime;
		this.name = "memory" + counter++;
		storage = createBackingStorage();
		logger = Log4jLogger.getLogger(LDefaultMemory.class.getName() + "." + getName());
	}

	/**
	 * @param defaultRetentionTime
	 * @param name
	 *        the memory's name
	 */
	public LDefaultMemory(int defaultRetentionTime, String name) {
		this.defaultRetentionTime = defaultRetentionTime;
		this.name = name;
		storage = createBackingStorage();
		logger = Log4jLogger.getLogger(LDefaultMemory.class.getName() + "." + getName());
	}

	/**
	 * Overwrite this method in order to change the storage to be used by the memory.
	 * 
	 * @return
	 */
	protected LaraStorage<PropertyType> createBackingStorage() {
		return new LDefaultStorage<PropertyType>();
	}

	private int		step		= 0;

	/**
	 * for concurrency reasons...
	 */
	private boolean	cleaningUp	= false;

	private void checkIfNewStep() {
		if (!cleaningUp) {
			cleaningUp = true;
			if (LModel.getModel().getCurrentStep() > step) {
				Set<PropertyType> propertiesToForget;
				for (int i = step; i <= LModel.getModel().getCurrentStep(); i++) {
					propertiesToForget = tod2properties.get(i);
					if (propertiesToForget != null) {
						forgetAll(propertiesToForget);
						tod2properties.remove(i);
					}
				}
				if (logger.isDebugEnabled()) {
					logger
							.debug(getName() + " is up-to-date:" + System.getProperty("line.separator")
									+ this.toString());
				}
			}
			step = LModel.getModel().getCurrentStep();
			cleaningUp = false;
		}
	}

	@Override
	public void clear() throws LRemoveException {
		if (propertyListeners.containsKey(MemoryEvent.PROPERTY_FORGOTTEN)) {
			for (LaraMemoryListener listener : propertyListeners.get(MemoryEvent.PROPERTY_FORGOTTEN)) {
				for (String key : getAllPropertyKeys()) {
					for (PropertyType property : recallAll(key)) {
						listener.memoryEventOccured(MemoryEvent.PROPERTY_FORGOTTEN, property);
					}
				}
			}
		}
		storage.clear();
		tod2properties = new HashMap<Integer, Set<PropertyType>>();
		properties2tod = new HashMap<PropertyType, Integer>();
		logger.info(getName() + " was cleared");
	}

	private void removeFromMaps(PropertyType propertyToRemove) {
		tod2properties.get(properties2tod.remove(propertyToRemove)).remove(propertyToRemove);
	}

	@Override
	public PropertyType forget(PropertyType propertyToRemove) throws LRemoveException {
		PropertyType removedProperty = forgetEssential(propertyToRemove);
		if (propertyListeners.containsKey(MemoryEvent.PROPERTY_FORGOTTEN)) {
			for (LaraMemoryListener listener : propertyListeners.get(MemoryEvent.PROPERTY_FORGOTTEN)) {
				listener.memoryEventOccured(MemoryEvent.PROPERTY_FORGOTTEN, removedProperty);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug(getName() + ": Forgot property" + removedProperty);
		}
		return removedProperty;
	}

	/**
	 * Essential forget-method without listener invocation and logging.
	 * 
	 * @param propertyToRemove
	 * @return
	 * @throws LRemoveException
	 */
	private PropertyType forgetEssential(PropertyType propertyToRemove) throws LRemoveException {
		checkIfNewStep();
		PropertyType removedProperty = storage.remove(propertyToRemove);
		removeFromMaps(removedProperty);
		return removedProperty;
	}

	/**
	 * Essential forget-method without listener invocation and logging.
	 * 
	 * @param propertyToRemove
	 * @return
	 * @throws LRemoveException
	 */
	private Collection<PropertyType> forgetAllEssential(String keyToRemove) throws LRemoveException {
		checkIfNewStep();
		Collection<PropertyType> removedProperties = storage.removeAll(keyToRemove);
		for (PropertyType removedProperty : removedProperties) {
			removeFromMaps(removedProperty);
		}
		return removedProperties;
	}

	@Override
	public PropertyType forget(String key, int step) throws LRemoveException {
		checkIfNewStep();
		PropertyType removedProperty = storage.remove(key, step);
		removeFromMaps(removedProperty);
		if (propertyListeners.containsKey(MemoryEvent.PROPERTY_FORGOTTEN)) {
			for (LaraMemoryListener listener : propertyListeners.get(MemoryEvent.PROPERTY_FORGOTTEN)) {
				listener.memoryEventOccured(MemoryEvent.PROPERTY_FORGOTTEN, removedProperty);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug(getName() + ": Forgot property" + removedProperty);
		}
		return removedProperty;
	}

	@Override
	public Collection<PropertyType> forgetAll(Collection<PropertyType> propertiesToBeRemoved)
			throws LRemoveException {
		checkIfNewStep();
		Collection<PropertyType> removedProperties = storage.removeAll(propertiesToBeRemoved);
		for (PropertyType removedProperty : removedProperties) {
			if (propertyListeners.containsKey(MemoryEvent.PROPERTY_FORGOTTEN)) {
				for (LaraMemoryListener listener : propertyListeners.get(MemoryEvent.PROPERTY_FORGOTTEN)) {
					listener.memoryEventOccured(MemoryEvent.PROPERTY_FORGOTTEN, removedProperty);
				}
			}
			removeFromMaps(removedProperty);
			if (logger.isDebugEnabled()) {
				logger.debug(getName() + ": Forget property" + removedProperty);
			}
		}
		return removedProperties;
	}

	@Override
	public Collection<PropertyType> forgetAll(String key) throws LRemoveException {
		Collection<PropertyType> removedProperties = forgetAllEssential(key);
		for (PropertyType removedProperty : removedProperties) {
			if (propertyListeners.containsKey(MemoryEvent.PROPERTY_FORGOTTEN)) {
				for (LaraMemoryListener listener : propertyListeners.get(MemoryEvent.PROPERTY_FORGOTTEN)) {
					listener.memoryEventOccured(MemoryEvent.PROPERTY_FORGOTTEN, removedProperty);
				}
			}
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug(getName() + ": Forget property" + removedProperty);
			}
			// LOGGING ->
		}
		return removedProperties;
	}

	@Override
	public void memorize(PropertyType propertyToMemorize) throws LContainerFullException,
			LInvalidTimestampException {
		memorize(propertyToMemorize, defaultRetentionTime);
	}

	@Override
	public void memorize(PropertyType propertyToMemorize, int retentionTime) throws 
			LContainerFullException, LInvalidTimestampException {
		checkIfNewStep();
		storage.store(propertyToMemorize);
		if (propertyListeners.containsKey(MemoryEvent.PROPERTY_MEMORIZED)) {
			for (LaraMemoryListener listener : propertyListeners.get(MemoryEvent.PROPERTY_MEMORIZED)) {
				listener.memoryEventOccured(MemoryEvent.PROPERTY_MEMORIZED, propertyToMemorize);
			}
		}

		int tod;
		if (retentionTime == UNLIMITED_RETENTION) {
			tod = NO_DEATH;
		} else {
			tod = propertyToMemorize.getTimestamp() + retentionTime;
		}

		Set<PropertyType> properties = tod2properties.get(tod);
		if (properties == null) {
			properties = new HashSet<PropertyType>();
			tod2properties.put(tod, properties);
		}
		properties.add(propertyToMemorize);
		properties2tod.put(propertyToMemorize, tod);

		if (logger.isDebugEnabled()) {
			// logger.debug(getName() + ": Memorised property: " + propertyToMemorize);
		}
	}

	@Override
	public PropertyType recall(String key, int step) throws LRetrieveException {
		checkIfNewStep();
		try {
			PropertyType recalled = storage.fetch(key, step);

			if (propertyListeners.containsKey(MemoryEvent.PROPERTY_RECALLED)) {
				for (LaraMemoryListener listener : propertyListeners.get(MemoryEvent.PROPERTY_RECALLED)) {
					listener.memoryEventOccured(MemoryEvent.PROPERTY_RECALLED, recalled);
				}
			}
			return recalled;
		} catch (LRetrieveException ex) {
			logger.error(ex + ex.getStackTrace().toString());
			throw ex;
		}

	}

	@Override
	public PropertyType recall(String key) throws LRetrieveException {
		checkIfNewStep();
		PropertyType property = storage.fetch(key);
		if (propertyListeners.containsKey(MemoryEvent.PROPERTY_RECALLED)) {
			for (LaraMemoryListener listener : propertyListeners.get(MemoryEvent.PROPERTY_RECALLED)) {
				listener.memoryEventOccured(MemoryEvent.PROPERTY_RECALLED, property);
			}
		}
		return property;
	}

	@Override
	public PropertyType recall(Class<PropertyType> propertyType, String key, int step) throws LRetrieveException {
		checkIfNewStep();
		// TODO test and implement! (SH)
		return null;
	}

	@Override
	public PropertyType recall(Class<PropertyType> propertyType, String key) throws LRetrieveException {
		checkIfNewStep();
		// TODO test and implement! (SH)
		return null;
	}

	@Override
	public Collection<PropertyType> recallAll(String key) throws LRetrieveException {
		checkIfNewStep();
		Collection<PropertyType> properties = storage.fetchAll(key);
		if (propertyListeners.containsKey(MemoryEvent.PROPERTY_RECALLED)) {
			for (LaraMemoryListener listener : propertyListeners.get(MemoryEvent.PROPERTY_RECALLED)) {
				for (PropertyType property : properties) {
					listener.memoryEventOccured(MemoryEvent.PROPERTY_RECALLED, property);
				}
			}
		}
		return properties;
	}

	@Override
	public Collection<PropertyType> recallAll(Class<PropertyType> propertyType, String key)
			throws LRetrieveException {
		checkIfNewStep();
		// TODO test and implement! (SH)
		return null;
	}

	@Override
	public int getCapacity() {
		checkIfNewStep();
		return storage.getCapacity();
	}

	@Override
	public int getSize() {
		checkIfNewStep();
		return storage.getSize();
	}

	@Override
	public boolean isEmpty() {
		checkIfNewStep();
		return storage.isEmpty();
	}

	@Override
	public boolean isFull() {
		checkIfNewStep();
		return storage.isFull();
	}

	@Override
	public Iterator<PropertyType> iterator() {
		checkIfNewStep();
		return storage.iterator();
	}

	@Override
	public int getDefaultRetentionTime() {
		checkIfNewStep();
		return defaultRetentionTime;
	}

	@Override
	public void setDefaultRetentionTime(int defaultRetentionTime) {
		checkIfNewStep();
		this.defaultRetentionTime = defaultRetentionTime;
		logger.info(getName() + ": Retention time set to " + defaultRetentionTime);
	}

	/**
	 * @return the entries of this memory
	 */
	@Override
	public String toString() {
		checkIfNewStep();
		final String NEWLINE = System.getProperty("line.separator");
		return "\t> " + getName() + NEWLINE + storage.toString();
	}

	@Override
	public int getRetentionTime(PropertyType property) {
		checkIfNewStep();
		Integer tod = properties2tod.get(property);
		if (tod == null) {
			return 0;
		}
		return tod - LModel.getModel().getCurrentStep();
	}

	/**
	 * @see de.cesr.lara.components.container.memory.LaraMemory#getAllPropertyKeys()
	 */
	@Override
	public Set<String> getAllPropertyKeys() {
		return storage.getAllPropertyKeys();
	}

	/**
	 * @see de.cesr.lara.components.container.memory.LaraMemory#contains(java.lang.String)
	 */
	@Override
	public boolean contains(String key) {
		checkIfNewStep();
		return storage.contains(key);
	}

	// observer Management:
	/**
	 * @see de.cesr.lara.components.container.memory.LaraMemory#addMemoryPropertyObserver(de.cesr.lara.components.container.memory.LaraMemoryListener.MemoryEvent,
	 *      de.cesr.lara.components.container.memory.LaraMemoryListener)
	 */
	@Override
	public void addMemoryPropertyObserver(MemoryEvent eventType, LaraMemoryListener listener) {
		propertyListeners.put(eventType, listener);
		logger.info(getName() + ": Memory property listener added for event type " + eventType + ": " + listener);
	}

	/**
	 * @see de.cesr.lara.components.container.memory.LaraMemory#removeMemoryPropertyObserver(de.cesr.lara.components.container.memory.LaraMemoryListener.MemoryEvent,
	 *      de.cesr.lara.components.container.memory.LaraMemoryListener)
	 */
	@Override
	public void removeMemoryPropertyObserver(MemoryEvent eventType, LaraMemoryListener listener) {
		propertyListeners.remove(eventType, listener);
		logger.info(getName() + ": Memory property listener removed for event type " + eventType + ": " + listener);
	}

	@Override
	public void storageEventOccured(StorageEvent event, LaraProperty<?> property) {
		switch (event) {
			case PROPERTY_REMOVED: {
				for (LaraMemoryListener listener : propertyListeners
						.get(LaraMemoryListener.MemoryEvent.PROPERTY_FORGOTTEN)) {
					listener.memoryEventOccured(LaraMemoryListener.MemoryEvent.PROPERTY_FORGOTTEN,
							property);
				}
				break;
			}
		}
	}

	/**
	 * @see de.cesr.lara.components.container.memory.LaraMemory#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void refresh(PropertyType propertyToRefresh) throws LContainerFullException,
			LInvalidTimestampException {
		// in case a property was forgotten in mean time it may not be refreshed...:
		checkIfNewStep();

		// property needs to be forgotten first, because it may not be overwritten due to different time stamps:
		forgetAllEssential(propertyToRefresh.getKey());
		memorize((PropertyType) propertyToRefresh.getRefreshedProperty());

		if (propertyListeners.containsKey(MemoryEvent.REFRESHED_PROPERTY_FORGOTTEN)) {
			for (LaraMemoryListener listener : propertyListeners
					.get(MemoryEvent.REFRESHED_PROPERTY_FORGOTTEN)) {
				listener.memoryEventOccured(MemoryEvent.REFRESHED_PROPERTY_FORGOTTEN, propertyToRefresh);
			}
		}
		if (propertyListeners.containsKey(MemoryEvent.REFRESHED_PROPERTY_MEMORIZED)) {
			for (LaraMemoryListener listener : propertyListeners
					.get(MemoryEvent.REFRESHED_PROPERTY_MEMORIZED)) {
				listener.memoryEventOccured(MemoryEvent.REFRESHED_PROPERTY_MEMORIZED, propertyToRefresh);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug(getName() + ": Memorised property: " + propertyToRefresh);
		}
	}

	/**
	 * TODO optimise! (SH) TODO don't call memorise and forget since listeners are called three times, then (SH)
	 * 
	 * @see de.cesr.lara.components.container.memory.LaraMemory#refresh(java.lang.String) (SH)
	 */
	@SuppressWarnings("unchecked")
	// memorise: LaraPropery.getRefreshedProperty needs to return a property of same type
	@Override
	public void refresh(String key) throws LRemoveException {
		// in case a property was forgotten in mean time it may not be refreshed...:
		checkIfNewStep();

		PropertyType propertyToRefresh = storage.fetch(key);

		// property needs to be forgotten first, because it may not be overwritten due to different time stamps:
		forgetEssential(propertyToRefresh);
		memorize((PropertyType) propertyToRefresh.getRefreshedProperty());

		if (propertyListeners.containsKey(MemoryEvent.REFRESHED_PROPERTY_FORGOTTEN)) {
			for (LaraMemoryListener listener : propertyListeners
					.get(MemoryEvent.REFRESHED_PROPERTY_FORGOTTEN)) {
				listener.memoryEventOccured(MemoryEvent.REFRESHED_PROPERTY_FORGOTTEN, propertyToRefresh);
			}
		}
		if (propertyListeners.containsKey(MemoryEvent.REFRESHED_PROPERTY_MEMORIZED)) {
			for (LaraMemoryListener listener : propertyListeners
					.get(MemoryEvent.REFRESHED_PROPERTY_MEMORIZED)) {
				listener.memoryEventOccured(MemoryEvent.REFRESHED_PROPERTY_MEMORIZED, propertyToRefresh);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug(getName() + ": Memorised property: " + propertyToRefresh);
		}
	}

	@Override
	public void refresh(String key, int step) throws LRemoveException {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}
}
