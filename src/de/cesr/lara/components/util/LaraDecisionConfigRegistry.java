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
package de.cesr.lara.components.util;

import java.util.Collection;

import de.cesr.lara.components.decision.LaraDecisionConfiguration;

/**
 * @author Sascha Holzhauer
 *
 */
public interface LaraDecisionConfigRegistry {

	/**
	 * @param dConfig
	 * @return registered decision configuration
	 */
	public LaraDecisionConfiguration register(LaraDecisionConfiguration dConfig);

	/**
	 * @param id
	 * @return true if the given id is registered
	 */
	public boolean isRegistered(String id);

	/**
	 * @param dConfig
	 * @return true if the given id is registered
	 */
	public boolean isRegistered(LaraDecisionConfiguration dConfig);

	/**
	 * @param id
	 * @return decision configuration associated with given ID
	 */
	public LaraDecisionConfiguration get(String id);

	/**
	 * @param id
	 * @return true if decision configuration for given ID could be removed
	 */
	public boolean remove(String id);

	/**
	 * @param dConfig
	 * @return true if given decision configuration could be removed
	 */
	public boolean remove(LaraDecisionConfiguration dConfig);

	/**
	 * @return collection of all registered decision configurations
	 */
	public Collection<LaraDecisionConfiguration> getAll();

	/**
	 * Clears list of registered decision configurations
	 * 
	 * @return true
	 */
	public boolean reset();
}
