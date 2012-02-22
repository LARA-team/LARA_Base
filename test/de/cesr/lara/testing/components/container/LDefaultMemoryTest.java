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
package de.cesr.lara.testing.components.container;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;

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
import de.cesr.lara.components.eventbus.events.LModelStepEvent;
import de.cesr.lara.components.eventbus.impl.LEventbus;
import de.cesr.lara.components.model.impl.LModel;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;
import de.cesr.lara.testing.LTestUtils;
import de.cesr.lara.testing.components.container.LContainerTestUtils.LSubTestProperty;
import de.cesr.lara.testing.components.container.LContainerTestUtils.LTestProperty;

/**
 * @author Sascha Holzhauer
 * @date 16.12.2009
 * 
 */
public class LDefaultMemoryTest {

	/**
	 * Logger
	 */
	static private Logger	logger	= Log4jLogger.getLogger(LDefaultMemoryTest.class);

	LaraMemory<LTestProperty> memory;


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
		public void memoryEventOccured(MemoryEvent event, LaraProperty<?,?> property) {
			informed = true;
		}
	}

	/**
	 * @throws java.lang.Exception
	 *         Created by Sascha Holzhauer on 16.12.2009
	 */
	@Before
	public void setUp() throws Exception {
		memory = new LDefaultMemory<LTestProperty>();
		LTestUtils.initTestModel();
		LEventbus.getInstance().publish(new LModelStepEvent());
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

		assertTrue(memory.isEmpty());

		LContainerTestUtils.storeSixStandardEntries(memory);
		LTestProperty invalid = new LTestProperty("IBecomeInvalid", "");

		assertFalse("Memory may not be empty after six properties were inserted", memory.isEmpty());
		assertTrue("Memory shall contain six inserted proeprties", memory.getSize() == 6);

		memory.memorize(new LTestProperty("key01", "Haha"));
		assertTrue("since the new property overwrites an exisiting one (same key, same time step) the size is equal",
				memory.getSize() == 6);
		assertTrue("the memory shall recall the recently inserted property", memory.recall("key01", 1).value == "Haha");

		LEventbus.getInstance().publish(new LModelStepEvent());

		LContainerTestUtils.storeSixStandardEntries(memory);

		assertFalse("For unlimited capacity, isFull() should always return false", memory.isFull());

		assertTrue("Additionally, six properties were added at another time step", memory.getSize() == 12);
		LTestProperty property1 = memory.recall("key06", 1);
		LTestProperty property2 = memory.recall("key06", 2);
		assertNotSame("Proeprties that are inserted at different time steps are not the same", property1, property2);

		LTestProperty propertyX = memory.recall("key06");
		assertSame(property2, propertyX);

		// testing wrong time steps:
		try {
			memory.memorize(invalid);
			fail("This should have raised an exception.");
		} catch (LInvalidTimestampException litse) {
		}
	}

	/**
	 * 
	 */
	@Test
	public void testRecallByKey() {
		LContainerTestUtils.storeSixStandardEntries(memory);

		LEventbus.getInstance().publish(new LModelStepEvent());

		memory.memorize(new LTestProperty("key01", "newer"));

		LTestProperty property = memory.recall("key02");
		assertNotNull(
				"The racalled property was inserted before and must not be null",
				property);
		assertTrue(property.getKey() == "key02");
		assertTrue(property.getValue() == "value02");
		assertTrue(property.getTimestamp() == 1);

		assertEquals("newer", memory.recall("key01").getValue());

		try {
			memory.recall("IDontExist");
			fail("This should have raised an exception.");
		} catch (LRetrieveException lre) {
		}
	}

	/**
	 * 
	 */
	@Test
	public void testRecallByKeyStep() {
		LContainerTestUtils.storeSixStandardEntries(memory);

		LEventbus.getInstance().publish(new LModelStepEvent());

		memory.memorize(new LTestProperty("key01", "newer"));

		LTestProperty property = memory.recall("key02", 1);
		assertNotNull(
				"The racalled property was inserted before and must not be null",
				property);
		assertTrue(property.getKey() == "key02");
		assertTrue(property.getValue() == "value02");
		assertTrue(property.getTimestamp() == 1);

		assertEquals("newer", memory.recall("key01", 2).getValue());

		try {
			memory.recall("IDontExist", 1);
			fail("This should have raised an exception.");
		} catch (LRetrieveException lre) {
		}

		try {
			memory.recall("key02", 2);
			fail("This should have raised an exception.");
		} catch (LRetrieveException lre) {
		}
	}

	@Test
	public void testRecallByClass() {
		LTestProperty prop1 = new LTestProperty("key01", "value01");
		memory.memorize(prop1);
		LSubTestProperty prop2 = new LSubTestProperty("key02", "value01");
		memory.memorize(prop2);

		LEventbus.getInstance().publish(new LModelStepEvent());

		LTestProperty prop3 = new LTestProperty("key03", "value01");
		memory.memorize(prop3);

		assertEquals(prop2, memory.recall(LSubTestProperty.class, "key02"));
		assertEquals(prop1, memory.recall(LTestProperty.class, "key01"));
		assertEquals(prop3, memory.recall(LTestProperty.class, "key03"));
		
		assertEquals(prop2, memory.recall(LTestProperty.class, "key02"));
		
		try {
			memory.recall(LSubTestProperty.class, "key01");
			fail("This should have raised an exception.");
		} catch (LRetrieveException lre) {
		}
	}

	@Test
	public void testRecallAllByClass() {
		LTestProperty prop1 = new LTestProperty("key01", "value01");
		memory.memorize(prop1);
		LSubTestProperty prop2 = new LSubTestProperty("key02", "value01");
		memory.memorize(prop2);

		LEventbus.getInstance().publish(new LModelStepEvent());

		LTestProperty prop3 = new LTestProperty("key01", "value01");
		memory.memorize(prop3);

		Collection<LTestProperty> properties = memory
				.recallAll(LTestProperty.class);
		assertTrue(properties.contains(prop1));
		assertTrue(properties.contains(prop3));
		assertTrue(properties.contains(prop2));
		assertEquals(3, properties.size());

		Collection<LSubTestProperty> subProperties = memory
				.recallAll(LSubTestProperty.class);
		assertFalse(subProperties.contains(prop1));
		assertTrue(subProperties.contains(prop2));
		assertFalse(subProperties.contains(prop3));
		assertEquals(1, subProperties.size());

		subProperties = memory.recallAll(LSubTestProperty.class);
		assertFalse(subProperties.contains(prop1));
		assertFalse(subProperties.contains(prop3));
		assertTrue(subProperties.contains(prop2));
		assertEquals(1, subProperties.size());
	}

	@Test
	public void testRecallByClassStep() {
		LTestProperty prop1 = new LTestProperty("key01", "value01");
		memory.memorize(prop1);
		LSubTestProperty prop2 = new LSubTestProperty("key02", "value01");
		memory.memorize(prop2);
		
		LEventbus.getInstance().publish(new LModelStepEvent());
		
		LTestProperty prop3 = new LTestProperty("key03", "value01");
		memory.memorize(prop3);
		
		assertEquals(prop2, memory.recall(LSubTestProperty.class, "key02", 1));
		assertEquals(prop1, memory.recall(LTestProperty.class, "key01", 1));
		assertEquals(prop3, memory.recall(LTestProperty.class, "key03", 2));

		assertEquals(prop2, memory.recall(LTestProperty.class, "key02", 1));

		try {
			memory.recall(LSubTestProperty.class, "key01", 1);
			fail("This should have raised an exception.");
		} catch (LRetrieveException lre) {
		}

		try {
			memory.recall(LSubTestProperty.class, "key01", 1);
			fail("This should have raised an exception.");
		} catch (LRetrieveException lre) {
		}

		try {
			memory.recall(LSubTestProperty.class, "key01", 2);
			fail("This should have raised an exception.");
		} catch (LRetrieveException lre) {
		}
	}

	/**
	 * 
	 */
	@Test
	public void testRecallAllByClassKey() {
		LTestProperty prop1 = new LTestProperty("key01", "value01");
		memory.memorize(prop1);
		LSubTestProperty prop2 = new LSubTestProperty("key02", "value01");
		memory.memorize(prop2);

		LEventbus.getInstance().publish(new LModelStepEvent());

		LTestProperty prop3 = new LTestProperty("key01", "value01");
		memory.memorize(prop3);

		Collection<LTestProperty> properties = memory.recallAll(
				LTestProperty.class, "key01");
		assertTrue(properties.contains(prop1));
		assertTrue(properties.contains(prop3));
		assertFalse(properties.contains(prop2));
		assertEquals(2, properties.size());

		Collection<LSubTestProperty> subProperties = memory.recallAll(
				LSubTestProperty.class, "key01");
		assertFalse(subProperties.contains(prop1));
		assertFalse(subProperties.contains(prop3));
		assertFalse(subProperties.contains(prop2));
		assertEquals(0, subProperties.size());

		subProperties = memory.recallAll(LSubTestProperty.class, "key02");
		assertFalse(subProperties.contains(prop1));
		assertFalse(subProperties.contains(prop3));
		assertTrue(subProperties.contains(prop2));
		assertEquals(1, subProperties.size());
	}

	/**
	 * 
	 */
	@Test
	public void testRecallAll() {
		LTestProperty prop1 = new LTestProperty("key01", "value01");
		memory.memorize(prop1);
		LTestProperty prop2 = new LTestProperty("key02", "value01");
		memory.memorize(prop2);

		LEventbus.getInstance().publish(new LModelStepEvent());

		LTestProperty prop3 = new LTestProperty("key01", "value01");
		memory.memorize(prop3);

		Collection<LTestProperty> properties = memory.recallAll("key01");
		assertTrue(properties.contains(prop1));
		assertTrue(properties.contains(prop3));
		assertFalse(properties.contains(prop2));
		assertEquals(2, properties.size());

		try {
			memory.recallAll("IDontExist");
			fail("This should have raised an exception.");
		} catch (LRetrieveException lre) {
		}

		try {
			memory.recallAll("key02");
		} catch (LRetrieveException lre) {
			fail("This should not have raised an exception.");
		}
	}

	/**
	 * 
	 */
	@Test
	public void testForget() {
		LContainerTestUtils.storeSixStandardEntries(memory);

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
		LContainerTestUtils.storeSixStandardEntries(memory);
		LEventbus.getInstance().publish(new LModelStepEvent());
		memory.memorize(new LTestProperty("key01", "value01"));
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

		LContainerTestUtils.storeSixStandardEntries(memory);
		assertTrue(memory.getSize() == 6);

		logger.info("step " + LModel.getModel().getCurrentStep());

		int retentionTime = memory.getRetentionTime(memory.recall("key01"));
		assertTrue("defautl retention tiem is 2", retentionTime == 2);

		LEventbus.getInstance().publish(new LModelStepEvent());

		logger.info("step " + LModel.getModel().getCurrentStep());
		LContainerTestUtils.storeSixStandardEntries(memory);
		assertTrue("2x 6 properties added", memory.getSize() == 12);

		LEventbus.getInstance().publish(new LModelStepEvent());

		logger.info("step " + LModel.getModel().getCurrentStep());
		assertTrue("in step 3 properties memorised in step 1 shall be forgotten (12 - 6 = 6)", memory.getSize() == 6);

		LModel.getModel().step(1);
		logger.info("step " + LModel.getModel().getCurrentStep());
		assertTrue("all properties memorised in step 1 and 2 shall be forgotten in step 4", memory.isEmpty());

		LTestProperty property07 = new LTestProperty("key07", "value07");
		memory.memorize(property07, 10);
		assertTrue(memory.getRetentionTime(property07) == 10);

		for (int i = 0; i < 4; i++) {
			LEventbus.getInstance().publish(new LModelStepEvent());
		}

		assertTrue(memory.getRetentionTime(property07) == 6);
	}

	/**
	 * 
	 */
	@Test
	public void testCustomRetention() {
		final int CUSTOM_RETENTION = 2;
		memory = new LDefaultMemory<LTestProperty>(CUSTOM_RETENTION);

		assertTrue(memory.getDefaultRetentionTime() == CUSTOM_RETENTION);
		assertTrue("Default retention time was set to " + CUSTOM_RETENTION,
				memory.getDefaultRetentionTime() == CUSTOM_RETENTION);

		LContainerTestUtils.storeSixStandardEntries(memory);
		assertTrue(memory.getSize() == 6);

		logger.info("Step " + LModel.getModel().getCurrentStep());

		int retentionTime = memory.getRetentionTime(memory.recall("key01"));
		assertTrue("defautl retention tiem is " + CUSTOM_RETENTION, retentionTime == CUSTOM_RETENTION);

		LEventbus.getInstance().publish(new LModelStepEvent());

		logger.info("step " + LModel.getModel().getCurrentStep());
		LContainerTestUtils.storeSixStandardEntries(memory);
		assertTrue("2x 6 properties added", memory.getSize() == 12);

		for (int i = 1; i < CUSTOM_RETENTION; i++) {
			LEventbus.getInstance().publish(new LModelStepEvent());
		}

		logger.info("step " + LModel.getModel().getCurrentStep());
		assertTrue("in step " + (CUSTOM_RETENTION + 1)
				+ " properties memorised in step 1 shall be forgotten (12 - 6 = 6)", memory.getSize() == 6);

		LEventbus.getInstance().publish(new LModelStepEvent());

		logger.info("step " + LModel.getModel().getCurrentStep());
		assertTrue("all properties memorised in step 1 and 2 shall be forgotten in step " + (CUSTOM_RETENTION + 2),
				memory.isEmpty());

		/* ***** */

		LTestProperty property07 = new LTestProperty("key07", "value07");
		memory.memorize(property07, 10);
		assertTrue(memory.getRetentionTime(property07) == 10);

		for (int i = 0; i < 4; i++) {
			LEventbus.getInstance().publish(new LModelStepEvent());
		}

		assertTrue(memory.getRetentionTime(property07) == 6);
	}

	/**
	 * testRecallingInbetweenTimeSteps
	 */
	@Test
	@Ignore
	// not yet implemented (see todo.html)
	public void testRecallingInbetweenTimeSteps() {
		LContainerTestUtils.storeSixStandardEntries(memory);

		LEventbus.getInstance().publish(new LModelStepEvent());
		LEventbus.getInstance().publish(new LModelStepEvent());

		memory.memorize(new LTestProperty("key01", "newer"));

		assertEquals(
				"Recalling an item for a step inbetween two memorizations should yield the item before the given step",
				"value01", memory.recall("key01", 2).getValue());
	}

	@Test
	public void testRefreshByKey() {
		memory.setDefaultRetentionTime(2);
		assertEquals("Default retention time was set to 2", 2,
				memory.getDefaultRetentionTime());

		LContainerTestUtils.storeSixStandardEntries(memory);
		assertEquals(6, memory.getSize());

		int retentionTime = memory.getRetentionTime(memory.recall("key01"));
		assertEquals("defautl retention tiem is 2", 2, retentionTime);

		LEventbus.getInstance().publish(new LModelStepEvent());
		assertEquals(6, memory.getSize());

		LContainerTestUtils.storeSixStandardEntries(memory);
		assertEquals("2x 6 properties added", 12, memory.getSize());

		LEventbus.getInstance().publish(new LModelStepEvent());
		assertEquals(
				"In step 3, properties memorised in step 1 shall be forgotten "
						+ "(Refresh should have affected second memorized property) (12 - 6 = 6)",
				6, memory.getSize());
		logger.error(memory);
		memory.refresh("key01");
		logger.error(memory);

		LEventbus.getInstance().publish(new LModelStepEvent());
		assertEquals(
				"all properties memorised in step 1 and 2 shall be forgotten in step 4 except refreshed"
						+ "property.", 1, memory.getSize());

		LEventbus.getInstance().publish(new LModelStepEvent());
		assertTrue(
				"All properties memorised in step 1 and 2 and refreshed property shall be forgotten in step 5",
				memory.isEmpty());

		// refreshing props that have not been memorised
		try {
			memory.refresh("NotInMemory");
			fail("This should have raised an exception since the prperty to refresh is not in memory.");
		} catch (LRetrieveException lre) {
		}
	}

	@Test
	public void testContainsProperty() {
		LTestProperty prop1 = new LTestProperty("Test01", "Value01");
		LTestProperty prop2 = new LTestProperty("Test02", "Value02");

		memory.memorize(prop1);

		assertTrue(memory.contains(prop1));
		assertFalse(memory.contains(prop2));

		memory.memorize(prop2);
		assertTrue(memory.contains(prop2));

		memory.forget(prop1);
		assertFalse(memory.contains(prop1));
		assertTrue(memory.contains(prop2));
	}

	@Test
	public void testContainsKey() {
		LContainerTestUtils.storeSixStandardEntries(memory);
		assertTrue(memory.contains("key01"));
		assertTrue(memory.contains("key02"));
		assertTrue(memory.contains("key03"));
		assertTrue(memory.contains("key04"));
		assertTrue(memory.contains("key05"));
		assertTrue(memory.contains("key06"));

		assertTrue(memory.contains("key01", 1));
		assertTrue(memory.contains("key02", 1));
		assertTrue(memory.contains("key03", 1));
		assertTrue(memory.contains("key04", 1));
		assertTrue(memory.contains("key05", 1));
		assertTrue(memory.contains("key06", 1));

		assertFalse(memory.contains("key01", 2));
		assertFalse(memory.contains("key02", 3));
		assertFalse(memory.contains("key03", 4));
		assertFalse(memory.contains("key04", 5));
		assertFalse(memory.contains("key05", 6));
		assertFalse(memory.contains("key06", 7));

		LEventbus.getInstance().publish(new LModelStepEvent());

		assertTrue(memory.contains("key01"));
		assertTrue(memory.contains("key02"));
		assertTrue(memory.contains("key03"));
		assertTrue(memory.contains("key04"));
		assertTrue(memory.contains("key05"));
		assertTrue(memory.contains("key06"));

		memory.forgetAll("key01");
		assertFalse(memory.contains("key01"));
		assertFalse(memory.contains("key01", 1));

		assertTrue(memory.contains("key02"));
		assertTrue(memory.contains("key02", 1));

		assertTrue(memory.contains("key03"));
		assertTrue(memory.contains("key03", 1));

		assertTrue(memory.contains("key04"));
		assertTrue(memory.contains("key04", 1));

		assertTrue(memory.contains("key05"));
		assertTrue(memory.contains("key05", 1));

		assertTrue(memory.contains("key06"));
		assertTrue(memory.contains("key06", 1));

		memory.forgetAll("key02");
		assertFalse(memory.contains("key02"));

		memory.forgetAll("key03");
		assertFalse(memory.contains("key03"));

		memory.forgetAll("key04");
		assertFalse(memory.contains("key04"));

		memory.forgetAll("key05");
		assertFalse(memory.contains("key05"));

		memory.forgetAll("key06");
		assertFalse(memory.contains("key06"));
	}

	@Test
	public void testRefreshByProperty() {
		LTestProperty prop1 = new LTestProperty("Test1", "value01");
		LTestProperty prop2 = new LTestProperty("Test2", "value02");

		memory.setDefaultRetentionTime(2);

		memory.memorize(prop1);
		memory.memorize(prop2);
		assertEquals(2, memory.getSize());

		LEventbus.getInstance().publish(new LModelStepEvent());

		assertEquals("2 properties in memory", 2, memory.getSize());
		memory.refresh(prop2);

		LEventbus.getInstance().publish(new LModelStepEvent());

		// "old" object was refreshed!
		assertFalse(memory.contains(prop2));
		assertTrue(memory.contains("Test2"));
		assertEquals("1 property in memory", 1, memory.getSize());

		LEventbus.getInstance().publish(new LModelStepEvent());
		assertEquals("1 property in memory", 0, memory.getSize());

		// refreshing props that have not been memorised
		try {
			memory.refresh(prop1);
			fail("This should have raised an exception since the prperty to refresh is not in memory.");
		} catch (LRemoveException lre) {
		}
	}

	@Test
	public void testRecallLastEntryForKey() {
		// insert property
		LTestProperty prop1 = new LTestProperty("key01", "value01");
		memory.memorize(prop1);

		// advance model
		LEventbus.getInstance().publish(new LModelStepEvent());

		// insert property
		LTestProperty prop2 = new LTestProperty("key01", "value02");
		memory.memorize(prop2);

		// retrieve last property and check
		assertEquals("The last added property of key key01 has value value02",
				prop2, memory.recall("key01"));
	}

	/**
	 * 
	 */
	@Test
	public void testIterator() {
		LContainerTestUtils.storeSixStandardEntries(memory);
		LEventbus.getInstance().publish(new LModelStepEvent());
		LContainerTestUtils.storeSixStandardEntries(memory);
		int size = 0;
		for (@SuppressWarnings("unused")
		LTestProperty property : memory) {
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
		memory.memorize(new LTestProperty("key01", "value01"));
		assertTrue(listener.isInformed());
		listener.resetInformed();

		memory.recall("key01");
		assertFalse(listener.isInformed());

		memory.forgetAll("key01");
		assertFalse(listener.isInformed());

		memory.removeMemoryPropertyObserver(LaraMemoryListener.MemoryEvent.PROPERTY_MEMORIZED, listener);
		memory.addMemoryPropertyObserver(LaraMemoryListener.MemoryEvent.PROPERTY_RECALLED, listener);
		memory.memorize(new LTestProperty("key01", "value01"));
		assertFalse(listener.isInformed());

		memory.recall("key01");
		assertTrue(listener.isInformed());
		listener.resetInformed();

		memory.recall("key01", 1);
		assertTrue(listener.isInformed());
		listener.resetInformed();

		/*
		 * not yet implemented memory.recall(LTestProperty.class, "key01", 1);
		 * assertTrue(listener.isInformed()); listener.resetInformed();
		 */

		memory.recallAll("key01");
		assertTrue(listener.isInformed());
		listener.resetInformed();

		/*
		 * not yet implemented memory.recallAll(LTestProperty.class, "key01");
		 * assertFalse(listener.isInformed()); listener.resetInformed();
		 */

		memory.forgetAll("key01");
		assertFalse(listener.isInformed());

		memory.removeMemoryPropertyObserver(LaraMemoryListener.MemoryEvent.PROPERTY_RECALLED, listener);
		memory.addMemoryPropertyObserver(LaraMemoryListener.MemoryEvent.PROPERTY_FORGOTTEN, listener);
		memory.memorize(new LTestProperty("key01", "value01"));
		assertFalse(listener.isInformed());

		memory.recall("key01");
		assertFalse(listener.isInformed());
		listener.resetInformed();

		memory.forgetAll("key01");
		assertTrue(listener.isInformed());
		listener.resetInformed();
		memory.memorize(new LTestProperty("key01", "value01"));

		memory.forget("key01", 1);
		assertTrue(listener.isInformed());
		listener.resetInformed();
		LTestProperty property = new LTestProperty("key01", "value01");
		memory.memorize(property);

		memory.forget(property);
		assertTrue(listener.isInformed());
		listener.resetInformed();
		property = new LTestProperty("key01", "value01");
		memory.memorize(property);

		Collection<LTestProperty> props = new ArrayList<LTestProperty>();
		props.add(property);
		memory.forgetAll(props);
		assertTrue(listener.isInformed());
		listener.resetInformed();
		memory.memorize(new LTestProperty("key01", "value01"));

		memory.clear();
		assertTrue(listener.isInformed());
		listener.resetInformed();
		memory.memorize(new LTestProperty("key01", "value01"));

	}
}
