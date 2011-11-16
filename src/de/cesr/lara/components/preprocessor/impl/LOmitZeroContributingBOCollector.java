/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 17.02.2010
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
 * This LaraBoScanner implementation also filters out behavioural option that
 * indeed define one or more preferenceWeights also defined in the
 * {@link LaraDecisionConfiguration} but whose utility is not above zero. This
 * might be critical for some models since the utility may increase during
 * updating but might be useful for others.
 * 
 * @param <A>
 * @param <BO>
 *            the type of behavioural options the given BO-memory memorises
 */
public class LOmitZeroContributingBOCollector<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<? super A, BO>>
		implements LaraBOCollector<A, BO> {

	/**
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
				if (utility.getValue().doubleValue() > 0.0
						&& dConfiguration.getPreferences().contains(
								utility.getKey())) {
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
