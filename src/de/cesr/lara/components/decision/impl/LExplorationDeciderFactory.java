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

import org.apache.log4j.Logger;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecider;
import de.cesr.lara.components.decision.LaraDeciderFactory;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;

/**
 * This decider factory creates a decider that just fetches the aagent's
 * preceding behavioural actions and chooses it as new action. TODO when the
 * model selector is able to define/alter the course of decision making, this
 * steps of the factory and decider might be omitted. However, it could be
 * useful to evaluate not only the last but the k last actions...
 * 
 * @author Friedrich Krebs, Sascha Holzhauer
 * @date 17.09.2010
 * 
 * @param <A>
 *            type of agent
 * @param <BO>
 *            type of behavioural option
 */
public class LExplorationDeciderFactory<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
		implements LaraDeciderFactory<A, BO> {

	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger
			.getLogger(LExplorationDeciderFactory.class);

	static LaraDeciderFactory<?, ?> factory = null;
	static Class<?> clazz;

	/**
	 * The problem: In java, non-static class AgentT (parameter) cannot be
	 * referenced from a static context, since there is only one class per class
	 * no matter how many parameters it has. On the other hand, for a singleton
	 * one needs to assure that it returns an instance of the requested
	 * parameter. In order to prevent from instancing a map of
	 * parameter-to-instance pairs we check if the current instance's parameter
	 * is valid by comparing the class object and create a new instance if not.
	 * In case of many agent classes the map approach might be more efficient.
	 * For few agent classes it is unproblematic as long as agents of a certain
	 * class are executed in line and different agent classes are not mixed to
	 * often.
	 * 
	 * @param clazz
	 * @param <A>
	 * @param <BO>
	 * 
	 * @return an instance of this factory
	 */
	@SuppressWarnings("unchecked")
	// if static instance has wrong type a new instance is created
	public static <A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>> LaraDeciderFactory<A, BO> getFactory(
			Class<A> clazz) {
		if (LExplorationDeciderFactory.clazz != clazz || factory == null) {
			// <- LOGGING
			logger.info("New instance created for type " + clazz.getName());
			// LOGGING ->

			factory = new LExplorationDeciderFactory<A, BO>();
		}
		return (LExplorationDeciderFactory<A, BO>) factory;
	}

	private LExplorationDeciderFactory() {
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDeciderFactory#getDecider(de.cesr.lara.components.agents.LaraAgent,
	 *      de.cesr.lara.components.decision.LaraDecisionConfiguration)
	 */
	@Override
	public LaraDecider<BO> getDecider(A agent,
			LaraDecisionConfiguration dConfiguration) {
		return new LExplorationDecider<A, BO>(agent, dConfiguration);
	}
}
