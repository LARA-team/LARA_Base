/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * 
 */
package de.cesr.lara.components.decision.impl;


import java.util.List;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.decision.LaraRow;


/**
 * 
 * TODO parameters a row for utility matrix
 * 
 * @author klemm
 * 
 */
public class LRow implements LaraRow {

	private LaraBehaviouralOption	bO		= null;

	private List<Double>			values	= null;

	/**
	 * constructor - requires a list of cells
	 * 
	 * @param bO
	 * @param values
	 */
	public LRow(LaraBehaviouralOption bO, List<Double> values) {
		this.bO = bO;
		this.values = values;
	}

	/**
	 * @return behavioural option
	 * @see de.cesr.lara.components.decision.LaraRow#getBehaviouralOption()
	 */
	@Override
	public LaraBehaviouralOption getBehaviouralOption() {
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
