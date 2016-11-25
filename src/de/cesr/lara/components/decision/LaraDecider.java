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
package de.cesr.lara.components.decision;


import java.util.Collection;
import java.util.List;

import de.cesr.lara.components.LaraBehaviouralOption;


/**
 * Interface for all components that may execute a decision.
 * 
 * @param <BO>
 *        type of behavioural options the decider is indented for
 */
public interface LaraDecider<BO extends LaraBehaviouralOption<?, ?>> {

	/**
	 * Executes the decision! That is, select a {@link LaraBehaviouralOption}.
	 */
	public void decide();

	/**
	 * @return Returns a collection of all considered BOs.
	 */
	public Collection<BO> getSelectableBos();

	/**
	 * @return the number of rows in the decision's laraBoRows
	 */
	public int getNumSelectableBOs();

	/**
	 * Return the {@link LaraBehaviouralOption}s that were selected in {@link LaraDecider#decide()}.
	 * 
	 * Note for implementers: Do not perform the actual selection here since this method might be called more than once!
	 * 
	 * @return a list of selected {@link LaraBehaviouralOption} as result of the decision process
	 */
	public List<BO> getSelectedBos();

	/**
	 * Intended for post-processing to change the list of selected {@link LaraBehaviouralOption}s
	 * 
	 * @param selectedBos
	 */
	public void setSelectedBos(List<BO> selectedBos);

	/**
	 * Return the {@link LaraBehaviouralOption} that was selected in {@link LaraDecider#decide()}.
	 * 
	 * Note: Do not perform the actual selection here since this method might be called more than once!
	 * 
	 * @return a {@link LaraBehaviouralOption} as result of the decision process
	 * @deprecated
	 */
	public BO getSelectedBo();

	/**
	 * @return the decision mode the decider belongs to
	 */
	public LaraDecisionMode getDecisionMode();
}