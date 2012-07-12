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

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import de.cesr.lara.components.LaraPreference;

/**
 * Overrides {@link HashMap#toString()} in order to return an alphabetically
 * ordered list of preferenceWeights and preference values.
 * 
 */
public class LPreferenceWeightMap extends
		LinkedHashMap<Class<? extends LaraPreference>, Double> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9015768374329397298L;

	/**
	 * 
	 */
	public LPreferenceWeightMap() {
		super();
	}

	/**
	 * @param map
	 */
	public LPreferenceWeightMap(Map<Class<? extends LaraPreference>, Double> map) {
		super(map);
	}

	/**
	 * @param map
	 */
	public LPreferenceWeightMap(LPrefEntry... entry) {
		super();
		for (LPrefEntry e : entry) {
			this.put(e.getKey(), e.getValue());
		}
	}

	@Override
	public String toString() {
		TreeSet<Entry<Class<? extends LaraPreference>, Double>> orderedEntries = new TreeSet<Entry<Class<? extends LaraPreference>, Double>>(
				new Comparator<Entry<Class<? extends LaraPreference>, Double>>() {
					@Override
					public int compare(
							Entry<Class<? extends LaraPreference>, Double> o1,
							Entry<Class<? extends LaraPreference>, Double> o2) {
						return o1.getKey().getName()
								.compareTo(o2.getKey().getName());
					}
				});

		return orderedEntries.toString();
	}

}