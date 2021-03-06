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

import de.cesr.lara.components.LaraPreference;

/**
 * @author Sascha Holzhauer
 *
 */
public class LPreference implements LaraPreference {

	private String id;

	private String description = "NOT GIVEN";

	LPreference(String id, String desription) {
		this.id = id;
		this.description = desription;
	}

	LPreference(String id) {
		this.id = id;
	}

	/**
	 * @see de.cesr.lara.components.LaraPreference#getId()
	 */
	@Override
	public String getId() {
		return this.id;
	}

	/**
	 * @see de.cesr.lara.components.LaraPreference#getDescription()
	 */
	@Override
	public String getDescription() {
		return this.description;
	}

	public String toString() {
		return "LPreference(" + this.id + ")";
	}
}
