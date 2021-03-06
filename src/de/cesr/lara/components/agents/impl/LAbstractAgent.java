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
package de.cesr.lara.components.agents.impl;


import org.apache.log4j.Logger;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.agents.LaraAgentComponent;
import de.cesr.lara.components.environment.LaraEnvironment;
import de.cesr.lara.components.model.LaraModel;
import de.cesr.lara.components.util.logging.impl.LAgentLevel;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;


/**
 * 
 * @param <A>
 * @param <BO>
 *        the type of behavioural options the BO-memory of this agent may store
 * 
 * @author Sascha Holzhauer
 * @date 12.02.2010
 */
@SuppressWarnings("unchecked")
public abstract class LAbstractAgent<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, BO>> implements
		LaraAgent<A, BO> {

	static private Logger logger = Log4jLogger.getLogger(LAbstractAgent.class);

	static private int counter = 0;

	protected LaraModel lmodel;

	/**
	 * id as String
	 */
	protected String id;

	/**
	 * AgentComponent
	 */
	protected LaraAgentComponent<A, BO> agentComp;

	/**
	 * Instance specific logger allows to log a certain agent.
	 */
	private Logger agentLogger = null;

	/**
	 * Assigns an ID of the form agent000X where X is substituted by a counter
	 * value.
	 * 
	 * NOTE: The LARA implementation does not directly depend on the agent's
	 * {@link LaraEnvironment}. Therefore it does not cause problems to assign
	 * NULL.
	 * 
	 * @param lmodel
	 *            LaraModel
	 * @param env
	 *            the environment this agent is associated with
	 */
	public LAbstractAgent(LaraModel lmodel, LaraEnvironment env) {
		this(lmodel, env, "agent" + String.format("%1$04d", counter++));
	}

	/**
	 * NOTE: The LARA implementation does not directly depend on the agent's
	 * {@link LaraEnvironment}. Therefore it does not cause problems to assign
	 * NULL.
	 * 
	 * @param lmodel
	 *            LaraModel
	 * @param env
	 *            the environment this agent is associated with
	 * @param id
	 *            the ID this agent is identified with
	 */
	public LAbstractAgent(LaraModel lmodel, LaraEnvironment env, String id) {
		super();
		this.lmodel = lmodel;
		this.id = id;
		agentComp = new LDefaultAgentComp<>(this.lmodel, getThis(), env);

		// init agent specific logger (agent id is first part of logger name):
		if (Log4jLogger.getLogger(getAgentId() + "." + LAbstractAgent.class.getName()).isEnabledFor(LAgentLevel.AGENT)) {
			agentLogger = Log4jLogger.getLogger(getAgentId() + "." + LAbstractAgent.class.getName());
		}

		// <- LOGGING
		logger.info("Agent " + id + " initialised");
		if (Log4jLogger.getLogger(getAgentId() + "." + LAbstractAgent.class.getName()).isEnabledFor(LAgentLevel.AGENT)) {
			agentLogger.info("Agent " + id + " initialised");
		}
		// LOGGING ->
	}

	// //
	// Static methods
	// //

	/**
	 * Resets the counter used to label agents (agentID)
	 */
	public static void resetCounter() {
		counter = 0;
	}

	// //
	// Instance methods
	// //

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getAgentId();
	}

	/**
	 * Compares agent IDs
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof LAbstractAgent)) {
			return false;
		}
		return this.getAgentId().equals(((LAbstractAgent<A, BO>) o).getAgentId());
	}

	/**
	 * @return the agentID's hash code
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getAgentId().hashCode();
	}

	// //
	// GETTER and SETTER
	// //

	/**
	 * @see de.cesr.lara.components.agents.LaraAgent#getAgentId()
	 */
	@Override
	public final String getAgentId() {
		return this.id;
	}

	/**
	 * @see de.cesr.lara.components.agents.LaraAgent#getLaraComp()
	 */
	@Override
	public LaraAgentComponent<A, BO> getLaraComp() {
		try {
			return agentComp;
		} catch (ClassCastException e) {
			throw new ClassCastException("The Lara Component of this agent was not of requested parameter");
		}
	}

	/**
	 * Must be implemented in subclasses when the agent type parameter gets concrete.
	 * 
	 * @return reference to this object of the agent parameter's type
	 */
	abstract public A getThis();
}
