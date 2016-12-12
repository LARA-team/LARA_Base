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
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.decision.LaraBoRow;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.LaraDecisionMode;
import de.cesr.lara.components.decision.LaraDecisionModes;
import de.cesr.lara.components.decision.LaraDeliberativeChoiceComponent;
import de.cesr.lara.components.decision.LaraDeliberativeDecider;
import de.cesr.lara.components.decision.LaraScoreReportingDecider;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;


/**
 * 
 * the agents decision module
 * 
 * To apply {@link LInspectionBoRow}s, set the logger
 * de.cesr.lara.components.decision.impl.LDeliberativeDecider.RowInspection at least to DEBUG level!
 * 
 * @param <BO>
 *        type of behavioural option
 */
public class LDeliberativeDecider<BO extends LaraBehaviouralOption<?, ? extends BO>> implements
		LaraDeliberativeDecider<BO>, LaraScoreReportingDecider<BO> {

	/**
	 * logger
	 */
	protected Logger logger = Log4jLogger.getLogger(LDeliberativeDecider.class);

	protected Logger rowLogger = Log4jLogger.getLogger(LDeliberativeDecider.class.getName() + ".RowInspection");

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
	protected Map<LaraPreference, Double> preferenceWeights = null;

	/**
	 * behavioural options
	 */
	protected Collection<BO> selectableBOs = new ArrayList<>();

	/**
	 * BO selected by decision
	 */
	protected List<BO> selectedBos = new ArrayList<>();

	protected List<LaraBoRow<BO>> situationalUtilityMatrixRows;

	protected int numToSelect = 1;

	protected boolean neutralisePeferenceWeights = false;

	/**
	 * @param dConfiguration
	 *            the decision configuration of the current decision process
	 */
	public LDeliberativeDecider(LaraDecisionConfiguration dConfiguration) {
		// <- LOGGING
		logger.info(this.getClass().getSimpleName() + " initialised for decision " + dConfiguration + ".");
		// LOGGING ->

		this.dConfiguration = dConfiguration;
	}

	/**
	 * @param dConfiguration
	 *        the decision configuration of the current decision process
	 */
	public LDeliberativeDecider(LaraDecisionConfiguration dConfiguration, boolean neutralisePreferenceWeights) {
		this(dConfiguration);
		this.neutralisePeferenceWeights = neutralisePreferenceWeights;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDecider#decide()
	 */
	@Override
	public void decide() {
		selectedBos.clear();

		// <-- LOGGING
		if (selectableBOs.size() > 0) {
			logger.info(selectableBOs.iterator().next().getAgent() + "> decide()");
		} else {
			logger.info("> decide()");
		}
		// LOGGING -->

		situationalUtilityMatrixRows = new ArrayList<LaraBoRow<BO>>();

		// <- LOGGING
		// Facilitate comparison of rows:
		if (logger.isDebugEnabled()) {
			Collections.<BO> sort(new ArrayList<BO>(selectableBOs),
					new Comparator<BO>() {
						@Override
						public int compare(BO arg0, BO arg1) {
							return arg0.getKey().compareTo(arg1.getKey());
						}
					});
		}

		if (logger.isDebugEnabled()) {
			StringBuffer buffer = new StringBuffer();
			for (BO bo : selectableBOs) {
				buffer.append(System.getProperty("line.separator") + "\t");
				buffer.append(bo);
			}
			logger.debug("Selectable BOs: " + buffer);
		}
		// LOGGING ->

		double averageWeight = 0.0;
		if (this.neutralisePeferenceWeights) {
			for (Entry<LaraPreference, Double> entry : this.preferenceWeights.entrySet()) {
				averageWeight += entry.getValue();
			}
			averageWeight /= this.preferenceWeights.size();
		}

		for (BO bo : selectableBOs) {

			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Handle BO " + bo.toShortString() + ": " + bo.getValue().entrySet());
			}
			// LOGGING ->

			LaraBoRow<BO> boRow;
			if (rowLogger.isDebugEnabled()) {
				boRow = new LInspectionBoRow<>(bo);
			} else {
				boRow = new LLightBoRow<>(bo);
			}

			for (Entry<LaraPreference, Double> utility : bo.getValue()
					.entrySet()) {

				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("Calculate matrix value for utility "
							+ utility);
				}
				// LOGGING ->

				/*
				 * avoid putting utilities into laraBoRows which are not needed for current decision (BOs may be valid
				 * for other decisions and thus define utility for more preferenceWeights.)
				 */
				if (isGoalConsidered(utility.getKey())) {
					double preference = getPreferenceForGoal(utility.getKey());
					// error handling:
					if (Double.isNaN(utility.getValue())) {
						// <- LOGGING
						logger.error("Utility value of goal "
								+ utility.getKey().getId() + " is NaN for BO "
								+ bo);
						// LOGGING ->

						throw new IllegalStateException(
								"Utility value of utility "
										+ utility.getKey().getId()
								+ " is NaN for BO " + bo);
					}
					if (Double.isNaN(preference)) {
						// <- LOGGING
						logger.error("Preference value of goal "
								+ utility.getKey().getId() + " is NaN.");
						// LOGGING ->

						throw new IllegalStateException(
								"Preference value of goal "
										+ utility.getKey().getId()
								+ " is NaN.");
					}

					boRow.setIndividualUtilityValue(utility.getKey(), utility.getValue() * preference);

					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug("Multiply situational value for "
								+ utility.getKey() + ": " + utility.getValue()
								+ " = " + utility.getValue() * getPreferenceForGoal(utility.getKey()));
					}
					// LOGGING ->
				}
			}
			if (this.neutralisePeferenceWeights) {
				boRow.neutralisePreferenceWeights(averageWeight);
			}

			situationalUtilityMatrixRows.add(boRow);
		}

		assert selectedBos.size() == 0;
		selectedBos.addAll(getDeliberativeChoiceComp().getKSelectedBos(dConfiguration, situationalUtilityMatrixRows,
				numToSelect));

		// <- LOGGING
		logger.info("Post decide > SituationalMatrix: "
				+ situationalUtilityMatrixRows);
		// LOGGING ->
	}

	/******************************************************************************
	 * HELPER METHODS
	 ******************************************************************************/

	/**
	 * 
	 * @see de.cesr.lara.components.decision.LaraDeliberativeDecider#getNumSelectableBOs()
	 */
	@Override
	public int getNumSelectableBOs() {
		return situationalUtilityMatrixRows
				.size();
	}

	/******************************************************************************
	 * GETTER & SETTER
	 ******************************************************************************/

	/**
	 * @see de.cesr.lara.components.decision.LaraDeliberativeDecider#getPreferenceWeights()
	 */
	@Override
	public Map<LaraPreference, Double> getPreferenceWeights() {
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
	 * @deprecated
	 */
	@Override
	public BO getSelectedBo() {
		if (selectedBos.size() == 0) {
			// <- LOGGING
			logger.warn((selectableBOs.size() > 0 ? selectableBOs.iterator().next().getAgent() : "")
					+ "decide() has not been called for decision '" + dConfiguration.getId() + "' or yielded no result");
			// LOGGING ->
			return null;
		} else {
			return selectedBos.get(0);
		}
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDeliberativeDecider#setDeliberativeChoiceComponent(de.cesr.lara.components.decision.LaraDeliberativeChoiceComponent)
	 */
	@Override
	public void setDeliberativeChoiceComponent(LaraDeliberativeChoiceComponent deliberativeChoiceComponent) {
		this.deliberativeChoiceComponent = deliberativeChoiceComponent;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDeliberativeDecider#setPreferenceWeights(java.util.Map)
	 */
	@Override
	public void setPreferenceWeights(Map<LaraPreference, Double> preferences) {
		this.preferenceWeights = new LinkedHashMap<LaraPreference, Double>();
		for (Entry<LaraPreference, Double> entry : preferences.entrySet()) {
			if (this.dConfiguration.getPreferences().contains(entry.getKey())) {
				this.preferenceWeights.put(entry.getKey(), entry.getValue());
			}
		}

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Set Preferences: " + preferenceWeights);
		}
		logger.info("Received " + this.preferenceWeights.size()
 + " relevant preference weight(s)");
		// LOGGING ->
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDeliberativeDecider#setSelectableBos(java.util.Collection)
	 */
	@Override
	public void setSelectableBos(Collection<BO> behaviouralOptions) {
		this.selectableBOs = behaviouralOptions;

		// <- LOGGING
		logger.info(behaviouralOptions != null ? "Received " + behaviouralOptions.size() + " behavioural option(s)"
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
	private double getPreferenceForGoal(LaraPreference goal) {
		return preferenceWeights.get(goal).doubleValue();
	}

	/**
	 * Checks if the given preference is defined, which also means by definition whether the specified preference is
	 * considered during the decision process.
	 * 
	 * @param preference
	 *        the preference to check
	 * @return true if the given preference is considered
	 */
	private boolean isGoalConsidered(LaraPreference preference) {
		return preferenceWeights.containsKey(preference);
	}

	/**
	 * Checks if the deliberative choice component has been set and provides it true.
	 * 
	 * @return deliberative choice component
	 */
	protected LaraDeliberativeChoiceComponent getDeliberativeChoiceComp() {
		if (this.deliberativeChoiceComponent == null) {
			// <- LOGGING
			logger.error("Deliberative Choice Component has not been set at " + this);
			// LOGGING ->
			throw new IllegalStateException("Deliberative Choice Component has not been set at " + this);
		} else {
			return this.deliberativeChoiceComponent;
		}
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDecider#getDecisionMode()
	 */
	@Override
	public LaraDecisionMode getDecisionMode() {
		return LaraDecisionModes.DELIBERATIVE;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraScoreReportingDecider#getScore(de.cesr.lara.components.LaraBehaviouralOption)
	 */
	public double getScore(BO bo) {
		for (LaraBoRow<BO> r : this.situationalUtilityMatrixRows) {
			if (r.getBehaviouralOption().equals(bo))
				return r.getSum();
		}
		return Double.NaN;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraScoreReportingDecider#getScore(de.cesr.lara.components.LaraBehaviouralOption,
	 *      de.cesr.lara.components.LaraPreference)
	 */
	public double getScore(BO bo, LaraPreference pref) {
		for (LaraBoRow<BO> r : this.situationalUtilityMatrixRows) {
			if (r.getBehaviouralOption().equals(bo))
				return r.getIndividualUtilityValue(pref);
		}
		return Double.NaN;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDecider#getSelectedBos()
	 */
	@Override
	public List<BO> getSelectedBos() {
		if (selectedBos.size() == 0) {
			// <- LOGGING
			logger.warn((selectableBOs.size() > 0 ? selectableBOs.iterator().next().getAgent() : "")
					+ "decide() has not been called for decision '" + dConfiguration.getId() + "' or yielded no result");
			// LOGGING ->
		}
		return selectedBos;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDecider#setSelectedBos(java.util.List)
	 */
	@Override
	public void setSelectedBos(List<BO> selectedBos) {
		this.selectedBos = selectedBos;
	}
}
