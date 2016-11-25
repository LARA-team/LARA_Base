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
package de.cesr.lara.components.decision.impl;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Element;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecider;
import de.cesr.lara.components.decision.LaraDeciderFactory;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.LaraDecisionModes;
import de.cesr.lara.components.decision.LaraDeliberativeDecider;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;

/**
 * @param <A>
 *            type of agent
 * @param <BO>
 *            type of behavioural option to deal with
 */
public class LDeliberativeDeciderFactory<A extends LaraAgent<? super A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
		implements LaraDeciderFactory<A, BO> {

	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger
			.getLogger(LDeliberativeDeciderFactory.class);

	static LaraDeciderFactory<?, ?> factory = null;
	static Class<?> clazz;

	@Element(required = false)
	protected boolean singleSelectedBoExpected = false;

	@Element(required = false)
	protected boolean neutralisePreferenceWeights = false;

	/**
	 * The problem: in java, non-static class AgentT (parameter) cannot be
	 * referenced from a static context, since there is only one class per class
	 * no matter how many parameters it has. On the other hand, for a singleton
	 * one needs to assure that it returns an instance of the requested
	 * parameter. In order to prevent from instancing a map of
	 * parameter-to-instance pairs we check if the current instance's parameter
	 * is valid by comparing the class object and create a new instance if not.
	 * 
	 * In case of many agent classes the map approach might be more efficient.
	 * For few agent classes it is unproblematic as long as agents of a certain
	 * class are executed in line and different agent classes are not mixed to
	 * often.
	 * 
	 * @param clazz
	 * @param <A>
	 *            agent type
	 * @param <BO>
	 *            BO type
	 * 
	 * @return an instance of this factory
	 */
	@SuppressWarnings("unchecked")
	public static <A extends LaraAgent<? super A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>> LaraDeciderFactory<A, BO> getFactory(
			Class<A> clazz) {
		if (LDeliberativeDeciderFactory.clazz != clazz || factory == null) {
			factory = new LDeliberativeDeciderFactory<A, BO>();
		}
		return (LDeliberativeDeciderFactory<A, BO>) factory;
	}

	private LDeliberativeDeciderFactory() {
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDeciderFactory#getDecider(de.cesr.lara.components.agents.LaraAgent,
	 *      de.cesr.lara.components.decision.LaraDecisionConfiguration)
	 */
	@Override
	public LaraDecider<BO> getDecider(A agent,
			LaraDecisionConfiguration dConfiguration) {

		Collection<BO> bos = agent.getLaraComp()
				.getDecisionData(dConfiguration).getBos();
		if (bos != null) {

			// <- LOGGING
			if (logger.isDebugEnabled()) {
				StringBuffer buffer = new StringBuffer();
				for (BO bo : bos) {
					buffer.append(bo);
				}
				logger.debug("Context updated BOs: " + buffer.toString());
			}
			// LOGGING ->

			LaraDeliberativeDecider<BO> decider = new LDeliberativeDecider<BO>(
dConfiguration, this.neutralisePreferenceWeights);
			decider.setSelectableBos(agent.getLaraComp()
					.getDecisionData(dConfiguration).getBos());

			if (agent.getLaraComp().getDeliberativeChoiceComp(
					agent.getLaraComp().getLaraModel(), dConfiguration) == null) {
				// <- LOGGING
				logger.error("Deliberative Choice component has not been set for LaraDecisionConfiguration "
						+ dConfiguration + " at the agent " + agent);
				// <- LOGGING
				throw new IllegalStateException(
						"Deliberative Choice component has not been set for LaraDecisionConfiguration "
								+ dConfiguration + " at the agent " + agent);
			}

			decider.setDeliberativeChoiceComponent(agent.getLaraComp()
					.getDeliberativeChoiceComp(
							agent.getLaraComp().getLaraModel(), dConfiguration));

			// send only preferenceWeights that are considered during the
			// decision process:
			if (dConfiguration.getPreferences() == null) {
				// <- LOGGING
				logger.warn("No preference weights set for LaraDecisionConfiguration "
						+ dConfiguration);
				// <- LOGGING
			}
			decider.setPreferenceWeights(agent.getLaraComp()
					.getPreferenceWeights());
			return decider;

		} else {
			logger.warn(agent.getAgentId() + "> Decision process for "
					+ dConfiguration.getId()
					+ " cancelled because of empty set of BOs");

			return new LNoOptionDecider<BO>(LaraDecisionModes.DELIBERATIVE);
		}
	}
}
