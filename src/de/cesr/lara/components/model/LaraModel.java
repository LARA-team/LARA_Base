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
package de.cesr.lara.components.model;

import java.text.NumberFormat;
import java.util.Date;

import de.cesr.lara.components.eventbus.impl.LEventbus;
import de.cesr.lara.components.model.impl.LSimulationStage;
import de.cesr.lara.components.util.LaraDecisionConfigRegistry;
import de.cesr.lara.components.util.LaraPreferenceRegistry;
import de.cesr.lara.components.util.LaraRandom;

/**
 * Classes that implement this interface need to provide basic simulation
 * functionality for LARA like time stepping etc. For many cases, this
 * represents an adapter to existing simulation frameworks.
 */
public interface LaraModel {

	/**
	 * Returns the {@link Date} object for the date that is associated with the
	 * current tick. Note that several ticks may be associated with the same
	 * {@link Date}.
	 * 
	 * @return date the current date
	 */
	public Date getCurrentDate();

	/**
	 * Returns the current simulation stage, no matter if agents are triggered
	 * synchronously or asynchronously.
	 * 
	 * @return the current simulation stage
	 */
	public LSimulationStage getCurrentSimulationStage();

	/**
	 * Retrieve the current overall time step.
	 * 
	 * @return step the current time step of the overall model
	 */
	public int getCurrentStep();

	/**
	 * Returns a {@link NumberFormat} to format decimal floating point numbers
	 * 
	 * @return A NumberFormat to format decimal floating point numbers
	 */
	public NumberFormat getFloatPointFormat();

	/**
	 * Returns a {@link NumberFormat} to format integer numbers
	 * 
	 * @return A NumberFormat to format integer numbers
	 */
	public NumberFormat getIntegerFormat();

	/**
	 * Return the random manager that is used for random processes in LARA.
	 * Either, the model author should implement (or assign to this.randomMan
	 * when extending AbstracLModel) the random number generator used in the
	 * custom model part, or reset the LRandomService by calling
	 * getLRandom.setSeed(seed) using the correct seed parameter.
	 * 
	 * NOTE: Make sure that the {@link LaraRandom} class is instantiated only
	 * once since creating an instance every time this method is called results
	 * in starting the random sequence anew each time the method is called!
	 * 
	 * @return the random manager
	 */
	public LaraRandom getLRandom();

	/**
	 * Set the current overall time step.
	 * 
	 * @param step
	 *            the current time step of the overall model
	 */
	public void setCurrentStep(int step);

	/**
	 * This method id invoked by the model controller to trigger the model. The
	 * last step number is increased by 1.
	 */
	public void step();

	/**
	 * This method id invoked by the model controller to trigger the model. The
	 * last step number is increased by stepIncrease.
	 * 
	 * @param stepIncrease
	 *            the number of steps to increase
	 */
	public void step(int stepIncrease);

	/**
	 * @return the eventbus associated with this model.
	 */
	public LEventbus getLEventbus();

	/**
	 * Access to this model's {@link LaraPreferenceRegistry}.
	 * 
	 * @return preference registry
	 */
	public LaraPreferenceRegistry getPrefRegistry();

	/**
	 * Access to this model's {@link LaraDecisionConfigRegistry}.
	 * 
	 * @return decision configuration registry
	 */
	public LaraDecisionConfigRegistry getDecisionConfigRegistry();

	/**
	 * Required for batch runs, especially for Repast Simphony when the subclass of LAbstractModel is persistent!
	 */
	public void resetLara();
}