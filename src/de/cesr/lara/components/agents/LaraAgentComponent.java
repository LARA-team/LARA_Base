/**
 * LARA - Lightweight Architecture for bounded Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 17.12.2009
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
import de.cesr.lara.components.preprocessor.LaraPreprocessorFactory;


/**
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
		extends LaraEnvironmentListener {

	/**
	 * Do the pre-processing for a decision: Scan memory for BOs, check BOs and adapt them. Also updates the agent's
	 * situational preferenceWeights. *
	 * 
	 * @param dConfiguration
	 *        decision builder that identifies the decision to pre-process
	 */
	public void preProcess(LaraDecisionConfiguration dConfiguration);

	/**
	 * Executes decision making for the given {@link LaraDecisionConfiguration}: Simulates the agent's decision making
	 * process.
	 * 
	 * @param dConfiguration
	 *        decision builder that identifies the decision to execute
	 */
	public void decide(LaraDecisionConfiguration dConfiguration);

	/**
	 * Set the agent's pre-processor.
	 * 
	 * @param preProcessorFactory
	 * 
	 */
	public void setPreProcessorFactory(LaraPreprocessorFactory<A, BO> preProcessorFactory);

	/**
	 * Get the environment this agent belongs to by its type
	 * 
	 * @return environment the agent is embedded in
	 */

	public LaraEnvironment getEnvironment();

	/*****************************
	 * MEMORY MANGEMENT
	 *****************************/

	/**
	 * Get the {@link LaraMemory} (for general properties) of this agent.
	 * 
	 * @return agent's general property memory
	 */
	public LaraMemory<LaraProperty<?>> getGeneralMemory();

	/**
	 * Set the agent's general property memory
	 * 
	 * @param memory
	 *        agent's general property memory
	 */
	public void setGeneralMemory(LaraMemory<LaraProperty<?>> memory);

	/**
	 * Get the {@link LaraBOMemory} (for behavioural options) of this agent.
	 * 
	 * @return agent's behavioural option memory
	 */
	public LaraBOMemory<BO> getBOMemory();

	/**
	 * Set the agent's behavioural options memory
	 * 
	 * @param boMemory
	 *        the new BO memory
	 */
	public void setBOMemory(LaraBOMemory<BO> boMemory);

	/*****************************
	 * PREFERENCE MANGEMENT
	 *****************************/

	/**
	 * Set the agent's preferenceWeights towards its preferenceWeights. Giving <code>null</code> as dConfiguration sets the default.
	 * 
	 * @param preferenceWeights
	 *        the preferenceWeights to set
	 */
	public void addPreferenceWeights(Map<Class<? extends LaraPreference>, Double> preferenceWeights);

	/**
	 * @param preference
	 * @return agent's preference weight regarding the given preferenceWeights
	 */
	public Double getPreferenceWeight(Class<? extends LaraPreference> preference);

	/**
	 * @return a map of the agents decision builders an its corresponding preferenceWeights
	 * 
	 */
	public Map<Class<? extends LaraPreference>, Double> getPreferenceWeights();

	/*****************************
	 * PROPERTY MANGEMENT
	 *****************************/

	/**
	 * @param name
	 *        the property's name
	 * @return the double value of that property - NaN if property does not exist
	 */
	public double getDoubleProperty(String name);

	/**
	 * @param name
	 *        the name of existing or new property
	 * @param value
	 *        the property's (new) value
	 */
	public void setDoubleProperty(String name, double value);

	/*****************************
	 * DECISION DATA
	 *****************************/

	/**
	 * Returns the {@link LaraDecisionData} object associated with the given {@link LaraDecisionConfiguration}. If the object
	 * does not exist, it is created.
	 * 
	 * NOTE: The LDC parameter should not be bounded since it would require the LaraBehaviouralOption (which calls this method)
	 * to restricts its agent parameter's BO parameter.
	 * 
	 * @param dConfiguration
	 * @return the decision data associated with the given decision builder.
	 */
	public LaraDecisionData<A, BO> getDecisionData(LaraDecisionConfiguration dConfiguration);
	
	
	/**
	 * Returns the {@link LaraDeliberativeChoiceComponent} that shall be used for the given
	 * decision configuration.
	 * 
	 * @param dConfiguration
	 * @return
	 */
	public LaraDeliberativeChoiceComponent getDeliberativeChoiceComp(LaraDecisionConfiguration dConfiguration);
	
	
	/**
	 * Sets the {@link LaraDeliberativeChoiceComponent} that shall be used for the given
	 * decision configuration.
	 * 
	 * @param dConfiguration
	 * @param comp
	 */
	public void setDeliberativeChoiceComp(LaraDecisionConfiguration dConfiguration, LaraDeliberativeChoiceComponent comp);

	/**
	 * The number of {@link LaraDecisionData} objects currently stored at the agent.
	 * 
	 * @return the number of decision data objects
	 */
	public int getNumDecisionDataObjects();

	/**
	 * Returns an iterator that iterates over all {@link LaraDecisionData}.
	 * 
	 * @return the iterator
	 */
	public Iterable<LaraDecisionData<A, BO>> getDecisionDataIterable();

	/**
	 * Removes the {@link LaraDecisionData} that is associated with the specified {@link LaraDecisionConfiguration}.
	 * 
	 * @param dConfiguration
	 */
	public void removeDecisionData(LaraDecisionConfiguration dConfiguration);
}