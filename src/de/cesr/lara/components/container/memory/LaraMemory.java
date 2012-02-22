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
package de.cesr.lara.components.container.memory;


import java.util.Collection;
import java.util.Set;

import de.cesr.lara.components.LaraProperty;
import de.cesr.lara.components.container.LaraContainer;
import de.cesr.lara.components.container.exceptions.LContainerFullException;
import de.cesr.lara.components.container.exceptions.LInvalidTimestampException;
import de.cesr.lara.components.container.exceptions.LRemoveException;
import de.cesr.lara.components.container.exceptions.LRetrieveException;


/**
 * Interface for all agent memories in LARA
 * 
 * @param <PropertyType>
 *        the type of properties the memory may memorise
 */
public interface LaraMemory<PropertyType extends LaraProperty<? extends PropertyType, ?>>
		extends LaraContainer<PropertyType> {

	/**
	 * constant representing unlimited retention time
	 */
	public static final int	UNLIMITED_RETENTION	= -2;

	/**
	 * Sets the default retention time
	 * 
	 * @param defaultRetentionTime
	 *        the default retention time
	 */
	public void setDefaultRetentionTime(int defaultRetentionTime);

	/**
	 * Return the default retention time
	 * 
	 * @return the default retention time
	 */
	public int getDefaultRetentionTime();

	/*
	 * STORING
	 */

	/**
	 * Tries to add the specified property to this memory applying the default
	 * retention time. In case the memory already contains a property of the
	 * same key for the same time-stamp the "old" property is overwritten.
	 * 
	 * @param propertyToMemorize
	 *            the property to be memorised.
	 * @throws LContainerFullException
	 * @throws LInvalidTimestampException
	 */
	public void memorize(PropertyType propertyToMemorize) throws LContainerFullException,
			LInvalidTimestampException;

	/**
	 * Tries to add the specified property to this memory with the given
	 * retention time. In case the memory already contains a property of the
	 * same key for the same time-stamp the "old" property is overwritten.
	 * 
	 * @param propertyToMemorize
	 *            the property to be memorised.
	 * @param retentionTime
	 * @throws LContainerFullException
	 * @throws LInvalidTimestampException
	 */
	public void memorize(PropertyType propertyToMemorize, int retentionTime) throws 
			LContainerFullException, LInvalidTimestampException;

	/*
	 * REFRESHING
	 */

	/**
	 * Tries to refresh the specified property in this memory applying the default retention time.
	 * 
	 * @param propertyToMemorize
	 *        the property to be memorised.
	 * @throws LContainerFullException
	 * @throws LInvalidTimestampException
	 */
	public void refresh(PropertyType propertyToMemorize) throws LContainerFullException,
			LInvalidTimestampException;

	/**
	 * Refreshes the property with the specified key that was memorised last from this memory.
	 * 
	 * @param key
	 *        identifier for the property to be refreshed.
	 * @throws LRemoveException
	 */
	public void refresh(String key) throws LRemoveException;

	/**
	 * Refreshes the property with the specified key that was memorised in <code>step</code> from this memory.
	 * 
	 * @param key
	 *        identifier for the property to be refreshed.
	 * @param step
	 *        the step in which the property to be removed was memorised.
	 * @throws LRemoveException
	 */
	public void refresh(String key, int step) throws LRemoveException;

	/**
	 * Refreshes the property with the specified key that was memorised last
	 * from this memory and assigns the given retention time.
	 * 
	 * @param propertyToMemorize
	 *            the property to be memorised.
	 * @param retentionTime
	 *            time the property lasts in memory
	 * @throws LContainerFullException
	 * @throws LInvalidTimestampException
	 */
	public void refresh(String key, int step, int retentionTime)
			throws LInvalidTimestampException, LRemoveException;

	/**
	 * Refreshes the property from this memory and assigns the given retention
	 * time.
	 * 
	 * @param propertyToMemorize
	 *            the property to be memorised.
	 * @param retentionTime
	 *            time the property lasts in memory
	 * @throws LContainerFullException
	 * @throws LInvalidTimestampException
	 */
	public void refresh(PropertyType propertyToMemorize, int retentionTime)
			throws LInvalidTimestampException, LRemoveException;

	/*
	 * REMOVING
	 */

	/**
	 * Removes the specified property from this memory.
	 * 
	 * @param propertyToRemove
	 *            the property to be removed.
	 * @return the property that was forgotten
	 * @throws LRemoveException
	 */
	public PropertyType forget(PropertyType propertyToRemove) throws LRemoveException;

	/**
	 * Removes the property with the specified key that was memorised in
	 * <code>step</code> from this memory.
	 * 
	 * @param key
	 *            identifier for the property to be removed.
	 * @param step
	 *            the step in which the property to be removed was memorised.
	 * @return the property that was forgotten
	 * @throws LRemoveException
	 */
	public PropertyType forget(String key, int step) throws LRemoveException;

	/**
	 * Removes all properties in the specified collection from this memory.
	 * 
	 * @param propertiesToBeRemoved
	 *        the properties to be removed
	 * @return the properties that were forgotten
	 * @throws LRemoveException
	 * 
	 */
	public Collection<PropertyType> forgetAll(Collection<PropertyType> propertiesToBeRemoved)
			throws LRemoveException;

	/**
	 * Removes all properties that are identified by the specified key.
	 * 
	 * @param key
	 *            key identifier for the properties to be removed.
	 * @return the properties that were forgotten
	 * @throws LRemoveException
	 */
	public Collection<PropertyType> forgetAll(String key) throws LRemoveException;

	/**
	 * Clears the memory, i.e. removes all properties.
	 * 
	 * @throws LRemoveException
	 */
	public void clear() throws LRemoveException;

	/*
	 * FETCHING
	 */

	/**
	 * Generic method that returns the first property that was memorised in {@code step} and identified with {@code key}
	 * .
	 * 
	 * @param key
	 *        identifier for the property to be retrieved.
	 * @param step
	 *        step in which the property was memorised.
	 * @return the first property that was memorised in {@code step} and identified with {@code key}.
	 * @throws LRetrieveException
	 */
	public PropertyType recall(String key, int step) throws LRetrieveException;

	/**
	 * Generic method that returns the most recently memorised property that is identified with {@code key}.
	 * 
	 * @param key
	 *        identifier for the property to be retrieved.
	 * @return the most recently memorised property that is identified with {@code key}.
	 * @throws LRetrieveException
	 */
	public PropertyType recall(String key) throws LRetrieveException;

	/**
	 * Generic method that returns a collection of all properties found that are identified by {@code key}.
	 * 
	 * @param key
	 *        identifier for the properties to be retrieved.
	 * @return the properties of the specified type that were stored in {@code step} and identified with {@code key}.
	 * @throws LRetrieveException
	 */
	public Collection<PropertyType> recallAll(String key) throws LRetrieveException;

	/**
	 * Generic method that returns the first property found that is of the
	 * specified type (including sub types), was memorised in {@code step} and
	 * identified with {@code key}.
	 * 
	 * @param propertyType
	 * @param key
	 *            identifier for the property to be retrieved.
	 * @param step
	 *            step in which the property was memorised.
	 * @return the property of the specified type that was memorised in
	 *         {@code step} and identified with {@code key}.
	 * @throws LRetrieveException
	 */
	public <RequestPropertyType extends PropertyType> RequestPropertyType recall(
			Class<RequestPropertyType> propertyType,
			String key, int step) throws LRetrieveException;

	/**
	 * Generic method that returns the most recently memorised property that is
	 * of the specified type (including sub types) and identified with
	 * {@code key}.
	 * 
	 * @param propertyType
	 * @param key
	 *            identifier for the property to be retrieved.
	 * @return the most recently memorised property that is of the specified
	 *         type and identified with {@code key}.
	 * @throws LRetrieveException
	 */
	public <RequestPropertyType extends PropertyType> RequestPropertyType recall(
			Class<RequestPropertyType> propertyType,
			String key) throws LRetrieveException;

	/**
	 * Generic method that returns a collection of all properties found that are
	 * of the specified type (including sub types).
	 * 
	 * @param propertyType
	 * @return the properties of the specified type that were memorised in
	 *         {@code step} and identified with {@code key}.
	 * @throws LRetrieveException
	 */
	public <RequestPropertyType extends PropertyType> Collection<RequestPropertyType> recallAll(
			Class<RequestPropertyType> propertyType) throws LRetrieveException;

	/**
	 * Generic method that returns a collection of all properties found that are
	 * of the specified type (including sub types)and are identified by
	 * {@code key}.
	 * 
	 * @param propertyType
	 * @param key
	 *            identifier for the properties to be retrieved.
	 * @return the properties of the specified type that were memorised in
	 *         {@code step} and identified with {@code key}.
	 * @throws LRetrieveException
	 */
	public <RequestPropertyType extends PropertyType> Collection<RequestPropertyType> recallAll(
			Class<RequestPropertyType> propertyType, String key)
			throws LRetrieveException;

	/**
	 * Returns the remaining retention time for the given property.
	 * 
	 * @param property
	 * @return the remaining retention time for the given property.
	 */
	public int getRetentionTime(PropertyType property);

	/**
	 * Returns a set of Strings that represent the keys of properties memorised in the memory such that any property in
	 * the memory is represented.
	 * 
	 * @return set of keys that represent a property memorised in the memory
	 */
	public Set<String> getAllPropertyKeys();

	/**
	 * Checks whether the memory contains a {@link LaraProperty} with the given key.
	 * 
	 * @param key
	 * @return true if such a property is contained
	 */
	public boolean contains(String key);

	/**
	 * Checks whether the memory contains a {@link LaraProperty} with the given
	 * key for the given time stamp.
	 * 
	 * @param key
	 * @param timestamp
	 * @return true if such a property is contained
	 */
	public boolean contains(String key, int timestamp);

	/**
	 * Checks whether the memory contains the given {@link LaraProperty}.
	 * 
	 * @param key
	 * @return true if such a property is contained
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
	 * Checks whether the memory contains a property of the given type with the
	 * given key.
	 * 
	 * @param key
	 * @return true if such a property is contained
	 */
	public boolean contains(Class<?> propertyType,
			String key);

	// Observer management:
	/**
	 * Adds a given {@link LaraMemoryListener} for the specified
	 * {@link LaraMemoryListener.MemoryEvent}
	 * 
	 * @param eventType
	 *        the category of memory property events the listeners shall be registered for
	 * @param listener
	 *        the listener to be registered
	 */
	public void addMemoryPropertyObserver(LaraMemoryListener.MemoryEvent eventType,
			LaraMemoryListener listener);

	/**
	 * @param eventType
	 *        the category of memory property events the listeners shall be removed from
	 * @param listener
	 *        the listener to be removed
	 */
	public void removeMemoryPropertyObserver(LaraMemoryListener.MemoryEvent eventType,
			LaraMemoryListener listener);

	/**
	 * Returns the name of this memory
	 * 
	 * @return the memory's name
	 */
	public String getName();

}
