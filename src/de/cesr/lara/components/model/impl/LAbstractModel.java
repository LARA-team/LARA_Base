/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 10.02.2010
 */
package de.cesr.lara.components.model.impl;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import de.cesr.lara.components.agents.impl.LAbstractAgent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.eventbus.LaraInternalEventSubscriber;
import de.cesr.lara.components.eventbus.events.LModelFinishEvent;
import de.cesr.lara.components.eventbus.events.LModelInitializedEvent;
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
 * @author Sascha Holzhauer
 * @date 10.02.2010
 * 
 */
public abstract class LAbstractModel implements LaraModel,
		LaraInternalEventSubscriber {

	private final Logger logger = Log4jLogger.getLogger(LAbstractModel.class);

	/**
	 * 
	 */
	protected Calendar calendar;

	/**
	 * The simulatsion's current stage
	 */
	protected LSimulationStage currentSimStage = LSimulationStage.UNDEFINED;

	protected LEventbus eventBus;

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
	 * current timestep
	 */
	protected int step;

	/**
	 * Constructor.
	 */
	public LAbstractModel() {
		LAbstractAgent.resetCounter();
		LModel.setNewModel(this);

		eventBus = LEventbus.getInstance();
		eventBus.subscribe(this, LModelInstantiatedEvent.class);
		eventBus.subscribe(this, LModelStepEvent.class);
	}

	/**
	 * Advances the calendar by one day.
	 */
	public void advanceCalender() {
		// TODO day, month? should this be overriden?
		calendar.add(Calendar.DAY_OF_MONTH, 1);
	}

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
	 * Methods that override this method must (except you know what you are
	 * doing) call super.init()! Triggered by ModelInstantiatedEvent.
	 * 
	 * 
	 */
	public void init() {
		logger.info("LAbstractModel: Initialising/Resetting");
		this.step = 0;
		this.randomMan = new LRandomService(0);
		this.integerFormat = new DecimalFormat("0000");
		this.floatPointFormat = new DecimalFormat("0.0000");

		calendar = Calendar.getInstance();
		// cases the calendar for instance to jump from May to April when adding
		// a
		// day to May, 31th (results in April 1st).
		calendar.setLenient(true);
	}

	@Override
	public void onInternalEvent(LaraEvent event) {

		if (event instanceof LModelInstantiatedEvent) {
			init();
			eventBus.publish(new LModelInitializedEvent());
		} else if (event instanceof LModelStepEvent) {
			step();
		} else if (event instanceof LModelFinishEvent) {
			LEventbus.resetAll();
		}

	}

	/**
	 * Processes a given decision (perceive, pre-process, make decision). This
	 * method may be invoked several times (for each decision).
	 * 
	 * Overriding methods need to take care of the simulation stage variable.
	 * TODO destroyed - needs to be repaired
	 * 
	 * @param dConfiguration
	 *            the {@link LaraDecisionConfiguration} that represents the
	 *            decision to process.
	 */
	public void processConsecutively(LaraDecisionConfiguration dConfiguration) {
		logger.info(">>>>> Simulating timestep " + getCurrentStep() + " <<<<<<");

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(">> Perceive: " + getCurrentStep());
			// LOGGING ->
		}

		currentSimStage = LSimulationStage.PERCEIVE;
		// perceive(dConfiguration);

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(">> Preprocess: " + getCurrentStep());
			// LOGGING ->
		}

		currentSimStage = LSimulationStage.PREPROCESS;
		// preProcess(dConfiguration);

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(">> Decide: " + getCurrentStep());
			// LOGGING ->
		}

		currentSimStage = LSimulationStage.DECIDE;
		// decide(dConfiguration);

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(">> Process: " + getCurrentStep());
			// LOGGING ->
		}

		currentSimStage = LSimulationStage.PROCESS;

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(">> Postprocess: " + getCurrentStep());
			// LOGGING ->
		}

		currentSimStage = LSimulationStage.POSTPROCESS;
		// postProcess(dConfiguration);
		// clean(dConfiguration);
	}

	/**
	 * @see de.cesr.lara.components.model.LaraModel#setCurrentStep(int)
	 */
	@Override
	public void setCurrentStep(int step) {
		this.step = step;
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
	}

	/**
	 * Note: This method is alternative to {@link LAbstractModel#step}
	 * 
	 * @see de.cesr.lara.components.model.LaraModel#step(int)
	 */
	@Override
	public final void step(int stepIncrease) {
		this.step = this.step + stepIncrease;
		this.advanceCalender();
	}
}
