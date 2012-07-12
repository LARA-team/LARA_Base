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

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.preprocessor.LaraBOCollector;
import de.cesr.lara.components.preprocessor.event.LPpBoCollectorEvent;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;

/**
 * 
 * Retrieves all recent behavioural options in memory (does _not_ checks for
 * each if any utility > 0.0 contributes to the decision).
 * 
 * NOTE regarding LOGGING: The agent logger for logging BOs is only enabled when
 * the logger for this class is enabled at least for {@link Priority#INFO}!
 * 
 * @author Sascha Holzhauer
 * @param <A>
 *            the type of agents this BO collector is intended for
 * @param <BO>
 *            the type of behavioural options the given BO-memory memorises
 * @date 23.02.2011
 */
public class LCompleteBoCollector<A extends LaraAgent<? super A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
		extends LAbstractPpComp<A, BO> implements LaraBOCollector<A, BO> {

	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger
			.getLogger(LCompleteBoCollector.class);

	/**
	 * Retrieves all recent behavioural options in memory (does _not_ checks for
	 * each if any utility > 0.0 contributes to the decision).
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

		LPpBoCollectorEvent event = castEvent(LPpBoCollectorEvent.class, e);
		Collection<BO> bos = new ArrayList<BO>();
		@SuppressWarnings("unchecked")
		// the event will only be published by agents of type A
		A agent = (A) event.getAgent();
		for (BO bo : agent.getLaraComp().getBOMemory().recallAllMostRecent()) {
			bos.add(bo);
		}
		// <-- LOGGING
		if (logger.isEnabledFor(Priority.INFO)) {
			logBOs(logger, bos, "after collection", agent);
		}
		// LOGGING -->
		agent.getLaraComp().getDecisionData(event.getdConfig()).setBos(bos);
	}
}
