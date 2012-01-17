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
 * {@link LaraContainer}s implementing this interface accept a
 * {@link LaraCapacityManager} and commit themselves to calling its
 * {@link LaraCapacityManager#manage(LaraCapacityManagementView))} whenever they
 * are full and a new item is to be stored. Usually, the manager will then
 * remove an item from the container at a position according to its strategy and
 * thus clear space for the new item.
 * 
 * @author Michael Elbers
 * @param <ItemType>
 * @date 22.02.2010
 * 
 */
public interface LaraCapacityManageableContainer<PropertyType extends LaraProperty<?>>
		extends LaraContainer<PropertyType> {

	/**
	 * @return
	 */
	public LaraCapacityManagementView<PropertyType> getCapacityManagementView();

	/**
	 * @return
	 */
	public LaraCapacityManager<PropertyType> getCapacityManager();

	/**
	 * 
	 * @param capacity
	 * @return
	 */
	public boolean setCapacity(int capacity);

	/**
	 * @param manager
	 */
	public void setCapacityManager(LaraCapacityManager<PropertyType> manager);

}
