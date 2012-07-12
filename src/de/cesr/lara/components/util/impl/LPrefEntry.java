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

import java.util.AbstractMap.SimpleEntry;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;

/**
 * Used to specify preference class - value pairs in order to instantiate
 * {@link LaraBehaviouralOption}s more easily.
 * 
 * @author Sascha Holzhauer
 * 
 */
public class LPrefEntry extends
		SimpleEntry<Class<? extends LaraPreference>, Double> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3002322186440419016L;

	/**
	 * @param prefs
	 *            preference class
	 * @param value
	 *            double value
	 */
	public LPrefEntry(Class<? extends LaraPreference> prefs, Double value) {
		super(prefs, value);
	}
}