/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 08.03.2010
 */
package de.cesr.lara.components.preprocessor.impl;

import java.util.Collection;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.preprocessor.LaraBOUtilityUpdater;
import de.cesr.lara.components.preprocessor.LaraBOUtilityUpdaterBuilder;

/**
 * 
 * Returns a {@link LaraBOUtilityUpdater} that just pipes the given behavioural
 * options.
 * 
 * @param <A>
 *            agent type
 * @param <BO>
 *            type of behavioural options
 * 
 */
public class LPseudoBoUpdaterBuilder<A extends LaraAgent<A, ? super BO>, BO extends LaraBehaviouralOption<? super A, BO>>
		implements LaraBOUtilityUpdaterBuilder<A, BO> {

	static class LPseudoBoUpdater<A extends LaraAgent<A, ? super BO>, BO extends LaraBehaviouralOption<? super A, BO>>
			implements LaraBOUtilityUpdater<A, BO> {

		/**
		 * Just pipes the given behavioural options.
		 * 
		 * @see de.cesr.lara.components.preprocessor.LaraBOUtilityUpdater#updateBOUtilities(de.cesr.lara.components.agents.LaraAgent,
		 *      java.util.Collection,
		 *      de.cesr.lara.components.decision.LaraDecisionConfiguration)
		 */
		@Override
		public Collection<BO> updateBOUtilities(A agent, Collection<BO> bos,
				LaraDecisionConfiguration<BO> dConfiguration) {
			return bos;
		}
	}

	/**
	 * Returns a {@link LaraBOUtilityUpdater} that just pipes the given
	 * behavioural options.
	 * 
	 * @see de.cesr.lara.components.preprocessor.LaraBOUtilityUpdaterBuilder#getBOUtilityUpdater()
	 */
	@Override
	public LaraBOUtilityUpdater<A, BO> getBOUtilityUpdater() {
		return new LPseudoBoUpdater<A, BO>();
	}
}