/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 16.08.2010
 */
package de.cesr.lara.components.decision.impl;


import java.util.HashSet;
import java.util.Set;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecider;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;


/**
 * @param <A>
 *        type of agent
 * @param <BO>
 *        type of behavioural option
 * 
 */
public class LHabitDecider<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>> implements
		LaraDecider<BO> {

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
		// TODO fetch BO from LAST time step!
		bo = agent.getLaraComp().getDecisionData(dConfiguration).getBo();
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
