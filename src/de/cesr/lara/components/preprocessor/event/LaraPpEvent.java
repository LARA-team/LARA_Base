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
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.preprocessor.LaraPreprocessor;

/**
 * (Sub-)Interface for {@link LaraPreprocessor} related event.
 * @author Sascha Holzhauer
 *
 */
public interface LaraPpEvent extends LaraEvent{
	
	/**
	 * Provides the {@link LaraDecisionConfiguration} that 
	 * is currently processed.
	 * @return current decision configuration
	 */
	public LaraDecisionConfiguration getdConfig();
	
	/**
	 * Returns the agent that is currently processed and
	 * whose event bus triggers this even.
	 * @return the agent that is currently processed
	 */
	public LaraAgent<?, ?> getAgent();
	
}