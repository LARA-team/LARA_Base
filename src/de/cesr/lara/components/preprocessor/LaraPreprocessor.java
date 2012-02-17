/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 05.02.2010
 */
package de.cesr.lara.components.preprocessor;


import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;


/**
 * The Pre-process Factory is used to pick the appropriate pre-processor components for the given decision (
 * {@linkLaraDecisionBuilder}).
 * 
 * 
 * @param <A>
 *        the type of agents this pre-process builder is intended for
 * @param <BO>
 *        the type of behavioural options the preprocessor shall manage
 * 
 * @author Sascha Holzhauer
 * @date 05.02.2010
 */
public interface LaraPreprocessor<A extends LaraAgent<? super A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>> {

	/**
	 * @param accuracy
	 * @param agent
	 */
	public abstract void preprocess(LaraDecisionConfiguration dConfig, A agent);

	/**
	 * @param configuration
	 * @return true if this preprocessor meets the given configuration object
	 */
	public boolean meetsConfiguration(
			LaraPreprocessorConfigurator<A, BO> configuration);

	/**
	 * Lists all preprocessor components defined in this preprocessor.
	 * 
	 * @return String
	 */
	public String getComponentsString();

}
