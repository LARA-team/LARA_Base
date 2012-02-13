package de.cesr.lara.components.decision.impl;


import java.util.Collection;

import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;


/**
 * creates instance of decision for given set of preferenceWeights and deliberativeChoiceComp TODO make immutable (SH) TODO consider id
 * field to make the builder identifiable - to be set in constructor like other fields (SH) sorry - needed these
 * features instantly (SH) > not possible to make static since multiple instances with different goal-definitions are
 * required! (SH)
 * 
 */
public class LDecisionConfiguration implements LaraDecisionConfiguration {

	/**
	 * preferenceWeights XXX: why not store hash map (goal, value) of preferenceWeights instead of preferenceWeights? cause
	 * preferenceWeights differ from agent to agent, but preferenceWeights are reusable
	 */
	protected Collection<Class<? extends LaraPreference>>	preferences	= null;

	private String											id;

	/**
	 * The default id string
	 */
	public final String										DEFAULT_ID	= "default";

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
	 * @param deliberativeChoiceComp
	 *        the mechanism used to fetch best BOs.
	 */
	public LDecisionConfiguration(String id) {
		this.id = id;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDecisionConfiguration#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setPreferences(Collection<Class<? extends LaraPreference>> preferences) {
		this.preferences = preferences;
	}


	@Override
	public Collection<Class<? extends LaraPreference>> getPreferences() {
		return preferences;
	}


	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LaraDecisionConfiguration " + id;
	}
}
