package de.cesr.lara.components.eventbus.events;

import de.cesr.lara.components.decision.LaraDecisionConfiguration;

/**
 * Triggers agents to perceive.
 */
public class LAgentPerceptionEvent implements LaraSynchronousEvent {
	private LaraDecisionConfiguration decisionConfiguration;

	public LAgentPerceptionEvent(LaraDecisionConfiguration decisionConfiguration) {
		this.decisionConfiguration = decisionConfiguration;
	}

	public LaraDecisionConfiguration getDecisionConfiguration() {
		return decisionConfiguration;
	}

}
