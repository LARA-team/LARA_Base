package de.cesr.lara.components.decision;


import java.util.Collection;

import de.cesr.lara.components.LaraPreference;


/**
 * Used to get an instance of LaraDecision fitting a given set of preferenceWeights and an heuristic.
 * 
 * TODO rename get/SetHeuristic to get/SetChoiceComponent (SH)
 * 
 */
public interface LaraDecisionConfiguration {

	/**
	 * @param preferenceWeights
	 */
	public void setPreferences(Collection<Class<? extends LaraPreference>> preferences);

	/**
	 * @return preferenceWeights
	 */
	public Collection<Class<? extends LaraPreference>> getPreferences();

	/**
	 * @return the id
	 */
	public String getId();

}
