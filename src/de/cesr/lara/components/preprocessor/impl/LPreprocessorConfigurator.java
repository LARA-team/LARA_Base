/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 01.03.2011
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
import de.cesr.lara.components.preprocessor.LaraPreprocessorComp;
import de.cesr.lara.components.preprocessor.LaraPreprocessorConfiguration;


/**
 * default configurator
 * 
 * @param <A>
 *        the type of agents the according preprocessor builder is intended for
 * @param <BO>
 *        the type of behavioural options the preprocessor shall manage
 */
public class LPreprocessorConfigurator<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
		implements LaraPreprocessorConfiguration<A, BO> {

	private Map<LaraDecisionConfiguration, LaraDecisionModeSelector<A, BO>>					selectorMap;
	private Map<LaraDecisionConfiguration, LaraBOCollector<A, ? extends BO>>		scannerMap;
	private Map<LaraDecisionConfiguration, LaraBOPreselector<A, ? extends BO>>	checkerMap;
	private Map<LaraDecisionConfiguration, LaraBOUtilityUpdaterBuilder<A, BO>>	adapterMap;
	private Map<LaraDecisionConfiguration, LaraPreferenceUpdater<? extends A>>	prefUpdaterMap;

	private LaraPreprocessorFactory<A, BO>									preprocessorBuilder;
	/**
	 * 
	 */
	public final LaraDecisionModeSelector<A, BO>										DEFAULT_DECISION_MODE_SELECTOR	= new LDeliberativeDecisionModeSelector<A, BO>();
	/**
	 * 
	 */
	public final LaraBOCollector<A, BO>										DEFAULT_BO_COLLECTOR			= new LAllBOCollector<A, BO>();
	/**
	 * 
	 */
	public final LaraBOPreselector<A, BO>									DEFAULT_BO_PRESELECTOR			= new LPseudoBOPreselector<A, BO>();
	/**
	 * 
	 */
	public final LaraBOUtilityUpdaterBuilder<A, BO>							DEFAULT_BO_UPDATE_BUILDER		= new LDefaultLBOUpdaterBuilder<A, BO>();
	/**
	 * 
	 */
	public final LaraPreferenceUpdater<A>									DEFAULT_PREFERENCE_UPDATER		= new LPseudoPrefereceUpdater<A>();

	/**
	 * constructor
	 */
	private LPreprocessorConfigurator() {
		selectorMap = new HashMap<LaraDecisionConfiguration, LaraDecisionModeSelector<A, BO>>();
		scannerMap = new HashMap<LaraDecisionConfiguration, LaraBOCollector<A, ? extends BO>>();
		checkerMap = new HashMap<LaraDecisionConfiguration, LaraBOPreselector<A, ? extends BO>>();
		adapterMap = new HashMap<LaraDecisionConfiguration, LaraBOUtilityUpdaterBuilder<A, BO>>();
		prefUpdaterMap = new HashMap<LaraDecisionConfiguration, LaraPreferenceUpdater<? extends A>>();
	}

	@Override
	public void setBOAdapter(LaraBOUtilityUpdaterBuilder<A, BO> boAdapter, LaraDecisionConfiguration dConfiguration) {
		adapterMap.put(dConfiguration, boAdapter);
	}

	@Override
	public void setBOAdapter(LaraBOUtilityUpdaterBuilder<A, BO> boAdapter) {
		adapterMap.put(null, boAdapter);
	}

	@Override
	public void setBOPreselector(LaraBOPreselector<A, BO> boChecker, LaraDecisionConfiguration dConfiguration) {
		checkerMap.put(dConfiguration, boChecker);
	}

	@Override
	public void setBOChecker(LaraBOPreselector<A, BO> boChecker) {
		checkerMap.put(null, boChecker);
	}

	@Override
	public void setBOCollector(LaraBOCollector<A, BO> boscanner, LaraDecisionConfiguration dConfiguration) {
		scannerMap.put(dConfiguration, boscanner);
	}

	@Override
	public void setBOCollector(LaraBOCollector<A, BO> boscanner) {
		scannerMap.put(null, boscanner);
	}

	@Override
	public void setDecisionModeSelector(LaraDecisionModeSelector<A, BO> modeSelector, LaraDecisionConfiguration dConfiguration) {
		selectorMap.put(dConfiguration, modeSelector);
	}

	@Override
	public void setDecisionModeSelector(LaraDecisionModeSelector<A, BO> modeSelector) {
		selectorMap.put(null, modeSelector);
	}

	@Override
	public void setPreferenceUpdater(LaraPreferenceUpdater<? extends A> prefUpdater, LaraDecisionConfiguration dConfiguration) {
		prefUpdaterMap.put(dConfiguration, prefUpdater);
	}

	@Override
	public void setPreferenceUpdater(LaraPreferenceUpdater<? extends A> prefUpdater) {
		prefUpdaterMap.put(null, prefUpdater);
	}

	/**
	 * TODO make type save!
	 * 
	 * @param <T>
	 *        the component type regarding agent type of the requested component
	 * @param <U>
	 *        the general type of the requested component
	 * @param dConfiguration
	 *        The {@link LaraDecisionConfiguration} the given component shall be applied to.
	 * @param type
	 *        the general type of the requested component
	 * @param value
	 * 
	 */
	@SuppressWarnings("unchecked")
	public <T extends LaraPreprocessorComp<A>, U extends LaraPreprocessorComp<?>> void set(
			LaraDecisionConfiguration dConfiguration, Class<U> type, T value) {
		if (LaraDecisionModeSelector.class.isAssignableFrom(type)) {
			selectorMap.put(dConfiguration, (LaraDecisionModeSelector<A, BO>) value);
		}
		if (LaraBOCollector.class.isAssignableFrom(type)) {
			scannerMap.put(dConfiguration, (LaraBOCollector<A, BO>) value);
		}
		if (LaraBOPreselector.class.isAssignableFrom(type)) {
			checkerMap.put(dConfiguration, (LaraBOPreselector<A, BO>) value);
		}
		if (LaraBOUtilityUpdaterBuilder.class.isAssignableFrom(type)) {
			adapterMap.put(dConfiguration, (LaraBOUtilityUpdaterBuilder<A, BO>) value);
		}
		if (LaraPreferenceUpdater.class.isAssignableFrom(type)) {
			prefUpdaterMap.put(dConfiguration, (LaraPreferenceUpdater<A>) value);
		}
	}

	/**
	 * @see de.cesr.lara.components.preprocessor.LaraPreprocessorConfiguration#get(de.cesr.lara.components.decision.LaraDecisionConfiguration,
	 *      java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(LaraDecisionConfiguration dConfiguration, Class<T> type) {
		if (LaraDecisionModeSelector.class.isAssignableFrom(type)) {
			return (T) selectorMap.get(dConfiguration);
		}
		if (LaraBOCollector.class.isAssignableFrom(type)) {
			return (T) scannerMap.get(dConfiguration);
		}
		if (LaraBOPreselector.class.isAssignableFrom(type)) {
			return (T) checkerMap.get(dConfiguration);
		}
		if (LaraBOUtilityUpdaterBuilder.class.isAssignableFrom(type)) {
			return (T) adapterMap.get(dConfiguration);
		}
		if (LaraPreferenceUpdater.class.isAssignableFrom(type)) {
			return (T) prefUpdaterMap.get(dConfiguration);
		}
		new AssertionError("Missing case in DefaultConfigurator#get()");
		return null;
	}

	/**
	 * @see de.cesr.lara.components.preprocessor.LaraPreprocessorConfiguration#getMap(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends LaraPreprocessorComp<A>, U extends LaraPreprocessorComp<?>> Map<LaraDecisionConfiguration, T> getMap(
			Class<U> type) {
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
			if (LaraBOUtilityUpdaterBuilder.class.isAssignableFrom(type)) {
				return (Map<LaraDecisionConfiguration, T>) adapterMap;
			}
			if (LaraPreferenceUpdater.class.isAssignableFrom(type)) {
				return (Map<LaraDecisionConfiguration, T>) prefUpdaterMap;
			}
			new AssertionError("Missing case in DefaultConfigurator#getMap()");
			return null;
		} catch (ClassCastException e) {
			throw new ClassCastException("Agent parameter type did not match requested one!");
		}
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	public LaraPreprocessorConfiguration<A, BO> clone() {
		LPreprocessorConfigurator<A, BO> clone = new LPreprocessorConfigurator<A, BO>();
		clone.selectorMap.putAll(this.selectorMap);
		clone.adapterMap.putAll(this.adapterMap);
		clone.checkerMap.putAll(this.checkerMap);
		clone.prefUpdaterMap.putAll(this.prefUpdaterMap);
		clone.scannerMap.putAll(this.scannerMap);
		return clone;
	}

	/**
	 * @see de.cesr.lara.components.preprocessor.LaraPreprocessorConfiguration#getPreprocessorFactory()
	 */
	public LaraPreprocessorFactory<A, BO> getPreprocessorFactory() {
		try {
			if (this.preprocessorBuilder == null || !this.preprocessorBuilder.meetsConfiguration(this)) {
				this.preprocessorBuilder = new LPreprocessFactory<A, BO>(this);
			}
			return this.preprocessorBuilder;
		} catch (ClassCastException e) {
			throw new ClassCastException("The stored builder has not the requested agent type!");
		}
	}

	/**
	 * @param <A>
	 * @param <BO>
	 * @return the default configurator
	 */
	public static <A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>> LPreprocessorConfigurator<A, BO> getDefaultPreprocessConfigurator() {
		return new LPreprocessorConfigurator<A, BO>();
	}
}