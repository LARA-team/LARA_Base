/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 */
package de.cesr.lara.components.environment.impl;

import java.util.List;

import de.cesr.lara.components.LaraProperty;
import de.cesr.lara.components.environment.LaraEnvironment;

public class LEnvironmentalListOfStringProperty extends
		LAbstractEnvironmentalProperty<List<String>> {

	/**
	 * value
	 */
	protected List<String> listOfString;

	/**
	 * @param key
	 * @param value
	 * @param timestamp
	 * @param env
	 */
	public LEnvironmentalListOfStringProperty(String key, List<String> value,
			int timestamp, LaraEnvironment env) {
		super(key, env, timestamp);
		this.listOfString = value;
	}

	/**
	 * @param key
	 * @param value
	 * @param env
	 */
	public LEnvironmentalListOfStringProperty(String key, List<String> value,
			LaraEnvironment env) {
		super(key, env);
		this.listOfString = value;
	}

	@Override
	public LaraProperty<List<String>> getModifiedProperty(List<String> value) {
		return new LEnvironmentalListOfStringProperty(getKey(), value,
				environment);
	}

	/**
	 * @return the value
	 */
	@Override
	public List<String> getValue() {
		return listOfString;
	}
}
