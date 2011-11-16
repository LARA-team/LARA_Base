/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 23.03.2010
 */
package de.cesr.lara.components.container.properties.impl;

import de.cesr.lara.components.LaraProperty;

/**
 * 
 */
public class LIntProperty extends LaraProperty<Integer> {

	private Integer value;

	/**
	 * @param key
	 * @param value
	 */
	public LIntProperty(String key, int value) {
		super(key);
		this.value = new Integer(value);
	}

	/**
	 * @see de.cesr.lara.components.LaraProperty#getModifiedProperty(java.lang.Object)
	 */
	@Override
	public LIntProperty getModifiedProperty(Integer value) {
		return new LIntProperty(this.getKey(), value);
	}

	/**
	 * @see de.cesr.lara.components.LaraProperty#getValue()
	 */
	@Override
	public Integer getValue() {
		return value;
	}
}