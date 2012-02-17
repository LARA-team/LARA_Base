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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.container.LaraContainer;
import de.cesr.lara.components.container.exceptions.LInvalidTimestampException;
import de.cesr.lara.components.container.exceptions.LRemoveException;
import de.cesr.lara.components.container.exceptions.LRetrieveException;
import de.cesr.lara.components.container.storage.LaraStorage;
import de.cesr.lara.components.container.storage.impl.LDefaultStorage;
import de.cesr.lara.testing.components.container.LContainerTestUtils.MyProperty;


/**
 * 
 * @author Sascha Holzhauer
 * @date 16.12.2009
 * 
 */
public class LDefaultStorageTest {

	LaraStorage<MyProperty>	storage;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		storage = new LDefaultStorage<MyProperty>();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		storage = null;
	}

	/**
	 * 
	 */
	@Test
	public void testClear() {
		storage.clear();
		assertTrue(storage.isEmpty());
	}

	/**
	 * test store
	 */
	@Test
	public void testStore() {
		assertTrue(storage.getCapacity() == LaraContainer.UNLIMITED_CAPACITY);
		assertFalse(storage.isFull());
		assertTrue(storage.getSize() == 0);
		assertTrue(storage.isEmpty());

		try {
			storage.store(new MyProperty("IAmInvalid", "", -1));
			fail("This should have raised an exception.");
		} catch (LInvalidTimestampException litse) {

		}
		assertTrue(storage.isEmpty());

		storeSixStandardEntries(1);
		// overwrite entries
		storeSixStandardEntries(1);

		assertFalse(storage.isEmpty());
		assertTrue(storage.getSize() == 6);

		// overwrite one entry with new value
		storage.store(new MyProperty("key01", "Haha", 1));
		assertTrue(storage.getSize() == 6);
		assertTrue(storage.fetch("key01", 1).value == "Haha");

		storeSixStandardEntries(2);

		assertFalse(storage.isFull()); // should always return false (at least for LDefaultStorage), so doesn't
										// really matter where it's called

		assertTrue(storage.getSize() == 12);
		MyProperty property1 = storage.fetch("key06", 1);
		MyProperty property2 = storage.fetch("key06", 2);
		assertNotSame(property1, property2);
		// assertFalse(property1.equals(property2)); // This actually a test for the property; see comment in
		// LaraProperty.equals (ME)

		MyProperty propertyX = storage.fetch("key06");
		assertSame(property2, propertyX);
	}

	/**
	 * 
	 */
	@Test
	public void testFetch() {
		storeSixStandardEntries(1);
		MyProperty property = storage.fetch("key02", 1);
		assertNotNull(property);
		assertTrue(property.getKey() == "key02");
		assertTrue(property.getValue() == "value02");
		assertTrue(property.getTimestamp() == 1);

		try {
			storage.fetch("IDontExist");
			fail("This should have raised an exception.");
		} catch (LRetrieveException lre) {

		}

		try {
			storage.fetch("key02", 2);
			fail("This should have raised an exception.");
		} catch (LRetrieveException lre) {

		}

		storage.store(new MyProperty("key05", "lastAdded", 5));
		assertEquals("The last added property of key key05 hast value lastAdded", "lastAdded", storage.fetch("key05")
				.getValue());
	}

	private void storeSixStandardEntries(int step) {
		storage.store(new MyProperty("key01", "value01", step));
		storage.store(new MyProperty("key02", "value02", step));
		storage.store(new MyProperty("key03", "value03", step));
		storage.store(new MyProperty("key04", "value04", step));
		storage.store(new MyProperty("key05", "value05", step));
		storage.store(new MyProperty("key06", "value06", step));
	}

	/**
	 * 
	 */
	@Test
	public void testRemove() {
		storeSixStandardEntries(1);

		storage.remove("key03", 1);
		assertTrue(storage.getSize() == 5);
		storage.removeAll("key04");
		assertTrue(storage.getSize() == 4);

		try {
			storage.remove("key01", 2);
			fail("This should have raised an exception.");
		} catch (LRemoveException lre) {

		}

		try {
			storage.removeAll("IDontExist");
			fail("This should have raised an exception.");
		} catch (LRemoveException lre) {

		}
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
		MyProperty property : storage) {
			size++;
		}
		assertTrue("iterator needs to iterate over 12 proeprties", size == 12);
	}
}