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
 * Collects behavioural options from the BO-memory. The collection may be
 * filtered by certain criteria. Note that normally afterwards the
 * {@link LaraBOPreselector} is applied to each BO.
 * 
 * @param <A>
 *            type of agents this BO collector is intended for
 * @param <BO>
 *            type of behavioural options the given BO-memory memorises
 */
public interface LaraBOCollector<A extends LaraAgent<? super A, ?>, BO extends LaraBehaviouralOption<?, ?>>
		extends LaraPreprocessorComp<A, BO> {

}