/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Friedrich Krebs on 17.09.2010
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
 * This decider factory creates a decider that just fetches the aagent's preceding behavioural actions and chooses it as
 * new action. TODO when the model selector is able to define/alter the course of decision making, this steps of the
 * factory and decider might be omitted. However, it could be useful to evaluate not only the last but the k last
 * actions...
 * 
 * @param <A>
 *        type of agent
 * @param <BO>
 *        type of behavioural option
 */
public class LExplorationDeciderFactory<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>> implements
		LaraDeciderFactory<A, BO> {

	/**
	 * Logger
	 */
	static private Logger			logger	= Log4jLogger.getLogger(LExplorationDeciderFactory.class);

	static LaraDeciderFactory<?, ?>	factory	= null;
	static Class<?>					clazz;

	private LExplorationDeciderFactory() {
	}

	/**
	 * The problem: in java, non-static class AgentT (parameter) cannot be referenced from a static context, since there
	 * is only one class per class no matter how many parameters it has. On the other hand, for a singleton one needs to
	 * assure that it returns an instance of the requested parameter. In order to prevent from instancing a map of
	 * parameter-to-instance pairs we check if the current instance's parameter is valid by comparing the class object
	 * and create a new instance if not. In case of many agent classes the map approach might be more efficient. For few
	 * agent classes it is unproblematic as long as agents of a certain class are executed in line and different agent
	 * classes are not mixed to often.
	 * 
	 * @param clazz
	 * @param <A>
	 * @param <BO>
	 * 
	 * @return an instance of this factory
	 */
	@SuppressWarnings("unchecked")
	public static <A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>> LaraDeciderFactory<A, BO> getFactory(
			Class<A> clazz) {
		if (LExplorationDeciderFactory.clazz != clazz || factory == null) {
			factory = new LExplorationDeciderFactory<A, BO>();
		}
		return (LExplorationDeciderFactory<A, BO>) factory;
	}

	@Override
	public LaraDecider<BO> getDecider(A agent, LaraDecisionConfiguration dConfiguration) {
		return new LExplorationDecider<A, BO>(agent, dConfiguration);
	}

}
