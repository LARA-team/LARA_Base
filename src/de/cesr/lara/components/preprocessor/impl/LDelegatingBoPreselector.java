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
import de.cesr.lara.components.preprocessor.LaraBOPreselector;
import de.cesr.lara.components.preprocessor.event.LPpBoPreselectorEvent;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;

/**
 * Delegates checking to each behavioural option.
 * 
 * NOTE regarding LOGGING: The agent logger for logging BOs is only enabled when
 * the logger for this class is enabled at least for {@link Priority#INFO}!
 * 
 * @param <A>
 *            type of agents this BO preselector is intended for
 * @param <BO>
 *            type of behavioural options that are checked
 * 
 * @author Sascha Holzhauer
 * @date 13.11.2009
 */
public class LDelegatingBoPreselector<A extends LaraAgent<? super A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
		extends LAbstractPpComp<A, BO> implements LaraBOPreselector<A, BO> {

	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger
			.getLogger(LDelegatingBoPreselector.class);

	/**
	 * Delegates checking to each behavioural option.
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

		LPpBoPreselectorEvent event = castEvent(LPpBoPreselectorEvent.class, e);
		@SuppressWarnings("unchecked")
		// the event will only be published by agents of type A
		A agent = (A) event.getAgent();
		Collection<BO> bos = new ArrayList<BO>();
		for (BO bo : agent.getLaraComp().getDecisionData(event.getdConfig())
				.getBos()) {
			if (bo.isCurrentlyApplicable()) {
				bos.add(bo);
			}
		}
		// <-- LOGGING
		if (bos.size() == 0) {
			logger.warn("No BOs after pre-selection for agent " + agent + "!");
		}
		if (logger.isEnabledFor(Priority.INFO)) {
			logBOs(logger, bos, "after preselection", agent);
		}
		// LOGGING -->

		agent.getLaraComp().getDecisionData(event.getdConfig()).setBos(bos);
	}
}
