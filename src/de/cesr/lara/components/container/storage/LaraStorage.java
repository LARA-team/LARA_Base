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
package de.cesr.lara.components.container.storage;

import java.util.Collection;
import java.util.Set;

import de.cesr.lara.components.LaraProperty;
import de.cesr.lara.components.container.LaraContainer;
import de.cesr.lara.components.container.exceptions.LContainerFullException;
import de.cesr.lara.components.container.exceptions.LInvalidTimestampException;
import de.cesr.lara.components.container.exceptions.LRemoveException;
import de.cesr.lara.components.container.exceptions.LRetrieveException;

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
public interface LaraStorage<PropertyType extends LaraProperty<? extends PropertyType, ?>>
		extends LaraContainer<PropertyType> {

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
	 * Checks whether the storage contains a property of the given type with the
	 * given key.
	 * 
	 * @param key
	 * @return true if such a property is contained
	 */
	public boolean contains(Class<?> propertyType, String key);

	/**
	 * Returns <code>true</code>, if, and only if, this storage contains the
	 * given property.
	 * 
	 * @param property
	 * @return <code>true</code>, if, and only if, this storage contains the
	 *         given property.
	 * 
	 */
	public boolean contains(PropertyType property);

	/**
	 * Checks whether the memory contains the given {@link LaraProperty} with
	 * the given key.
	 * 
	 * @param key
	 * @return true if such a property is contained
	 */
	public boolean contains(PropertyType property, String key);

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

	/*
	 * FETCHING
	 */

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
	 * Generic method that returns the most recently stored property that is of
	 * the specified type and identified with {@code key}.
	 * 
	 * @param propertyType
	 * @param key
	 *            identifier for the property to be retrieved.
	 * @return the most recently stored property that is of the specified type
	 *         and identified with {@code key}.
	 */
	public <RequestPropertyType extends PropertyType> RequestPropertyType fetch(
			Class<RequestPropertyType> propertyType, String key)
			throws LRetrieveException;

	/**
	 * Generic method that returns the first property that is of the specified
	 * type, was stored in {@code step} and identified with {@code key}.
	 * 
	 * @param propertyType
	 * @param key
	 *            identifier for the property to be retrieved.
	 * @param step
	 *            step in which the property was stored.
	 * @return the property of the specified type that was stored in
	 *         {@code step} and identified with {@code key}.
	 */
	public <RequestPropertyType extends PropertyType> RequestPropertyType fetch(
			Class<RequestPropertyType> propertyType, String key, int step)
			throws LRetrieveException;

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

	/**
	 * Generic method that returns a collection of all properties found.
	 * 
	 * @return all properties.
	 * @throws LRetrieveException
	 */
	public Collection<PropertyType> fetchAll() throws LRetrieveException;

	/**
	 * Generic method that returns a collection of all properties found.
	 * 
	 * @param propertyType
	 *            identifier for the properties to be retrieved.
	 * @return the properties of the specified type that were stored in
	 *         {@code step} and identified with {@code key}.
	 */
	public <RequestPropertyType extends PropertyType> Collection<RequestPropertyType> fetchAll(
			Class<RequestPropertyType> propertyType) throws LRetrieveException;

	/**
	 * Generic method that returns a collection of all properties found that are
	 * of the specified type and are identified by {@code key}.
	 * 
	 * @param propertyType
	 * @param key
	 *            identifier for the properties to be retrieved.
	 * @return the properties of the specified type that were stored in
	 *         {@code step} and identified with {@code key}.
	 */
	public <RequestPropertyType extends PropertyType> Collection<RequestPropertyType> fetchAll(
			Class<RequestPropertyType> propertyType, String key)
			throws LRetrieveException;

	/*
	 * UTIL
	 */

	/**
	 * Generic method that returns a collection of all properties found that are
	 * identified by {@code key}.
	 * 
	 * @param key
	 *            identifier for the properties to be retrieved.
	 * @return the properties of the specified type that are identified with
	 *         {@code key}.
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
	 *            the property to be removed.
	 * @return the property that was removed
	 * @throws LRemoveException
	 */
	public PropertyType remove(PropertyType propertyToRemove)
			throws LRemoveException;

	/**
	 * Removes the property with the specified key that was stored in
	 * <code>step</code> from this storage.
	 * 
	 * @param key
	 *            identifier for the property to be removed.
	 * @param step
	 *            the step in which the property to be removed was stored.
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
	 *            key identifier for the properties to be removed.
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
	 * Tries to add the specified property to this storage. In case the storage
	 * already contains a property of the same key for the same time-stamp the
	 * "old" property is overwritten.
	 * 
	 * @param propertyToStore
	 *            the property to be stored.
	 * @throws LStoreException
	 * @throws LContainerFullException
	 * @throws LInvalidTimestampException
	 */
	public void store(PropertyType propertyToStore)
			throws LContainerFullException, LInvalidTimestampException;

}
