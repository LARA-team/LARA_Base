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


import java.util.Collection;
import java.util.Map;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;

/**
 * A special {@link LaraDecider} that adds functionality required for laraBoRows
 * decisions.
 * 
 * @param <BO>
 *            type of behavioural options
 */
public interface LaraDeliberativeDecider<BO extends LaraBehaviouralOption<?, ? extends BO>> extends LaraDecider<BO> {

	/**
	 * @param deliberativeChoiceComponent
	 */
	public void setDeliberativeChoiceComponent(LaraDeliberativeChoiceComponent deliberativeChoiceComponent);

	/**
	 * @return Returns the behaviouralOption.
	 */
	public Collection< BO> getSelectableBos();

	/**
	 * Set the collection of BOs the decision process decides upon.
	 * 
	 * @param selectableBos
	 */
	public void setSelectableBos(Collection<BO> selectableBos);

	/**
	 * Provides the individual preference weights
	 * 
	 * @return Returns the preference weights
	 */
	public Map<Class<? extends LaraPreference>, Double> getPreferenceWeights();

	/**
	 * Sets the individual preference weights.
	 * 
	 * @param preferenceWeights
	 *            individual preference weights
	 */
	public void setPreferenceWeights(Map<Class<? extends LaraPreference>, Double> preferenceWeights);
}