/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * 
 */
package de.cesr.lara.components.environment.impl;

import de.cesr.lara.components.environment.LaraEnvironment;

/**
 * @param <ValueType>
 */
public class LEnvironmentalProperty<ValueType> extends
		LAbstractEnvironmentalProperty<ValueType> {

	/**
	 * 
	 */
	protected ValueType value;

	/**
	 * @param key
	 * @param value
	 * @param timestamp
	 * @param env
	 */
	public LEnvironmentalProperty(String key, ValueType value, int timestamp,
			LaraEnvironment env) {
		super(key, env, timestamp);
		this.value = value;
	}

	/**
	 * @param key
	 * @param value
	 * @param env
	 */
	public LEnvironmentalProperty(String key, ValueType value,
			LaraEnvironment env) {
		super(key, env);
		this.value = value;
	}

	@Override
	public LEnvironmentalProperty<ValueType> getModifiedProperty(ValueType value) {
		return new LEnvironmentalProperty<ValueType>(getKey(), value,
				environment);
	}

	@Override
	public ValueType getValue() {
		return value;
	}

}
