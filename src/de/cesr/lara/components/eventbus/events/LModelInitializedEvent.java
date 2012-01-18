package de.cesr.lara.components.eventbus.events;

/**
 * Is fired on initialization of the model. Triggers initialization of
 * components. TODO explain difference to ModelInstantiatedEvent
 */
public class LModelInitializedEvent implements LaraSynchronousEvent,
		LaraRequiresPrecedingEvent {

	@Override
	public Class<? extends LaraEvent> getRequiredPrecedingEventClass() {
		return LModelInstantiatedEvent.class;
	}

}