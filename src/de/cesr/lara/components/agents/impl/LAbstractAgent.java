/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * 
 */
package de.cesr.lara.components.agents.impl;

import org.apache.log4j.Logger;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.agents.LaraAgentComponent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.LaraDecisionData;
import de.cesr.lara.components.environment.LaraEnvironment;
import de.cesr.lara.components.util.logging.impl.LAgentLevel;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;

/**
 * TODO logging
 * 
 * @param <A>
 * @param <BO>
 *            the type of behavioural options the BO-memory of this agent may
 *            store
 * 
 * @author Sascha Holzhauer
 * @date 12.02.2010
 */
@SuppressWarnings("unchecked")
public abstract class LAbstractAgent<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<? super A, BO>>
		implements LaraAgent<A, BO> {

	private static int counter = 0;
	static private Logger logger = Log4jLogger.getLogger(LAbstractAgent.class);

	/**
	 * Resets the counter used to label agents (agentID)
	 */
	public static void resetCounter() {
		counter = 0;
	}

	private Logger agentLogger = null;

	/**
	 * AgentComponent
	 */
	protected LaraAgentComponent<A, BO> agentComp;

	/**
	 * id as String
	 */
	protected String id;

	/**
	 * @param env
	 * 
	 */
	public LAbstractAgent(LaraEnvironment env) {
		this(env, "agent" + String.format("%1$04d", counter++));
	}

	/**
	 * To initialise the {@link LaraAgentComponent} properly the id needs to
	 * passed to this constructor.
	 * 
	 * @param env
	 * @param id
	 */
	public LAbstractAgent(LaraEnvironment env, String id) {
		super();
		this.id = id;
		agentComp = new LDefaultAgentComp<A, BO>(getThis(), env);

		// init agent specific logger (agent id is first part of logger name):
		if (Log4jLogger.getLogger(
				getAgentId() + "." + LAbstractAgent.class.getName())
				.isEnabledFor(LAgentLevel.AGENT)) {
			agentLogger = Log4jLogger.getLogger(getAgentId() + "."
					+ LAbstractAgent.class.getName());
		}

		// <- LOGGING
		logger.info("Agent " + id + " initialised");
		if (Log4jLogger.getLogger(
				getAgentId() + "." + LAbstractAgent.class.getName())
				.isEnabledFor(LAgentLevel.AGENT)) {
			agentLogger.info("Agent " + id + " initialised");
		}
		// LOGGING ->
	}

	// //
	// Implementing methods of LaraAgent
	// //

	/**
	 * Removes the {@link LaraDecisionData} according to the specified
	 * {@link LaraDecisionConfiguration}.
	 * 
	 * @see de.cesr.lara.components.agents.LaraAgent#clean(de.cesr.lara.components.decision.LaraDecisionConfiguration)
	 */
	public final void clean(LaraDecisionConfiguration dConfiguration) {
		agentComp.removeDecisionData(dConfiguration);
		customClean(dConfiguration);
	}

	/**
	 * Called by {@link LAbstractAgent#clean(LaraDecisionConfiguration)} Created
	 * by Sascha Holzhauer on 12.10.2010
	 * 
	 * @param dConfiguration
	 */
	public void customClean(LaraDecisionConfiguration dConfiguration) {
		// hook method
	}

	/**
	 * Compares the agent IDs
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof LAbstractAgent)) {
			return false;
		}
		return this.getAgentId().equals(((LAbstractAgent) o).getAgentId());
	}

	/**
	 * @see de.cesr.lara.components.agents.LaraAgent#getAgentId()
	 */
	@Override
	public final String getAgentId() {
		return this.id;
	}

	// //
	// Implementing methods of LaraAgent
	// //

	/**
	 * @see de.cesr.lara.components.agents.LaraAgent#getLaraComp()
	 */
	@Override
	public LaraAgentComponent<A, BO> getLaraComp() {
		try {
			return agentComp;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					"The Lara Component of this agent was not of requested parameter");
		}
	}

	/**
	 * Must be implemented in subclasses when the agent type parameter gets
	 * concrete.
	 * 
	 * @return reference to this object of the agent parameter's type
	 */
	abstract public A getThis();

	// //
	// Overriding default methods
	// //

	/**
	 * @return the agentID's hash code
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getAgentId().hashCode();
	}

	/**
	 * hook method...
	 * 
	 * @see de.cesr.lara.components.agents.LaraAgent#laraExecute(de.cesr.lara.components.decision.LaraDecisionConfiguration)
	 */
	public void laraExecute(LaraDecisionConfiguration dConfiguration) {
		// nothing to do since this is a hook method
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getAgentId();
	}
}
