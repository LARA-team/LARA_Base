package de.cesr.lara.components.eventbus.events;

import de.cesr.lara.components.decision.LaraDecisionConfiguration;

/**
 * Triggers agents to preprocess.
 */
public class LAgentPreprocessEvent implements LaraSynchronousEvent {
	private LaraDecisionConfiguration decisionConfiguration;

	public LAgentPreprocessEvent(LaraDecisionConfiguration decisionConfiguration) {
		this.decisionConfiguration = decisionConfiguration;
	}

	public LaraDecisionConfiguration getDecisionConfiguration() {
		return decisionConfiguration;
	}

}
