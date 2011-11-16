/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Friedrich Krebs on 17.09.2010
 */
package de.cesr.lara.components.decision.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecider;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.model.impl.LModel;
import de.cesr.lara.components.util.LaraRandom;
import de.cesr.lara.components.util.logging.impl.LAgentLevel;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;

/**
 * @param <A>
 *            type of agent
 * @param <BO>
 *            type of behavioural options
 * 
 */
public class LExplorationDecider<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<? super A, BO>>
		implements LaraDecider<BO> {

	private Logger agentLogger = null;
	/**
	 * logger
	 */
	private Logger logger = Log4jLogger.getLogger(LExplorationDecider.class);

	A agent = null;
	BO bo = null;
	LaraDecisionConfiguration dConfiguration;
	LaraRandom random;

	/**
	 * @param agent
	 * @param dConfiguration
	 */
	public LExplorationDecider(A agent, LaraDecisionConfiguration dConfiguration) {
		this.agent = agent;
		this.dConfiguration = dConfiguration;

		this.random = LModel.getModel().getLRandom();

		// init agent specific logger (agent id is first part of logger name):
		if (Log4jLogger.getLogger(
				agent.getAgentId() + "." + LExplorationDecider.class.getName())
				.isEnabledFor(LAgentLevel.AGENT)) {
			agentLogger = Log4jLogger.getLogger(agent.getAgentId() + "."
					+ LExplorationDecider.class.getName());
		}
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDecider#decide()
	 */
	@Override
	public void decide() {
		int randomBOnum = random.getUniform().nextIntFromTo(
				0,
				agent.getLaraComp().getDecisionData(dConfiguration).getBos()
						.size() - 1);
		// TODO switch to BO lists
		this.bo = (BO) agent.getLaraComp().getDecisionData(dConfiguration)
				.getBos().toArray()[randomBOnum];

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			StringBuffer buffer = new StringBuffer();
			for (BO bo : agent.getLaraComp().getDecisionData(dConfiguration)
					.getBos()) {
				buffer.append("\t" + bo + "\n");
			}
			logger.debug("BOs to explore: " + buffer.toString());
		}
		logger.info(agent + "> Explored BO: " + bo);
		if (agentLogger != null) {
			agentLogger.debug(agent + "> Explored BO:\n" + bo);
		}
		// LOGGING ->
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDecider#getKSelectedBos(int)
	 */
	@Override
	public Set<BO> getKSelectedBos(int k) {
		if ((k != Integer.MAX_VALUE) && (k != 1)) {
			throw new IllegalStateException("The number of requested BOs (" + k
					+ ") is larger than the number of available BOs (1)!");
		}
		Set<BO> bos = new HashSet<BO>(1);
		bos.add(this.bo);
		return bos;
	}

	@Override
	public Set<? extends BO> getKSelectedBosSituational(int k) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDecider#getNumSelectableBOs()
	 */
	@Override
	public int getNumSelectableBOs() {
		return 1;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDecider#getSelectedBo()
	 */
	@Override
	public BO getSelectedBo() {
		return this.bo;
	}

	@Override
	public BO getSelectedBoSituational() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Return the name of this decider and the {@link LaraDecisionConfiguration}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LExplorationDecider for " + dConfiguration;
	}
}
