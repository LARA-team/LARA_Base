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


/**
 * Used to identify applied decision modes. Every {@link LaraDecider} needs to specify the decision mode it represents.
 * If custom decision modes are added, a custom enumeration which extends this one should be implemented.
 * 
 * @author Sascha Holzhauer
 * 
 */
public enum LaraDecisionModes implements LaraDecisionMode {

	HABIT(1),

	DELIBERATIVE(10),

	HEURISTICS(20),
	
	HEURISTICS_EXPLORATION(21),

	IMITATION(30);
	
	
	protected final int id;

	LaraDecisionModes(int id) {
		this.id = id;
	}


	/**
	 * @see de.cesr.lara.components.decision.LaraDecisionMode#getId()
	 */
	@Override
	public final int getId() {
		return this.id;
	}
}
