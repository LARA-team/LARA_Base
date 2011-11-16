/**
 * LARA
 *
 * KUBUS ABM Prototype
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 13.11.2009
 */
package de.cesr.lara.components.preprocessor.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.container.memory.LaraBOMemory;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.preprocessor.LaraBOCollector;

/**
 * 
 * Retrieves all recent behavioural options in memory and checks for each if any
 * utility > 0.0 contributes to the decision represented by this decision
 * builder.
 * 
 * @param <A>
 *            type of agents this BO collector is intended for
 * @param <BO>
 *            type of behavioural options the given BO-memory memorises
 * 
 * @author Sascha Holzhauer
 * @date 13.11.2009
 */
public class LContributingBOPreselector<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<? super A, BO>>
		implements LaraBOCollector<A, BO> {

	/**
	 * Retrieves all recent behavioural options in memory and checks for each if
	 * any utility > 0.0 contributes to the decision represented by this
	 * decision builder. Contributing behavioural options are returned.
	 * 
	 * @see de.cesr.lara.components.preprocessor.LaraBOCollector#collectBOs(de.cesr.lara.components.agents.LaraAgent,
	 *      de.cesr.lara.components.container.memory.LaraBOMemory,
	 *      de.cesr.lara.components.decision.LaraDecisionConfiguration)
	 */
	@Override
	public Collection<BO> collectBOs(A agent, LaraBOMemory<BO> bomemory,
			LaraDecisionConfiguration dConfiguration) {
		Collection<BO> bos = new ArrayList<BO>();
		for (BO bo : bomemory.recallAllMostRecent()) {
			boolean contributes = false;
			for (Entry<Class<? extends LaraPreference>, Double> utility : bo
					.getValue().entrySet()) {
				if (dConfiguration.getPreferences().contains(utility.getKey())) {
					contributes = true;
					break;
				}
			}
			if (contributes) {
				bos.add(bo);
			}
		}
		return bos;
	}
}
