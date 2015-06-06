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
package de.cesr.lara.testing.components.eventbus;


import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.eventbus.LaraEventSubscriber;
import de.cesr.lara.components.eventbus.events.LaraAsynchronousEvent;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.eventbus.events.LaraSequentialEvent;
import de.cesr.lara.components.eventbus.events.LaraSynchronousEvent;
import de.cesr.lara.components.eventbus.impl.LEventbus;


/**
 * 
 */
public class LEventbusTest {

	protected class TestDecrementEvent_Asynchronous implements
			LaraAsynchronousEvent {
	}

	protected class TestDecrementEvent_Sequential implements
			LaraSequentialEvent {
	}

	protected class TestDecrementEvent_Synchronous implements
			LaraSynchronousEvent {
	}

	protected class TestEndEvent_Synchronous implements LaraSynchronousEvent {
	}

	protected class TestEnvironment {
		private int	counter	= 0;

		public synchronized void decrementCounter() {
			counter--;
		}

		public synchronized int getCounter() {
			return counter;
		}

		public synchronized void incrementCounter() {
			counter++;
		}
	}

	protected class TestEnvironmentDcSpecific {
		private HashMap<LaraDecisionConfiguration, Integer> counters = new HashMap<LaraDecisionConfiguration, Integer>();

		public synchronized void decrementCounter(LaraDecisionConfiguration dc) {
			checkMap(dc);
			counters.put(dc, counters.get(dc).intValue() - 1);
		}

		public synchronized int getCounter(LaraDecisionConfiguration dc) {
			checkMap(dc);
			return counters.get(dc);
		}

		public synchronized void incrementCounter(LaraDecisionConfiguration dc) {
			checkMap(dc);
			counters.put(dc, counters.get(dc).intValue() + 1);
		}

		protected void checkMap(LaraDecisionConfiguration dc) {
			if (!counters.containsKey(dc)) {
				counters.put(dc, new Integer(0));
			}
		}
	}

	protected class TestIncrementEvent_Asynchronous implements
			LaraAsynchronousEvent {
	}

	protected class TestIncrementEvent_Sequential implements
			LaraSequentialEvent {
	}

	protected class TestIncrementEvent_Synchronous implements
			LaraSynchronousEvent {
	}

	protected class TestStartEvent_Synchronous implements LaraSynchronousEvent {
	}

	protected class TestSubscriber implements LaraEventSubscriber {
		private final TestEnvironment	testEnvironment;

		public TestSubscriber(TestEnvironment testEnvironment, LEventbus eventbus) {
			this.testEnvironment = testEnvironment;
			eventbus.subscribe(this, TestDecrementEvent_Synchronous.class);
			eventbus.subscribe(this, TestDecrementEvent_Sequential.class);
			eventbus.subscribe(this, TestDecrementEvent_Asynchronous.class);
			eventbus.subscribe(this, TestIncrementEvent_Synchronous.class);
			eventbus.subscribe(this, TestIncrementEvent_Sequential.class);
			eventbus.subscribe(this, TestIncrementEvent_Asynchronous.class);
		}

		@Override
		public <T extends LaraEvent> void onEvent(T event) {
			if (event instanceof TestDecrementEvent_Asynchronous || event instanceof TestDecrementEvent_Sequential
					|| event instanceof TestDecrementEvent_Synchronous) {
				wasteCpuTime();
				testEnvironment.decrementCounter();
			} else if (event instanceof TestIncrementEvent_Asynchronous
					|| event instanceof TestIncrementEvent_Sequential
					|| event instanceof TestIncrementEvent_Synchronous) {
				wasteCpuTime();
				testEnvironment.incrementCounter();
			}
		}

		private void wasteCpuTime() {
			double resultNobodyCaresAbout = 0d;
			String loremIpsumText =
					"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin eleifend convallis magna ut dapibus. Aliquam ornare sagittis sodales. Ut tempus vestibulum ipsum. Integer ut felis quam, sit amet viverra nisl. Fusce ac mi urna, et vestibulum quam. Praesent at dolor mauris, vel condimentum risus. Suspendisse semper ullamcorper imperdiet. Donec vehicula gravida risus, vitae tempus libero sagittis sit amet. Nunc elementum tempus mauris, ut auctor libero aliquam vel. Nullam tempus porta turpis ut elementum. Curabitur id purus massa. Integer massa sem, gravida sed congue sed, placerat quis libero. Aliquam tempor, lacus id venenatis molestie, sem tortor mattis nisl, in scelerisque erat.";

			for (int i = 0; i < numberOfWasteCpuTimeCycles; i++) {
				int count = 0;
				for (char c : loremIpsumText.toCharArray()) {
					if (c == 'a' || c == 'A') {
						count++;
					}
				}
				resultNobodyCaresAbout = (resultNobodyCaresAbout * i) + i;
				resultNobodyCaresAbout = resultNobodyCaresAbout / i;
				resultNobodyCaresAbout += count + loremIpsumText.toLowerCase().indexOf("e");
			}
		}
	}

	protected static final int numberOfSubscribers = 200;

	protected static final int numberOfWasteCpuTimeCycles = 10;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * asynch means, there might be increments/decrements after(!) assertEquals
	 */
	@SuppressWarnings("unused")
	@Test
	public void testCountAsynchronous() {
		LEventbus eventbus = LEventbus.getInstance("asynchronous");
		eventbus.publish(new TestStartEvent_Synchronous());
		TestEnvironment testEnvironment = new TestEnvironment();
		for (int i = 0; i < numberOfSubscribers; i++) {
			new TestSubscriber(testEnvironment, eventbus);
		}
		// make every subscriber increment a variable
		eventbus.publish(new TestIncrementEvent_Asynchronous());
		// check if variable has expected value >= 0 <= number of subscribers
		assertEquals(true, testEnvironment.getCounter() >= 0 && testEnvironment.getCounter() <= numberOfSubscribers);
		// make every subscriber decrement a variable
		eventbus.publish(new TestDecrementEvent_Asynchronous());
		// check if variable has expected value >= 0 <= number of subscribers
		assertEquals(true, testEnvironment.getCounter() >= 0 && testEnvironment.getCounter() <= numberOfSubscribers);
		eventbus.publish(new TestEndEvent_Synchronous());
		eventbus = null;
		testEnvironment = null;
	}

	@SuppressWarnings("unused")
	@Test
	public void testCountSequential() {
		LEventbus eventbus = LEventbus.getInstance("sequential");
		eventbus.publish(new TestStartEvent_Synchronous());
		TestEnvironment testEnvironment = new TestEnvironment();
		for (int i = 0; i < numberOfSubscribers; i++) {
			new TestSubscriber(testEnvironment, eventbus);
		}
		// make every subscriber increment a variable
		eventbus.publish(new TestIncrementEvent_Sequential());
		// check if variable has expected value = n = number of subscribers
		assertEquals(numberOfSubscribers, testEnvironment.getCounter());
		// make every subscriber decrement a variable
		eventbus.publish(new TestDecrementEvent_Sequential());
		// check if variable has expected value = n-n = 0
		assertEquals(0, testEnvironment.getCounter());
		eventbus.publish(new TestEndEvent_Synchronous());
		eventbus = null;
		testEnvironment = null;
	}

	@SuppressWarnings("unused")
	@Test
	public void testCountSynchronous() {
		LEventbus eventbus = LEventbus.getInstance("synchronous");
		eventbus.publish(new TestStartEvent_Synchronous());
		TestEnvironment testEnvironment = new TestEnvironment();
		for (int i = 0; i < numberOfSubscribers; i++) {
			new TestSubscriber(testEnvironment, eventbus);
		}
		// make every subscriber increment a variable
		eventbus.publish(new TestIncrementEvent_Synchronous());
		// check if variable has expected value = n = number of subscribers
		assertEquals(numberOfSubscribers, testEnvironment.getCounter());
		// make every subscriber decrement a variable
		eventbus.publish(new TestDecrementEvent_Synchronous());
		// check if variable has expected value = n-n = 0
		assertEquals(0, testEnvironment.getCounter());
		eventbus.publish(new TestEndEvent_Synchronous());
		eventbus = null;
		testEnvironment = null;
	}
}
