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

import de.cesr.lara.components.LaraProperty;
import de.cesr.lara.components.container.LaraCapacityManageableContainer;
import de.cesr.lara.components.container.exceptions.LRemoveException;

/**
 * Memorises only one property per key. I.e., there is no history.
 * 
 * @param <PropertyType>
 * 
 */
public interface LaraOverwriteMemory<PropertyType extends LaraProperty<?>>
		extends LaraMemory<PropertyType>, LaraCapacityManageableContainer<PropertyType> {

	/**
	 * @param key
	 * @return the property to forget
	 * @throws LRemoveException
	 */
	public PropertyType forget(String key) throws LRemoveException;

	/**
	 * @see de.cesr.lara.components.container.memory.LaraMemory#forgetAll(java.util.Collection)
	 */
	@Override
	public Collection<PropertyType> forgetAll(
			Collection<PropertyType> propertiesToBeRemoved)
			throws LRemoveException;

}
