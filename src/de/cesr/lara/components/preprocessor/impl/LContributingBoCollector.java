/**
 * This file is part of
 * 
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 * 
 * Copyright (C) 2012 Center for Environmental Systems Research, Kassel, Germany
 * 
 * LARA is free software: You can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * LARA is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.cesr.lara.components.preprocessor.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.preprocessor.LaraBOCollector;
import de.cesr.lara.components.preprocessor.event.LPpBoCollectorEvent;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;

/**
 * 
 * Retrieves all recent behavioural options in memory and checks for each if any
 * utility contributes to the decision represented by this decision builder.
 * 
 * NOTE regarding LOGGING: The agent logger for logging BOs is only enabled when
 * the logger for this class is enabled at least for {@link Priority#INFO}!
 * 
 * @param <A>
 *            type of agents this BO collector is intended for
 * @param <BO>
 *            type of behavioural options the given BO-memory memorises
 * 
 * @author Sascha Holzhauer
 * @date 13.11.2009
 */
public class LContributingBoCollector<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, BO>>
		extends LAbstractPpComp<A, BO> implements LaraBOCollector<A, BO> {

	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger
			.getLogger(LContributingBoCollector.class);

	/**
	 * Retrieves all recent behavioural options in memory and checks for each if
	 * any utility contributes to the decision represented by this decision
	 * builder even if it is 0.0 or below. Contributing behavioural options are
	 * returned. See {@link LOmitZeroContributingBOCollector}.
	 * 
	 * @see de.cesr.lara.components.preprocessor.LaraBOCollector#onInternalEvent(de.cesr.lara.components.preprocessor.event.LPpBoCollectorEvent)
	 */
	@Override
	public void onInternalEvent(LaraEvent e) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(this.getClass() + "> processes " + e.getClass());
		}
		// LOGGING ->
		LPpBoCollectorEvent event = castEvent(LPpBoCollectorEvent.class, e);

		Collection<BO> bos = new ArrayList<BO>();
		@SuppressWarnings("unchecked")
		// the event will only be published by agents of type A
		A agent = ((A) event.getAgent());

		for (BO bo : agent.getLaraComp().getBOMemory().recallAllMostRecent()) {
			boolean contributes = false;

			// <- LOGGING
			if (event.getdConfig().getPreferences().isEmpty()) {
				logger.warn("Decision " + event.getdConfig() + " does not define any preference. Consequently,"
						+ "no BO will contribute to it!");
			}
			// LOGGING ->

			for (Entry<Class<? extends LaraPreference>, Double> utility : bo
					.getValue().entrySet()) {
				if (event.getdConfig().getPreferences()
						.contains(utility.getKey())) {
					contributes = true;
					break;
				}
			}
			if (contributes) {
				bos.add(bo);
			}
		}
		// <-- LOGGING
		if (bos.size() == 0) {
			logger.warn("No BOs have been collected for agent " + agent + "!");
		}
		if (logger.isEnabledFor(Priority.INFO)) {
			logBOs(logger, bos, "after collection", agent);
		}
		// LOGGING -->
		agent.getLaraComp().getDecisionData(event.getdConfig()).setBos(bos);
	}
}
