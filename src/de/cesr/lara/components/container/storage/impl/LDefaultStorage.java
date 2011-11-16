/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * 
 */
package de.cesr.lara.components.container.storage.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;
import org.apache.log4j.Logger;

import de.cesr.lara.components.LaraProperty;
import de.cesr.lara.components.container.exceptions.LContainerFullException;
import de.cesr.lara.components.container.exceptions.LInvalidTimestampException;
import de.cesr.lara.components.container.exceptions.LRemoveException;
import de.cesr.lara.components.container.exceptions.LRetrieveException;
import de.cesr.lara.components.container.storage.LaraStorage;
import de.cesr.lara.components.container.storage.LaraStorageListener;
import de.cesr.lara.components.container.storage.LaraStorageListener.StorageEvent;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;

/**
 * TODO in which cases should this class check for invalid time stamps and throw
 * exceptions? (ME) TODO implement observer management for other events than
 * AUTO_REMOVED! (SH)
 * 
 * @param <PropertyType>
 */
public class LDefaultStorage<PropertyType extends LaraProperty<?>> implements
		LaraStorage<PropertyType> {

	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger.getLogger(LDefaultStorage.class);

	private Map<String, Map<Integer, PropertyType>> keywise = new HashMap<String, Map<Integer, PropertyType>>();

	// observer management:
	private MultiMap<LaraStorageListener.StorageEvent, LaraStorageListener> propertyListeners = new MultiHashMap<LaraStorageListener.StorageEvent, LaraStorageListener>();
	private int size;

	private Map<Integer, Map<String, PropertyType>> stepwise = new HashMap<Integer, Map<String, PropertyType>>();

	/**
	 * 
	 */
	public LDefaultStorage() {
		size = 0;
	}

	/**
	 * @see de.cesr.lara.components.container.storage.LaraStorage#addStoragePropertyListener(de.cesr.lara.components.container.storage.LaraStorageListener.StorageEvent,
	 *      de.cesr.lara.components.container.storage.LaraStorageListener)
	 */
	@Override
	public void addStoragePropertyListener(StorageEvent eventType,
			LaraStorageListener listener) {
		propertyListeners.put(eventType, listener);
	}

	@Override
	public void clear() {
		stepwise.clear();
		keywise.clear();
		size = 0;
	}

	/**
	 * TODO test
	 * 
	 * @see de.cesr.lara.components.container.storage.LaraStorage#contains(java.lang.String)
	 */
	@Override
	public boolean contains(String key) {
		if (!keywise.containsKey(key)) {
			return false;
		}

		// TODO is it efficient to construct the tree set every time? (SH) -
		// needs to be up to date...
		SortedSet<Integer> stepsDescendingOrder = new TreeSet<Integer>(
				stepwise.keySet()).descendingSet();
		boolean contains = false;
		for (Integer step : stepsDescendingOrder) {
			if (contains(key, step)) {
				contains = true;
			}
		}
		return contains;
	}

	/**
	 * @see de.cesr.lara.components.container.storage.LaraStorage#contains(java.lang.String,
	 *      int)
	 */
	@Override
	public boolean contains(String key, int step) {
		if (!keywise.containsKey(key)) {
			return false;
		}
		if (!stepwise.containsKey(step)) {
			return false;
		}
		return keywise.get(key).containsKey(step);
	}

	@Override
	public PropertyType fetch(String key) {
		if (isEmpty()) {
			throw new LRetrieveException("No entry found. Memory is empty.");
		}
		if (!keywise.containsKey(key)) {
			throw new LRetrieveException("No entry found for key " + key + ".");
		}
		SortedSet<Integer> stepsDescendingOrder = new TreeSet<Integer>(
				stepwise.keySet()).descendingSet();
		PropertyType property = null;
		for (Integer step : stepsDescendingOrder) {
			try {
				property = fetch(key, step);
				return property;
			} catch (LRetrieveException lre) {
				// ignore exception
			}
		}
		// These lines should never be reached!
		if (property == null) {
			throw new LRetrieveException("No entry found for key " + key + ".");
		}
		return null;
	}

	@Override
	public PropertyType fetch(String key, int step) {
		if (isEmpty()) {
			LRetrieveException ex = new LRetrieveException(
					"No entry found. Memory is empty.");
			logger.error(ex.getMessage() + ex.getStackTrace());
			throw ex;
		}
		if (!stepwise.containsKey(step)) {
			LRetrieveException ex = new LRetrieveException(
					"No entries for step " + step + ".");
			logger.error(ex.getMessage() + ex.getStackTrace());
			throw ex;
		}
		if (!stepwise.get(step).containsKey(key)) {
			throw new LRetrieveException("No entry found with key " + key
					+ " for step " + step + ".");

		}
		return stepwise.get(step).get(key);
	}

	@Override
	public Collection<PropertyType> fetchAll(String key) {
		if (isEmpty()) {
			throw new LRetrieveException("No entry found. Memory is empty.");
		}
		if (!keywise.containsKey(key)) {
			throw new LRetrieveException("No entries found for key " + key
					+ ".");
		}
		return keywise.get(key).values();
	}

	/**
	 * @see de.cesr.lara.components.container.storage.LaraStorage#getAllPropertyKeys()
	 */
	@Override
	public Set<String> getAllPropertyKeys() {
		return keywise.keySet();
	}

	@Override
	public int getCapacity() {
		return UNLIMITED_CAPACITY;
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public boolean isFull() {
		return false;
	}

	/**
	 * In no particular order. TODO Throw Exception in case empty? (ME)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<PropertyType> iterator() {
		Collection<PropertyType> properties = new HashSet<PropertyType>(size);

		for (Map<String, PropertyType> map : stepwise.values()) {
			for (PropertyType property : map.values()) {
				properties.add(property);
			}
		}

		return properties.iterator();
	}

	@Override
	public PropertyType remove(PropertyType propertyToRemove) {
		return remove(propertyToRemove.getKey(),
				propertyToRemove.getTimestamp());
	}

	@Override
	public PropertyType remove(String key, int step) {
		if (!stepwise.containsKey(step)) {
			throw new LRemoveException("No entries for step " + step
					+ ". Nothing removed.");
		}

		Map<String, PropertyType> keyMap = stepwise.get(step);

		if (!keyMap.containsKey(key)) {
			throw new LRetrieveException("No entry with key " + key
					+ " found for step " + step + ". Nothing removed.");
		}

		Map<Integer, PropertyType> stepMap = keywise.get(key);

		PropertyType removedProperty = keyMap.remove(key);
		stepMap.remove(step);

		if (stepMap.isEmpty()) {
			keywise.remove(key);
		}
		if (keyMap.isEmpty()) {
			stepwise.remove(step);
		}

		size--;

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
				remove(prop);
			}
		}
		return removedProperties;
	}

	@Override
	public Collection<PropertyType> removeAll(String key) {
		if (!keywise.containsKey(key)) {
			throw new LRemoveException("No entries found for key " + key
					+ ". Nothing removed.");
		}
		return removeAll(keywise.get(key).values());
	}

	/**
	 * @see de.cesr.lara.components.container.storage.LaraStorage#removeStoragePropertyListener(de.cesr.lara.components.container.storage.LaraStorageListener.StorageEvent,
	 *      de.cesr.lara.components.container.storage.LaraStorageListener)
	 */
	@Override
	public void removeStoragePropertyListener(StorageEvent eventType,
			LaraStorageListener listener) {
		propertyListeners.remove(eventType, listener);
	}

	@Override
	public void store(PropertyType propertyToStore) {
		if (propertyToStore.getTimestamp() < 0) {
			throw new LInvalidTimestampException();
		}
		if (isFull()) {
			throw new LContainerFullException();
		}

		boolean overwrite = contains(propertyToStore.getKey(),
				propertyToStore.getTimestamp());

		Map<String, PropertyType> keyMap = stepwise.get(propertyToStore
				.getTimestamp());
		if (keyMap == null) {
			keyMap = new HashMap<String, PropertyType>();
			stepwise.put(propertyToStore.getTimestamp(), keyMap);
		}
		keyMap.put(propertyToStore.getKey(), propertyToStore);

		Map<Integer, PropertyType> stepMap = keywise.get(propertyToStore
				.getKey());
		if (stepMap == null) {
			stepMap = new HashMap<Integer, PropertyType>();
			keywise.put(propertyToStore.getKey(), stepMap);
		}
		stepMap.put(propertyToStore.getTimestamp(), propertyToStore);

		if (!overwrite) {
			size++;
		}
	}

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
