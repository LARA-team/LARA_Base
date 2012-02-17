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
package de.cesr.lara.components.param;

import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;

/**
 * @author Sascha Holzhauer
 *
 */
public enum LDecisionMakingPa implements PmParameterDefinition {

	/**
	 * The number of ticks an agent need to select the same BO in order to
	 * switch to habitual behaviour.
	 */
	HABIT_TRESHOLD(Integer.class, 5);

	private Class<?> type;
	private Object defaultValue;

	LDecisionMakingPa(Class<?> type) {
		this(type, null);
	}

	LDecisionMakingPa(Class<?> type, Object defaultValue) {
		this.type = type;
		this.defaultValue = defaultValue;
	}

	LDecisionMakingPa(Class<?> type, PmParameterDefinition defaultDefinition) {
		this.type = type;
		this.defaultValue = defaultDefinition.getDefaultValue();
		PmParameterManager.setDefaultParameterDef(this, defaultDefinition);
	}

	@Override
	public Class<?> getType() {
		return type;
	}

	@Override
	public Object getDefaultValue() {
		return defaultValue;
	}

}
