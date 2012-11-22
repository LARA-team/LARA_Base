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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.decision.LaraBoRow;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.LaraDeliberativeChoiceComponent;
import de.cesr.lara.components.decision.LaraDeliberativeDecider;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;

/**
 * 
 * the agents decision module
 * 
 * @param <BO>
 *            type of behavioural option
 */
public class LDeliberativeDecider<BO extends LaraBehaviouralOption<?, ? extends BO>>
		implements LaraDeliberativeDecider<BO> {

	/**
	 * logger
	 */
	protected Logger logger = Log4jLogger.getLogger(LDeliberativeDecider.class);

	/**
	 * According {@link LaraDecisionConfiguration}
	 */
	protected LaraDecisionConfiguration dConfiguration;

	/**
	 * deliberativeChoiceComp component
	 */
	protected LaraDeliberativeChoiceComponent deliberativeChoiceComponent = null;

	/**
	 * preferenceWeights
	 */
	protected Map<Class<? extends LaraPreference>, Double> preferenceWeights = null;

	/**
	 * behavioural options
	 */
	protected Collection<BO> selectableBOs = new ArrayList<BO>();

	/**
	 * BO selected by decision
	 */
	protected BO selectedBo = null;

	protected Collection<LaraBoRow<BO>> situationalUtilityMatrixRows;

	/**
	 * @param dConfiguration
	 *            the decision configuration of the current decision process
	 */
	public LDeliberativeDecider(LaraDecisionConfiguration dConfiguration) {
		// <- LOGGING
		logger.info(this.getClass().getSimpleName() + " initialised.");
		// LOGGING ->

		this.dConfiguration = dConfiguration;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDecider#decide()
	 */
	@Override
	public void decide() {
		// <-- LOGGING
		if (selectableBOs.size() > 0) {
			logger.info(selectableBOs.iterator().next().getAgent()
					+ "> decide()");
		} else {
			logger.info("> decide()");
		}
		// LOGGING -->

		situationalUtilityMatrixRows = new ArrayList<LaraBoRow<BO>>();

		// // Facilitate comparison of rows:
		// Collections.<LaraBehaviouralOption>sort((ArrayList<LaraBehaviouralOption>)
		// selectableBOs,
		// new Comparator<LaraBehaviouralOption>() {
		// @Override
		// public int compare(LaraBehaviouralOption arg0, LaraBehaviouralOption
		// arg1) {
		// return arg0.getKey().compareTo(arg1.getKey());
		// }
		// });

		for (BO bo : selectableBOs) {
			LaraBoRow<BO> boRow = new LLightBoRow<BO>(bo);
			for (Entry<Class<? extends LaraPreference>, Double> utility : bo
					.getValue().entrySet()) {
				/*
				 * avoid putting utilities into laraBoRows which are not needed
				 * for current decision (BOs may be valid for other decisions
				 * and thus define utility for more preferenceWeights.)
				 */
				if (isGoalConsidered(utility.getKey())) {
					double preference = getPreferenceForGoal(utility.getKey());
					// error handling:
					if (Double.isNaN(utility.getValue())) {
						// <- LOGGING
						logger.error("Utility value of goal "  + utility.getKey().getSimpleName() + " is NaN for BO " + bo);
						// LOGGING ->
						
						throw new IllegalStateException("Utility value of utility "  + 
								utility.getKey().getSimpleName() + " is NaN for BO " + bo);
					}
					if (Double.isNaN(preference)) {
						// <- LOGGING
						logger.error("Preference value of goal "  + utility.getKey().getSimpleName() + " is NaN.");
						// LOGGING ->
						
						throw new IllegalStateException("Preference value of goal "  + 
								utility.getKey().getSimpleName() + " is NaN.");
					}
					
					boRow.setIndividualUtilityValue(
							utility.getKey(),
							utility.getValue()
									* preference);

					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug("Add situaltional value for "
								+ utility.getKey() + ": " + utility.getValue()
								+ " = " + utility.getValue()
								* getPreferenceForGoal(utility.getKey()));
					}
					// LOGGING ->
				}
			}
			situationalUtilityMatrixRows.add(boRow);
		}

		selectedBo = getDeliberativeChoiceComp().getSelectedBo(dConfiguration,
				situationalUtilityMatrixRows);
		// <- LOGGING
		logger.info("Post decide: SitautionalMatrix: "
				+ situationalUtilityMatrixRows);
		// LOGGING ->
	}

	/******************************************************************************
	 * HELPER METHODS
	 ******************************************************************************/

	/**
	 * @see de.cesr.lara.components.decision.LaraDeliberativeDecider#getNumSelectableBOs()
	 */
	@Override
	public int getNumSelectableBOs() {
		return situationalUtilityMatrixRows.size();
	}

	/******************************************************************************
	 * GETTER & SETTER
	 ******************************************************************************/

	/**
	 * @see de.cesr.lara.components.decision.LaraDeliberativeDecider#getPreferenceWeights()
	 */
	@Override
	public Map<Class<? extends LaraPreference>, Double> getPreferenceWeights() {
		return preferenceWeights;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDeliberativeDecider#getSelectableBos()
	 */
	@Override
	public Collection<BO> getSelectableBos() {
		return selectableBOs;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDecider#getSelectedBo()
	 */
	@Override
	public BO getSelectedBo() {
		if (selectedBo == null) {
			// <- LOGGING
			logger.error((selectableBOs.size() > 0 ? selectableBOs.iterator()
					.next().getAgent() : "")
					+ "decide() has not been called for decision '"
					+ dConfiguration.getId() + "'");
			// LOGGING ->
		}
		return selectedBo;
	}

	/**
	 * This method delegates to the deliberative choice component since bos may
	 * not be stored in this class because k is not finitely defined.
	 * 
	 * NOTE: It can not be guaranteed that several calls of this method yields
	 * identical sets of BOs!
	 * 
	 * @see de.cesr.lara.components.decision.LaraDecider#getKSelectedBos(int)
	 */
	@Override
	public Set<? extends BO> getKSelectedBos(int k) {
		if ((k != Integer.MAX_VALUE) && (k > getNumSelectableBOs())) {
			throw new IllegalStateException("The number of requested BOs (" + k
					+ ") is larger than the number of available BOs"
					+ getNumSelectableBOs() + ")!");
		}
		return getDeliberativeChoiceComp().getKSelectedBos(dConfiguration,
				situationalUtilityMatrixRows, k);
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDeliberativeDecider#setDeliberativeChoiceComponent(de.cesr.lara.components.decision.LaraDeliberativeChoiceComponent)
	 */
	@Override
	public void setDeliberativeChoiceComponent(
			LaraDeliberativeChoiceComponent deliberativeChoiceComponent) {
		this.deliberativeChoiceComponent = deliberativeChoiceComponent;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDeliberativeDecider#setPreferenceWeights(java.util.Map)
	 */
	@Override
	public void setPreferenceWeights(
			Map<Class<? extends LaraPreference>, Double> preference) {
		this.preferenceWeights = preference;

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Received Preferences: " + preferenceWeights);
		}
		logger.info("Received " + preference.size() + " preferenceWeights");
		// LOGGING ->
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDeliberativeDecider#setSelectableBos(java.util.Collection)
	 */
	@Override
	public void setSelectableBos(Collection<BO> behaviouralOptions) {
		this.selectableBOs = behaviouralOptions;

		// <- LOGGING
		logger.info(behaviouralOptions != null ? "Received "
				+ behaviouralOptions.size() + " behavioural options"
				: "Received set of BOs is empty!");
		// LOGGING ->
	}

	/**
	 * Return the name of this decider and the {@link LaraDecisionConfiguration}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LDeliberativeDecider for " + dConfiguration;
	}

	/**
	 * Provides the individual preference weight of the given preference.
	 * 
	 * @param preference
	 * @return individual preference weight
	 */
	private double getPreferenceForGoal(Class<? extends LaraPreference> goal) {
		return preferenceWeights.get(goal).doubleValue();
	}

	/**
	 * Checks if the given preference is defined, which also means by definition
	 * whether the specified preference is considered during the decision
	 * process.
	 * 
	 * @param preference
	 *            the preference to check
	 * @return true if the given preference is considered
	 */
	private boolean isGoalConsidered(Class<? extends LaraPreference> preference) {
		return preferenceWeights.containsKey(preference);
	}

	/**
	 * Checks if the deliberative choice component has been set and provides it
	 * true.
	 * 
	 * @return deliberative choice component
	 */
	protected LaraDeliberativeChoiceComponent getDeliberativeChoiceComp() {
		if (this.deliberativeChoiceComponent == null) {
			// <- LOGGING
			logger.error("Deliberative Choice Component has not been set at "
					+ this);
			// LOGGING ->
			throw new IllegalStateException(
					"Deliberative Choice Component has not been set at " + this);
		} else {
			return this.deliberativeChoiceComponent;
		}
	}
}
