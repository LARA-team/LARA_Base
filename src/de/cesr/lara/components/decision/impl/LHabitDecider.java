/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 16.08.2010
 */
package de.cesr.lara.components.decision.impl;


import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecider;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.postprocessor.impl.LSelectedBoProperty;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;

/**
 * 
 * TODO implement decide() TODO implement methods to retrieve last property from
 * general memory TODO implement method to check whether LSelectedBoPropertyx
 * exists for last tick TODO implement getter/setter for postProcessor-Component
 * TODO set default postProcessor-Component TODO doc
 * 
 * @param <A>
 *            type of agent
 * @param <BO>
 *            type of behavioural option
 * 
 */
public class LHabitDecider<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>> implements
		LaraDecider<BO> {

	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger.getLogger(LHabitDecider.class);

	A										agent	= null;
	BO										bo		= null;
	LaraDecisionConfiguration	dConfiguration;

	/**
	 * @param agent
	 * @param dConfiguration
	 */
	public LHabitDecider(A agent, LaraDecisionConfiguration dConfiguration) {
		this.agent = agent;
		this.dConfiguration = dConfiguration;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDecider#decide()
	 */
	@Override
	public void decide() {
		if (agent.getLaraComp().getGeneralMemory()
				.contains(dConfiguration.getId())) {
			bo = (BO) agent.getLaraComp().getGeneralMemory()
					.recall(LSelectedBoProperty.class, dConfiguration.getId())
					.getValue();
		} else {
			// <- LOGGING
			logger.warn("Habitual behaviour could not select a behavioural option!");
			// LOGGING ->
		}
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDecider#getKSelectedBos(int)
	 */
	@Override
	public Set<BO> getKSelectedBos(int k) {
		Set<BO> bos = new HashSet<BO>(1);
		return bos;
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
		return bo;
	}

	/**
	 * Return the name of this decider and the {@link LaraDecisionConfiguration}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LHabitDecider for " + dConfiguration;
	}
}
