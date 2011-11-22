/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Daniel Klemm
 */
package de.cesr.lara.components.decision;

import de.cesr.lara.components.LaraBehaviouralOption;

/**
 * Represents a row of a decision matrix (in LARA context most probably the
 * matrix' internal representation of a behavioural option).
 * 
 * @param <BO>
 */
public interface LaraRow<BO extends LaraBehaviouralOption<?, ?>> {

	/**
	 * @return the behavioural option that belongs to this row
	 */
	public BO getBehaviouralOption();

	/**
	 * @return the sum of all cells in row
	 */
	public double getSum();

}
