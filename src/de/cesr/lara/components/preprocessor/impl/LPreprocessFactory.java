/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 05.02.2010
 */
package de.cesr.lara.components.preprocessor.impl;


import java.util.HashMap;
import java.util.Map;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.preprocessor.LaraBOCollector;
import de.cesr.lara.components.preprocessor.LaraBOPreselector;
import de.cesr.lara.components.preprocessor.LaraBOUtilityUpdaterBuilder;
import de.cesr.lara.components.preprocessor.LaraDecisionModeSelector;
import de.cesr.lara.components.preprocessor.LaraPreferenceUpdater;
import de.cesr.lara.components.preprocessor.LaraPreprocessorFactory;
import de.cesr.lara.components.preprocessor.LaraPreprocessor;
import de.cesr.lara.components.preprocessor.LaraPreprocessorComp;
import de.cesr.lara.components.preprocessor.LaraPreprocessorConfiguration;


/**
 * The {@link LPreprocessFactory} class provides immutable (once assigned, the references to preprocessor components are
 * only passed to the preprocessor constructor which is designed to be used only for one agent during a single
 * simulation step) instances of {@link LPreprocessFactory}s.
 * 
 * IMPLEMENTATION NOTE: An enum implementation would be nice but fails to deal with different types of pre-process
 * components...
 * 
 * TODO rename: LPreprocessorFactory
 * 
 * @author Sascha Holzhauer
 * @param <A>
 *        the type of agents this preprocessor builder is intended for
 * @param <BO>
 *        the type of behavioural options the preprocessor shall manage
 * @date 05.02.2010
 * 
 */
public final class LPreprocessFactory<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
		implements LaraPreprocessorFactory<A, BO> {

	private Map<LaraDecisionConfiguration, LaraDecisionModeSelector<A, BO>>			selectorMap;
	private Map<LaraDecisionConfiguration, LaraBOCollector<A, BO>>				collectorMap;
	private Map<LaraDecisionConfiguration, LaraBOPreselector<A, BO>>				preSelectorMap;
	private Map<LaraDecisionConfiguration, LaraBOUtilityUpdaterBuilder<A, BO>>	updaterMap;
	private Map<LaraDecisionConfiguration, LaraPreferenceUpdater<A>>				prefUpdaterMap;

	private LPreprocessorConfigurator<A, BO>								configuration;

	/**
	 * @param configuration
	 */
	public LPreprocessFactory(LPreprocessorConfigurator<A, BO> configuration) {

		this.configuration = configuration;

		selectorMap = new HashMap<LaraDecisionConfiguration, LaraDecisionModeSelector<A, BO>>();
		collectorMap = new HashMap<LaraDecisionConfiguration, LaraBOCollector<A, BO>>();
		preSelectorMap = new HashMap<LaraDecisionConfiguration, LaraBOPreselector<A, BO>>();
		updaterMap = new HashMap<LaraDecisionConfiguration, LaraBOUtilityUpdaterBuilder<A, BO>>();
		prefUpdaterMap = new HashMap<LaraDecisionConfiguration, LaraPreferenceUpdater<A>>();

		// assigning default components:
		selectorMap.put(null, this.configuration.DEFAULT_DECISION_MODE_SELECTOR);
		collectorMap.put(null, this.configuration.DEFAULT_BO_COLLECTOR);
		preSelectorMap.put(null, this.configuration.DEFAULT_BO_PRESELECTOR);
		updaterMap.put(null, this.configuration.DEFAULT_BO_UPDATE_BUILDER);
		prefUpdaterMap.put(null, this.configuration.DEFAULT_PREFERENCE_UPDATER);

		copyComponentMap(selectorMap, configuration.getMap(LaraDecisionModeSelector.class), LaraDecisionModeSelector.class);
		copyComponentMap(collectorMap, configuration.getMap(LaraBOCollector.class), LaraBOCollector.class);
		copyComponentMap(preSelectorMap, configuration.getMap(LaraBOPreselector.class), LaraBOPreselector.class);
		copyComponentMap(updaterMap, configuration.getMap(LaraBOUtilityUpdaterBuilder.class),
				LaraBOUtilityUpdaterBuilder.class);
		copyComponentMap(prefUpdaterMap, configuration.getMap(LaraPreferenceUpdater.class), LaraPreferenceUpdater.class);
	}

	/**
	 * {@link HashMap#clone()} is not appropriate because default values may not be deleted.
	 * 
	 * Because this method actually requires to be parameterised by [Component]<AgentT> which is not possible so far it
	 * is not type save!
	 * 
	 * @param instanceMap
	 * @param configuratorMap
	 * @param clazz
	 *        Created by Sascha Holzhauer on 08.02.2010
	 */
	@SuppressWarnings("unchecked")
	private void copyComponentMap(Map instanceMap, Map<LaraDecisionConfiguration, ?> configuratorMap, Class s) {
		for (Map.Entry<LaraDecisionConfiguration, ?> entry : configuratorMap.entrySet()) {
			instanceMap.put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * This method returns a {@link LaraPreprocessorConfiguration} that equals this instance apart from the configuration
	 * included in the given {@link LaraPreprocessorConfiguration}.
	 * 
	 * @param changeConfiguration
	 * @return Created by Sascha Holzhauer on 08.02.2010
	 */
	public LaraPreprocessorConfiguration<A, BO> getAlteredConfiguration(
			LaraPreprocessorConfiguration<A, BO> changeConfiguration) {
		// make save against altering the given configurator:
		LaraPreprocessorConfiguration<A, BO> pConfiguration = LPreprocessorConfigurator
				.<A, BO> getDefaultPreprocessConfigurator();

		fillConfiguration(changeConfiguration, pConfiguration, selectorMap, LaraDecisionModeSelector.class);
		fillConfiguration(changeConfiguration, pConfiguration, collectorMap, LaraBOCollector.class);
		fillConfiguration(changeConfiguration, pConfiguration, preSelectorMap, LaraBOPreselector.class);
		fillConfiguration(changeConfiguration, pConfiguration, updaterMap, LaraBOUtilityUpdaterBuilder.class);
		fillConfiguration(changeConfiguration, pConfiguration, prefUpdaterMap, LaraPreferenceUpdater.class);

		return changeConfiguration;
	}

	/**
	 * @param changeConfiguration
	 * @param dConfiguration
	 *        Created by Sascha Holzhauer on 08.02.2010
	 */
	private <T extends LaraPreprocessorComp<A>, U extends LaraPreprocessorComp<?>> void fillConfiguration(
			LaraPreprocessorConfiguration<A, BO> changeConfiguration, LaraPreprocessorConfiguration<A, BO> dConfiguration,
			Map<LaraDecisionConfiguration, T> map, Class<U> clazz) {
		for (Map.Entry<LaraDecisionConfiguration, T> entry : map.entrySet()) {
			if (!(changeConfiguration.get(entry.getKey(), clazz) == null)) {
				changeConfiguration.set(entry.getKey(), clazz, (T) changeConfiguration.get(entry.getKey(), clazz));
			} else {
				changeConfiguration.set(entry.getKey(), clazz, entry.getValue());
			}
		}
	}

	/**
	 * Checks whether this preprocessor builder1 conforms to the configuration represented by the given
	 * {@link LaraPreprocessorConfiguration}.
	 * 
	 * @param configuration
	 * @return true, if every component map matches the given configurator, false else and if the given configurator is
	 *         not compatible
	 * 
	 *         Created by Sascha Holzhauer on 05.02.2010
	 */
	public boolean meetsConfiguration(LaraPreprocessorConfiguration<A, BO> configuration) {
		if (!meetsConfiguration(configuration.getMap(LaraDecisionModeSelector.class), selectorMap,
				this.configuration.DEFAULT_DECISION_MODE_SELECTOR))
			return false;
		if (!meetsConfiguration(configuration.getMap(LaraBOCollector.class), collectorMap,
				this.configuration.DEFAULT_BO_COLLECTOR))
			return false;
		if (!meetsConfiguration(configuration.getMap(LaraBOPreselector.class), preSelectorMap,
				this.configuration.DEFAULT_BO_PRESELECTOR))
			return false;
		if (!meetsConfiguration(configuration.getMap(LaraBOUtilityUpdaterBuilder.class), updaterMap,
				this.configuration.DEFAULT_BO_UPDATE_BUILDER))
			return false;
		if (!meetsConfiguration(configuration.getMap(LaraPreferenceUpdater.class), prefUpdaterMap,
				this.configuration.DEFAULT_PREFERENCE_UPDATER))
			return false;

		return true;
	}

	/**
	 * Tests whether a certain component map matches the configuration of the given {@link DefaultConfigurator}.
	 * 
	 * @param dconfigurator
	 * @param map
	 * @param defaultComp
	 * @return true, if the component map matches the given configurator, false else Created by Sascha Holzhauer on
	 *         08.02.2010
	 */
	private <T> boolean meetsConfiguration(Map<LaraDecisionConfiguration, ? extends T> configurationMap,
			Map<LaraDecisionConfiguration, ? extends T> instanceMap, T defaultComp) {
		// check if all entries of the configurator are included in the builder1 instance (i.e. if the configurator does
		// not define more):
		for (Map.Entry<LaraDecisionConfiguration, ? extends T> entry : configurationMap.entrySet()) {
			if (!instanceMap.containsKey(entry.getKey())) {
				return false;
			}
			if (!entry.getValue().equals(instanceMap.get(entry.getKey()))) {
				return false;
			}
		}
		// Check if every definition of the builder1 is included in the configurator (i.e. the builder1 does not define
		// more):
		for (Map.Entry<LaraDecisionConfiguration, ? extends T> entry : instanceMap.entrySet()) {

			if (entry.getValue() == null && configurationMap.get(entry.getKey()) == null) {
				// component is set to null (to avoid computation)
				break;
			}
			// when a component is set null the configurator also needs to set this component null
			// (testing vice verse is not valid since null is returned when key not defined -> default component
			// defined):
			if (entry.getValue() == null && configurationMap.get(entry.getKey()) != null) {
				return false;
			}
			if (entry.getValue().equals(configurationMap.get(entry.getKey()))) {
				break;
			}
			if (entry.getKey() == null) {
				// when default is set to default component, the configurator may not define anything
				if (entry.getValue() == defaultComp) {
					if (configurationMap.containsKey(null)) {
						return false;
					}
				}
			} else {
				if (!configurationMap.containsKey(entry.getKey())) {
					return false;
				} else {
					if (!entry.getValue().equals(configurationMap.get(entry.getKey()))) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * @see de.cesr.lara.components.preprocessor.LaraPreprocessorFactory#getPreprocessor(de.cesr.lara.components.decision.LaraDecisionConfiguration)
	 */
	@Override
	public LaraPreprocessor<A, BO> getPreprocessor(LaraDecisionConfiguration dConfiguration) {
		return new LPreprocessor<A, BO>(selectorMap.containsKey(dConfiguration) ? selectorMap.get(dConfiguration) : selectorMap
				.get(null), collectorMap.containsKey(dConfiguration) ? collectorMap.get(dConfiguration) : collectorMap.get(null),
				preSelectorMap.containsKey(dConfiguration) ? preSelectorMap.get(dConfiguration) : preSelectorMap.get(null),
				updaterMap.containsKey(dConfiguration) ? updaterMap.get(dConfiguration).getBOUtilityUpdater() : updaterMap
				.get(null)
				.getBOUtilityUpdater(), prefUpdaterMap.containsKey(dConfiguration) ? prefUpdaterMap.get(dConfiguration)
				: prefUpdaterMap.get(null), dConfiguration);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object preprocessorBuilder) {
		// this is valid since there is only one instance per configuration
		if (this == preprocessorBuilder) {
			return true;
		}
		return false;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer text = new StringBuffer();
		text.append("\t ModeSelector:  " + selectorMap.toString() + "\n");
		text.append("\t BOCollector:   " + collectorMap.toString() + "\n");
		text.append("\t BOPreSelector: " + preSelectorMap.toString() + "\n");
		text.append("\t BOUpdater:     " + updaterMap.toString() + "\n");
		text.append("\t PrefUpdater:   " + prefUpdaterMap.toString() + "\n");
		return text.toString();
	}
}