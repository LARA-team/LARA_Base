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
package de.cesr.lara.components.agents;


import java.util.Map;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.LaraProperty;
import de.cesr.lara.components.container.memory.LaraBOMemory;
import de.cesr.lara.components.container.memory.LaraMemory;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.LaraDecisionData;
import de.cesr.lara.components.decision.LaraDeliberativeChoiceComponent;
import de.cesr.lara.components.environment.LaraEnvironment;
import de.cesr.lara.components.environment.LaraEnvironmentListener;
import de.cesr.lara.components.eventbus.LaraInternalEventSubscriber;
import de.cesr.lara.components.postprocessor.LaraPostprocessorComp;
import de.cesr.lara.components.preprocessor.LaraPreprocessor;
import de.cesr.lara.components.util.impl.LPrefEntry;


/**
 * Agent component for LARA specific processes and properties.
 * 
 * @param <A>
 *        type of agent this LARA component belongs to
 * @param <BO>
 *        type of behavioural options memorised in the BO-memory
 * 
 * @author Sascha Holzhauer
 * @date 17.12.2009
 */
public interface LaraAgentComponent<A extends LaraAgent<? super A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
		extends LaraEnvironmentListener, LaraInternalEventSubscriber {

	/**
	 * Set the agent's preferenceWeights towards its preferences.
	 * 
	 * @param prefEntry
	 */
	public void addPreferenceWeights(LPrefEntry... prefEntry);

	/**
	 * Set the agent's preference weights towards its preferences.
	 * 
	 * @param preferenceWeights
	 *        the preferenceWeights to set
	 */
	public void addPreferenceWeights(Map<Class<? extends LaraPreference>, Double> preferenceWeights);

	/**
	 * Executes decision making for the given {@link LaraDecisionConfiguration}: Simulates the agent's decision making
	 * process.
	 * 
	 * @param dConfiguration
	 *        decision builder that identifies the decision to execute
	 */
	public void decide(LaraDecisionConfiguration dConfiguration);

	/*****************************
	 * DECISION DATA
	 *****************************/

	/**
	 * Removes the {@link LaraDecisionData} that is associated with the specified {@link LaraDecisionConfiguration}.
	 * 
	 * @param dConfiguration
	 */
	public void removeDecisionData(LaraDecisionConfiguration dConfiguration);

	/**
	 * Returns the {@link LaraDecisionData} object associated with the given {@link LaraDecisionConfiguration}. If the
	 * object does not exist, it is created.
	 * 
	 * NOTE: The LDC parameter should not be bounded since it would require the LaraBehaviouralOption (which calls this
	 * method) to restricts its agent parameter's BO parameter.
	 * 
	 * @param dConfiguration
	 * @return the decision data associated with the given decision builder.
	 */
	public LaraDecisionData<A, BO> getDecisionData(LaraDecisionConfiguration dConfiguration);

	/**
	 * Returns an iterator that iterates over all {@link LaraDecisionData}s.
	 * 
	 * @return the iterator
	 */
	public Iterable<LaraDecisionData<A, BO>> getDecisionDataIterable();

	/*****************************
	 * SET COMPONENTS
	 *****************************/

	/**
	 * Set the agent's behavioural options memory
	 * 
	 * @param boMemory
	 *        the new BO memory
	 */
	public void setBOMemory(LaraBOMemory<BO> boMemory);

	/**
	 * Sets the {@link LaraDeliberativeChoiceComponent} that shall be used for the given decision configuration.
	 * 
	 * Note: if possible, use the same instance of the LaraDeliberativeChoiceComponent for all agents to save memory.
	 * 
	 * @param dConfiguration
	 * @param comp
	 */
	public void setDeliberativeChoiceComp(LaraDecisionConfiguration dConfiguration, LaraDeliberativeChoiceComponent comp);

	/**
	 * Set the agent's general property memory
	 * 
	 * @param memory
	 *        agent's general property memory
	 */
	public void setGeneralMemory(LaraMemory<LaraProperty<?, ?>> memory);

	/**
	 * Sets the post-processor component.
	 * 
	 * @param postprocesor
	 */
	public void setPostProcessor(LaraPostprocessorComp<A, BO> postprocesor);

	/**
	 * Set the agent's pre-processor. Since the preprocessor itself differentiates between
	 * {@link LaraDecisionConfiguration}s there is only one preprocessor to assign for all decisions.
	 * 
	 * @param preprocessor
	 */
	public void setPreprocessor(LaraPreprocessor<A, BO> preprocessor);

	/*****************************
	 * Further GETTER and SETTER
	 *****************************/
	
	/**
	 * Get the {@link LaraBOMemory} (for behavioural options) of this agent.
	 * 
	 * @return agent's behavioural option memory
	 */
	public LaraBOMemory<BO> getBOMemory();

	/**
	 * Get the environment this agent belongs to by its type
	 * 
	 * @return environment the agent is embedded in
	 */
	public LaraEnvironment getEnvironment();

	/**
	 * Get the {@link LaraMemory} (for general properties) of this agent.
	 * 
	 * @return agent's general property memory
	 */
	public LaraMemory<LaraProperty<?, ?>> getGeneralMemory();

	/**
	 * The number of {@link LaraDecisionData} objects currently stored at the agent.
	 * 
	 * @return the number of decision data objects
	 */
	public int getNumDecisionDataObjects();

	/**
	 * @param preference
	 * @return agent's preference weight regarding the given preference
	 */
	public Double getPreferenceWeight(Class<? extends LaraPreference> preference);

	/**
	 * @return a map of the agents preferenceWeights
	 * 
	 */
	public Map<Class<? extends LaraPreference>, Double> getPreferenceWeights();

	/**
	 * Returns the {@link LaraDeliberativeChoiceComponent} that shall be used for the given decision configuration.
	 * 
	 * @param dConfiguration
	 * @return
	 */
	public LaraDeliberativeChoiceComponent getDeliberativeChoiceComp(LaraDecisionConfiguration dConfiguration);
}