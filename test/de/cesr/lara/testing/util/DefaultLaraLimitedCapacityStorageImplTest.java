package de.cesr.lara.testing.util;


import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.LaraProperty;
import de.cesr.lara.components.container.LaraCapacityManageable;
import de.cesr.lara.components.container.LaraContainer;
import de.cesr.lara.components.container.storage.LaraStorage;
import de.cesr.lara.components.container.storage.impl.LDefaultLimitedCapacityStorage;
import de.cesr.lara.components.util.impl.LCapacityManagers;


public class DefaultLaraLimitedCapacityStorageImplTest {

	LaraStorage<MyProperty>	storage;

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

	@Before
	public void setUp() throws Exception {
		storage = new LDefaultLimitedCapacityStorage<MyProperty>(LCapacityManagers.<MyProperty> makeFIFO(), 7);
	}

	/**
	 * 
	 */
	@Test
	public void testCapacity() {
		storeSomeEntries(6, 1);

		assertTrue(storage.getCapacity() == 7);
		assertTrue(storage.getSize() == 6);

		storage.store(new MyProperty("key07", "value07", 2));
		assertTrue(storage.getSize() == 7);

		storage.store(new MyProperty("key08", "value08", 2));
		assertTrue(storage.getSize() == 7);

		MyProperty propertyX = null;
		try {
			propertyX = storage.fetch("key08");
		} catch (Exception e) {
			fail("This should not have raised an exception.");
		}

		storeSomeEntries(6, 2);
		assertTrue(storage.getSize() == 7);

		LaraCapacityManageable<MyProperty> cmStorage = (LaraCapacityManageable<MyProperty>) storage;
		cmStorage.setCapacity(LaraContainer.UNLIMITED_CAPACITY);

		storeSomeEntries(100, 2);
		assertTrue(storage.getSize() == 107);
		assertTrue(storage.getCapacity() == LaraContainer.UNLIMITED_CAPACITY);

		cmStorage.setCapacity(10);

		assertTrue(storage.getSize() == 10);
		assertTrue(storage.getCapacity() == 10);

		cmStorage.setCapacity(0);
		assertTrue(storage.getSize() == 0);
		assertTrue(storage.getCapacity() == 0);

		cmStorage.setCapacity(LaraContainer.UNLIMITED_CAPACITY);
		storeSomeEntries(100, 2);
		assertTrue(storage.getSize() == 100);

	}

	/**
	 * 
	 */
	@Test
	public void testNINO() {
		LaraCapacityManageable<MyProperty> cmStorage = (LaraCapacityManageable<MyProperty>) storage;
		cmStorage.setCapacityManager(LCapacityManagers.<MyProperty> makeNINO());
		cmStorage.setCapacity(2);

		MyProperty property01 = new MyProperty("key01", "value01", 0);
		MyProperty property02 = new MyProperty("key02", "value02", 1);
		MyProperty property03 = new MyProperty("key03", "value03", 2);

		storage.store(property01);
		storage.store(property02);
		storage.store(property03);

		MyProperty propertyX = null;
		try {
			propertyX = storage.fetch("key01");
		} catch (Exception e) {
			fail("This should not have raised an exception.");
		}

		try {
			propertyX = storage.fetch("key02");
		} catch (Exception e) {
			fail("This should not have raised an exception.");
		}

		try {
			propertyX = storage.fetch("key03");
			fail("This should have raised an exception.");
		} catch (Exception e) {

		}

	}

	/**
	 * 
	 */
	@Test
	public void testFIFO() {
		LaraCapacityManageable<MyProperty> cmStorage = (LaraCapacityManageable<MyProperty>) storage;
		cmStorage.setCapacityManager(LCapacityManagers.<MyProperty> makeFIFO());
		cmStorage.setCapacity(2);

		MyProperty property01 = new MyProperty("key01", "value01", 0);
		MyProperty property02 = new MyProperty("key02", "value02", 1);
		MyProperty property03 = new MyProperty("key03", "value03", 2);

		storage.store(property01);
		storage.store(property02);
		storage.store(property03);

		MyProperty propertyX = null;
		try {
			propertyX = storage.fetch("key02");
		} catch (Exception e) {
			fail("This should not have raised an exception.");
		}

		try {
			propertyX = storage.fetch("key03");
		} catch (Exception e) {
			fail("This should not have raised an exception.");
		}

		try {
			propertyX = storage.fetch("key01");
			fail("This should have raised an exception.");
		} catch (Exception e) {

		}

	}

	/**
	 * 
	 */
	@Test
	public void testFILO() {
		LaraCapacityManageable<MyProperty> cmStorage = (LaraCapacityManageable<MyProperty>) storage;
		cmStorage.setCapacityManager(LCapacityManagers.<MyProperty> makeFILO());
		cmStorage.setCapacity(2);

		MyProperty property01 = new MyProperty("key01", "value01", 0);
		MyProperty property02 = new MyProperty("key02", "value02", 1);
		MyProperty property03 = new MyProperty("key03", "value03", 2);

		storage.store(property01);
		storage.store(property02);
		storage.store(property03);

		MyProperty propertyX = null;
		try {
			propertyX = storage.fetch("key01");
		} catch (Exception e) {
			fail("This should not have raised an exception.");
		}

		try {
			propertyX = storage.fetch("key03");
		} catch (Exception e) {
			fail("This should not have raised an exception.");
		}

		try {
			propertyX = storage.fetch("key02");
			fail("This should have raised an exception.");
		} catch (Exception e) {

		}

	}

	@After
	public void tearDown() throws Exception {
		storage = null;
	}

	private static int	count	= 0;

	private void storeSomeEntries(int num, int step) {
		for (int i = 0; i < num; i++) {
			storage.store(new MyProperty("key" + count, "value" + count++, step));
		}
	}

}
