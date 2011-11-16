/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * 
 */
package de.cesr.lara.components.agents;

import de.cesr.lara.components.LaraBehaviouralOption;
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
public interface LaraAgent<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<? super A, BO>>
		extends LaraEventSubscriber {

	/**
	 * Get the custom agent id.
	 * 
	 * @return agent id string
	 * 
	 *         Created by Sascha Holzhauer on 17.12.2009
	 */
	public String getAgentId();

	/**
	 * Returns the {@link LaraAgentComponent} of this agent.
	 * 
	 * @return component Lara agent component
	 * 
	 *         Created by Sascha Holzhauer on 17.12.2009
	 */
	public LaraAgentComponent<A, BO> getLaraComp();

}
