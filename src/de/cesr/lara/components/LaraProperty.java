/**
 * LARA
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 10.12.2009
 */
package de.cesr.lara.components;

import de.cesr.lara.components.model.impl.LModel;

/**
 * Common abstract base class for all properties used in environment, memory
 * etc. These properties are meant to be immutable. Therefore, subclasses of
 * <code>LaraProperty</code> will usually need to provide copying/cloning
 * facilities either by implementing a copy constructor or an equivalent method
 * and setting the time stamp of the copy appropriately (normally to the current
 * step).
 * 
 * @author Sascha Holzhauer, Michael Elbers
 * @param <ValueType>
 * @date 19.02.2010
 */
public abstract class LaraProperty<ValueType> {

	private String key;
	private int timestamp;

	/**
	 * @param key
	 *            the property's key
	 */
	public LaraProperty(String key) {
		this(key, LModel.getModel().getCurrentStep());
	}

	/**
	 * @param key
	 *            the property's key
	 * @param timestamp
	 *            the time step the property was created at
	 */
	public LaraProperty(String key, int timestamp) {
		this.key = key;
		this.timestamp = timestamp;
	}

	/**
	 * Two properties are equal if their names and values are equal. TODO what
	 * about time stamp? (see also hashCode()) (SH)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if (o instanceof LaraProperty<?>) {
			LaraProperty<ValueType> otherProperty = (LaraProperty<ValueType>) o;
			return (getKey().equals(otherProperty.getKey())
					&& getTimestamp() == otherProperty.getTimestamp() && getValue()
					.equals(otherProperty.getValue()));
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @return Created by Michael Elbers on 19.02.2010
	 */
	public final String getKey() {
		return key;
	}

	/**
	 * @param value
	 * @return a new property with new value and key and current time stamp
	 *         Created by Sascha Holzhauer on 10.02.2010
	 */
	public abstract LaraProperty<ValueType> getModifiedProperty(ValueType value);

	/**
	 * @return a property with same value and current time stamp
	 */
	public LaraProperty<ValueType> getRefreshedProperty() {
		return getModifiedProperty(getValue());
	}

	/**
	 * 
	 * @return Created by Michael Elbers on 19.02.2010
	 */
	public final int getTimestamp() {
		return timestamp;
	}

	/**
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

	@Override
	public String toString() {
		return "[" + getKey() + ", " + getValue() + ", " + getTimestamp() + "]";
	}
}
