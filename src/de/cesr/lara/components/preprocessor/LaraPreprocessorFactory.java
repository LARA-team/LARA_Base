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
 * The Pre-process Factory is used to pick the appropriate pre-processor
 * components for the given decision ( {@linkLaraDecisionBuilder}).
 * 
 * 
 * @param <A>
 *            the type of agents this pre-process builder is intended for
 * @param <BO>
 *            the type of behavioural options the preprocessor shall manage
 * 
 * @author Sascha Holzhauer
 * @date 05.02.2010
 */
public interface LaraPreprocessorFactory<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<? super A, BO>> {

	/**
	 * @param dConfiguration
	 * @return preprocessor Created by klemm on 16.02.2010
	 */
	public LaraPreprocessor<A, BO> getPreprocessor(
			LaraDecisionConfiguration dConfiguration);

	/**
	 * @param configuration
	 * @return true or false Created by klemm on 16.02.2010
	 */
	public boolean meetsConfiguration(
			LaraPreprocessorConfiguration<A, BO> configuration);

}
