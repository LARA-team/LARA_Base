/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * 
 */
package de.cesr.lara.components.agents;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.LaraDecisionData;
import de.cesr.lara.components.eventbus.LaraEventSubscriber;

/**
 * The class uses recursive generics since the {@link LaraAgentComponent}
 * requires the type of agent. See Maurice, M. N. & Wadler, P. Java Generics and
 * Collections O'Reilly Media, 2006, p. 133ff.
 * 
 * Agents also work with BOs that require (only) a super class of their (agent)
 * class.
 * 
 * @param <A>
 *            the agent type of the implementing class
 * @param <BO>
 *            the type of behavioural options the implementing agent class works
 *            with
 * 
 */
public interface LaraAgent<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
		extends LaraEventSubscriber {

	/**
	 * Returns the {@link LaraAgentComponent} of this agent.
	 * 
	 * @return component Lara agent component
	 */
	public LaraAgentComponent<A, BO> getLaraComp();

	/**
	 * Get the custom agent id.
	 * 
	 * @return agent id string
	 */
	public String getAgentId();
}