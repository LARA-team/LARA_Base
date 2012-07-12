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
package de.cesr.lara.components.agents;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.preprocessor.LaraBOPreselector;

/**
 * 
 * The defined method is invoked by {@link LaraBOPreselector} in case of
 * {@link LaraBOPreselector.LAccuracy} = <code>ASK_AGENT.</code>
 * 
 * @author Sascha Holzhauer
 * @param <A>
 *            type of agent
 * @param <BO>
 *            type of behavioural option
 * @date 10.02.2010
 * 
 */
public interface LaraBOPreselectingAgent<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, BO>> {

	/**
	 * Invoked by {@link LaraBOPreselector} in case of
	 * {@link LaraBOPreselector.LAccuracy} = <code>ASK_AGENT.</code>
	 * 
	 * @param option
	 *            behavioural option to preselect
	 * @return true if the behavioural option passes the check
	 */
	public boolean preselect(BO option);

}
