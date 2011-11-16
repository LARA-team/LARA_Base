/**
 * LARA
 *
 * KUBUS ABM Prototype
 * Center for Environmental Systems Research, Kassel
 * 
 * Created by Sascha Holzhauer on 23.02.2011
 */
package de.cesr.lara.components.preprocessor.impl;

import java.util.ArrayList;
import java.util.Collection;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.container.memory.LaraBOMemory;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.preprocessor.LaraBOCollector;

/**
 * 
 * Retrieves all recent behavioural options in memory (does _not_ checks for
 * each if any utility > 0.0 contributes to the decision).
 * 
 * @author Sascha Holzhauer
 * @param <A>
 *            the type of agents this BO collector is intended for
 * @param <BO>
 *            the type of behavioural options the given BO-memory memorises
 * @date 23.02.2011
 */
public class LAllBOCollector<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<? super A, BO>>
		implements LaraBOCollector<A, BO> {

	/**
	 * Retrieves all recent behavioural options in memory (does _not_ checks for
	 * each if any utility > 0.0 contributes to the decision).
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
			bos.add(bo);
		}
		return bos;
	}
}
