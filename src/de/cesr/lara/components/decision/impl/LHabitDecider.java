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
package de.cesr.lara.components.decision.impl;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecider;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.LaraDecisionModes;
import de.cesr.lara.components.postprocessor.impl.LSelectedBoProperty;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;


/**
 * 
 * @param <A>
 *        type of agent
 * @param <BO>
 *        type of behavioural option
 * 
 */
public class LHabitDecider<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>> implements
		LaraDecider<BO> {

	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger.getLogger(LHabitDecider.class);

	A agent = null;
	List<BO> bos = new ArrayList<>();
	LaraDecisionConfiguration dConfiguration;

	/**
	 * @param agent
	 * @param dConfiguration
	 */
	public LHabitDecider(A agent, LaraDecisionConfiguration dConfiguration) {
		this.agent = agent;
		this.dConfiguration = dConfiguration;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDecider#decide()
	 */
	@SuppressWarnings("unchecked")
	// LSelectedBoProperty is associated with the agent and parameterized with
	// BO
	@Override
	public void decide() {
		bos.clear();

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(agent + ">> Memory: " + agent.getLaraComp().getGeneralMemory().toString());
		}
		// LOGGING ->

		if (agent.getLaraComp().getGeneralMemory().contains(dConfiguration.getId())) {
			bos.add((BO) agent.getLaraComp().getGeneralMemory()
					.recall(LSelectedBoProperty.class, dConfiguration.getId()).getValue());
		} else {
			// <- LOGGING
			logger.warn("Habitual behaviour could not select a behavioural option since the "
					+ "(general) memory does not contain information about previously selected BO!");
			// LOGGING ->
		}
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDecider#getNumSelectableBOs()
	 */
	@Override
	public int getNumSelectableBOs() {
		return 1;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDecider#getSelectedBo()
	 */
	@Override
	public BO getSelectedBo() {
		return bos.get(0);
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDecider#getDecisionMode()
	 */
	@Override
	public LaraDecisionModes getDecisionMode() {
		return LaraDecisionModes.HABIT;
	}

	/**
	 * Return the name of this decider and the {@link LaraDecisionConfiguration}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LHabitDecider for " + dConfiguration;
	}

	/**
	 * Return the same BO as {@link LHabitDecider#getSelectedBo()}.
	 * 
	 * @see de.cesr.lara.components.decision.LaraDecider#getSelectableBos()
	 */
	@Override
	public Collection<BO> getSelectableBos() {
		return bos;
	}

	@Override
	public List<BO> getSelectedBos() {
		return bos;
	}

	@Override
	public void setSelectedBos(List<BO> selectedBos) {
		this.bos = selectedBos;
	}

}