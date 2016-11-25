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
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Element;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecider;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.LaraScoreReportingDecider;
import de.cesr.lara.components.postprocessor.LaraPostprocessorComp;


/**
 * @author Sascha Holzhauer
 * @param <A>
 * @param <BO>
 * 
 */
public class LEffectTargetPostProcessor<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
		implements LaraPostprocessorComp<A, BO> {

	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(LEffectTargetPostProcessor.class);

	@Element(required = false)
	protected double effectTarget = 1.0;

	@Override
	public void postProcess(A agent, LaraDecisionConfiguration dConfig) {
		LaraDecider<BO> decider = agent.getLaraComp().getDecisionData(dConfig).getDecider();

		if (!(decider instanceof LaraScoreReportingDecider)) {
			// <- LOGGING
			logger.error("Application of LThresholdPostProcessor requires the applied LaraDecider "
					+ "to implement the interface LaraScoreReportingDecider!");
			throw new IllegalStateException("Application of LThresholdPostProcessor requires the applied LaraDecider "
					+ "to implement the interface LaraScoreReportingDecider!");
			// LOGGING ->
		}

		if (this.calculateEffectTarget(agent, dConfig) != Double.NaN) {
			this.effectTarget = this.calculateEffectTarget(agent, dConfig);
		}

		TreeMap<Double, BO> orderedBos = new TreeMap<>(new Comparator<Double>() {
			// reverse normal, ascending order
			@Override
			public int compare(Double o1, Double o2) {
				return o2.compareTo(o1);
			}
		});
		List<BO> processedBos = new ArrayList<>();
		
		double effectSum = 0.0;
		
		// order bos
		for (BO selectedBo : decider.getSelectedBos()) {
			orderedBos.put(((LaraScoreReportingDecider<BO>) decider).getScore(selectedBo), selectedBo);
		}
		
		for (Entry<Double, BO> entry : orderedBos.entrySet()) {
			if (effectSum >= this.effectTarget) {
				break;
			}
			effectSum += entry.getKey();

			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("EffectSum: + " + entry.getKey() + " = " + effectSum);
			}
			// LOGGING ->

			processedBos.add(entry.getValue());
			
		}

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Selected BOs: " + processedBos);
		}
		// LOGGING ->

		decider.setSelectedBos(processedBos);
	}

	/**
	 * Can be overridden by subclasses to determine the effect target on demand.
	 * 
	 * @param agent
	 * @return Double.NaN
	 */
	protected double calculateEffectTarget(A agent, LaraDecisionConfiguration dConfig) {
		return Double.NaN;
	}
}
