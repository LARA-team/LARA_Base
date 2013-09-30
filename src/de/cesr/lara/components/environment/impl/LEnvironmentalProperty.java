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


import de.cesr.lara.components.environment.LaraEnvironment;


/**
 * @author Sascha Holzhauer
 * 
 * @param <ValueType>
 *        type of value
 */
public class LEnvironmentalProperty<ValueType> extends LAbstractEnvironmentalProperty<ValueType> {

	/**
	 * 
	 */
	protected ValueType	value;

	/**
	 * @param key
	 * @param value
	 * @param env
	 */
	public LEnvironmentalProperty(String key, ValueType value, LaraEnvironment env) {
		super(key, env);
		this.value = value;
	}

	/**
	 * @see de.cesr.lara.components.LaraProperty#getModifiedProperty(java.lang.Object)
	 */
	@Override
	public LEnvironmentalProperty<ValueType> getModifiedProperty(ValueType value) {
		return new LEnvironmentalProperty<ValueType>(getKey(), value, environment);
	}

	/**
	 * @see de.cesr.lara.components.LaraProperty#getValue()
	 */
	@Override
	public ValueType getValue() {
		return value;
	}
}