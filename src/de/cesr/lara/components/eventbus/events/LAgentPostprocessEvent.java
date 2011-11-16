package de.cesr.lara.components.eventbus.events;

import de.cesr.lara.components.decision.LaraDecisionConfiguration;

/**
 * Triggers agents to postprocess.
 */
public class LAgentPostprocessEvent implements LaraSynchronousEvent {
	private LaraDecisionConfiguration decisionConfiguration;

	public LAgentPostprocessEvent(LaraDecisionConfiguration decisionConfiguration) {
		this.decisionConfiguration = decisionConfiguration;
	}

	public LaraDecisionConfiguration getDecisionConfiguration() {
		return decisionConfiguration;
	}

}
