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


import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecider;
import de.cesr.lara.components.decision.LaraDeciderFactory;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.LaraDecisionTree;


/**
 * This default factory for decision trees provides {@link LTreeDecider} with the decision tree given at construction of
 * this factory.
 * 
 * @author Sascha Holzhauer
 */
public class LTreeDeciderFactory<A extends LaraAgent<? super A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
		implements LaraDeciderFactory<A, BO> {

	LaraDecisionTree<A, BO, LaraDecisionConfiguration> tree;

	/**
	 * @param tree
	 */
	public LTreeDeciderFactory(LaraDecisionTree<A, BO, LaraDecisionConfiguration> tree) {
		this.tree = tree;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDeciderFactory#getDecider(de.cesr.lara.components.agents.LaraAgent,
	 *      de.cesr.lara.components.decision.LaraDecisionConfiguration)
	 */
	@Override
	public LaraDecider<BO> getDecider(A agent, LaraDecisionConfiguration dConfiguration) {
		return new LTreeDecider<A, BO, LaraDecisionConfiguration>(tree, agent, dConfiguration);
	}

}
