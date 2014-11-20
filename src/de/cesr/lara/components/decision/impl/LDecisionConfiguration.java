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
package de.cesr.lara.components.decision.impl;

import java.util.Collection;

import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;

/**
 * Creates instance of decision configuration for given set of preferences
 *
 * NOTE: Currently no immutable since preferences could change
 */
public class LDecisionConfiguration implements LaraDecisionConfiguration {

	protected Collection<Class<? extends LaraPreference>> preferences = null;

	private final String id;

	/**
	 * The default id string
	 */
	public static int idCounter = 0;

	/**
	 * Assigns the default id String.
	 */
	public LDecisionConfiguration() {
		this.id = "DConfig" + idCounter++;
	}

	/**
	 * @param id
	 */
	/**
	 * @param id
	 * @param deliberativeChoiceComp
	 *            the mechanism used to fetch best BOs.
	 */
	public LDecisionConfiguration(String id) {
		this.id = id;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDecisionConfiguration#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	@Override
	public Collection<Class<? extends LaraPreference>> getPreferences() {
		return preferences;
	}

	@Override
	public void setPreferences(
			Collection<Class<? extends LaraPreference>> preferences) {
		this.preferences = preferences;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LaraDecisionConfiguration " + id;
	}
}
