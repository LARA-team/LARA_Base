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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import cern.jet.random.AbstractDistribution;
import cern.jet.random.Uniform;
import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.decision.LaraBoRow;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.LaraDeliberativeChoiceComponent;
import de.cesr.lara.components.model.impl.LModel;
import de.cesr.lara.components.util.LaraRandom;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;


/**
 * Tie Rule: In case there are more than one BOs with the highest score, a random one is chosen among these.
 */
public class LDeliberativeChoiceComp_MaxLineTotalRandomAtTie implements
		LaraDeliberativeChoiceComponent {

	/**
	 * Logger
	 */
	static private Logger		logger	= Log4jLogger
												.getLogger(LDeliberativeChoiceComp_MaxLineTotalRandomAtTie.class);

	static Map<String, LDeliberativeChoiceComp_MaxLineTotalRandomAtTie> instances = new HashMap<String, LDeliberativeChoiceComp_MaxLineTotalRandomAtTie>();

	protected AbstractDistribution rand;

	/**
	 * The distribution must be of type Uniform
	 * 
	 * @param distribution
	 *            the distribution name to draw random numbers from
	 */
	private LDeliberativeChoiceComp_MaxLineTotalRandomAtTie(String distribution) {
		this.rand = LModel.getModel().getLRandom()
				.getDistribution(
				distribution);
		if (!(rand instanceof Uniform)) {
			logger.error("The given random stream name does not belong to a Uniform distribution!");
			throw new IllegalArgumentException(
					"The given random stream name does not belong to a Uniform distribution!");
		}
	}


	/**
	 * Null is translated to LaraRandom.UNIFORM_DEFAULT.
	 * 
	 * @param distribution
	 * @return
	 */
	static public LDeliberativeChoiceComp_MaxLineTotalRandomAtTie getInstance(
			String distribution) {
		if (distribution == null) {
			distribution = LaraRandom.UNIFORM_DEFAULT;
		}
		if (instances.get(distribution) == null) {
			instances.put(distribution,
					new LDeliberativeChoiceComp_MaxLineTotalRandomAtTie(
							distribution));
		}
		return instances.get(distribution);
	}

	/**
	 * Return the BO with the highest sum of preference fulfillment.
	 * 
	 * Tie Rule: In case there are more than one BOs with the highest score, a
	 * random one is chosen among these.
	 * 
	 * @see de.cesr.lara.components.decision.LaraDeliberativeChoiceComponent#getBestBehaviouralOption(de.cesr.lara.components.decision.LaraUtilityMatrix)
	 */
	@Override
	public <BO extends LaraBehaviouralOption<?, ? extends BO>> BO getSelectedBo(
			LaraDecisionConfiguration dConfiguration,
			Collection<LaraBoRow<BO>> boRows) {
		// <- LOGGING
		logger.info("getBestBehaviouralOption()");
		// LOGGING ->

		if (boRows.size() == 0) {
			throw new IllegalStateException("The laraBoRows does not contain any row to choose from!");

		} else {
			List<LaraBoRow<BO>> bestBos = new ArrayList<LaraBoRow<BO>>();
			double bestSum = Float.NEGATIVE_INFINITY;
			double rSum = 0;
			for (LaraBoRow<BO> r : boRows) {
				rSum = r.getSum();
				if (rSum >= bestSum) {
					if (rSum == bestSum) {
						bestBos.add(r);
					} else {
						bestBos.clear();
						bestSum = rSum;
						bestBos.add(r);
					}
				}
				// <- LOGGING
				if (logger.isEnabledFor(Priority.INFO)) {
					logger.info("Score for " + r.getBehaviouralOption().getClass().getSimpleName() + ": " + rSum);
				}
				// LOGGING ->
			}

			if (bestBos.size() == 1) {
				// <- LOGGING
				logger.info("There is one BO with highest score.");
				// LOGGING ->

				return bestBos.get(0).getBehaviouralOption();
			} else {
				// <- LOGGING
				logger.info("There are " + bestBos.size() + " BOs with highest score.");
				// LOGGING ->

				return bestBos.get(new Random().nextInt(bestBos.size() - 1)).getBehaviouralOption();
			}
		}
	}

	/**
	 * Return the k BOs with the highest sum of preference fulfillment.
	 * 
	 * Tie Rule: In case there are more than one BOs with the highest score, a
	 * random one is chosen among these.
	 * 
	 * @param boRows
	 *            collection of {@link LaraBoRow}s to select from
	 * @param k
	 *            number of (best) BOs to select
	 * @return a set of k best behavioural option (regarding row sum)
	 */
	@Override
	public <BO extends LaraBehaviouralOption<?, ? extends BO>> Set<? extends BO> getKSelectedBos(
			LaraDecisionConfiguration dConfiguration,
			Collection<LaraBoRow<BO>> boRows,
			int k) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			for (LaraBoRow<BO> row : boRows) {
				logger.debug("\t\t row: " + row.getBehaviouralOption().getKey());
			}
		}
		// LOGGING ->

		if (k > boRows.size()) {
			// <- LOGGING
			if (logger.isDebugEnabled())
			 {
				logger.debug("k (" + k + ") greater thant number of rows: " + boRows.size());
			// LOGGING ->
			}

			throw new IllegalArgumentException("The number of rows in the laraBoRows is below the number of requested BOs");
		}

		Set<BO> bos = new TreeSet<BO>();

		// add rows to a sorted set:
		SortedSet<LaraBoRow<BO>> rows = new TreeSet<LaraBoRow<BO>>(new Comparator<LaraBoRow<BO>>() {
			@Override
			public int compare(LaraBoRow<BO> row1, LaraBoRow<BO> row2) {
						return Double.compare(row2.getSum(), row1.getSum()) != 0 ? Double
								.compare(row2.getSum(), row1.getSum()) :
						// same sum: compare according to key names:
								row2.getBehaviouralOption().compareTo(
										row1.getBehaviouralOption());
			}
		});
		rows.addAll(boRows);

		if (k == boRows.size()) {
			for (LaraBoRow<BO> row : rows) {
				bos.add(row.getBehaviouralOption());
			}

			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("k: " + k + " / number of available rows: " + rows.size() + " rows: " + rows);
				for (LaraBoRow<BO> row : rows) {
					logger.debug("\t\t row: " + row.getBehaviouralOption().getKey());
				}
			}
			// LOGGING ->

			return bos;
		}

		assert (k < boRows.size());

		@SuppressWarnings("unchecked")
		// rows only consist of LaraBoRow<BO>s!
		LaraBoRow<BO>[] arrayRows = new LaraBoRow[rows.size()];
		// arrayRows = sorted BOs in ascending order:
		arrayRows = rows.toArray(arrayRows);

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("k: " + k + " / number of available rows: " + rows.size() + " rows: " + rows + " array size: "
					+ arrayRows.length);
			for (LaraBoRow<BO> row : rows) {
				logger.debug("\t\t row: " + row.getBehaviouralOption().getKey());
			}
		}
		// LOGGING ->

		// check whether the a row outside the requested range has same sum than
		// the last row inside the range:
		if (arrayRows[k - 1].getSum() == arrayRows[k].getSum()) {
			// the last row in selected range is equal to the first row outside the selected range

			// choose the rows with the same sum as the last in the selected range:
			int numSameSum = 0;
			int numWithinRange = 0;
			List<LaraBoRow<BO>> bestBos = new ArrayList<LaraBoRow<BO>>();
			for (int i = 0; i < rows.size(); i++) {
				if (arrayRows[i].getSum() == arrayRows[k - 1].getSum()) {
					bestBos.add(arrayRows[i]);
					numSameSum++;
					// check whether there are more rows with best sum than k (the number of rows that is requested):
					if (i < k) {
						numWithinRange++;
					}
				}
			}
			// add behavioural options within range that have different sum (i.e. higher sum):
			for (int i = 0; i < k - numWithinRange; i++) {
				bos.add(arrayRows[i].getBehaviouralOption());
			}

			assert numWithinRange <= numSameSum;

			// add remaining BOs from all BOs with same sum:
			for (int i = 0; i < numWithinRange; i++) {
				// -i because the size of bestBos decreases!
				int random = ((Uniform) rand).nextIntFromTo(0, numSameSum - i - 1);
				bos.add(bestBos.get(random).getBehaviouralOption());

				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("random: " + random);
				}
				// LOGGING ->

				bestBos.remove(random);
			}
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("number of bos: " + bos.size() + " bos: " + bos);
			}
			// LOGGING ->
		} else {
			for (int i = 0; i < k; i++) {
				bos.add(arrayRows[i].getBehaviouralOption());
			}
		}
		return bos;
	}
}