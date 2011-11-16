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
public class LFloatProperty extends LaraProperty<Float> {

	private Float value;

	/**
	 * @param key
	 * @param value
	 */
	public LFloatProperty(String key, float value) {
		super(key);
		this.value = new Float(value);
	}

	/**
	 * @see de.cesr.lara.components.LaraProperty#getModifiedProperty(java.lang.Object)
	 */
	@Override
	public LFloatProperty getModifiedProperty(Float value) {
		return new LFloatProperty(this.getKey(), value);
	}

	/**
	 * @see de.cesr.lara.components.LaraProperty#getValue()
	 */
	@Override
	public Float getValue() {
		return value;
	}
}
