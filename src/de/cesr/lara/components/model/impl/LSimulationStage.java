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
package de.cesr.lara.components.model.impl;

import de.cesr.lara.components.model.LaraModel;
import de.cesr.lara.components.model.LaraSimulationStage;

/**
 * Items of this enumeration identify the simulation stage.
 */
public enum LSimulationStage implements LaraSimulationStage {

	/**
	 * Simulation is in decision stage.
	 */
	DECIDE,

	/**
	 * Simulation is in perceive stage.
	 */
	PERCEIVE,

	/**
	 * Simulation is in post-process stage.
	 */
	POSTPROCESS,

	/**
	 * Simulation is in pre-processing stage.
	 */
	PREPROCESS,

	/**
	 * Simulation is in process decision stage.
	 */
	EXECUTE,

	/**
	 * Default value if an implementation of {@link LaraModel} does not update
	 * the simulation stage variable.
	 */
	UNDEFINED;

}