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
package de.cesr.lara.components.preprocessor.event;

import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.eventbus.events.LaraSynchronousEvent;

/**
 * Abstract class that provides basic methods for {@link LaraPpEvent}s.
 * 
 * @author Sascha Holzhauer
 */
public abstract class LAbstractPpEvent implements LaraPpEvent,
		LaraSynchronousEvent {

	protected LaraDecisionConfiguration dConfig;
	protected LaraAgent<?, ?> agent;

	/**
	 * @param dConfig
	 * @param agent
	 */
	public LAbstractPpEvent(LaraAgent<?, ?> agent,
			LaraDecisionConfiguration dConfig) {
		this.dConfig = dConfig;
		this.agent = agent;
	}

	/**
	 * @see de.cesr.lara.components.preprocessor.event.LaraPpEvent#getAgent()
	 */
	@Override
	public LaraAgent<?, ?> getAgent() {
		return agent;
	}

	/**
	 * @see de.cesr.lara.components.preprocessor.event.LaraPpEvent#getdConfig()
	 */
	@Override
	public LaraDecisionConfiguration getdConfig() {
		return dConfig;
	}

}