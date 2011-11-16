/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * 
 */
package de.cesr.lara.components.preprocessor;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;

/**
 * BOAdapter
 * 
 * @param <A>
 *            type of agents this BO utilityUpdater is intended for
 * @param <BO>
 *            type of behavioural options that are updated
 */
public interface LaraBOUtilityUpdaterBuilder<A extends LaraAgent<A, ? super BO>, BO extends LaraBehaviouralOption<? super A, BO>>
		extends LaraPreprocessorComp<A> {

	/**
	 * @return behavioural option utilityUpdater
	 */
	public abstract LaraBOUtilityUpdater<A, BO> getBOUtilityUpdater();

}
