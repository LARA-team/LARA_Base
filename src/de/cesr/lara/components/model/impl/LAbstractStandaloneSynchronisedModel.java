/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 09.11.2009
 */
package de.cesr.lara.components.model.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;

/**
 * The class provides a default implementation of {@link LModel} that does not
 * depend on any MAS framework. The procedures <code>perceive()</code>,
 * <code>preProcess()</code> and <code>decide()</code> are executed for all
 * agents in (kind of) parallel. The <code>AbstractSandaloneParallelModel</code>
 * cares about agent organisation.
 * 
 * @author Sascha Holzhauer
 * @date 09.11.2009
 * 
 */
public abstract class LAbstractStandaloneSynchronisedModel extends
		LAbstractModel {

	private Logger logger = Log4jLogger
			.getLogger(LAbstractStandaloneSynchronisedModel.class);

	/**
	 * provides access to the agents via the interface AgentToModel
	 */
	protected Collection<LaraAgent<?, ?>> agents = new ArrayList<LaraAgent<?, ?>>();

	/**
	 * Constructor
	 */
	public LAbstractStandaloneSynchronisedModel() {
		super();
	}

	/**
	 * 
	 * @param agent
	 */
	public void addAgent(LaraAgent<?, ?> agent) {
		agents.add(agent);
	}

	/**
	 * 
	 * @see de.cesr.lara.components.model.impl.LAbstractModel#init()
	 */
	@Override
	public void init() {
		super.init();
		logger = Log4jLogger
				.getLogger(LAbstractStandaloneSynchronisedModel.class);
		agents = new ArrayList<LaraAgent<?, ?>>();

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("LAbstractStandaloneSynchronisedModel " + this
					+ " initialised.");
		}
		// LOGGING ->
	}

	/**
	 * 
	 * @see de.cesr.lara.components.model.impl.LAbstractModel#decide(de.cesr.lara.components.decision.LaraDecisionConfiguration)
	 */
	// @Override
	// public void decide(LaraDecisionConfiguration decisionBuilder) {
	// for (LaraAgent<?,?> a : agents) {
	// a.getLaraComp().decide(decisionBuilder);
	// }
	// }
	//
	// /**
	// *
	// * @see
	// de.cesr.lara.components.model.impl.LAbstractModel#perceive(de.cesr.lara.components.decision.LaraDecisionConfiguration)
	// */
	// @Override
	// protected void perceive(LaraDecisionConfiguration dConfiguration) {
	// for (LaraAgent<?,?> a : agents) {
	// a.laraPerceive(dConfiguration);
	// }
	// }
	//
	// /**
	// *
	// * @see
	// de.cesr.lara.components.model.impl.LAbstractModel#preProcess(de.cesr.lara.components.decision.LaraDecisionConfiguration)
	// */
	// @Override
	// protected void preProcess(LaraDecisionConfiguration dConfiguration) {
	// for (LaraAgent<?,?> a : agents) {
	// a.getLaraComp().preProcess(dConfiguration);
	// }
	// }
	//
	// /**
	// * @see
	// de.cesr.lara.components.model.impl.LAbstractModel#execute(de.cesr.lara.components.decision.LaraDecisionConfiguration)
	// */
	// @Override
	// protected void postProcess(LaraDecisionConfiguration dConfiguration) {
	// for (LaraAgent<?,?> a : agents) {
	// a.laraExecute(dConfiguration);
	// }
	// }
	//
	// @Override
	// protected void clean(LaraDecisionConfiguration dConfiguration) {
	// for (LaraAgent<?,?> a : agents) {
	// a.clean(dConfiguration);
	// }
	// }
}