/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 02.03.2011
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
	 * Simulation is in preprocessing stage.
	 */
	PREPROCESS,

	/**
	 * Simulation is in process decision stage.
	 */
	PROCESS,

	/**
	 * Default value if an implementation of {@link LaraModel} does not update
	 * the simulation stage variable.
	 */
	UNDEFINED;

}
