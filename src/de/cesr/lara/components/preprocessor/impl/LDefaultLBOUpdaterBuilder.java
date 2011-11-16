/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 16.02.2010
 */
package de.cesr.lara.components.preprocessor.impl;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.preprocessor.LaraBOUtilityUpdater;
import de.cesr.lara.components.preprocessor.LaraBOUtilityUpdaterBuilder;

/**
 * @param <A>
 *            the type of agents this BO utilityUpdater builder is intended for
 * @param <BO>
 *            the type of behavioural options that are checked
 */
public class LDefaultLBOUpdaterBuilder<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<? super A, BO>>
		implements LaraBOUtilityUpdaterBuilder<A, BO> {

	/**
	 * @see de.cesr.lara.components.preprocessor.LaraBOUtilityUpdaterBuilder#getBOUtilityUpdater()
	 */
	@Override
	public LaraBOUtilityUpdater<A, BO> getBOUtilityUpdater() {
		return new LDefaultBOUpdater<A, BO>();
	}

}
