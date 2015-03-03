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
import de.cesr.lara.components.agents.LaraDecisionModeProvidingAgent;
import de.cesr.lara.components.container.exceptions.LContainerException;
import de.cesr.lara.components.decision.LaraDeciderFactory;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.LaraDecisionModes;
import de.cesr.lara.components.decision.impl.LDeliberativeDeciderFactory;
import de.cesr.lara.components.decision.impl.LExplorationDeciderFactory;
import de.cesr.lara.components.decision.impl.LHabitDeciderFactory;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.eventbus.impl.LEventbus;
import de.cesr.lara.components.model.impl.LModel;
import de.cesr.lara.components.param.LDecisionMakingPa;
import de.cesr.lara.components.postprocessor.impl.LSelectedBoProperty;
import de.cesr.lara.components.preprocessor.LaraDecisionModeSelector;
import de.cesr.lara.components.preprocessor.event.LPpBoCollectorEvent;
import de.cesr.lara.components.preprocessor.event.LPpBoPreselectorEvent;
import de.cesr.lara.components.preprocessor.event.LPpBoUtilityUpdaterEvent;
import de.cesr.lara.components.preprocessor.event.LPpModeSelectorEvent;
import de.cesr.lara.components.preprocessor.event.LPpPreferenceUpdaterEvent;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;
import de.cesr.parma.core.PmParameterManager;


/**
 * 
 * If the agent is an instance of {@link LaraDecisionModeProvidingAgent} this
 * selector respects the suggested decision mode by the agent.
 * 
 * Otherwise, it selects habit mode if last
 * {@link LDecisionMakingPa#HABIT_THRESHOLD} BOs are equal and deliberative mode
 * otherwise.
 * 
 * This default implementation is used in {@link LPreprocessor}.
 * 
 * PP component order:
 * <ol>
 * <li>LPpBoCollectorEvent</li>
 * <li>LPpBoPreselectorEvent</li>
 * <li>LPpBoUtilityUpdaterEvent</li>
 * <li>LPpPreferenceUpdaterEvent</li>
 * </ol>
 * 
 * @author Sascha Holzhauer
 * @param <A>
 *            Agent Type
 * @param <BO>
 *            BO Type
 * 
 */
public class LDefaultDecisionModeSelector<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
		extends LAbstractPpComp<A, BO> implements LaraDecisionModeSelector<A, BO> {

	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger.getLogger(LDefaultDecisionModeSelector.class);

	protected LEventbus eBus;
	protected A agent;
	protected LaraDecisionConfiguration dConfig;

	/**
	 * 
	 * @see de.cesr.lara.components.preprocessor.LaraDecisionModeSelector#onInternalEvent(LaraEvent)
	 */
	@SuppressWarnings("unchecked")
	// agent: the event will only be published by agents of type A
	@Override
	public void onInternalEvent(LaraEvent e) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(this.getClass() + "> processes " + e.getClass());
		}
		// LOGGING ->
		agent = null;

		LPpModeSelectorEvent event = castEvent(LPpModeSelectorEvent.class, e);

		agent = (A) event.getAgent();
		dConfig = event.getdConfig();
		eBus = LEventbus.getInstance(agent);

		LaraDecisionModes mode = this.getDecisionModeSuggestion(event);

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(agent + "> Suggested Decision mode from agent: " + mode);
		}
		// LOGGING ->
	
		if (mode == null) {
			// check for habit
			this.tryHabit();
		} else {
			switch (mode) {

				case DELIBERATIVE:
					doDeliberative();
					break;
				case HABIT:
					tryHabit();
					break;
				case HEURISTICS_EXPLORATION:
					doExplorative();
					break;
				default:
					tryHabit();
			}
		}

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(agent
					+ ">> Time step: "
					+ LModel.getModel().getCurrentStep()
					+ " | habitTH: "
					+ ((Integer) PmParameterManager.getParameter(LDecisionMakingPa.HABIT_THRESHOLD)).intValue()
					+ " | Selected Mode: "
					+ agent.getLaraComp().getDecisionData(dConfig).getDeciderFactory().getDecider(agent, dConfig)
							.getDecisionMode());
		}
		// LOGGING ->
	}

	/**
	 * 
	 */
	protected void tryHabit() {
		// check habits
		int habitTH = ((Integer) PmParameterManager.getParameter(LDecisionMakingPa.HABIT_THRESHOLD)).intValue();
		int currStep = LModel.getModel().getCurrentStep();

		if ((currStep > habitTH && agent.getLaraComp().getGeneralMemory()
				.contains(LSelectedBoProperty.class, dConfig.getId()))
				|| agent.getLaraComp().getGeneralMemory().contains(LSelectedBoProperty.class, dConfig.getId(), 0)) {

			@SuppressWarnings("unchecked")
			// LSelectedBoProperty is parameterized with BO
			BO bo =
					(BO) agent.getLaraComp().getGeneralMemory().recall(LSelectedBoProperty.class, dConfig.getId())
							.getValue();

			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug(agent + ">> Last BO: " + bo);
			}
			// LOGGING ->

			// start from the past...
			for (int i = habitTH; i >= 1; i--) {
				try {
					if (!agent.getLaraComp().getGeneralMemory()
							.recall(LSelectedBoProperty.class, dConfig.getId(), LModel.getModel().getCurrentStep() - i)
							.getValue().getKey().equals(bo.getKey())) {
						doDeliberative();
						break;
					}
				} catch (LContainerException ex) {
					// <- LOGGING
					logger.error(this + "> Could not find " + LSelectedBoProperty.class + " for " + dConfig.getId()
							+ " at time step " + (LModel.getModel().getCurrentStep() - i));
					// LOGGING ->

					throw ex;
				}
			}
			@SuppressWarnings("unchecked")
			// unchecked cast
			LaraDeciderFactory<A, BO> factory = LHabitDeciderFactory.getFactory(agent.getClass());
			agent.getLaraComp().getDecisionData(dConfig).setDeciderFactory(factory);
		} else {
			doDeliberative();
		}
	}

	/**
	 * 
	 */
	protected void doDeliberative() {
		// do deliberative decision making:
		@SuppressWarnings("unchecked")
		// unchecked cast
		LaraDeciderFactory<A, BO> factory = LDeliberativeDeciderFactory.getFactory(agent.getClass());
		agent.getLaraComp().getDecisionData(dConfig).setDeciderFactory(factory);
		eBus.publish(new LPpBoCollectorEvent(agent, dConfig));
		eBus.publish(new LPpBoPreselectorEvent(agent, dConfig));
		eBus.publish(new LPpBoUtilityUpdaterEvent(agent, dConfig));
		eBus.publish(new LPpPreferenceUpdaterEvent(agent, dConfig));
	}

	/**
	 * 
	 */
	protected void doExplorative() {
		@SuppressWarnings("unchecked")
		// unchecked cast
		LaraDeciderFactory<A, BO> factory = LExplorationDeciderFactory.getFactory(agent.getClass());
		agent.getLaraComp().getDecisionData(dConfig).setDeciderFactory(factory);
	}

	/**
	 * Hook method for custom implementations to decide about deliberative decision mode. As a default, this method
	 * delegates the decision to the agent.
	 * 
	 * @return true if deliberative decision is desired
	 */
	protected LaraDecisionModes getDecisionModeSuggestion(LPpModeSelectorEvent event) {
		@SuppressWarnings("unchecked")
		A agent = (A) event.getAgent();

		// extend the class and place your custom code here
		if (agent instanceof LaraDecisionModeProvidingAgent) {
			return ((LaraDecisionModeProvidingAgent) agent).getDecisionModeSuggestion();
		}
		return null;
	}
}
