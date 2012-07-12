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
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.preprocessor.LaraBOUtilityUpdater;
import de.cesr.lara.components.preprocessor.event.LPpBoUtilityUpdaterEvent;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;

/**
 * Calls
 * {@link LaraBehaviouralOption#getSituationalUtilities(LaraDecisionConfiguration)}
 * 
 * NOTE regarding LOGGING: The agent logger for logging BOs is only enabled when
 * the logger for this class is enabled at least for {@link Priority#INFO}!
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
public class LDefaultBOUpdater<A extends LaraAgent<? super A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
		extends LAbstractPpComp<A, BO> implements LaraBOUtilityUpdater<A, BO> {

	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger
			.getLogger(LDefaultBOUpdater.class);

	/**
	 * @see de.cesr.lara.components.eventbus.LaraInternalEventSubscriber#onInternalEvent(de.cesr.lara.components.eventbus.events.LaraEvent)
	 */
	@Override
	public void onInternalEvent(LaraEvent e) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(this.getClass() + "> processes " + e.getClass());
		}
		// LOGGING ->

		LPpBoUtilityUpdaterEvent event = castEvent(
				LPpBoUtilityUpdaterEvent.class, e);

		@SuppressWarnings("unchecked")
		// the event will only be published by agents of type A
		A agent = (A) event.getAgent();
		Collection<BO> updatedBos = new ArrayList<BO>();
		Collection<BO> bos = agent.getLaraComp()
				.getDecisionData(event.getdConfig()).getBos();
		for (BO bo : bos) {
			updatedBos.add(bo.getModifiedUtilitiesBO(bo
					.getSituationalUtilities(event.getdConfig())));
		}
		// <-- LOGGING
		if (logger.isEnabledFor(Priority.INFO)) {
			logBOs(logger, bos, "after utility updating", agent);
		}
		// LOGGING -->
		// TODO which BOs to set? set updated or the non updated?
		// TODO what are situational preferences?!
		agent.getLaraComp().getDecisionData(event.getdConfig())
				.setBos(updatedBos);
		// agent.getLaraComp().getDecisionData(event.getdConfig()).setBos(bos);
	}
}
