/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * 
 */
package de.cesr.lara.components.model;

import java.text.NumberFormat;
import java.util.Date;

import de.cesr.lara.components.model.impl.LSimulationStage;
import de.cesr.lara.components.util.LaraRandom;

/**
 * Classes that implement this interface need to provide basic simulation
 * functionality for Lara like time stepping etc. For many cases, this
 * represents an adapter to existing simulation frameworks. TODO splitt into
 * interfaces like hasEnvironment, hasMemory, hasInput, hasOutput, hasRandom
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
	 * 
	 *         Created by Sascha Holzhauer on 22.12.2009
	 */
	public int getCurrentStep();

	/**
	 * Returns a {@link NumberFormat} to format decimal floating point numbers
	 * 
	 * @return A NumberFormat to format decimal floating point numbers Created
	 *         by Sascha Holzhauer on 19.10.2010
	 */
	public NumberFormat getFloatPointFormat();

	/**
	 * Returns a {@link NumberFormat} to format integer numbers
	 * 
	 * @return A NumberFormat to format integer numbers Created by Sascha
	 *         Holzhauer on 19.10.2010
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
	 * 
	 *            Created by Sascha Holzhauer on 22.12.2009
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
	 *            the current step to set
	 */
	public void step(int stepIncrease);

}