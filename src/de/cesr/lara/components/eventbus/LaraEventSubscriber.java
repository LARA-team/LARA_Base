package de.cesr.lara.components.eventbus;

import de.cesr.lara.components.eventbus.events.LaraEvent;

/**
 * Common interface for subscribers of events.
 */
public interface LaraEventSubscriber extends AbstractLaraEventSubscriber {

	/**
	 * Will be called with an event as a parameter when an event the subscriber
	 * subscribed to occurs. Implement this to react on events. Typically you
	 * would start doing something like <pseudocode> if (event instanceof
	 * VeryInterestingEvent) { //do something smart } </pseudocode>
	 * 
	 * @param event
	 */
	public abstract <T extends LaraEvent> void onEvent(T event);

}
