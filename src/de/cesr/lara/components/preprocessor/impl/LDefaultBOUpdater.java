/**
 * LARA - Lightweight Architecture for bounded Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 05.02.2010
 */
package de.cesr.lara.components.preprocessor.impl;

import java.util.ArrayList;
import java.util.Collection;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.preprocessor.LaraBOUtilityUpdater;

/**
 * Calls
 * {@link LaraBehaviouralOption#getSituationalUtilities(LaraDecisionConfiguration)}
 * 
 * @param <A>
 *            the type of agents this BO utilityUpdater is intended for
 * @param <BO>
 *            the type of behavioural options that are updated
 * 
 * @author Sascha Holzhauer
 * @date 05.02.2010
 * 
 */
public class LDefaultBOUpdater<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<? super A, BO>>
		implements LaraBOUtilityUpdater<A, BO> {

	/**
	 * @see de.cesr.lara.components.preprocessor.LaraBOUtilityUpdater#updateBOUtilities(de.cesr.lara.components.agents.LaraAgent,
	 *      java.util.Collection,
	 *      de.cesr.lara.components.decision.LaraDecisionConfiguration)
	 */
	@Override
	public Collection<BO> updateBOUtilities(A agent, Collection<BO> bos,
			LaraDecisionConfiguration<BO> dConfiguration) {
		Collection<BO> updatedBos = new ArrayList<BO>();
		for (BO bo : bos) {
			updatedBos.add(bo.getModifiedUtilitiesBO(bo
					.getSituationalUtilities(dConfiguration)));
		}
		return updatedBos;
	}
}
