/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 05.02.2010
 */
package de.cesr.lara.components.preprocessor.impl;

import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.LaraDecisionData;
import de.cesr.lara.components.preprocessor.LaraPreferenceUpdater;

/**
 * Does _not_ update any preference.
 * 
 * @param <A>
 *            he type of agents this preference utilityUpdater is intended for
 * 
 * @author Sascha Holzhauer
 * @date 05.02.2010
 * 
 */
public class LPseudoPrefereceUpdater<A extends LaraAgent<A, ?>> implements
		LaraPreferenceUpdater<A> {

	/**
	 * Does _not_ update any preference (only sets agent's basic
	 * preferenceWeights at the {@link LaraDecisionData} object).
	 * 
	 * @see de.cesr.lara.components.preprocessor.LaraPreferenceUpdater#updatePreferenceWeights(de.cesr.lara.components.agents.LaraAgent,
	 *      de.cesr.lara.components.decision.LaraDecisionConfiguration)
	 */
	@Override
	public void updatePreferenceWeights(A agent,
			LaraDecisionConfiguration dConfiguration) {
		agent.getLaraComp()
				.getDecisionData(dConfiguration)
				.setSituationalPreferences(
						agent.getLaraComp().getPreferenceWeights());
	}

}
