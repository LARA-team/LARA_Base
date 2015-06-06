package de.cesr.lara.testing.components.eventbus;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.impl.LDecisionConfiguration;
import de.cesr.lara.components.eventbus.LaraEventSubscriber;
import de.cesr.lara.components.eventbus.events.LAgentPreprocessEvent;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.eventbus.impl.LDcSpecificEventbus;
import de.cesr.lara.components.eventbus.impl.LEventbus;

/**
 * TODO test interplay with non-dc-specific events
 * 
 * @author Sascha Holzhauer
 * 
 */
public class LEventbusDConfigSpecificTest extends LEventbusTest {

	LDcSpecificEventbus eventb;
	TestEnvironmentDcSpecific testEnvironment;

	protected interface IncrementEvent {
	}

	protected interface DecrementEvent {
	}

	LaraDecisionConfiguration dcA = new LDecisionConfiguration();
	LaraDecisionConfiguration dcB = new LDecisionConfiguration();

	protected class TestIncrementEvent extends LAgentPreprocessEvent implements
			IncrementEvent {
		public TestIncrementEvent(
				LaraDecisionConfiguration decisionConfiguration) {
			super(decisionConfiguration);
		}
	}

	protected class TestDecrementEvent extends LAgentPreprocessEvent implements
			DecrementEvent {
		public TestDecrementEvent(
				LaraDecisionConfiguration decisionConfiguration) {
			super(decisionConfiguration);
		}
	}

	protected class TestSubscriber implements LaraEventSubscriber {
		private final TestEnvironmentDcSpecific testEnvironment;

		public TestSubscriber(TestEnvironmentDcSpecific testEnvironment,
				LDcSpecificEventbus eventbus, LaraDecisionConfiguration dc) {
			this.testEnvironment = testEnvironment;
			eventbus.subscribeOnce(this, TestIncrementEvent.class, dc);
			eventbus.subscribeOnce(this, TestDecrementEvent.class, dc);
		}

		@Override
		public <T extends LaraEvent> void onEvent(T event) {
			if (event instanceof TestDecrementEvent) {
				wasteCpuTime();
				testEnvironment.decrementCounter(((TestDecrementEvent) event)
						.getDecisionConfiguration());
			} else if (event instanceof TestIncrementEvent) {
				wasteCpuTime();
				testEnvironment.incrementCounter(((TestIncrementEvent) event)
						.getDecisionConfiguration());
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
		LEventbus.reset(null);
		this.eventb = LEventbus.getDcSpecificInstance(null);
		this.eventb.resetInstance();
		this.testEnvironment = new TestEnvironmentDcSpecific();
	}

	@After
	public void tidyUp() {
		this.testEnvironment = null;
	}

	@SuppressWarnings("unused")
	@Test
	public void testResetInstance() {
		for (int i = 0; i < numberOfSubscribers; i++) {
			new TestSubscriber(testEnvironment, this.eventb, dcA);
			new TestSubscriber(testEnvironment, this.eventb, dcB);
		}

		this.eventb.resetInstance();
		this.eventb.publish(new TestIncrementEvent(dcA));
		assertEquals(0, testEnvironment.getCounter(dcA));
	}


	@SuppressWarnings("unused")
	@Test
	public void testSubscribeOnceOneEvent() {
		for (int i = 0; i < numberOfSubscribers; i++) {
			new TestSubscriber(testEnvironment, this.eventb, dcA);
			new TestSubscriber(testEnvironment, this.eventb, dcB);
		}
		// make dcA subscribers increment a variable
		this.eventb.publish(new TestIncrementEvent(dcA));
		// check if variable has expected value: number of subscribers
		assertEquals(numberOfSubscribers, testEnvironment.getCounter(dcA));

		// make every dcA subscriber decrement a variable
		this.eventb.publish(new TestDecrementEvent(dcA));
		// check if variable has expected value: 0
		assertEquals(0, testEnvironment.getCounter(dcA));

		// publish the event a second time:
		this.eventb.publish(new TestDecrementEvent(dcA));
		// check if variable has expected value : 0 (because subscribers are
		// only subscribed for the 1st event)
		assertEquals(0, testEnvironment.getCounter(dcA));
	}

	@SuppressWarnings("unused")
	@Test
	public void testSubscribeOnceTwoEvents() {
		for (int i = 0; i < numberOfSubscribers; i++) {
			new TestSubscriber(testEnvironment, this.eventb, dcA);
			new TestSubscriber(testEnvironment, this.eventb, dcB);
		}
		// make dcA subscribers increment a variable
		this.eventb.publish(new TestIncrementEvent(dcA));
		// check if variable has expected value: number of subscribers
		assertEquals(numberOfSubscribers, testEnvironment.getCounter(dcA));
		assertEquals(0, testEnvironment.getCounter(dcB));

		// make every dcA subscriber decrement a variable
		this.eventb.publish(new TestDecrementEvent(dcA));
		// check if variable has expected value: 0
		assertEquals(0, testEnvironment.getCounter(dcA));
		assertEquals(0, testEnvironment.getCounter(dcB));

		// publish the event a second time:
		this.eventb.publish(new TestDecrementEvent(dcA));
		// check if variable has expected value : 0 (because subscribers are
		// only subscribed for the 1st event)
		assertEquals(0, testEnvironment.getCounter(dcA));
		assertEquals(0, testEnvironment.getCounter(dcB));

		// make dcA subscribers increment a variable
		this.eventb.publish(new TestIncrementEvent(dcB));
		// check if variable has expected value: number of subscribers
		assertEquals(0, testEnvironment.getCounter(dcA));
		assertEquals(numberOfSubscribers, testEnvironment.getCounter(dcB));

		// make every dcA subscriber decrement a variable
		this.eventb.publish(new TestDecrementEvent(dcB));
		// check if variable has expected value: 0
		assertEquals(0, testEnvironment.getCounter(dcA));
		assertEquals(0, testEnvironment.getCounter(dcB));

		// publish the event a second time:
		this.eventb.publish(new TestDecrementEvent(dcB));
		// check if variable has expected value : 0 (because subscribers are
		// only subscribed for the 1st event)
		assertEquals(0, testEnvironment.getCounter(dcA));
		assertEquals(0, testEnvironment.getCounter(dcB));
	}

	@SuppressWarnings("unused")
	@Test
	public void testUnsubscribeEventClass() {
		for (int i = 0; i < numberOfSubscribers; i++) {
			new TestSubscriber(testEnvironment, this.eventb, dcA);
		}
		this.eventb.unsubscribe(TestIncrementEvent.class);

		// publish the event:
		this.eventb.publish(new TestIncrementEvent(dcA));
		// check if variable has expected value : 0
		assertEquals(0, testEnvironment.getCounter(dcA));
	}

	@Test
	public void testUnsubscribeSubscriber() {
		Set<LaraEventSubscriber> subscribers = new HashSet<LaraEventSubscriber>();

		for (int i = 0; i < numberOfSubscribers; i++) {
			subscribers.add(new TestSubscriber(testEnvironment, this.eventb,
					dcA));
		}

		for (LaraEventSubscriber subscriber : subscribers) {
			this.eventb.unsubscribe(subscriber);
		}

		// publish the event:
		this.eventb.publish(new TestIncrementEvent(dcA));
		// check if variable has expected value : 0
		assertEquals(0, testEnvironment.getCounter(dcA));
	}

	@Test
	public void testUnsubscribeSubscriberForEventClass() {
		Set<LaraEventSubscriber> subscribers = new HashSet<LaraEventSubscriber>();

		for (int i = 0; i < numberOfSubscribers; i++) {
			subscribers.add(new TestSubscriber(testEnvironment, this.eventb,
					dcA));
		}

		for (LaraEventSubscriber subscriber : subscribers) {
			this.eventb.unsubscribe(subscriber, TestIncrementEvent.class);
		}

		// publish the event:
		this.eventb.publish(new TestIncrementEvent(dcA));
		// check if variable has expected value : 0
		assertEquals(0, testEnvironment.getCounter(dcA));

		this.eventb.publish(new TestIncrementEvent(dcB));
		// check if variable has expected value : 0
		assertEquals(0, testEnvironment.getCounter(dcB));
	}

	@SuppressWarnings("unused")
	@Test
	public void testUnsubscribeLaraEvent() {
		for (int i = 0; i < numberOfSubscribers; i++) {
			new TestSubscriber(testEnvironment, this.eventb, dcA);
		}
		this.eventb.unsubscribe(new TestIncrementEvent(dcA));

		// publish the event:
		this.eventb.publish(new TestIncrementEvent(dcA));
		// check if variable has expected value : 0
		assertEquals(0, testEnvironment.getCounter(dcA));
	}

	@Test
	public void testDoubleSubscription() {
		Set<LaraEventSubscriber> subscribers = new HashSet<LaraEventSubscriber>();

		for (int i = 0; i < numberOfSubscribers; i++) {
			subscribers.add(new TestSubscriber(testEnvironment, this.eventb,
					dcA));
		}

		for (LaraEventSubscriber subscriber : subscribers) {
			this.eventb.subscribe(subscriber, TestIncrementEvent.class);
		}

		// publish the event:
		this.eventb.publish(new TestIncrementEvent(dcA));
		// check if variable has expected value : 0
		assertEquals(numberOfSubscribers, testEnvironment.getCounter(dcA));
	}
}
