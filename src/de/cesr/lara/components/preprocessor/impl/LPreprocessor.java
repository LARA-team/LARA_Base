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
package de.cesr.lara.components.preprocessor.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.eventbus.LaraInternalEventSubscriber;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.eventbus.impl.LEventbus;
import de.cesr.lara.components.preprocessor.LaraBOCollector;
import de.cesr.lara.components.preprocessor.LaraBOPreselector;
import de.cesr.lara.components.preprocessor.LaraBOUtilityUpdater;
import de.cesr.lara.components.preprocessor.LaraDecisionModeSelector;
import de.cesr.lara.components.preprocessor.LaraPreferenceUpdater;
import de.cesr.lara.components.preprocessor.LaraPreprocessor;
import de.cesr.lara.components.preprocessor.LaraPreprocessorComp;
import de.cesr.lara.components.preprocessor.LaraPreprocessorConfigurator;
import de.cesr.lara.components.preprocessor.event.LPpBoCollectorEvent;
import de.cesr.lara.components.preprocessor.event.LPpBoPreselectorEvent;
import de.cesr.lara.components.preprocessor.event.LPpBoUtilityUpdaterEvent;
import de.cesr.lara.components.preprocessor.event.LPpModeSelectorEvent;
import de.cesr.lara.components.preprocessor.event.LPpPreferenceUpdaterEvent;
import de.cesr.lara.components.preprocessor.event.LPpUnsubscribeEvent;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;

/**
 * The {@link LPreprocessor} class provides immutable (once assigned, the
 * references to preprocessor components are only passed to the preprocessor
 * constructor which is designed to be used only for one agent during a single
 * simulation step) instances of {@link LPreprocessor}s.
 * 
 * Default implementation of {@link LaraPreprocessor}. Supports comprehensive
 * logging.
 * 
 * IMPLEMENTATION NOTE: An enum implementation would be nice but fails to deal
 * with different types of pre-process components...
 * 
 * @author Sascha Holzhauer
 * @param <A>
 *            the type of agents this preprocessor builder is intended for
 * @param <BO>
 *            the type of behavioural options the preprocessor shall manage
 * @date 05.02.2010
 * 
 */
public final class LPreprocessor<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
		implements LaraPreprocessor<A, BO>, LaraInternalEventSubscriber {

	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger.getLogger(LPreprocessor.class);

	private final Map<LaraDecisionConfiguration, LaraDecisionModeSelector<A, BO>> selectorMap;

	private final Map<LaraDecisionConfiguration, LaraBOCollector<A, BO>> collectorMap;

	private final Map<LaraDecisionConfiguration, LaraBOPreselector<A, BO>> preSelectorMap;

	private final Map<LaraDecisionConfiguration, LaraBOUtilityUpdater<A, BO>> updaterMap;

	private final Map<LaraDecisionConfiguration, LaraPreferenceUpdater<A, BO>> prefUpdaterMap;

	private final LaraPreprocessorConfigurator<A, BO> configuration;

	/**
	 * Use {@link LaraPreprocessorConfigurator#getPreprocessor()} to receive an
	 * instance of a pre-processor (it keeps instances and returns it if
	 * appropriate)!
	 * 
	 * Uses LPreprocessorConfigurator.<A, BO>getDefaultPreprocessConfigurator().
	 */
	protected LPreprocessor() {
		this(LPreprocessorConfigurator.<A, BO> getNewPreprocessorConfigurator());
	}

	/**
	 * Use {@link LaraPreprocessorConfigurator#getPreprocessor()} to receive an
	 * instance of a pre-processor (it keeps instances and returns it if
	 * appropriate)!
	 * 
	 * @param configuration
	 */
	protected LPreprocessor(LaraPreprocessorConfigurator<A, BO> configuration) {

		this.configuration = configuration;

		selectorMap = new HashMap<LaraDecisionConfiguration, LaraDecisionModeSelector<A, BO>>();
		collectorMap = new HashMap<LaraDecisionConfiguration, LaraBOCollector<A, BO>>();
		preSelectorMap = new HashMap<LaraDecisionConfiguration, LaraBOPreselector<A, BO>>();
		updaterMap = new HashMap<LaraDecisionConfiguration, LaraBOUtilityUpdater<A, BO>>();
		prefUpdaterMap = new HashMap<LaraDecisionConfiguration, LaraPreferenceUpdater<A, BO>>();

		// assigning default components:
		selectorMap.put(null, configuration.getDefaultDecisionModeSelector());
		collectorMap.put(null, this.configuration.getDefaultBoCollector());
		preSelectorMap.put(null, this.configuration.getDefaultBoPreselector());
		updaterMap.put(null, this.configuration.getDefaultBoUtilityUpdater());
		prefUpdaterMap.put(null,
				this.configuration.getDefaultPreferenceUpdater());

		copyComponentMap(
				selectorMap,
				configuration
						.<LaraDecisionModeSelector<A, BO>> getMap(LaraDecisionModeSelector.class),
				LaraDecisionModeSelector.class);
		copyComponentMap(
				collectorMap,
				configuration
						.<LaraBOCollector<A, BO>> getMap(LaraBOCollector.class),
				LaraBOCollector.class);
		copyComponentMap(
				preSelectorMap,
				configuration
						.<LaraBOPreselector<A, BO>> getMap(LaraBOPreselector.class),
				LaraBOPreselector.class);
		copyComponentMap(
				updaterMap,
				configuration
						.<LaraBOUtilityUpdater<A, BO>> getMap(LaraBOUtilityUpdater.class),
				LaraBOUtilityUpdater.class);
		copyComponentMap(
				prefUpdaterMap,
				configuration
						.<LaraPreferenceUpdater<A, BO>> getMap(LaraPreferenceUpdater.class),
				LaraPreferenceUpdater.class);
	}

	/**
	 * This method returns a {@link LaraPreprocessorConfigurator} that equals
	 * this instance apart from the configuration included in the given
	 * {@link LaraPreprocessorConfigurator}.
	 * 
	 * @param changeConfiguration
	 * @return LaraPreprocessorConfigurator
	 */
	public LaraPreprocessorConfigurator<A, BO> getAlteredConfiguration(
			LaraPreprocessorConfigurator<A, BO> changeConfiguration) {
		// make save against altering the given configurator:
		LaraPreprocessorConfigurator<A, BO> pConfiguration = LPreprocessorConfigurator
				.<A, BO> getNewPreprocessorConfigurator();

		fillConfiguration(changeConfiguration, pConfiguration, selectorMap,
				LaraDecisionModeSelector.class);
		fillConfiguration(changeConfiguration, pConfiguration, collectorMap,
				LaraBOCollector.class);
		fillConfiguration(changeConfiguration, pConfiguration, preSelectorMap,
				LaraBOPreselector.class);
		fillConfiguration(changeConfiguration, pConfiguration, updaterMap,
				LaraBOUtilityUpdater.class);
		fillConfiguration(changeConfiguration, pConfiguration, prefUpdaterMap,
				LaraPreferenceUpdater.class);

		return changeConfiguration;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String getComponentsString() {
		StringBuffer text = new StringBuffer();
		text.append("\tPreprocessor:" + System.getProperty("line.separator"));
		text.append("\tModeSelector:  " + selectorMap.toString() + System.getProperty("line.separator"));
		text.append("\tBOCollector:   " + collectorMap.toString() + System.getProperty("line.separator"));
		text.append("\tBOPreSelector: " + preSelectorMap.toString() + System.getProperty("line.separator"));
		text.append("\tBOUpdater:     " + updaterMap.toString() + System.getProperty("line.separator"));
		text.append("\tPrefUpdater:   " + prefUpdaterMap.toString() + System.getProperty("line.separator"));
		return text.toString();
	}

	/**
	 * Checks whether this preprocessor conforms to the configuration
	 * represented by the given {@link LaraPreprocessorConfigurator}.
	 * 
	 * @param configuration
	 * @return true, if every component map matches the given configurator,
	 *         false else and if the given configurator is not compatible
	 */
	@Override
	public boolean meetsConfiguration(
			LaraPreprocessorConfigurator<A, BO> configuration) {
		if (!meetsConfiguration(
				configuration
						.<LaraDecisionModeSelector<A, BO>> getMap(LaraDecisionModeSelector.class),
				selectorMap, this.configuration
						.getDefaultDecisionModeSelector())) {
			return false;
		}
		if (!meetsConfiguration(
				configuration
						.<LaraBOCollector<A, BO>> getMap(LaraBOCollector.class),
				collectorMap, this.configuration.getDefaultBoCollector())) {
			return false;
		}
		if (!meetsConfiguration(
				configuration
						.<LaraBOPreselector<A, BO>> getMap(LaraBOPreselector.class),
				preSelectorMap, this.configuration.getDefaultBoPreselector())) {
			return false;
		}
		if (!meetsConfiguration(
				configuration
						.<LaraBOUtilityUpdater<A, BO>> getMap(LaraBOUtilityUpdater.class),
				updaterMap, this.configuration.getDefaultBoUtilityUpdater())) {
			return false;
		}
		if (!meetsConfiguration(
				configuration
						.<LaraPreferenceUpdater<A, BO>> getMap(LaraPreferenceUpdater.class),
				prefUpdaterMap, this.configuration
						.getDefaultPreferenceUpdater())) {
			return false;
		}

		return true;
	}

	/**
	 * @see de.cesr.lara.components.eventbus.LaraInternalEventSubscriber#onInternalEvent(de.cesr.lara.components.eventbus.events.LaraEvent)
	 */
	@Override
	public void onInternalEvent(LaraEvent event) {
		if (event instanceof LPpUnsubscribeEvent) {
			@SuppressWarnings("unchecked")
			A agent = (A) ((LPpUnsubscribeEvent) event).getAgent();
			LEventbus eBus = LEventbus.getInstance(agent);
			eBus.unsubscribe(LPpModeSelectorEvent.class);
			eBus.unsubscribe(LPpBoCollectorEvent.class);
			eBus.unsubscribe(LPpBoPreselectorEvent.class);
			eBus.unsubscribe(LPpBoUtilityUpdaterEvent.class);
			eBus.unsubscribe(LPpPreferenceUpdaterEvent.class);
		}
	}

	/**
	 * 
	 * @see de.cesr.lara.components.preprocessor.LaraPreprocessor#preprocess(LaraDecisionConfiguration, LaraAgent)
	 */
	@Override
	public void preprocess(LaraDecisionConfiguration dConfig, A agent) {

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(agent + "> preprocess...");
		}
		// LOGGING ->

		LEventbus eBus = LEventbus.getInstance(agent);

		eBus.subscribe(
				selectorMap.containsKey(dConfig) ? selectorMap.get(dConfig)
						: selectorMap.get(null), LPpModeSelectorEvent.class);
		eBus.subscribe(
				collectorMap.containsKey(dConfig) ? collectorMap.get(dConfig)
						: collectorMap.get(null), LPpBoCollectorEvent.class);
		eBus.subscribe(
				preSelectorMap.containsKey(dConfig) ? preSelectorMap
						.get(dConfig) : preSelectorMap.get(null),
				LPpBoPreselectorEvent.class);
		eBus.subscribe(
				updaterMap.containsKey(dConfig) ? updaterMap.get(dConfig)
						: updaterMap.get(null), LPpBoUtilityUpdaterEvent.class);
		eBus.subscribe(
				prefUpdaterMap.containsKey(dConfig) ? prefUpdaterMap
						.get(dConfig) : prefUpdaterMap.get(null),
				LPpPreferenceUpdaterEvent.class);

		// TODO substitute by 'once' events!
		eBus.subscribe(this, LPpUnsubscribeEvent.class);

		eBus.publish(new LPpModeSelectorEvent(agent, dConfig));
		eBus.publish(new LPpUnsubscribeEvent(agent, dConfig));
	}

	/**
	 * {@link HashMap#clone()} is not appropriate because default values may not
	 * be deleted.
	 * 
	 * Because this method actually requires to be parameterised by
	 * [Component]<AgentT> which is not possible so far it is not type save!
	 * 
	 * @param instanceMap
	 * @param configuratorMap
	 * @param clazz
	 */
	private <P> void copyComponentMap(
			Map<LaraDecisionConfiguration, P> instanceMap,
			Map<LaraDecisionConfiguration, P> configuratorMap,
			Class<? super P> p) {
		for (Map.Entry<LaraDecisionConfiguration, P> entry : configuratorMap
				.entrySet()) {
			instanceMap.put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * @param changeConfiguration
	 * @param dConfiguration
	 */
	private <T extends LaraPreprocessorComp<A, BO>> void fillConfiguration(
			LaraPreprocessorConfigurator<A, BO> changeConfiguration,
			LaraPreprocessorConfigurator<A, BO> dConfiguration,
			Map<LaraDecisionConfiguration, T> map, Class<? super T> clazz) {
		for (Map.Entry<LaraDecisionConfiguration, T> entry : map.entrySet()) {
			if (!(changeConfiguration.get(entry.getKey(), clazz) == null)) {
				changeConfiguration.set(entry.getKey(), clazz,
						changeConfiguration.<T> get(entry.getKey(), clazz));
			} else {
				changeConfiguration
						.set(entry.getKey(), clazz, entry.getValue());
			}
		}
	}

	/**
	 * Tests whether a certain component map matches the configuration of the
	 * given {@link DefaultConfigurator}.
	 * 
	 * @param dconfigurator
	 * @param map
	 * @param defaultComp
	 * @return true, if the component map matches the given configurator, false
	 *         else
	 */
	private <T> boolean meetsConfiguration(
			Map<LaraDecisionConfiguration, ? extends T> configurationMap,
			Map<LaraDecisionConfiguration, ? extends T> instanceMap,
			T defaultComp) {
		// check if all entries of the configurator are included in the builder1
		// instance (i.e. if the configurator does
		// not define more):
		for (Map.Entry<LaraDecisionConfiguration, ? extends T> entry : configurationMap
				.entrySet()) {
			if (!instanceMap.containsKey(entry.getKey())) {
				return false;
			}
			if (!entry.getValue().equals(instanceMap.get(entry.getKey()))) {
				return false;
			}
		}
		// Check if every definition of the builder1 is included in the
		// configurator (i.e. the builder1 does not define
		// more):
		for (Map.Entry<LaraDecisionConfiguration, ? extends T> entry : instanceMap
				.entrySet()) {

			if (entry.getValue() == null
					&& configurationMap.get(entry.getKey()) == null) {
				// component is set to null (to avoid computation)
				break;
			}
			// when a component is set null the configurator also needs to set
			// this component null
			// (testing vice verse is not valid since null is returned when key
			// not defined -> default component
			// defined):
			if (entry.getValue() == null
					&& configurationMap.get(entry.getKey()) != null) {
				return false;
			}
			if (entry.getValue().equals(configurationMap.get(entry.getKey()))) {
				break;
			}
			if (entry.getKey() == null) {
				// when default is set to default component, the configurator
				// may not define anything
				if (entry.getValue() == defaultComp) {
					if (configurationMap.containsKey(null)) {
						return false;
					}
				}
			} else {
				if (!configurationMap.containsKey(entry.getKey())) {
					return false;
				} else {
					if (!entry.getValue().equals(
							configurationMap.get(entry.getKey()))) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public String getConfigurationString(LaraDecisionConfiguration dConfig) {
		StringBuffer buffer = new StringBuffer("Configuration for " + dConfig
				+ ":");
		buffer.append("\n\tMode selector:\t");
		buffer.append(this.selectorMap.containsKey(dConfig) ? this.selectorMap
				.get(dConfig) : this.selectorMap.get(null));
		buffer.append("\n\tBO collector:\t");
		buffer.append(this.collectorMap.containsKey(dConfig) ? this.collectorMap
				.get(dConfig) : this.collectorMap.get(null));
		buffer.append("\n\tBO selector:\t");
		buffer.append(this.preSelectorMap.containsKey(dConfig) ? this.preSelectorMap
				.get(dConfig) : this.preSelectorMap.get(null));
		buffer.append("\n\tBO updater:\t");
		buffer.append(this.updaterMap.containsKey(dConfig) ? this.updaterMap
				.get(dConfig) : this.updaterMap.get(null));
		buffer.append("\n\tPref updater:\t");
		buffer.append(this.prefUpdaterMap.containsKey(dConfig) ? this.prefUpdaterMap
				.get(dConfig) : this.prefUpdaterMap.get(null));

		return buffer.toString();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this
				.getClass()
				.getName();
	}
}