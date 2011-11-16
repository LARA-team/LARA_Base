/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 25.05.2010
 */
package de.cesr.lara.components.decision.impl;

import java.util.Set;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecisionTree;

/**
 * TODO java docs
 * 
 * @param <A>
 *            type of agent
 * @param <BO>
 *            type of behavioural option
 * @param <P>
 *            type of tree parameter
 * 
 */
public abstract class LAbstractBinaryDecisionLeave<A extends LaraAgent<A, ? super BO>, BO extends LaraBehaviouralOption<? super A, BO>, P>
		implements LaraDecisionTree<A, BO, P> {

	/**
	 * @see de.cesr.lara.components.decision.LaraDecisionTree#getBos(de.cesr.lara.components.agents.LaraAgent,
	 *      java.lang.Object)
	 */
	@Override
	abstract public Set<BO> getBos(A agent, P parameter);
}
