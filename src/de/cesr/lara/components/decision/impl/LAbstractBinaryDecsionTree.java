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


import java.util.List;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraBinaryDecisionTree;
import de.cesr.lara.components.decision.LaraDecisionTree;

/**
 * @param <A>
 *            agent class this decision tree is used by
 * @param <BO>
 *            type of behavioural options
 * @param <P>
 *            the parameter class type
 * 
 */
// public abstract class LAbstractBinaryDecsionTree<A extends LaraAgent<A, ? super BO>, BO extends
// LaraBehaviouralOption<?, BO>, P>
// implements LaraBinaryDecisionTree<A, BO, P> {
public abstract class LAbstractBinaryDecsionTree<A extends LaraAgent<A, ? extends LaraBehaviouralOption<?, ?>>, R, P>
		implements LaraBinaryDecisionTree<A, R, P> {

	/**
	 * This decision tree is evaluated in case evaluate() returns false
	 */
	protected LaraDecisionTree<A, R, P> falseTree = null;

	/**
	 * This decision tree is evaluated in case evaluate() returns true
	 */
	protected LaraDecisionTree<A, R, P> trueTree = null;

	/**
	 * 
	 */
	public LAbstractBinaryDecsionTree() {
	}

	/**
	 * Init the decision tree with true decision tree and false decision tree.
	 * 
	 * @param trueTree
	 *            This decision tree is evaluated in case evaluate() returns
	 *            true
	 * @param falseTree
	 *            This decision tree is evaluated in case evaluate() returns
	 *            false
	 */
	public LAbstractBinaryDecsionTree(LaraDecisionTree<A, R, P> trueTree, LaraDecisionTree<A, R, P> falseTree) {
		this.trueTree = trueTree;
		this.falseTree = falseTree;
	}

	@Override
	public List<R> getBos(A agent, P parameter) {
		if (evaluate(agent, parameter)) {
			if (trueTree != null) {
				return trueTree.getBos(agent, parameter);
			} else {
				throw new IllegalStateException(
						"True Decision Tree has not been set but is requiered!");
			}
		} else {
			if (falseTree != null) {
				return falseTree.getBos(agent, parameter);
			} else {
				throw new IllegalStateException(
						"False Decision Tree has not been set but is requiered!");
			}
		}
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraBinaryDecisionTree#setFalseDecisionTree(de.cesr.lara.components.decision.LaraDecisionTree)
	 */
	@Override
	public void setFalseDecisionTree(LaraDecisionTree<A, R, P> falseTree) {
		this.falseTree = falseTree;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraBinaryDecisionTree#setTrueDecisionTree(de.cesr.lara.components.decision.LaraDecisionTree)
	 */
	@Override
	public void setTrueDecisionTree(LaraDecisionTree<A, R, P> trueTree) {
		this.trueTree = trueTree;
	}

	/**
	 * @return false when the false tree shall be evaluated, true if the true
	 *         tree shall be evaluated.
	 */
	abstract protected boolean evaluate(A agent, P parameter);

}