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


import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;

/**
 * @author Sascha Holzhauer
 *
 * @param <A> agent type
 * @param <R> type of result object
 * @param <P> parameter type
 */
public interface LaraBinaryDecisionTree<A extends LaraAgent<? super A, ? extends LaraBehaviouralOption<?, ?>>, R, P>
		extends LaraDecisionTree<A, R, P> {

	/**
	 * @param falseTree
	 *        the decision tree to evaluate when evaluate() returns false
	 */
	public void setFalseDecisionTree(LaraDecisionTree<A, R, P> falseTree);

	/**
	 * @param trueTree
	 *        the decision tree to evaluate when evaluate() returns true
	 */
	public void setTrueDecisionTree(LaraDecisionTree<A, R, P> trueTree);
}
