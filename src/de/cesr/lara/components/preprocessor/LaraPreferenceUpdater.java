/**
 * LARA - Lightweight Architecture for bounded Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 05.02.2010
 */
package de.cesr.lara.components.preprocessor;

import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;

/**
 * 
 * @author Sascha Holzhauer
 * @param <A>
 * @date 05.02.2010
 * 
 */
public interface LaraPreferenceUpdater<A extends LaraAgent<A, ?>> extends
		LaraPreprocessorComp<A> {

	/**
	 * Updates an agent's preference. NOTE: Normally, preferenceWeights are
	 * updated at last in the preprocessor's chain.
	 * 
	 * @param agent
	 *            the agent whose preferenceWeights are updated
	 * @param dConfiguration
	 *            the decision builder for which preferenceWeights are updated
	 */
	public void updatePreferenceWeights(A agent,
			LaraDecisionConfiguration dConfiguration);

}
