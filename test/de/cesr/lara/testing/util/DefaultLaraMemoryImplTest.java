/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 16.12.2009
 */
package de.cesr.lara.testing.util;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.cesr.lara.components.LaraProperty;
import de.cesr.lara.components.container.LaraContainer;
import de.cesr.lara.components.container.exceptions.LInvalidTimestampException;
import de.cesr.lara.components.container.exceptions.LRemoveException;
import de.cesr.lara.components.container.exceptions.LRetrieveException;
import de.cesr.lara.components.container.memory.LaraMemory;
import de.cesr.lara.components.container.memory.LaraMemoryListener;
import de.cesr.lara.components.container.memory.impl.LDefaultMemory;
import de.cesr.lara.components.model.LaraModel;
import de.cesr.lara.components.model.impl.LModel;
import de.cesr.lara.components.model.impl.LSimulationStage;
import de.cesr.lara.components.util.LaraRandom;
import de.cesr.lara.components.util.impl.LRandomService;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;


/**
 * TODO add test for correct time stamps of properties
 * 
 * @author Sascha Holzhauer
 * @date 16.12.2009
 * 
 */
public class DefaultLaraMemoryImplTest {

	/**
	 * Logger
	 */
	static private Logger	logger	= Log4jLogger.getLogger(DefaultLaraMemoryImplTest.class);

	LaraMemory<MyProperty>	memory;

	private class MyProperty extends LaraProperty<String> {

		String	value;

		public MyProperty(String key, String value, int timestamp) {
			super(key, timestamp);
			this.value = value;
		}

		@Override
		public LaraProperty<String> getModifiedProperty(String value) {
			return new MyProperty(getKey(), value, getTimestamp());
		}

		@Override
		public String getValue() {
			return value;
		}

	}

	private class MyMemoryPropertyListener implements LaraMemoryListener {

		boolean	informed	= false;

		/**
		 * @return the informed
		 */
		public boolean isInformed() {
			return informed;
		}

		/**
		 * 
		 */
		public void resetInformed() {
			this.informed = false;
		}

		@Override
		public void memoryEventOccured(MemoryEvent event, LaraProperty<?> property) {
			informed = true;
		}
	}

	/**
	 * @throws java.lang.Exception
	 *         Created by Sascha Holzhauer on 16.12.2009
	 */
	@Before
	public void setUp() throws Exception {
		memory = new LDefaultMemory<MyProperty>();
		LModel.setNewModel(new LaraModel() {

			int	step	= 0;

			@Override
			public void step(int stepnumber) {
				step += stepnumber;
			}

			@Override
			public void setCurrentStep(int step) {
				this.step = step;
			}

			@Override
			public int getCurrentStep() {
				return step;
			}

			@Override
			public LaraRandom getLRandom() {
				return new LRandomService((int) System.currentTimeMillis());
			}

			@Override
			public void step() {
				this.step++;
			}

			@Override
			public NumberFormat getFloatPointFormat() {
				return new DecimalFormat("0.0000");
			}

			@Override
			public NumberFormat getIntegerFormat() {
				return new DecimalFormat("0000");
			}

			@Override
			public LSimulationStage getCurrentSimulationStage() {
				return LSimulationStage.UNDEFINED;
			}

			@Override
			public Date getCurrentDate() {
				return null;
			}
		});
	}

	/**
	 * @throws java.lang.Exception
	 *         Created by Sascha Holzhauer on 16.12.2009
	 */
	@After
	public void tearDown() throws Exception {
		memory = null;
	}

	/**
	 * 
	 */
	@Test
	public void testClear() {
		memory.clear();
		assertTrue(memory.isEmpty());
	}

	/**
	 * test store
	 */
	@Test
	public void testMemorize() {
		// testing capacity management:
		assertTrue(memory.getCapacity() == LaraContainer.UNLIMITED_CAPACITY);
		assertFalse(memory.isFull());
		assertTrue(memory.getSize() == 0);
		assertTrue(memory.isEmpty());

		// testing wrong time steps:
		try {
			memory.memorize(new MyProperty("IAmInvalid", "", -1));
			fail("This should have raised an exception.");
		} catch (LInvalidTimestampException litse) {

		}
		assertTrue(memory.isEmpty());

		storeSixStandardEntries(1);
		assertFalse("Memory may not be empty after six properties were inserted", memory.isEmpty());
		assertTrue("Memory shall contain six inserted proeprties", memory.getSize() == 6);

		memory.memorize(new MyProperty("key01", "Haha", 1));
		assertTrue("since the new property overwrites an exisiting one (same key, same time step) the size is equal",
				memory.getSize() == 6);
		assertTrue("the memory shall recall the recently inserted property", memory.recall("key01", 1).value == "Haha");

		storeSixStandardEntries(2);

		assertFalse("For unlimited capacity, isFull() should always return false", memory.isFull());

		assertTrue("Additionally, six properties were added at another time step", memory.getSize() == 12);
		MyProperty property1 = memory.recall("key06", 1);
		MyProperty property2 = memory.recall("key06", 2);
		assertNotSame("Proeprties that are inserted at different time steps are not the same", property1, property2);

		MyProperty propertyX = memory.recall("key06");
		assertSame(property2, propertyX);
	}

	/**
	 * 
	 */
	@Test
	public void testRecall() {
		storeSixStandardEntries(1);
		memory.memorize(new MyProperty("key01", "newer", 2));

		MyProperty property = memory.recall("key02", 1);
		assertNotNull("teh racalled property was inserted before and must not be null", property);
		assertTrue(property.getKey() == "key02");
		assertTrue(property.getValue() == "value02");
		assertTrue(property.getTimestamp() == 1);

		try {
			memory.recall("IDontExist");
			fail("This should have raised an exception.");
		} catch (LRetrieveException lre) {

		}

		try {
			memory.recall("key02", 2);
			fail("This should have raised an exception.");
		} catch (LRetrieveException lre) {

		}

		memory.memorize(new MyProperty("key05", "lastAdded", 5));
		assertEquals("The last added property of key key05 hast value lastAdded", "lastAdded", memory.recall("key05")
				.getValue());
	}

	/**
	 * 
	 */
	@Test
	public void testForget() {
		storeSixStandardEntries(1);

		memory.forget("key03", 1);
		assertTrue("6 proeprties inserted - 1 forgot = 5", memory.getSize() == 5);
		memory.forgetAll("key04");
		assertTrue("5 properties - 1 forgot = 4", memory.getSize() == 4);

		try {
			memory.forget("key01", 2);
			fail("This should have raised an exception since at time step 2 no property was inserted");
		} catch (LRemoveException lre) {
		}

		try {
			memory.forgetAll("IDontExist");
			fail("This should have raised an exception since the property to forget does not exist.");
		} catch (LRemoveException lre) {
		}
	}

	/**
	 * 
	 */
	@Test
	public void testForgetAll() {
		storeSixStandardEntries(1);
		memory.memorize(new MyProperty("key01", "value01", 2));
		assertTrue("6 properties + 1 property = 7", memory.getSize() == 7);
		memory.forgetAll("key01");
		assertTrue("7 properties - 2 property = 5", memory.getSize() == 5);
	}

	/**
	 * 
	 */
	@Test
	public void testRetention() {
		assertTrue(memory.getDefaultRetentionTime() == LaraMemory.UNLIMITED_RETENTION);
		memory.setDefaultRetentionTime(2);
		assertTrue("Default retention time was set to 2", memory.getDefaultRetentionTime() == 2);

		storeSixStandardEntries(LModel.getModel().getCurrentStep());
		assertTrue(memory.getSize() == 6);

		logger.info("step " + LModel.getModel().getCurrentStep());

		int retentionTime = memory.getRetentionTime(memory.recall("key01"));
		assertTrue("defautl retention tiem is 2", retentionTime == 2);

		LModel.getModel().step(1);
		logger.info("step " + LModel.getModel().getCurrentStep());
		storeSixStandardEntries(LModel.getModel().getCurrentStep());
		assertTrue("2x 6 properties added", memory.getSize() == 12);

		LModel.getModel().step(1);
		logger.info("step " + LModel.getModel().getCurrentStep());
		assertTrue("in step 3 properties memorised in step 1 shall be forgotten (12 - 6 = 6)", memory.getSize() == 6);

		LModel.getModel().step(1);
		logger.info("step " + LModel.getModel().getCurrentStep());
		assertTrue("all properties memorised in step 1 and 2 shall be forgotten in step 4", memory.isEmpty());

		/* ***** */

		MyProperty property07 = new MyProperty("key07", "value07", LModel.getModel().getCurrentStep());
		memory.memorize(property07, 10);
		assertTrue(memory.getRetentionTime(property07) == 10);

		LModel.getModel().step(4);
		assertTrue(memory.getRetentionTime(property07) == 6);
	}

	/**
	 * 
	 */
	@Test
	public void testCustomRetention() {
		final int CUSTOM_RETENTION = 2;
		memory = new LDefaultMemory<MyProperty>(CUSTOM_RETENTION);

		assertTrue(memory.getDefaultRetentionTime() == CUSTOM_RETENTION);
		assertTrue("Default retention time was set to " + CUSTOM_RETENTION,
				memory.getDefaultRetentionTime() == CUSTOM_RETENTION);

		storeSixStandardEntries(LModel.getModel().getCurrentStep());
		assertTrue(memory.getSize() == 6);

		logger.info("step " + LModel.getModel().getCurrentStep());

		int retentionTime = memory.getRetentionTime(memory.recall("key01"));
		assertTrue("defautl retention tiem is " + CUSTOM_RETENTION, retentionTime == CUSTOM_RETENTION);

		LModel.getModel().step(1);
		logger.info("step " + LModel.getModel().getCurrentStep());
		storeSixStandardEntries(LModel.getModel().getCurrentStep());
		assertTrue("2x 6 properties added", memory.getSize() == 12);

		LModel.getModel().step(CUSTOM_RETENTION - 1);
		logger.info("step " + LModel.getModel().getCurrentStep());
		assertTrue("in step " + (CUSTOM_RETENTION + 1)
				+ " properties memorised in step 1 shall be forgotten (12 - 6 = 6)", memory.getSize() == 6);

		LModel.getModel().step(1);
		logger.info("step " + LModel.getModel().getCurrentStep());
		assertTrue("all properties memorised in step 1 and 2 shall be forgotten in step " + (CUSTOM_RETENTION + 2),
				memory.isEmpty());

		/* ***** */

		MyProperty property07 = new MyProperty("key07", "value07", LModel.getModel().getCurrentStep());
		memory.memorize(property07, 10);
		assertTrue(memory.getRetentionTime(property07) == 10);

		LModel.getModel().step(4);
		assertTrue(memory.getRetentionTime(property07) == 6);
	}

	/**
	 * testRecallingInbetweenTimeSteps
	 */
	@Test
	@Ignore
	// not yet implemented (see todo.html)
	public void testRecallingInbetweenTimeSteps() {
		storeSixStandardEntries(1);
		memory.memorize(new MyProperty("key01", "newer", 3));
		assertEquals(
				"Recalling an item for a step inbetween two memorizations should yield the item before the given step",
				"value01", memory.recall("key01", 2).getValue());
	}

	/**
	 * TODO test refreshing props that have not been memorised!
	 */
	@Test
	public void testRefresh() {
		memory.setDefaultRetentionTime(2);
		assertTrue("Default retention time was set to 2", memory.getDefaultRetentionTime() == 2);

		storeSixStandardEntries(LModel.getModel().getCurrentStep());
		assertTrue(memory.getSize() == 6);

		logger.info("step " + LModel.getModel().getCurrentStep());

		int retentionTime = memory.getRetentionTime(memory.recall("key01"));
		assertTrue("defautl retention tiem is 2", retentionTime == 2);

		LModel.getModel().step(1);
		logger.info("step " + LModel.getModel().getCurrentStep());
		storeSixStandardEntries(LModel.getModel().getCurrentStep());
		assertTrue("2x 6 properties added", memory.getSize() == 12);

		memory.refresh("key01");

		LModel.getModel().step(1);
		logger.info("step " + LModel.getModel().getCurrentStep());
		assertTrue(
				"in step 3 properties memorised in step 1 shall be forgotten except the refreshed one (12 - 6 + 1 = 7)",
				memory.getSize() == 6);

		LModel.getModel().step(1);
		logger.info("step " + LModel.getModel().getCurrentStep());
		assertTrue("all properties memorised in step 1 and 2 shall be forgotten in step 4", memory.isEmpty());
	}

	/**
	 * 
	 */
	@Test
	public void testIterator() {
		storeSixStandardEntries(1);
		storeSixStandardEntries(2);
		int size = 0;
		for (@SuppressWarnings("unused")
		MyProperty property : memory) {
			size++;
		}
		assertTrue("iterator needs to iterate over 12 proeprties", size == 12);
	}

	/**
	 * 
	 */
	@Test
	public void testMemoryProeprtyListenerManagement() {
		MyMemoryPropertyListener listener = new MyMemoryPropertyListener();
		memory.addMemoryPropertyObserver(LaraMemoryListener.MemoryEvent.PROPERTY_MEMORIZED, listener);
		memory.memorize(new MyProperty("key01", "value01", 1));
		assertTrue(listener.isInformed());
		listener.resetInformed();

		memory.recall("key01");
		assertFalse(listener.isInformed());

		memory.forgetAll("key01");
		assertFalse(listener.isInformed());

		memory.removeMemoryPropertyObserver(LaraMemoryListener.MemoryEvent.PROPERTY_MEMORIZED, listener);
		memory.addMemoryPropertyObserver(LaraMemoryListener.MemoryEvent.PROPERTY_RECALLED, listener);
		memory.memorize(new MyProperty("key01", "value01", 1));
		assertFalse(listener.isInformed());

		memory.recall("key01");
		assertTrue(listener.isInformed());
		listener.resetInformed();

		memory.recall("key01", 1);
		assertTrue(listener.isInformed());
		listener.resetInformed();

		/*
		 * not yet implemented memory.recall(MyProperty.class, "key01", 1); assertTrue(listener.isInformed());
		 * listener.resetInformed();
		 */

		memory.recallAll("key01");
		assertTrue(listener.isInformed());
		listener.resetInformed();

		/*
		 * not yet implemented memory.recallAll(MyProperty.class, "key01"); assertFalse(listener.isInformed());
		 * listener.resetInformed();
		 */

		memory.forgetAll("key01");
		assertFalse(listener.isInformed());

		memory.removeMemoryPropertyObserver(LaraMemoryListener.MemoryEvent.PROPERTY_RECALLED, listener);
		memory.addMemoryPropertyObserver(LaraMemoryListener.MemoryEvent.PROPERTY_FORGOTTEN, listener);
		memory.memorize(new MyProperty("key01", "value01", 1));
		assertFalse(listener.isInformed());

		memory.recall("key01");
		assertFalse(listener.isInformed());
		listener.resetInformed();

		memory.forgetAll("key01");
		assertTrue(listener.isInformed());
		listener.resetInformed();
		memory.memorize(new MyProperty("key01", "value01", 1));

		memory.forget("key01", 1);
		assertTrue(listener.isInformed());
		listener.resetInformed();
		MyProperty property = new MyProperty("key01", "value01", 1);
		memory.memorize(property);

		memory.forget(property);
		assertTrue(listener.isInformed());
		listener.resetInformed();
		property = new MyProperty("key01", "value01", 1);
		memory.memorize(property);

		Collection<MyProperty> props = new ArrayList<MyProperty>();
		props.add(property);
		memory.forgetAll(props);
		assertTrue(listener.isInformed());
		listener.resetInformed();
		memory.memorize(new MyProperty("key01", "value01", 1));

		memory.clear();
		assertTrue(listener.isInformed());
		listener.resetInformed();
		memory.memorize(new MyProperty("key01", "value01", 1));

	}

	private void storeSixStandardEntries(int step) {
		memory.memorize(new MyProperty("key01", "value01", step));
		memory.memorize(new MyProperty("key02", "value02", step));
		memory.memorize(new MyProperty("key03", "value03", step));
		memory.memorize(new MyProperty("key04", "value04", step));
		memory.memorize(new MyProperty("key05", "value05", step));
		memory.memorize(new MyProperty("key06", "value06", step));
	}
}
