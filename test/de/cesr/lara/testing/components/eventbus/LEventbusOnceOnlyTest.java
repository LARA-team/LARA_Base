package de.cesr.lara.testing.components.eventbus;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.eventbus.LaraEventSubscriber;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.eventbus.impl.LEventbus;

public class LEventbusOnceOnlyTest extends LEventbusTest {

	LEventbus eventb;
	TestEnvironment testEnvironment;

	protected class TestSubscriberOnce implements LaraEventSubscriber {
		private final TestEnvironment testEnvironment;

		public TestSubscriberOnce(TestEnvironment testEnvironment,
				LEventbus eventbus) {
			this.testEnvironment = testEnvironment;
			eventbus.subscribeOnce(this, TestDecrementEvent_Synchronous.class);
			eventbus.subscribeOnce(this, TestDecrementEvent_Sequential.class);
			eventbus.subscribeOnce(this, TestDecrementEvent_Asynchronous.class);
			eventbus.subscribeOnce(this, TestIncrementEvent_Synchronous.class);
			eventbus.subscribeOnce(this, TestIncrementEvent_Sequential.class);
			eventbus.subscribeOnce(this, TestIncrementEvent_Asynchronous.class);
		}

		@Override
		public <T extends LaraEvent> void onEvent(T event) {
			if (event instanceof TestDecrementEvent_Asynchronous
					|| event instanceof TestDecrementEvent_Sequential
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
			String loremIpsumText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin eleifend convallis magna ut dapibus. Aliquam ornare sagittis sodales. Ut tempus vestibulum ipsum. Integer ut felis quam, sit amet viverra nisl. Fusce ac mi urna, et vestibulum quam. Praesent at dolor mauris, vel condimentum risus. Suspendisse semper ullamcorper imperdiet. Donec vehicula gravida risus, vitae tempus libero sagittis sit amet. Nunc elementum tempus mauris, ut auctor libero aliquam vel. Nullam tempus porta turpis ut elementum. Curabitur id purus massa. Integer massa sem, gravida sed congue sed, placerat quis libero. Aliquam tempor, lacus id venenatis molestie, sem tortor mattis nisl, in scelerisque erat.";

			for (int i = 0; i < numberOfWasteCpuTimeCycles; i++) {
				int count = 0;
				for (char c : loremIpsumText.toCharArray()) {
					if (c == 'a' || c == 'A') {
						count++;
					}
				}
				resultNobodyCaresAbout = (resultNobodyCaresAbout * i) + i;
				resultNobodyCaresAbout = resultNobodyCaresAbout / i;
				resultNobodyCaresAbout += count
						+ loremIpsumText.toLowerCase().indexOf("e");
			}
		}
	}

	@Before
	public void setUp() throws Exception {
		this.eventb = LEventbus.getInstance();
		this.eventb.resetInstance();
		this.testEnvironment = new TestEnvironment();
	}

	@After
	public void tidyUp() {
		this.testEnvironment = null;
	}

	@SuppressWarnings("unused")
	@Test
	public void testResetInstance() {
		for (int i = 0; i < numberOfSubscribers; i++) {
			new TestSubscriberOnce(testEnvironment, this.eventb);
		}

		this.eventb.resetInstance();
		this.eventb.publish(new TestIncrementEvent_Sequential());
		assertEquals(0, testEnvironment.getCounter());
	}

	@SuppressWarnings("unused")
	@Test
	public void testSubscribeOnceSequential() {
		for (int i = 0; i < numberOfSubscribers; i++) {
			new TestSubscriberOnce(testEnvironment, this.eventb);
		}
		// make every subscriber increment a variable
		this.eventb.publish(new TestIncrementEvent_Sequential());
		// check if variable has expected value: number of subscribers
		assertEquals(numberOfSubscribers, testEnvironment.getCounter());

		// make every subscriber decrement a variable
		this.eventb.publish(new TestDecrementEvent_Sequential());
		// check if variable has expected value: 0
		assertEquals(0, testEnvironment.getCounter());

		// publish the event a second time:
		this.eventb.publish(new TestIncrementEvent_Sequential());
		// check if variable has expected value : 0
		assertEquals(0, testEnvironment.getCounter());
	}

	@SuppressWarnings("unused")
	@Test
	public void testSubscribeOnceSynchronous() {
		for (int i = 0; i < numberOfSubscribers; i++) {
			new TestSubscriberOnce(testEnvironment, this.eventb);
		}
		// make every subscriber increment a variable
		this.eventb.publish(new TestIncrementEvent_Synchronous());
		// check if variable has expected value: number of subscribers
		assertEquals(numberOfSubscribers, testEnvironment.getCounter());

		// make every subscriber decrement a variable
		this.eventb.publish(new TestDecrementEvent_Synchronous());
		// check if variable has expected value : 0
		assertEquals(0, testEnvironment.getCounter());

		// publish the event a second time:
		this.eventb.publish(new TestIncrementEvent_Synchronous());
		// check if variable has expected value : 0
		assertEquals(0, testEnvironment.getCounter());
	}

	@SuppressWarnings("unused")
	@Test
	public void testSubscribeOnceAsynchronous() {
		for (int i = 0; i < numberOfSubscribers; i++) {
			new TestSubscriberOnce(testEnvironment, this.eventb);
		}
		// make every subscriber increment a variable
		this.eventb.publish(new TestIncrementEvent_Asynchronous());
		// check if variable has expected value >= 0 <= number of subscribers
		assertEquals(
				true,
				testEnvironment.getCounter() >= 0
						&& testEnvironment.getCounter() <= numberOfSubscribers);

		// make every subscriber decrement a variable
		this.eventb.publish(new TestDecrementEvent_Asynchronous());
		// check if variable has expected value >= 0 <= number of subscribers
		assertEquals(
				true,
				testEnvironment.getCounter() >= 0
						&& testEnvironment.getCounter() <= numberOfSubscribers);

		// publish the event a second time:
		this.eventb.publish(new TestIncrementEvent_Asynchronous());
		// check if variable has expected value : 0
		assertEquals(
				true,
				testEnvironment.getCounter() >= 0
						&& testEnvironment.getCounter() <= numberOfSubscribers);
	}

	@SuppressWarnings("unused")
	@Test
	public void testUnsubscribeEventClass() {
		for (int i = 0; i < numberOfSubscribers; i++) {
			new TestSubscriberOnce(testEnvironment, this.eventb);
		}
		this.eventb.unsubscribe(TestIncrementEvent_Sequential.class);

		// publish the event:
		this.eventb.publish(new TestIncrementEvent_Sequential());
		// check if variable has expected value : 0
		assertEquals(0, testEnvironment.getCounter());
	}

	@Test
	public void testUnsubscribeSubscriber() {
		Set<LaraEventSubscriber> subscribers = new HashSet<LaraEventSubscriber>();

		for (int i = 0; i < numberOfSubscribers; i++) {
			subscribers
					.add(new TestSubscriberOnce(testEnvironment, this.eventb));
		}

		for (LaraEventSubscriber subscriber : subscribers) {
			this.eventb.unsubscribe(subscriber);
		}

		// publish the event:
		this.eventb.publish(new TestIncrementEvent_Sequential());
		// check if variable has expected value : 0
		assertEquals(0, testEnvironment.getCounter());
	}

	@Test
	public void testUnsubscribeSubscriberForEventClass() {
		Set<LaraEventSubscriber> subscribers = new HashSet<LaraEventSubscriber>();

		for (int i = 0; i < numberOfSubscribers; i++) {
			subscribers
					.add(new TestSubscriberOnce(testEnvironment, this.eventb));
		}

		for (LaraEventSubscriber subscriber : subscribers) {
			this.eventb.unsubscribe(subscriber,
					TestIncrementEvent_Synchronous.class);
		}

		// publish the event:
		// publish a different event than the one that was unsubscribed from
		this.eventb.publish(new TestIncrementEvent_Sequential());
		// check if variable has expected value : 0
		assertEquals(numberOfSubscribers, testEnvironment.getCounter());

		this.eventb.publish(new TestIncrementEvent_Synchronous());
		// check if variable has expected value : 0 (no further increment)
		assertEquals(numberOfSubscribers, testEnvironment.getCounter());
	}

	@SuppressWarnings("unused")
	@Test
	public void testUnsubscribeLaraEvent() {
		for (int i = 0; i < numberOfSubscribers; i++) {
			new TestSubscriberOnce(testEnvironment, this.eventb);
		}
		this.eventb.unsubscribe(new TestIncrementEvent_Sequential());

		// publish the event:
		this.eventb.publish(new TestIncrementEvent_Sequential());
		// check if variable has expected value : 0
		assertEquals(0, testEnvironment.getCounter());
	}

	@Test
	public void testDoubleSubscription() {
		Set<LaraEventSubscriber> subscribers = new HashSet<LaraEventSubscriber>();

		for (int i = 0; i < numberOfSubscribers; i++) {
			subscribers
					.add(new TestSubscriberOnce(testEnvironment, this.eventb));
		}

		for (LaraEventSubscriber subscriber : subscribers) {
			this.eventb.subscribe(subscriber,
					TestIncrementEvent_Sequential.class);
		}
		// publish the event:
		this.eventb.publish(new TestIncrementEvent_Sequential());
		// check if variable has expected value : 0
		assertEquals(numberOfSubscribers, testEnvironment.getCounter());
	}
}
