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
package de.cesr.lara.components.preprocessor;


import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;


/**
 * Updates the utility values for the given behavioural options regarding the decision of the given decision builder.
 * 
 * @param <A>
 *        the type of agents this BO utilityUpdater is intended for
 * @param <BO>
 *        the type of behavioural options that are updated
 * 
 */
public interface LaraBOUtilityUpdater<A extends LaraAgent<? super A, BO>, BO extends LaraBehaviouralOption<?, ?>>
		extends LaraPreprocessorComp<A, BO> {
}