/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 26.05.2010
 */
package de.cesr.lara.components.decision.impl;


import java.util.Set;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecider;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.LaraDecisionTree;


/**
 * 
 * TODO why no factory for this decider? 
 * @param <A>
 *        type of agents
 * @param <BO>
 *        type of behavioural options
 * @param <P>
 *        type of parameter
 * 
 */
public class LTreeDecider<A extends LaraAgent<A, ? super BO>, BO extends LaraBehaviouralOption<?, BO>, P>
		implements LaraDecider<BO> {

	A							agent;
	Set<BO>						bos;
	LaraDecisionTree<A, BO, P>	decisionTree;
	P							parameter;

	/**
	 * @param decisionTree
	 * @param agent
	 * @param parameter
	 */
	public LTreeDecider(LaraDecisionTree<A, BO, P> decisionTree, A agent,
			P parameter) {
		this.decisionTree = decisionTree;
		this.agent = agent;
		this.parameter = parameter;
	}

	@Override
	public void decide() {
		bos = decisionTree.getBos(agent, parameter);
	}

	@Override
	public Set<BO> getKSelectedBos(int k) {
		if (bos.size() <= k) {
			return bos;
		} else {
			throw new IllegalStateException(
					"Number of Bos retrieved from dections tree exeeds k!");
		}
	}

	@Override
	public int getNumSelectableBOs() {
		if (bos == null) {
			throw new IllegalStateException(
					"Decide() needs to be called before getNumAvailableBOs() is called!");
		}
		return bos.size();
	}

	@Override
	public BO getSelectedBo() {
		if (bos.size() == 1) {
			return bos.iterator().next();
		} else {
			throw new IllegalStateException(
					"Number of Bos retrieved from dections tree exeeds 1!");
		}
	}

	/**
	 * Return the name of this decider and the {@link LaraDecisionConfiguration}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LTreeDecider";
	}
}
