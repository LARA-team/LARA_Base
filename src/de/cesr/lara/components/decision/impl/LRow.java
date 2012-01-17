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


import java.util.List;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.decision.LaraRow;


/**
 * @author klemm
 * 
 */
public class LRow<BO extends LaraBehaviouralOption<?, ?>> implements
		LaraRow<BO> {

	private BO bO = null;

	private List<Double>			values	= null;

	/**
	 * constructor - requires a list of cells
	 * 
	 * @param bO
	 * @param values
	 */
	public LRow(BO bO, List<Double> values) {
		this.bO = bO;
		this.values = values;
	}

	/**
	 * @return behavioural option
	 * @see de.cesr.lara.components.decision.LaraRow#getBehaviouralOption()
	 */
	@Override
	public BO getBehaviouralOption() {
		return bO;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraRow#getSum()
	 */
	@Override
	public double getSum() {
		float sum = 0;
		for (Double f : values) {
			sum += f.doubleValue();
		}
		return sum;
	}

	@Override
	public String toString() {
		return "BO: " + bO.toString();
	}
}
