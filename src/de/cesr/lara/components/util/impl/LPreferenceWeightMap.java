/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 14.02.2011
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
