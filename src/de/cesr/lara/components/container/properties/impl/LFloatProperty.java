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
package de.cesr.lara.components.container.properties.impl;


import de.cesr.lara.components.LaraProperty;
import de.cesr.lara.components.model.LaraModel;


/**
 * 
 */
public class LFloatProperty extends LaraProperty<LFloatProperty, Float> {

	private final Float	value;

	private LaraModel lmodel;

	/**
	 * @param lmodel 
	 * @param key
	 * @param value
	 */
	public LFloatProperty(LaraModel lmodel, String key, float value) {
		super(lmodel, key);
		this.lmodel = lmodel;
		this.value = new Float(value);
	}

	/**
	 * @see de.cesr.lara.components.LaraProperty#getModifiedProperty(java.lang.Object)
	 */
	@Override
	public LFloatProperty getModifiedProperty(Float value) {
		return new LFloatProperty(this.lmodel, this.getKey(), value);
	}

	/**
	 * @see de.cesr.lara.components.LaraProperty#getValue()
	 */
	@Override
	public Float getValue() {
		return value;
	}

}