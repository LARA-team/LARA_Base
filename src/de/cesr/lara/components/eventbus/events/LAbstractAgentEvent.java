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
package de.cesr.lara.components.eventbus.events;

import de.cesr.lara.components.decision.LaraDecisionConfiguration;

/**
 * @author Sascha Holzhauer
 *
 */
public abstract class LAbstractAgentEvent implements LaraSynchronousEvent,
		LaraDcSpecificEvent {

	protected LaraDecisionConfiguration decisionConfiguration;

	/**
	 * @param dconfig
	 */
	public LAbstractAgentEvent(LaraDecisionConfiguration dconfig) {
		this.decisionConfiguration = dconfig;
	}

	/**
	 * @see de.cesr.lara.components.eventbus.events.LaraDcSpecificEvent#getDecisionConfiguration()
	 */
	@Override
	public LaraDecisionConfiguration getDecisionConfiguration() {
		return this.decisionConfiguration;
	}

	public String toString() {
		return "AgentEvent for " + decisionConfiguration;
	}

	@Override
	public void exchangeDecisionConfiguration(LaraDecisionConfiguration dConfig) {
		this.decisionConfiguration = dConfig;
	}
}
