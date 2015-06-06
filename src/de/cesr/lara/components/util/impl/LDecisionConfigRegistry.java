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
package de.cesr.lara.components.util.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.util.LaraDecisionConfigRegistry;

/**
 * @author Sascha Holzhauer
 *
 */
public class LDecisionConfigRegistry implements LaraDecisionConfigRegistry {

	/**
	 * Logger
	 */
	static private Logger logger = Logger
			.getLogger(LDecisionConfigRegistry.class);

	Map<String, LaraDecisionConfiguration> dConfigs = new HashMap<String, LaraDecisionConfiguration>();


	/**
	 * @see de.cesr.lara.components.util.LaraDecisionConfigRegistry#register(LaraDecisionConfiguration)
	 */
	@Override
	public LaraDecisionConfiguration register(LaraDecisionConfiguration dConfig) {
		if (this.isRegistered(dConfig)) {
			throw new IllegalStateException(
					"There is already a LaraDecisionConfiguration registered with ID "
							+ dConfig.getId() + "!");
		}
		this.dConfigs.put(dConfig.getId(), dConfig);
		return dConfig;
	}

	/**
	 * @see de.cesr.lara.components.util.LaraDecisionConfigRegistry#isRegistered(java.lang.String)
	 */
	@Override
	public boolean isRegistered(String id) {
		return this.dConfigs.containsKey(id);
	}

	/**
	 * @see de.cesr.lara.components.util.LaraDecisionConfigRegistry#get(java.lang.String)
	 */
	@Override
	public LaraDecisionConfiguration get(String id) {
		if (!this.dConfigs.containsKey(id)) {
			logger.warn("LDecisionConfigRegistry does not contain decision config with ID >"
					+ id + "<!");
			throw new IllegalStateException(
					"LDecisionConfigRegistry does not contain decision config with ID >"
							+ id + "<!");
		}
		return this.dConfigs.get(id);
	}

	/**
	 * @see de.cesr.lara.components.util.LaraDecisionConfigRegistry#remove(java.lang.String)
	 */
	@Override
	public boolean remove(String id) {
		return this.dConfigs.remove(id) != null;
	}

	/**
	 * @see de.cesr.lara.components.util.LaraDecisionConfigRegistry#remove(de.cesr.lara.components.decision.LaraDecisionConfiguration)
	 */
	@Override
	public boolean remove(LaraDecisionConfiguration dConfig) {
		return this.dConfigs.remove(dConfig.getId()) != null;
	}

	/**
	 * @see de.cesr.lara.components.util.LaraDecisionConfigRegistry#reset()
	 */
	@Override
	public boolean reset() {
		this.dConfigs.clear();
		return true;
	}

	/**
	 * @param dConfig
	 * @return true if registered
	 */
	@Override
	public boolean isRegistered(LaraDecisionConfiguration dConfig) {
		return this.dConfigs.containsKey(dConfig.getId());
	}

	/**
	 * @see de.cesr.lara.components.util.LaraDecisionConfigRegistry#getAll()
	 */
	@Override
	public Collection<LaraDecisionConfiguration> getAll() {
		return new ArrayList<LaraDecisionConfiguration>(this.dConfigs.values());
	}
}
