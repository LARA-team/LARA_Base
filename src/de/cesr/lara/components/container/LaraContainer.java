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
package de.cesr.lara.components.container;

import de.cesr.lara.components.LaraProperty;

/**
 * Defines methods to retrieve information about a container regarding its
 * capacity and its degree of capacity utilisation.
 * 
 * @param <PropertyType>
 *            the type of properties this container shall store
 */
public interface LaraContainer<PropertyType extends LaraProperty<?>> extends
		Iterable<PropertyType> {

	/**
	 * A constant to indicate that a storage has (virtually) unlimited capacity.
	 */
	public static final int UNLIMITED_CAPACITY = -1;

	/**
	 * Returns the capacity of this container, i.e. the number of items that can
	 * be stored or {@link #UNLIMITED_CAPACITY} (the default) if it is
	 * (virtually) unlimited.
	 * 
	 * @return the capacity of this storage.
	 */
	public int getCapacity();

	/**
	 * Returns the current size, i.e. the number of items currently stored.
	 * 
	 * @return the current size, i.e. the number of items currently stored.
	 */
	public int getSize();

	/**
	 * Returns true, if and only if this container is empty.
	 * 
	 * @return true, if and only if this container is empty.0
	 */
	public boolean isEmpty();

	/**
	 * Returns true, if and only if this container is full (see also
	 * {@link LaraCapacityManager}).
	 * 
	 * @return true, if and only if this container is full.
	 */
	public boolean isFull();
}
