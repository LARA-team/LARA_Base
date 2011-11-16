/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 16.12.2009
 */
package de.cesr.lara.components.container.storage;

import java.util.Collection;
import java.util.Set;

import de.cesr.lara.components.LaraProperty;
import de.cesr.lara.components.container.LaraContainer;
import de.cesr.lara.components.container.exceptions.LContainerFullException;
import de.cesr.lara.components.container.exceptions.LInvalidTimestampException;
import de.cesr.lara.components.container.exceptions.LRemoveException;
import de.cesr.lara.components.container.exceptions.LRetrieveException;
import de.cesr.lara.components.container.exceptions.LStoreException;

/**
 * The <code>LaraStorage</code> provides a general abstract data type that
 * stores instances of {@link LaraProperty} each identified by a key of type
 * {@link String}.
 * 
 * <code>PropertyType</code> specifies the type of {@link LaraProperty}s the
 * storage may store (<code>Object</code> in most cases).
 * 
 * Any implementing class must guarantee (at least) all of following criteria of
 * the contract for a <code>LaraStorage</code>:
 * <nl>
 * <li>The combination of key and timestamp uniquely defines a property, i.e.
 * for each pair of key/timestamp the storage will hold at most one item.
 * <li>
 * </nl>
 * 
 * @author Michael Elbers
 * @param <PropertyType>
 * @date 16.03.2010
 * 
 */
public interface LaraStorage<PropertyType extends LaraProperty<?>> extends
		LaraContainer<PropertyType> {

	/*
	 * STORING
	 */

	// Observer management:
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
	public void addStoragePropertyListener(
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
	 * Returns <code>true</code>, if, and only if, this storage contains a
	 * property for the specified {@code key} and {@code step}.
	 * 
	 * @param key
	 * @param step
	 * @return <code>true</code>, if, and only if, this storage contains a
	 *         property for the specified {@code key} and {@code step}.
	 * 
	 */
	public boolean contains(String key, int step);

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
	 * Generic method that returns the first property that was stored in
	 * {@code step} and identified with {@code key}.
	 * 
	 * @param key
	 *            identifier for the property to be retrieved.
	 * @param step
	 *            step in which the property was stored.
	 * @return the first property that was stored in {@code step} and identified
	 *         with {@code key}.
	 * @throws LRetrieveException
	 */
	public PropertyType fetch(String key, int step) throws LRetrieveException;

	/*
	 * FETCHING
	 */

	/**
	 * Generic method that returns a collection of all properties found that are
	 * identified by {@code key}.
	 * 
	 * @param key
	 *            identifier for the properties to be retrieved.
	 * @return the properties of the specified type that were stored in
	 *         {@code step} and identified with {@code key}.
	 * @throws LRetrieveException
	 */
	public Collection<PropertyType> fetchAll(String key)
			throws LRetrieveException;

	/**
	 * Returns a set of Strings that represent the keys of properties stored in
	 * the storage such that any property in the storage is represented.
	 * 
	 * @return set of keys that represent a property stored in the storage
	 */
	public Set<String> getAllPropertyKeys();

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

	// /**
	// * Generic method that returns the first property that is of the specified
	// type, was stored in {@code step} and
	// identified with {@code key}.
	// *
	// * @param propertyType
	// * @param key
	// * identifier for the property to be retrieved.
	// * @param step
	// * step in which the property was stored.
	// * @return the property of the specified type that was stored in {@code
	// step} and identified with {@code key}.
	// */
	// public PropertyType fetch(Class<PropertyType> propertyType, String key,
	// int step) throws LRetrieveException;
	//
	// /**
	// * Generic method that returns the most recently stored property that is
	// of the specified type and identified with
	// {@code key}.
	// *
	// * @param propertyType
	// * @param key
	// * identifier for the property to be retrieved.
	// * @return the most recently stored property that is of the specified type
	// and identified with {@code key}.
	// */
	// public PropertyType fetch(Class<PropertyType> propertyType, String key)
	// throws LRetrieveException;
	//
	// /**
	// * Generic method that returns a collection of all properties found that
	// are of the specified type and are
	// identified by {@code key}.
	// *
	// * @param propertyType
	// * @param key
	// * identifier for the properties to be retrieved.
	// * @return the properties of the specified type that were stored in {@code
	// step} and identified with {@code key}.
	// */
	// public Collection<PropertyType> fetchAll(Class<PropertyType>
	// propertyType, String key) throws
	// LRetrieveException;

	/*
	 * UTIL
	 */

	/**
	 * Removes the property with the specified key that was stored in
	 * <code>step</code> from this storage.
	 * 
	 * @param key
	 *            identifier for the property to be removed.
	 * @param step
	 *            the step in which the property to be removed was stored.
	 *            Created by Michael Elbers on 21.02.2010
	 * @return the property that was removed
	 * @throws LRemoveException
	 */
	public PropertyType remove(String key, int step) throws LRemoveException;

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

	/**
	 * Removes all properties that are identified by the specified key.
	 * 
	 * @param key
	 *            key identifier for the properties to be removed. Created by
	 *            Michael Elbers on 21.02.2010
	 * @return the properties that were removed
	 * @throws LRemoveException
	 */
	public Collection<PropertyType> removeAll(String key)
			throws LRemoveException;

	/**
	 * @param eventType
	 *            the category of storage property events the listeners shall be
	 *            removed from
	 * @param listener
	 *            the listener to be removed
	 */
	public void removeStoragePropertyListener(
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
