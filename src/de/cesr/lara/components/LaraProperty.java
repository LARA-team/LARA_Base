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
package de.cesr.lara.components;

import de.cesr.lara.components.model.impl.LModel;

/**
 * Common abstract base class for all properties used in environment, memory
 * etc. These properties are meant to be immutable. Therefore, subclasses of
 * <code>LaraProperty</code> will usually need to provide copying/cloning
 * facilities either by implementing a copy constructor or an equivalent method.
 * 
 * The time-stamp may not be manipulated to ensure correct handling of
 * properties in memory. Consider wrapping properties in order to handle
 * 'ancient' properties.
 * 
 * @author Sascha Holzhauer, Michael Elbers
 * @param <ValueType>
 * @date 19.02.2010
 */
public abstract class LaraProperty<PropType extends LaraProperty<?, ValueType>, ValueType> {

	private final String key;
	private final int timestamp;

	/**
	 * @param key
	 *            the property's key
	 */
	public LaraProperty(String key) {
		this.key = key;
		this.timestamp = LModel.getModel().getCurrentStep();
	}

	/**
	 * Two properties are equal if their names and values and timestamps are
	 * equal.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if (o instanceof LaraProperty<?, ?>) {
			LaraProperty<PropType, ValueType> otherProperty = (LaraProperty<PropType, ValueType>) o;
			return (getKey().equals(otherProperty.getKey())
					&& getTimestamp() == otherProperty.getTimestamp() && getValue()
					.equals(otherProperty.getValue()));
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @return
	 */
	public final String getKey() {
		return key;
	}

	/**
	 * @param value
	 * @return a new property with new value and key and current time stamp
	 */
	public abstract PropType getModifiedProperty(ValueType value);

	/**
	 * @return a property with same value and current time stamp
	 */
	public PropType getRefreshedProperty() {
		return getModifiedProperty(getValue());
	}

	/**
	 * 
	 * @return
	 */
	public final int getTimestamp() {
		return timestamp;
	}

	/**
	 * NOTE: Since {@link LaraProperty}s are meant to be immutable we avoid a
	 * member in this class which is not private (members other than private
	 * allow implementing a subclass that hurts the immutability). However,
	 * implementing the getValue() method requires a non-private member because
	 * subclasses need the ability to override getValues() and access value in
	 * case they need to return and create a deep copy of value in order to
	 * prevent external changes in the value (which is true for most
	 * non-primitive data types).
	 * 
	 * @return the property's value
	 */
	public abstract ValueType getValue();

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + (getKey() == null ? 0 : getKey().hashCode());
		result = 31 * result + getTimestamp();
		result = 31 * result + (getValue() == null ? 0 : getValue().hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + getKey() + ", " + getValue() + ", " + getTimestamp() + "]";
	}
}
