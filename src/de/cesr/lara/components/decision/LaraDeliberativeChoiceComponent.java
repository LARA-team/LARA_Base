package de.cesr.lara.components.decision;


import java.util.Set;

import de.cesr.lara.components.LaraBehaviouralOption;


/**
 * Heuristic used for selection of best behavioural option
 * 
 * TODO This implementation does not allow to define more specific requirements on BO parameter in subclasses.
 *      Alternative: parameterisation (disadvantage: unaesthetic to implement singletons as LaraDeliberativeChoiceComponent) 
 * 
 * @param <BO> Type of BOs this choice component handles (might be more general than LaraDecisionConfiguration's BO)
 */
public interface LaraDeliberativeChoiceComponent {
	/**
	 * @param dBuilder
	 *        the decision builder of the current decision
	 * @param matrix
	 * @return behavioural option
	 */
	public <BO extends LaraBehaviouralOption<?,? extends BO>> BO getSelectedBO(LaraDecisionConfiguration dBuilder, LaraUtilityMatrix<BO> matrix);

	/**
	 * If k is {@link Integer#MAX_VALUE} all available BOs should be returned!
	 * 
	 * Note: The component is to guarantee that for several calls with identical k the same set of BOs is returned!
	 * 
	 * @param dBuilder
	 *        the decision builder of the current decision
	 * @param matrix
	 * @param k
	 * @return k best behavioural options
	 */
	public <BO extends LaraBehaviouralOption<?, ? extends BO>> Set<? extends BO> getKSelectedBOs(LaraDecisionConfiguration dBuilder, LaraUtilityMatrix<BO> matrix,
			int k);
}
