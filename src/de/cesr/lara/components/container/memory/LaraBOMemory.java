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

import java.util.Set;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.container.exceptions.LRetrieveException;

/**
 * 
 * Interface for memories of behavioural options that ensures that properties
 * are of type {@link LaraBehaviouralOption}.
 * 
 * @param <BO>
 *            type of behavioural options this memory may store
 * 
 * @author Sascha Holzhauer
 * @date 18.12.2009
 * 
 */
public interface LaraBOMemory<BO extends LaraBehaviouralOption<?, ? extends BO>>
		extends
		LaraMemory<BO> {

	/**
	 * Memorises the given set of behavioural options
	 * 
	 * @param bos
	 *            behavioural options to memorise
	 */
	public void memoriseAll(Set<BO> bos);

	/**
	 * Generic method that returns a collection of the most recently memorised
	 * BO for all behavioural options.
	 * 
	 * @return for every behavioural option the one that was stored at last.
	 * @throws LRetrieveException
	 */
	public Set<BO> recallAllMostRecent() throws LRetrieveException;
}
