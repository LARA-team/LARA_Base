/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 23.02.2011
 */
package de.cesr.lara.components.impl;

import java.util.Map;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.util.impl.LPreferenceWeightMap;

/**
 * Default implementation of a {@link LaraBehaviouralOption} that assigns a
 * serial id and provides several constructors. NOTE: This implementation does
 * not alter any utility values!
 * 
 * NOTE: When constructor calls with and without id are mixed, the counter only
 * increases when non-id-parameter-constructors are called!
 * 
 * @author Sascha Holzhauer
 * @param <A>
 * @date 18.12.2009
 * 
 */
public class LGeneralBehaviouralOption<A extends LaraAgent<A, LGeneralBehaviouralOption<A>>>
		extends LaraBehaviouralOption<A, LGeneralBehaviouralOption<A>> {

	/**
	 * Counter to assign serial ids
	 */
	private static int counter = 0;

	/**
	 * @param agent
	 * @param utilities
	 */
	public LGeneralBehaviouralOption(A agent,
			Map<Class<? extends LaraPreference>, Double> utilities) {
		super(new Integer(counter++).toString(), agent, utilities);
	}

	/**
	 * @param key
	 * @param agent
	 */
	public LGeneralBehaviouralOption(String key, A agent) {
		super(key, agent, new LPreferenceWeightMap());
	}

	/**
	 * @param key
	 * @param agent
	 * @param utilities
	 * 
	 */
	public LGeneralBehaviouralOption(String key, A agent,
			Map<Class<? extends LaraPreference>, Double> utilities) {
		super(key, agent, utilities);
	}

	/**
	 * @see de.cesr.lara.components.LaraBehaviouralOption#getModifiedBO(de.cesr.lara.components.agents.LaraAgent,
	 *      java.util.Map)
	 */
	@Override
	public LGeneralBehaviouralOption<A> getModifiedBO(A agent,
			Map<Class<? extends LaraPreference>, Double> utilities) {
		return new LGeneralBehaviouralOption<A>(this.getKey(), agent, utilities);
	}

	/**
	 * @see de.cesr.lara.components.LaraBehaviouralOption#getSituationalUtilities(de.cesr.lara.components.decision.LaraDecisionConfiguration)
	 */
	@Override
	public Map<Class<? extends LaraPreference>, Double> getSituationalUtilities(
			LaraDecisionConfiguration<LGeneralBehaviouralOption<A>> dBuilder) {
		return getValue();
	}
}
