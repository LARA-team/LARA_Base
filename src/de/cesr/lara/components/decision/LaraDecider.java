/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 26.05.2010
 */
package de.cesr.lara.components.decision;

import java.util.Set;

import de.cesr.lara.components.LaraBehaviouralOption;

/**
 * Interface for all components that may execute a decision.
 * 
 * @param <BO>
 *            type of behavioural options the decider is indented for
 */
public interface LaraDecider<BO extends LaraBehaviouralOption<?, BO>> {

	/**
	 * Executes the decision! That is, select a {@link LaraBehaviouralOption}.
	 */
	public void decide();

	/**
	 * Return the {@link LaraBehaviouralOption}s that were selected in
	 * {@link LaraDecider#decide()}. Note: Do not perform the actual selection
	 * here since this method might be called more than once!
	 * 
	 * @param k
	 *            the size of the returned set - Integer.MAX_VALUE if all BOs
	 *            shall be returned
	 * @return a set of {@link LaraBehaviouralOption}s as result of the decision
	 *         process
	 */
	public Set<? extends BO> getKSelectedBos(int k);

	/**
	 * Return the {@link LaraBehaviouralOption}s that were selected in
	 * {@link LaraDecider#decide()}. Note: Do not perform the actual selection
	 * here since this method might be called more than once!
	 * 
	 * @param k
	 *            the size of the returned set - Integer.MAX_VALUE if all BOs
	 *            shall be returned
	 * @return a set of {@link LaraBehaviouralOption}s as result of the decision
	 *         process
	 */
	public Set<? extends BO> getKSelectedBosSituational(int k);

	/**
	 * @return the number of rows in the decision's matrix
	 */
	public int getNumSelectableBOs();

	/**
	 * Return the {@link LaraBehaviouralOption} that was selected in
	 * {@link LaraDecider#decide()}. Note: Do not perform the actual selection
	 * here since this method might be called more than once!
	 * 
	 * @return a {@link LaraBehaviouralOption} as result of the decision process
	 */
	public BO getSelectedBo();

	/**
	 * Return the {@link LaraBehaviouralOption} that was selected in
	 * {@link LaraDecider#decide()}. Note: Do not perform the actual selection
	 * here since this method might be called more than once!
	 * 
	 * @return a {@link LaraBehaviouralOption} as result of the decision process
	 */
	public BO getSelectedBoSituational();
}