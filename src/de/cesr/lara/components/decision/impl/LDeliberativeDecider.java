package de.cesr.lara.components.decision.impl;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.LaraDeliberativeChoiceComponent;
import de.cesr.lara.components.decision.LaraHeader;
import de.cesr.lara.components.decision.LaraDeliberativeDecider;
import de.cesr.lara.components.decision.LaraRow;
import de.cesr.lara.components.decision.LaraUtilityMatrix;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;


/**
 * TODO parameters
 * 
 * the agents decision module
 * @param <BO>
 * 			type of behavioural option
 */
public class LDeliberativeDecider<BO extends LaraBehaviouralOption<?, ? extends BO>> implements LaraDeliberativeDecider<BO> {

	/**
	 * logger
	 */
	protected Logger										logger						= Log4jLogger
																								.getLogger(LDeliberativeDecider.class);

	/**
	 * preferenceWeights
	 */
	protected Map<Class<? extends LaraPreference>, Double>	preferenceWeights					= null;
	/**
	 * behavioural options
	 */
	protected Collection<BO>						selectableBOs			= new ArrayList<BO>();

	/**
	 * BO selected by decision
	 */
	protected BO											selectedBo					= null;

	/**
	 * header
	 */
	protected LaraHeader									header						= null;

	/**
	 * utility matrix
	 */
	protected LaraUtilityMatrix<BO>								utilityMatrix				= null;

	/**
	 * situational utility matrix
	 */
	protected LaraUtilityMatrix<BO>							situationalUtilityMatrix	= null;

	/**
	 * heuristic component
	 */
	protected LaraDeliberativeChoiceComponent			deliberativeChoiceComponent			= null;

	/**
	 * According {@link LaraDecisionConfiguration}
	 */
	protected LaraDecisionConfiguration					dConfiguration;

	/**
	 * constructor
	 * 
	 * @param dConfiguration
	 */
	public LDeliberativeDecider(LaraDecisionConfiguration dConfiguration) {
		logger.info(this.getClass().getSimpleName() + "()");

		this.dConfiguration = dConfiguration;
		// initialised here because in case of empty set of BOs initMatrix() is not called (SH):
		// create matrix
		utilityMatrix = new LUtilityMatrix();
		// create situational matrix
		situationalUtilityMatrix = new LUtilityMatrix();

	}

	/**
	 * returns the preference (importance) value of the given goal (by simple class name)
	 * 
	 * @param goalname
	 * @return
	 */
	private double getPreferenceForGoal(Class<? extends LaraPreference> goal) {
		return preferenceWeights.get(goal).doubleValue();
	}

	/**
	 * Checks if preference for the given goal is defined, which also means by definition whether the specified goal is
	 * considered during the decision process.
	 * 
	 * @param utility
	 *        the goal to check
	 * @return Created by klemm on 10.02.2010
	 */
	private boolean isGoalConsidered(Class<? extends LaraPreference> utilityGoal) {
		return preferenceWeights.containsKey(utilityGoal);
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDeliberativeDecider#getPreferenceWeights()
	 */
	@Override
	public Map<Class<? extends LaraPreference>, Double> getPreferenceWeights() {
		return preferenceWeights;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDeliberativeDecider#getSelectedBO()
	 */
	@Override
	public BO getSelectedBO() {
		return deliberativeChoiceComponent.getSelectedBO(dConfiguration, situationalUtilityMatrix);
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDeliberativeDecider#getSelectableBOs()
	 */
	@Override
	public Collection<BO> getSelectableBOs() {
		return selectableBOs;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDeliberativeDecider#getHeader()
	 */
	@Override
	public LaraHeader getHeader() {
		return header;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDeliberativeDecider#setSelectableBOs(java.util.Collection)
	 */
	@Override
	public void setSelectableBOs(Collection<BO> behaviouralOptions) {
		this.selectableBOs = behaviouralOptions;
		logger.info(behaviouralOptions != null ? "Received " + behaviouralOptions.size() + " behavioural options"
				: "Received set of BOs is empty!");
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDeliberativeDecider#setHeader(de.cesr.lara.components.decision.LaraHeader)
	 */
	@Override
	public void setHeader(LaraHeader header) {
		this.header = header;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDecider#decide()
	 */
	public void decide() {
		// <-- LOGGING
		if (selectableBOs.size() > 0) {
			logger.info(selectableBOs.iterator().next().getAgent() + "> decide()");
		} else {
			logger.info("> decide()");
		}
		// LOGGING -->

		// add header
		List<String> cells = new ArrayList<String>();
		cells.add("Behavioural Option");

		// TODO for all Goals
		for (Class<? extends LaraPreference> g : dConfiguration.getPreferences()) {
			cells.add(g.getSimpleName());
		}
		header = new LHeader(cells);
		// set header
		utilityMatrix.setHeader(header);
		situationalUtilityMatrix.setHeader(header);

		Collection<LaraRow<BO>> utilityMatrixRows = new ArrayList<LaraRow<BO>>();
		Collection<LaraRow<BO>> situationalUtilityMatrixRows = new ArrayList<LaraRow<BO>>();
		// add behavioural options as row to matrix

		// // Facilitate comparison of rows:
		// Collections.<LaraBehaviouralOption>sort((ArrayList<LaraBehaviouralOption>) selectableBOs,
		// new Comparator<LaraBehaviouralOption>() {
		// @Override
		// public int compare(LaraBehaviouralOption arg0, LaraBehaviouralOption arg1) {
		// return arg0.getKey().compareTo(arg1.getKey());
		// }
		// });

		for (BO bo : selectableBOs) {
			List<Double> values = new ArrayList<Double>();
			List<Double> situationalValues = new ArrayList<Double>();
			// TODO calculate utility for current time step / in preprocessor?!
			for (Entry<Class<? extends LaraPreference>, Double> utility : bo.getValue().entrySet()) {
				// avoid putting utilities into matrix which are not needed for current decision
				// (BOs may be valid for other decisions and thus define utility for more preferenceWeights.)
				if (isGoalConsidered(utility.getKey())) {
					values.add(new Double(utility.getValue()));
					// TODO here double/float should make a difference!
					situationalValues.add(new Double(utility.getValue().doubleValue()
							* getPreferenceForGoal(utility.getKey())));

					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug("Add situaltional value for " + utility.getKey() + ": " + utility.getValue()
								+ " = " + utility.getValue() * getPreferenceForGoal(utility.getKey()));
					}
					// LOGGING ->

				}
			}
			utilityMatrixRows.add(new LRow(bo, values));
			situationalUtilityMatrixRows.add(new LRow(bo, situationalValues));
		}
		utilityMatrix.setRows(utilityMatrixRows);
		situationalUtilityMatrix.setRows(situationalUtilityMatrixRows);
		selectedBo = deliberativeChoiceComponent.getSelectedBO(dConfiguration, utilityMatrix);
		logger.info("Post decide: SitautionalMatrix: " + situationalUtilityMatrix);
	}

	@Override
	public void setDeliberativeChoiceComponent(LaraDeliberativeChoiceComponent deliberativeChoiceComponent) {
		this.deliberativeChoiceComponent = deliberativeChoiceComponent;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDeliberativeDecider#setPreferences(java.util.Map)
	 */
	@Override
	public void setPreferences(Map<Class<? extends LaraPreference>, Double> preference) {
		this.preferenceWeights = preference;

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Received Preferences: " + preferenceWeights);
		}
		// LOGGING ->

		logger.info("Received " + preference.size() + " preferenceWeights");
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDeliberativeDecider#getSelectedBo()
	 */
	@Override
	public BO getSelectedBo() {
		if (selectedBo == null) {
			selectedBo = deliberativeChoiceComponent.getSelectedBO(dConfiguration, utilityMatrix);
		}
		return selectedBo;
	}

	/**
	 * This method delegates to the deliberative choice component since bos may not be stored in this class because k is
	 * not finitely defined.
	 * 
	 * @see de.cesr.lara.components.decision.LaraDecider#getKSelectedBos(int)
	 */
	@Override
	public Set<? extends BO> getKSelectedBos(int k) {
		if ((k != Integer.MAX_VALUE) && (k > getNumSelectableBOs())) {
			throw new IllegalStateException("The number of requested BOs (" + k
					+ ") is larger than the number of available BOs" + getNumSelectableBOs() + ")!");
		}
		return deliberativeChoiceComponent.getKSelectedBOs(dConfiguration, utilityMatrix, k);
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDeliberativeDecider#getNumSelectableBOs()
	 */
	@Override
	public int getNumSelectableBOs() {
		return utilityMatrix.getNumRows();
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
}
