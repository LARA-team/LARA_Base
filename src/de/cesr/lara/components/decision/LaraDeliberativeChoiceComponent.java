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
 * Heuristic used for selection of (best) behavioural option
 */
public interface LaraDeliberativeChoiceComponent {

	/**
	 * If k is {@link Integer#MAX_VALUE} all available BOs should be returned!
	 * 
	 * Note: The component is to guarantee that for several calls with identical
	 * k the same set of BOs is returned!
	 * 
	 * @param dConfig
	 *            the decision configuration of this decision process
	 * @param boRows
	 *            collection of {@link LaraBoRow}s
	 * @param k
	 *            number of BOs to select
	 * @return k best behavioural options
	 */
	public <BO extends LaraBehaviouralOption<?, ? extends BO>> List<? extends BO> getKSelectedBos(
			LaraDecisionConfiguration dConfig,
			Collection<LaraBoRow<BO>> boRows, int k);

	/**
	 * Provides the selected BO for the given decision configuration
	 * 
	 * @param dConfig
	 *        the decision configuration of this decision process
	 * @param boRows
	 *        collection of {@link LaraBoRow}s
	 * @return selected behavioural option
	 */
	public <BO extends LaraBehaviouralOption<?, ? extends BO>> BO getSelectedBo(
			LaraDecisionConfiguration dConfig, Collection<LaraBoRow<BO>> boRows);
}
