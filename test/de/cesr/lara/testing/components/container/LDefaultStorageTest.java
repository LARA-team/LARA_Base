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
import de.cesr.lara.components.eventbus.events.LModelStepEvent;
import de.cesr.lara.components.eventbus.impl.LEventbus;
import de.cesr.lara.testing.LTestUtils;
import de.cesr.lara.testing.components.container.LContainerTestUtils.LTestProperty;


/**
 * 
 * @author Sascha Holzhauer
 * @date 16.12.2009
 * 
 */
public class LDefaultStorageTest {

	LaraStorage<LTestProperty> storage;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		LTestUtils.initTestModel(LTestUtils.dConfig);
		LEventbus.getInstance().publish(new LModelStepEvent());
		storage = new LDefaultStorage<LTestProperty>();
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

	@Test
	public void testContains() {
		LContainerTestUtils.storeSixStandardEntries(storage);
		assertTrue(storage.contains("key01"));
		assertTrue(storage.contains("key02"));
		assertTrue(storage.contains("key03"));
		assertTrue(storage.contains("key04"));
		assertTrue(storage.contains("key05"));
		assertTrue(storage.contains("key06"));

		assertTrue(storage.contains("key01", 1));
		assertTrue(storage.contains("key02", 1));
		assertTrue(storage.contains("key03", 1));
		assertTrue(storage.contains("key04", 1));
		assertTrue(storage.contains("key05", 1));
		assertTrue(storage.contains("key06", 1));

		assertFalse(storage.contains("key01", 2));
		assertFalse(storage.contains("key02", 3));
		assertFalse(storage.contains("key03", 4));
		assertFalse(storage.contains("key04", 5));
		assertFalse(storage.contains("key05", 6));
		assertFalse(storage.contains("key06", 7));

		LEventbus.getInstance().publish(new LModelStepEvent());

		assertTrue(storage.contains("key01"));
		assertTrue(storage.contains("key02"));
		assertTrue(storage.contains("key03"));
		assertTrue(storage.contains("key04"));
		assertTrue(storage.contains("key05"));
		assertTrue(storage.contains("key06"));

		storage.removeAll("key01");
		assertFalse(storage.contains("key01"));
		assertFalse(storage.contains("key01", 1));

		assertTrue(storage.contains("key02"));
		assertTrue(storage.contains("key02", 1));

		assertTrue(storage.contains("key03"));
		assertTrue(storage.contains("key03", 1));

		assertTrue(storage.contains("key04"));
		assertTrue(storage.contains("key04", 1));

		assertTrue(storage.contains("key05"));
		assertTrue(storage.contains("key05", 1));

		assertTrue(storage.contains("key06"));
		assertTrue(storage.contains("key06", 1));

		storage.removeAll("key02");
		assertFalse(storage.contains("key02"));

		storage.removeAll("key03");
		assertFalse(storage.contains("key03"));

		storage.removeAll("key04");
		assertFalse(storage.contains("key04"));

		storage.removeAll("key05");
		assertFalse(storage.contains("key05"));

		storage.removeAll("key06");
		assertFalse(storage.contains("key06"));
	}

	/**
	 * 
	 */
	@Test
	public void testFetch() {
		LContainerTestUtils.storeSixStandardEntries(storage);
		LTestProperty property = storage.fetch("key02", 1);
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

		LEventbus.getInstance().publish(new LModelStepEvent());

		storage.store(new LTestProperty("key05", "lastAdded"));
		assertEquals("The last added property of key key05 hast value lastAdded", "lastAdded", storage.fetch("key05")
				.getValue());
	}

	/**
	 * 
	 */
	@Test
	public void testIterator() {
		LContainerTestUtils.storeSixStandardEntries(storage);

		LEventbus.getInstance().publish(new LModelStepEvent());

		LContainerTestUtils.storeSixStandardEntries(storage);

		int size = 0;
		for (@SuppressWarnings("unused")
		LTestProperty property : storage) {
			size++;
		}
		assertTrue("iterator needs to iterate over 12 proeprties", size == 12);
	}

	/**
	 * 
	 */
	@Test
	public void testRemove() {
		LContainerTestUtils.storeSixStandardEntries(storage);

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
	 * test store
	 */
	@Test
	public void testStore() {
		assertTrue(storage.getCapacity() == LaraContainer.UNLIMITED_CAPACITY);
		assertFalse(storage.isFull());
		assertTrue(storage.getSize() == 0);
		assertTrue(storage.isEmpty());

		LTestProperty invalid = new LTestProperty("IBecomeInvalid", "");

		assertTrue(storage.isEmpty());

		LContainerTestUtils.storeSixStandardEntries(storage);
		// overwrite entries
		LContainerTestUtils.storeSixStandardEntries(storage);

		assertFalse(storage.isEmpty());
		assertTrue(storage.getSize() == 6);

		// overwrite one entry with new value
		storage.store(new LTestProperty("key01", "Haha"));
		assertTrue(storage.getSize() == 6);
		assertTrue(storage.fetch("key01", 1).value == "Haha");

		LEventbus.getInstance().publish(new LModelStepEvent());

		try {
			storage.store(invalid);
			fail("This should have raised an exception.");
		} catch (LInvalidTimestampException litse) {
		}

		LContainerTestUtils.storeSixStandardEntries(storage);

		assertFalse(storage.isFull()); // should always return false (at least
										// for LDefaultStorage), so doesn't
										// really matter where it's called

		assertTrue(storage.getSize() == 12);
		LTestProperty property1 = storage.fetch("key06", 1);
		LTestProperty property2 = storage.fetch("key06", 2);
		assertNotSame(property1, property2);
		// assertFalse(property1.equals(property2)); // This actually a test for
		// the property; see comment in
		// LaraProperty.equals (ME)

		LTestProperty propertyX = storage.fetch("key06");
		assertSame(property2, propertyX);
	}
}