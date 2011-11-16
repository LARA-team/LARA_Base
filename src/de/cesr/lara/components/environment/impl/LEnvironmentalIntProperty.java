/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 22.12.2009
 */
package de.cesr.lara.components.environment.impl;

import de.cesr.lara.components.LaraProperty;
import de.cesr.lara.components.environment.LaraEnvironment;

/**
 * 
 * @author Sascha Holzhauer
 * @date 22.12.2009
 * 
 */
public class LEnvironmentalIntProperty extends
		LAbstractEnvironmentalProperty<Integer> {

	/**
	 * value
	 */
	protected int intValue;

	/**
	 * @param key
	 * @param value
	 * @param timestamp
	 * @param env
	 */
	public LEnvironmentalIntProperty(String key, int value, int timestamp,
			LaraEnvironment env) {
		super(key, env, timestamp);
		this.intValue = value;
	}

	/**
	 * @param key
	 * @param value
	 * @param env
	 */
	public LEnvironmentalIntProperty(String key, int value, LaraEnvironment env) {
		super(key, env);
		this.intValue = value;
	}

	/**
	 * Two int properties are equal if their names and int values are equal.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof LEnvironmentalIntProperty) {
			LEnvironmentalIntProperty intProperty = (LEnvironmentalIntProperty) o;
			return (getKey().equals(intProperty.getKey()) && this.intValue == intProperty
					.getIntValue());
		} else {
			return false;
		}
	}

	/**
	 * @return Created by Sascha Holzhauer on 22.12.2009
	 */
	public int getIntValue() {
		return intValue;
	}

	@Override
	public LaraProperty<Integer> getModifiedProperty(Integer value) {
		return new LEnvironmentalIntProperty(getKey(), value, environment);
	}

	/**
	 * @return the value
	 */
	@Override
	public Integer getValue() {
		return new Integer(intValue);
	}
}
