/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 25.05.2010
 */
package de.cesr.lara.components.decision;

import java.util.Set;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;

/**
 * @param <A>
 *            type of agent
 * @param <BO>
 *            type of behavioural option
 * @param <P>
 *            the parameter class type
 * 
 */
public interface LaraDecisionTree<A extends LaraAgent<A, ? super BO>, BO extends LaraBehaviouralOption<?, BO>, P> {

	/**
	 * Processes this node of the decision tree.
	 * 
	 * @param agent
	 *            the {@link LaraAgent} this decision tree is processed for
	 * @param parameter
	 *            the parameter object (decisionBuilder, for instance)
	 * @return a set of {@link LaraBehaviouralOption}s
	 */
	public Set<BO> getBos(A agent, P parameter);

}
