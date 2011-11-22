/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * 
 */
package de.cesr.lara.components.preprocessor;


import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;


/**
 * ModeSelector
 * 
 * @param <A>
 *        the type of agents this mode selector is intended for
 */
public interface LaraDecisionModeSelector<A extends LaraAgent<? super A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>> extends LaraPreprocessorComp<A> {

	/**
	 * @param agent
	 *        the type of agents the mode is selected for
	 * @param dBuilder
	 *        decision builder of the current decision process
	 */
	public abstract void selectDecisionMode(A agent, LaraDecisionConfiguration dConfiguration);

}
