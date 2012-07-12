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
package de.cesr.lara.components.decision;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;

/**
 * Combines a behavioural option and its individual (i.e. weighted by personal
 * preference weights) utility values during the decision process.
 * 
 * @param <BO>
 */
public interface LaraBoRow<BO extends LaraBehaviouralOption<?, ?>> {

	/**
	 * @return the behavioural option this row represents
	 */
	public BO getBehaviouralOption();

	/**
	 * Provides the individual utility values regarding the given preference.
	 * 
	 * @return individual utility value
	 */
	public double getIndividualUtilityValue(
			Class<? extends LaraPreference> preference);

	/**
	 * @return the sum of all individual utility values
	 */
	public double getSum();

	/**
	 * Adds a individual utility values regarding a given preference.
	 * 
	 * @param preference
	 * @param value
	 */
	public void setIndividualUtilityValue(
			Class<? extends LaraPreference> preference, double value);
}
