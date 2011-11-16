package de.cesr.lara.components.decision.impl;

import java.util.Collection;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.LaraDeliberativeChoiceComponent;

/**
 * creates instance of decision for given set of preferenceWeights and heuristic
 * TODO make immutable (SH) TODO consider id field to make the builder
 * identifiable - to be set in constructor like other fields (SH) sorry - needed
 * these features instantly (SH) > not possible to make static since multiple
 * instances with different goal-definitions are required! (SH)
 * 
 * @param <A>
 *            type of agent
 * @param <BO>
 *            type of behavioural option
 */
public class LDecisionConfiguration<BO extends LaraBehaviouralOption<?, BO>>
		implements LaraDecisionConfiguration<BO> {

	/**
	 * The default id string
	 */
	public final String DEFAULT_ID = "default";

	private String id;

	/**
	 * heuristic
	 */
	protected LaraDeliberativeChoiceComponent<BO> heuristic;

	/**
	 * preferenceWeights XXX: why not store hash map (goal, value) of
	 * preferenceWeights instead of preferenceWeights? cause preferenceWeights
	 * differ from agent to agent, but preferenceWeights are reusable
	 */
	protected Collection<Class<? extends LaraPreference>> preferences = null;

	/**
	 * Assigns the default id String.
	 */
	public LDecisionConfiguration() {
		this.id = DEFAULT_ID;
	}

	/**
	 * @param id
	 */
	/**
	 * @param id
	 * @param heuristic
	 *            the mechanism used to fetch best BOs.
	 */
	public LDecisionConfiguration(String id,
			LaraDeliberativeChoiceComponent<BO> heuristic) {
		this.id = id;
		this.heuristic = heuristic;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDecisionConfiguration#getHeuristic()
	 */
	@Override
	public LaraDeliberativeChoiceComponent<BO> getHeuristic() {
		return this.heuristic;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDecisionConfiguration#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	@Override
	public Collection<Class<? extends LaraPreference>> getPreferences() {
		return preferences;
	}

	@Override
	public void setHeuristic(LaraDeliberativeChoiceComponent<BO> heuristic) {
		this.heuristic = heuristic;
	}

	@Override
	public void setPreferences(
			Collection<Class<? extends LaraPreference>> preferences) {
		this.preferences = preferences;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LaraDecisionConfiguration " + id;
	}
}
