/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 26.05.2010
 */
package de.cesr.lara.components.decision;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;

/**
 * @param <A>
 *            the agent class
 * @param <BO>
 */
public interface LaraDeciderFactory<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<? super A, BO>> {

	/**
	 * @param agent
	 * @param dConfiguration
	 * @return the valid {@link LaraDecider}
	 */
	public LaraDecider<BO> getDecider(A agent,
			LaraDecisionConfiguration<BO> dConfiguration);
}
