/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 18.05.2010
 */
package de.cesr.lara.components.container.storage.impl;

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
import de.cesr.lara.components.container.LaraCapacityManagementView;
import de.cesr.lara.components.container.LaraCapacityManager;
import de.cesr.lara.components.container.exceptions.LRetrieveException;
import de.cesr.lara.components.container.storage.LaraOverwriteStorage;
import de.cesr.lara.components.container.storage.LaraStorageListener;
import de.cesr.lara.components.container.storage.LaraStorageListener.StorageEvent;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;

/**
 * A storage that does not care about time stamps since it only stores the most
 * recent property for every key. This storage enables capacity management!
 * 
 * NOTE: Other than LDefaultStorage this implementation does not throw
 * LInvalidTimestampException()
 * 
 * @param <PropertyType>
 * 
 */
public class LDefaultSimpleStorage<PropertyType extends LaraProperty<?>>
		implements LaraOverwriteStorage<PropertyType> {

	/**
	 * Higher values decrease the space overhead but increase the lookup cost
	 * (reflected in most of the operations of the HashMap class, including get
	 * and put)
	 */
	private static final float DEFAULT_LOAD_CAPACITY = 0.75f;

	/**
	 * number of buckets in the hash table
	 */
	private static final int DEFAULT_NUM_BUCKETS = 32;

	/**
	 * 
	 */
	public static final int DEFAULT_INITIAL_CAPACITY = (int) (DEFAULT_NUM_BUCKETS * DEFAULT_LOAD_CAPACITY);

	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger
			.getLogger(LDefaultSimpleStorage.class);
	private int capacity = UNLIMITED_CAPACITY;
	private LaraCapacityManager<PropertyType> capacityManager = null;

	private Map<String, PropertyType> properties;

	// observer management:
	private MultiMap<LaraStorageListener.StorageEvent, LaraStorageListener> propertyListeners = new MultiHashMap<LaraStorageListener.StorageEvent, LaraStorageListener>();

	/**
	 * 
	 */
	public LDefaultSimpleStorage() {
		this(UNLIMITED_CAPACITY);
	}

	/**
	 * @param capacity
	 */
	public LDefaultSimpleStorage(int capacity) {
		this.capacity = capacity;
		if (capacity <= 0) {
			properties = new HashMap<String, PropertyType>(
					DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_CAPACITY);
		} else {
			properties = new HashMap<String, PropertyType>(
					(int) (capacity / DEFAULT_LOAD_CAPACITY),
					DEFAULT_LOAD_CAPACITY);
		}
	}

	/**
	 * @see de.cesr.lara.components.container.storage.LaraStorage#addStoragePropertyListener(de.cesr.lara.components.container.storage.LaraStorageListener.StorageEvent,
	 *      de.cesr.lara.components.container.storage.LaraStorageListener)
	 */
	@Override
	public void addStoragePropertyObserver(StorageEvent eventType,
			LaraStorageListener listener) {
		propertyListeners.put(eventType, listener);
	}

	@Override
	public void clear() {
		properties.clear();
	}

	/**
	 * TODO test
	 * 
	 * @see de.cesr.lara.components.container.storage.LaraStorage#contains(java.lang.String)
	 */
	@Override
	public boolean contains(String key) {
		return properties.containsKey(key);
	}

	@Override
	public PropertyType fetch(String key) {
		if (isEmpty()) {
			throw new LRetrieveException("No entry found. Memory is empty.");
		}
		if (!properties.containsKey(key)) {
			throw new LRetrieveException("No entry found for key " + key + ".");
		}
		PropertyType propertyToStore = properties.get(key);
		// observer notification:
		if (propListenersContainsEventKey(StorageEvent.PROPERTY_FETCHED)) {
			for (LaraStorageListener listener : getPropertyListeners(LaraStorageListener.StorageEvent.PROPERTY_FETCHED)) {
				listener.storageEventOccured(
						LaraStorageListener.StorageEvent.PROPERTY_FETCHED,
						propertyToStore);
			}
		}
		return propertyToStore;
	}

	/**
	 * @see de.cesr.lara.components.container.storage.LaraStorage#getAllPropertyKeys()
	 */
	@Override
	public Set<String> getAllPropertyKeys() {
		return properties.keySet();
	}

	@Override
	public int getCapacity() {
		return this.capacity;
	}

	@Override
	public LaraCapacityManagementView<PropertyType> getCapacityManagementView() {
		return new LaraCapacityManagementView<PropertyType>() {

			@Override
			public int getCapacity() {
				return LDefaultSimpleStorage.this.getCapacity();
			}

			@Override
			public int getSize() {
				return LDefaultSimpleStorage.this.getSize();
			}

			@Override
			public boolean isEmpty() {
				return LDefaultSimpleStorage.this.isEmpty();
			}

			@Override
			public boolean isFull() {
				return LDefaultSimpleStorage.this.isFull();
			}

			@Override
			public Iterator<PropertyType> iterator() {
				return LDefaultSimpleStorage.this.iterator();
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
				LDefaultSimpleStorage.this.removeWithoutNotification(item
						.getKey());
			}
		};
	}

	@Override
	public LaraCapacityManager<PropertyType> getCapacityManager() {
		return this.capacityManager;
	}

	@Override
	public int getSize() {
		return properties.size();
	}

	@Override
	public boolean isEmpty() {
		return properties.isEmpty();
	}

	@Override
	public boolean isFull() {
		return capacity == UNLIMITED_CAPACITY ? false : getSize() == capacity;
	}

	/**
	 * In no particular order. TODO Throw Exception in case empty? (ME)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<PropertyType> iterator() {
		return properties.values().iterator();
	}

	@Override
	public PropertyType remove(PropertyType propertyToRemove) {
		// (observer notification in called method)
		return remove(propertyToRemove.getKey());
	}

	@Override
	public PropertyType remove(String key) {
		if (!properties.containsKey(key)) {
			throw new LRetrieveException("No entry with key " + key
					+ " found. Nothing removed.");
		}

		PropertyType removedProperty = properties.get(key);
		// observer notification:
		if (propListenersContainsEventKey(StorageEvent.PROPERTY_REMOVED)) {
			for (LaraStorageListener listener : getPropertyListeners(LaraStorageListener.StorageEvent.PROPERTY_REMOVED)) {
				listener.storageEventOccured(
						LaraStorageListener.StorageEvent.PROPERTY_REMOVED,
						removedProperty);
			}
		}
		properties.remove(key);
		return removedProperty;
	}

	@Override
	public Collection<PropertyType> removeAll(
			Collection<PropertyType> propertiesToBeRemoved) {
		Collection<PropertyType> removedProperties = new HashSet<PropertyType>();
		if (propertiesToBeRemoved != null) {
			for (PropertyType propertyToRemove : propertiesToBeRemoved) {
				removedProperties.add(propertyToRemove);
			}
			for (PropertyType prop : removedProperties) {
				// (observer notification done in called method):
				remove(prop);
			}
		}
		return removedProperties;
	}

	/**
	 * @see de.cesr.lara.components.container.storage.LaraStorage#removeStoragePropertyListener(de.cesr.lara.components.container.storage.LaraStorageListener.StorageEvent,
	 *      de.cesr.lara.components.container.storage.LaraStorageListener)
	 */
	@Override
	public void removeStoragePropertyObserver(StorageEvent eventType,
			LaraStorageListener listener) {
		propertyListeners.remove(eventType, listener);
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
		// observer notification:
		if (contains(propertyToStore.getKey())) {
			if (propListenersContainsEventKey(StorageEvent.PROPERTY_OVERWRITTEN)) {
				for (LaraStorageListener listener : getPropertyListeners(LaraStorageListener.StorageEvent.PROPERTY_OVERWRITTEN)) {
					listener.storageEventOccured(
							LaraStorageListener.StorageEvent.PROPERTY_OVERWRITTEN,
							properties.get(propertyToStore.getKey()));
				}
			}
			if (propListenersContainsEventKey(StorageEvent.PROPERTY_RESTORED)) {
				for (LaraStorageListener listener : getPropertyListeners(LaraStorageListener.StorageEvent.PROPERTY_RESTORED)) {
					listener.storageEventOccured(
							LaraStorageListener.StorageEvent.PROPERTY_RESTORED,
							propertyToStore);
				}
			}
		} else {
			if (isFull()) {
				if (!capacityManager.manage(this)) {
					return;
				}
			}
			if (propListenersContainsEventKey(StorageEvent.PROPERTY_STORED)) {
				for (LaraStorageListener listener : getPropertyListeners(LaraStorageListener.StorageEvent.PROPERTY_STORED)) {
					listener.storageEventOccured(
							LaraStorageListener.StorageEvent.PROPERTY_STORED,
							propertyToStore);
				}
			}
		}
		properties.put(propertyToStore.getKey(), propertyToStore);

		if (logger.isDebugEnabled()) {
			logger.debug("Property stored: " + propertyToStore);
		}
	}

	/*
	 * CAPACITY MANAGEMENT
	 */

	@Override
	public String toString() {
		final String NEWLINE = System.getProperty("line.separator");
		StringBuffer buffer = new StringBuffer();
		for (PropertyType property : this) {
			buffer.append("\t" + property.toString() + NEWLINE);
		}
		return buffer.toString();
	}

	/**
	 * @param key
	 * @return
	 */
	private PropertyType removeWithoutNotification(String key) {
		if (!properties.containsKey(key)) {
			throw new LRetrieveException("No entry with key " + key
					+ " found. Nothing removed.");
		}
		PropertyType removedProperty = properties.get(key);
		properties.remove(key);
		return removedProperty;
	}

	/**
	 * @param event
	 * @return
	 */
	protected Collection<LaraStorageListener> getPropertyListeners(
			LaraStorageListener.StorageEvent event) {
		return propertyListeners.get(event);
	}

	/**
	 * @param event
	 * @return
	 */
	protected boolean propListenersContainsEventKey(StorageEvent event) {
		return propertyListeners.containsKey(event);
	}
}
