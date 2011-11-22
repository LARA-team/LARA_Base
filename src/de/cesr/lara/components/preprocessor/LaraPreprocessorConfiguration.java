/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 05.02.2010
 */
package de.cesr.lara.components.preprocessor;


import java.util.Map;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;


/**
 * This configurator is used to configure a {@link LaraPreprocessorFactory} with desired preprocessor components.
 * 
 * The BO type parameter indicates which BO classes the pre-processor may handle. Components BO type need to exactly
 * match that BO type. <? super BO> as component BO parameter does not work since A<A, ? super BO> does not fulfil ?
 * super (? super BO) regarding BO parameter for the component (in which that would result for the component).
 * 
 * 
 * @param <A>
 *        the type of agents the preprocessor builder is intended for
 * @param <BO>
 *        the type of behavioural options the preprocessor manages
 * 
 * @author Sascha Holzhauer
 * @date 05.02.2010
 * 
 */
public interface LaraPreprocessorConfiguration<A extends LaraAgent<? super A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>> {

	/**
	 * Specifying <code>null</code> corresponds to
	 * {@link LaraPreprocessorConfiguration#setDecisionModeSelector(LaraDecisionModeSelector) )} and sets the default.
	 * 
	 * @param decisionModeSelector
	 * @param dConfiguration
	 *        The {@link LaraDecisionConfiguration} the given mode selector shall be applied to. Created by Sascha Holzhauer
	 *        on 10.02.2010
	 */
	public void setDecisionModeSelector(LaraDecisionModeSelector<A, BO> decisionModeSelector, LaraDecisionConfiguration dConfiguration);

	/**
	 * @param decisionModeSelector
	 *        Created by Sascha Holzhauer on 10.02.2010
	 */
	public void setDecisionModeSelector(LaraDecisionModeSelector<A, BO> decisionModeSelector);

	/**
	 * Specifying <code>null</code> corresponds to {@link LaraPreprocessorConfiguration#setBOCollector(LaraBOCollector)}
	 * and sets the default.
	 * 
	 * @param bOCollector
	 * @param dConfiguration
	 *        The {@link LaraDecisionConfiguration} the given behavioural options collector shall be applied to. Created by
	 *        Sascha Holzhauer on 10.02.2010
	 */
	public void setBOCollector(LaraBOCollector<A, BO> bOCollector, LaraDecisionConfiguration dConfiguration);

	/**
	 * @param boCollector
	 */
	public void setBOCollector(LaraBOCollector<A, BO> boCollector);

	/**
	 * Specifying <code>null</code> corresponds to {@link LaraPreprocessorConfiguration#setBOChecker(LaraBOPreselector)
	 * )} and sets the default.
	 * 
	 * @param boPreselector
	 * @param dConfiguration
	 *        The {@link LaraDecisionConfiguration} the given behavioural options preselector shall be applied to. Created by
	 *        Sascha Holzhauer on 10.02.2010
	 */
	public void setBOPreselector(LaraBOPreselector<A, BO> boPreselector, LaraDecisionConfiguration dConfiguration);

	/**
	 * @author Sascha Holzhauer
	 * @param boChecker
	 * @date 10.11.2009
	 */
	public void setBOChecker(LaraBOPreselector<A, BO> boChecker);

	/**
	 * Specifying <code>null</code> corresponds to
	 * {@link LaraPreprocessorConfiguration#setBOAdapter(LaraBOUtilityUpdaterBuilder)} and sets the default.
	 * 
	 * @param boUpdater
	 * @param dConfiguration
	 *        The {@link LaraDecisionConfiguration} the given behavioural options utilityUpdater shall be applied to. Created by
	 *        Sascha Holzhauer on 10.02.2010
	 */
	public void setBOAdapter(LaraBOUtilityUpdaterBuilder<A, BO> boUpdater, LaraDecisionConfiguration dConfiguration);

	/**
	 * @param boAdapter
	 *        Created by Sascha Holzhauer on 10.02.2010
	 */
	public void setBOAdapter(LaraBOUtilityUpdaterBuilder<A, BO> boAdapter);

	/**
	 * Specifying <code>null</code> corresponds to
	 * {@link LaraPreprocessorConfiguration#setPreferenceUpdater(LaraPreferenceUpdater)} and sets the default.
	 * 
	 * @param prefUpdater
	 * @param dConfiguration
	 *        The {@link LaraDecisionConfiguration} the given preference utilityUpdater shall be applied to. Created by Sascha
	 *        Holzhauer on 10.02.2010
	 */
	public void setPreferenceUpdater(LaraPreferenceUpdater<? extends A> prefUpdater, LaraDecisionConfiguration dConfiguration);

	/**
	 * @param prefUpdater
	 *        Created by Sascha Holzhauer on 10.02.2010
	 */
	public void setPreferenceUpdater(LaraPreferenceUpdater<? extends A> prefUpdater);

	/**
	 * @param <T>
	 *        the generic type of the agent
	 * @param <U>
	 *        the generic type of the requested component
	 * @param dConfiguration
	 *        The {@link LaraDecisionConfiguration} the given component shall be applied to.
	 * @param type
	 *        the general type of the requested component
	 * @param value
	 *        Created by Sascha Holzhauer on 10.02.2010
	 */
	public <T extends LaraPreprocessorComp<A>, U extends LaraPreprocessorComp<?>> void set(
			LaraDecisionConfiguration dConfiguration, Class<U> type, T value);

	/**
	 * @param <T>
	 *        the general type of the requested component
	 * @param dConfiguration
	 *        The {@link LaraDecisionConfiguration} the given component shall be applied to.
	 * @param type
	 *        the general type of the requested component
	 * @return 
	 */
	public <T> T get(LaraDecisionConfiguration dConfiguration, Class<T> type);

	/**
	 * @return preprocessor builder for the given configuration
	 */
	public LaraPreprocessorFactory<A, BO> getPreprocessorFactory();

	/**
	 * Return a new configuration that meets this configuration.
	 * 
	 * @return new configuration that meets this configuration
	 */
	public LaraPreprocessorConfiguration<A, BO> clone();

	/**
	 * @param <T>
	 *        the generic type of the agent
	 * @param <U>
	 *        comp type
	 * @param compType
	 * @return Created by Sascha Holzhauer on 10.02.2010
	 */
	public <T extends LaraPreprocessorComp<A>, U extends LaraPreprocessorComp<?>> Map<LaraDecisionConfiguration, T> getMap(
			Class<U> compType);
}
