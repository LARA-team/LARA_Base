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
package de.cesr.lara.testing.util;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.LaraProperty;
import de.cesr.lara.components.container.LaraContainer;
import de.cesr.lara.components.container.memory.LaraMemory;
import de.cesr.lara.components.container.memory.LaraOverwriteMemory;
import de.cesr.lara.components.container.memory.impl.LDefaultLimitedCapacityOverwriteMemory;
import de.cesr.lara.components.util.impl.LCapacityManagers;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;

/**
 * @author Sascha Holzhauer
 * @date 19.05.2010
 */
public class DefaultLLimitedCapacityOverwriteMemoryTest {

	/**
	 * Logger
	 */
	static private Logger	logger	= Log4jLogger.getLogger(DefaultLLimitedCapacityOverwriteMemoryTest.class);

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

	/**
	 * 
	 */
	@Before
	public void setUp() {
		memory = new LDefaultLimitedCapacityOverwriteMemory<MyProperty>(LCapacityManagers.<MyProperty> makeFIFO(), 7);
	}

	/**
	 * 
	 */
	@Test
	public void testCapacity() {
		storeSomeEntries(6, 1);
		logger.info(memory);

		assertTrue("memory was initialised with a capacity of 7", memory.getCapacity() == 7);
		assertTrue("6 properties were memorised", memory.getSize() == 6);

		memory.memorize(new MyProperty("key07", "value07", 2));
		assertTrue("6 + 1 properties were memorised", memory.getSize() == 7);
		assertTrue("7 properties added at capacity of 7 means full", memory.isFull());

		memory.memorize(new MyProperty("key08", "value08", 2));
		assertTrue(memory.getSize() == 7);
		assertFalse("The first property should have been removed now", memory.contains("key01"));

		logger.info(memory);

		MyProperty propertyX = null;
		propertyX = memory.recall("key08");
		assertEquals(propertyX.getValue(), "value08");

		storeSomeEntries(6, 2);
		assertTrue("After inserting several properties memory size should remain 7", memory.getSize() == 7);

		// change capacity to 0:
		((LaraOverwriteMemory<MyProperty>) memory)
				.setCapacity(LaraContainer.UNLIMITED_CAPACITY);

		assertTrue(memory.getCapacity() == LaraContainer.UNLIMITED_CAPACITY);
		storeSomeEntries(100, 2);
		assertTrue("7 + 100 (at unlimited capcaity) = 107", memory.getSize() == 107);

		logger.info(memory);

		// change capacity to 10:
		((LaraOverwriteMemory<MyProperty>) memory).setCapacity(10);
		logger.info(memory);

		assertTrue("After setting capacity of memory of size 107 to 10 size should be 10", memory.getSize() == 10);
		assertTrue(memory.getCapacity() == 10);
		logger.info(memory);

		// change capacity to 0:
		((LaraOverwriteMemory<MyProperty>) memory).setCapacity(0);
		assertTrue(memory.getSize() == 0);
		assertTrue(memory.getCapacity() == 0);
		logger.info(memory);

		// change capacity back to 0:
		((LaraOverwriteMemory<MyProperty>) memory)
				.setCapacity(LaraContainer.UNLIMITED_CAPACITY);
		storeSomeEntries(100, 2);
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

	/**
	 * counter for labeling properties
	 */
	private static int	count	= 0;

	private void storeSomeEntries(int num, int step) {
		for (int i = 0; i < num; i++) {
			memory.memorize(new MyProperty("key" + count, "value" + count++, step));
		}
	}
}
