/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 08.03.2010
 */
package de.cesr.lara.components.decision.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import cern.jet.random.AbstractDistribution;
import cern.jet.random.Uniform;
import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.LaraDeliberativeChoiceComponent;
import de.cesr.lara.components.decision.LaraRow;
import de.cesr.lara.components.decision.LaraUtilityMatrix;
import de.cesr.lara.components.model.impl.LModel;
import de.cesr.lara.components.util.LaraRandom;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;

/**
 * Tie Rule: In case there are more than one BOs with the highest score, a
 * random one is chosen among these.
 */
public class LDecisionHeuristicComponent_MaxLineTotalRandomAtTie<BO extends LaraBehaviouralOption<?, BO>>
		implements LaraDeliberativeChoiceComponent<BO> {

	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger
			.getLogger(LDecisionHeuristicComponent_MaxLineTotalRandomAtTie.class);

	static AbstractDistribution rand = null;

	/**
	 * 
	 */
	public LDecisionHeuristicComponent_MaxLineTotalRandomAtTie() {
		this(LaraRandom.UNIFORM_DEFAULT);
	}

	/**
	 * @param distribution
	 *            the distribution name to draw random number from
	 */
	public LDecisionHeuristicComponent_MaxLineTotalRandomAtTie(
			String distribution) {
		LDecisionHeuristicComponent_MaxLineTotalRandomAtTie.rand = LModel
				.getModel().getLRandom().getDistribution(distribution);
		if (!(rand instanceof Uniform)) {
			logger.error("The given random stream name does not belong to a Uniform distribution!");
			throw new IllegalArgumentException(
					"The given random stream name does not belong to a Uniform distribution!");
		}
	}

	/**
	 * TODO test!
	 * 
	 * @param matrix
	 * @param k
	 * @return a set of k best behavioural option (regarding row sum)
	 */
	@Override
	public Set<? extends BO> getKSelectedBOs(
			LaraDecisionConfiguration dConfiguration,
			LaraUtilityMatrix<BO> matrix, int k) {
		if (logger.isDebugEnabled()) {
			for (LaraRow<BO> row : matrix.getRows()) {
				logger.debug("\t\t row: " + row.getBehaviouralOption().getKey());
			}
		}
		if (k > matrix.getNumRows()) {
			if (logger.isDebugEnabled()) {
				logger.debug("k (" + k + ") greater thant number of rows: "
						+ matrix.getNumRows());
			}
			throw new IllegalArgumentException(
					"The number of rows in the matrix is below the number of requested BOs");
		}

		Set<BO> bos = new TreeSet<BO>();

		// add rows to a sorted set:
		SortedSet<LaraRow<BO>> rows = new TreeSet<LaraRow<BO>>(
				new Comparator<LaraRow<BO>>() {
					@Override
					public int compare(LaraRow<BO> row1, LaraRow<BO> row2) {
						return Double.compare(row2.getSum(), row1.getSum()) != 0 ? Double
								.compare(row2.getSum(), row1.getSum()) :
						// same sum: compare according to key names:
						// TODO refactor!
						// return 1 if even keys are equal:
								row1.getBehaviouralOption()
										.getKey()
										.compareTo(
												row2.getBehaviouralOption()
														.getKey()) != 0 ? row1
										.getBehaviouralOption()
										.getKey()
										.compareTo(
												row2.getBehaviouralOption()
														.getKey()) : row1
										.toString().compareTo(row2.toString());
					}
				});
		rows.addAll(matrix.getRows());

		if (k == matrix.getNumRows()) {
			for (LaraRow<BO> row : rows) {
				bos.add(row.getBehaviouralOption());
			}
			if (logger.isDebugEnabled()) {
				logger.debug("k: " + k + " / number of available rows: "
						+ rows.size() + " rows: " + rows);
				for (LaraRow row : rows) {
					logger.debug("\t\t row: "
							+ row.getBehaviouralOption().getKey());
				}
			}
			return bos;
		}

		assert (k < matrix.getNumRows());

		LaraRow<BO>[] arrayRows = new LaraRow[rows.size()];
		// arrayRows = sorted BOs in ascending order:
		arrayRows = rows.toArray(arrayRows);
		// check whether the a row outside the requested range has same sum than
		// the last row inside the range:
		if (logger.isDebugEnabled()) {
			logger.debug("k: " + k + " / number of available rows: "
					+ rows.size() + " rows: " + rows + " array size: "
					+ arrayRows.length);
			for (LaraRow row : rows) {
				logger.debug("\t\t row: " + row.getBehaviouralOption().getKey());
			}
		}
		if (arrayRows[k - 1].getSum() == arrayRows[k].getSum()) {
			// the last row in selected range is equal to the first row outside
			// the selected range

			// choose the rows with the same sum as the last in the selected
			// range:
			int numSameSum = 0;
			int numWithinRange = 0;
			List<LaraRow<BO>> bestBos = new ArrayList<LaraRow<BO>>();
			for (int i = 0; i < rows.size(); i++) {
				if (arrayRows[i].getSum() == arrayRows[k - 1].getSum()) {
					bestBos.add(arrayRows[i]);
					numSameSum++;
					// check whether there are more rows with best sum than k
					// (the number of rows that is requested):
					if (i < k) {
						numWithinRange++;
					}
				}
			}
			// add behavioural options within range that have different sum
			// (i.e. higher sum):
			for (int i = 0; i < k - numWithinRange; i++) {
				bos.add(arrayRows[i].getBehaviouralOption());
			}

			assert numWithinRange <= numSameSum;

			// add remaining BOs from all BOs with same sum:
			for (int i = 0; i < numWithinRange; i++) {
				// -i because the size of bestBos decreases!
				int random = ((Uniform) rand).nextIntFromTo(0, numSameSum - i
						- 1);
				bos.add(bestBos.get(random).getBehaviouralOption());
				if (logger.isDebugEnabled()) {
					logger.debug("random: " + random);
				}
				bestBos.remove(random);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("number of bos: " + bos.size() + " bos: " + bos);
			}
		} else {
			for (int i = 0; i < k; i++) {
				bos.add(arrayRows[i].getBehaviouralOption());
			}
		}
		return bos;
	}

	/**
	 * /** Return the BO with the highest sum of preference fulfilment. Tie
	 * Rule: In case there are more than one BOs with the highest score, a
	 * random one is chosen among these.
	 * 
	 * @see de.cesr.lara.components.decision.LaraDeliberativeChoiceComponent#getBestBehaviouralOption(de.cesr.lara.components.decision.LaraUtilityMatrix)
	 */
	@Override
	public BO getSelectedBO(LaraDecisionConfiguration dConfiguration,
			LaraUtilityMatrix<BO> matrix) {
		logger.info(".getBestBehaviouralOption()");

		if (matrix.getNumRows() == 0) {
			throw new IllegalStateException(
					"The matrix does not contain any row to choose from!");

		} else {
			List<LaraRow<BO>> bestBos = new ArrayList<LaraRow<BO>>();
			double bestSum = Float.NEGATIVE_INFINITY;
			double rSum = 0;
			for (LaraRow<BO> r : matrix.getRows()) {
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
				if (logger.isEnabledFor(Priority.INFO)) {
					logger.info("Score for "
							+ r.getBehaviouralOption().getClass()
									.getSimpleName() + ": " + rSum);
				}
			}

			if (bestBos.size() == 1) {
				logger.info("There is one BO with highest score.");
				return bestBos.get(0).getBehaviouralOption();
			} else {
				logger.info("There are " + bestBos.size()
						+ " BOs with highest score.");
				return bestBos.get(new Random().nextInt(bestBos.size() - 1))
						.getBehaviouralOption();
			}
		}
	}
}