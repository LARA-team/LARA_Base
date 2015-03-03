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

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.decision.LaraBoRow;

/**
 * This implementation of {@link LaraBoRow} does not store values for every
 * preference but only the sum. Therefore, it may not be suitable for any
 * comprehensive deliberative choice component or post-processing.
 * 
 * @author Daniel Klemm
 * @author Sascha Holzhauer
 * @param <BO>
 *            behavioural option
 * 
 */
public class LLightBoRow<BO extends LaraBehaviouralOption<?, ?>> implements
		LaraBoRow<BO> {

	protected BO bo = null;

	protected double utilitySum = 0.0;

	/**
	 * @param bo
	 *            behavioural option
	 */
	public LLightBoRow(BO bo) {
		this.bo = bo;
	}

	/**
	 * @param bo
	 *            behavioural option
	 * @param utilitySum
	 *            sum of all individual utility values
	 */
	public LLightBoRow(BO bo, double utilitySum) {
		this.bo = bo;
		this.utilitySum = utilitySum;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraBoRow#getBehaviouralOption()
	 */
	@Override
	public BO getBehaviouralOption() {
		return bo;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraBoRow#getIndividualUtilityValue(LaraPreference)
	 */
	@Override
	public double getIndividualUtilityValue(
LaraPreference preference) {
		throw new UnsupportedOperationException(
				"This implementation of LaraBoRow "
						+ "stores only the sum of individual utiltiy values");
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraBoRow#getSum()
	 */
	@Override
	public double getSum() {
		return utilitySum;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraBoRow#setIndividualUtilityValue(LaraPreference,
	 *      double)
	 */
	@Override
	public void setIndividualUtilityValue(
LaraPreference preference,
			double value) {
		this.utilitySum += value;
	}

	@Override
	public String toString() {
		return "BO: " + bo.toString() + "(utility sum: " + this.utilitySum
				+ ")";
	}
}