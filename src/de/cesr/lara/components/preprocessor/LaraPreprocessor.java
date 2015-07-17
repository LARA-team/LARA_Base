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
package de.cesr.lara.components.preprocessor;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;

/**
 * The Pre-process Factory is used to pick the appropriate pre-processor
 * components for the given decision ( {@linkLaraDecisionBuilder}).
 * 
 * 
 * @param <A>
 *            the type of agents this pre-process builder is intended for
 * @param <BO>
 *            the type of behavioural options the preprocessor shall manage
 * 
 * @author Sascha Holzhauer
 * @date 05.02.2010
 */
public interface LaraPreprocessor<A extends LaraAgent<? super A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>> {

	/**
	 * Lists all preprocessor components defined in this preprocessor per
	 * component.
	 * 
	 * @return String
	 */
	public String getComponentsString();

	/**
	 * Lists components defined for the given {@link LaraDecisionConfiguration
	 * dConfig}.
	 * 
	 * @param dConfig
	 * @return string representation
	 */
	public String getConfigurationString(LaraDecisionConfiguration dConfig);

	/**
	 * @param configuration
	 * @return true if this preprocessor meets the given configuration object
	 */
	public boolean meetsConfiguration(
			LaraPreprocessorConfigurator<A, BO> configuration);

	/**
	 * @param dConfig
	 *            decision configuration
	 * @param agent
	 */
	public abstract void preprocess(LaraDecisionConfiguration dConfig, A agent);

}