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
import de.cesr.lara.components.container.LaraCapacityManageableContainer;
import de.cesr.lara.components.container.LaraContainer;
import de.cesr.lara.components.container.exceptions.LContainerFullException;
import de.cesr.lara.components.container.exceptions.LInvalidTimestampException;
import de.cesr.lara.components.container.exceptions.LRemoveException;
import de.cesr.lara.components.container.exceptions.LRetrieveException;

/**
 * The interface is intended for simple storages that do not care about time
 * steps (i.e. store only the latest entry for a specific key).
 * 
 * @date 22.02.2010
 * 
 * @param <PropertyType>
 */
public interface LaraOverwriteStorage<PropertyType extends LaraProperty<PropertyType, ?>>
		extends LaraContainer<PropertyType>,
		LaraCapacityManageableContainer<PropertyType> {
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

	/*
	 * FETCHING
	 */

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

	/*
	 * UTIL
	 */

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
	 *            the property to be stored.
	 * @throws LContainerFullException
	 * @throws LInvalidTimestampException
	 */
	public void store(PropertyType propertyToStore)
			throws LContainerFullException, LInvalidTimestampException;

}
