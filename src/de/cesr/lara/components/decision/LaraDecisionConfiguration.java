package de.cesr.lara.components.decision;

import java.util.Collection;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;

/**
 * used to get an instance of LaraDecision fitting a given set of
 * preferenceWeights and an heuristic TODO: Name? Maybe replace Builder by
 * something less technical
 * 
 * @param <A>
 *            type of agent
 * @param <BO>
 *            type of behavioural options
 */
public interface LaraDecisionConfiguration<BO extends LaraBehaviouralOption<?, BO>> {

	/**
	 * @return heuristic
	 */
	public LaraDeliberativeChoiceComponent<BO> getHeuristic();

	/**
	 * @return the id
	 */
	public String getId();

	/**
	 * @return preferenceWeights
	 * 
	 */
	public Collection<Class<? extends LaraPreference>> getPreferences();

	/**
	 * @param heuristic
	 */
	public void setHeuristic(LaraDeliberativeChoiceComponent<BO> heuristic);

	/**
	 * @param preferenceWeights
	 */
	public void setPreferences(
			Collection<Class<? extends LaraPreference>> preferences);

}
