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

import java.util.Iterator;

import de.cesr.lara.components.LaraProperty;

/**
 * The management view constitutes a view to the underlying container that
 * enables {@link LaraCapacityManager}s to manager the container.
 * 
 * @author Michael Elbers
 * 
 * @param <PropertyType>
 */
public interface LaraCapacityManagementView<PropertyType extends LaraProperty<?, ?>>
		extends Iterable<PropertyType> {

	/**
	 * @param item
	 */
	public void remove(PropertyType property);
	
	/**
	 * Provides an iterator over the underlying container
	 * 
	 * @return iterator over underlying container
	 */
	@Override
	public Iterator<PropertyType> iterator();
}
