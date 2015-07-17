/**
 * This file is part of
 * 
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 * 
 * Copyright (C) 2012 Center for Environmental Systems Research, Kassel, Germany
 * 
 * LARA is free software: You can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * LARA is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.cesr.lara.components.eventbus.impl;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.eventbus.LaraAbstractEventSubscriber;
import de.cesr.lara.components.eventbus.LaraEventSubscriber;
import de.cesr.lara.components.eventbus.events.LaraAsynchronousEvent;
import de.cesr.lara.components.eventbus.events.LaraDcSpecificEvent;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.eventbus.events.LaraHasConsecutiveEvent;
import de.cesr.lara.components.eventbus.events.LaraSynchronousEvent;
import de.cesr.lara.components.util.exceptions.LIdentifyCallerException;

/**
 * Enables subscribers to subscribe for specific
 * {@link LaraDecisionConfiguration}s only.
 * 
 * @author Sascha Holzhauer
 * 
 */
public class LDcSpecificEventbus extends LEventbus {

	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(LDcSpecificEventbus.class);

	protected volatile Map<Class<? extends LaraEvent>, Map<LaraDecisionConfiguration, Set<LaraAbstractEventSubscriber>>> eventSubscriberMapDc = new HashMap<Class<? extends LaraEvent>, Map<LaraDecisionConfiguration, Set<LaraAbstractEventSubscriber>>>();

	protected volatile Map<Class<? extends LaraEvent>, Map<LaraDecisionConfiguration, Set<LaraAbstractEventSubscriber>>> eventSubscriberOnceMapDc = new HashMap<Class<? extends LaraEvent>, Map<LaraDecisionConfiguration, Set<LaraAbstractEventSubscriber>>>();

	protected LDcSpecificEventbus(Object id) {
		super(id);
	}

	protected void checkSubscriberMapDc(
			Map<Class<? extends LaraEvent>, Map<LaraDecisionConfiguration, Set<LaraAbstractEventSubscriber>>> eventSubscriberMap,
			Class<? extends LaraEvent> eventClass, LaraDecisionConfiguration dc) {
		if (!eventSubscriberMap.containsKey(eventClass)) {
			eventSubscriberMap.put(eventClass, new HashMap<LaraDecisionConfiguration, Set<LaraAbstractEventSubscriber>>());
		}
		
		if (!eventSubscriberMap.get(eventClass).containsKey(dc)) {
			Set<LaraAbstractEventSubscriber> subscribers = new LinkedHashSet<LaraAbstractEventSubscriber>();
			eventSubscriberMap.get(eventClass).put(dc, subscribers);
		}
	}

	/**
	 * Subscribes the given {@link LaraEventSubscriber} to be notified only once
	 * for the next event.
	 * 
	 * NOTE: If the same subscriber is subscribed to the event continuously (via
	 * {@link #subscribe(LaraAbstractEventSubscriber, Class)} it is notified
	 * only once!
	 * 
	 * @param subscriber
	 * @param eventClass
	 * @param dc
	 */
	public void subscribeOnce(LaraAbstractEventSubscriber subscriber,
			Class<? extends LaraEvent> eventClass, LaraDecisionConfiguration dc) {
		checkSubscriberMapDc(eventSubscriberOnceMapDc, eventClass, dc);
		eventSubscriberOnceMapDc.get(eventClass).get(dc).add(subscriber);

		// <- LOGGING
		logger.info("Subscribed " + subscriber + " to event "
				+ eventClass.getName() + " for decision configuration " + dc);

		if (logger.isDebugEnabled()) {
			logger.error("Subscriber: ", new LIdentifyCallerException(2, 1));
		}
		// LOGGING ->
	}

	protected boolean notifySubscribers(LaraEvent event, boolean hasSubscribers) {
		boolean hasSubscribersHere = false;
		if (event instanceof LaraDcSpecificEvent) {
			LaraDcSpecificEvent e = (LaraDcSpecificEvent) event;
			Set<LaraAbstractEventSubscriber> subscribers = null;

			if (eventSubscriberOnceMapDc.containsKey(e.getClass())
					&& eventSubscriberOnceMapDc.get(e.getClass())
							.containsKey(e.getDecisionConfiguration())) {
				// get subscribers set
				subscribers = eventSubscriberOnceMapDc.get(e.getClass())
						.get(e.getDecisionConfiguration());
				eventSubscriberOnceMapDc.get(e.getClass()).remove(
						e.getDecisionConfiguration());
			}

			if (eventSubscriberMapDc.containsKey(e.getClass())
					&& eventSubscriberMapDc.get(e.getClass()).containsKey(
							e.getDecisionConfiguration())) {
				// get subscribers set
				if (subscribers != null) {
					subscribers.addAll(eventSubscriberMapDc.get(e.getClass())
							.get(e.getDecisionConfiguration()));
				} else {
					subscribers = eventSubscriberMapDc.get(e.getClass()).get(
							e.getDecisionConfiguration());
				}
			}

			if (subscribers != null) {
				hasSubscribersHere = true;
				logSubscribers(subscribers, event);
				
				// check whether the subscriber is already considered because it is registered
				// for the event independent from decision configuration.
				
				Set<LaraAbstractEventSubscriber> filteredSubscribers = new LinkedHashSet<LaraAbstractEventSubscriber>(
						subscribers);
				if (eventSubscriberMap.containsKey(event.getClass())) {
					for (LaraAbstractEventSubscriber subscriber : subscribers) {
						if (eventSubscriberMap.get(event.getClass()).contains(
								subscriber)) {
							filteredSubscribers.remove(subscriber);
						}
					}
				}

				if (eventSubscriberOnceMap.containsKey(event.getClass())) {
					for (LaraAbstractEventSubscriber subscriber : subscribers) {
						if (eventSubscriberOnceMap.get(event.getClass())
								.contains(
								subscriber)) {
							filteredSubscribers.remove(subscriber);
						}
					}
				}

				logger.debug("Notifying " + filteredSubscribers.size()
						+ " subscriber(s) of event of type "
						+ event.getClass().getSimpleName());

				// notify subscribers according to event type
				if (event instanceof LaraSynchronousEvent) {
					if (this.forceSequential) {
						notifySubscribersSequential(filteredSubscribers, event);
					} else {
						// starts several threads and waits until last one has
						// finished
						notifySubscribersSynchronous(filteredSubscribers, event);
					}
				} else if (event instanceof LaraAsynchronousEvent) {
					notifySubscribersAsynchronous(filteredSubscribers, event);
				} else {
					notifySubscribersSequential(filteredSubscribers, event);
				}
			}
			// if event has consecutive event fire this
			if (event instanceof LaraHasConsecutiveEvent) {
				publish(((LaraHasConsecutiveEvent) event).getConsecutiveEvent());
			}
		}

		return super.notifySubscribers(event, hasSubscribers
				|| hasSubscribersHere);
	}

	/**
	 * Unsubscribes all subscribers from the given event class. Note: This
	 * method is inefficient since it iterates over all registered events.
	 * 
	 * @param eventClass
	 */
	public void unsubscribe(Class<? extends LaraEvent> eventClass) {
		eventSubscriberMapDc.remove(eventClass);
		eventSubscriberOnceMapDc.remove(eventClass);
		super.unsubscribe(eventClass);
	}

	/**
	 * Unsubscribes all subscribers from the event class the given event belongs
	 * to.
	 * 
	 * @param event
	 *            to unsubscribe
	 */
	public void unsubscribe(LaraEvent event) {
		eventSubscriberMapDc.remove(event.getClass());
		eventSubscriberOnceMapDc.remove(event.getClass());
		super.unsubscribe(event);
	}

	/**
	 * Unsubscribe the given subscriber from all events. NOTE: This method is
	 * inefficient since it iterates over all registered events!
	 * 
	 * @param subscriber
	 */
	public void unsubscribe(LaraAbstractEventSubscriber subscriber) {
		for (Map<LaraDecisionConfiguration, Set<LaraAbstractEventSubscriber>> sets : eventSubscriberMapDc
				.values()) {
			for (Set<LaraAbstractEventSubscriber> subscribers : sets.values()) {
				subscribers.remove(subscriber);
			}
		}

		for (Map<LaraDecisionConfiguration, Set<LaraAbstractEventSubscriber>> onceSets : eventSubscriberOnceMapDc
				.values()) {
			for (Set<LaraAbstractEventSubscriber> subscribers : onceSets.values()) {
				subscribers.remove(subscriber);
			}
		}
		super.unsubscribe(subscriber);
	}

	/**
	 * Unsubscribe the given subscriber from all events for the given decision
	 * configuration. NOTE: This method is inefficient since it iterates over
	 * all registered events!
	 * 
	 * @param subscriber
	 * @param dc
	 */
	public void unsubscribe(LaraAbstractEventSubscriber subscriber,
			LaraDecisionConfiguration dc) {
		for (Map<LaraDecisionConfiguration, Set<LaraAbstractEventSubscriber>> sets : eventSubscriberMapDc
				.values()) {
			sets.get(dc).remove(subscriber);
		}

		for (Map<LaraDecisionConfiguration, Set<LaraAbstractEventSubscriber>> onceSets : eventSubscriberOnceMapDc
				.values()) {
			onceSets.get(dc).remove(subscriber);
		}
		super.unsubscribe(subscriber);
	}

	/**
	 * Unsubscribe a subscriber from a class of events
	 * 
	 * @param subscriber
	 * @param eventClass
	 */
	public void unsubscribe(LaraAbstractEventSubscriber subscriber,
			Class<? extends LaraEvent> eventClass) {
		if (eventSubscriberMapDc.containsKey(eventClass) ) {
			for (Entry<LaraDecisionConfiguration, Set<LaraAbstractEventSubscriber>> dcs : eventSubscriberMapDc
					.get(eventClass).entrySet())
				dcs.getValue().remove(subscriber);
			}

		if (eventSubscriberOnceMapDc.containsKey(eventClass) ) {
			for (Entry<LaraDecisionConfiguration, Set<LaraAbstractEventSubscriber>> dcs : eventSubscriberOnceMapDc
					.get(eventClass).entrySet())
				dcs.getValue().remove(subscriber);
			}
		super.unsubscribe(subscriber, eventClass);
	}

	/**
	 * Clears eventsThisTimestamp, event subscriber map, event-waiting counters,
	 * and statistics.
	 */
	public void resetInstance() {
		eventSubscriberMapDc.clear();
		eventSubscriberOnceMapDc.clear();
		super.resetInstance();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (instances.containsValue(this)) {
			return "LDcSpecificEventbus (" + instances.getKey(this) + ")";
		} else {
			return "LDcSpecificLEventbus";
		}
	}
}
