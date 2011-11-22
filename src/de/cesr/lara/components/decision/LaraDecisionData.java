/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 26.05.2010
 */
package de.cesr.lara.components.decision;


import java.util.Collection;
import java.util.Map;

import org.apache.commons.collections15.map.UnmodifiableMap;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.preprocessor.LaraDecisionModeSelector;


/**
 * TODO investigate whether the LaraDeciderFactory is appropriate and high-performance! (SH) TODO check creating a sub
 * class for dealing with single BO decisions (problem requires probably casting) > both classes provide all methods but
 * throw exceptions when not supported TODO improve interaction between bos and bo
 * 
 * @param <A>
 *        the agent class
 * @param <BO>
 */
public final class LaraDecisionData<A extends LaraAgent<? super A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>> {

	private LaraDecisionConfiguration			dConfiguration;
	private Collection<BO>									bos;
	private Map<Class<? extends LaraPreference>, Double>	situationalPreferenceWeights;

	private BO												bo;
	private A												agent;

	/**
	 * A factory is used to prevent the process from storing LaraDecider objects before decide()! (SH)
	 */
	LaraDeciderFactory<A, BO>								deciderFactory;
	LaraDecider<BO>											decider;

	/**
	 * @param dConfiguration
	 * @param agent
	 */
	public LaraDecisionData(LaraDecisionConfiguration dConfiguration, A agent) {
		this.dConfiguration = dConfiguration;
		this.agent = agent;
	}

	/**
	 * @return the current {@link LaraDecider} for the according decision process
	 */
	public LaraDecider<BO> getDecider() {
		if (deciderFactory == null) {
			throw new IllegalStateException("DeciderFactory must be set!");
		}
		/*
		 * Since one and the same decider needs to be persistent throughout a decision cycle because it stores important
		 * data, it may not be overwritten during a decision cycle (furthermore, it is quite costy to initialize it
		 * again and again). However, to prevent inconsistencies it needs to be guaranteed that the LaraDecisionData
		 * objects are deleted after the decision cycle. Otherwise, the decider object persists beyond one decision
		 * cycle which often is not desired, since in the next cycle a different decision mode is to be applied.
		 */
		if (decider == null) {
			decider = deciderFactory.getDecider(agent, dConfiguration);
		}
		return decider;
	}

	/**
	 * @return the BOs
	 */
	public Collection<BO> getBos() {
		return bos;
	}

	/**
	 * @return the BO
	 */
	public BO getBo() {
		return bo;
	}

	/**
	 * @param bo
	 *        the BO to set
	 */
	public void setBo(BO bo) {
		this.bo = bo;
	}

	/**
	 * @param bos
	 *        the BOs to set
	 */
	public void setBos(Collection<BO> bos) {
		this.bos = bos;
	}

	/**
	 * @return the currentPreferences
	 */
	public Map<Class<? extends LaraPreference>, Double> getSituationalPreferenceWeights() {
		if (situationalPreferenceWeights == null) {
			throw new IllegalStateException(agent + "> Situational preference weights not set!");
		}
		return UnmodifiableMap.decorate(situationalPreferenceWeights);
	}

	/**
	 * @param situationalPreferenceWeights
	 *        the currentPreferences to set
	 */
	public void setSituationalPreferences(Map<Class<? extends LaraPreference>, Double> situationalPreferenceWeights) {
		this.situationalPreferenceWeights = situationalPreferenceWeights;
	}

	/**
	 * @return the deciderFactory
	 */
	public LaraDeciderFactory<A, BO> getDeciderFactory() {
		return deciderFactory;
	}

	/**
	 * Normally called by a {@link LaraDecisionModeSelector}.
	 * 
	 * @param deciderFactory
	 *        the deciderFactory to set
	 */
	public void setDeciderFactory(LaraDeciderFactory<A, BO> deciderFactory) {
		this.deciderFactory = deciderFactory;
	}

	/**
	 * @return the dConfiguration
	 */
	public LaraDecisionConfiguration getdConfiguration() {
		return dConfiguration;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("DecisionData of " + agent + " for " + dConfiguration + ". Selectable BO(s): ");
		if (bo != null) {
			buffer.append(bo);
		} else if (bos != null) {
			for (BO b : bos) {
				buffer.append("\t" + b + System.getProperty("line.separator"));
			}
		}
		return buffer.toString();
	}
}
