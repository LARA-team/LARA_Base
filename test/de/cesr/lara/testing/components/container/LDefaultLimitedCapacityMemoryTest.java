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
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.container.LaraCapacityManageableContainer;
import de.cesr.lara.components.container.LaraContainer;
import de.cesr.lara.components.container.memory.LaraMemory;
import de.cesr.lara.components.container.memory.impl.LDefaultLimitedCapacityMemory;
import de.cesr.lara.components.eventbus.events.LModelStepEvent;
import de.cesr.lara.components.eventbus.impl.LEventbus;
import de.cesr.lara.components.util.impl.LCapacityManagers;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;
import de.cesr.lara.testing.components.container.LContainerTestUtils.LTestProperty;


/**
 * Tests the memory functionality with respect to the capacity management
 */
public class LDefaultLimitedCapacityMemoryTest {

	/**
	 * Logger
	 */
	static private Logger	logger	= Log4jLogger.getLogger(LDefaultLimitedCapacityMemoryTest.class);

	LaraMemory<LTestProperty>	memory;

	/**
	 * 
	 */
	@Before
	public void setUp() {
		memory = new LDefaultLimitedCapacityMemory<LTestProperty>(LCapacityManagers.<LTestProperty> makeFIFO(), 7);
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCapacity() {
		LContainerTestUtils.storeSomeEntries(memory, 6);

		assertTrue("memory was initialised with a capacity of 7", memory.getCapacity() == 7);
		assertTrue("6 properties were memorised", memory.getSize() == 6);

		LEventbus.getInstance().publish(new LModelStepEvent());

		memory.memorize(new LTestProperty("key07", "value07"));
		assertTrue("6 + 1 properties were memorised", memory.getSize() == 7);
		assertTrue("7 properties added at capacity of 7 means full", memory.isFull());

		memory.memorize(new LTestProperty("key08", "value08"));
		assertTrue(memory.getSize() == 7);
		assertFalse("The first property should have been removed now", memory.contains("key01"));

		logger.info(memory);

		LTestProperty propertyX = null;
		propertyX = memory.recall("key08");
		assertEquals(propertyX.getValue(), "value08");

		LContainerTestUtils.storeSomeEntries(memory, 6);
		assertTrue("After inserting several properties memory size should remain 7", memory.getSize() == 7);

		// change capacity to 0:
		LaraCapacityManageableContainer<LTestProperty> cmStorage = (LaraCapacityManageableContainer<LTestProperty>) memory;
		cmStorage.setCapacity(LaraContainer.UNLIMITED_CAPACITY);

		assertTrue(memory.getCapacity() == LaraContainer.UNLIMITED_CAPACITY);
		LContainerTestUtils.storeSomeEntries(memory, 100);
		assertTrue("7 + 100 (at unlimited capcaity) = 107", memory.getSize() == 107);

		logger.info(memory);

		// change capacity to 10:
		cmStorage.setCapacity(10);
		logger.info(memory);

		assertTrue("After setting capacity of memory of size 107 to 10 size should be 10", memory.getSize() == 10);
		assertTrue(memory.getCapacity() == 10);
		logger.info(memory);

		// change capacity to 0:
		cmStorage.setCapacity(0);
		assertTrue(memory.getSize() == 0);
		assertTrue(memory.getCapacity() == 0);
		logger.info(memory);

		// change capacity back to 0:
		cmStorage.setCapacity(LaraContainer.UNLIMITED_CAPACITY);
		LContainerTestUtils.storeSomeEntries(memory, 100);
		assertTrue(memory.getSize() == 100);
		logger.info(memory);
	}

	/**
	 * 
	 */
	@After
	public void tearDown() {
		memory = null;
	}
}
