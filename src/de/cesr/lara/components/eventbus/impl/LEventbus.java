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
import de.cesr.lara.components.util.exceptions.LIdentifyCallerException;
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
 * 
 * NOTE: Decision configuration specific subscription is only supported for
 * non-internal events!
 */
public class LEventbus {

	private static LEventbus instance = null;
	private static BidiMap<Object, LEventbus>	instances	= new DualHashBidiMap<Object, LEventbus>();
	private static Logger logger = Log4jLogger.getLogger(LEventbus.class);

	/**
	 * Returns a reference to the global eventbus
	 * 
	 * TODO make available from LaraModel (would enable parallel computation
	 * with several model instances)
	 * 
	 * @return global eventbus
	 */
	public static LEventbus getInstance() {
		return getInstance(null);
	}

	/**
	 * Returns a reference to a specific eventbus
	 * 
	 * @param id
	 *            the id is also applied to acquire the
	 *            {@link PmParameterManager}.
	 * @return eventbus
	 */
	public static LEventbus getInstance(Object id) {
		LEventbus theInstance = instances.get(id);
		if (theInstance == null) {
			theInstance = new LEventbus(id);
			Log4jLogger.init();
			instances.put(id, theInstance);
			logger.debug("LaraEventbus '" + id + "' instantiated");
		}
		return theInstance;
	}

	/**
	 * Creates a new instance of {@link LEventbus}. Also registers the new
	 * instance and possibly overwrites previously registered eventbus at the
	 * given id.
	 * 
	 * @param id
	 * @param pm
	 * @return eventbus
	 */
	public static LEventbus getNewInstance(Object id, PmParameterManager pm) {
		LEventbus theInstance = new LEventbus(id, pm);
		Log4jLogger.init();
		instances.put(id, theInstance);
		logger.debug("LaraEventbus '" + id + "' instantiated");

		return theInstance;
	}

	public static LDcSpecificEventbus getDcSpecificInstance(Object id) {
		LEventbus theInstance = instances.get(id);
		if (theInstance == null) {
			theInstance = new LDcSpecificEventbus(id);
			Log4jLogger.init();
			instances.put(id, theInstance);
			logger.debug("LaraEventbus '" + id + "' instantiated");
		} else if (!(theInstance instanceof LDcSpecificEventbus)) {
			throw new IllegalStateException(
					"The registered eventbus instance for id "
							+ id
							+ " is not of decision configuration specific! "
							+ "Use resetInstance(id) to remove it!");
		}
		return (LDcSpecificEventbus) theInstance;
	}
	/**
	 * @param id
	 * @return true if an instance of Eventbus exists for the given id
	 */
	public static boolean isInstanceRegistered(Object id) {
		return instances.containsKey(id);
	}

	/**
	 * Calls {@link LEventbus#resetInstance()} at the instance of the given id
	 * and removes it from the map of registered instances. Clears collection of
	 * instances.
	 * 
	 * @param id
	 */
	public static void reset(Object id) {
		// <- LOGGING
		logger.info("Reset instance for id " + id);
		// LOGGING ->

		instances.get(id).resetInstance();
		instances.remove(id);
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

	protected boolean forceSequential;

	protected volatile Set<Class<? extends LaraEvent>> eventsThisTimestep = new HashSet<Class<? extends LaraEvent>>();

	protected volatile Map<Class<? extends LaraEvent>, Set<LaraAbstractEventSubscriber>> eventSubscriberMap = new HashMap<Class<? extends LaraEvent>, Set<LaraAbstractEventSubscriber>>();

	protected volatile Map<Class<? extends LaraEvent>, Set<LaraAbstractEventSubscriber>> eventSubscriberOnceMap = new HashMap<Class<? extends LaraEvent>, Set<LaraAbstractEventSubscriber>>();

	protected volatile Map<LaraEvent, Integer> eventWaitingCounters = new HashMap<LaraEvent, Integer>();

	protected final Map<Class<? extends LaraEvent>, Long> statistics = new HashMap<Class<? extends LaraEvent>, Long>();

	protected PmParameterManager pm;

	protected LEventbus(Object id) {
		this(id, PmParameterManager.getInstance(id));
	}
	/**
	 * This is a singleton. Use of constructor is permitted. Use getInstance()
	 * to obtain a reference to the event bus.
	 */
	protected LEventbus(Object id, PmParameterManager pm) {
		this.pm = pm;
		if (this.pm == null) {
			this.pm = PmParameterManager.getInstance(null);
		}
		this.forceSequential = (Boolean) this.pm
				.getParam(LBasicPa.EVENTBUS_FORCE_SEQUENTIAL);

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
	 */
	protected void decrementWaitingCounter(LaraEvent event) {
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
	protected Integer getWaitingCounter(LaraEvent event) {
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
	protected void incrementWaitingCounter(LaraEvent event) {
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
	protected void logSubscribers(
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
	 * Notifies internal subscriber sequential. Subscribers will execute their
	 * related code sequential. Method finishes after last subscriber finishes.
	 * 
	 * @param subscribers
	 * @param event
	 */
	protected void notifyInternalSubscribersSequential(
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
	 * Notifies non-internal subscriber sequential. Subscribers will execute
	 * their related code sequential. Method finishes after last subscriber
	 * finishes.
	 * 
	 * @param subscribers
	 * @param event
	 */
	protected void notifyNoninternalSubscribersSequential(
			Set<LaraAbstractEventSubscriber> subscribers, LaraEvent event) {
		// TODO DC where referenced?
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
	protected boolean notifySubscribers(LaraEvent event, boolean hasSubscriberFlag) {
		boolean hasSubscribers = hasSubscriberFlag;
		// notify only once subscribers according to event type
		Set<LaraAbstractEventSubscriber> subscribers = null;

		if (eventSubscriberOnceMap.containsKey(event.getClass())) {
			// get subscribers set
			subscribers = eventSubscriberOnceMap.get(event.getClass());
			eventSubscriberOnceMap.remove(event.getClass());
		}

		if (eventSubscriberMap.containsKey(event.getClass())) {
			// get subscribers set
			if (subscribers != null) {
				subscribers.addAll(eventSubscriberMap.get(event.getClass()));
			} else {
				subscribers = eventSubscriberMap.get(event.getClass());
			}
		}

		if (subscribers != null) {
			hasSubscribers = true;
			logSubscribers(subscribers, event);

			logger.debug("Notifying " + subscribers.size()
					+ " subscriber(s) of event of type "
					+ event.getClass().getSimpleName());

			// notify subscribers according to event type
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
		} else if (!hasSubscribers) {
			// no subscribers - log this
			logger.warn("Event of type "
					+ event.getClass().getSimpleName()
					+ " published, but has no subscribers. Maybe you should check if this was intended.");
		}
		// if event has consecutive event fire this
		if (event instanceof LaraHasConsecutiveEvent) {
			publish(((LaraHasConsecutiveEvent) event).getConsecutiveEvent());
		}
		return hasSubscribers;
	}

	/**
	 * Notifies all subscribers at once. Subscribers will execute their related
	 * code in parallel. Method will not wait for subscribers to finish. Method
	 * will return immediately
	 * 
	 * @param subscribers
	 * @param event
	 */
	protected void notifySubscribersAsynchronous(
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
	protected void notifySubscribersSequential(
			Set<LaraAbstractEventSubscriber> subscribers, LaraEvent event) {
		// <- LOGGING
		logger.info("Notifying " + subscribers.size()
				+ " subscriber(s) sequentially ("
				+ event.getClass().getSimpleName() + ")");
		// LOGGING ->

		// internal first
		for (LaraAbstractEventSubscriber s : subscribers) {
			if (s instanceof LaraInternalEventSubscriber) {
				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("Notified subscriber (internal): " + s);
				}
				// LOGGING ->
				((LaraInternalEventSubscriber) s).onInternalEvent(event);
			}
		}
		for (LaraAbstractEventSubscriber s : subscribers) {
			if (s instanceof LaraEventSubscriber) {
				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("Notified subscriber (external): " + s);
				}
				// LOGGING ->

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
	protected void notifySubscribersSynchronous(
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

		if (logger.isDebugEnabled()) {
			logger.debug("Publisher: ", new LIdentifyCallerException(2, 1));
		}
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
				notifySubscribers(event, false);
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
			notifySubscribers(event, false);
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
		eventSubscriberOnceMap.clear();
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
	 * @param eventClass
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

		if (logger.isDebugEnabled()) {
			logger.debug("Subscriber: ", new LIdentifyCallerException(2, 1));
		}
		// LOGGING ->
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
	 */
	public void subscribeOnce(LaraAbstractEventSubscriber subscriber,
			Class<? extends LaraEvent> eventClass) {
		if (eventSubscriberOnceMap.containsKey(eventClass)) {
			// add to existing set
			Set<LaraAbstractEventSubscriber> subscribers = eventSubscriberOnceMap
					.get(eventClass);
			subscribers.add(subscriber);
		} else {
			// add to new set
			Set<LaraAbstractEventSubscriber> subscribers = new LinkedHashSet<LaraAbstractEventSubscriber>();
			subscribers.add(subscriber);
			// add to event-subscriber mapping
			eventSubscriberOnceMap.put(eventClass, subscribers);
		}

		// <- LOGGING
		logger.info("Subscribed " + subscriber + " to event "
				+ eventClass.getName());

		if (logger.isDebugEnabled()) {
			logger.error("Subscriber: ", new LIdentifyCallerException(2, 1));
		}
		// LOGGING ->
	}

	/**
	 * Unsubscribes all subscribers from the given event class. Note: This
	 * method is inefficient since it iterates over all registered events.
	 * 
	 * @param eventClass
	 */
	public void unsubscribe(Class<? extends LaraEvent> eventClass) {
		eventSubscriberMap.remove(eventClass);
		eventSubscriberOnceMap.remove(eventClass);

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

		for (Set<LaraAbstractEventSubscriber> subscribers : eventSubscriberOnceMap
				.values()) {
			subscribers.remove(subscriber);
		}

		// <- LOGGING
		logger.info("Unsubscribed " + subscriber + " from all events.");
		// LOGGING ->
	}

	/**
	 * Unsubscribe a subscriber from a class of events
	 * 
	 * @param subscriber
	 * @param eventClass
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

		if (eventSubscriberOnceMap.containsKey(eventClass)) {
			// get existing set of subscribers
			Set<LaraAbstractEventSubscriber> subscribers = eventSubscriberOnceMap
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
	 * @param event
	 *            to unsubscribe
	 */
	public void unsubscribe(LaraEvent event) {
		eventSubscriberMap.remove(event.getClass());
		eventSubscriberOnceMap.remove(event.getClass());

		// <- LOGGING
		logger.info("Unsubscribed all subscribers from event "
				+ event.getClass() + ".");
		// LOGGING ->
	}

	/**
	 * Wait for worker threads to finish. The last thread that finishes will
	 * notify the monitor.
	 * 
	 */
	protected void waitUntilWorkDone(final LaraEvent event) {
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
				// infothread.stop(); // deprecated and not required (because of
				// condition in run()) !?
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