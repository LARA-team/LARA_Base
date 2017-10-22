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
import de.cesr.lara.components.model.LaraModel;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;

/**
 * 
 * TODO memory observer pattern!
 * 
 * @param <PropertyType>
 */
public class LDefaultMemory<PropertyType extends LaraProperty<? extends PropertyType, ?>>
		implements LaraMemory<PropertyType>, LaraStorageListener {

	/**
	 * Logger
	 */
	private Logger logger;
	static private boolean logindividual = Log4jLogger.getLogger(LDefaultMemory.class + ".individual").isDebugEnabled();

	private int defaultRetentionTime = UNLIMITED_RETENTION;

	/**
	 * Used to indicate in maps that a property hat no "time of death" to be
	 * removed from memory
	 */
	private static int NO_DEATH = -1;

	protected LaraModel lmodel;

	private LaraStorage<PropertyType> storage;

	// tod = time of death
	private Map<Integer, Set<PropertyType>> tod2properties = new HashMap<Integer, Set<PropertyType>>();
	private Map<PropertyType, Integer> properties2tod = new HashMap<PropertyType, Integer>();

	// observer management:
	private final MultiMap<LaraMemoryListener.MemoryEvent, LaraMemoryListener> propertyListeners = new MultiHashMap<LaraMemoryListener.MemoryEvent, LaraMemoryListener>();

	private static int counter = 0;
	private String name;

	private int step = 0;

	/**
	 * for concurrency reasons...
	 */
	private boolean cleaningUp = false;

	/**
	 * @param lmodel 
	 * 
	 */
	public LDefaultMemory(LaraModel lmodel) {
		this(lmodel, "memory" + counter++);
	}

	/**
	 * @param lmodel
	 * @param defaultRetentionTime
	 */
	public LDefaultMemory(LaraModel lmodel, int defaultRetentionTime) {
		this.lmodel = lmodel;
		this.defaultRetentionTime = defaultRetentionTime;
		this.name = "memory" + counter++;
		storage = createBackingStorage();
		logger = Log4jLogger.getLogger(LDefaultMemory.class.getName() + (logindividual ? "." + getName() : ""));
	}

	/**
	 * @param lmodel 
	 * @param defaultRetentionTime
	 * @param name
	 *            the memory's name
	 */
	public LDefaultMemory(LaraModel lmodel, int defaultRetentionTime,
			String name) {
		this.lmodel = lmodel;
		this.defaultRetentionTime = defaultRetentionTime;
		this.name = name;
		storage = createBackingStorage();
		logger = Log4jLogger.getLogger(LDefaultMemory.class.getName() + (logindividual ? "." + getName() : ""));
	}

	/**
	 * @param lmodel 
	 * @param name
	 *            the memory's name
	 * 
	 */
	public LDefaultMemory(LaraModel lmodel, String name) {
		this.lmodel = lmodel;
		this.name = name;
		storage = createBackingStorage();
		logger = Log4jLogger.getLogger(LDefaultMemory.class.getName() +
				(logindividual ? "." + getName() : ""));
	}

	// observer Management:
	/**
	 * @see de.cesr.lara.components.container.memory.LaraMemory#addMemoryPropertyObserver(de.cesr.lara.components.container.memory.LaraMemoryListener.MemoryEvent,
	 *      de.cesr.lara.components.container.memory.LaraMemoryListener)
	 */
	@Override
	public void addMemoryPropertyObserver(MemoryEvent eventType,
			LaraMemoryListener listener) {
		propertyListeners.put(eventType, listener);
		logger.info(getName()
				+ ": Memory property listener added for event type "
				+ eventType + ": " + listener);
	}

	@Override
	public void clear() throws LRemoveException {
		if (propertyListeners.containsKey(MemoryEvent.PROPERTY_FORGOTTEN)) {
			for (LaraMemoryListener listener : propertyListeners
					.get(MemoryEvent.PROPERTY_FORGOTTEN)) {
				for (String key : getAllPropertyKeys()) {
					for (PropertyType property : recallAll(key)) {
						listener.memoryEventOccured(
								MemoryEvent.PROPERTY_FORGOTTEN, property);
					}
				}
			}
		}
		storage.clear();
		tod2properties = new HashMap<Integer, Set<PropertyType>>();
		properties2tod = new HashMap<PropertyType, Integer>();
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
	 * @see de.cesr.lara.components.container.memory.LaraMemory#contains(Class, String, int)
	 */
	@Override
	public boolean contains(Class<?> propertyType, String key, int timestamp) {
		return storage.contains(propertyType, key, timestamp);
	}

	/**
	 * @see de.cesr.lara.components.container.memory.LaraMemory#contains(java.lang.String)
	 */
	@Override
	public boolean contains(String key) {
		checkIfNewStep();
		return storage.contains(key);
	}

	/**
	 * @see de.cesr.lara.components.container.memory.LaraMemory#contains(java.lang.String,
	 *      int)
	 */
	@Override
	public boolean contains(String key, int timestamp) {
		return storage.contains(key, timestamp);
	}

	@Override
	public PropertyType forget(PropertyType propertyToRemove)
			throws LRemoveException {
		PropertyType removedProperty = forgetEssential(propertyToRemove);
		if (propertyListeners.containsKey(MemoryEvent.PROPERTY_FORGOTTEN)) {
			for (LaraMemoryListener listener : propertyListeners
					.get(MemoryEvent.PROPERTY_FORGOTTEN)) {
				listener.memoryEventOccured(MemoryEvent.PROPERTY_FORGOTTEN,
						removedProperty);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug(getName() + ": Forgot property" + removedProperty);
		}
		return removedProperty;
	}

	/**
	 * @see de.cesr.lara.components.container.memory.LaraMemory#forget(java.lang.String, int)
	 */
	@Override
	public PropertyType forget(String key, int step) throws LRemoveException {
		checkIfNewStep();
		PropertyType removedProperty = storage.remove(key, step);
		removeFromMaps(removedProperty);
		if (propertyListeners.containsKey(MemoryEvent.PROPERTY_FORGOTTEN)) {
			for (LaraMemoryListener listener : propertyListeners
					.get(MemoryEvent.PROPERTY_FORGOTTEN)) {
				listener.memoryEventOccured(MemoryEvent.PROPERTY_FORGOTTEN,
						removedProperty);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug(getName() + ": Forgot property" + removedProperty);
		}
		return removedProperty;
	}

	@Override
	public Collection<PropertyType> forgetAll(
			Collection<PropertyType> propertiesToBeRemoved)
			throws LRemoveException {
		checkIfNewStep();
		Collection<PropertyType> removedProperties = storage
				.removeAll(propertiesToBeRemoved);
		for (PropertyType removedProperty : removedProperties) {
			if (propertyListeners.containsKey(MemoryEvent.PROPERTY_FORGOTTEN)) {
				for (LaraMemoryListener listener : propertyListeners
						.get(MemoryEvent.PROPERTY_FORGOTTEN)) {
					listener.memoryEventOccured(MemoryEvent.PROPERTY_FORGOTTEN,
							removedProperty);
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
	public Collection<PropertyType> forgetAll(String key)
			throws LRemoveException {
		Collection<PropertyType> removedProperties = forgetAllEssential(key);
		for (PropertyType removedProperty : removedProperties) {
			if (propertyListeners.containsKey(MemoryEvent.PROPERTY_FORGOTTEN)) {
				for (LaraMemoryListener listener : propertyListeners
						.get(MemoryEvent.PROPERTY_FORGOTTEN)) {
					listener.memoryEventOccured(MemoryEvent.PROPERTY_FORGOTTEN,
							removedProperty);
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

	/**
	 * @see de.cesr.lara.components.container.memory.LaraMemory#getAllPropertyKeys()
	 */
	@Override
	public Set<String> getAllPropertyKeys() {
		return storage.getAllPropertyKeys();
	}

	@Override
	public int getCapacity() {
		checkIfNewStep();
		return storage.getCapacity();
	}

	@Override
	public int getDefaultRetentionTime() {
		checkIfNewStep();
		return defaultRetentionTime;
	}

	/**
	 * @see de.cesr.lara.components.container.memory.LaraMemory#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int getRetentionTime(PropertyType property) {
		checkIfNewStep();
		Integer tod = properties2tod.get(property);
		if (tod == null) {
			return 0;
		}
		return tod - lmodel.getCurrentStep();
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
	public void memorize(PropertyType propertyToMemorize)
			throws LContainerFullException, LInvalidTimestampException {
		memorize(propertyToMemorize, defaultRetentionTime);
	}

	@Override
	public void memorize(PropertyType propertyToMemorize, int retentionTime)
			throws LContainerFullException, LInvalidTimestampException {
		checkIfNewStep();

		// check if a prop with that key is already contained:
		if (contains(propertyToMemorize.getKey(),
				propertyToMemorize.getTimestamp())) {
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Overwrite property with key "
						+ propertyToMemorize.getKey()
						+ " that is already contained.");
			}
			// LOGGING ->
			PropertyType contained = recall(propertyToMemorize.getKey(),
					propertyToMemorize.getTimestamp());
			removeFromMaps(contained);
		}

		storage.store(propertyToMemorize);
		if (propertyListeners.containsKey(MemoryEvent.PROPERTY_MEMORIZED)) {
			for (LaraMemoryListener listener : propertyListeners
					.get(MemoryEvent.PROPERTY_MEMORIZED)) {
				listener.memoryEventOccured(MemoryEvent.PROPERTY_MEMORIZED,
						propertyToMemorize);
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

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(getName() + ": Memorised property: "
					+ propertyToMemorize);
		}
		// LOGGING ->
	}

	/**
	 * @see de.cesr.lara.components.container.memory.LaraMemory#recall(java.lang.Class,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	// checked by isInstance
	@Override
	public <RequestPropertyType extends PropertyType> RequestPropertyType recall(
			Class<RequestPropertyType> propertyType, String key)
			throws LRetrieveException {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(getName() + ": Recall '" + key + "' of type "
					+ propertyType);
		}
		// LOGGING ->

		checkIfNewStep();
		PropertyType property = storage.fetch(propertyType, key);
		if (propertyListeners.containsKey(MemoryEvent.PROPERTY_RECALLED)) {
			for (LaraMemoryListener listener : propertyListeners
					.get(MemoryEvent.PROPERTY_RECALLED)) {
				listener.memoryEventOccured(MemoryEvent.PROPERTY_RECALLED,
						property);
			}
		}
		return (RequestPropertyType) property;
	}

	/**
	 * @see de.cesr.lara.components.container.memory.LaraMemory#recall(java.lang.Class,
	 *      java.lang.String, int)
	 */
	@SuppressWarnings("unchecked")
	// Checked by isInstance
	@Override
	public <RequestPropertyType extends PropertyType> RequestPropertyType recall(
			Class<RequestPropertyType> propertyType, String key, int step)
			throws LRetrieveException {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(getName() + ": Recall '" + key + "' at step " + step);
		}
		// LOGGING ->
		checkIfNewStep();
		try {
			PropertyType recalled = storage.fetch(propertyType, key, step);
			if (propertyListeners.containsKey(MemoryEvent.PROPERTY_RECALLED)) {
				for (LaraMemoryListener listener : propertyListeners
						.get(MemoryEvent.PROPERTY_RECALLED)) {
					listener.memoryEventOccured(MemoryEvent.PROPERTY_RECALLED,
							recalled);
				}
			}
			return (RequestPropertyType) recalled;
		} catch (LRetrieveException ex) {
			logger.error(ex + ex.getStackTrace().toString());
			throw ex;
		}
	}

	@Override
	public PropertyType recall(String key) throws LRetrieveException {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(getName() + ": Recall '" + key + "'");
		}
		// LOGGING ->

		checkIfNewStep();
		PropertyType property = storage.fetch(key);
		if (propertyListeners.containsKey(MemoryEvent.PROPERTY_RECALLED)) {
			for (LaraMemoryListener listener : propertyListeners
					.get(MemoryEvent.PROPERTY_RECALLED)) {
				listener.memoryEventOccured(MemoryEvent.PROPERTY_RECALLED,
						property);
			}
		}
		return property;
	}

	@Override
	public PropertyType recall(String key, int step) throws LRetrieveException {
		checkIfNewStep();
		try {
			PropertyType recalled = storage.fetch(key, step);

			if (propertyListeners.containsKey(MemoryEvent.PROPERTY_RECALLED)) {
				for (LaraMemoryListener listener : propertyListeners
						.get(MemoryEvent.PROPERTY_RECALLED)) {
					listener.memoryEventOccured(MemoryEvent.PROPERTY_RECALLED,
							recalled);
				}
			}
			return recalled;
		} catch (LRetrieveException ex) {
			logger.error(ex + ex.getStackTrace().toString());
			throw ex;
		}
	}

	/**
	 * @see de.cesr.lara.components.container.memory.LaraMemory#recallAll(java.lang.Class,
	 *      java.lang.String)
	 */
	@Override
	public <RequestPropertyType extends PropertyType> Collection<RequestPropertyType> recallAll(
			Class<RequestPropertyType> propertyType) throws LRetrieveException {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(getName() + ": Recall all of type " + propertyType);
		}
		// LOGGING ->

		checkIfNewStep();
		Collection<RequestPropertyType> properties = storage
				.fetchAll(propertyType);
		if (propertyListeners.containsKey(MemoryEvent.PROPERTY_RECALLED)) {
			for (LaraMemoryListener listener : propertyListeners
					.get(MemoryEvent.PROPERTY_RECALLED)) {
				for (PropertyType property : properties) {
					listener.memoryEventOccured(MemoryEvent.PROPERTY_RECALLED,
							property);
				}
			}
		}
		return properties;
	}

	/**
	 * @see de.cesr.lara.components.container.memory.LaraMemory#recallAll(java.lang.Class,
	 *      java.lang.String)
	 */
	@Override
	public <RequestPropertyType extends PropertyType> Collection<RequestPropertyType> recallAll(
			Class<RequestPropertyType> propertyType, String key)
			throws LRetrieveException {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(getName() + ": Recall all of type " + propertyType
					+ " of type " + propertyType);
		}
		// LOGGING ->

		checkIfNewStep();
		Collection<RequestPropertyType> properties = storage.fetchAll(
				propertyType, key);
		if (propertyListeners.containsKey(MemoryEvent.PROPERTY_RECALLED)) {
			for (LaraMemoryListener listener : propertyListeners
					.get(MemoryEvent.PROPERTY_RECALLED)) {
				for (PropertyType property : properties) {
					listener.memoryEventOccured(MemoryEvent.PROPERTY_RECALLED,
							property);
				}
			}
		}
		return properties;
	}

	@Override
	public Collection<PropertyType> recallAll(String key)
			throws LRetrieveException {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(getName() + ": Recall all of key '" + key + "'");
		}
		// LOGGING ->

		checkIfNewStep();
		Collection<PropertyType> properties = storage.fetchAll(key);
		if (propertyListeners.containsKey(MemoryEvent.PROPERTY_RECALLED)) {
			for (LaraMemoryListener listener : propertyListeners
					.get(MemoryEvent.PROPERTY_RECALLED)) {
				for (PropertyType property : properties) {
					listener.memoryEventOccured(MemoryEvent.PROPERTY_RECALLED,
							property);
				}
			}
		}
		return properties;
	}

	@Override
	public void refresh(PropertyType propertyToRefresh)
			throws LContainerFullException, LInvalidTimestampException {
		// in case a property was forgotten in mean time it may not be
		// refreshed...:
		checkIfNewStep();

		// property needs to be forgotten first, because it may not be
		// overwritten due to different time stamps:
		forgetAllEssential(propertyToRefresh.getKey());

		memorize(propertyToRefresh.getRefreshedProperty());

		informListenersRefresh(propertyToRefresh);
		if (logger.isDebugEnabled()) {
			logger.debug(getName() + ": Memorised property: "
					+ propertyToRefresh);
		}
	}

	@Override
	public void refresh(PropertyType propertyToRefresh, int retentionTime)
			throws LInvalidTimestampException, LRemoveException {
		// in case a property was forgotten in mean time it may not be
		// refreshed...:
		checkIfNewStep();

		forgetEssential(propertyToRefresh);
		memorize(propertyToRefresh.getRefreshedProperty(), retentionTime);

		informListenersRefresh(propertyToRefresh);
		if (logger.isDebugEnabled()) {
			logger.debug(getName() + ": Memorised property: "
					+ propertyToRefresh);
		}
	}

	/**
	 * @see de.cesr.lara.components.container.memory.LaraMemory#refresh(java.lang.String)
	 *      (SH)
	 */
	@Override
	public void refresh(String key) throws LRemoveException {
		// in case a property was forgotten in mean time it may not be
		// refreshed...:
		checkIfNewStep();

		PropertyType propertyToRefresh = storage.fetch(key);

		// property needs to be forgotten first, because it may not be
		// overwritten due to different time stamps:
		forgetEssential(propertyToRefresh);
		memorize(propertyToRefresh.getRefreshedProperty());

		informListenersRefresh(propertyToRefresh);
		if (logger.isDebugEnabled()) {
			logger.debug(getName() + ": Memorised property: "
					+ propertyToRefresh);
		}
	}

	@Override
	public void refresh(String key, int step) throws LRemoveException {
		// in case a property was forgotten in mean time it may not be
		// refreshed...:
		checkIfNewStep();
		PropertyType propertyToRefresh = storage.fetch(key, step);

		forgetEssential(propertyToRefresh);
		memorize(propertyToRefresh.getRefreshedProperty());

		informListenersRefresh(propertyToRefresh);
		if (logger.isDebugEnabled()) {
			logger.debug(getName() + ": Memorised property: "
					+ propertyToRefresh);
		}
	}

	@Override
	public void refresh(String key, int step, int retentionTime)
			throws LInvalidTimestampException, LRemoveException {
		// in case a property was forgotten in mean time it may not be
		// refreshed...:
		checkIfNewStep();
		PropertyType propertyToRefresh = storage.fetch(key, step);

		forgetEssential(propertyToRefresh);
		memorize(propertyToRefresh.getRefreshedProperty(), retentionTime);

		informListenersRefresh(propertyToRefresh);
		if (logger.isDebugEnabled()) {
			logger.debug(getName() + ": Memorised property: "
					+ propertyToRefresh);
		}
	}

	/**
	 * @see de.cesr.lara.components.container.memory.LaraMemory#removeMemoryPropertyObserver(de.cesr.lara.components.container.memory.LaraMemoryListener.MemoryEvent,
	 *      de.cesr.lara.components.container.memory.LaraMemoryListener)
	 */
	@Override
	public void removeMemoryPropertyObserver(MemoryEvent eventType,
			LaraMemoryListener listener) {
		propertyListeners.remove(eventType, listener);
		logger.info(getName()
				+ ": Memory property listener removed for event type "
				+ eventType + ": " + listener);
	}

	@Override
	public void setDefaultRetentionTime(int defaultRetentionTime) {
		checkIfNewStep();
		this.defaultRetentionTime = defaultRetentionTime;
		logger.info(getName() + ": Retention time set to "
				+ defaultRetentionTime);
	}

	@Override
	public void storageEventOccured(StorageEvent event,
			LaraProperty<?, ?> property) {
		switch (event) {
		case PROPERTY_REMOVED: {
			for (LaraMemoryListener listener : propertyListeners
					.get(LaraMemoryListener.MemoryEvent.PROPERTY_FORGOTTEN)) {
				listener.memoryEventOccured(
						LaraMemoryListener.MemoryEvent.PROPERTY_FORGOTTEN,
						property);
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
		checkIfNewStep();
		final String NEWLINE = System.getProperty("line.separator");
		StringBuffer buffer = new StringBuffer();
		for (PropertyType property : this) {
			buffer.append("\t" + property.toString() + "(ToD: "
					+ getRetentionTime(property) + ")" + NEWLINE);
		}
		return buffer.toString();
	}

	private void checkIfNewStep() {
		if (!cleaningUp) {
			cleaningUp = true;
			if (lmodel.getCurrentStep() > step) {
				Set<PropertyType> propertiesToForget;
				for (int i = step; i <= lmodel.getCurrentStep(); i++) {
					propertiesToForget = tod2properties.get(i);
					if (propertiesToForget != null) {
						forgetAll(propertiesToForget);
						tod2properties.remove(i);
					}
				}
				if (logger.isDebugEnabled()) {
					logger.debug(getName() + " is up-to-date:"
							+ System.getProperty("line.separator")
							+ this.toString());
				}
			}
			step = lmodel.getCurrentStep();
			cleaningUp = false;
		}
	}

	/**
	 * Essential forget-method without listener invocation and logging.
	 * 
	 * @param propertyToRemove
	 * @return
	 * @throws LRemoveException
	 */
	private Collection<PropertyType> forgetAllEssential(String keyToRemove)
			throws LRemoveException {
		checkIfNewStep();
		Collection<PropertyType> removedProperties = storage
				.removeAll(keyToRemove);
		for (PropertyType removedProperty : removedProperties) {
			removeFromMaps(removedProperty);
		}
		return removedProperties;
	}

	/**
	 * Essential forget-method without listener invocation and logging.
	 * 
	 * @param propertyToRemove
	 * @return
	 * @throws LRemoveException
	 */
	private PropertyType forgetEssential(PropertyType propertyToRemove)
			throws LRemoveException {
		checkIfNewStep();
		PropertyType removedProperty = storage.remove(propertyToRemove);
		removeFromMaps(removedProperty);
		return removedProperty;
	}

	private void informListenersRefresh(PropertyType propertyToRefresh) {
		if (propertyListeners
				.containsKey(MemoryEvent.REFRESHED_PROPERTY_FORGOTTEN)) {
			for (LaraMemoryListener listener : propertyListeners
					.get(MemoryEvent.REFRESHED_PROPERTY_FORGOTTEN)) {
				listener.memoryEventOccured(
						MemoryEvent.REFRESHED_PROPERTY_FORGOTTEN,
						propertyToRefresh);
			}
		}
		if (propertyListeners
				.containsKey(MemoryEvent.REFRESHED_PROPERTY_MEMORIZED)) {
			for (LaraMemoryListener listener : propertyListeners
					.get(MemoryEvent.REFRESHED_PROPERTY_MEMORIZED)) {
				listener.memoryEventOccured(
						MemoryEvent.REFRESHED_PROPERTY_MEMORIZED,
						propertyToRefresh);
			}
		}
	}

	private void removeFromMaps(PropertyType propertyToRemove) {
		tod2properties.get(properties2tod.remove(propertyToRemove)).remove(
				propertyToRemove);
	}

	/**
	 * Overwrite this method in order to change the storage to be used by the
	 * memory.
	 * 
	 * @return storage
	 */
	protected LaraStorage<PropertyType> createBackingStorage() {
		return new LDefaultStorage<PropertyType>(this.lmodel);
	}
}
