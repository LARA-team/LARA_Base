/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * 
 */
package de.cesr.lara.components;


import java.text.NumberFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.model.impl.LModel;
import de.cesr.lara.components.util.impl.LPreferenceWeightMap;
import de.cesr.lara.components.util.logging.impl.LAgentLevel;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;


/**
 * {@link LaraBehaviouralOption} declares behavioural options an agent may choose from to perform. The agent type
 * parameter allows the user to access agent members without casting, for instance to preselect the behavioural option
 * with respect to agent properties.
 * 
 * Instances of this class are only modifiable by invoking the instance methods get<modification>BO(<modified object>).
 * The load of the necessity to implement such methods for _every_ subclass is unavoidable. Otherwise, the user receives
 * an instance of the next superclass that implements that method (an exception may be thrown to prevent such a
 * behaviour)!
 * 
 * Subclasses may not provide means to alter their fields. Otherwise, the consistency of these fields across agents is
 * not guaranteed (e.g. hash codes are computed at initialisation)!
 * 
 * BO also work with agents that require (only) a super type of their (the BO's) own class. See <a
 * href="../../../../../implementation_concept.html#behaviouraloptions_types">Type parameters</a>
 * 
 * @author Sascha Holzhauer
 * @param <A>
 *        the agent class that may deal with these BOs
 * @param <BO>
 *        the behavioural option type
 * 
 * @date 12.02.2010
 * 
 * TODO is there any reason for ...extends LaraAgent<A,)>>...?
 */
public abstract class LaraBehaviouralOption<A extends LaraAgent<? super A, ?>, BO extends LaraBehaviouralOption<?,? extends BO>>
		extends LaraProperty<Map<Class<? extends LaraPreference>, Double>> implements
		Comparable<LaraBehaviouralOption<A, BO>> {

	/**
	 * Logger
	 */
	static private Logger									logger		= Log4jLogger
																				.getLogger(LaraBehaviouralOption.class);
	// init agent specific logger (agent id is first part of logger name):
	Logger													agentLogger	= null;

	/**
	 * The agent this BO belongs to.
	 */
	private final A											agent;
	private int												hashCode;

	/**
	 * The BO's collection of utility values
	 */
	private Map<Class<? extends LaraPreference>, Double>	preferenceUtilities;

	/**
	 * constructor
	 * 
	 * @param key
	 * @param agent
	 * @param preferenceUtilities
	 */
	public LaraBehaviouralOption(String key, A agent, Map<Class<? extends LaraPreference>, Double> preferenceUtilities) {
		super(key);
		this.agent = agent;
		this.preferenceUtilities = new LPreferenceWeightMap(preferenceUtilities);
		this.hashCode = calculateHashCode();
		if (Log4jLogger.getLogger(agent.getAgentId() + "." + LaraBehaviouralOption.class.getName()).isEnabledFor(
				LAgentLevel.AGENT)) {
			agentLogger = Log4jLogger.getLogger(agent.getAgentId() + "." + LaraBehaviouralOption.class.getName());
		}

	}

	/**
	 * Does not call constructor with more parameters above to prevent double initialisation of preferenceUtilities
	 * 
	 * @param key
	 * @param agent
	 */
	public LaraBehaviouralOption(String key, A agent) {
		super(key);
		this.agent = agent;
		this.preferenceUtilities = new LPreferenceWeightMap();
		this.hashCode = calculateHashCode();
		if (Log4jLogger.getLogger(agent.getAgentId() + "." + LaraBehaviouralOption.class.getName()).isEnabledFor(
				LAgentLevel.AGENT)) {
			agentLogger = Log4jLogger.getLogger(agent.getAgentId() + "." + LaraBehaviouralOption.class.getName());
		}
	}

	/**
	 * calculate hash code:
	 * 
	 * @return the hash code Created by Sascha Holzhauer on 25.02.2011
	 */
	protected int calculateHashCode() {
		int result = 17;
		result = 31 * result + (getAgent() == null ? 0 : agent.hashCode());
		result = 31 * result + (getKey() == null ? 0 : getKey().hashCode());
		result = 31 * result + getTimestamp();
		int sum = 0;
		if (getValue() != null) {
			for (Entry<Class<? extends LaraPreference>, Double> entry : getValue().entrySet()) {
				// a multiplication is not possible since it would depend on the order!
				sum += entry.getKey().getName().hashCode() + entry.getValue().hashCode();
			}
		}
		result = 31 * result + sum;
		return result;
	}

	// ////////////////////////////////
	// ABSTRACT METHODS
	// ////////////////////////////////

	/**
	 * @param dBuilder
	 * @return the current preferenceUtilities of this behavioural option
	 */
	public abstract Map<Class<? extends LaraPreference>, Double> getSituationalUtilities(
			LaraDecisionConfiguration dBuilder);

	/**
	 * @param agent
	 * @param preferenceUtilities
	 * @return behavioural option Created by klemm on 16.02.2010
	 */
	public abstract BO getModifiedBO(A agent, Map<Class<? extends LaraPreference>, Double> preferenceUtilities);

	// ////////////////////////////////
	// GETTER
	// ////////////////////////////////

	/**
	 * Returns the agent this BO belongs to
	 * 
	 * @return the agent this BO belongs to
	 */
	public A getAgent() {
		return agent;
	}

	/**
	 * Returns an unmodifiable(!) map Therefore, in order to edit utility values, the user needs to make a deep copy,
	 * edit and return it.
	 * 
	 */
	@Override
	public Map<Class<? extends LaraPreference>, Double> getValue() {
		return Collections.unmodifiableMap(preferenceUtilities);
	}

	/**
	 * Returns a copy of the internal utility map that may be edited.
	 * 
	 * @return an editable copy of the internal utility map
	 */
	public Map<Class<? extends LaraPreference>, Double> getModifiableUtilities() {
		return new LPreferenceWeightMap(preferenceUtilities);
	}

	/**
	 * Returns the sum of BO-utility * agent'S situational preference over all preferenceWeights. Sorts preferenceWeights only when agent
	 * debugging is enabled.
	 * 
	 * TODO check name clash in subclasses <-> precise LaraDecisionConfiguration object
	 * @param dBuilder
	 *        the {@link LaraDecisionConfiguration} the situational preferenceWeights apply to
	 * 
	 * @return sum of situational preferenceUtilities over all preferenceWeights.
	 */
	public float getTotalSituationalUtility(LaraDecisionConfiguration dBuilder) {
		// float overall_utility = 0.0f;
		double overall_utility = 0.0;

		// float situationalGoalPreference;
		double situationalGoalPreference;

		Map<Class<? extends LaraPreference>, Double> goals;

		// <- LOGGING
		if (agentLogger != null) {
			goals = new TreeMap<Class<? extends LaraPreference>, Double>(
					new Comparator<Class<? extends LaraPreference>>() {
						@Override
						public int compare(Class<? extends LaraPreference> arg0, Class<? extends LaraPreference> arg1) {
							return arg0.getName().compareTo(arg1.getName());
						}
					});
			goals.putAll(this.getValue());
		} else {
			goals = this.getValue();
		}
		// LOGGING ->

		for (Entry<Class<? extends LaraPreference>, Double> utilityEntry : goals.entrySet()) {
			situationalGoalPreference = agent.getLaraComp().getDecisionData(dBuilder).getSituationalPreferenceWeights().get(
					utilityEntry.getKey()).doubleValue();

			// security:
			if (utilityEntry.getValue().isNaN()) {
				logger.warn(agent + "> BO " + this + " contained NaN for goal " + utilityEntry.getKey());
			}
			// only count the goal if its utility is not NaN:
			else {
				if (agentLogger != null) {
					agentLogger.debug(agent + "> Sit. preference for bo " + this.getKey() + "; goal "
							+ utilityEntry.getKey() + ": " + situationalGoalPreference
							* utilityEntry.getValue().doubleValue());
				}
				overall_utility += situationalGoalPreference * utilityEntry.getValue().doubleValue();
			}
		}
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(agent + "> Overall-Utility: " + overall_utility);
		}
		// LOGGING ->
		return (float) overall_utility;
	}

	// ////////////////////////////////
	// METHODS TO RETURN MODIFIED BOs
	// ////////////////////////////////

	/**
	 * @see de.cesr.lara.components.LaraProperty#getModifiedProperty(java.lang.Object)
	 */
	public BO getModifiedProperty(Map<Class<? extends LaraPreference>, Double> value) {
		return getModifiedUtilitiesBO(value);
	}

	/**
	 * @param agent
	 * @return a new BO with modified agent
	 */
	public BO getModifiedAgentBO(A agent) {
		return getModifiedBO(agent, getValue());
	}

	/**
	 * @param preferenceUtilities
	 *        map of preferenceWeights and values
	 * @return new BO that contains the given preferenceUtilities
	 */
	public BO getModifiedUtilitiesBO(Map<Class<? extends LaraPreference>, Double> preferenceUtilities) {
		return getModifiedBO(getAgent(), preferenceUtilities);
	}

	// ////////////////////////////////
	// STANDARD METHODS
	// ////////////////////////////////

	/**
	 * TODO make appropriate!
	 * 
	 * @param bo1
	 * @return a negative int if the given BO is larger, a positive int if the given BO is smaller and 0 otherwise
	 */
	public int compareTo(LaraBehaviouralOption<A, BO> bo1) {
		return getKey().compareTo(bo1.getKey());
	}

	/**
	 * NOTE: Subclasses that add fields need to redefine the method appropriately according to the contract of equals()
	 * of {@link Object}.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof LaraBehaviouralOption<?,?>) {
			LaraBehaviouralOption<?,?> bo = (LaraBehaviouralOption<?,?>) o;
			if (getAgent() != bo.getAgent()) {
				return false;
			}
			if (getKey() != bo.getKey()) {
				return false;
			}
			if (getTimestamp() != bo.getTimestamp()) {
				return false;
			}
			// TODO check advantages/disadvantages of comparing actual values for getValue()
			if (!getValue().equals(bo.getValue())) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	/**
	 * @see de.cesr.lara.components.LaraProperty#hashCode()
	 */
	@Override
	public int hashCode() {
		return hashCode;
	}

	/**
	 * The returned String representation has the format [<key>, <Goal>:<Value>, <...>, TS: <time step>] The order of
	 * preferenceWeights is sorted alphabetically.
	 * 
	 * @see de.cesr.lara.components.LaraProperty#toString()
	 */
	@Override
	public String toString() {
		NumberFormat format = LModel.getModel().getFloatPointFormat();
		StringBuffer buffer = new StringBuffer();
		buffer.append("[" + getKey() + ", ");
		TreeSet<Map.Entry<Class<? extends LaraPreference>, Double>> sortedSet = new TreeSet<Map.Entry<Class<? extends LaraPreference>, Double>>(
				new Comparator<Map.Entry<Class<? extends LaraPreference>, Double>>() {
					@Override
					public int compare(Entry<Class<? extends LaraPreference>, Double> o1,
							Entry<Class<? extends LaraPreference>, Double> o2) {
						return o1.getKey().getSimpleName().compareTo(o2.getKey().getSimpleName());
					}
				});
		sortedSet.addAll(preferenceUtilities.entrySet());
		for (Map.Entry<Class<? extends LaraPreference>, Double> entry : sortedSet) {
			buffer.append(entry.getKey().getSimpleName() + ": " + format.format(entry.getValue().doubleValue()) + "; ");
		}
		buffer.append("TS: " + getTimestamp() + "]");
		return buffer.toString();
	}
}
