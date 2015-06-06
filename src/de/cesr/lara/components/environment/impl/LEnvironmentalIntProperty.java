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
import de.cesr.lara.components.model.LaraModel;


/**
 * 
 * @author Sascha Holzhauer
 * 
 */
public class LEnvironmentalIntProperty extends LAbstractEnvironmentalProperty<Integer> {

	/**
	 * value
	 */
	protected int	intValue;

	/**
	 * @param lmodel 
	 * @param key
	 * @param value
	 * @param env
	 */
	public LEnvironmentalIntProperty(LaraModel lmodel, String key, int value,
			LaraEnvironment env) {
		super(lmodel, key, env);
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
			return (getKey().equals(intProperty.getKey()) && this.intValue == intProperty.getIntValue());
		} else {
			return false;
		}
	}

	/**
	 * @return the integer value as primitive
	 */
	public int getIntValue() {
		return intValue;
	}

	@Override
	public LEnvironmentalIntProperty getModifiedProperty(Integer value) {
		return new LEnvironmentalIntProperty(lmodel, getKey(), value,
				environment);
	}

	/**
	 * @return the value
	 */
	@Override
	public Integer getValue() {
		return new Integer(intValue);
	}
}
