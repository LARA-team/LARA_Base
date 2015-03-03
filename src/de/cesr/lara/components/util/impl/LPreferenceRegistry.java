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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.util.LaraPreferenceRegistry;

/**
 * @author Sascha Holzhauer
 *
 */
public class LPreferenceRegistry implements LaraPreferenceRegistry {

	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(LPreferenceRegistry.class);

	Map<String, LaraPreference> preferences = new HashMap<String, LaraPreference>();

	/**
	 * @see de.cesr.lara.components.util.LaraPreferenceRegistry#register(java.lang.String)
	 */
	@Override
	public LaraPreference register(String id) {
		return this.register(id, "NOT GIVEN");
	}

	/**
	 * @see de.cesr.lara.components.util.LaraPreferenceRegistry#register(java.lang.String, java.lang.String)
	 */
	@Override
	public LaraPreference register(String id, String description) {
		if (this.isRegistered(id)) {
			throw new IllegalStateException(
					"There is already a LaraPreference registered with ID "
							+ id + "!");
		}
		LaraPreference preference = new LPreference(id);
		this.preferences.put(id, preference);
		return preference;
	}

	/**
	 * @see de.cesr.lara.components.util.LaraPreferenceRegistry#isRegistered(java.lang.String)
	 */
	@Override
	public boolean isRegistered(String id) {
		return this.preferences.containsKey(id);
	}

	/**
	 * @see de.cesr.lara.components.util.LaraPreferenceRegistry#get(java.lang.String)
	 */
	@Override
	public LaraPreference get(String id) {
		if (!this.preferences.containsKey(id)) {
			logger.warn("LaraPreferenceRegistry does not contain preference with ID >"
					+ id + "<!");
			throw new IllegalStateException(
					"LaraPreferenceRegistry does not contain preference with ID >"
							+ id + "<!");
		}
		return this.preferences.get(id);
	}

	/**
	 * @see de.cesr.lara.components.util.LaraPreferenceRegistry#remove(java.lang.String)
	 */
	@Override
	public boolean remove(String id) {
		return this.preferences.remove(id) != null;
	}

	/**
	 * @see de.cesr.lara.components.util.LaraPreferenceRegistry#reset()
	 */
	@Override
	public boolean reset() {
		this.preferences.clear();
		return true;
	}
}
