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
package de.cesr.lara.components.decision;


import java.util.List;

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
// public interface LaraDecisionTree<A extends LaraAgent<? super A, ? super BO>, BO extends LaraBehaviouralOption<?, ?
// extends BO>, P> {
public interface LaraDecisionTree<A extends LaraAgent<? super A, ? extends LaraBehaviouralOption<?, ?>>, R, P> {

	/**
	 * Processes this node of the decision tree.
	 * 
	 * @param agent
	 *            the {@link LaraAgent} this decision tree is processed for
	 * @param parameter
	 *            the parameter object (decisionBuilder, for instance)
	 * @return a set of {@link LaraBehaviouralOption}s
	 */
	public List<R> getBos(A agent, P parameter);

}