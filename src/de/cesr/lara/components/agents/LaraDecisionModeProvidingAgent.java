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
package de.cesr.lara.components.agents;


import de.cesr.lara.components.decision.LaraDecisionModes;
import de.cesr.lara.components.preprocessor.LaraDecisionModeSelector;


/**
 * @author Sascha Holzhauer
 * 
 */
public interface LaraDecisionModeProvidingAgent {

	/**
	 * Provides a suggestion for the decision mode this agent should select in terms of {@link LaraDecisionModes}. It is
	 * only a suggestion since the {@link LaraDecisionModeSelector} may be forced to select e.g. deliberative decision
	 * mode if habit is suggested but its preconditions are not fulfilled.
	 * 
	 * This method may return <code>null</code>, and the decision falls back to the {@link LaraDecisionModeSelector}.
	 * 
	 * @return decision mode
	 */
	public LaraDecisionModes getDecisionModeSuggestion();
}
