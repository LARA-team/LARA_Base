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
import de.cesr.lara.components.decision.LaraDecisionMode;
import de.cesr.lara.components.decision.LaraDecisionModes;
import de.cesr.lara.components.util.LaraRandom;
import de.cesr.lara.components.util.logging.impl.LAgentLevel;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;


/**
 * @param <A>
 *        type of agent
 * @param <BO>
 *        type of behavioural options
 * 
 */
public class LExplorationDecider<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
		implements LaraDecider<BO> {

	/**
	 * logger
	 */
	private final Logger logger = Log4jLogger.getLogger(LExplorationDecider.class);
	private Logger agentLogger = null;

	A agent = null;
	List<BO> bos = new ArrayList<>();
	LaraDecisionConfiguration dConfiguration;
	LaraRandom random;

	/**
	 * @param agent
	 * @param dConfiguration
	 */
	public LExplorationDecider(A agent,
 LaraDecisionConfiguration dConfiguration) {
		this.agent = agent;
		this.dConfiguration = dConfiguration;

		this.random = agent.getLaraComp().getLaraModel().getLRandom();

		// init agent specific logger (agent id is first part of logger name):
		if (Log4jLogger.getLogger(agent.getAgentId() + "." + LExplorationDecider.class.getName()).isEnabledFor(
				LAgentLevel.AGENT)) {
			agentLogger = Log4jLogger.getLogger(agent.getAgentId() + "." + LExplorationDecider.class.getName());
		}
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDecider#decide()
	 */
	@Override
	public void decide() {
		this.bos.clear();

		int randomBOnum =
				random.getUniform().nextIntFromTo(0,
						agent.getLaraComp().getDecisionData(dConfiguration).getBos().size() - 1);
		// TODO switch to BO lists
		this.bos.add(new ArrayList<BO>(agent.getLaraComp().getDecisionData(dConfiguration).getBos()).get(randomBOnum));

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			StringBuffer buffer = new StringBuffer();
			for (BO bo : agent.getLaraComp().getDecisionData(dConfiguration).getBos()) {
				buffer.append("\t" + bo + "\n");
			}
			logger.debug("BOs to explore: " + buffer.toString());
		}
		logger.info(agent + "> Explored BO: " + bos.get(0));
		if (agentLogger != null) {
			agentLogger.debug(agent + "> Explored BO:\n" + bos.get(0));
		}
		// LOGGING ->
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
	public List<BO> getSelectedBos() {
		return this.bos;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDecider#getSelectedBo()
	 */
	@Override
	public BO getSelectedBo() {
		return this.bos.get(0);
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDecider#getDecisionMode()
	 */
	@Override
	public LaraDecisionMode getDecisionMode() {
		return LaraDecisionModes.HEURISTICS_EXPLORATION;
	}

	/**
	 * Return the name of this decider and the {@link LaraDecisionConfiguration}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LExplorationDecider for " + dConfiguration;
	}

	@Override
	public Collection<BO> getSelectableBos() {
		return agent.getLaraComp().getDecisionData(dConfiguration).getBos();
	}


	@Override
	public void setSelectedBos(List<BO> selectedBos) {
		this.bos = selectedBos;
	}
}