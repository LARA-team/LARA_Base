/**
 * LARA
 *
 * KUBUS ABM Prototype
 * Center for Environmental Systems Research, Kassel
 * Created by holzhauer on 10.07.2009
 */
package de.cesr.lara.components.preprocessor.impl;


import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.impl.LDeliberativeDeciderFactory;
import de.cesr.lara.components.preprocessor.LaraDecisionModeSelector;


/**
 * Constantly selects {@link LDeliberativeDeciderFactory} as decider factory.
 * 
 * @param <A>
 *        the type of agents this preprocessor is intended for
 * @param <BO>
 *        type of behavioural options
 * 
 * @author Sascha Holzhauer
 * @date 10.07.2009
 */
public class LDeliberativeDecisionModeSelector<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
		implements LaraDecisionModeSelector<A, BO> {

	/**
	 * Always selects the matrix deliberation mode ( {@link LDeliberativeDeciderFactory}. This default implementation is
	 * used in {@link LPreprocessFactory}.
	 * 
	 * @see de.cesr.lara.components.preprocessor.LaraDecisionModeSelector#selectDecisionMode(de.cesr.lara.components.agents.LaraAgent,
	 *      de.cesr.lara.components.decision.LaraDecisionConfiguration)
	 */
	@SuppressWarnings("unchecked")
	// agents needs to be of type A
	@Override
	public void selectDecisionMode(A agent, LaraDecisionConfiguration dConfiguration) {
		agent.getLaraComp().getDecisionData(dConfiguration)
				.setDeciderFactory(LDeliberativeDeciderFactory.getFactory((Class<A>) agent.getClass()));
	}
}