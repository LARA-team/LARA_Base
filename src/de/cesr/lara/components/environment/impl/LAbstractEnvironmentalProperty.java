/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * 
 */
package de.cesr.lara.components.environment.impl;

import de.cesr.lara.components.LaraProperty;
import de.cesr.lara.components.environment.LaraEnvironment;

/**
 * 
 * Subclasses need to override {@link #getModifiedProperty(Object)} and return
 * their own type in order to be stored as their type in the environment!
 * 
 * @author Sascha Holzhauer
 * @date 04.01.2010
 * 
 * @param <V>
 *            type of property's value
 */
public abstract class LAbstractEnvironmentalProperty<V> extends
		LaraProperty<LAbstractEnvironmentalProperty<V>, V> {

	/**
	 * environment this property is associated with
	 */
	protected LaraEnvironment environment;

	/**
	 * @param key
	 *            the <code>LEnvironmentalProperty</code>'s name
	 * @param env
	 *            the {@link LaraEnvironment} the property belongs to
	 */
	public LAbstractEnvironmentalProperty(String key, LaraEnvironment env) {
		super(key);
		this.environment = env;
	}

	/**
	 * @param key
	 *            the <code>LEnvironmentalProperty</code>'s name
	 * @param timestamp
	 * @param env
	 *            the {@link LaraEnvironment} the property belongs to
	 */
	public LAbstractEnvironmentalProperty(String key, LaraEnvironment env,
			int timestamp) {
		super(key, timestamp);
		this.environment = env;
	}

	/**
	 * Getter of the property <tt>environment</tt>
	 * 
	 * @return environment this property is associated with.
	 */
	public LaraEnvironment getEnvironment() {
		return environment;
	}
}
