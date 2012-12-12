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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import de.cesr.lara.components.agents.impl.LAbstractAgent;
import de.cesr.lara.components.eventbus.LaraInternalEventSubscriber;
import de.cesr.lara.components.eventbus.events.LAgentDecideEvent;
import de.cesr.lara.components.eventbus.events.LAgentExecutionEvent;
import de.cesr.lara.components.eventbus.events.LAgentPerceptionEvent;
import de.cesr.lara.components.eventbus.events.LAgentPostprocessEvent;
import de.cesr.lara.components.eventbus.events.LAgentPreprocessEvent;
import de.cesr.lara.components.eventbus.events.LInternalModelInitializedEvent;
import de.cesr.lara.components.eventbus.events.LModelInstantiatedEvent;
import de.cesr.lara.components.eventbus.events.LModelStepEvent;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.eventbus.impl.LEventbus;
import de.cesr.lara.components.model.LaraModel;
import de.cesr.lara.components.util.LaraRandom;
import de.cesr.lara.components.util.impl.LRandomService;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;

/**
 * Since this class does not deal with agents, certain methods regarding agents
 * are abstract
 * 
 * Features:
 * <ul>
 * <li>Sets up eventbus</li>
 * <li>Updating a calendar</li>
 * <li>Updating a simulation state variable</li>
 * </ul>
 * 
 * NOTE: The simulation state is only valid in case of synchronous agent triggering!
 * 
 * @author Sascha Holzhauer
 * @date 10.02.2010
 * 
 */
public abstract class LAbstractModel implements LaraModel,
		LaraInternalEventSubscriber {

	private static final Logger logger = Log4jLogger.getLogger(LAbstractModel.class);

	
	protected LEventbus eventBus;
	
	/**
	 * Calendar that tracks simulation step
	 */
	protected Calendar calendar;

	/**
	 * The simulatsion's current stage
	 */
	protected LSimulationStage currentSimStage = LSimulationStage.UNDEFINED;

	/**
	 * {@link NumberFormat} to format floating point numbers
	 */
	protected NumberFormat floatPointFormat;

	/**
	 * {@link NumberFormat} to format integer numbers
	 */
	protected NumberFormat integerFormat;

	/**
	 * The {@link LaraRandom} instance of this model
	 */
	protected LaraRandom randomMan;

	/**
	 * current time step
	 */
	protected int step;
	

	/**
	 * Constructor. Registers for events at the event bus.
	 */
	public LAbstractModel() {
		LAbstractAgent.resetCounter();
		LModel.setNewModel(this);
		
		eventBus = LEventbus.getInstance();
		eventBus.subscribe(this, LModelInstantiatedEvent.class);
		eventBus.subscribe(this, LModelStepEvent.class);
	}

	/**
	 * This method is called every time step.
	 * Advances the calendar by one day. 
	 * 
	 * It should be overridden in case a tick means something
	 * other than a day.
	 */
	public void advanceCalender() {
		calendar.add(Calendar.DAY_OF_MONTH, 1);
	}

	/**
	 * Methods that override this method must (except you know what you are
	 * doing) call super.init()! Triggered by ModelInstantiatedEvent.
	 * 
	 * 
	 */
	public void init() {
		// <- LOGGING
		logger.info("LAbstractModel: Initialising/Resetting");
		// LOGGING ->
		
		this.step = 0;
		this.randomMan = new LRandomService(0);
		this.integerFormat = new DecimalFormat("0000");
		this.floatPointFormat = new DecimalFormat("0.0000");

		calendar = Calendar.getInstance();
		
		// Causes the calendar for instance to jump from May to April when adding
		// a day to May, 31th (results in April 1st).
		calendar.setLenient(true);
	}

	/**
	 * When overridden, needs to call <code>super.onInternalEvent(event);</code>!
	 * 
	 * @see de.cesr.lara.components.eventbus.LaraInternalEventSubscriber#onInternalEvent(de.cesr.lara.components.eventbus.events.LaraEvent)
	 */
	@Override
	public void onInternalEvent(LaraEvent event) {

		if (event instanceof LModelInstantiatedEvent) {
			init();
			// TODO modeler's duty...!
			eventBus.publish(new LInternalModelInitializedEvent());
			
		} else if (event instanceof LModelStepEvent) {
			step();
			
		} else if (event instanceof LAgentPerceptionEvent) {
			currentSimStage = LSimulationStage.PERCEIVE;
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug(">> Perceive: " + getCurrentStep());
			}
			// LOGGING ->

		} else if (event instanceof LAgentPreprocessEvent) {
			currentSimStage = LSimulationStage.PREPROCESS;
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug(">> Preprocess: " + getCurrentStep());
			}
			// LOGGING ->
		
		} else if (event instanceof LAgentDecideEvent) {
			currentSimStage = LSimulationStage.DECIDE;
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug(">> Decide: " + getCurrentStep());
			}
			// LOGGING ->
		
		} else if (event instanceof LAgentExecutionEvent) {
			currentSimStage = LSimulationStage.EXECUTE;
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug(">> Execute: " + getCurrentStep());
			}
			// LOGGING ->
		
		} else if (event instanceof LAgentPostprocessEvent) {
			currentSimStage = LSimulationStage.POSTPROCESS;
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug(">> Postprocess: " + getCurrentStep());
			}
			// LOGGING ->
		}
	}

	/**
	 * Note: This method is alternative to
	 * {@link LAbstractModel#step(int stepIncrease)}
	 * 
	 * @see de.cesr.lara.components.model.LaraModel#step()
	 */
	@Override
	public final void step() {
		this.step++;
		this.advanceCalender();

		// <- LOGGING
		logger.info(">>>>> Simulating timestep " + getCurrentStep() + " <<<<<<");
		// LOGGING ->
	}

	/**
	 * Note: This method is alternative to {@link LAbstractModel#step}
	 * 
	 * @see de.cesr.lara.components.model.LaraModel#step(int)
	 */
	@Override
	public final void step(int stepIncrease) {
		this.step = this.step + stepIncrease;
		for (int i = 0; i < stepIncrease; i++) {
			this.advanceCalender();
		}
	}

	/**
	 * @see de.cesr.lara.components.model.LaraModel#resetLara()
	 */
	@Override
	public void resetLara() {
		LAbstractAgent
				.resetCounter();
		LModel.setNewModel(this);

		eventBus = LEventbus
				.getInstance();
		eventBus.subscribe(
				this,
				LModelInstantiatedEvent.class);
		eventBus.subscribe(
				this,
				LModelStepEvent.class);
	}

	/****************************************************
	 * GETTER and SETTER                                *
	 ****************************************************/
	
	/**
	 * @see de.cesr.lara.components.model.LaraModel#getCurrentDate()
	 */
	@Override
	public Date getCurrentDate() {
		return calendar.getTime();
	}

	/**
	 * @see de.cesr.lara.components.model.LaraModel#getCurrentSimulationStage()
	 */
	@Override
	public LSimulationStage getCurrentSimulationStage() {
		return currentSimStage;
	}

	/**
	 * @see de.cesr.lara.components.model.LaraModel#getCurrentStep()
	 */
	@Override
	public int getCurrentStep() {
		return this.step;
	}

	/**
	 * @see de.cesr.lara.components.model.LaraModel#getFloatPointFormat()
	 */
	@Override
	public NumberFormat getFloatPointFormat() {
		return this.floatPointFormat;
	}

	/**
	 * @see de.cesr.lara.components.model.LaraModel#getIntegerFormat()
	 */
	@Override
	public NumberFormat getIntegerFormat() {
		return this.integerFormat;
	}

	/**
	 * @see de.cesr.lara.components.model.LaraModel#getLRandom()
	 */
	@Override
	public LaraRandom getLRandom() {
		return this.randomMan;
	}
	
	/**
	 * @see de.cesr.lara.components.model.LaraModel#setCurrentStep(int)
	 */
	@Override
	public void setCurrentStep(int step) {
		this.step = step;
	}
}
