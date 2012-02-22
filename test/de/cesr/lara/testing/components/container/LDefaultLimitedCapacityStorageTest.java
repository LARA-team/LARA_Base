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


import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.container.LaraCapacityManageableContainer;
import de.cesr.lara.components.container.LaraContainer;
import de.cesr.lara.components.container.storage.LaraStorage;
import de.cesr.lara.components.container.storage.impl.LDefaultLimitedCapacityStorage;
import de.cesr.lara.components.eventbus.events.LModelStepEvent;
import de.cesr.lara.components.eventbus.impl.LEventbus;
import de.cesr.lara.components.util.impl.LCapacityManagers;
import de.cesr.lara.testing.components.container.LContainerTestUtils.LTestProperty;


public class LDefaultLimitedCapacityStorageTest {

	LaraStorage<LTestProperty>	storage;

	@Before
	public void setUp() throws Exception {
		storage = new LDefaultLimitedCapacityStorage<LTestProperty>(LCapacityManagers.<LTestProperty> makeFIFO(), 7);
	}

	/**
	 * 
	 */
	@Test
	public void testCapacity() {
		LContainerTestUtils.storeSomeEntries(storage, 6);

		assertTrue(storage.getCapacity() == 7);
		assertTrue(storage.getSize() == 6);

		LEventbus.getInstance().publish(new LModelStepEvent());

		storage.store(new LTestProperty("key07", "value07"));
		assertTrue(storage.getSize() == 7);

		storage.store(new LTestProperty("key08", "value08"));
		assertTrue(storage.getSize() == 7);


		try {
			storage.fetch("key08");
		} catch (Exception e) {
			fail("This should not have raised an exception.");
		}

		LContainerTestUtils.storeSomeEntries(storage, 6);
		assertTrue(storage.getSize() == 7);

		@SuppressWarnings("unchecked")
		LaraCapacityManageableContainer<LTestProperty> cmStorage = (LaraCapacityManageableContainer<LTestProperty>) storage;
		cmStorage.setCapacity(LaraContainer.UNLIMITED_CAPACITY);

		LContainerTestUtils.storeSomeEntries(storage, 100);
		assertTrue(storage.getSize() == 107);
		assertTrue(storage.getCapacity() == LaraContainer.UNLIMITED_CAPACITY);

		cmStorage.setCapacity(10);

		assertTrue(storage.getSize() == 10);
		assertTrue(storage.getCapacity() == 10);

		cmStorage.setCapacity(0);
		assertTrue(storage.getSize() == 0);
		assertTrue(storage.getCapacity() == 0);

		cmStorage.setCapacity(LaraContainer.UNLIMITED_CAPACITY);
		LContainerTestUtils.storeSomeEntries(storage, 100);
		assertTrue(storage.getSize() == 100);

	}

	/**
	 * 
	 */
	@Test
	public void testNINO() {
		@SuppressWarnings("unchecked")
		LaraCapacityManageableContainer<LTestProperty> cmStorage = (LaraCapacityManageableContainer<LTestProperty>) storage;
		cmStorage.setCapacityManager(LCapacityManagers.<LTestProperty> makeNINO());
		cmStorage.setCapacity(2);

		LTestProperty property01 = new LTestProperty("key01", "value01");
		storage.store(property01);

		LEventbus.getInstance().publish(new LModelStepEvent());

		LTestProperty property02 = new LTestProperty("key02", "value02");
		storage.store(property02);

		LEventbus.getInstance().publish(new LModelStepEvent());

		LTestProperty property03 = new LTestProperty("key03", "value03");
		storage.store(property03);

		try {
			storage.fetch("key01");
		} catch (Exception e) {
			fail("This should not have raised an exception.");
		}

		try {
			storage.fetch("key02");
		} catch (Exception e) {
			fail("This should not have raised an exception.");
		}

		try {
			storage.fetch("key03");
			fail("This should have raised an exception.");
		} catch (Exception e) {

		}

	}

	/**
	 * 
	 */
	@Test
	public void testFIFO() {
		@SuppressWarnings("unchecked")
		LaraCapacityManageableContainer<LTestProperty> cmStorage = (LaraCapacityManageableContainer<LTestProperty>) storage;
		cmStorage.setCapacityManager(LCapacityManagers.<LTestProperty> makeFIFO());
		cmStorage.setCapacity(2);

		LTestProperty property01 = new LTestProperty("key01", "value01");
		storage.store(property01);

		LEventbus.getInstance().publish(new LModelStepEvent());

		LTestProperty property02 = new LTestProperty("key02", "value02");
		storage.store(property02);

		LEventbus.getInstance().publish(new LModelStepEvent());

		LTestProperty property03 = new LTestProperty("key03", "value03");
		storage.store(property03);

		try {
			storage.fetch("key02");
		} catch (Exception e) {
			fail("This should not have raised an exception.");
		}

		try {
			storage.fetch("key03");
		} catch (Exception e) {
			fail("This should not have raised an exception.");
		}

		try {
			storage.fetch("key01");
			fail("This should have raised an exception.");
		} catch (Exception e) {

		}

	}

	/**
	 * 
	 */
	@Test
	public void testFILO() {
		@SuppressWarnings("unchecked")
		LaraCapacityManageableContainer<LTestProperty> cmStorage = (LaraCapacityManageableContainer<LTestProperty>) storage;
		cmStorage.setCapacityManager(LCapacityManagers.<LTestProperty> makeFILO());
		cmStorage.setCapacity(2);

		LTestProperty property01 = new LTestProperty("key01", "value01");
		storage.store(property01);

		LEventbus.getInstance().publish(new LModelStepEvent());

		LTestProperty property02 = new LTestProperty("key02", "value02");
		storage.store(property02);

		LEventbus.getInstance().publish(new LModelStepEvent());

		LTestProperty property03 = new LTestProperty("key03", "value03");
		storage.store(property03);

		try {
			storage.fetch("key01");
		} catch (Exception e) {
			fail("This should not have raised an exception.");
		}

		try {
			storage.fetch("key03");
		} catch (Exception e) {
			fail("This should not have raised an exception.");
		}

		try {
			storage.fetch("key02");
			fail("This should have raised an exception.");
		} catch (Exception e) {

		}

	}

	@After
	public void tearDown() throws Exception {
		storage = null;
	}
}
