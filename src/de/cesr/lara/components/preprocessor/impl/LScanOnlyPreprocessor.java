/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 20.05.2010
 */
package de.cesr.lara.components.preprocessor.impl;

import java.util.Collection;

import org.apache.log4j.Logger;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.preprocessor.LaraBOCollector;
import de.cesr.lara.components.preprocessor.LaraBOPreselector;
import de.cesr.lara.components.preprocessor.LaraDecisionModeSelector;
import de.cesr.lara.components.preprocessor.LaraPreprocessor;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;

/**
 * Only applies a LaraBoScanner and then sets the BOs at the agent component's
 * decision data.
 * 
 * @param <A>
 * @param <BO>
 * 
 */
public class LScanOnlyPreprocessor<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<? super A, BO>>
		implements LaraPreprocessor<A, BO> {
	static private Logger

	logger = Log4jLogger.getLogger(LPreprocessor.class);

	/**
	 * decision builder
	 */
	protected LaraDecisionConfiguration dConfiguration;

	/**
	 * @uml.property name="bOScanner"
	 */
	protected LaraBOCollector<A, BO> scanner;

	/*
	 * <------- CONSTRUCTORS ------------>
	 */

	/**
	 * Only applies a LaraBoScanner and then sets the BOs at the agent
	 * component's decision data according to the dConfiguration.
	 * 
	 * @param collector
	 * @param dConfiguration
	 */
	public LScanOnlyPreprocessor(LaraBOCollector<A, BO> scanner,
			LaraDecisionConfiguration dConfiguration) {
		this.scanner = scanner;
		this.dConfiguration = dConfiguration;

		if (logger.isDebugEnabled()) {
			logger.debug("Scanner " + scanner + " for dConfiguration "
					+ dConfiguration.getId());
		}
	}

	/*
	 * <------- METHODS ------------>
	 */

	/**
	 * Getter of the property <tt>bOChecker</tt>
	 * 
	 * @return Returns the preselector.
	 */
	public LaraBOPreselector<A, BO> getBOChecker() {
		return null;
	}

	/**
	 * Getter of the property <tt>bOScanner</tt>
	 * 
	 * @return Returns the collector.
	 * @uml.property name="bOScanner"
	 */
	public LaraBOCollector<A, BO> getBOScanner() {
		return scanner;
	}

	/*
	 * <------- GETTER & SETTER ------------>
	 */

	/**
	 * Getter of the property <tt>decisionModeSelector</tt>
	 * 
	 * @return Returns the decisionModeSelector.
	 * @uml.property name="decisionModeSelector"
	 */
	public LaraDecisionModeSelector<A> getModeSelector() {
		return null;
	}

	/**
	 * 
	 * @see de.cesr.lara.components.preprocessor.LaraPreprocessor#preprocess(de.cesr.lara.components.preprocessor.LaraPreprocessor.Accuracy,
	 *      de.cesr.lara.components.agents.LaraAgent)
	 */
	@Override
	public void preprocess(LaraBOPreselector.Accuracy accuracy, A agent) {
		// TODO

		logger.info(this.getClass().getSimpleName() + ".preprocess()");

		Collection<BO> bos;

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			bos = agent.getLaraComp().getBOMemory().recallAllMostRecent();
			logBOs(bos, "BOs in memory - ", agent);
		}
		// LOGGING ->

		bos = scanner.collectBOs(agent, agent.getLaraComp().getBOMemory(),
				dConfiguration);

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logBOs(bos, "BOs from collector - ", agent);
			// LOGGING ->
		}

		agent.getLaraComp().getDecisionData(dConfiguration).setBos(bos);
	}

	/**
	 * Setter of the property <tt>bOScanner</tt>
	 */
	public void setBOCollector(LaraBOCollector<A, BO> scanner) {
		this.scanner = scanner;
	}

	/**
	 * Setter of the property <tt>bOChecker</tt>
	 */
	public void setBOPreselector(LaraBOPreselector<A, BO> checker) {
	}

	/**
	 * Setter of the property <tt>decisionModeSelector</tt>
	 * 
	 * @param decisionModeSelector
	 *            The decisionModeSelector to set.
	 * @uml.property name="decisionModeSelector"
	 */
	public void setModeSelector(LaraDecisionModeSelector<A> modeSelector) {
	}

	/**
	 * @param agent
	 */
	private void logBOs(Collection<BO> bos, String desc, A agent) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(desc + "for " + agent + ":"
				+ System.getProperty("line.separator"));
		int i = 0;
		for (BO bo : bos) {
			buffer.append(i + "th BO: " + bo
					+ System.getProperty("line.separator"));
			i++;
		}
		logger.debug(buffer);
	}
}
