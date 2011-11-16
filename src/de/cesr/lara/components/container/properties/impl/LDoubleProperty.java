/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 05.05.2010
 */
package de.cesr.lara.components.container.properties.impl;

import de.cesr.lara.components.LaraProperty;

/**
 * 
 */
public class LDoubleProperty extends LaraProperty<Double> {

	private Double value;

	/**
	 * @param key
	 * @param value
	 */
	public LDoubleProperty(String key, double value) {
		super(key);
		this.value = new Double(value);
	}

	/**
	 * @see de.cesr.lara.components.LaraProperty#getModifiedProperty(java.lang.Object)
	 */
	@Override
	public LDoubleProperty getModifiedProperty(Double value) {
		return new LDoubleProperty(this.getKey(), value);
	}

	/**
	 * @see de.cesr.lara.components.LaraProperty#getValue()
	 */
	@Override
	public Double getValue() {
		return value;
	}
}
