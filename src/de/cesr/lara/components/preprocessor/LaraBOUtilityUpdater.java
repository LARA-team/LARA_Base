/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 16.02.2010
 */
package de.cesr.lara.components.preprocessor;

import java.util.Collection;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;

/**
 * Updates the utility values for the given behavioural options regarding the
 * decision of the given decision builder.
 * 
 * @param <A>
 *            the type of agents this BO utilityUpdater is intended for
 * @param <BO>
 *            the type of behavioural options that are updated
 * 
 */
public interface LaraBOUtilityUpdater<A extends LaraAgent<A, ? super BO>, BO extends LaraBehaviouralOption<? super A, BO>>
		extends LaraPreprocessorComp<A> {

	/**
	 * @param agent
	 *            the agent the BO belongs to
	 * @param bos
	 *            behavioural options to update
	 * @param dConfiguration
	 *            decision builder of the decision for which BOs are updated
	 * @return behavioural options
	 */
	public abstract Collection<BO> updateBOUtilities(A agent,
			Collection<BO> bos, LaraDecisionConfiguration<BO> dConfiguration);

}
