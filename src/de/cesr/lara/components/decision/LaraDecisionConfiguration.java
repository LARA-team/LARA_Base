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
package de.cesr.lara.components.decision;

import java.util.Collection;

import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.agents.LaraAgentComponent;
import de.cesr.lara.components.preprocessor.LaraPreprocessor;

/**
 * The {@link LaraDecisionConfiguration} is an identifier for a certain decision
 * and is passed to
 * {@link LaraPreprocessor#preprocess(LaraDecisionConfiguration, de.cesr.lara.components.agents.LaraAgent)}
 * and to {@link LaraAgentComponent#decide(LaraDecisionConfiguration)}. It
 * further defines the set of preferences that are relevant for the decision.
 */
public interface LaraDecisionConfiguration {

	/**
	 * @return the decision's id
	 */
	public String getId();

	/**
	 * Provides the preferences that are relevant for this decision
	 * 
	 * @return relevant preferences
	 */
	public Collection<LaraPreference> getPreferences();

	/**
	 * Set the preferences that are relevant for this decision
	 * 
	 * @param preferences
	 */
	public void setPreferences(Collection<LaraPreference> preferences);

	/**
	 * @return true if the decision making process should select a single BO.
	 */
	public boolean singleSelectedBoExpected();
}
