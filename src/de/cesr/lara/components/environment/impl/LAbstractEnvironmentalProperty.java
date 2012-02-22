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
	 * Getter of the property <tt>environment</tt>
	 * 
	 * @return environment this property is associated with.
	 */
	public LaraEnvironment getEnvironment() {
		return environment;
	}
}