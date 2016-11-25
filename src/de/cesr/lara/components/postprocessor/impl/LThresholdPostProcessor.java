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


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Attribute;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecider;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.LaraScoreReportingDecider;
import de.cesr.lara.components.postprocessor.LaraPostprocessorComp;


/**
 * Checks the set of selected BOs by the decider and returns only those whose score is equal or above the defined
 * threshold.
 * 
 * TODO test
 * 
 * @author Sascha Holzhauer
 * @param <A>
 * @param <BO>
 * 
 */
public class LThresholdPostProcessor<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
		implements LaraPostprocessorComp<A, BO> {

	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(LThresholdPostProcessor.class);

	@Attribute(required = true)
	protected double threshold = 0.0;

	@Override
	public void postProcess(A agent, LaraDecisionConfiguration dConfig) {
		List<BO> processedBos = new ArrayList<>();
		
		LaraDecider<BO> decider = agent.getLaraComp().getDecisionData(dConfig).getDecider();
		
		if(!(decider instanceof LaraScoreReportingDecider)) {
			// <- LOGGING
			logger.error("Application of LThresholdPostProcessor requires the applied LaraDecider "
					+ "to implement the interface LaraScoreReportingDecider!");
			throw new IllegalStateException("Application of LThresholdPostProcessor requires the applied LaraDecider "
					+ "to implement the interface LaraScoreReportingDecider!");
			// LOGGING ->
		}
		
		for (BO selectedBo : decider.getSelectedBos()) {
			if (((LaraScoreReportingDecider<BO>) decider).getScore(selectedBo) >= this.threshold) {
				processedBos.add(selectedBo);
			}
		}
		decider.setSelectedBos(processedBos);
	}

}
