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

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.decision.LaraBoRow;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.LaraDeliberativeChoiceComponent;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;

/**
 * Selects the behavioural option with maximal line total.
 * 
 * Tie Rule: In case there are more than one BOs with the highest score, the one
 * with highest row number is returned.
 */
public class LDeliberativeChoiceComp_MaxLineTotal implements
		LaraDeliberativeChoiceComponent {

	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger
			.getLogger(LDeliberativeChoiceComp_MaxLineTotal.class);

	static LDeliberativeChoiceComp_MaxLineTotal instance = null;

	/**
	 * @return LDeliberativeChoiceComp_MaxLineTotal (singleton)
	 */
	static public LDeliberativeChoiceComp_MaxLineTotal getInstance() {
		if (instance == null) {
			instance = new LDeliberativeChoiceComp_MaxLineTotal();
		}
		return instance;
	}

	/**
	 * Private constructor enables singleton
	 */
	private LDeliberativeChoiceComp_MaxLineTotal() {
	}

	/**
	 * Return the k BOs with the highest sum of preference fulfillment.
	 * 
	 * Tie Rule: In case there are more than one BOs with the highest score, the
	 * one with highest row number (i.e. BO's compareTo yields a higher value)
	 * is returned.
	 * 
	 * @see de.cesr.lara.components.decision.LaraDeliberativeChoiceComponent#
	 *      getKSelectedBos(de.cesr.lara.components.decision.LaraDecisionConfiguration,
	 *      java.util.Collection, int)
	 */
	@Override
	public <BO extends LaraBehaviouralOption<?, ? extends BO>> Set<? extends BO> getKSelectedBos(
			LaraDecisionConfiguration dConfiguration,
			Collection<LaraBoRow<BO>> boRows, int k) {
		if (k > boRows.size()) {
			throw new IllegalArgumentException(
					"The number of rows in the laraBoRows is below the number of requested behavioural options");
		}

		Set<BO> bos = new HashSet<BO>();

		if (k == boRows.size()) {
			for (LaraBoRow<BO> row : boRows) {
				bos.add(row.getBehaviouralOption());
			}
			return bos;
		}

		SortedSet<LaraBoRow<BO>> sortedRows = new TreeSet<LaraBoRow<BO>>(
				new Comparator<LaraBoRow<BO>>() {
					@Override
					public int compare(LaraBoRow<BO> row1, LaraBoRow<BO> row2) {
						// parameters in Double.compare are exchanged since we
						// want decreasing order
						return Double.compare(row2.getSum(), row1.getSum()) != 0 ? Double
								.compare(row2.getSum(), row1.getSum()) : row2
								.getBehaviouralOption().compareTo(
										row1.getBehaviouralOption());
					}
				});
		sortedRows.addAll(boRows);

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Sorted rows: " + sortedRows);
		}
		// LOGGING ->

		Iterator<LaraBoRow<BO>> iterator = sortedRows.iterator();
		for (int i = 0; i < k; i++) {
			bos.add(iterator.next().getBehaviouralOption());
		}
		return bos;
	}

	/**
	 * Return the BO with the highest sum of preference fulfillment.
	 * 
	 * Tie Rule: In case there are more than one BOs with the highest score, the
	 * one with highest row number is returned.
	 * 
	 * @see de.cesr.lara.components.decision.LaraDeliberativeChoiceComponent#getSelectedBo(de.cesr.lara.components.decision.LaraDecisionConfiguration,
	 *      java.util.Collection)
	 */
	@Override
	public <BO extends LaraBehaviouralOption<?, ? extends BO>> BO getSelectedBo(
			LaraDecisionConfiguration dConfiguration,
			Collection<LaraBoRow<BO>> boRows) {
		// <- LOGGING
		logger.info("Determine selected BO...");
		// LOGGING ->

		if (boRows.size() == 0) {
			logger.error("The number of BOs passed to LDeliberativeChoiceComp_MaxLineTotal was 0!");
		}

		// get best row form laraBoRows
		double bestSum = Float.NEGATIVE_INFINITY;
		double rSum = Float.NEGATIVE_INFINITY;

		LaraBoRow<BO> bestRow = null;
		for (LaraBoRow<BO> r : boRows) {
			rSum = r.getSum();
			if (rSum >= bestSum) {
				bestSum = rSum;
				bestRow = r;
			}
			// <- LOGGING
			logger.info("Score for "
					+ r.getBehaviouralOption().getClass().getSimpleName()
					+ ": " + rSum);
			// LOGGING ->
		}
		return bestRow.getBehaviouralOption();
	}
}
