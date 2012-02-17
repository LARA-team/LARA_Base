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
package de.cesr.lara.components.preprocessor;


import java.util.Map;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;

/**
 * This configurator is used to configure a {@link LaraPreprocessor} with
 * desired preprocessor components.
 * 
 * The BO type parameter indicates which BO classes the pre-processor may
 * handle. Components BO type need to exactly match that BO type. <? super BO>
 * as component BO parameter does not work since A<A, ? super BO> does not
 * fulfill ? super (? super BO) regarding BO parameter for the component (in
 * which that would result for the component).
 * 
 * 
 * @param <A>
 *            the type of agents the preprocessor builder is intended for
 * @param <BO>
 *            the type of behavioural options the preprocessor manages
 * 
 * @author Sascha Holzhauer
 * @date 05.02.2010
 * 
 */
public interface LaraPreprocessorConfigurator<A extends LaraAgent<? super A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>> {

	/**
	 * Specifying <code>null</code> corresponds to
	 * {@link LaraPreprocessorConfigurator#setDecisionModeSelector(LaraDecisionModeSelector)
	 * )} and sets the default.
	 * 
	 * @param decisionModeSelector
	 * @param dConfiguration
	 *            The {@link LaraDecisionConfiguration} the given mode selector
	 *            shall be applied to.
	 */
	public void setDecisionModeSelector(LaraDecisionModeSelector<A, BO> decisionModeSelector, LaraDecisionConfiguration dConfiguration);

	/**
	 * @param decisionModeSelector
	 */
	public void setDecisionModeSelector(LaraDecisionModeSelector<A, BO> decisionModeSelector);

	/**
	 * Specifying <code>null</code> corresponds to
	 * {@link LaraPreprocessorConfigurator#setBOCollector(LaraBOCollector)} and
	 * sets the default.
	 * 
	 * @param bOCollector
	 * @param dConfiguration
	 *            The {@link LaraDecisionConfiguration} the given behavioural
	 *            options collector shall be applied to.
	 */
	public void setBOCollector(LaraBOCollector<A, BO> bOCollector, LaraDecisionConfiguration dConfiguration);

	/**
	 * @param boCollector
	 */
	public void setBOCollector(LaraBOCollector<A, BO> boCollector);

	/**
	 * Specifying <code>null</code> corresponds to
	 * {@link LaraPreprocessorConfigurator#setBoPreselector(LaraBOPreselector)
	 * )} and sets the default.
	 * 
	 * @param boPreselector
	 * @param dConfiguration
	 *            The {@link LaraDecisionConfiguration} the given behavioural
	 *            options preselector shall be applied to.
	 */
	public void setBOPreselector(LaraBOPreselector<A, BO> boPreselector, LaraDecisionConfiguration dConfiguration);

	/**
	 * @author Sascha Holzhauer
	 * @param boChecker
	 * @date 10.11.2009
	 */
	public void setBoPreselector(LaraBOPreselector<A, BO> boChecker);

	/**
	 * Specifying <code>null</code> corresponds to
	 * {@link LaraPreprocessorConfigurator#setBOAdapter(LaraBOUtilityUpdaterBuilder)}
	 * and sets the default.
	 * 
	 * @param boUpdater
	 * @param dConfiguration
	 *            The {@link LaraDecisionConfiguration} the given behavioural
	 *            options utilityUpdater shall be applied to.
	 */
	public void setBOAdapter(LaraBOUtilityUpdater<A, BO> boUpdater,
			LaraDecisionConfiguration dConfiguration);

	/**
	 * @param boAdapter
	 */
	public void setBOAdapter(LaraBOUtilityUpdater<A, BO> boAdapter);

	/**
	 * Specifying <code>null</code> corresponds to
	 * {@link LaraPreprocessorConfigurator#setPreferenceUpdater(LaraPreferenceUpdater)}
	 * and sets the default.
	 * 
	 * @param prefUpdater
	 * @param dConfiguration
	 *            The {@link LaraDecisionConfiguration} the given preference
	 *            utilityUpdater shall be applied to.
	 */
	public void setPreferenceUpdater(
			LaraPreferenceUpdater<? extends A, BO> prefUpdater,
			LaraDecisionConfiguration dConfiguration);

	/**
	 * @param prefUpdater
	 */
	public void setPreferenceUpdater(
			LaraPreferenceUpdater<? extends A, BO> prefUpdater);

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
	 */
	public <T extends LaraPreprocessorComp<A, BO>> void set(
			LaraDecisionConfiguration dConfiguration, Class<? super T> type,
			T value);

	/**
	 * @param <T>
	 *        the general type of the requested component
	 * @param dConfiguration
	 *        The {@link LaraDecisionConfiguration} the given component shall be applied to.
	 * @param type
	 *        the general type of the requested component
	 * @return 
	 */
	public <T> T get(LaraDecisionConfiguration dConfiguration,
			Class<? super T> type);

	/**
	 * Provides the {@link LaraPreprocessor} that matches this configuration.
	 * 
	 * @return preprocessor for the given configuration
	 */
	public LaraPreprocessor<A, BO> getPreprocessor();

	/**
	 * Return a new configuration that meets this configuration. This is
	 * intended for creating new {@link LaraPreprocessor}s with a different
	 * configuration. I.e. the provided configuration is altered and used to
	 * create a new pre-processor.
	 * 
	 * @return new configuration that meets this configuration
	 */
	public LaraPreprocessorConfigurator<A, BO> clone();

	/**
	 * Provides the map of {@link LaraDecisionConfiguration} to
	 * {@link LaraPreprocessorComp} as defined by the given component class.
	 * 
	 * @param <T>
	 *            the generic type of the agent
	 * @param compType
	 *            the class of component type that is requested
	 * @return the map of defined components
	 */
	public <T extends LaraPreprocessorComp<A, BO>> Map<LaraDecisionConfiguration, T> getMap(
			Class<? super T> compType);

	/*********************************************
	 * DEFAULT value ACCESSORS
	 ********************************************/

	/**
	 * @return the default {@link LaraDecisionConfiguration}
	 */
	public LaraDecisionModeSelector<A, BO> getDefaultDecisionModeSelector();

	/**
	 * @return the default {@link LaraBOCollector}
	 */
	public LaraBOCollector<A, BO> getDefaultBoCollector();

	/**
	 * @return the default {@link LaraBOPreselector}
	 */
	public LaraBOPreselector<A, BO> getDefaultBoPreselector();

	/**
	 * @return the default {@link LaraBOUtilityUpdater}
	 */
	public LaraBOUtilityUpdater<A, BO> getDefaultBoUtilityUpdater();

	/**
	 * @return the default {@link LaraPreferenceUpdater}
	 */
	public LaraPreferenceUpdater<A, BO> getDefaultPreferenceUpdater();
}
