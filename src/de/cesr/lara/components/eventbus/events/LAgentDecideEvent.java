package de.cesr.lara.components.eventbus.events;

import de.cesr.lara.components.decision.LaraDecisionConfiguration;

/**
 * Triggers agents to decide.
 */
public class LAgentDecideEvent implements LaraSynchronousEvent {
	private LaraDecisionConfiguration decisionConfiguration;

	public LAgentDecideEvent(LaraDecisionConfiguration decisionConfiguration) {
		this.decisionConfiguration = decisionConfiguration;
	}

	public LaraDecisionConfiguration getDecisionConfiguration() {
		return decisionConfiguration;
	}

}
