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
package de.cesr.lara.components.decision;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.collections15.map.UnmodifiableMap;
import org.apache.log4j.Logger;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.preprocessor.LaraDecisionModeSelector;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;

/**
 * 
 * @author Sascha Holzhauer
 * @date 26.05.2010
 * 
 * @param <A>
 *            the agent type
 * @param <BO>
 *            the behavioural options type
 */
public final class LaraDecisionData<A extends LaraAgent<? super A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>> {

	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger
			.getLogger(LaraDecisionData.class);

	private final LaraDecisionConfiguration dConfiguration;
	private Collection<BO> bos;
	private Map<LaraPreference, Double> individualPreferenceWeights;

	private final A agent;

	/**
	 * A factory is used to prevent the process from storing LaraDecider objects
	 * which might cause side effects.
	 */
	LaraDeciderFactory<A, BO> deciderFactory;
	LaraDecider<BO> decider;

	/**
	 * @param dConfiguration
	 * @param agent
	 */
	public LaraDecisionData(LaraDecisionConfiguration dConfiguration, A agent) {
		this.dConfiguration = dConfiguration;
		this.agent = agent;
	}

	/**
	 * @return the BOs
	 */
	public Collection<BO> getBos() {
		return bos;
	}

	/**
	 * @return the dConfiguration
	 */
	public LaraDecisionConfiguration getdConfiguration() {
		return dConfiguration;
	}

	/**
	 * @return the current {@link LaraDecider} for the according decision
	 *         process
	 */
	public LaraDecider<BO> getDecider() {
		if (deciderFactory == null) {
			logger.error(agent + "> DeciderFactory must be set for decision " + dConfiguration + "!");
			throw new IllegalStateException(agent + "> DeciderFactory must be set for decision " + dConfiguration + "!");
		}
		/*
		 * Since one and the same decider needs to be persistent throughout a
		 * decision cycle because it stores important data, it may not be
		 * overwritten during a decision cycle (furthermore, it is quite costy
		 * to initialize it again and again). However, to prevent
		 * inconsistencies it needs to be guaranteed that the LaraDecisionData
		 * objects are deleted after the decision cycle. Otherwise, the decider
		 * object persists beyond one decision cycle which often is not desired,
		 * since in the next cycle a different decision mode is to be applied.
		 */
		if (decider == null) {
			decider = deciderFactory.getDecider(agent, dConfiguration);
		}
		return decider;
	}

	/**
	 * @return the deciderFactory
	 */
	public LaraDeciderFactory<A, BO> getDeciderFactory() {
		return deciderFactory;
	}

	/**
	 * @return the currentPreferences
	 */
	public Map<LaraPreference, Double> getIndividualPreferenceWeights() {
		if (individualPreferenceWeights == null) {
			// <- LOGGING
			logger.error(agent + "> Situational preference weights not set!");
			// LOGGING ->

			throw new IllegalStateException(agent
					+ "> Situational preference weights not set!");
		}
		return UnmodifiableMap.decorate(individualPreferenceWeights);
	}

	/**
	 * @param bo
	 *            list of BOs to set
	 */
	public void setBos(BO... bos) {
		if (logger.isDebugEnabled()) {
			logger.debug(agent);
		}

		// // <- LOGGING
		// if (logger.isDebugEnabled()) {
		// StringBuffer buffer = new StringBuffer();
		// buffer.append("Set BOs:" + System.getProperty("line.separator"));
		// for (BO bo : bos) {
		// buffer.append(bo + System.getProperty("line.separator"));
		// }
		// logger.debug(buffer.toString());
		// }
		// // LOGGING ->

		this.bos = Arrays.asList(bos);
	}

	/**
	 * @param bos
	 *            the BOs to set
	 */
	public void setBos(Collection<BO> bos) {
		if (logger.isDebugEnabled()) {
			logger.debug(agent);
		}
		// <- LOGGING
		// if (logger.isDebugEnabled()) {
		// StringBuffer buffer = new StringBuffer();
		// buffer.append("Set BOs:" + System.getProperty("line.separator"));
		// for (BO bo : bos) {
		// buffer.append(bo + System.getProperty("line.separator"));
		// }
		// logger.debug(buffer.toString());
		// }
		// LOGGING ->

		this.bos = bos;
	}

	/**
	 * Normally called by a {@link LaraDecisionModeSelector}.
	 * 
	 * @param deciderFactory
	 *            the deciderFactory to set
	 */
	public void setDeciderFactory(LaraDeciderFactory<A, BO> deciderFactory) {
		this.deciderFactory = deciderFactory;
		this.decider = null;

		// <- LOGGING
		// if (logger.isDebugEnabled()) {
		// logger.debug(this.dConfiguration + "> Set deciderFactory: "
		// + deciderFactory);
		// }
		// LOGGING ->
	}

	/**
	 * @param individualPreferenceWeights
	 *            the currentPreferences to set
	 */
	public void setIndividualPreferences(
			Map<LaraPreference, Double> individualPreferenceWeights) {
		this.individualPreferenceWeights = individualPreferenceWeights;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("DecisionData of " + agent + " for " + dConfiguration
				+ ". Selectable BO(s): ");
		if (bos != null) {
			for (BO b : bos) {
				buffer.append("\t" + b + System.getProperty("line.separator"));
			}
		}
		return buffer.toString();
	}
}