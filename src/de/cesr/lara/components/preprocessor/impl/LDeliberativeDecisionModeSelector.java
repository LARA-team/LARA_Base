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


import org.apache.log4j.Logger;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDeciderFactory;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.impl.LDeliberativeDeciderFactory;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.eventbus.impl.LEventbus;
import de.cesr.lara.components.preprocessor.LaraDecisionModeSelector;
import de.cesr.lara.components.preprocessor.event.LPpBoCollectorEvent;
import de.cesr.lara.components.preprocessor.event.LPpBoPreselectorEvent;
import de.cesr.lara.components.preprocessor.event.LPpBoUtilityUpdaterEvent;
import de.cesr.lara.components.preprocessor.event.LPpModeSelectorEvent;
import de.cesr.lara.components.preprocessor.event.LPpPreferenceUpdaterEvent;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;


/**
 * Constantly selects {@link LDeliberativeDeciderFactory} as decider factory.
 * 
 * @param <A>
 *            the type of agents this preprocessor is intended for
 * @param <BO>
 *            type of behavioural options
 * 
 * @author Sascha Holzhauer
 * @date 10.07.2009
 */
public class LDeliberativeDecisionModeSelector<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
		extends LAbstractPpComp<A, BO> implements
		LaraDecisionModeSelector<A, BO> {

	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger
			.getLogger(LDeliberativeDecisionModeSelector.class);

	/**
	 * Always selects the matrix deliberation mode (
	 * {@link LDeliberativeDeciderFactory}. This default implementation is used
	 * in {@link LPreprocessor}.
	 * 
	 * PP component order:
	 * <ol>
	 * <li>LPpBoCollectorEvent</li>
	 * <li>LPpBoPreselectorEvent</li>
	 * <li>LPpBoUtilityUpdaterEvent</li>
	 * <li>LPpPreferenceUpdaterEvent</li>
	 * </ol>
	 * 
	 * @see de.cesr.lara.components.preprocessor.LaraDecisionModeSelector#onInternalEvent(de.cesr.lara.components.preprocessor.event.LPpModeSelectorEvent)
	 */
	@Override
	public void onInternalEvent(LaraEvent e) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(this.getClass() + "> processes " + e.getClass());
		}
		// LOGGING ->

		LPpModeSelectorEvent event = castEvent(LPpModeSelectorEvent.class, e);
		@SuppressWarnings("unchecked")
		// the event will only be published by agents of type A
		A agent = (A) event.getAgent();
		LaraDecisionConfiguration dConfig = event.getdConfig();
		LEventbus eBus = LEventbus.getInstance(agent);

		@SuppressWarnings("unchecked")
		// unchecked cast
		LaraDeciderFactory<A, BO> factory = LDeliberativeDeciderFactory
				.getFactory(agent.getClass());
		agent.getLaraComp().getDecisionData(dConfig).setDeciderFactory(factory);

		eBus.publish(new LPpBoCollectorEvent(agent, dConfig));
		eBus.publish(new LPpBoPreselectorEvent(agent, dConfig));
		eBus.publish(new LPpBoUtilityUpdaterEvent(agent, dConfig));
		eBus.publish(new LPpPreferenceUpdaterEvent(agent, dConfig));
	}
}