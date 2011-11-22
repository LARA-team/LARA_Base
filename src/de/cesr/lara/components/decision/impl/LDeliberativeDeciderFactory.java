/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 26.05.2010
 */
package de.cesr.lara.components.decision.impl;


import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecider;
import de.cesr.lara.components.decision.LaraDeciderFactory;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.LaraDeliberativeDecider;
import de.cesr.lara.components.util.impl.LPreferenceWeightMap;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;


/**
 * TODO consistent naming schema factory or builder TODO derive an AbstractLDeciderFactory that implements the
 * singleton!
 * 
 * @param <A>
 *        type of agent
 * @param <BO>
 *        type of behavioural option to deal with
 */
public class LDeliberativeDeciderFactory<A extends LaraAgent<? super A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
		implements LaraDeciderFactory<A, BO> {

	/**
	 * Logger
	 */
	static private Logger			logger	= Log4jLogger.getLogger(LDeliberativeDeciderFactory.class);

	static LaraDeciderFactory<?, ?>	factory	= null;
	static Class<?>					clazz;

	private LDeliberativeDeciderFactory() {
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
	 *        TODO make save
	 * 
	 * @return an instance of this factory
	 */
	@SuppressWarnings("unchecked")
	public static <A extends LaraAgent<? super A, BO>, BO extends LaraBehaviouralOption<?,? extends BO>> LaraDeciderFactory<A, BO> getFactory(
			Class<A> clazz) {
		if (LDeliberativeDeciderFactory.clazz != clazz || factory == null) {
			factory = new LDeliberativeDeciderFactory<A, BO>();
		}
		return (LDeliberativeDeciderFactory<A, BO>) factory;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraDeciderFactory#getDecider(de.cesr.lara.components.agents.LaraAgent,
	 *      de.cesr.lara.components.decision.LaraDecisionConfiguration)
	 *      
	 *     TODO naming: getDecider / return decision? (SH)
	 */
	@Override
	public LaraDecider<BO>  getDecider(A agent, LaraDecisionConfiguration dConfiguration) {

		Collection<BO> bos = agent.getLaraComp().getDecisionData(dConfiguration).getBos();
		if (bos != null) {

			if (logger.isDebugEnabled()) {
				StringBuffer buffer = new StringBuffer();
				for (BO bo : bos) {
					buffer.append(bo);
				}
				logger.debug("Context updated BOs: " + buffer.toString());
			}

			LaraDeliberativeDecider<BO> decision = new LDeliberativeDecider<BO>(dConfiguration);
			decision.setSelectableBOs(agent.getLaraComp().getDecisionData(dConfiguration).getBos());

			if (agent.getLaraComp().getDeliberativeChoiceComp(dConfiguration) == null) {
				throw new IllegalStateException("Heuristic component was not set in decision builder");
			}
			
			// TODO do associate Heurisitc (DecisionComponent) with agent instead of dConfiguration!
			decision.setDeliberativeChoiceComponent(agent.getLaraComp().getDeliberativeChoiceComp(dConfiguration));

			// send only preferenceWeights that are considered during the decision process:
			Map<Class<? extends LaraPreference>, Double> prefs = new LPreferenceWeightMap();

			if (dConfiguration.getPreferences() == null) {
				logger.error("No preferenceWeights set for LaraDecisionConfiguration " + dConfiguration);
			}

			for (Class<? extends LaraPreference> goal : dConfiguration.getPreferences()) {
				prefs.put(goal, agent.getLaraComp().getPreferenceWeights().get(goal));
			}
			decision.setPreferences(prefs);
			return decision;

		} else {
			logger.warn(agent.getAgentId() + "> Decision process for " + dConfiguration.getId()
					+ " cancelled because of empty set of BOs");
			return null;
		}
	}
}
