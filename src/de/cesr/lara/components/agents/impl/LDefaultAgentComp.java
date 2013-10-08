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
package de.cesr.lara.components.agents.impl;


import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.LaraProperty;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.agents.LaraAgentComponent;
import de.cesr.lara.components.container.memory.LaraBOMemory;
import de.cesr.lara.components.container.memory.LaraMemory;
import de.cesr.lara.components.container.memory.impl.LDefaultLimitedCapacityBOMemory;
import de.cesr.lara.components.container.memory.impl.LDefaultLimitedCapacityMemory;
import de.cesr.lara.components.decision.LaraDecider;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.LaraDecisionData;
import de.cesr.lara.components.decision.LaraDeliberativeChoiceComponent;
import de.cesr.lara.components.decision.impl.LDeliberativeChoiceComp_MaxLineTotalRandomAtTie;
import de.cesr.lara.components.environment.LaraEnvironment;
import de.cesr.lara.components.environment.impl.LAbstractEnvironmentalProperty;
import de.cesr.lara.components.eventbus.events.LAgentDecideEvent;
import de.cesr.lara.components.eventbus.events.LAgentPerceptionEvent;
import de.cesr.lara.components.eventbus.events.LAgentPostExecutionEvent;
import de.cesr.lara.components.eventbus.events.LAgentPostprocessEvent;
import de.cesr.lara.components.eventbus.events.LAgentPreprocessEvent;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.eventbus.impl.LEventbus;
import de.cesr.lara.components.param.LDecisionMakingPa;
import de.cesr.lara.components.postprocessor.LaraPostprocessorComp;
import de.cesr.lara.components.postprocessor.impl.LDefaultPostProcessorComp;
import de.cesr.lara.components.preprocessor.LaraBOPreselector;
import de.cesr.lara.components.preprocessor.LaraPreprocessor;
import de.cesr.lara.components.preprocessor.impl.LPreprocessorConfigurator;
import de.cesr.lara.components.util.impl.LCapacityManagers;
import de.cesr.lara.components.util.impl.LPrefEntry;
import de.cesr.lara.components.util.impl.LPreferenceWeightMap;
import de.cesr.lara.components.util.logging.impl.LAgentLevel;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;
import de.cesr.parma.core.PmParameterManager;


/**
 * The default implementation of {@link LaraAgentComponent}.
 * 
 * @param <A>
 *        type of agent
 * @param <BO>
 *        the type of behavioural options memorised in the BO memory The type of agent this agent component belongs to
 * 
 * @author Sascha Holzhauer
 * @date 17.12.2009
 */
public class LDefaultAgentComp<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
		implements LaraAgentComponent<A, BO> {

	// //
	// Static Members
	// //

	/**
	 * Deliberative choice components according to {@link LaraDecisionConfiguration}. Contains default for key NULL.
	 */
	protected static Map<LaraDecisionConfiguration, LaraDeliberativeChoiceComponent> defaultDeliberativeChoiceComponents =
			null;

	/**
	 * Init deliberative choice components.
	 */
	static {
		defaultDeliberativeChoiceComponents = new HashMap<LaraDecisionConfiguration, LaraDeliberativeChoiceComponent>();
		defaultDeliberativeChoiceComponents
				.put(null, LDeliberativeChoiceComp_MaxLineTotalRandomAtTie.getInstance(null));
	}

	// //
	// Static Methods
	// //

	/**
	 * @param dConfiguration
	 * @return deliberative choice component
	 */
	static public LaraDeliberativeChoiceComponent getDefaultDeliberativeChoiceComp(
			LaraDecisionConfiguration dConfiguration) {
		return defaultDeliberativeChoiceComponents.get(dConfiguration);
	}

	/**
	 * @param dConfiguration
	 * @param comp
	 */
	static public void setDefaultDeliberativeChoiceComp(LaraDecisionConfiguration dConfiguration,
			LaraDeliberativeChoiceComponent comp) {
		defaultDeliberativeChoiceComponents.put(dConfiguration, comp);
	}

	// //
	// Instance Fields
	// //

	/**
	 * the agent the component belongs to
	 */
	protected A agent;

	protected LEventbus eventBus = LEventbus.getInstance();

	/**
	 * memory for behavioural options
	 */
	protected LaraBOMemory<BO> boMemory;

	/**
	 * the agents decision module
	 */
	protected Map<LaraDecisionConfiguration, LaraDecisionData<A, BO>> decisionData = null;

	/**
	 * Deliberative choice components according to {@link LaraDecisionConfiguration}. Contains default for key NULL.
	 */
	protected Map<LaraDecisionConfiguration, LaraDeliberativeChoiceComponent> deliberativeChoiceCompents = null;

	/**
	 * environment
	 */
	protected LaraEnvironment environment;


	/**
	 * a collection of the agents behavioural options
	 */
	protected LaraMemory<LaraProperty<?, ?>> memory = null;

	protected LaraPostprocessorComp<A, BO> postProcessorComp;

	/**
	 * a collection of the agents preferenceWeights
	 */
	protected Collection<Class<? extends LaraPreference>> preferences = null;

	/**
	 * a collection of the agents preferenceWeights towards preferenceWeights
	 */
	protected Map<Class<? extends LaraPreference>, Double> preferenceWeights = null;

	/**
	 * Since each agent may have different strategies and modes of action selection, each agent is assigned an instance
	 * of {@link LaraPreprocessor} . (SH)
	 */
	protected LaraPreprocessor<A, BO> preprocessor;

	/**
	 * accuracy
	 */
	protected LaraBOPreselector.Accuracy preselectingBOaccuracy = LaraBOPreselector.LAccuracy.ACCURATE;

	private Logger agentLogger = null;

	private final Logger logger = Log4jLogger.getLogger(LDefaultAgentComp.class);

	private final Logger loggerDecision = Log4jLogger.getLogger(LDefaultAgentComp.class.getName() + ".decision");


	/**
	 * Simplest Constructor (e.g. social environment, geographical environment)
	 * 
	 * @param agent
	 * @param env
	 */
	public LDefaultAgentComp(A agent, LaraEnvironment env) {

		// init agent specific logger (agent id is first part of logger name):
		if (Log4jLogger.getLogger(agent.getAgentId() + "." + LDefaultAgentComp.class.getName()).isEnabledFor(
				LAgentLevel.AGENT)) {
			agentLogger = Log4jLogger.getLogger(agent.getAgentId() + "." + LDefaultAgentComp.class.getName());
		}

		this.agent = agent;
		this.environment = env;

		memory =
				new LDefaultLimitedCapacityMemory<LaraProperty<?, ?>>(LCapacityManagers.<LaraProperty<?, ?>> makeFIFO());

		checkMemoryCapacityForHabitSelection();

		boMemory = new LDefaultLimitedCapacityBOMemory<BO>(LCapacityManagers.<BO> makeFIFO());
		preferenceWeights = new LPreferenceWeightMap();
		decisionData = new HashMap<LaraDecisionConfiguration, LaraDecisionData<A, BO>>();
		deliberativeChoiceCompents = new HashMap<LaraDecisionConfiguration, LaraDeliberativeChoiceComponent>();

		this.postProcessorComp = new LDefaultPostProcessorComp<A, BO>();

		eventBus.subscribe(this, LAgentPerceptionEvent.class);
		eventBus.subscribe(this, LAgentPreprocessEvent.class);
		eventBus.subscribe(this, LAgentDecideEvent.class);
		eventBus.subscribe(this, LAgentPostprocessEvent.class);
		eventBus.subscribe(this, LAgentPostExecutionEvent.class);
	}

	// //
	// Instance Methods
	// //

	@Override
	public void addPreferenceWeights(LPrefEntry... prefEntry) {
		if (this.preferenceWeights == null) {
			this.preferenceWeights = new HashMap<Class<? extends LaraPreference>, Double>();
		}
		for (LPrefEntry e : prefEntry) {
			this.preferenceWeights.put(e.getKey(), e.getValue());
		}

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(agent + "> Preferences: " + this.preferenceWeights);
		}
		// LOGGING ->
	}

	/**
	 * @see de.cesr.lara.components.agents.LaraAgentComponent#addPreferenceWeights(java.util.Map)
	 */
	@Override
	public void addPreferenceWeights(Map<Class<? extends LaraPreference>, Double> preferenceWeights) {
		if (this.preferenceWeights == null) {
			this.preferenceWeights = new HashMap<Class<? extends LaraPreference>, Double>();
		}
		this.preferenceWeights.putAll(preferenceWeights);

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(agent + "> Preferences: " + this.preferenceWeights);
		}
		// LOGGING ->
	}

	/**
	 * @see de.cesr.lara.components.agents.LaraAgentComponent#decide(de.cesr.lara.components.decision.LaraDecisionConfiguration)
	 */
	@Override
	public void decide(LaraDecisionConfiguration decisionConfig) {
		LaraDecider<BO> decider = this.getDecisionData(decisionConfig).getDecider();

		if (Log4jLogger.getLogger(agent.getAgentId() + "." + LDefaultAgentComp.class.getName()).isEnabledFor(
				LAgentLevel.AGENT)) {
			agentLogger.info(this.agent + "> decide() (" + decider + ")");
		}

		decider.decide();
	}

	/**
	 * @see de.cesr.lara.components.environment.LaraEnvironmentListener#envPropertyChanged(de.cesr.lara.components.environment.impl.LAbstractEnvironmentalProperty)
	 */
	@Override
	public void envPropertyChanged(LAbstractEnvironmentalProperty<?> envProperty) {
		logger.info("Agent " + agent.getAgentId() + " received environmental property change event: "
				+ envProperty.getKey() + ": " + envProperty.getValue());
	}

	/**
	 * 
	 * {@link LAgentPreprocessEvent}: If no {@link LaraPreprocessor} was set, the default preprocessor is set now.
	 * However, it is recommended to use a global configurator before.
	 * 
	 * @param <T>
	 * @param event
	 */
	@Override
	public void onInternalEvent(LaraEvent event) {

		if (event instanceof LAgentPreprocessEvent) {
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug(this.agent + "> preprocess DB "
						+ ((LAgentPreprocessEvent) event).getDecisionConfiguration().getId());
				logger.debug("(Preprocessor Builder: " + this.preprocessor + ")");
			}
			// LOGGING ->

			if (this.preprocessor == null) {
				logger.warn(this.agent + "> The preprocessor has not been set! The default is used.");
				setPreprocessor(LPreprocessorConfigurator.<A, BO> getNewPreprocessorConfigurator().getPreprocessor());
			}
			preprocessor.preprocess(((LAgentPreprocessEvent) event).getDecisionConfiguration(), agent);

		} else if (event instanceof LAgentDecideEvent) {
			if (((LAgentDecideEvent) event).getDecisionConfiguration() == null) {
				// <- LOGGING
				logger.error("Decision configuration in event " + event + " of agent " + this.agent + " not defiend");
				// LOGGING ->

				throw new IllegalStateException("Decision configuration in event " + event + " of agent " + this.agent
						+ " not defiend");
			}
			decide(((LAgentDecideEvent) event).getDecisionConfiguration());

		} else if (event instanceof LAgentPostprocessEvent) {
			postProcessorComp.postProcess(agent, ((LAgentPostprocessEvent) event).getDecisionConfiguration());
			logDecision(((LAgentPostprocessEvent) event).getDecisionConfiguration());

		} else if (event instanceof LAgentPostExecutionEvent) {
			removeDecisionData(((LAgentPostExecutionEvent) event).getDecisionConfiguration());
		}
	}

	/**
	 * @see de.cesr.lara.components.agents.LaraAgentComponent#removeDecisionData(de.cesr.lara.components.decision.LaraDecisionConfiguration)
	 */
	@Override
	public void removeDecisionData(LaraDecisionConfiguration dConfiguration) {
		if (logger.isDebugEnabled()) {
			logger.debug("LaraDecisionData for " + agent + " and " + dConfiguration + " removed");
		}
		decisionData.remove(dConfiguration);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getClass().getName() + " (" + this.agent + ")";
	}

	/**
	 * Log decision made
	 * 
	 * @param dConfig
	 */
	private void logDecision(LaraDecisionConfiguration dConfig) {
		if (loggerDecision.isInfoEnabled()) {
			loggerDecision.info("Selected BO: " + getDecisionData(dConfig).getDecider().getSelectedBo());
		}
	}

	private void checkMemoryCapacityForHabitSelection() {
		if (this.memory.getCapacity() < ((Integer) PmParameterManager.getParameter(LDecisionMakingPa.HABIT_THRESHOLD))
				.intValue()) {
			logger.warn(agent + "> LDecisionMakingPa.HABIT_THRESHOLD exceeds this agent's memory capacity!");
		}
	}

	// //
	// Instance GETTER and SETTER
	// //

	/**
	 * @see de.cesr.lara.components.agents.LaraAgentComponent#getGeneralMemory()
	 */
	@Override
	public LaraBOMemory<BO> getBOMemory() {
		return boMemory;
	}

	@Override
	public LaraDecisionData<A, BO> getDecisionData(LaraDecisionConfiguration dConfiguration) {
		if (!decisionData.containsKey(dConfiguration)) {
			decisionData.put(dConfiguration, new LaraDecisionData<A, BO>(dConfiguration, agent));

			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("LaraDecisionData for " + agent + " and " + dConfiguration + " initiated");
				// LOGGING ->
			}

		}
		return decisionData.get(dConfiguration);
	}

	/**
	 * @see de.cesr.lara.components.agents.LaraAgentComponent#getDecisionDataIterable()
	 */
	@Override
	public Iterable<LaraDecisionData<A, BO>> getDecisionDataIterable() {
		return decisionData.values();
	}

	/**
	 * @see de.cesr.lara.components.agents.LaraAgentComponent#getDeliberativeChoiceComp(de.cesr.lara.components.decision.LaraDecisionConfiguration)
	 */
	@Override
	public LaraDeliberativeChoiceComponent getDeliberativeChoiceComp(LaraDecisionConfiguration dConfiguration) {
		if (deliberativeChoiceCompents.containsKey(dConfiguration)) {
			return deliberativeChoiceCompents.get(dConfiguration);
		} else if (defaultDeliberativeChoiceComponents.containsKey(dConfiguration)) {
			return defaultDeliberativeChoiceComponents.get(dConfiguration);
		} else if (deliberativeChoiceCompents.containsKey(null)) {
			return deliberativeChoiceCompents.get(null);
		} else {
			return defaultDeliberativeChoiceComponents.get(null);
		}
	}

	/**
	 * @see de.cesr.lara.components.agents.LaraAgentComponent#getEnvironment()
	 */
	@Override
	public LaraEnvironment getEnvironment() {
		return environment;
	}

	/**
	 * @see de.cesr.lara.components.agents.LaraAgentComponent#getGeneralMemory()
	 */
	@Override
	public LaraMemory<LaraProperty<?, ?>> getGeneralMemory() {
		return memory;
	}

	/**
	 * @see de.cesr.lara.components.agents.LaraAgentComponent#getNumDecisionDataObjects()
	 */
	@Override
	public int getNumDecisionDataObjects() {
		return decisionData.size();
	}

	@Override
	public Double getPreferenceWeight(Class<? extends LaraPreference> preference) {
		return preferenceWeights.get(preference);
	}

	/**
	 * @see de.cesr.lara.components.agents.LaraAgentComponent#getPreferenceWeights()
	 */
	@Override
	public Map<Class<? extends LaraPreference>, Double> getPreferenceWeights() {
		return new LinkedHashMap<Class<? extends LaraPreference>, Double>(preferenceWeights);
	}

	/**
	 * @see de.cesr.lara.components.agents.LaraAgentComponent#setBOMemory(de.cesr.lara.components.container.memory.LaraBOMemory)
	 */
	@Override
	public void setBOMemory(LaraBOMemory<BO> boMemory) {
		this.boMemory = boMemory;
	}

	/**
	 * @see de.cesr.lara.components.agents.LaraAgentComponent#setDeliberativeChoiceComp(de.cesr.lara.components.decision.LaraDecisionConfiguration,
	 *      de.cesr.lara.components.decision.LaraDeliberativeChoiceComponent)
	 */
	@Override
	public void setDeliberativeChoiceComp(LaraDecisionConfiguration dConfiguration, LaraDeliberativeChoiceComponent comp) {
		deliberativeChoiceCompents.put(dConfiguration, comp);
	}

	/**
	 * @see de.cesr.lara.components.agents.LaraAgentComponent#setGeneralMemory(LaraMemory)
	 */
	@Override
	public void setGeneralMemory(LaraMemory<LaraProperty<?, ?>> memory) {
		this.memory = memory;
	}

	/**
	 * @see de.cesr.lara.components.agents.LaraAgentComponent#setPostProcessor(de.cesr.lara.components.postprocessor.LaraPostprocessorComp)
	 */
	@Override
	public void setPostProcessor(LaraPostprocessorComp<A, BO> postprocesor) {
		this.postProcessorComp = postprocesor;
	}

	/**
	 * 
	 * @see de.cesr.lara.components.agents.LaraAgentComponent#setPreprocessor(de.cesr.lara.components.preprocessor.LaraPreprocessor)
	 */
	@Override
	public void setPreprocessor(LaraPreprocessor<A, BO> preprocessor) {
		this.preprocessor = preprocessor;
	}

}