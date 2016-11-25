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
package de.cesr.lara.components.decision.impl;


import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.decision.LaraBoRow;


/**
 * This implementation of {@link LaraBoRow} does store values for every preference
 * 
 * TODO test
 * 
 * @author Sascha Holzhauer
 * @param <BO>
 *        behavioural option
 * 
 */
public class LInspectionBoRow<BO extends LaraBehaviouralOption<?, ? extends BO>> extends LLightBoRow<BO> {

	protected Map<LaraPreference, Double> utilities = new HashMap<>();

	public LInspectionBoRow(BO bo) {
		super(bo);
	}

	public LInspectionBoRow(BO bo, double utilitySum) {
		super(bo, utilitySum);
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraBoRow#getIndividualUtilityValue(LaraPreference)
	 */
	@Override
	public double getIndividualUtilityValue(LaraPreference preference) {
		return this.utilities.containsKey(preference) ? this.utilities.get(preference) : Double.NaN;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraBoRow#setIndividualUtilityValue(LaraPreference, double)
	 */
	@Override
	public void setIndividualUtilityValue(LaraPreference preference, double value) {
		if (this.utilities.containsKey(preference)) {
			this.utilitySum -= this.utilities.get(preference);
		}
		this.utilities.put(preference, value);
		this.utilitySum += value;
	}

	/**
	 * @see de.cesr.lara.components.decision.impl.LLightBoRow#neutralisePreferenceWeights(double)
	 */
	@Override
	public void neutralisePreferenceWeights(double averageWeight) {
		this.utilitySum /= averageWeight;
		for (Entry<LaraPreference, Double> entry : utilities.entrySet()) {
			entry.setValue(entry.getValue() / averageWeight);
		}
	}
}
