/**
 * LARA - Lightweight Architecture for bounded Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 17.12.2009
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
import de.cesr.lara.components.environment.LaraEnvironment;
import de.cesr.lara.components.environment.impl.LAbstractEnvironmentalProperty;
import de.cesr.lara.components.eventbus.LaraInternalEventSubscriber;
import de.cesr.lara.components.eventbus.events.LAgentDecideEvent;
import de.cesr.lara.components.eventbus.events.LAgentExecutionEvent;
import de.cesr.lara.components.eventbus.events.LAgentPerceptionEvent;
import de.cesr.lara.components.eventbus.events.LAgentPostprocessEvent;
import de.cesr.lara.components.eventbus.events.LAgentPreprocessEvent;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.eventbus.impl.LEventbus;
import de.cesr.lara.components.preprocessor.LaraBOPreselector;
import de.cesr.lara.components.preprocessor.LaraPreprocessor;
import de.cesr.lara.components.preprocessor.LaraPreprocessorFactory;
import de.cesr.lara.components.preprocessor.impl.LPreprocessorConfigurator;
import de.cesr.lara.components.util.impl.LCapacityManagers;
import de.cesr.lara.components.util.impl.LPreferenceWeightMap;
import de.cesr.lara.components.util.logging.impl.LAgentLevel;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;

/**
 * 
 * @author Sascha Holzhauer
 * @param <A>
 *            type of agent
 * @param <BO>
 *            the type of behavioural options memorised in the BO memory The
 *            type of agent this agent component belongs to
 * @date 17.12.2009
 */
public class LDefaultAgentComp<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<? super A, BO>>
		implements LaraAgentComponent<A, BO>, LaraInternalEventSubscriber {

	private Logger agentLogger = null;
	private Logger logger = Log4jLogger.getLogger(LDefaultAgentComp.class);

	private LEventbus eventBus;

	/**
	 * the agent the component belongs to
	 */
	protected A agent;

	/**
	 * memory for behavioural options
	 */
	protected LaraBOMemory<BO> boMemory;

	/**
	 * the agents decision module
	 */
	protected Map<LaraDecisionConfiguration, LaraDecisionData<A, BO>> decisionData = null;

	/**
	 * 
	 * DODOC describe this feature properties
	 */
	protected Map<String, Double> doubleProperties;

	// TODO enable multiple environments!
	/**
	 * environment
	 */
	protected LaraEnvironment environment;

	/**
	 * a collection of the agents behavioural options
	 */
	protected LaraMemory<LaraProperty<?>> memory = null;

	/**
	 * a collection of the agents preferenceWeights
	 */
	protected Collection<Class<? extends LaraPreference>> preferences = null;

	/**
	 * a collection of the agents preferenceWeights towards preferenceWeights
	 */
	protected Map<Class<? extends LaraPreference>, Double> preferenceWeights = null;

	/**
	 * Since each agent may have different strategies and modes of action
	 * selection, each agent is assigned an instance of {@link LaraPreprocessor}
	 * . (SH)
	 */
	protected LaraPreprocessorFactory<A, BO> preprocessorFactory;

	// memory property?!
	/**
	 * accuracy
	 */
	protected LaraBOPreselector.Accuracy preselectingBOaccuracy = LaraBOPreselector.LAccuracy.ACCURATE;

	/**
	 * Simplest Constructor TODO: Agent should have one or more environments!
	 * (e.g. social environment, geographical environment)
	 * 
	 * @param agent
	 * @param env
	 */
	public LDefaultAgentComp(A agent, LaraEnvironment env) {
		eventBus = LEventbus.getInstance();

		// init agent specific logger (agent id is first part of logger name):

		if (Log4jLogger.getLogger(
				agent.getAgentId() + "." + LDefaultAgentComp.class.getName())
				.isEnabledFor(LAgentLevel.AGENT)) {
			agentLogger = Log4jLogger.getLogger(agent.getAgentId() + "."
					+ LDefaultAgentComp.class.getName());
		}

		this.agent = agent;
		this.environment = env;
		logger.info("Assign preprocessor to agent");
		memory = new LDefaultLimitedCapacityMemory<LaraProperty<?>>(
				LCapacityManagers.<LaraProperty<?>> makeNINO());
		boMemory = new LDefaultLimitedCapacityBOMemory<BO>(
				LCapacityManagers.<BO> makeNINO());
		doubleProperties = new HashMap<String, Double>();
		preferenceWeights = new LPreferenceWeightMap();
		decisionData = new HashMap<LaraDecisionConfiguration, LaraDecisionData<A, BO>>();
		eventBus.subscribe(this, LAgentPerceptionEvent.class);
		eventBus.subscribe(this, LAgentPreprocessEvent.class);
		eventBus.subscribe(this, LAgentDecideEvent.class);
		eventBus.subscribe(this, LAgentPostprocessEvent.class);
		eventBus.subscribe(this, LAgentExecutionEvent.class);
	}

	/**
	 * @see de.cesr.lara.components.agents.LaraAgentComponent#addPreferenceWeights(java.util.Map)
	 */
	@Override
	public void addPreferenceWeights(
			Map<Class<? extends LaraPreference>, Double> preferenceWeights) {
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
		LaraDecider<BO> decider = this.getDecisionData(decisionConfig)
				.getDecider();

		logger.info(this.agent + "> decide() (" + decider + ")");
		if (Log4jLogger.getLogger(
				agent.getAgentId() + "." + LDefaultAgentComp.class.getName())
				.isEnabledFor(LAgentLevel.AGENT)) {
			agentLogger.info(this.agent + "> decide() (" + decider + ")");
		}
		
		decider.decide();
	}

	/**
	 * @see de.cesr.lara.components.environment.LaraEnvironmentListener#envPropertyChanged(de.cesr.lara.components.environment.impl.LAbstractEnvironmentalProperty)
	 */
	@Override
	public void envPropertyChanged(LAbstractEnvironmentalProperty<?> envProperty) {
		logger.info("Agent " + agent.getAgentId()
				+ " received environmental property change event: "
				+ envProperty.getKey() + ": " + envProperty.getValue());
	}

	/**
	 * @see de.cesr.lara.components.agents.LaraAgentComponent#getGeneralMemory()
	 */
	@Override
	public LaraBOMemory<BO> getBOMemory() {
		return boMemory;
	}

	@Override
	public LaraDecisionData<A, BO> getDecisionData(
			LaraDecisionConfiguration dConfiguration) {
		if (!decisionData.containsKey(dConfiguration)) {
			decisionData.put(dConfiguration, new LaraDecisionData<A, BO>(
					dConfiguration, agent));

			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("LaraDecisionData for " + agent + " and "
						+ dConfiguration + " initiated");
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

	@Override
	public double getDoubleProperty(String name) {
		Double value = doubleProperties.get(name);
		return value == null ? Double.NaN : value.doubleValue();
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
	public LaraMemory<LaraProperty<?>> getGeneralMemory() {
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
		// TODO check performance...
		return new LinkedHashMap<Class<? extends LaraPreference>, Double>(
				preferenceWeights);
	}

	/**
	 * If no {@link LaraPreprocessorFactory} was set, it is set now. However,
	 * since in this case for every agent a separate configurator needs to be
	 * initialised it is highly recommended to use a global configurator before.
	 * 
	 * @see de.cesr.lara.components.agents.LaraAgentComponent#preProcess(de.cesr.lara.components.decision.LaraDecisionConfiguration)
	 */
	@Override
	public void preProcess(LaraDecisionConfiguration dConfiguration) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(this.agent + "> preprocess DB "
					+ dConfiguration.getId());
			logger.debug("Preprocessor Builder: " + this.preprocessorFactory);
		}
		// LOGGING ->

		if (this.preprocessorFactory == null) {
			setPreProcessorFactory(LPreprocessorConfigurator
					.<A, BO> getDefaultPreprocessConfigurator()
					.getPreprocessorFactory());
		}
		preprocessorFactory.getPreprocessor(dConfiguration).preprocess(
				preselectingBOaccuracy, agent);
	}

	/**
	 * @see de.cesr.lara.components.agents.LaraAgentComponent#removeDecisionData(de.cesr.lara.components.decision.LaraDecisionConfiguration)
	 */
	@Override
	public void removeDecisionData(LaraDecisionConfiguration dConfiguration) {
		if (logger.isDebugEnabled()) {
			logger.debug("LaraDecisionData for " + agent + " and "
					+ dConfiguration + " removed");
		}
		decisionData.remove(dConfiguration);
	}

	/**
	 * @see de.cesr.lara.components.agents.LaraAgentComponent#setBOMemory(de.cesr.lara.components.container.memory.LaraBOMemory)
	 */
	@Override
	public void setBOMemory(LaraBOMemory<BO> boMemory) {
		this.boMemory = boMemory;
	}

	@Override
	public void setDoubleProperty(String name, double value) {
		doubleProperties.put(name, value);
	}

	/**
	 * @see de.cesr.lara.components.agents.LaraAgentComponent#setGeneralMemory(LaraMemory)
	 */
	@Override
	public void setGeneralMemory(LaraMemory<LaraProperty<?>> memory) {
		this.memory = memory;
	}

	/**
	 * 
	 * @see de.cesr.lara.components.agents.LaraAgentComponent#setPreProcessorFactory(de.cesr.lara.components.preprocessor.LaraPreprocessorFactory)
	 */
	@Override
	public void setPreProcessorFactory(
			LaraPreprocessorFactory<A, BO> preprocessorFactory) {
		this.preprocessorFactory = preprocessorFactory;
	}

	/**
	 * 
	 * @param <T>
	 * @param event
	 */
	@Override
	public <T extends LaraEvent> void onInternalEvent(T event) {

		if (event instanceof LAgentPreprocessEvent) {
			preProcess(((LAgentPreprocessEvent) event)
					.getDecisionConfiguration());
		} else if (event instanceof LAgentDecideEvent) {
			decide(((LAgentDecideEvent) event).getDecisionConfiguration());
		} else if (event instanceof LAgentPostprocessEvent) {
			// TODO do something
		} else if (event instanceof LAgentExecutionEvent) {
			removeDecisionData(((LAgentExecutionEvent) event)
					.getDecisionConfiguration());
		}

	}
}
