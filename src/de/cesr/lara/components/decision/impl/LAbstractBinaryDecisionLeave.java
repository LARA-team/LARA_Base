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

import java.util.Set;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecisionTree;

/**
 * @author Sascha Holzhauer
 * @date 25.05.2010
 * 
 * @param <A>
 *            type of agent
 * @param <BO>
 *            type of behavioural option
 * @param <P>
 *            type of tree parameter
 */
public abstract class LAbstractBinaryDecisionLeave<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, BO>, P>
		implements LaraDecisionTree<A, BO, P> {

	/**
	 * @see de.cesr.lara.components.decision.LaraDecisionTree#getBos(de.cesr.lara.components.agents.LaraAgent,
	 *      java.lang.Object)
	 */
	@Override
	abstract public Set<BO> getBos(A agent, P parameter);
}
