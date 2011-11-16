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
 * @param <A>
 *            agent class this decision tree is used by
 * @param <BO>
 *            type of behavioural options
 * @param <P>
 *            the parameter class type
 * 
 */
public abstract class LAbstractBinaryDecsionTree<A extends LaraAgent<A, ? super BO>, BO extends LaraBehaviouralOption<? super A, BO>, P>
		implements LaraDecisionTree<A, BO, P> {

	/**
	 * This decision tree is evaluated in case evaluate() returns false
	 */
	protected LaraDecisionTree<A, BO, P> falseTree = null;

	/**
	 * This decision tree is evaluated in case evaluate() returns true
	 */
	protected LaraDecisionTree<A, BO, P> trueTree = null;

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
	public LAbstractBinaryDecsionTree(LaraDecisionTree<A, BO, P> trueTree,
			LaraDecisionTree<A, BO, P> falseTree) {
		this.trueTree = trueTree;
		this.falseTree = falseTree;
	}

	@Override
	public Set<BO> getBos(A agent, P parameter) {
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
	 * @param falseTree
	 *            the decision tree to evaluate when evaluate() returns false
	 */
	public void setFalseDecisionTree(LaraDecisionTree<A, BO, P> falseTree) {
		this.falseTree = falseTree;
	}

	/**
	 * @param trueTree
	 *            the decision tree to evaluate when evaluate() returns true
	 */
	public void setTrueDecisionTree(LaraDecisionTree<A, BO, P> trueTree) {
		this.trueTree = trueTree;
	}

	/**
	 * @return false when the false tree shall be evaluated, true if the true
	 *         tree shall be evaluated.
	 */
	abstract protected boolean evaluate(A agent, P parameter);
}
