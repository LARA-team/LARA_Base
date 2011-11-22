/**
 * LARA
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 25.06.2009
 */
package de.cesr.lara.components.preprocessor.impl;


import java.text.DecimalFormat;
import java.util.Collection;

import org.apache.log4j.Logger;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.preprocessor.LaraBOCollector;
import de.cesr.lara.components.preprocessor.LaraBOPreselector;
import de.cesr.lara.components.preprocessor.LaraBOUtilityUpdater;
import de.cesr.lara.components.preprocessor.LaraDecisionModeSelector;
import de.cesr.lara.components.preprocessor.LaraPreferenceUpdater;
import de.cesr.lara.components.preprocessor.LaraPreprocessor;
import de.cesr.lara.components.util.logging.impl.LAgentLevel;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;


/**
 * Default implementation of {@link LaraPreprocessor}. Supports comprehensive logging.
 * 
 * TODO are getters/setters required?? (SH)
 * 
 * @param <A>
 *        agent type
 * @param <BO>
 *        the type of behavioural options the preprocessor shall manage
 * 
 * @author Sascha Holzhauer
 * @date 25.06.2009
 */
public class LPreprocessor<A extends LaraAgent<? super A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>> implements
		LaraPreprocessor<A, BO> {

	static private Logger					logger		= Log4jLogger.getLogger(LPreprocessor.class);
	private Logger							agentLogger	= null;

	/*
	 * <------- PROPERTIES ------------>
	 */

	/**
	 * @uml.property name="decisionModeSelector"
	 */
	protected LaraDecisionModeSelector<A, BO>	decisionModeSelector;

	/**
	 * @uml.property name="bOScanner"
	 */
	protected LaraBOCollector<A, BO>		collector;

	/**
	 * @uml.property name="bOChecker"
	 */
	protected LaraBOPreselector<A, BO>		preselector;

	/**
	 * @uml.property name="bOAdapter"
	 */

	protected LaraBOUtilityUpdater<A, BO>	utilityUpdater;

	/**
	 * preference utilityUpdater
	 */
	protected LaraPreferenceUpdater<A>		preferenceUpdater;

	/**
	 * decision builder
	 */
	protected LaraDecisionConfiguration			dConfiguration;

	/*
	 * <------- CONSTRUCTORS ------------>
	 */

	/**
	 * @param selector
	 * @param collector
	 * @param preselector
	 * @param utilityUpdater
	 * @param preferenceUpdater
	 * @param dConfiguration
	 */
	public LPreprocessor(LaraDecisionModeSelector<A, BO> selector, LaraBOCollector<A, BO> collector,
			LaraBOPreselector<A, BO> preselector, LaraBOUtilityUpdater<A, BO> updater,
			LaraPreferenceUpdater<A> preferenceUpdater, LaraDecisionConfiguration dConfiguration) {
		this.decisionModeSelector = selector;
		this.collector = collector;
		this.preselector = preselector;
		this.utilityUpdater = updater;
		this.preferenceUpdater = preferenceUpdater;
		this.dConfiguration = dConfiguration;
		logger.debug("Scanner " + collector + " for dConfiguration " + dConfiguration.getId());
	}

	/*
	 * <------- METHODS ------------>
	 */

	/**
	 * 
	 * @see de.cesr.lara.components.preprocessor.LaraPreprocessor#preprocess(de.cesr.lara.components.preprocessor.LaraPreprocessor.Accuracy,
	 *      de.cesr.lara.components.agents.LaraAgent)
	 */
	public void preprocess(LaraBOPreselector.Accuracy accuracy, A agent) {
		Collection<BO> bos;

		// <- LOGGING
		// init agent specific logger (agent id is first part of logger name):
		if (Log4jLogger.getLogger(agent.getAgentId() + "." + LPreprocessor.class.getName()).isEnabledFor(
				LAgentLevel.AGENT)) {
			agentLogger = Log4jLogger.getLogger(agent.getAgentId() + "." + LPreprocessor.class.getName());
		}

		if (agentLogger != null) {
			agentLogger.debug(agent + "> preprocess");

		} else {
			logger.info(agent + "> preprocess");
		}

		if (logger.isDebugEnabled() || agentLogger != null) {
			bos = agent.getLaraComp().getBOMemory().recallAllMostRecent();
			logBOs(bos, "BOs in memory - ", agent);
		}
		// LOGGING ->

		// Mode Selection:
		if (decisionModeSelector != null) {
			decisionModeSelector.selectDecisionMode(agent, dConfiguration);
		}

		// BO Scanning:
		if (collector != null) {
			bos = collector.collectBOs(agent, agent.getLaraComp().getBOMemory(), dConfiguration);
		} else {
			bos = agent.getLaraComp().getBOMemory().recallAllMostRecent();
		}

		if (logger.isDebugEnabled() || agentLogger != null)
			logBOs(bos, "BOs from collector - ", agent);

		// BO Updating:
		if (utilityUpdater != null) {
			bos = utilityUpdater.updateBOUtilities(agent, bos, dConfiguration);
		}

		if (logger.isDebugEnabled() || agentLogger != null)
			logBOs(bos, "Updated BOs - ", agent);

		// BO Checking:
		if (preselector != null) {
			bos = preselector.preselectBOs(agent, bos);
		}

		if (logger.isDebugEnabled() || agentLogger != null)
			logBOs(bos, "Checked BOs - ", agent);

		agent.getLaraComp().getDecisionData(dConfiguration).setBos(bos);

		// Agent Preference Updating:
		if (preferenceUpdater != null) {
			preferenceUpdater.updatePreferenceWeights(agent, dConfiguration);
		}
	}

	/**
	 * Logs BOs to agentLogger if its not null and to logger if debug is enabled otherwise.
	 * 
	 * @param bos
	 *        BOs to log
	 * @param desc
	 *        description that comments to BOs origin
	 * @param agent
	 */
	private void logBOs(Collection<BO> bos, String desc, A agent) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(desc + "for " + agent + ":" + System.getProperty("line.separator"));
		int i = 0;
		for (BO bo : bos) {
			buffer.append(new DecimalFormat("00").format(i) + "th BO: " + bo + System.getProperty("line.separator"));
			i++;
		}
		if (agentLogger != null) {
			agentLogger.debug(buffer);
		} else if (logger.isDebugEnabled()) {
			logger.debug(buffer);
		}
	}
}