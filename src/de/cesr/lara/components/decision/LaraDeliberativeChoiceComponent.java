package de.cesr.lara.components.decision;

import java.util.Set;

import de.cesr.lara.components.LaraBehaviouralOption;

/**
 * heuristic used for selection of best behavioural option
 * 
 * @param <BO>
 */
public interface LaraDeliberativeChoiceComponent<BO extends LaraBehaviouralOption<?, BO>> {
	/**
	 * If k is {@link Integer#MAX_VALUE} all available BOs should be returned!
	 * 
	 * Note: The component is to guarantee that for several calls with identical
	 * k the same set of BOs is returned!
	 * 
	 * @param dBuilder
	 *            the decision builder of the current decision
	 * @param matrix
	 * @param k
	 * @return k best behavioural options
	 */
	public Set<? extends BO> getKSelectedBOs(
			LaraDecisionConfiguration dBuilder, LaraUtilityMatrix<BO> matrix,
			int k);

	/**
	 * @param dBuilder
	 *            the decision builder of the current decision
	 * @param matrix
	 * @return behavioural option Created by klemm on 05.02.2010
	 */
	public BO getSelectedBO(LaraDecisionConfiguration dBuilder,
			LaraUtilityMatrix<BO> matrix);
}
