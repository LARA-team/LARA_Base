package de.cesr.lara.components.eventbus.events;

public interface LaraRequiresPrecedingEvent {

	/**
	 * The Class.getSimpleName() of the event which should have occured before
	 * in the same timestep
	 * 
	 * @return simpleName
	 */
	public String getRequiredPrecedingEventName();
}
