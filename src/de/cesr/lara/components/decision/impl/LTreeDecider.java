/**
 * This file is part of
 * 
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 * 
 * Copyright (C) 2012 Center for Environmental Systems Research, Kassel, Germany
 * 
 * LARA is free software: You can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * LARA is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.cesr.lara.components.decision.impl;


import java.util.Collection;
import java.util.List;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecider;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.LaraDecisionModes;
import de.cesr.lara.components.decision.LaraDecisionTree;


/**
 * @author Sascha Holzhauer
 * @date 25.05.2010
 * 
 *       TODO why no factory for this decider?
 * @param <A>
 *        type of agents
 * @param <BO>
 *        type of behavioural options
 * @param <P>
 *        type of parameter
 * 
 */
public class LTreeDecider<A extends LaraAgent<? super A, ? super BO>, BO extends LaraBehaviouralOption<?, ? extends BO>, P>
		implements
		LaraDecider<BO> {

	A agent;
	List<BO> bos;
	LaraDecisionTree<A, BO, P> decisionTree;
	P parameter;

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
	public int getNumSelectableBOs() {
		if (bos == null) {
			throw new IllegalStateException("Decide() needs to be called before getNumAvailableBOs() is called!");
		}
		return bos.size();
	}

	@Override
	public BO getSelectedBo() {
		if (bos.size() == 1) {
			return bos.iterator().next();
		} else {
			throw new IllegalStateException("Number of Bos retrieved from dections tree exeeds 1!");
		}
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDecider#getDecisionMode()
	 */
	@Override
	public LaraDecisionModes getDecisionMode() {
		return LaraDecisionModes.DELIBERATIVE;
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

	/**
	 * Return bos collection returned by the decision tree.
	 * 
	 * @see de.cesr.lara.components.decision.LaraDecider#getSelectableBos()
	 */
	@Override
	public Collection<BO> getSelectableBos() {
		return bos;
	}

	@Override
	public List<BO> getSelectedBos() {
		return bos;
	}

	@Override
	public void setSelectedBos(List<BO> selectedBos) {
		// TODO Auto-generated method stub

	}
}
