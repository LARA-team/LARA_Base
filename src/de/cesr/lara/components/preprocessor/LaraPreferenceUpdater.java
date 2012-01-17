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
 * Updates an agent's preference. NOTE: Normally, preferenceWeights are updated
 * at last in the preprocessor's chain.
 * 
 * @author Sascha Holzhauer
 * @param <A>
 * @date 05.02.2010
 * 
 */
public interface LaraPreferenceUpdater<A extends LaraAgent<? super A, ?>, BO extends LaraBehaviouralOption<?, ?>>
		extends LaraPreprocessorComp<A, BO> {
}
