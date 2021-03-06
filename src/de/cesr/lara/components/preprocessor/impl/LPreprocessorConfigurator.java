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

import org.simpleframework.xml.ElementMap;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.preprocessor.LaraBOCollector;
import de.cesr.lara.components.preprocessor.LaraBOPreselector;
import de.cesr.lara.components.preprocessor.LaraBOUtilityUpdater;
import de.cesr.lara.components.preprocessor.LaraDecisionModeSelector;
import de.cesr.lara.components.preprocessor.LaraPreferenceUpdater;
import de.cesr.lara.components.preprocessor.LaraPreprocessor;
import de.cesr.lara.components.preprocessor.LaraPreprocessorComp;
import de.cesr.lara.components.preprocessor.LaraPreprocessorConfigurator;


/**
 * default configurator
 * 
 * @param <A>
 *        the type of agents the according preprocessor builder is intended for
 * @param <BO>
 *        the type of behavioural options the preprocessor shall manage
 * 
 *        TODO why can't A be A extends LaraAgent<? super A, BO>
 */
public class LPreprocessorConfigurator<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
		implements LaraPreprocessorConfigurator<A, BO> {

	@SuppressWarnings("unchecked")
	// default components actually do not have any constraints regarding type
	// parameters. TODO check
	public static <A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>> LaraPreprocessor<A, BO> getDefaultPreprocessor() {
		if (defaultPreprocessor == null) {
			defaultPreprocessor = new LPreprocessor<A, BO>(
					LPreprocessorConfigurator
							.<A, BO> getNewPreprocessorConfigurator());
		}
		return (LaraPreprocessor<A, BO>) defaultPreprocessor;
	}

	/**
	 * @param <A>
	 * @param <BO>
	 * @return the default configurator
	 */
	public static <A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>> LPreprocessorConfigurator<A, BO> getNewPreprocessorConfigurator() {
		return new LPreprocessorConfigurator<A, BO>();
	}

	@ElementMap(entry = "selectorEntry", key = "dConfig", value = "selector", attribute = false, inline = true, required = false)
	private Map<LaraDecisionConfiguration, LaraDecisionModeSelector<A, BO>> selectorMap = new HashMap<LaraDecisionConfiguration, LaraDecisionModeSelector<A, BO>>();

	@ElementMap(entry = "collectorEntry", key = "dConfig", value = "collector", attribute = false, inline = true, required = false)
	private Map<LaraDecisionConfiguration, LaraBOCollector<A, ? extends BO>> scannerMap = new HashMap<LaraDecisionConfiguration, LaraBOCollector<A, ? extends BO>>();

	@ElementMap(entry = "checkerEntry", key = "dConfig", value = "checker", attribute = false, inline = true, required = false)
	private Map<LaraDecisionConfiguration, LaraBOPreselector<A, ? extends BO>> checkerMap = new HashMap<LaraDecisionConfiguration, LaraBOPreselector<A, ? extends BO>>();

	@ElementMap(entry = "boUpdaterEntry", key = "dConfig", value = "updater", attribute = false, inline = true, required = false)
	private Map<LaraDecisionConfiguration, LaraBOUtilityUpdater<A, BO>> adapterMap = new HashMap<LaraDecisionConfiguration, LaraBOUtilityUpdater<A, BO>>();

	@ElementMap(entry = "prefUpdaterEntry", key = "dConfig", value = "updater", attribute = false, inline = true, required = false)
	private Map<LaraDecisionConfiguration, LaraPreferenceUpdater<? extends A, BO>> prefUpdaterMap = new HashMap<>();

	private LaraPreprocessor<A, BO> preprocessor = null;
	private static LaraPreprocessor<?, ?> defaultPreprocessor = null;
	/**
	 * 
	 */
	public final LaraDecisionModeSelector<A, BO> DEFAULT_DECISION_MODE_SELECTOR = new LDefaultDecisionModeSelector<A, BO>();
	/**
	 * 
	 */
	public final LaraBOCollector<A, BO> DEFAULT_BO_COLLECTOR = new LCompleteBoCollector<A, BO>();

	/**
	 * 
	 */
	public final LaraBOPreselector<A, BO> DEFAULT_BO_PRESELECTOR = new LDelegatingBoPreselector<A, BO>();

	/**
	 * 
	 */
	public final LaraBOUtilityUpdater<A, BO> DEFAULT_BO_UPDATE_BUILDER = new LDefaultBOUpdater<A, BO>();

	/**
	 * 
	 */
	public final LaraPreferenceUpdater<A, BO> DEFAULT_PREFERENCE_UPDATER = new LPseudoPrefereceUpdater<A, BO>();

	/**
	 * constructor
	 */
	private LPreprocessorConfigurator() {
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	public LaraPreprocessorConfigurator<A, BO> clone() {
		LPreprocessorConfigurator<A, BO> clone = new LPreprocessorConfigurator<A, BO>();
		clone.selectorMap.putAll(this.selectorMap);
		clone.adapterMap.putAll(this.adapterMap);
		clone.checkerMap.putAll(this.checkerMap);
		clone.prefUpdaterMap.putAll(this.prefUpdaterMap);
		clone.scannerMap.putAll(this.scannerMap);
		return clone;
	}

	/**
	 * @see de.cesr.lara.components.preprocessor.LaraPreprocessorConfigurator#get(de.cesr.lara.components.decision.LaraDecisionConfiguration,
	 *      java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(LaraDecisionConfiguration dConfiguration,
			Class<? super T> type) {
		if (LaraDecisionModeSelector.class.isAssignableFrom(type)) {
			return (T) selectorMap.get(dConfiguration);
		}
		if (LaraBOCollector.class.isAssignableFrom(type)) {
			return (T) scannerMap.get(dConfiguration);
		}
		if (LaraBOPreselector.class.isAssignableFrom(type)) {
			return (T) checkerMap.get(dConfiguration);
		}
		if (LaraBOUtilityUpdater.class.isAssignableFrom(type)) {
			return (T) adapterMap.get(dConfiguration);
		}
		if (LaraPreferenceUpdater.class.isAssignableFrom(type)) {
			return (T) prefUpdaterMap.get(dConfiguration);
		}
		throw new AssertionError("Missing case in DefaultConfigurator#get()");
	}

	/**
	 * @see de.cesr.lara.components.preprocessor.LaraPreprocessorConfigurator#getDefaultBoCollector()
	 */
	@Override
	public LaraBOCollector<A, BO> getDefaultBoCollector() {
		return DEFAULT_BO_COLLECTOR;
	}

	/**
	 * @see de.cesr.lara.components.preprocessor.LaraPreprocessorConfigurator#getDefaultBoPreselector()
	 */
	@Override
	public LaraBOPreselector<A, BO> getDefaultBoPreselector() {
		return DEFAULT_BO_PRESELECTOR;
	}

	/**
	 * @see de.cesr.lara.components.preprocessor.LaraPreprocessorConfigurator#getDefaultBoUtilityUpdater()
	 */
	@Override
	public LaraBOUtilityUpdater<A, BO> getDefaultBoUtilityUpdater() {
		return DEFAULT_BO_UPDATE_BUILDER;
	}

	/**
	 * @see de.cesr.lara.components.preprocessor.LaraPreprocessorConfigurator#getDefaultDecisionModeSelector()
	 */
	@Override
	public LaraDecisionModeSelector<A, BO> getDefaultDecisionModeSelector() {
		return DEFAULT_DECISION_MODE_SELECTOR;
	}

	/**
	 * @see de.cesr.lara.components.preprocessor.LaraPreprocessorConfigurator#getDefaultPreferenceUpdater()
	 */
	@Override
	public LaraPreferenceUpdater<A, BO> getDefaultPreferenceUpdater() {
		return DEFAULT_PREFERENCE_UPDATER;
	}

	/**
	 * @see de.cesr.lara.components.preprocessor.LaraPreprocessorConfigurator#getMap(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends LaraPreprocessorComp<A, BO>> Map<LaraDecisionConfiguration, T> getMap(
			Class<? super T> type) {
		try {
			if (LaraDecisionModeSelector.class.isAssignableFrom(type)) {
				return (Map<LaraDecisionConfiguration, T>) selectorMap;
			}
			if (LaraBOCollector.class.isAssignableFrom(type)) {
				return (Map<LaraDecisionConfiguration, T>) scannerMap;
			}
			if (LaraBOPreselector.class.isAssignableFrom(type)) {
				return (Map<LaraDecisionConfiguration, T>) checkerMap;
			}
			if (LaraBOUtilityUpdater.class.isAssignableFrom(type)) {
				return (Map<LaraDecisionConfiguration, T>) adapterMap;
			}
			if (LaraPreferenceUpdater.class.isAssignableFrom(type)) {
				return (Map<LaraDecisionConfiguration, T>) prefUpdaterMap;
			}
			throw new AssertionError(
					"Missing case in DefaultConfigurator#getMap()");
		} catch (ClassCastException e) {
			throw new ClassCastException(
					"Agent parameter type did not match requested one!");
		}
	}

	/**
	 * Checks whether the stored preprocessor matches this configuration and
	 * return it if true. Builds a new preprocessor of the current configuration
	 * otherwise.
	 * 
	 * @see de.cesr.lara.components.preprocessor.LaraPreprocessorConfigurator#getPreprocessor()
	 */
	@Override
	public LaraPreprocessor<A, BO> getPreprocessor() {
		if (this.preprocessor == null
				|| !this.preprocessor.meetsConfiguration(this)) {
			this.preprocessor = new LPreprocessor<A, BO>(this);
		}
		return this.preprocessor;
	}

	/**
	 * @param <T>
	 *            the component type regarding agent type of the requested
	 *            component
	 * @param dConfiguration
	 *            The {@link LaraDecisionConfiguration} the given component
	 *            shall be applied to.
	 * @param type
	 *            the general type of the requested component
	 * @param value
	 * 
	 */
	@Override
	public <T extends LaraPreprocessorComp<A, BO>> void set(
			LaraDecisionConfiguration dConfiguration, Class<? super T> type,
			T value) {
		if (LaraDecisionModeSelector.class.isAssignableFrom(type)) {
			selectorMap.put(dConfiguration,
					(LaraDecisionModeSelector<A, BO>) value);
		}
		if (LaraBOCollector.class.isAssignableFrom(type)) {
			scannerMap.put(dConfiguration, (LaraBOCollector<A, BO>) value);
		}
		if (LaraBOPreselector.class.isAssignableFrom(type)) {
			checkerMap.put(dConfiguration, (LaraBOPreselector<A, BO>) value);
		}
		if (LaraBOUtilityUpdater.class.isAssignableFrom(type)) {
			adapterMap.put(dConfiguration, (LaraBOUtilityUpdater<A, BO>) value);
		}
		if (LaraPreferenceUpdater.class.isAssignableFrom(type)) {
			prefUpdaterMap.put(dConfiguration,
					(LaraPreferenceUpdater<A, BO>) value);
		}
	}

	/**
	 * @see de.cesr.lara.components.preprocessor.LaraPreprocessorConfigurator#setBOAdapter(de.cesr.lara.components.preprocessor.LaraBOUtilityUpdater)
	 */
	@Override
	public void setBOAdapter(LaraBOUtilityUpdater<A, BO> boAdapter) {
		adapterMap.put(null, boAdapter);
	}

	/**
	 * @see de.cesr.lara.components.preprocessor.LaraPreprocessorConfigurator#setBOAdapter(de.cesr.lara.components.preprocessor.LaraBOUtilityUpdater,
	 *      de.cesr.lara.components.decision.LaraDecisionConfiguration)
	 */
	@Override
	public void setBOAdapter(LaraBOUtilityUpdater<A, BO> boAdapter,
			LaraDecisionConfiguration dConfiguration) {
		adapterMap.put(dConfiguration, boAdapter);
	}

	/**
	 * @see de.cesr.lara.components.preprocessor.LaraPreprocessorConfigurator#setBOCollector(de.cesr.lara.components.preprocessor.LaraBOCollector)
	 */
	@Override
	public void setBOCollector(LaraBOCollector<A, BO> boscanner) {
		scannerMap.put(null, boscanner);
	}

	/**
	 * @see de.cesr.lara.components.preprocessor.LaraPreprocessorConfigurator#setBOCollector(de.cesr.lara.components.preprocessor.LaraBOCollector,
	 *      de.cesr.lara.components.decision.LaraDecisionConfiguration)
	 */
	@Override
	public void setBOCollector(LaraBOCollector<A, BO> boscanner,
			LaraDecisionConfiguration dConfiguration) {
		scannerMap.put(dConfiguration, boscanner);
	}

	/**
	 * @see de.cesr.lara.components.preprocessor.LaraPreprocessorConfigurator#setBoPreselector(de.cesr.lara.components.preprocessor.LaraBOPreselector)
	 */
	@Override
	public void setBoPreselector(LaraBOPreselector<A, BO> boChecker) {
		checkerMap.put(null, boChecker);
	}

	/**
	 * @see de.cesr.lara.components.preprocessor.LaraPreprocessorConfigurator#setBOPreselector(de.cesr.lara.components.preprocessor.LaraBOPreselector,
	 *      de.cesr.lara.components.decision.LaraDecisionConfiguration)
	 */
	@Override
	public void setBOPreselector(LaraBOPreselector<A, BO> boChecker,
			LaraDecisionConfiguration dConfiguration) {
		checkerMap.put(dConfiguration, boChecker);
	}

	/**
	 * @see de.cesr.lara.components.preprocessor.LaraPreprocessorConfigurator#setDecisionModeSelector(de.cesr.lara.components.preprocessor.LaraDecisionModeSelector)
	 */
	@Override
	public void setDecisionModeSelector(
			LaraDecisionModeSelector<A, BO> modeSelector) {
		selectorMap.put(null, modeSelector);
	}

	/**
	 * @see de.cesr.lara.components.preprocessor.LaraPreprocessorConfigurator#setDecisionModeSelector(de.cesr.lara.components.preprocessor.LaraDecisionModeSelector,
	 *      de.cesr.lara.components.decision.LaraDecisionConfiguration)
	 */
	@Override
	public void setDecisionModeSelector(
			LaraDecisionModeSelector<A, BO> modeSelector,
			LaraDecisionConfiguration dConfiguration) {
		selectorMap.put(dConfiguration, modeSelector);
	}

	/**
	 * @see de.cesr.lara.components.preprocessor.LaraPreprocessorConfigurator#setPreferenceUpdater(de.cesr.lara.components.preprocessor.LaraPreferenceUpdater)
	 */
	@Override
	public void setPreferenceUpdater(
			LaraPreferenceUpdater<? extends A, BO> prefUpdater) {
		prefUpdaterMap.put(null, prefUpdater);
	}

	/**
	 * @see de.cesr.lara.components.preprocessor.LaraPreprocessorConfigurator#setPreferenceUpdater(de.cesr.lara.components.preprocessor.LaraPreferenceUpdater,
	 *      de.cesr.lara.components.decision.LaraDecisionConfiguration)
	 */
	@Override
	public void setPreferenceUpdater(
			LaraPreferenceUpdater<? extends A, BO> prefUpdater,
			LaraDecisionConfiguration dConfiguration) {
		prefUpdaterMap.put(dConfiguration, prefUpdater);
	}

}