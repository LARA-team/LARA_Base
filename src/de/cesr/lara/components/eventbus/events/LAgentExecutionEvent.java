package de.cesr.lara.components.eventbus.events;

import de.cesr.lara.components.decision.LaraDecisionConfiguration;

/**
 * Triggers agents to execute.
 */
public class LAgentExecutionEvent implements LaraSynchronousEvent {
	private LaraDecisionConfiguration decisionConfiguration;

	public LAgentExecutionEvent(LaraDecisionConfiguration decisionConfiguration) {
		this.decisionConfiguration = decisionConfiguration;
	}

	public LaraDecisionConfiguration getDecisionConfiguration() {
		return decisionConfiguration;
	}

}
