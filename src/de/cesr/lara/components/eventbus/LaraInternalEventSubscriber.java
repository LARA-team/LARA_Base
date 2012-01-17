package de.cesr.lara.components.eventbus;

import de.cesr.lara.components.eventbus.events.LaraEvent;

/**
 * Common interface for subscribers of events. Events declare an inherited
 * interface. Only for core components in de.cesr.lara.components.
 */
public interface LaraInternalEventSubscriber extends
		LaraAbstractEventSubscriber {

	/**
	 * Only for internal super classes beeing part of the LARA core. Use this in
	 * your base class to react on events, when the reaction should not be
	 * overridden by subclasses.
	 * 
	 * @param event
	 */
	public abstract void onInternalEvent(LaraEvent event);

}
