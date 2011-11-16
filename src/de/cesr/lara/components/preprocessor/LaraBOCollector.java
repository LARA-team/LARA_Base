/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * 
 */
package de.cesr.lara.components.preprocessor;

import java.util.Collection;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.container.memory.LaraBOMemory;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;

/**
 * Collects behavioural options from the BO-memory. The collection may be
 * filtered by certain criteria. Note that normally afterwards the
 * {@link LaraBOPreselector} is applied to each BO.
 * 
 * @param <A>
 *            type of agents this BO collector is intended for
 * @param <BO>
 *            type of behavioural options the given BO-memory memorises
 */
public interface LaraBOCollector<A extends LaraAgent<A, ? super BO>, BO extends LaraBehaviouralOption<? super A, BO>>
		extends LaraPreprocessorComp<A> {

	/**
	 * @param agent
	 *            agent the BO belongs to
	 * @param memory
	 *            BO memory to collect behavioural options from
	 * @param dConfiguration
	 *            indicator of the current decision
	 * @return collected behavioural options
	 */
	public abstract Collection<BO> collectBOs(A agent, LaraBOMemory<BO> memory,
			LaraDecisionConfiguration dConfiguration);
}
