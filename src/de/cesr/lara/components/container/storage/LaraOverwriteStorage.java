/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 18.05.2010
 */
package de.cesr.lara.components.container.storage;

import java.util.Collection;
import java.util.Set;

import de.cesr.lara.components.LaraProperty;
import de.cesr.lara.components.container.LaraCapacityManageable;
import de.cesr.lara.components.container.LaraContainer;
import de.cesr.lara.components.container.exceptions.LContainerFullException;
import de.cesr.lara.components.container.exceptions.LInvalidTimestampException;
import de.cesr.lara.components.container.exceptions.LRemoveException;
import de.cesr.lara.components.container.exceptions.LRetrieveException;
import de.cesr.lara.components.container.exceptions.LStoreException;

/**
 * The interface is intended for simple storages that do not care about time
 * steps (i.e. store only the latest entry for a specific key).
 * 
 * @param <PropertyType>
 */
public interface LaraOverwriteStorage<PropertyType extends LaraProperty<?>>
		extends LaraContainer<PropertyType>,
		LaraCapacityManageable<PropertyType> {
	/*
	 * STORING
	 */

	/**
	 * Adds a given {@link LaraStorageListener} for the specified
	 * {@link LaraStorageListener.StorageEvent}
	 * 
	 * @param eventType
	 *            the category of storage property events the listeners shall be
	 *            registered for
	 * @param listener
	 *            the listener to be registered
	 */
	public void addStoragePropertyObserver(
			LaraStorageListener.StorageEvent eventType,
			LaraStorageListener listener);

	/*
	 * REMOVING
	 */

	/**
	 * Clears the storage, i.e. removes all properties.
	 * 
	 * @throws LRemoveException
	 */
	public void clear() throws LRemoveException;

	/**
	 * Returns <code>true</code>, if, and only if, this storage contains a
	 * property for the specified {@code key}.
	 * 
	 * @param key
	 * @return <code>true</code>, if, and only if, this storage contains a
	 *         property for the specified {@code key}.
	 * 
	 */
	public boolean contains(String key);

	/**
	 * Generic method that returns the most recently stored property that is
	 * identified with {@code key}.
	 * 
	 * @param key
	 *            identifier for the property to be retrieved.
	 * @return the most recently stored property that is identified with
	 *         {@code key}.
	 * @throws LRetrieveException
	 */
	public PropertyType fetch(String key) throws LRetrieveException;

	/**
	 * Returns a set of Strings that represent the keys of properties stored in
	 * the storage such that any property in the storage is represented.
	 * 
	 * @return set of keys that represent a property stored in the storage
	 */
	public Set<String> getAllPropertyKeys();

	/*
	 * FETCHING
	 */

	/**
	 * Removes the specified property from this storage.
	 * 
	 * @param propertyToRemove
	 *            the property to be removed. Created by Michael Elbers on
	 *            22.02.2010
	 * @return the property that was removed
	 * @throws LRemoveException
	 */
	public PropertyType remove(PropertyType propertyToRemove)
			throws LRemoveException;

	/*
	 * UTIL
	 */

	/**
	 * Removes the specified property from this storage.
	 * 
	 * @param key
	 *            Key of property that shall be removed the property to be
	 *            removed. Created by Michael Elbers on 22.02.2010
	 * @return the property that was removed
	 * @throws LRemoveException
	 */
	public PropertyType remove(String key) throws LRemoveException;

	/**
	 * Removes all properties in the specified collection from this storage.
	 * 
	 * @param propertiesToBeRemoved
	 *            the properties to be removed
	 * @return the properties that were removed
	 * @throws LRemoveException
	 * 
	 */
	public Collection<PropertyType> removeAll(
			Collection<PropertyType> propertiesToBeRemoved)
			throws LRemoveException;

	// Observer management:

	/**
	 * @param eventType
	 *            the category of storage property events the listeners shall be
	 *            removed from
	 * @param listener
	 *            the listener to be removed
	 */
	public void removeStoragePropertyObserver(
			LaraStorageListener.StorageEvent eventType,
			LaraStorageListener listener);

	/**
	 * Tries to add the specified property to this storage.
	 * 
	 * @param propertyToStore
	 *            the property to be stored. Created by Michael Elbers on
	 *            21.02.2010
	 * @throws LStoreException
	 * @throws LContainerFullException
	 * @throws LInvalidTimestampException
	 */
	public void store(PropertyType propertyToStore) throws LStoreException,
			LContainerFullException, LInvalidTimestampException;

}
