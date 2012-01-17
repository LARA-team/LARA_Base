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
import de.cesr.lara.components.agents.LaraBOPreselectingAgent;


/**
 * BOChecker
 * 
 * @param <A>
 *        type of agents this BO preselector is intended for
 * @param <BO>
 *        type of behavioural options that are checked
 */
public interface LaraBOPreselector<A extends LaraAgent<? super A, ?>, BO extends LaraBehaviouralOption<?,?>>
		extends LaraPreprocessorComp<A, BO> {

	/**
	 * A common interface for preprocessor accuracy statements which enables the user to provide his own set of
	 * accuracies.
	 * 
	 * @author Sascha Holzhauer
	 * @date 10.02.2010
	 * 
	 */
	public interface Accuracy {
	}
	
	/**
	 * accuracy
	 * 
	 * @author klemm
	 * @date 16.02.2010
	 * 
	 */
	public enum LAccuracy implements LaraBOPreselector.Accuracy {
		/**
		 * Using ASK_AGENT will cause the preprocessor to step back to the agent (this requires the user to implement
		 * {@link LaraBOPreselectingAgent}).
		 */
		ASK_AGENT,
	
		/**
		 * Most accurate preprocessor handling
		 */
		ACCURATE,
	
		/**
		 * Moderate preprocessor handling
		 */
		MODERATE,
	
		/**
		 * Tolerant preprocessor handling
		 */
		TOLERANT;
	}
}
