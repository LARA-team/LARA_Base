/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * 
 */
package de.cesr.lara.components.decision.impl;


import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.LaraDeliberativeChoiceComponent;
import de.cesr.lara.components.decision.LaraRow;
import de.cesr.lara.components.decision.LaraUtilityMatrix;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;


/**
 * 
 * TODO parameters
 * 
 * Selects the behavioural option with maximal line total. Tie Rule: In case there are more than one BOs with the
 * highest score, the one with highest row number is returned.
 * 
 * @param <BO>
 *        type of behavioural option
 */
public class LDeliberativeChoiceComp_MaxLineTotal implements
		LaraDeliberativeChoiceComponent {

	/**
	 * Logger
	 */
	static private Logger	logger	= Log4jLogger.getLogger(LDeliberativeChoiceComp_MaxLineTotal.class);

	static LDeliberativeChoiceComp_MaxLineTotal instance = null;
	
	private LDeliberativeChoiceComp_MaxLineTotal() {
	}

	/**
	 * @return LDeliberativeChoiceComp_MaxLineTotal (singelton)
	 */
	static public LDeliberativeChoiceComp_MaxLineTotal getInstance() {
		if (instance == null) {
			instance = new LDeliberativeChoiceComp_MaxLineTotal();
		}
		return instance;
	}

	/**
	 * Return the BO with the highest sum of preference fulfilment. Tie Rule: In case there are more than one BOs with
	 * the highest score, the one with highest row number is returned.
	 *
	 * @see de.cesr.lara.components.decision.LaraDeliberativeChoiceComponent#getSelectedBO(de.cesr.lara.components.decision.LaraDecisionConfiguration, de.cesr.lara.components.decision.LaraUtilityMatrix)
	 */
	public <BO extends LaraBehaviouralOption<?, ? extends BO>> BO getSelectedBO(LaraDecisionConfiguration dConfiguration, LaraUtilityMatrix<BO> matrix) {
		logger.info(".getBestBehaviouralOption()");
		// get best row form matrix
		double bestSum = Float.NEGATIVE_INFINITY;
		double rSum = Float.NEGATIVE_INFINITY;
		LaraRow<BO> bestRow = null;
		for (LaraRow<BO> r : matrix.getRows()) {
			rSum = r.getSum();
			if (rSum >= bestSum) {
				bestSum = rSum;
				bestRow = r;
			}
			logger.info("Score for " + r.getBehaviouralOption().getClass().getSimpleName() + ": " + rSum);
		}
		return bestRow.getBehaviouralOption();
	}

	/**
	 * TODO test!
	 * 
	 * @see de.cesr.lara.components.decision.LaraDeliberativeChoiceComponent#getKSelectedBOs(de.cesr.lara.components.decision.LaraDecisionConfiguration, de.cesr.lara.components.decision.LaraUtilityMatrix, int)
	 */
	public <BO extends LaraBehaviouralOption<?, ? extends BO>> Set<? extends BO> getKSelectedBOs(LaraDecisionConfiguration dConfiguration, LaraUtilityMatrix<BO> matrix,
			int k) {
		if (k > matrix.getNumRows()) {
			throw new IllegalArgumentException(
					"The number of rows in the matrix is below the number of requested behavioural options");
		}

		Set<BO> bos = new HashSet<BO>();

		if (k == matrix.getNumRows()) {
			for (LaraRow<BO> row : matrix.getRows()) {
				bos.add(row.getBehaviouralOption());
			}
			return bos;
		}

		SortedSet<LaraRow> rows = new TreeSet<LaraRow>(new Comparator<LaraRow>() {
			@Override
			public int compare(LaraRow row1, LaraRow row2) {
				return row2.getSum() - row1.getSum() < 0 ? -1 : row2.getSum() - row1.getSum() > 0 ? 1 : 0;
			}
		});
		rows.addAll(matrix.getRows());

		LaraRow<BO>[] arrayRows = new LaraRow[rows.size()];
		arrayRows = rows.toArray(arrayRows);

		for (int i = 0; i < k; i++) {
			bos.add(arrayRows[i].getBehaviouralOption());
		}
		return bos;
	}
}
