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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import de.cesr.lara.components.eventbus.LaraAbstractEventSubscriber;
import de.cesr.lara.components.eventbus.LaraEventSubscriber;
import de.cesr.lara.components.eventbus.LaraInternalEventSubscriber;
import de.cesr.lara.components.eventbus.events.LModelStepEvent;
import de.cesr.lara.components.eventbus.events.LaraAsynchronousEvent;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.eventbus.events.LaraHasConsecutiveEvent;
import de.cesr.lara.components.eventbus.events.LaraRequiresPrecedingEvent;
import de.cesr.lara.components.eventbus.events.LaraSynchronousEvent;
import de.cesr.lara.components.param.LBasicPa;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;
import de.cesr.parma.core.PmParameterManager;

/**
 * The LEventbus serves as a communication system between different components,
 * where the components do not necessarily know each other. Components can
 * publish events to the LaraEventbus. All subscribers of this event will be
 * notified on occurrence. The event bus is implemented as a singleton to ensure
 * all components communicate over the same channel and to make use of the event
 * bus easier. However, it also manages eventbuses by ID objects which enables
 * specific eventbuses per agent.
 * 
 * GOAL: Loose coupling of components, Event driven initialization.
 */
public class LEventbus {

	private static LEventbus instance = null;
	private static BidiMap<Object, LEventbus>	instances	= new DualHashBidiMap<Object, LEventbus>();
	private static Logger logger = Log4jLogger.getLogger(LEventbus.class);

	/**
	 * returns a reference to the global eventbus
	 */
	public static LEventbus getInstance() {
		if (instance == null) {
			instance = new LEventbus();
			Log4jLogger.init();
			logger.debug("LaraEventbus instantiated");
		}
		return instance;
	}

	/**
	 * returns a reference to a special eventbus
	 * 
	 * @param id
	 * @return
	 */
	public static LEventbus getInstance(Object id) {
		LEventbus theInstance = instances.get(id);
		if (theInstance == null) {
			theInstance = new LEventbus();
			instances.put(id, theInstance);
			logger.debug("LaraEventbus '" + id + "' instantiated");
		}
		return theInstance;
	}

	/**
	 * Iterates over all instances and calls {@link LEventbus#resetInstance()}.
	 * Clears collection of instances.
	 */
	public static void resetAll() {
		// <- LOGGING
		logger.info("Reset all eventbusses");
		// LOGGING ->

		if (instance != null) {
			instance.resetInstance();
			instance = null;
		}
		for (LEventbus eb : instances.values()) {
			eb.resetInstance();
		}
		instances.clear();
	}

	private boolean forceSequential;

	private volatile Set<Class<? extends LaraEvent>> eventsThisTimestep = new HashSet<Class<? extends LaraEvent>>();

	private volatile Map<Class<? extends LaraEvent>, Set<LaraAbstractEventSubscriber>> eventSubscriberMap = new HashMap<Class<? extends LaraEvent>, Set<LaraAbstractEventSubscriber>>();

	private volatile Map<LaraEvent, Integer> eventWaitingCounters = new HashMap<LaraEvent, Integer>();

	private final Map<Class<? extends LaraEvent>, Long> statistics = new HashMap<Class<? extends LaraEvent>, Long>();

	/**
	 * This is a singleton. Use of constructor is permitted. Use getInstance()
	 * to obtain a reference to the event bus.
	 */
	protected LEventbus() {
		this.forceSequential = (Boolean) PmParameterManager
				.getParameter(LBasicPa.EVENTBUS_FORCE_SEQUENTIAL);

		// <- LOGGING
		logger.info("EventBus runs in FORCE_SEQUENTIAL mode? "
				+ this.forceSequential);
		// LOGGING ->
	}

	/**
	 * Decrements the counter of how many event subscribers are notified but
	 * have not yet finished their work. If last subscriber finishes the monitor
	 * stops waiting.
	 * 
	 * @param event
	 * @param monitor
	 */
	private void decrementWaitingCounter(LaraEvent event) {
		synchronized (event) {
			Integer oldValue = getWaitingCounter(event);
			if (oldValue == null) {
				logger.error("Something went wrong during synchronized event notification of event "
						+ event.getClass().getSimpleName());
			} else {
				Integer newValue = new Integer(oldValue.intValue() - 1);
				logger.debug("Number of worker threads for event "
						+ event.getClass().getSimpleName() + ": "
						+ newValue.intValue());
				if (newValue.intValue() > 0) {
					eventWaitingCounters.put(event, newValue);
				} else {
					// is last worker thread
					// stop waiting
					event.notify();
					// do not waste memory
					eventWaitingCounters.remove(event);
				}
			}
		}
	}

	/**
	 * The counter of how many event subscribers are notified but have not yet
	 * finished their work.
	 * 
	 * @param event
	 */
	private Integer getWaitingCounter(LaraEvent event) {
		synchronized (event) {
			return eventWaitingCounters.get(event);
		}
	}

	/**
	 * Increments the counter of how many event subscribers are notified but
	 * have not yet finished their work.
	 * 
	 * @param event
	 */
	private void incrementWaitingCounter(LaraEvent event) {
		synchronized (event) {
			Integer oldValue = getWaitingCounter(event);
			if (oldValue == null) {
				oldValue = new Integer(0);
			}
			Integer newValue = new Integer(oldValue.intValue() + 1);
			logger.debug("Number of worker threads for event "
					+ event.getClass().getSimpleName() + ": "
					+ newValue.intValue());
			eventWaitingCounters.put(event, newValue);
		}
	}

	/**
	 * @return the forceSequential
	 */
	public boolean isForceSequential() {
		return forceSequential;
	}

	/**
	 * Logs a list of given subscribers (only when {@link Priority#DEBUG} is
	 * enabled).
	 * 
	 * @param subscribers
	 * @param event
	 */
	private void logSubscribers(
			Collection<LaraAbstractEventSubscriber> subscribers, LaraEvent event) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("Notifying subscribers for event "
					+ event.getClass().getName() + "\n");
			for (LaraAbstractEventSubscriber subscriber : subscribers) {
				buffer.append("\t" + subscriber + "\n");
			}
			logger.debug(buffer.toString());
		}
		// LOGGING ->
	}

	/**
	 * Notifies internat subscriber sequential. Subscribers will execute their
	 * related code sequential. Method finishes after last subscriber finishes.
	 * 
	 * @param subscribers
	 * @param event
	 */
	private void notifyInternalSubscribersSequential(
			Set<LaraAbstractEventSubscriber> subscribers, LaraEvent event) {
		// <- LOGGING
		logger.info("Notifying " + subscribers.size()
				+ " internal subscriber(s) sequentially ("
				+ event.getClass().getSimpleName() + ")");
		// LOGGING ->

		// internal first
		for (LaraAbstractEventSubscriber s : subscribers) {
			if (s instanceof LaraInternalEventSubscriber) {
				((LaraInternalEventSubscriber) s).onInternalEvent(event);
			}
		}

		// <- LOGGING
		logger.debug("Notified " + subscribers.size()
				+ " internal subscribers sequentially ("
				+ event.getClass().getSimpleName() + ")");
		// LOGGING ->
	}

	/**
	 * Notifies noninternal subscriber sequential. Subscribers will execute
	 * their related code sequential. Method finishes after last subscriber
	 * finishes.
	 * 
	 * @param subscribers
	 * @param event
	 */
	private void notifyNoninternalSubscribersSequential(
			Set<LaraAbstractEventSubscriber> subscribers, LaraEvent event) {
		// <- LOGGING
		logger.info("Notifying " + subscribers.size()
				+ " noninternal subscribers sequentially ("
				+ event.getClass().getSimpleName() + ")");
		// LOGGING ->

		for (LaraAbstractEventSubscriber s : subscribers) {
			if (s instanceof LaraEventSubscriber) {
				((LaraEventSubscriber) s).onEvent(event);
			}
		}

		// <- LOGGING
		logger.debug("Notified " + subscribers.size()
				+ " noninternal subscribers sequentially ("
				+ event.getClass().getSimpleName() + ")");
		// LOGGING ->
	}

	/**
	 * TODO search for subscribers to super classes! (Custom PP component
	 * implementations could publish sub classes of PP-event that other
	 * components need to recognise)
	 * 
	 * @param event
	 */
	protected void notifySubscribers(LaraEvent event) {
		// notify all subscribers
		if (eventSubscriberMap.containsKey(event.getClass())) {
			// get subscribers set
			Set<LaraAbstractEventSubscriber> subscribers = eventSubscriberMap
					.get(event.getClass());

			logSubscribers(subscribers, event);

			logger.debug("Notifying " + subscribers.size()
					+ " subscriber(s) of event of type "
					+ event.getClass().getSimpleName());

			// notify subscriber according to event type
			if (event instanceof LaraSynchronousEvent) {
				if (this.forceSequential) {
					notifySubscribersSequential(subscribers, event);
				} else {
					// starts several threads and waits until last one has
					// finished
					notifySubscribersSynchronous(subscribers, event);
				}
			} else if (event instanceof LaraAsynchronousEvent) {
				notifySubscribersAsynchronous(subscribers, event);
			} else {
				notifySubscribersSequential(subscribers, event);
			}
		} else {
			// no subscribers - log this
			logger.warn("Event of type "
					+ event.getClass().getSimpleName()
					+ " published, but has no subscribers. Maybe you should check if this was intended.");
		}
		// if event has consecutive event fire this
		if (event instanceof LaraHasConsecutiveEvent) {
			publish(((LaraHasConsecutiveEvent) event).getConsecutiveEvent());
		}
	}

	/**
	 * Notifies all subscribers at once. Subscribers will execute their related
	 * code in parallel. Method will not wait for subscribers to finish. Method
	 * will return immediately
	 * 
	 * @param subscribers
	 * @param event
	 */
	private void notifySubscribersAsynchronous(
			Set<LaraAbstractEventSubscriber> subscribers, final LaraEvent event) {
		// <- LOGGING
		logger.info("Notifying " + subscribers.size()
				+ " subscribers assynchonously ("
				+ event.getClass().getSimpleName() + ")");
		// LOGGING ->

		// internal first
		for (final LaraAbstractEventSubscriber s : subscribers) {
			if (s instanceof LaraInternalEventSubscriber) {
				Thread workerThread = new Thread() {
					@Override
					public void run() {
						((LaraInternalEventSubscriber) s)
								.onInternalEvent(event);
					}
				};
				workerThread.start();
			}
		}
		for (final LaraAbstractEventSubscriber s : subscribers) {
			if (s instanceof LaraEventSubscriber) {
				Thread workerThread = new Thread() {
					@Override
					public void run() {
						((LaraEventSubscriber) s).onEvent(event);
					}
				};
				workerThread.start();
			}
		}

		// <- LOGGING
		logger.info("Notified " + subscribers.size()
				+ " subscribers assynchonously ("
				+ event.getClass().getSimpleName() + ")");
		// LOGGING ->
	}

	/**
	 * Notifies subscriber sequential. Subscribers will execute their related
	 * code sequential. Method finishes after last subscriber finishes.
	 * 
	 * @param subscribers
	 * @param event
	 */
	private void notifySubscribersSequential(
			Set<LaraAbstractEventSubscriber> subscribers, LaraEvent event) {
		// <- LOGGING
		logger.info("Notifying " + subscribers.size()
				+ " subscriber(s) sequentially ("
				+ event.getClass().getSimpleName() + ")");
		// LOGGING ->

		// internal first
		for (LaraAbstractEventSubscriber s : subscribers) {
			if (s instanceof LaraInternalEventSubscriber) {
				((LaraInternalEventSubscriber) s).onInternalEvent(event);
			}
		}
		for (LaraAbstractEventSubscriber s : subscribers) {
			if (s instanceof LaraEventSubscriber) {
				((LaraEventSubscriber) s).onEvent(event);
			}
		}

		// <- LOGGING
		logger.info("Notified " + subscribers.size()
				+ " subscriber(s) sequentially ("
				+ event.getClass().getSimpleName() + ")");
		// LOGGING ->
	}

	/**
	 * Notifies all subscribers at once. Subscribers will execute their related
	 * code in parallel. Method will wait until last subscriber finishes.
	 * 
	 * @param subscribers
	 * @param event
	 */
	private void notifySubscribersSynchronous(
			Set<LaraAbstractEventSubscriber> subscribers, final LaraEvent event) {
		// too much threads at once is highly ineffective. so we use
		// taskgroups to reduce max number of concurrent threads
		// we have to make sure ALL internal subscribers are notified before ALL
		// the others.
		// <- LOGGING
		logger.info("Notifying " + subscribers.size()
				+ " subscriber(s) synchronously ("
				+ event.getClass().getSimpleName() + ")");
		// LOGGING ->

		// limit number of concurrent tasks by building workgroups
		int numberOfCores = Runtime.getRuntime().availableProcessors();
		// TODO SH
		int numberOfWorkerGroups = numberOfCores * 4;
		if (logger.isDebugEnabled()) {
			logger.debug("Number of Worker Groups for event "
					+ event.getClass().getSimpleName() + ": "
					+ numberOfWorkerGroups);
		}

		Map<Integer, Set<LaraAbstractEventSubscriber>> workerGroupSubscriberMap = new HashMap<Integer, Set<LaraAbstractEventSubscriber>>();

		// required to check if monitor need to wait
		int currentWorkGroup = 0;
		for (final LaraAbstractEventSubscriber s : subscribers) {
			Set<LaraAbstractEventSubscriber> subscriberGroup = workerGroupSubscriberMap
					.get(currentWorkGroup);
			if (subscriberGroup == null) {
				subscriberGroup = new HashSet<LaraAbstractEventSubscriber>();
				workerGroupSubscriberMap.put(currentWorkGroup, subscriberGroup);
			}
			subscriberGroup.add(s);
			currentWorkGroup++;
			if (currentWorkGroup > numberOfWorkerGroups) {
				currentWorkGroup = 0;
			}
		}

		// for every worker group start a new thread, that notifies all
		// internal subscribers belonging to it sequentially
		for (final Entry<Integer, Set<LaraAbstractEventSubscriber>> entry : workerGroupSubscriberMap
				.entrySet()) {
			Thread workerThread = new Thread() {
				@Override
				public void run() {
					notifyInternalSubscribersSequential(entry.getValue(), event);
					decrementWaitingCounter(event);
				}
			};
			incrementWaitingCounter(event);
			workerThread.start();
		}
		waitUntilWorkDone(event);

		// for every worker group start a new thread, that notifies all
		// non-internal subscribers belonging to it sequentially
		for (final Entry<Integer, Set<LaraAbstractEventSubscriber>> entry : workerGroupSubscriberMap
				.entrySet()) {
			Thread workerThread = new Thread() {
				@Override
				public void run() {
					notifyNoninternalSubscribersSequential(entry.getValue(),
							event);
					decrementWaitingCounter(event);
				}
			};
			incrementWaitingCounter(event);
			workerThread.start();
		}
		waitUntilWorkDone(event);

		// <- LOGGING
		logger.info("Notified " + subscribers.size()
				+ " subscribers synchronously ("
				+ event.getClass().getSimpleName() + ")");
		// LOGGING ->
	}

	/**
	 * Checks whether the given LaraEvent has occurred during the current tick.
	 * 
	 * @param event
	 *            to check
	 * @return true if the given event has occurred
	 */
	public boolean occured(LaraEvent event) {
		return eventsThisTimestep.contains(event.getClass());
	}

	/**
	 * Publish / fire event - this will notify subscribers of this event about
	 * occurrence of this event
	 * 
	 * @param event
	 */
	public void publish(LaraEvent event) {
		// <- LOGGING
		logger.info(event.getClass().getName() + " published");
		// LOGGING ->

		// reset set of events which occurred this timestep if new timestep
		// begins. otherwise store event type.
		if (event instanceof LModelStepEvent) {
			eventsThisTimestep.clear();
		}
		eventsThisTimestep.add(event.getClass());
		// check precondition
		if (event instanceof LaraRequiresPrecedingEvent) {
			// TODO Also check for sub classes of the required event class!
			// TODO provide means to check more than one required event
			if (eventsThisTimestep
					.contains(((LaraRequiresPrecedingEvent) event)
							.getRequiredPrecedingEventClass())) {
				notifySubscribers(event);
			} else {
				// no subscribers - log this
				logger.warn("Event of type "
						+ event.getClass().getSimpleName()
						+ " requires event of type "
						+ ((LaraRequiresPrecedingEvent) event)
								.getRequiredPrecedingEventClass()
						+ " to have occured before - which did not happen.");
			}
		} else {
			notifySubscribers(event);
		}
	}

	/**
	 * Clears eventsThisTimestamp, event subscriber map, event-waiting counters,
	 * and statistics.
	 */
	public void resetInstance() {
		// <- LOGGING
		logger.info("Reset eventbus " + instance);
		// LOGGING ->

		eventsThisTimestep.clear();
		eventSubscriberMap.clear();
		eventWaitingCounters.clear();
		statistics.clear();
	}

	/**
	 * @param forceSequential
	 *            the forceSequential to set
	 */
	public void setForceSequential(boolean forceSequential) {
		this.forceSequential = forceSequential;
	}

	/**
	 * Checks whether the given LaraEvent or any sub class has occurred during
	 * the current tick.
	 * 
	 * @param event
	 *            to check
	 * @return true if the given event or any sub class has occurred
	 */
	public boolean subclassOccured(LaraEvent event) {
		for (Class<? extends LaraEvent> e : eventsThisTimestep) {
			if (e.isInstance(event)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Subscribe for an event - subscriber will be informed about occurrence of
	 * event (triggers onEvent() method of implemented interface
	 * LaraEventSubscriber)
	 * 
	 * @param subscriber
	 * @param event
	 */
	public void subscribe(LaraAbstractEventSubscriber subscriber,
			Class<? extends LaraEvent> eventClass) {
		if (eventSubscriberMap.containsKey(eventClass)) {
			// add to existing set
			Set<LaraAbstractEventSubscriber> subscribers = eventSubscriberMap
					.get(eventClass);
			subscribers.add(subscriber);
		} else {
			// add to new set
			Set<LaraAbstractEventSubscriber> subscribers = new LinkedHashSet<LaraAbstractEventSubscriber>();
			subscribers.add(subscriber);
			// add to event-subscriber mapping
			eventSubscriberMap.put(eventClass, subscribers);
		}

		// <- LOGGING
		logger.info("Subscribed " + subscriber + " to event "
				+ eventClass.getName());
		// LOGGING ->
	}

	/**
	 * Unsubscribes all subscribers from the given event class. Note: This
	 * method is inefficient since it iterates over all registered events.
	 * 
	 * @param subscriber
	 */
	public void unsubscribe(Class<? extends LaraEvent> eventClass) {
		eventSubscriberMap.remove(eventClass);

		// <- LOGGING
		logger.info("Unsubscribed all subscribers from event " + eventClass
				+ ".");
		// LOGGING ->
	}

	/**
	 * Unsubscribe the given subscriber from all events. NOTE: This method is
	 * inefficient since it iterates over all registered events!
	 * 
	 * @param subscriber
	 */
	public void unsubscribe(LaraAbstractEventSubscriber subscriber) {
		for (Set<LaraAbstractEventSubscriber> subscribers : eventSubscriberMap
				.values()) {
			subscribers.remove(subscriber);
		}

		// <- LOGGING
		logger.info("Unsubscribed " + subscriber + " from all events.");
		// LOGGING ->
	}

	/**
	 * Unsubscribe a subscriber from an event
	 * 
	 * @param subscriber
	 * @param event
	 */
	public void unsubscribe(LaraAbstractEventSubscriber subscriber,
			Class<? extends LaraEvent> eventClass) {
		if (eventSubscriberMap.containsKey(eventClass)) {
			// get existing set of subscribers
			Set<LaraAbstractEventSubscriber> subscribers = eventSubscriberMap
					.get(eventClass);
			subscribers.remove(subscriber);
		} else {
			logger.debug("instance of " + subscriber.getClass().getSimpleName()
					+ " wants to unsubscribe from event of type "
					+ eventClass.getSimpleName()
					+ " but is not a subscriber at the moment");
		}

		// <- LOGGING
		logger.info("Unsubscribed " + subscriber + " from event "
				+ eventClass.getName());
		// LOGGING ->
	}

	/**
	 * Unsubscribes all subscribers from the event class the given event belongs
	 * to.
	 * 
	 * @param subscriber
	 */
	public void unsubscribe(LaraEvent event) {
		eventSubscriberMap.remove(event.getClass());

		// <- LOGGING
		logger.info("Unsubscribed all subscribers from event "
				+ event.getClass() + ".");
		// LOGGING ->
	}

	/**
	 * Wait for worker threads to finish. The last thread that finishes will
	 * notify the monitor.
	 * 
	 * @param monitor
	 */
	private void waitUntilWorkDone(final LaraEvent event) {
		synchronized (event) {
			try {
				logger.debug("waiting for worker threads to finish for event "
						+ event.getClass().getSimpleName());
				Thread infothread = new Thread(new Runnable() {

					@Override
					public void run() {
						while (getWaitingCounter(event) != null) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
							}
							logger.info("Waiting counter for event "
									+ event.getClass().getSimpleName()
									+ ": "
									+ getWaitingCounter(event)
									+ " Threads in current thread's thread-group: "
									+ Thread.activeCount());
						}
					}
				});
				infothread.start();
				if (getWaitingCounter(event) != null) {
					if (getWaitingCounter(event) > 0) {
						event.wait();
					} else {
						// logger.info("not waiting for event " +
						// event.getClass().getSimpleName() +
						// " because nothing to wait for");
					}
				} else {
					// logger.info("not waiting for event " +
					// event.getClass().getSimpleName() +
					// " because nothing to wait for");
				}
				infothread.stop();
				logger.debug(this + "> All worker threads finished for event "
						+ event.getClass().getSimpleName());
			} catch (InterruptedException e) {
				logger.error(this + "> Waiting for worker threads to finished failed for event "
								+ event.getClass().getSimpleName(), e);
			}
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (instances
				.containsKey(this)) {
			return "LEventbus-"
					+ instances
							.getKey(this);
		} else {
			return "LEventbus";
		}
	}

}