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
import de.cesr.lara.components.container.storage.LaraStorage;

/**
 * Interface for classes that implement a capacity management strategy for
 * {@link LaraStorage}s, i.e. to remove an item from the storage at some
 * position or increase the storages capacity whenever the storage is full and a
 * new item is to be stored.
 * 
 * @author Michael Elbers
 * @param <PropertyType>
 *            The type of properties the according container stores
 * @date 22.02.2010
 * 
 */
public interface LaraCapacityManager<PropertyType extends LaraProperty<? extends PropertyType, ?>> {

	/**
	 * Tries to apply this manager's capacity management strategy to the storage
	 * provided and seeks to free space for an additional property. Storages
	 * using a capacity manager should <b>always</b> call this method
	 * <b>before</b> each item they try to add (i.e. they should make the
	 * following call <code>manager.manage(this)</code>) in case the storage is
	 * full. They should only try to add the new item if the capacity manager
	 * has returned <code>true</code>.
	 * 
	 * @param view
	 *            view to the container to free space withing
	 * @return true if management was successful, i.e. if at least one more
	 *         (additional) new item can be added to the storage than before,
	 *         false otherwise.
	 */
	public boolean freeSpace(LaraCapacityManagementView<PropertyType> view);
}
