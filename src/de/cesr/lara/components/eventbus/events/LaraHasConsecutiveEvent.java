package de.cesr.lara.components.eventbus.events;

public interface LaraHasConsecutiveEvent {

	/**
	 * The event which should be fired, after the current event
	 * 
	 * @return event
	 */
	public LaraEvent getConsecutiveEvent();

}
