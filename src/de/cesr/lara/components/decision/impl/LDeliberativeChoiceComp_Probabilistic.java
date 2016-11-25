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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import cern.jet.random.AbstractDistribution;
import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraBoRow;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.LaraDeliberativeChoiceComponent;
import de.cesr.lara.components.model.LaraModel;
import de.cesr.lara.components.model.impl.LModel;
import de.cesr.lara.components.util.logging.impl.LAgentLevel;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;


/**
 * Selects behavioural options based on a probabilistic choice mechanism.
 * 
 * BOs are placed on a roulette wheel with the range according to their utility score. The BO is selected in whose range
 * the random number falls. IN case of negative utility sums all utility score sums are shifted right by the lowest
 * value.
 * 
 * Parameter Eta is used as an exponent to the BO's utility score that influenced the range on the roulette wheel.
 * 
 * In case of a single BO or if (k == number of available BOs) the process is accelerated by directly choosing this BO.
 * 
 * @author Daniel Klemm
 * @author Sascha Holzhauer
 * 
 * @date 05.02.2010
 * 
 */
public class LDeliberativeChoiceComp_Probabilistic implements
		LaraDeliberativeChoiceComponent {

	/**
	 * logger
	 */
	protected Logger logger = Log4jLogger
			.getLogger(LDeliberativeChoiceComp_Probabilistic.class);

	protected LaraModel lmodel;

	private double eta = 0.0;
	private final AbstractDistribution rand;

	Logger agentLogger = null;

	/**
	 * @param lmodel 
	 * @param eta
	 *            distinction parameter
	 * @param distribution 
	 */
	public LDeliberativeChoiceComp_Probabilistic(LaraModel lmodel, float eta,
			String distribution) {
		this.lmodel = lmodel;
		this.eta = eta;
		this.rand = LModel.getModel().getLRandom()
				.getDistribution(distribution);
	}

	/**
	 * Uses eta = 1.0
	 * 
	 * @param lmodel 
	 * 
	 * @param distribution
	 */
	public LDeliberativeChoiceComp_Probabilistic(LaraModel lmodel,
			String distribution) {
		this(lmodel, 1.0f, distribution);
	}

	/**
	 * NOTE: The given Collection of {@link LaraBoRow}s is altered!
	 * 
	 * @see de.cesr.lara.components.decision.LaraDeliberativeChoiceComponent#getKSelectedBos(de.cesr.lara.components.decision.LaraDecisionConfiguration,
	 *      java.util.Collection, int)
	 */
	@Override
	public <BO extends LaraBehaviouralOption<?, ? extends BO>> List<? extends BO> getKSelectedBos(
			LaraDecisionConfiguration dConfig,
			Collection<LaraBoRow<BO>> boRows, int k) {

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			for (LaraBoRow<BO> row : boRows) {
				logger.debug("\t\t row: " + row.getBehaviouralOption().getKey());
			}
		}
		// LOGGING ->

		if (boRows.size() == 0) {
			throw new IllegalStateException(
					"The collection does not contain any BO to choose from!");
		}

		// init agent specific logger (agent id is first part of logger name):
		LaraAgent<?, ?> agent = boRows.iterator().next().getBehaviouralOption()
				.getAgent();

		// <- LOGGING
		if (Log4jLogger
				.getLogger(
						agent.getAgentId()
								+ "."
								+ LDeliberativeChoiceComp_Probabilistic.class
										.getName()).isEnabledFor(
						LAgentLevel.AGENT)) {
			agentLogger = Log4jLogger.getLogger(agent.getAgentId() + "."
					+ LDeliberativeChoiceComp_Probabilistic.class.getName());
		}
		// LOGGING ->

		List<BO> selectedBos = new ArrayList<>();

		if (k == boRows.size()) {
			for (LaraBoRow<BO> row : boRows) {
				selectedBos.add(row.getBehaviouralOption());
			}
			return selectedBos;
		}

		if (k > boRows.size()) {
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("k (" + k + ") greater than number of rows: "
						+ boRows.size());
				// LOGGING ->
			}

			throw new IllegalArgumentException(
					"The number of rows in the laraBoRows is below the number of requested BOs");
		}

		for (int i = 0; i < k; i++) {
			// <- LOGGING
			logger.info("BORows: " + boRows);
			// LOGGING ->
			LaraBoRow<BO> selected = this.selectProbabilistic(dConfig, boRows,
					agent);
			selectedBos.add(selected.getBehaviouralOption());
			boRows.remove(selected);
		}

		return selectedBos;
	}

	@Override
	public <BO extends LaraBehaviouralOption<?, ? extends BO>> BO getSelectedBo(
			LaraDecisionConfiguration dConfig, Collection<LaraBoRow<BO>> boRows) {

		if (boRows.size() == 0) {
			throw new IllegalStateException(
					"The collection does not contain any BO to choose from!");
		}

		// init agent specific logger (agent id is first part of logger name):
		LaraAgent<?, ?> agent = boRows.iterator().next().getBehaviouralOption()
				.getAgent();

		// <- LOGGING
		if (Log4jLogger
				.getLogger(
						agent.getAgentId()
								+ "."
								+ LDeliberativeChoiceComp_Probabilistic.class
										.getName()).isEnabledFor(
						LAgentLevel.AGENT)) {
			agentLogger = Log4jLogger.getLogger(agent.getAgentId() + "."
					+ LDeliberativeChoiceComp_Probabilistic.class.getName());
		}
		// LOGGING ->

		return selectProbabilistic(dConfig, boRows, agent)
				.getBehaviouralOption();
	}

	/**
	 * @param <BO>
	 * @param dConfig
	 * @param boRows
	 * @param agent
	 */
	protected <BO extends LaraBehaviouralOption<?, ? extends BO>> LaraBoRow<BO> selectProbabilistic(
			LaraDecisionConfiguration dConfig,
			Collection<LaraBoRow<BO>> boRows, LaraAgent<?, ?> agent) {

		// accelerate the process in case of a single BO to select from:
		if (boRows.size() == 1) {
			LaraBoRow<BO> selected = boRows.iterator().next();
			if (agentLogger != null) {
				agentLogger.debug(agent + "> selected: "
						+ selected.getBehaviouralOption());
			}
			return selected;
		}

		double u_eta;
		double u_eta_sum = 0.0;

		double overall_utility = 0.0f;

		boolean containsAllZero = true;
		boolean sameNegative = true;

		double minValue = 0.0d;
		double lastValue = Double.NaN;

		Map<LaraBoRow<BO>, Double> roulette_wheel = new LinkedHashMap<LaraBoRow<BO>, Double>();

		// check for negative utility sums:
		for (LaraBoRow<BO> r : boRows) {
			overall_utility = r.getSum();
			minValue = minValue > overall_utility ? overall_utility : minValue;
			if (overall_utility != 0.0) {
				containsAllZero = false;
				if (!Double.isNaN(lastValue) && (lastValue >= 0 || lastValue != overall_utility)) {
					sameNegative = false;
				}
			}
			lastValue = overall_utility;
		}

		// select a BO at random in case all BO's have utility 0:
		if (containsAllZero || sameNegative) {
			// <- LOGGING
			if (logger.isDebugEnabled() && containsAllZero) {
				logger.debug("All utility sums equal 0.0! Select random BO.");
			}
			if (logger.isDebugEnabled() && sameNegative) {
				logger.debug("All utility sums are of the same negative value! Select random BO.");
			}
			// LOGGING ->

			double randomNum = rand.nextDouble();
			if (randomNum < 0.0 || randomNum > 1.0) {
				throw new IllegalStateException(rand
						+ "> Make sure min = 0.0 and max = 1.0");
			}
			
			// <- LOGGING
			if (agentLogger != null) {
				agentLogger.debug(agent + "> selected: "
						+ new ArrayList<LaraBoRow<BO>>(boRows).get((int) (boRows.size() * randomNum)) + 
						" (Index:" + (int) (boRows.size() * randomNum) + ")");
			}
			if (logger.isDebugEnabled()) {
				logger.debug(agent + "> selected: "
						+ new ArrayList<LaraBoRow<BO>>(boRows).get((int) (boRows.size() * randomNum)) +
						" (Index:" + (int) (boRows.size() * randomNum) + ")");
			}
			// LOGGING ->

			return new ArrayList<LaraBoRow<BO>>(boRows).get((int) (boRows
					.size() * randomNum));
		}

		if (minValue < 0.0) {
			// <- LOGGING
			logger.info("Shift utility sums to positive range by " + minValue);
			// LOGGING ->
		}

		for (LaraBoRow<BO> r : boRows) {
			// shift to positive range:
			overall_utility = r.getSum()
					+ (minValue < 0.0 ? Math.abs(minValue) : 0.0);

			u_eta = Math.pow(overall_utility, eta);
			u_eta_sum += u_eta;
			roulette_wheel.put(r, u_eta);
		}

		double randFloat = rand.nextDouble();
		if (randFloat < 0.0 || randFloat > 1.0) {
			throw new IllegalStateException(rand
					+ "> Make sure min = 0.0 and max = 1.0");
		}

		// <- LOGGING
		if (agentLogger != null) {
			agentLogger.debug(agent + "> u_eta_sum: " + u_eta_sum
					+ " / random: " + randFloat);
		}
		if (logger.isDebugEnabled()) {
			logger.debug(agent + "> u_eta_sum: " + u_eta_sum + " / random: "
					+ randFloat);
		}
		// LOGGING ->

		randFloat *= u_eta_sum;

		// TODO check double/float?
		float pointer = 0.0f;

		// NOTE: For of BO's hash code that does not depend on an internal
		// object address, for instance, a HashMap
		// works and and produces comparable results:
		for (Entry<LaraBoRow<BO>, Double> entry : roulette_wheel.entrySet()) {
			pointer += entry.getValue().doubleValue();
			if (pointer >= randFloat) {
				// <- LOGGING
				if (agentLogger != null) {
					agentLogger.debug(agent + "> selected: " + entry.getKey());
				}
				if (logger.isDebugEnabled()) {
					logger.debug(agent + "> selected: " + entry.getKey());
				}
				// LOGGING ->

				return entry.getKey();
			}
		}
		logger.warn("This code should never be reached!");
		throw new AssertionError("This code should never be reached!");
	}
}
