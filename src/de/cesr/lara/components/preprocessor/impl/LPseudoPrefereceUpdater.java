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


import java.util.Map.Entry;

import org.apache.log4j.Logger;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecisionData;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.preprocessor.LaraPreferenceUpdater;
import de.cesr.lara.components.preprocessor.event.LPpPreferenceUpdaterEvent;
import de.cesr.lara.components.util.logging.impl.LAgentLevel;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;


/**
 * Does _not_ update any preference.
 * 
 * @param <A>
 *        he type of agents this preference utilityUpdater is intended for
 * 
 * @author Sascha Holzhauer
 * @date 05.02.2010
 * 
 */
public class LPseudoPrefereceUpdater<A extends LaraAgent<? super A, ?>, BO extends LaraBehaviouralOption<?, ?>> extends
		LAbstractPpComp<A, BO> implements LaraPreferenceUpdater<A, BO> {

	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger.getLogger(LPseudoPrefereceUpdater.class);

	/**
	 * Does _not_ update any preference (only sets agent's basic preferenceWeights at the {@link LaraDecisionData}
	 * object).
	 * 
	 * @see de.cesr.lara.components.eventbus.LaraInternalEventSubscriber#onInternalEvent(de.cesr.lara.components.eventbus.events.LaraEvent)
	 */
	@Override
	public void onInternalEvent(LaraEvent e) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(this.getClass() + "> processes " + e.getClass());
		}
		// LOGGING ->

		LPpPreferenceUpdaterEvent event = castEvent(LPpPreferenceUpdaterEvent.class, e);
		@SuppressWarnings("unchecked")
		// the event will only be published by agents of type A
		A agent = ((A) event.getAgent());
		agent.getLaraComp().getDecisionData(event.getdConfig())
				.setIndividualPreferences(agent.getLaraComp().getPreferenceWeights());

		// <- LOGGING
		// initialise agent specific logger (agent id is first part of logger
		// name):
		Logger agentLogger = null;
		if (Log4jLogger.getLogger(agent.getAgentId() + "." + LPreprocessor.class.getName()).isEnabledFor(
				LAgentLevel.AGENT)) {
			agentLogger = Log4jLogger.getLogger(agent.getAgentId() + "." + LPreprocessor.class.getName());
		}

		StringBuffer buffer = new StringBuffer();
		buffer.append("Preferences for " + agent + ":" + System.getProperty("line.separator"));
		int i = 0;
		for (Entry<Class<? extends LaraPreference>, Double> entry : agent.getLaraComp().getPreferenceWeights()
				.entrySet()) {
			buffer.append(entry.getKey() + ": " + entry.getValue() + System.getProperty("line.separator"));
			i++;
		}
		if (agentLogger != null) {
			agentLogger.debug(buffer);
		} else if (logger.isDebugEnabled()) {
			logger.debug(buffer);
		}
		// LOGGING ->

	}
}
