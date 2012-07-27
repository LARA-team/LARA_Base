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
package de.cesr.lara.components.postprocessor.impl;

import org.apache.log4j.Logger;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.param.LDecisionMakingPa;
import de.cesr.lara.components.postprocessor.LaraPostprocessorComp;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;
import de.cesr.parma.core.PmParameterManager;

/**
 * @author Sascha Holzhauer
 * 
 */
public class LDefaultPostProcessorComp<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
		implements LaraPostprocessorComp<A, BO> {

	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger
			.getLogger(LDefaultPostProcessorComp.class);

	@Override
	public void postProcess(A agent, LaraDecisionConfiguration dConfig) {

		// TODO check memories capacity (needs to be truly larger than habit
		// threshold!)
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(this
					+ "> Memorize selected BO: "
					+ agent.getLaraComp().getDecisionData(dConfig).getDecider()
							.getSelectedBo() + " for decision '" + dConfig);
		}
		// LOGGING ->

		agent.getLaraComp()
				.getGeneralMemory()
				.memorize(
						new LSelectedBoProperty<BO>(dConfig, agent
								.getLaraComp().getDecisionData(dConfig)
								.getDecider().getSelectedBo()),
						(Integer) PmParameterManager
								.getParameter(LDecisionMakingPa.HABIT_TRESHOLD) + 1);
	}
}
